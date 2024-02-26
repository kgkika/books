package com.morotech.books.controller;

import com.morotech.books.model.Book;
import com.morotech.books.payload.ReviewRequestPayload;
import com.morotech.books.payload.AddReviewResultPayload;
import com.morotech.books.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    @Autowired
    public ReviewController(ReviewService service) {
        this.service = service;
    }

    /**
     * @param reviewPayload The {@link ReviewRequestPayload} payload must contain the bookId, rating and review
     * @return {@link ResponseEntity}<{@link AddReviewResultPayload}> 201 CREATED when saved successfully
     */
    @PostMapping("/addReview")
    public ResponseEntity<AddReviewResultPayload> reviewBook(@Valid @RequestBody ReviewRequestPayload reviewPayload) {
        return service.addReview(reviewPayload);
    }
}
