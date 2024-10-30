package com.cloudq.cloudq.ui;

import com.cloudq.cloudq.model.SearchRequest;
import com.cloudq.cloudq.model.SearchResponse;
import com.cloudq.cloudq.exeption.ResourceNotFoundException;
import com.cloudq.cloudq.model.User;
import com.cloudq.cloudq.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchUIService {

    private final SearchService searchService;

    @Autowired
    public SearchUIService(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Executes a search using the provided search request and returns the results.
     *
     * @param query The search query.
     * @return A SearchResponse containing the results.
     * @throws ResourceNotFoundException If no results are found.
     */
    public SearchResponse executeSearch(String query) {
        SearchRequest searchRequest = new SearchRequest(query);
        return searchService.search(searchRequest);
    }

    /**
     * Formats the search response for the UI.
     *
     * @param searchResponse The search response.
     * @return A formatted response suitable for the frontend.
     */
    public String formatSearchResponse(SearchResponse searchResponse) {
        StringBuilder formattedResponse = new StringBuilder("Search Results:\n");
        for (User user : searchResponse.getResults()) {
            formattedResponse.append("User: ").append(user.getUsername()).append("\n");
        }
        formattedResponse.append("Total Users Found: ").append(searchResponse.getTotalResults());
        return formattedResponse.toString();
    }
}