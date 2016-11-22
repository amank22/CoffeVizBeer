package com.qurux.coffeevizbeer.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aman Kapoor on 10-11-2016.
 */

public class Post {

    private String title;
    private String linkThis;
    private String linkThat;
    private String summary;
    private String description;
    private String author;
    private String authorId;
    private String date;
    private String color;
    private Map<String, Boolean> likes = new HashMap<>();

    /**
     * Default constructor required for calls to
     * DataSnapshot.getValue(Post.class)
     */
    public Post() {
    }

    /**
     * @param title       title of post
     * @param linkThis    link of the first image. For local avatar it should be "local/{avatar}"
     * @param linkThat    link of the second image. For local avatar it should be "local/{avatar}"
     * @param summary     summary of post of max 100 characters.
     * @param description complete description of post i.e full post
     * @param author      author of post
     * @param authorId    author id to extract more information of the author
     * @param date        Time date when the post is created
     * @param likes       {@link ArrayList} of strings id's of users who have liked the append. Always append to this list.
     * @param color       Color of the background
     */
    public Post(String title, String linkThis, String linkThat, String summary, String description, String author,
                String authorId, String date, Map<String, Boolean> likes, String color) {
        this.title = title;
        this.linkThis = linkThis;
        this.linkThat = linkThat;
        this.summary = summary;
        this.description = description;
        this.author = author;
        this.authorId = authorId;
        this.date = date;
        this.likes = likes;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getLinkThis() {
        return linkThis;
    }

    public String getLinkThat() {
        return linkThat;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getDate() {
        return date;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public String getColor() {
        return color;
    }
}
