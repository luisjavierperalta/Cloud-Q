package com.cloudq.cloudq.model;


import java.util.List;

public class SearchResponse {

    private List<User> results; // List of search results
    private int totalResults; // Total number of results

    // Constructors, getters, and setters

    public SearchResponse() {}

    public SearchResponse(List<User> results, int totalResults) {
        this.results = results;
        this.totalResults = totalResults;
    }

    public List<User> getResults() {
        return results;
    }

    public void setResults(List<User> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
