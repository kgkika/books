package com.morotech.books.service;

import com.morotech.books.domain.Review;
import com.morotech.books.model.Author;
import com.morotech.books.model.Book;
import com.morotech.books.payload.BookResponsePayload;
import com.morotech.books.repository.ReviewRepository;
import com.morotech.books.utils.ConverterUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BooksServiceTest {

    @InjectMocks
    private BooksService service;

    @Mock
    private RestTemplate template;

    @Mock
    private ConverterUtils converterUtils;

    @Mock
    private ReviewRepository reviewRepository;

    private LinkedHashMap<String, Object> responseMap;

    private List<Book> books;

    private ResponseEntity<LinkedHashMap<String, Object>> response;

    @BeforeEach
    public void setup() {
        LinkedHashMap<String, Object> bookMap = new LinkedHashMap<>();
        bookMap.put("id", 1400);
        bookMap.put("language", List.of("en"));
        bookMap.put("authors", List.of(new Author("Dickens, Charles", "1812", "1870")));
        bookMap.put("title", "A tale of Two Cities");
        bookMap.put("downloadCount", 5);

        List<LinkedHashMap<String, Object>> resultsList = List.of(bookMap);

        responseMap = new LinkedHashMap<>();
        responseMap.put("count", 2);
        responseMap.put("next", null);
        responseMap.put("previous", null);
        responseMap.put("results", resultsList);

        response = new ResponseEntity<>(
                responseMap,
                HttpStatusCode.valueOf(200)
        );

        books = List.of(
                new Book(1400,
                        "A tale of Two Cities",
                        List.of(new Author("Dickens, Charles", "1812", "1870")),
                        List.of("en"),
                        null)
        );
    }

    @Test
    public void testSuccessGetBookByTerm() {
        when(template.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);
        when(converterUtils.convertToList(responseMap)).thenReturn(books);
        List<Book> books = service.getBooksByTerm("di");
        assertEquals(books.size(), 1);
        assertEquals(books.get(0).getAuthors().size(), 1);
        assertNull(books.get(0).getDownloadCount());
        assertNotNull(books.get(0).getTitle());
    }

    @Test
    public void testSuccessGetBookByTerm_noBooks() {
        LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("count", 2);
        responseMap.put("next", null);
        responseMap.put("previous", null);
        responseMap.put("results", new LinkedHashMap<>());
        ResponseEntity<LinkedHashMap<String, Object>> response = new ResponseEntity<>(
                responseMap,
                HttpStatusCode.valueOf(200)
        );
        when(template.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);
        assertEquals(service.getBooksByTerm("di").size(), 0);
        verify(converterUtils, times(1)).convertToList(any());
    }

    @Test
    public void testFailGetBookByTerm_methodNotSupported() {
        when(template.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("Method not supported"));
        assertTrue(service.getBooksByTerm("di").isEmpty());
        verify(converterUtils, never()).convertToList(any());
    }

    @Test
    public void testSuccessGetBookDetails() {
        when(template.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);
        when(converterUtils.convertToBook(responseMap)).thenReturn(books.get(0));
        when(reviewRepository.findAllByBookId(1400)).thenReturn(List.of(
                new Review(1400, 5, "Lorem Ipsum"),
                new Review(1400, 2, "Lorem Ipsum"),
                new Review(1400, 3, "Lorem Ipsum"),
                new Review(1400, 4, "Lorem Ipsum"),
                new Review(1400, 5, "Lorem Ipsum"),
                new Review(1400, 1, "Lorem Ipsum")
        ));
        ResponseEntity<BookResponsePayload> bookResponse = service.getBookDetails("1400");
        Book book = Objects.requireNonNull(bookResponse.getBody()).getBook();
        assertEquals(book.getAuthors().size(), 1);
        assertEquals(bookResponse.getBody().getAvgRating(), "3.3");
        assertEquals(bookResponse.getBody().getReviews().size(), 6);
    }
}
