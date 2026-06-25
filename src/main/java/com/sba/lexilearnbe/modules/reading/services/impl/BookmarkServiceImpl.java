package com.sba.lexilearnbe.modules.reading.services.impl;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.reading.dto.request.UpsertBookmarkRequest;
import com.sba.lexilearnbe.modules.reading.dto.response.BookmarkResponse;
import com.sba.lexilearnbe.modules.reading.entity.Bookmark;
import com.sba.lexilearnbe.modules.reading.mapper.BookmarkMapper;
import com.sba.lexilearnbe.modules.reading.repository.BookmarkRepository;
import com.sba.lexilearnbe.modules.reading.services.BookmarkService;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkSection;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkSectionRepository;
import com.sba.lexilearnbe.modules.work.utils.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private static final BigDecimal COMPLETED_PROGRESS = new BigDecimal("100.00");

    private final AccountRepository accountRepository;
    private final WorkRepository workRepository;
    private final WorkSectionRepository workSectionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getBookmarks(UUID accountId) {
        requireAccount(accountId);

        return bookmarkRepository.findReadableBookmarksByAccountId(accountId)
                .stream()
                .map(bookmarkMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BookmarkResponse upsertBookmark(UUID accountId, UUID workId, UpsertBookmarkRequest request) {
        Account account = requireAccount(accountId);
        Work work = requireReadableWork(workId);
        WorkSection currentSection = resolveCurrentSection(workId, request.currentSectionId());

        validatePosition(currentSection, request.position());

        Bookmark bookmark = bookmarkRepository.findByAccountIdAndWorkId(accountId, workId)
                .orElseGet(() -> Bookmark.builder()
                        .account(account)
                        .work(work)
                        .build());

        bookmark.setCurrentSection(currentSection);
        bookmark.setPosition(request.position());
        applyProgress(bookmark, request);

        return bookmarkMapper.toResponse(bookmarkRepository.save(bookmark));
    }

    @Override
    @Transactional
    public void deleteBookmark(UUID accountId, UUID workId) {
        requireAccount(accountId);

        Bookmark bookmark = bookmarkRepository.findByAccountIdAndWorkId(accountId, workId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }

    private Account requireAccount(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId không được để trống");

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private Work requireReadableWork(UUID workId) {
        Objects.requireNonNull(workId, "workId không được để trống");

        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
        WorkReadAccessValidator.validate(work);
        return work;
    }

    private WorkSection resolveCurrentSection(UUID workId, UUID sectionId) {
        if (sectionId == null) {
            return null;
        }

        WorkSection section = workSectionRepository.findByIdWithWork(sectionId)
                .orElseThrow(() -> new ApiException(ErrorCode.SECTION_NOT_FOUND));

        if (!section.getWork().getId().equals(workId)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Phần văn bản không thuộc tác phẩm này");
        }
        WorkReadAccessValidator.validate(section.getWork());

        return section;
    }

    private void validatePosition(WorkSection section, Integer position) {
        if (section == null) {
            return;
        }
        if (position > section.getContent().length()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Vị trí đọc vượt quá độ dài phần văn bản");
        }
    }

    private void applyProgress(Bookmark bookmark, UpsertBookmarkRequest request) {
        if (Boolean.TRUE.equals(request.isCompleted())) {
            bookmark.setProgressPercent(COMPLETED_PROGRESS);
            bookmark.setCompleted(true);
            bookmark.setCompletedAt(LocalDateTime.now());
            return;
        }

        bookmark.setProgressPercent(request.progressPercent());
        bookmark.setCompleted(false);
        bookmark.setCompletedAt(null);
    }
}
