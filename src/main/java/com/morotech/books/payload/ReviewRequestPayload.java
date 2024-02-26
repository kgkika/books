package com.morotech.books.payload;

public record ReviewRequestPayload(Integer bookId, Integer rating, String review) {

}
