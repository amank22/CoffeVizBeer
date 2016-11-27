package com.qurux.coffeevizbeer.events;

/**
 * Created by Aman Kapoor on 27-11-2016.
 */

public class SearchEvent {

    private String searchText;

    public SearchEvent(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }
}
