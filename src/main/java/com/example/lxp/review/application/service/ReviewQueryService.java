package com.example.lxp.review.application.service;

import com.example.lxp.review.adapter.in.web.dto.ReviewItemResponse;
import com.example.lxp.review.adapter.in.web.dto.ReviewListResponse;
import com.example.lxp.review.application.port.in.ReviewQueryUseCase;
import com.example.lxp.review.application.port.out.ReviewPersistencePort;
import com.example.lxp.review.application.port.out.ReviewUserClientPort;
import com.example.lxp.review.domain.model.Review;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReviewQueryService implements ReviewQueryUseCase {

    private static final Sort DEFAULT_SORT = Sort.by("updatedAt").descending()
            .and(Sort.by("id").descending());
    private static final String UNKNOWN_NICKNAME = "알 수 없음";

    private final ReviewPersistencePort reviewPersistencePort;
    private final ReviewUserClientPort userClientPort;

    public ReviewQueryService(
            ReviewPersistencePort reviewPersistencePort,
            ReviewUserClientPort userClientPort
    ) {
        this.reviewPersistencePort = reviewPersistencePort;
        this.userClientPort = userClientPort;
    }

    @Override
    public ReviewListResponse getReviewsByCourseId(Long courseId) {
        List<Review> reviews = reviewPersistencePort.findAllByCourseId(courseId, DEFAULT_SORT);

        if (reviews.isEmpty()) {
            return ReviewListResponse.of(Collections.emptyList());
        }

        Set<Long> authorIds = reviews.stream()
                .map(Review::getAuthorId)
                .collect(Collectors.toSet());

        Map<Long, String> nicknameMap = userClientPort.getNicknames(authorIds);

        List<ReviewItemResponse> reviewItems = reviews.stream()
                .map(review -> ReviewItemResponse.from(
                        review,
                        nicknameMap.getOrDefault(review.getAuthorId(), UNKNOWN_NICKNAME)
                ))
                .toList();

        return ReviewListResponse.of(reviewItems);
    }
}
