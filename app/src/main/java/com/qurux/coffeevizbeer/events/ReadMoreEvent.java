package com.qurux.coffeevizbeer.events;

/**
 * Created by Aman Kapoor on 02-12-2016.
 */

public class ReadMoreEvent {

    private int cursorId;

    public ReadMoreEvent(int cursorId) {
        this.cursorId = cursorId;
    }

    public int getCursorId() {
        return cursorId;
    }
}

