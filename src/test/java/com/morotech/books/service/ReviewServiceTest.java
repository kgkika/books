package com.morotech.books.service;

import com.morotech.books.domain.Review;
import com.morotech.books.repository.ReviewRepository;
import com.morotech.books.request.ReviewPayload;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService service;

    @Mock
    private ReviewRepository repository;

    @Mock
    private ReviewPayload payload;

    @Test
    public void testAddReviewSuccess() {
        when(repository.save(any(Review.class))).thenReturn(new Review(1, 4, "Lorem Ipsum"));
        assertEquals(service.addReview(payload).getStatusCode().value(), 201);
    }

    @Test
    public void testAddReviewFailsConstraintViolation() {
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        ConstraintViolation<?> cv = ConstraintViolationImpl.forBeanValidation("",
                null, null, "must be less than or equal to 5",
                null, null, null, null, PathImpl.createPathFromString("rating"), null, null);
        constraintViolations.add(cv);
        when(repository.save(any(Review.class))).thenThrow(new ConstraintViolationException("failed", constraintViolations));
        assertEquals(service.addReview(payload).getStatusCode().value(), 400);
        assertNotNull(service.addReview(payload).getBody());
        assertNotNull(Objects.requireNonNull(service.addReview(payload).getBody()).errors().get("rating"));
    }

    @Test
    public void testAddReviewFailsGenericException() {
        when(repository.save(any(Review.class))).thenThrow(new RuntimeException("failed"));
        assertEquals(service.addReview(payload).getStatusCode().value(), 400);
        assertNotNull(service.addReview(payload).getBody());
        assertNotNull(Objects.requireNonNull(service.addReview(payload).getBody()).errors().get("description"));
    }

}
