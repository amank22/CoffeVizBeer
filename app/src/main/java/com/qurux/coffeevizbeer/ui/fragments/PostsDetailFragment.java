package com.qurux.coffeevizbeer.ui.fragments;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.helper.CvBUtil;
import com.qurux.coffeevizbeer.helper.FireBaseHelper;
import com.qurux.coffeevizbeer.local.CvBContract;
import com.qurux.coffeevizbeer.views.ThisThatView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_PARCEL = "content";
    private static final String regex = "Local:avatar[0-9]{1,2}";
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
    private CollapsingToolbarLayout toolbarLayout;
    private FirebaseAnalytics mFirebaseAnalytics;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
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
        toolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.toolbar_layout);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        setImageToThisThatView(linkThisString, linkThatString);
        thisThatView.setBackgroundColor(Color.parseColor(color));
        title.setBackgroundColor(Color.parseColor(color));
        date.setBackgroundColor(Color.parseColor(color));
        toolbarLayout.setContentScrimColor(Color.parseColor(color));
        setStatusBarColor(Color.parseColor(color));
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
        share.setOnClickListener(view -> {

            String summary = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_SUMMARY));
            String title = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_TITLE));
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + summary + "\n\n" +
                    "Check out more such amazing and weird combo's only on Coffee viz Beer.");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to_title)));
            Bundle bundle1 = new Bundle();
            bundle1.putString("postId", serverIdText);
            bundle1.putInt("shared", 1);
            bundle1.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            mFirebaseAnalytics.logEvent("event_shared", bundle1);
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(color);
    }

    private void setImageToThisThatView(String linkThis, String linkThat) {
        if (linkThis.matches(regex)) {
            thisThatView.removeImage(0);
            int localPos = Character.getNumericValue(linkThis.charAt(linkThis.length() - 2)) * 10 +
                    Character.getNumericValue(linkThis.charAt(linkThis.length() - 1));
            thisThatView.getHolder().get(0).getHierarchy().setPlaceholderImage(CvBUtil.getAvatarResId(localPos));
        } else {
            thisThatView.getHolder().get(0).getHierarchy().setPlaceholderImage(R.drawable.circle_blue);
            thisThatView.setImage(0, linkThis);
        }
        if (linkThat.matches(regex)) {
            int localPos = Character.getNumericValue(linkThat.charAt(linkThat.length() - 2)) * 10 +
                    Character.getNumericValue(linkThat.charAt(linkThat.length() - 1));
            thisThatView.removeImage(1);
            thisThatView.getHolder().get(1).getHierarchy().setPlaceholderImage(CvBUtil.getAvatarResId(localPos));
        } else {
            thisThatView.getHolder().get(1).getHierarchy().setPlaceholderImage(R.drawable.circle_green);
            thisThatView.setImage(1, linkThat);
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
        Bundle bundle1 = new Bundle();
        bundle1.putString("postId", serverId);
        bundle1.putInt("bookmarked", newBookmarked);
        bundle1.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        mFirebaseAnalytics.logEvent("event_bookmark", bundle1);
    }

    private void handleLike(String serverId, Integer liked) {
        int newLike = 0;
        if (liked == 0) {
            newLike = 1;
        } else if (liked == 1) {
            newLike = 0;
        }
        FireBaseHelper.firebaseLike(getContext(), serverId, newLike);
        Bundle bundle = new Bundle();
        bundle.putString("postId", serverId);
        bundle.putInt("liked", newLike);
        bundle.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        mFirebaseAnalytics.logEvent("event_like", bundle);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Nothing in here
    }
}
