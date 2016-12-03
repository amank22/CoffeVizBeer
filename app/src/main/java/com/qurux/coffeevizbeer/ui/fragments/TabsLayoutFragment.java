package com.qurux.coffeevizbeer.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
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
    private ViewPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RelativeLayout toolbar = (RelativeLayout) view.findViewById(R.id.toolbar);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddPostActivity.class)));
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_tabs);
        setupViewPager(viewPager);
        searchBox = (EditText) view.findViewById(R.id.editTextSearch);
        setupTabLayout(view);
        if (savedInstanceState != null) {
            currentPos = savedInstanceState.getInt(KEY_TAB_CURRENT, 0);
            if (currentPos != 0) {
                viewPager.setCurrentItem(currentPos, true);

            }
        }
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchBox.getText().length() > 2) {
                    searchBox.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatDrawableManager.get().getDrawable(getContext(),
                            R.drawable.ic_vector_remove), null);
                    EventBus.getDefault().post(new SearchEvent(searchBox.getText().toString()));
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchBox.getText().toString());
                    bundle1.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SEARCH, bundle1);
                } else if (searchBox.getText().length() == 2) {
                    EventBus.getDefault().post(new SearchEvent(null));
                    searchBox.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                searchBox.clearFocus();
                return true;
            }
            return false;
        });
        searchBox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && searchBox.getCompoundDrawables()[2] != null) {
                final int bound = searchBox.getRight() - searchBox.getPaddingRight() -
                        searchBox.getCompoundDrawables()[2].getBounds().width();
                if (event.getRawX() >= bound) {
                    clearSearch();
                    return true;
                }
            }
            return false;
        });
    }

    private void clearSearch() {
        EventBus.getDefault().post(new SearchEvent(null));
        searchBox.setText(null);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        searchBox.clearFocus();
        searchBox.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    private void setupTabLayout(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_vector_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_vector_bookmark);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_vector_user);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPos = tab.getPosition();
                CvBUtil.log("Current Select:" + currentPos);
                switch (tab.getPosition()) {
                    case 0:
                        searchBox.setHint(R.string.search_hint);
                        break;
                    case 1:
                        searchBox.setHint(R.string.search_hint_bookmark);
                        break;
                    case 2:
                        searchBox.setHint(R.string.search_hint_about);
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TAB_CURRENT, currentPos);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(PostsFragment.newInstance());
        adapter.addFragment(PostsFragment.newInstance(PostsFragment.ALL_BOOKMARKED_POSTS_LOADER));
        adapter.addFragment(new AboutFragment());
        viewPager.setAdapter(adapter);
    }

    public static class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {
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
