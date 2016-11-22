package com.qurux.coffeevizbeer.ui.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.ui.AddPostActivity;
import com.qurux.coffeevizbeer.ui.HomeActivity;

/**
 * Created by Aman Kapoor on 09-11-2016.
 */

public class TabsLayoutFragment extends Fragment {

    private HomeActivity activity;
    private TabLayout tabLayout;
    private Fragment currentFrag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (HomeActivity) getActivity();
        LinearLayout toolbar = (LinearLayout) view.findViewById(R.id.toolbar);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddPostActivity.class)));
        currentFrag = PostsFragment.newInstance();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frag_container_tabs, currentFrag)
                .commit();
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_vector_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_vector_bookmark));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_vector_user));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentFrag = PostsFragment.newInstance();
                        break;
                    case 1:
                        currentFrag = PostsFragment.newInstance(PostsFragment.ALL_BOOKMARKED_POSTS_LOADER);
                        break;
                }
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frag_container_tabs, currentFrag)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
