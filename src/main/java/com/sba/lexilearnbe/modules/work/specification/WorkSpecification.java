package com.sba.lexilearnbe.modules.work.specification;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import com.sba.lexilearnbe.modules.work.entity.Work;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WorkSpecification {

    public static Specification<Work> filterWorks(String genre, String period, String tag, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isTrue(root.get("isPublished")));

            // Filter Thể loại
            if (StringUtils.hasText(genre)) {
                predicates.add(criteriaBuilder.equal(root.get("genre"), genre.trim()));
            }

            // Filter Thời kỳ
            if (StringUtils.hasText(period)) {
                predicates.add(criteriaBuilder.equal(root.get("period"), period.trim()));
            }

            // Filter Thẻ (JOIN bảng Tag)
            if (StringUtils.hasText(tag)) {
                Join<Work, Tag> tagJoin = root.join("tags", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(tagJoin.get("slug"), tag.trim()));
            }

            // Ô tìm kiếm (Tìm theo Tên tác phẩm hoặc Tên tác giả)
            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";

                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);

                Join<Work, Author> authorJoin = root.join("author", JoinType.LEFT);
                Predicate authorMatch = criteriaBuilder.like(criteriaBuilder.lower(authorJoin.get("name")), searchPattern);

                predicates.add(criteriaBuilder.or(titleMatch, authorMatch));
            }

            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}