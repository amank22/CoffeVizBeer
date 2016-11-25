package com.qurux.coffeevizbeer.dialog;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    public static final String ITEM_KEY = "option_item_key";

    public static final int OPTION_THIS = 0;
    public static final int OPTION_THAT = 1;
    public static final int OPTION_INVALID = -522;

    private int option = OPTION_INVALID;

    public ImageChooserDialog() {
    }

    public static ImageChooserDialog getInstance(int option) {
        ImageChooserDialog dialog = new ImageChooserDialog();
        Bundle bundle = new Bundle(1);
        bundle.putInt(ITEM_KEY, option);
        dialog.setArguments(bundle);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            option = getArguments().getInt(ITEM_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the XML view for the help dialog fragment
        View view = inflater.inflate(R.layout.dialog_image_chooser, container);
        TextView camera = (TextView) view.findViewById(R.id.dialog_item_photo);
        TextView gallery = (TextView) view.findViewById(R.id.dialog_item_gallery);
        TextView avatar = (TextView) view.findViewById(R.id.dialog_item_avatar);
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_vector_camera);
        Drawable drawableGallery = AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_vector_gallery);
        Drawable drawableAvatar = AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_vector_avatar);
        camera.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        gallery.setCompoundDrawablesWithIntrinsicBounds(null, drawableGallery, null, null);
        avatar.setCompoundDrawablesWithIntrinsicBounds(null, drawableAvatar, null, null);
        camera.setOnClickListener(view1 -> EventBus.getDefault().post(new ImageChooserEvent(ImageChooserEvent.OPTION_CAMERA, option)));
        gallery.setOnClickListener(view1 -> EventBus.getDefault().post(new ImageChooserEvent(ImageChooserEvent.OPTION_GALLERY, option)));
        avatar.setOnClickListener(view1 -> EventBus.getDefault().post(new ImageChooserEvent(ImageChooserEvent.OPTION_AVATAR, option)));
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
