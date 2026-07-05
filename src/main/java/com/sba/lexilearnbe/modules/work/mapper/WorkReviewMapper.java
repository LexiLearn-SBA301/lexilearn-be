package com.sba.lexilearnbe.modules.work.mapper;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AdminWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.MyWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.PublicWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.ReviewRevisionResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkReview;
import com.sba.lexilearnbe.modules.work.entity.WorkReviewRevision;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WorkReviewMapper {

    @Mapping(target = "work", source = "work")
    @Mapping(target = "account", source = "account")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WorkReview toEntity(Work work, Account account);

    @Mapping(target = "review", ignore = true)
    @Mapping(target = "versionNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "title", source = "title", qualifiedByName = "trimToNull")
    @Mapping(target = "content", source = "content", qualifiedByName = "trim")
    WorkReviewRevision toRevisionEntity(CreateWorkReviewRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "review", ignore = true)
    @Mapping(target = "versionNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    WorkReviewRevision toPendingRevision(WorkReviewRevision source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", source = "title", qualifiedByName = "trimToNull")
    @Mapping(target = "content", source = "content", qualifiedByName = "trim")
    void updateEntityFromRequest(
            UpdateWorkReviewRequest request,
            @MappingTarget WorkReviewRevision revision
    );

    @Mapping(target = "reviewedById", source = "reviewedBy.id")
    @Mapping(target = "reviewedByName", source = "reviewedBy.fullName")
    ReviewRevisionResponse toRevisionResponse(WorkReviewRevision revision);

    @Mapping(target = "reviewId", source = "review.id")
    @Mapping(target = "workId", source = "review.work.id")
    @Mapping(target = "reviewerId", source = "review.account.id")
    @Mapping(target = "reviewerName", source = "review.account.fullName")
    @Mapping(target = "approvedAt", source = "reviewedAt")
    PublicWorkReviewResponse toPublicResponse(WorkReviewRevision revision);

    @Mapping(target = "reviewId", source = "review.id")
    @Mapping(target = "workId", source = "review.work.id")
    @Mapping(target = "workTitle", source = "review.work.title")
    @Mapping(target = "workSlug", source = "review.work.slug")
    @Mapping(target = "approvedRevision", source = "approvedRevision")
    @Mapping(target = "pendingRevision", source = "pendingRevision")
    @Mapping(target = "latestRejectedRevision", source = "latestRejectedRevision")
    @Mapping(target = "createdAt", source = "review.createdAt")
    @Mapping(target = "updatedAt", source = "review.updatedAt")
    MyWorkReviewResponse toMyResponse(
            WorkReview review,
            ReviewRevisionResponse approvedRevision,
            ReviewRevisionResponse pendingRevision,
            ReviewRevisionResponse latestRejectedRevision
    );

    @Mapping(target = "reviewId", source = "revision.review.id")
    @Mapping(target = "workId", source = "revision.review.work.id")
    @Mapping(target = "workTitle", source = "revision.review.work.title")
    @Mapping(target = "workSlug", source = "revision.review.work.slug")
    @Mapping(target = "reviewerId", source = "revision.review.account.id")
    @Mapping(target = "reviewerName", source = "revision.review.account.fullName")
    @Mapping(target = "reviewerEmail", source = "revision.review.account.email")
    @Mapping(target = "revision", source = "revision")
    AdminWorkReviewResponse toAdminResponse(
            WorkReviewRevision revision,
            ReviewRevisionResponse approvedRevision
    );

    @Named("trim")
    default String trim(String value) {
        return value == null ? null : value.trim();
    }

    @Named("trimToNull")
    default String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
