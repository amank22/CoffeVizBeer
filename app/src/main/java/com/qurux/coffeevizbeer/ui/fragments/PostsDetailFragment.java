package com.qurux.coffeevizbeer.ui.fragments;


import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.events.ItemTapEvent;
import com.qurux.coffeevizbeer.helper.FireBaseHelper;
import com.qurux.coffeevizbeer.helper.ThisThatView;
import com.qurux.coffeevizbeer.local.CvBContract;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_PARCEL = "content";

    private static final int POST_DETAIL_LOADER = 4556;
    private static final String TAG = "DetailClass";
    private String serverIdText;
    private Integer bookmarkedInt, likedInt;
    private int _id;
    private TextView title;
    private TextView author;
    private TextView descrption;
    private ImageButton like, bookmark, share;
    private TextView date;
    private ThisThatView thisThatView;

    public PostsDetailFragment() {
        // Required empty public constructor
    }

    public static PostsDetailFragment newInstance(int _id) {
        PostsDetailFragment fragment = new PostsDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARCEL, _id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _id = getArguments().getInt(ARG_PARCEL);
            Log.d(TAG, "onCreate: " + _id);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(POST_DETAIL_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        title = (TextView) v.findViewById(R.id.post_item_title);
        author = (TextView) v.findViewById(R.id.post_item_author);
        date = (TextView) v.findViewById(R.id.post_item_date);
        descrption = (TextView) v.findViewById(R.id.post_item_description);
        like = (ImageButton) v.findViewById(R.id.button_like);
        bookmark = (ImageButton) v.findViewById(R.id.button_bookmark);
        share = (ImageButton) v.findViewById(R.id.button_share);
        thisThatView = (ThisThatView) v.findViewById(R.id.this_that_view_item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getActivity(), CvBContract.PostsEntry.buildPostsUri(_id), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {
        if (dataCursor.getCount() <= 0) {
            return;
        }
        dataCursor.moveToFirst();
        String titleText = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_TITLE));
        String authorText = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_AUTHOR));
        String dateText = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_DATE));
        String descriptionText = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_DESCRIPTION));
        String linkThisString = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LINK_THIS));
        String linkThatString = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LINK_THAT));
        String color = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_COLOR));
        likedInt = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LIKED));
        bookmarkedInt = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_BOOKMARKED));
        serverIdText = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_SERVER_ID));
        title.setText(titleText);
        author.setText(authorText);
        date.setText(dateText.split(" ")[0]);
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(getActivity(), R.drawable.ic_vector_user_black);
        author.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        descrption.setText(descriptionText);
        try {
            thisThatView.setImageToAllImages(new String[]{linkThisString, linkThatString});
        } catch (Exception e) {
            e.printStackTrace();
        }
        thisThatView.setBackgroundColor(Color.parseColor(color));
        title.setBackgroundColor(Color.parseColor(color));
        date.setBackgroundColor(Color.parseColor(color));
        if (bookmarkedInt == 0)
            bookmark.setImageResource(R.drawable.ic_vector_bookmark_black);
        else if (bookmarkedInt == 1)
            bookmark.setImageResource(R.drawable.ic_vector_bookmark_red);
        if (likedInt == 0)
            like.setImageResource(R.drawable.ic_vector_like);
        else if (likedInt == 1)
            like.setImageResource(R.drawable.ic_vector_like_red);
        bookmark.setOnClickListener(view -> handleBookmark(serverIdText, bookmarkedInt));
        like.setOnClickListener(view -> handleLike(serverIdText, likedInt));
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Nothing in here
    }


    private void handleBookmark() {
        int newBookmarked = 0;
        if (bookmarkedInt == 0) {
            newBookmarked = 1;
        } else if (bookmarkedInt == 1) {
            newBookmarked = 0;
        }
        EventBus.getDefault().post(new ItemTapEvent(ItemTapEvent.TAP_BOOKMARKED, serverIdText, newBookmarked));
    }

    private void handleLike() {
        int newLike = 0;
        if (likedInt == 0) {
            newLike = 1;
        } else if (likedInt == 1) {
            newLike = 0;
        }
        EventBus.getDefault().post(new ItemTapEvent(ItemTapEvent.TAP_LIKED, serverIdText, newLike));
    }
}
