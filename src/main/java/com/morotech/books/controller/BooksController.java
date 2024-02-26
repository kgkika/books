package com.morotech.books.controller;

import com.morotech.books.model.Book;
import com.morotech.books.payload.BookResponsePayload;
import com.morotech.books.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    private final BooksService service;

    @Autowired
    public BooksController(BooksService service) {
        this.service = service;
    }

    /**
     * @param term The term to search (author or title)
     * @return Returns a {@link List}<{@link Book}> based on the term parameter
     */
    @GetMapping
    public ResponseEntity<List<Book>> searchBooksByTerm(@RequestParam String term) {
        List<Book> books = service.getBooksByTerm(term);
        return books.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(books);
    }

    /**
     * @param term The term to search (author or title)
     * @return Returns a Page of {@link Book} based on the term parameter
     */
    @GetMapping("/paged")
    public Page<Book> searchBooksByTermPaged(@RequestParam String term) {
        return service.getBooksByTermPaged(term);
    }

    /**
     * @param bookId The book id
     * @return Returns the book info as {@link BookResponsePayload}
     */
    @GetMapping("/details/{bookId}")
    public ResponseEntity<BookResponsePayload> getBooksDetails(@PathVariable String bookId) {
        return service.getBookDetails(bookId);
    }

}
