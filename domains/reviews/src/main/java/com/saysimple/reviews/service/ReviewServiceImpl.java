package com.saysimple.reviews.service;

import com.saysimple.reviews.aop.ReviewErrorEnum;
import com.saysimple.reviews.entity.Review;
import com.saysimple.reviews.repository.ReviewRepository;
import com.saysimple.reviews.vo.ReviewRequest;
import com.saysimple.reviews.vo.ReviewRequestUpdate;
import com.saysimple.reviews.vo.ReviewResponse;
import lombok.extern.slf4j.Slf4j;
import org.saysimple.aop.exception.NotFoundException;
import org.saysimple.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ReviewResponse create(ReviewRequest review) {
        Review reviewEntity = ModelUtils.strictMap(review, Review.class);

        reviewEntity.setReviewId(UUID.randomUUID().toString());
        reviewRepository.save(reviewEntity);

        return ModelUtils.strictMap(reviewEntity, ReviewResponse.class);
    }

    @Override
    public List<ReviewResponse> list() {
        List<Review> reviewEntities = (List<Review>) reviewRepository.findAll();

        return reviewEntities.stream()
                .map(entity -> ModelUtils.map(entity, ReviewResponse.class))
                .toList();
    }


    @Override
    public ReviewResponse get(String reviewId) {
        Review review = reviewRepository.findByReviewId(reviewId).orElseThrow(() ->
                new NotFoundException(ReviewErrorEnum.REVIEW_NOT_FOUND.getMsg()));

        return ModelUtils.map(review, ReviewResponse.class);
    }

    @Override
    public ReviewResponse update(ReviewRequestUpdate review) {
        Review reviewEntity = reviewRepository.findByReviewId(review.getProductId()).orElseThrow(() ->
                new NotFoundException(ReviewErrorEnum.REVIEW_NOT_FOUND.getMsg()));

        reviewEntity.setProductId(review.getProductId());
        reviewEntity.setCategoryId(review.getCategoryId());
        reviewEntity.setUserId(review.getUserId());

        reviewEntity.setTitle(review.getTitle());
        reviewEntity.setContent(review.getContent());

        reviewRepository.save(reviewEntity);

        return ModelUtils.map(reviewEntity, ReviewResponse.class);
    }

    @Override
    public void delete(String reviewId) {
        Review review = reviewRepository.findByReviewId(reviewId).orElseThrow(() ->
                new NotFoundException(ReviewErrorEnum.REVIEW_NOT_FOUND.getMsg()));

        reviewRepository.delete(review);
    }
}
