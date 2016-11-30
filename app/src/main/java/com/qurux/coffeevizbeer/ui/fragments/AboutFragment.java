package com.qurux.coffeevizbeer.ui.fragments;


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
import com.qurux.coffeevizbeer.app.CvBApp;
import com.qurux.coffeevizbeer.bean.User;
import com.qurux.coffeevizbeer.helper.CvBUtil;
import com.qurux.coffeevizbeer.local.CvBContract;

import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int USER_POSTS_LOADER = 5;

    private RecyclerView recyclerView;
    private PostsAdapter adapter;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(USER_POSTS_LOADER, null, this);
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
        recyclerView = (RecyclerView) view.findViewById(R.id.posts_recyclerView);
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
        User user = CvBApp.getInstance().getUserExtra();
        if (user == null) {
            return null;
        }
        Map<String, Boolean> posts = user.getPosts();
        if (posts != null) {
            Set<String> keys = posts.keySet();
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (String key : keys) {
                sb.append("'").append(key).append("' ").append(",");
            }
            String argsString = sb.substring(0, sb.length() - 2) + ")";
            CvBUtil.log(argsString);
            return new CursorLoader(this.getActivity(), CvBContract.PostsEntry.CONTENT_URI, null,
                    CvBContract.PostsEntry.COLUMN_SERVER_ID + " IN " + argsString,
                    null,
                    CvBContract.PostsEntry._ID + " DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader == null) {
            return;
        }
        CvBUtil.log(String.valueOf(data.getCount()));
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader == null) {
            return;
        }
        adapter.swapCursor(null);
    }
}
