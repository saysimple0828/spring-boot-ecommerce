package com.saysimple.reviews.service;

import com.saysimple.reviews.vo.ReviewRequest;
import com.saysimple.reviews.vo.ReviewRequestUpdate;
import com.saysimple.reviews.vo.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(ReviewRequest product);

    List<ReviewResponse> list();

    ReviewResponse get(String productId);

    ReviewResponse update(ReviewRequestUpdate product);

    void delete(String productId);
}
