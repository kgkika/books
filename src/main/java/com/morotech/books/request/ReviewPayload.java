package com.morotech.books.request;

public record ReviewPayload(Integer bookId, Integer rating, String review) {

}
