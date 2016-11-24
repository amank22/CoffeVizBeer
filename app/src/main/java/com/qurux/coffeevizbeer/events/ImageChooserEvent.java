package com.qurux.coffeevizbeer.events;

/**
 * Created by Aman Kapoor on 24-11-2016.
 */

public class ImageChooserEvent {
    private boolean isImageClicked;
    private boolean isAvatarClicked;

    public ImageChooserEvent(boolean isImageClicked, boolean isAvatarClicked) {
        this.isImageClicked = isImageClicked;
        this.isAvatarClicked = isAvatarClicked;
    }

    public boolean isImageClicked() {
        return isImageClicked;
    }

    public boolean isAvatarClicked() {
        return isAvatarClicked;
    }
}
