package com.morotech.books.payload;

import com.morotech.books.model.Book;

import java.util.List;

public class BookResponsePayload {

    private Book book;

    private String avgRating;

    private List<String> reviews;

    private Error error;

    public BookResponsePayload(Book book, String avgRating, List<String> reviews) {
        this.book = book;
        this.avgRating = avgRating;
        this.reviews = reviews;
    }

    public BookResponsePayload(Error error) {
        this.error = error;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
