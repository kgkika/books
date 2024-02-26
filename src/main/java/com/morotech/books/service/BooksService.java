package com.morotech.books.service;

import com.morotech.books.domain.Review;
import com.morotech.books.model.Book;
import com.morotech.books.payload.BookResponsePayload;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    @Cacheable(value = "booksTerm")
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
                                    calculateAverage(reviews),
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

    private String getSearchUrl() {
        return HOST + ENDPOINT + SEARCH_PARAM;
    }

    private String getBooksByIdsUrl() {
        return HOST + ENDPOINT + IDS_PARAM;
    }

    private String calculateAverage(List<Review> reviews) {
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        return (new BigDecimal(average)).setScale(1, RoundingMode.HALF_UP).toString();
    }
}
