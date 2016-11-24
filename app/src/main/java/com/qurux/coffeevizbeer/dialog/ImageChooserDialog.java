package com.qurux.coffeevizbeer.dialog;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.events.ImageChooserEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Aman Kapoor on 23-11-2016.
 */

public class ImageChooserDialog extends DialogFragment {

    public ImageChooserDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the XML view for the help dialog fragment
        View view = inflater.inflate(R.layout.dialog_image_chooser, container);
        TextView image = (TextView) view.findViewById(R.id.dialog_item_photo);
        TextView avatar = (TextView) view.findViewById(R.id.dialog_item_avatar);
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_camera);
        Drawable drawableAvatar = AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_avatar);
        image.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        avatar.setCompoundDrawablesWithIntrinsicBounds(null, drawableAvatar, null, null);
        image.setOnClickListener(view1 -> EventBus.getDefault().post(new ImageChooserEvent(true, false)));
        avatar.setOnClickListener(view1 -> EventBus.getDefault().post(new ImageChooserEvent(false, true)));
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
