package com.qurux.coffeevizbeer.ui.fragments;


import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.TextView;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.adapter.PostsAdapter;
import com.qurux.coffeevizbeer.app.CvBApp;
import com.qurux.coffeevizbeer.bean.User;
import com.qurux.coffeevizbeer.events.ErrorEvent;
import com.qurux.coffeevizbeer.events.SearchEvent;
import com.qurux.coffeevizbeer.helper.CvBUtil;
import com.qurux.coffeevizbeer.local.CvBContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ALL_POSTS_LOADER = 0;
    public static final int ALL_BOOKMARKED_POSTS_LOADER = 1;
    public static final int ALL_SEARCH_LOADER = 2;
    public static final int USER_POSTS_LOADER = 3;
    private static final String ARG_POST_TYPE = "post_type";
    private static final String SEARCH_KEY = "search_key";
    private static final String POSITION_KEY = "position_key";
    private static final String SAVED_LAYOUT_MANAGER = "saved_layout_manager";
    private RecyclerView.LayoutManager layoutManager;
    private int type = ALL_POSTS_LOADER;
    private PostsAdapter adapter;
    private TextView errorText;
    private int oldPosition = 0;
    private Parcelable layoutManagerSavedState;

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
//        setRetainInstance(true);
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
        errorText = (TextView) rootView.findViewById(R.id.error_text);
        handleError(new ErrorEvent(ErrorEvent.LOADING));
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.posts_recyclerView);
        recyclerView.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
        }
        adapter = new PostsAdapter(getActivity(), null);
        recyclerView.setAdapter(adapter);
        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(ARG_POST_TYPE, ALL_POSTS_LOADER);
            oldPosition = savedInstanceState.getInt(POSITION_KEY + "_" + type, 0);
            layoutManagerSavedState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, layoutManager.onSaveInstanceState());
        outState.putInt(ARG_POST_TYPE, type);
        if (layoutManager instanceof LinearLayoutManager)
            outState.putInt(POSITION_KEY + "_" + type, ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition());
        else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int spanPositions[] = new int[2];
            spanPositions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(spanPositions);
            final int min = Math.min(Math.max(0, spanPositions[0]), Math.max(0, spanPositions[1]));
            outState.putInt(POSITION_KEY + "_" + type, min);
        }
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
        } else if (id == USER_POSTS_LOADER) {
            User user = CvBApp.getInstance().getUserExtra();
            if (user == null) {
                return null;
            }
            Map<String, Boolean> posts = user.getPosts();
            if (posts == null) {
                return null;
            }
            Set<String> keys = posts.keySet();
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (String key : keys) {
                sb.append("'").append(key).append("' ").append(",");
            }
            String argsString = sb.substring(0, sb.length() - 2) + ")";
            CvBUtil.log(argsString);
            loader = new CursorLoader(this.getActivity(), CvBContract.PostsEntry.CONTENT_URI, null,
                    CvBContract.PostsEntry.COLUMN_SERVER_ID + " IN " + argsString,
                    null,
                    CvBContract.PostsEntry._ID + " DESC");
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        User user = CvBApp.getInstance().getUserExtra();
        if (data.getCount() == 0 && !CvBUtil.isConnectedToInternet(getContext())) {
            handleError(new ErrorEvent(ErrorEvent.ERROR_NO_NETWORK));
        } else if (user == null && type == USER_POSTS_LOADER) {
            handleError(new ErrorEvent(ErrorEvent.ERROR_USER_LOADING));
        } else if (data.getCount() == 0) {
            handleError(new ErrorEvent(ErrorEvent.ERROR_NO_POSTS));
        } else {
            handleError(new ErrorEvent(ErrorEvent.DEFAULT));
        }
        adapter.swapCursor(data);
        if (oldPosition > 0) {
            layoutManager.scrollToPosition(oldPosition);
        }
        restoreLayoutManagerPosition();
    }

    private void restoreLayoutManagerPosition() {
        if (layoutManagerSavedState != null) {
            layoutManager.onRestoreInstanceState(layoutManagerSavedState);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

    public PostsAdapter getAdapter() {
        return adapter;
    }

    public void handleError(ErrorEvent event) {
        CvBUtil.log("for type:" + type + " error:" + event.getError());
        event.setError(getContext(), event.getError(), errorText);
    }
}
