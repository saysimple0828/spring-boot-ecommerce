package com.saysimple.reviews.controller;

import com.saysimple.reviews.service.ReviewService;
import com.saysimple.reviews.vo.ReviewRequest;
import com.saysimple.reviews.vo.ReviewRequestUpdate;
import com.saysimple.reviews.vo.ReviewResponse;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final Environment env;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(Environment env, ReviewService reviewService) {
        this.env = env;
        this.reviewService = reviewService;
    }

    @GetMapping("/health-check")
    @Timed(value = "reviews.status", longTask = true)
    public String status() {
        return String.format("It's Working in Review Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", gateway ip(env)=" + env.getProperty("gateway.ip")
                + ", token expiration time=" + env.getProperty("token.expiration_time"));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@RequestBody ReviewRequest product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(product));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> list() {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.list());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ReviewResponse> get(@PathVariable("productId") String productId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.get(productId));
    }

    @PutMapping
    public ResponseEntity<ReviewResponse> update(@RequestBody ReviewRequestUpdate product) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.update(product));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> delete(@PathVariable("productId") String productId) {
        reviewService.delete(productId);

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
