package com.qurux.coffeevizbeer.ui.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.events.SearchEvent;
import com.qurux.coffeevizbeer.helper.CvBUtil;
import com.qurux.coffeevizbeer.helper.SmartFragmentStatePagerAdapter;
import com.qurux.coffeevizbeer.ui.AddPostActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aman Kapoor on 09-11-2016.
 */

public class TabsLayoutFragment extends Fragment {

    private static final String KEY_TAB_CURRENT = "key_tab_current";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText searchBox;
    private int currentPos = 0;
    private boolean isOrientationChanged = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout toolbar = (LinearLayout) view.findViewById(R.id.toolbar);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }

        if (savedInstanceState != null) {
            currentPos = savedInstanceState.getInt(KEY_TAB_CURRENT, 0);
            if (currentPos != 0) {
                isOrientationChanged = true;
            }
        }
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddPostActivity.class)));
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_tabs);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isOrientationChanged) {
                    viewPager.setCurrentItem(currentPos);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        searchBox = (EditText) view.findViewById(R.id.editTextSearch);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_vector_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_vector_bookmark);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_vector_user);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPos = tab.getPosition();
                CvBUtil.log("Current:" + currentPos);
                switch (tab.getPosition()) {
                    case 0:
                        searchBox.setText(R.string.search_hint);
                        break;
                    case 1:
                        searchBox.setText(R.string.search_hint_bookmark);
                        break;
                    case 2:
                        searchBox.setText(R.string.search_hint_about);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        searchEditText();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TAB_CURRENT, currentPos);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(PostsFragment.newInstance());
        adapter.addFragment(PostsFragment.newInstance(PostsFragment.ALL_BOOKMARKED_POSTS_LOADER));
        adapter.addFragment(new AboutFragment());
        viewPager.setAdapter(adapter);
    }

    private void searchEditText() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CvBUtil.log(s + ":" + start + ":" + before + ":" + count + ":");
                //ch:2:1:0:
                //gu:1:0:1:
                if ((before == 0 && start > 1) || (before == 1 && start > 2)) {
                    EventBus.getDefault().post(new SearchEvent(s.toString()));
                } else if (before == 1 && start == 2) {
                    EventBus.getDefault().post(new SearchEvent(null));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private static class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }

}
