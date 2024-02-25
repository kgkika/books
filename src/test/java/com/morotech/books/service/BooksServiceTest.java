package com.morotech.books.service;

import com.morotech.books.model.Author;
import com.morotech.books.model.Book;
import com.morotech.books.utils.ConverterUtils;
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

    @Test
    public void testSuccessGetBookByTerm() {
        LinkedHashMap<String, Object> bookMap = new LinkedHashMap<>();
        bookMap.put("id", 1400);
        bookMap.put("language", List.of("en"));
        bookMap.put("authors", List.of(new Author("Dickens, Charles", "1812", "1870")));
        bookMap.put("title", "A tale of Two Cities");
        bookMap.put("downloadCount", 5);

        List<LinkedHashMap<String, Object>> resultsList = List.of(bookMap);

        LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("count", 2);
        responseMap.put("next", null);
        responseMap.put("previous", null);
        responseMap.put("results", resultsList);

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

        List<Book> books = List.of(
                new Book(1400,
                        "A tale of Two Cities",
                        List.of(new Author("Dickens, Charles", "1812", "1870")),
                        List.of("en"),
                        null)
        );

        when(converterUtils.convertToList(responseMap)).thenReturn(books);

        assertEquals(service.getBooksByTerm("di").size(), 1);
        assertEquals(service.getBooksByTerm("di").get(0).getAuthors().size(), 1);
        assertNull(service.getBooksByTerm("di").get(0).getDownloadCount());
        assertNotNull(service.getBooksByTerm("di").get(0).getTitle());
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
        assertNull(service.getBooksByTerm("di"));
        verify(converterUtils, never()).convertToList(any());
    }
}
