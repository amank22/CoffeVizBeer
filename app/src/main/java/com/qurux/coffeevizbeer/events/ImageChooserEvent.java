package com.qurux.coffeevizbeer.events;

/**
 * Created by Aman Kapoor on 24-11-2016.
 */

public class ImageChooserEvent {

    public static final int OPTION_CAMERA = 789;
    public static final int OPTION_GALLERY = 790;
    public static final int OPTION_AVATAR = 791;

    private int selectedOption;
    private int callingPosition;

    public ImageChooserEvent(int selectedOption, int callingPosition) {
        this.selectedOption = selectedOption;
        this.callingPosition = callingPosition;
    }

    public int getCallingPosition() {
        return callingPosition;
    }

    public int getSelectedOption() {
        return selectedOption;
    }
}
