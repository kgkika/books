package com.morotech.books.payload;

import java.time.Month;
import java.util.Map;

public class MonthlyAvgRatingResponse {

    private Map<Integer, Map<Month, Double>> averageRating;

    public MonthlyAvgRatingResponse(Map<Integer, Map<Month, Double>> averageRating) {
        this.averageRating = averageRating;
    }

    public Map<Integer, Map<Month, Double>> getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Map<Integer, Map<Month, Double>> averageRating) {
        this.averageRating = averageRating;
    }
}
