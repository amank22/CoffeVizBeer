package com.qurux.coffeevizbeer.events;

import android.database.Cursor;

/**
 * Created by Aman Kapoor on 20-11-2016.
 */

public class ItemTapAdapterEvent {

    private int tapType = -1;
    private Cursor dataCursor;
    private int position;

    public ItemTapAdapterEvent(int tapType, int position) {
        this.tapType = tapType;
        this.position = position;
    }

    public ItemTapAdapterEvent(int tapType, Cursor dataCursor) {
        this.tapType = tapType;
        this.dataCursor = dataCursor;
    }

    public int getTapType() {
        return tapType;
    }

    public int getPosition() {
        return position;
    }

    public Cursor getDataCursor() {
        return dataCursor;
    }
}
