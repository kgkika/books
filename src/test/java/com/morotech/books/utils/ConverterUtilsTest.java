package com.morotech.books.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morotech.books.model.Author;
import com.morotech.books.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConverterUtilsTest {

    @InjectMocks
    private ConverterUtils converterUtils;

    @Mock
    private ObjectMapper mapper;

    @Test
    public void testConvertToListSuccess() {
        LinkedHashMap<String, Object> bookMap = new LinkedHashMap<>();
        bookMap.put("id", 1400);
        bookMap.put("language", List.of("en"));
        bookMap.put("authors", List.of(new Author("Dickens, Charles", "1812", "1870")));
        bookMap.put("title", "A tale of Two Cities");
        bookMap.put("downloadCount", 5);
        LinkedHashMap<String, Object> hashMapResponse = new LinkedHashMap<>();
        hashMapResponse.put("results", List.of(bookMap));
        Book book = new Book(1400,
                "A tale of Two Cities",
                List.of(new Author("Dickens, Charles", "1812", "1870")),
                List.of("en"),
                null);
        when(mapper.convertValue(bookMap, Book.class)).thenReturn(book);
        assertEquals(converterUtils.convertToList(hashMapResponse).size(), 1);
    }

    @Test
    public void testConvertToListSuccess_noBooks() {
        LinkedHashMap<String, Object> hashMapResponse = new LinkedHashMap<>();
        hashMapResponse.put("results", new ArrayList<>());
        List<Book> books = converterUtils.convertToList(hashMapResponse);
        assertEquals(books.size(), 0);
        verify(mapper, never()).convertValue(eq(any()), Book.class);
    }

}
