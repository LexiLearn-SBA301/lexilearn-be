package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncPayload;
import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncAuthorData;
import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncCommentaryData;
import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncSectionData;
import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncTagData;
import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncWorkData;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import com.sba.lexilearnbe.modules.work.entity.WorkSection;
import com.sba.lexilearnbe.modules.work.repository.WorkCommentaryRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkSectionRepository;
import com.sba.lexilearnbe.modules.work.services.WorkAiSyncSnapshotService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkAiSyncSnapshotServiceImpl implements WorkAiSyncSnapshotService {

    private static final String SCHEMA_VERSION = "literature_work_snapshot.v1";

    private final WorkRepository workRepository;
    private final WorkSectionRepository workSectionRepository;
    private final WorkCommentaryRepository workCommentaryRepository;

    @Override
    @Transactional(readOnly = true)
    public WorkSyncPayload buildSnapshot(UUID workId) {
        Work work = workRepository.findByIdWithAuthorAndTags(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
        Author author = work.getAuthor();
        List<WorkSection> sections = workSectionRepository.findAllByWork_IdOrderByNumberAsc(workId);
        List<WorkCommentary> commentaries = workCommentaryRepository.findAllByWork_IdOrderByDisplayOrderAsc(workId);

        return new WorkSyncPayload(
                SCHEMA_VERSION,
                OffsetDateTime.now(ZoneOffset.UTC),
                toWorkData(work),
                toAuthorData(author),
                sections.stream().map(this::toSectionData).toList(),
                commentaries.stream().map(this::toCommentaryData).toList(),
                work.getTags().stream()
                        .sorted(Comparator.comparing(Tag::getSlug))
                        .map(this::toTagData)
                        .toList()
        );
    }

    private WorkSyncWorkData toWorkData(Work work) {
        return new WorkSyncWorkData(
                work.getId(),
                work.getTitle(),
                work.getSlug(),
                work.getOriginalTitle(),
                work.getGenre(),
                work.getSubGenre(),
                work.getPeriod(),
                work.getGrade(),
                work.getSemester(),
                work.getPublishYear(),
                work.getSummary(),
                work.getCoverUrl(),
                work.getIsPublished(),
                work.getHistoricalContext(),
                work.getRealisticValue(),
                work.getHumanisticValue(),
                work.getArtisticValue(),
                work.getFamousQuote(),
                work.getQuoteAttribution()
        );
    }

    private WorkSyncAuthorData toAuthorData(Author author) {
        return new WorkSyncAuthorData(
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

    private WorkSyncSectionData toSectionData(WorkSection section) {
        return new WorkSyncSectionData(
                section.getId(),
                section.getNumber(),
                section.getTitle(),
                section.getContent(),
                section.getContentType().name(),
                section.getWordCount()
        );
    }

    private WorkSyncCommentaryData toCommentaryData(WorkCommentary commentary) {
        return new WorkSyncCommentaryData(
                commentary.getId(),
                commentary.getTitle(),
                commentary.getContent(),
                commentary.getCommentatorName(),
                commentary.getCommentatorType().name(),
                commentary.getSourceTitle(),
                commentary.getSourceUrl(),
                commentary.getPublishedYear(),
                commentary.getDisplayOrder(),
                commentary.getIsFeatured(),
                commentary.getIsPublished()
        );
    }

    private WorkSyncTagData toTagData(Tag tag) {
        return new WorkSyncTagData(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                tag.getDescription()
        );
    }
}
