package com.qurux.coffeevizbeer.events;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.View;
import android.widget.TextView;

import com.qurux.coffeevizbeer.R;

/**
 * Created by Aman Kapoor on 01-12-2016.
 */

public class ErrorEvent {

    public static final int ERROR_NO_NETWORK = 4;
    public static final int ERROR_NO_POSTS = 1;
    public static final int ERROR_USER_LOADING = 2;
    public static final int ERROR_UNKNOWN = 3;
    public static final int LOADING = 5;
    public static final int DEFAULT = -1;

    private int error;

    public ErrorEvent(int error) {
        this.error = error;
    }

    public int getError() {
        return error;
    }

    public void setError(Context context, int error, TextView textView) {
        String message;
        int resId;
        switch (error) {
            case ERROR_NO_NETWORK:
                message = context.getString(R.string.error_no_internet);
                resId = R.drawable.ic_no_wifi;
                break;
            case ERROR_NO_POSTS:
                message = context.getString(R.string.error_no_posts);
                resId = R.drawable.ic_no_posts;
                break;
            case ERROR_USER_LOADING:
                message = context.getString(R.string.error_user_loading);
                resId = R.drawable.ic_user_waiting;
                break;
            case ERROR_UNKNOWN:
                message = context.getString(R.string.error_unknown);
                resId = R.drawable.ic_unknown_error;
                break;
            case LOADING:
                message = context.getString(R.string.default_loading_text);
                resId = R.drawable.ic_loading;
                break;
            default:
                message = null;
                resId = R.drawable.ic_loading;
        }
        textView.setText(message);
        Drawable errorDrawable = AppCompatDrawableManager.get().getDrawable(context, resId);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, errorDrawable, null, null);
        if (message == null)
            makeViewHide(textView);
        else
            makeViewVisible(textView);
    }

    private void makeViewVisible(View view) {
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1).start();
    }

    private void makeViewHide(View view) {
        view.animate().alpha(0).start();
        view.setVisibility(View.GONE);
    }

}
