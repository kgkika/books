package com.morotech.books.controller;

import com.morotech.books.model.Author;
import com.morotech.books.model.Book;
import com.morotech.books.payload.BookResponsePayload;
import com.morotech.books.service.BooksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BooksControllerTest {

    @InjectMocks
    private BooksController controller;

    @Mock
    private BooksService service;

    private List<Book> books;

    @BeforeEach
    public void setup() {
        books = List.of(
                new Book(1400,
                        "A tale of Two Cities",
                        List.of(new Author("Dickens, Charles", "1812", "1870")),
                        List.of("en"),
                        null),
                new Book(1401,
                        "A tale of Two Cities 1",
                        List.of(new Author("Dickens, Charles", "1812", "1870")),
                        List.of("en"),
                        null),
                new Book(1402,
                        "A tale of Two Cities 2",
                        List.of(new Author("Dickens, Charles", "1812", "1870")),
                        List.of("en"),
                        null),
                new Book(1403,
                        "A tale of Two Cities 3",
                        List.of(new Author("Dickens, Charles", "1812", "1870")),
                        List.of("en"),
                        null)
        );
    }

    @Test
    public void testSearchBooksByTerm() {
        when(service.getBooksByTerm("di")).thenReturn(books);
        assertEquals(Objects.requireNonNull(controller.searchBooksByTerm("di").getBody()).size(), 4);
    }

    @Test
    public void testSearchBooksByTermPaged() {
        when(service.getBooksByTermPaged("di")).thenReturn(new PageImpl<>(books, PageRequest.of(0, 3), books.size()));
        assertEquals(controller.searchBooksByTermPaged("di").getContent().size(), 4);
        assertEquals(controller.searchBooksByTermPaged("di").getPageable().getPageNumber(), 0);
        assertEquals(controller.searchBooksByTermPaged("di").getTotalPages(), 2);
    }

    @Test
    public void testGetBookDetails() {
        ResponseEntity<BookResponsePayload> responseEntity = ResponseEntity.ok(new BookResponsePayload(books.get(0), "4.5", List.of("Lorem Ipsum")));
        when(service.getBookDetails("1400")).thenReturn(responseEntity);
        assertEquals(Objects.requireNonNull(controller.getBooksDetails("1400").getBody()).getAvgRating(), "4.5");
    }
}
