package com.sba.lexilearnbe.modules.reading.services;

import com.sba.lexilearnbe.modules.reading.dto.request.CreateNoteRequest;
import com.sba.lexilearnbe.modules.reading.dto.response.NoteResponse;

import java.util.List;
import java.util.UUID;

public interface NoteService {

    List<NoteResponse> getNotes(UUID accountId, UUID sectionId);

    NoteResponse createNote(UUID accountId, UUID sectionId, CreateNoteRequest request);

    void deleteNote(UUID accountId, UUID noteId);
}
