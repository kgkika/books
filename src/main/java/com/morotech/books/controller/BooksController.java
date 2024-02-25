package com.morotech.books.controller;

import com.morotech.books.model.Book;
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
     * Returns a list of books based on the term parameter
     *
     * @param term The term to search (author or title)
     */
    @GetMapping
    public ResponseEntity<List<Book>> searchBooksByTerm(@RequestParam String term) {
        List<Book> books = service.getBooksByTerm(term);
        return books.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(books);
    }

    @GetMapping("/paged")
    public Page<Book> searchBooksByTermPaged(@RequestParam String term) {
        return service.getBooksByTermPaged(term);
    }
}
