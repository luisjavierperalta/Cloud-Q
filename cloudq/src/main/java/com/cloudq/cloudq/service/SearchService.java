package com.cloudq.cloudq.service;


import com.cloudq.cloudq.model.SearchRequest;
import com.cloudq.cloudq.model.SearchResponse;
import com.cloudq.cloudq.exeption.ResourceNotFoundException;
import com.cloudq.cloudq.model.SearchRequest;
import com.cloudq.cloudq.model.User;
import com.cloudq.cloudq.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final SearchRepository searchRepository;

    @Autowired
    public SearchService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public SearchResponse search(SearchRequest searchRequest) {
        List<User> results = searchRepository.searchByUsername(searchRequest.getQuery());
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("No users found for query: " + searchRequest.getQuery());
        }
        return new SearchResponse(results, results.size());
    }
}