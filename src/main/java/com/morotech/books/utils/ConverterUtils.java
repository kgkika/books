package com.morotech.books.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morotech.books.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ConverterUtils {

    private final ObjectMapper objectMapper;

    @Autowired
    public ConverterUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Book> convertToList(LinkedHashMap<String, Object> hashMapResponse) {
        return hashMapResponse.entrySet().stream()
                .filter(e -> "results".equals(e.getKey()))
                .flatMap(e -> {
                    if (e.getValue() instanceof List && !((List<?>) e.getValue()).isEmpty()) {
                        List<LinkedHashMap<String, Object>> listOfMap = (List<LinkedHashMap<String, Object>>) e.getValue();
                        return listOfMap.stream().map(this::transform);
                    }
                    return Stream.empty();
                })
                .collect(Collectors.toList());
    }

    private Book transform(LinkedHashMap<String, Object> itemMap) {
        return objectMapper.convertValue(itemMap, Book.class);
    }
}
