package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.ai.AuthorSyncPayload;
import com.sba.lexilearnbe.modules.work.dto.ai.AuthorSyncAuthorData;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.repository.AuthorRepository;
import com.sba.lexilearnbe.modules.work.services.AuthorAiSyncSnapshotService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorAiSyncSnapshotServiceImpl implements AuthorAiSyncSnapshotService {

    private static final String SCHEMA_VERSION = "literature_author_snapshot.v1";

    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthorSyncPayload buildSnapshot(UUID authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));

        return new AuthorSyncPayload(
                SCHEMA_VERSION,
                OffsetDateTime.now(ZoneOffset.UTC),
                toAuthorData(author)
        );
    }

    private AuthorSyncAuthorData toAuthorData(Author author) {
        return new AuthorSyncAuthorData(
                author.getId(),
                author.getName(),
                author.getPenName(),
                author.getSlug(),
                author.getBirthYear(),
                author.getDeathYear(),
                author.getPeriod(),
                author.getBio(),
                author.getPortraitUrl()
        );
    }
}
