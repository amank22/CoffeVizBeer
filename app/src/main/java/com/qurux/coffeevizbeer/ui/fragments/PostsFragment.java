package com.qurux.coffeevizbeer.ui.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.adapter.PostsAdapter;
import com.qurux.coffeevizbeer.events.ItemTapAdapterEvent;
import com.qurux.coffeevizbeer.events.ItemTapEvent;
import com.qurux.coffeevizbeer.events.SearchEvent;
import com.qurux.coffeevizbeer.helper.FireBaseHelper;
import com.qurux.coffeevizbeer.local.CvBContract;
import com.qurux.coffeevizbeer.ui.DetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ALL_POSTS_LOADER = 0;
    public static final int ALL_BOOKMARKED_POSTS_LOADER = 1;
    public static final int ALL_SEARCH_LOADER = 2;
    private static final String ARG_POST_TYPE = "post_type";
    private static final String SEARCH_KEY = "search_key";
    private RecyclerView recyclerView;

    private int type = ALL_POSTS_LOADER;
    private PostsAdapter adapter;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(int postType) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POST_TYPE, postType);
        fragment.setArguments(args);
        return fragment;
    }

    public static PostsFragment newInstance() {
        return PostsFragment.newInstance(ALL_POSTS_LOADER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_POST_TYPE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(type, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_home, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        recyclerView = (RecyclerView) rootView;
        recyclerView.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PostsAdapter(getActivity(), null);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == ALL_POSTS_LOADER)
            loader = new CursorLoader(this.getActivity(), CvBContract.PostsEntry.CONTENT_URI, null, null, null,
                    CvBContract.PostsEntry._ID + " DESC");
        else if (id == ALL_BOOKMARKED_POSTS_LOADER) {
            String selection = CvBContract.PostsEntry.COLUMN_BOOKMARKED + "=1";
            loader = new CursorLoader(this.getActivity(), CvBContract.PostsEntry.CONTENT_URI, null, selection, null,
                    CvBContract.PostsEntry._ID + " DESC");
        } else if (id == ALL_SEARCH_LOADER) {
            String key = args.getString(SEARCH_KEY, "");
            loader = new CursorLoader(this.getActivity(), CvBContract.PostsEntry.buildSearchUri(key), null, null, null,
                    CvBContract.PostsEntry._ID + " DESC");
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchEvent event) {
        String searchText = event.getSearchText();
        if (searchText != null) {
            Bundle b = new Bundle();
            b.putString(SEARCH_KEY, searchText);
            getLoaderManager().restartLoader(ALL_SEARCH_LOADER, b, this);
        } else {
            getLoaderManager().restartLoader(type, null, this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ItemTapAdapterEvent event) {
        int position = event.getPosition();
        Cursor dataCursor = adapter.getCursorAtPosition(position);
        int liked = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LIKED));
        int bookmarked = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_BOOKMARKED));
        String serverId = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_SERVER_ID));
        if (event.getTapType() == ItemTapEvent.TAP_LIKED) {
            handleLike(serverId, liked);
        } else if (event.getTapType() == ItemTapEvent.TAP_BOOKMARKED) {
            handleBookmark(serverId, bookmarked);
        } else if (event.getTapType() == ItemTapEvent.TAP_READMORE) {
            Intent i = new Intent(getActivity(), DetailActivity.class);
            i.putExtra(DetailActivity.KEY_ID, dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry._ID)));
            startActivity(i);
        }

    }

    private void handleBookmark(String serverId, Integer bookmarked) {
        int newBookmarked = 0;
        if (bookmarked == 0) {
            newBookmarked = 1;
        } else if (bookmarked == 1) {
            newBookmarked = 0;
        }
        FireBaseHelper.firebaseBookmark(getContext(), serverId, newBookmarked);
    }

    private void handleLike(String serverId, Integer liked) {
        int newLike = 0;
        if (liked == 0) {
            newLike = 1;
        } else if (liked == 1) {
            newLike = 0;
        }
        FireBaseHelper.firebaseLike(getContext(), serverId, newLike);
    }
}
