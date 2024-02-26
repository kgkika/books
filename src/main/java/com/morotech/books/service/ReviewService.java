package com.morotech.books.service;

import com.morotech.books.domain.Review;
import com.morotech.books.repository.ReviewRepository;
import com.morotech.books.request.ReviewPayload;
import com.morotech.books.result.AddReviewResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ResponseEntity<AddReviewResult> addReview(ReviewPayload reviewPayload) {
        Review review = new Review(reviewPayload.bookId(), reviewPayload.rating(), reviewPayload.review());
        try {
            reviewRepository.save(review);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ConstraintViolationException e) {
            Map<String, String> errorByProperty = e.getConstraintViolations().stream()
                    .collect(
                            Collectors.toMap(
                                    v -> v.getPropertyPath().toString(),
                                    ConstraintViolation::getMessage
                            ));
            return ResponseEntity.badRequest().body(new AddReviewResult(errorByProperty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AddReviewResult(new HashMap<>() {{
                put("description", e.getMessage());
            }}));
        }
    }
}
