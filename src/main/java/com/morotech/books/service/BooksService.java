package com.morotech.books.service;

import com.morotech.books.model.Book;
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

import java.util.LinkedHashMap;
import java.util.List;

@Service
public class BooksService {

    private static final String HOST = "https://gutendex.com";
    private static final String ENDPOINT = "/books";
    private static final String PARAM = "?search=";

    private static final Logger LOGGER = LogManager.getLogger(BooksService.class);

    private final RestTemplate restTemplate;

    private final ConverterUtils converterUtils;

    @Autowired
    public BooksService(RestTemplate restTemplate, ConverterUtils converterUtils) {
        this.restTemplate = restTemplate;
        this.converterUtils = converterUtils;
    }

    @Cacheable(value = "books")
    public List<Book> getBooksByTerm(String term) {
        try {
            ResponseEntity<LinkedHashMap<String, Object>> responseEntity = restTemplate.exchange(
                    getUrl().concat(term),
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
        return null;
    }

    @Cacheable(value = "books")
    public Page<Book> getBooksByTermPaged(String term) {
        List<Book> books = getBooksByTerm(term);
        return new PageImpl<>(books, PageRequest.of(0, 3), books.size());
    }

    private String getUrl() {
        return HOST + ENDPOINT + PARAM;
    }
}
