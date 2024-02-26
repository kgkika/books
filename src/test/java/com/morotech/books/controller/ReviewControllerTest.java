package com.morotech.books.controller;

import com.morotech.books.request.ReviewPayload;
import com.morotech.books.service.ReviewService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @InjectMocks
    private ReviewController controller;

    @Mock
    private ReviewService service;

    @Test
    public void testAddReviewSuccess() {
        ReviewPayload payload = new ReviewPayload(1, 5, "Lorem Ipsum");
        when(service.addReview(payload)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
        assertEquals(controller.reviewBook(payload).getStatusCode().value(), 201);
    }

    @Test
    public void testAddReviewFails() {
        when(service.addReview(any())).thenReturn(ResponseEntity.badRequest().build());
        assertEquals(controller.reviewBook(any()).getStatusCode().value(), 400);
    }
}
