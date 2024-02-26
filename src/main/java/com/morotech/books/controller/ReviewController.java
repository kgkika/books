package com.morotech.books.controller;

import com.morotech.books.request.ReviewPayload;
import com.morotech.books.result.AddReviewResult;
import com.morotech.books.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/addReview")
    public ResponseEntity<AddReviewResult> reviewBook(@Valid @RequestBody ReviewPayload reviewPayload) {
        return reviewService.addReview(reviewPayload);
    }
}
