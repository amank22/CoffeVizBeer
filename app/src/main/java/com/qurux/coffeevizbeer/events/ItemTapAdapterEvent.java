package com.qurux.coffeevizbeer.events;

/**
 * Created by Aman Kapoor on 20-11-2016.
 */

public class ItemTapAdapterEvent {

    private int tapType = -1;
    private int position = -1;

    public ItemTapAdapterEvent(int tapType, int position) {
        this.tapType = tapType;
        this.position = position;
    }

    public int getTapType() {
        return tapType;
    }

    public int getPosition() {
        return position;
    }
}
