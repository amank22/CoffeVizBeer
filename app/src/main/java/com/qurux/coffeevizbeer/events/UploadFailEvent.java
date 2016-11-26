package com.qurux.coffeevizbeer.events;

import android.net.Uri;

/**
 * Created by Aman Kapoor on 26-11-2016.
 */

public class UploadFailEvent {

    private Uri oldUploadSessionUri;
    private int position;

    public UploadFailEvent(Uri oldUploadSessionUri, int position) {
        this.oldUploadSessionUri = oldUploadSessionUri;
        this.position = position;
    }

    public Uri getOldUploadSessionUri() {
        return oldUploadSessionUri;
    }

    public int getPosition() {
        return position;
    }
}
