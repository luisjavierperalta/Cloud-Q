package com.cloudq.cloudq.model;

public class SearchRequest {

    private String query; // The search term

    // Constructors, getters, and setters

    public SearchRequest() {}

    public SearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}