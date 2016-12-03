package com.qurux.coffeevizbeer.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.qurux.coffeevizbeer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    private Fragment aboutPostFrag;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aboutPostFrag = getChildFragmentManager().findFragmentByTag("frag_user_posts");
        if (aboutPostFrag == null) {
            aboutPostFrag = PostsFragment.newInstance(PostsFragment.USER_POSTS_LOADER);
        }
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frag_container_user_posts, aboutPostFrag, "frag_user_posts").commit();
        SimpleDraweeView userImage = (SimpleDraweeView) view.findViewById(R.id.image_user_profile);
        userImage.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        ((TextView) view.findViewById(R.id.text_user_name)).setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ((TextView) view.findViewById(R.id.text_user_mail)).setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    public Fragment getAboutPostFrag() {
        return aboutPostFrag;
    }
}
