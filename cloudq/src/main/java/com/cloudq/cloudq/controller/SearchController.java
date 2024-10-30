package com.cloudq.cloudq.controller;

import com.cloudq.cloudq.model.SearchRequest;
import com.cloudq.cloudq.model.SearchResponse;
import com.cloudq.cloudq.exeption.ResourceNotFoundException;
import com.cloudq.cloudq.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<SearchResponse> search(@RequestBody SearchRequest searchRequest) {
        SearchResponse searchResponse = searchService.search(searchRequest);
        return ResponseEntity.ok(searchResponse);
    }
}