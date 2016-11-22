package com.qurux.coffeevizbeer.events;

import android.support.annotation.NonNull;

import com.qurux.coffeevizbeer.bean.Post;

/**
 * Created by Aman Kapoor on 20-11-2016.
 */

public class ItemTapEvent {

    public static final int TAP_LIKED = 4;
    public static final int TAP_BOOKMARKED = 1;
    public static final int TAP_SHARE = 2;
    public static final int TAP_READMORE = 3;

    private int tapType = -1;
    private int state = -1;
    private int position = -1;
    private String postServerId;
    private Post post;

    public ItemTapEvent(int tapType, int position) {
        this.tapType = tapType;
        this.position = position;
    }

    public ItemTapEvent(int tapType, String postServerId, int state) {
        this.tapType = tapType;
        this.postServerId = postServerId;
        this.state = state;
    }

    public ItemTapEvent(int tapType, @NonNull Post post) {
        this.tapType = tapType;
        this.post = post;
    }

    public int getState() {
        return state;
    }

    public String getPostServerId() {
        return postServerId;
    }

    public int getTapType() {
        return tapType;
    }

    public Post getPost() {
        return post;
    }

    public int getPosition() {
        return position;
    }
}
