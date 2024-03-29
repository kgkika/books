package com.morotech.books.service;

import com.morotech.books.domain.Review;
import com.morotech.books.model.Book;
import com.morotech.books.payload.BookResponsePayload;
import com.morotech.books.payload.MonthlyAvgRatingResponse;
import com.morotech.books.repository.ReviewRepository;
import com.morotech.books.utils.ConverterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.groupingBy;

@Service
public class BooksService {

    private static final String HOST = "https://gutendex.com";
    private static final String ENDPOINT = "/books";
    private static final String SEARCH_PARAM = "?search=";
    private static final String IDS_PARAM = "?ids=";

    private static final Logger LOGGER = LogManager.getLogger(BooksService.class);

    private final RestTemplate restTemplate;

    private final ConverterUtils converterUtils;

    private final ReviewRepository reviewRepository;

    @Autowired
    public BooksService(RestTemplate restTemplate, ConverterUtils converterUtils, ReviewRepository reviewRepository) {
        this.restTemplate = restTemplate;
        this.converterUtils = converterUtils;
        this.reviewRepository = reviewRepository;
    }

    @Cacheable(value = "booksTerm")
    public List<Book> getBooksByTerm(String term) {
        try {
            ResponseEntity<LinkedHashMap<String, Object>> responseEntity = restTemplate.exchange(
                    getSearchUrl().concat(term),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            if (responseEntity.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                LinkedHashMap<String, Object> responseBodyMap = responseEntity.getBody();
                assert responseBodyMap != null;
                return converterUtils.convertToList(responseBodyMap);
            }
        } catch (Exception e) {
            LOGGER.error("Error during querying books by the term {}. Error: {}", term, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Page<Book> getBooksByTermPaged(String term) {
        List<Book> books = getBooksByTerm(term);
        return new PageImpl<>(books, PageRequest.of(0, 3), books.size());
    }

    @Cacheable(value = "bookDetails")
    public ResponseEntity<BookResponsePayload> getBookDetails(String bookId) {
        try {
            ResponseEntity<LinkedHashMap<String, Object>> responseEntity = restTemplate.exchange(
                    getBooksByIdsUrl().concat(bookId),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            if (responseEntity.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                LinkedHashMap<String, Object> responseBodyMap = responseEntity.getBody();
                assert responseBodyMap != null;

                Book book = converterUtils.convertToBook(responseBodyMap);
                List<Review> reviews = reviewRepository.findAllByBookId(Integer.valueOf(bookId));

                if (reviews != null && book != null) {
                    return ResponseEntity.ok(
                            new BookResponsePayload(
                                    book,
                                    calculateListAverage(reviews),
                                    reviews.stream().map(Review::getReview).collect(Collectors.toList()))
                    );
                }
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            LOGGER.error("Error during getting book details for book with id {}. Error: {}", bookId, e.getMessage());
            return ResponseEntity.badRequest().body(new BookResponsePayload(new Error(e.getMessage())));
        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<MonthlyAvgRatingResponse>> getMonthlyAverageRating(String bookId) {
        try {
            List<Review> reviews = reviewRepository.findAllByBookId(Integer.valueOf(bookId));
            Map<Integer, List<Review>> reviewsPerYear = reviews.stream().collect(
                    groupingBy(s -> s.getCreated().getYear())
            );
            List<MonthlyAvgRatingResponse> averagePerMonth = reviewsPerYear.entrySet().stream()
                    .map(e -> {
                        Integer year = e.getKey();
                        List<Review> reviewsPerYearList = e.getValue();
                        Map<Month, Double> avgMap = reviewsPerYearList.stream()
                                .collect(groupingBy(s -> s.getCreated().getMonth(), averagingInt(Review::getRating)));
                        Map<Integer, Map<Month, Double>> reviewsToReturn = new HashMap<>();
                        reviewsToReturn.put(year, avgMap);
                        return new MonthlyAvgRatingResponse(reviewsToReturn);
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(averagePerMonth);
        } catch (Exception e) {
            LOGGER.error("Error during getting monthly average rating for book with id {}. Error: {}", bookId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    private String getSearchUrl() {
        return HOST + ENDPOINT + SEARCH_PARAM;
    }

    private String getBooksByIdsUrl() {
        return HOST + ENDPOINT + IDS_PARAM;
    }

    // TODO export in separate service
    private String calculateListAverage(List<Review> reviews) {
        double averageDouble = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        return averageToString(averageDouble);
    }

    // TODO export in separate service
    private String averageToString(double average) {
        return (new BigDecimal(average)).setScale(1, RoundingMode.HALF_UP).toString();
    }
}
