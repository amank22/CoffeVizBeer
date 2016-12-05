package com.qurux.coffeevizbeer.exceptions;

/**
 * Created by Aman Kapoor on 06-12-2016.
 */

public class EmptyDataException extends Exception {

    public EmptyDataException() {
        super("There are no posts");
    }
}
