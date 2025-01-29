package com.saysimple.reviews.repository;

import com.saysimple.reviews.entity.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    Optional<Review> findByReviewId(String reviewId);
}
