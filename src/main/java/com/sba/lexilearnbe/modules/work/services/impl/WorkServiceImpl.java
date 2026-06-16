package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.WorkRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.mapper.WorkMapper;
import com.sba.lexilearnbe.modules.work.repository.AuthorRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.WorkService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final AuthorRepository authorRepository;
    private final WorkRepository workRepository;
    private final WorkMapper workMapper;

    @Override
    public Page<WorkSummaryResponse> getWorksByFilter(String genre, String period, String searchKeyword, Pageable pageable) {
        Page<Work> worksPage = workRepository.findWorksWithFilter(genre, period, searchKeyword, pageable);
        if (worksPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Work> worksWithTags = workRepository.fetchTagsForWorks(worksPage.getContent());
        Map<UUID, Set<Tag>> tagsMap = worksWithTags.stream()
                .collect(Collectors.toMap(Work::getId, Work::getTags));

        return worksPage.map(work ->
                workMapper.toSummaryResponse(work, tagsMap.getOrDefault(work.getId(), Collections.emptySet()))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public WorkDetailResponse getWorkDetail(String slug) {
        return workMapper.toDetailResponse(
                workRepository.findBySlug(slug)
                        .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND))
        );
    }
    @Override
    @Transactional
    public WorkDetailResponse createWork(WorkRequest request) {
        // 1. Validate xem tác giả truyền lên có thực sự tồn tại không
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));

        // 2. Sinh slug THUẦN TÚY từ tiêu đề (Không cộng đuôi thời gian nữa)
        String slug = generateSlug(request.getTitle());

        // 3. Bây giờ hàm này mới phát huy tác dụng nè!
        if (workRepository.existsBySlug(slug)) {
            throw new ApiException(ErrorCode.WORK_ALREADY_EXISTS);
        }

        Work work = workMapper.toEntity(request);
        work.setAuthor(author);
        work.setSlug(slug); // Gán cái slug chuẩn vào

        Work savedWork = workRepository.save(work);
        return workMapper.toDetailResponse(savedWork);
    }

    @Override
    @Transactional
    public WorkDetailResponse updateWork(UUID id, WorkRequest request) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));

        workMapper.updateEntityFromRequest(request, work);
        work.setAuthor(author);

        Work updatedWork = workRepository.save(work);
        return workMapper.toDetailResponse(updatedWork);
    }

    @Override
    @Transactional
    public void deleteWork(UUID id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));

        // Cẩn thận: Nếu tác phẩm này đã bị Thành viên C tạo "work_sections" (nội dung đọc)
        // hoặc bị Thành viên D "bookmark", DB sẽ chặn xóa. Nhưng hiện tại họ chưa làm nên bác xóa vật lý thoải mái.
        workRepository.delete(work);
    }

    // Hàm sinh slug giống bên Author
    private String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) return "";
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        return java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized).replaceAll("")
                .replaceAll("Đ", "D").replaceAll("đ", "d")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}