package com.qurux.coffeevizbeer.bean;

import java.util.List;
import java.util.Map;

public class User {

    private String phoneNumber;
    private Map<String, Boolean> bookmarked;
    private Map<String, Boolean> posts;
    private List<String> searched;
    private List<String> feedbacks;
    private int rating;

    public User() {
    }

    public User(String phoneNumber, Map<String, Boolean> bookmarked,
                Map<String, Boolean> posts, List<String> searched, List<String> feedbacks, int rating) {
        this.phoneNumber = phoneNumber;
        this.bookmarked = bookmarked;
        this.posts = posts;
        this.searched = searched;
        this.feedbacks = feedbacks;
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Map<String, Boolean> getBookmarked() {
        return bookmarked;
    }

    public Map<String, Boolean> getPosts() {
        return posts;
    }

    public List<String> getSearched() {
        return searched;
    }

    public List<String> getFeedbacks() {
        return feedbacks;
    }

    public int getRating() {
        return rating;
    }
}
