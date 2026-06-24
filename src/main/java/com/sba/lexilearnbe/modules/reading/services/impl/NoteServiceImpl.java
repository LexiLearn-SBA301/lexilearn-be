package com.sba.lexilearnbe.modules.reading.services.impl;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.reading.dto.request.CreateNoteRequest;
import com.sba.lexilearnbe.modules.reading.dto.response.NoteResponse;
import com.sba.lexilearnbe.modules.reading.entity.Note;
import com.sba.lexilearnbe.modules.reading.mapper.NoteMapper;
import com.sba.lexilearnbe.modules.reading.repository.NoteRepository;
import com.sba.lexilearnbe.modules.reading.services.NoteService;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkSection;
import com.sba.lexilearnbe.modules.workdetail.repository.WorkSectionRepository;
import com.sba.lexilearnbe.modules.workdetail.util.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final AccountRepository accountRepository;
    private final WorkSectionRepository workSectionRepository;
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponse> getNotes(UUID accountId, UUID sectionId) {
        requireAccount(accountId);
        requireReadableSection(sectionId);

        return noteRepository.findByAccountIdAndSectionId(accountId, sectionId)
                .stream()
                .map(noteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NoteResponse createNote(UUID accountId, UUID sectionId, CreateNoteRequest request) {
        Account account = requireAccount(accountId);
        WorkSection section = requireReadableSection(sectionId);
        validateOffsetsAndHighlightedText(section, request);

        Note note = Note.builder()
                .account(account)
                .section(section)
                .startOffset(request.startOffset())
                .endOffset(request.endOffset())
                .highlightedText(request.highlightedText())
                .userNote(request.userNote())
                .color(request.color())
                .build();

        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Override
    @Transactional
    public void deleteNote(UUID accountId, UUID noteId) {
        requireAccount(accountId);

        Note note = noteRepository.findByAccountIdAndId(accountId, noteId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOTE_NOT_FOUND));

        noteRepository.delete(note);
    }

    private Account requireAccount(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId không được để trống");

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private WorkSection requireReadableSection(UUID sectionId) {
        Objects.requireNonNull(sectionId, "sectionId không được để trống");

        WorkSection section = workSectionRepository.findByIdWithWork(sectionId)
                .orElseThrow(() -> new ApiException(ErrorCode.SECTION_NOT_FOUND));
        WorkReadAccessValidator.validate(section.getWork());
        return section;
    }

    private void validateOffsetsAndHighlightedText(WorkSection section, CreateNoteRequest request) {
        int startOffset = request.startOffset();
        int endOffset = request.endOffset();
        String content = section.getContent();

        if (endOffset <= startOffset) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Vị trí kết thúc highlight phải lớn hơn vị trí bắt đầu");
        }
        if (endOffset > content.length()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Vùng highlight vượt quá độ dài phần văn bản");
        }

        String selectedText = content.substring(startOffset, endOffset);
        if (!selectedText.equals(request.highlightedText())) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Nội dung highlight không khớp với vị trí đã chọn");
        }
    }
}
