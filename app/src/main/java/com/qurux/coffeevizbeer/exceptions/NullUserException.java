package com.qurux.coffeevizbeer.exceptions;

/**
 * Created by Aman Kapoor on 06-12-2016.
 */

public class NullUserException extends Exception {

    public NullUserException() {
        super("Your details are is not loaded");
    }
}
