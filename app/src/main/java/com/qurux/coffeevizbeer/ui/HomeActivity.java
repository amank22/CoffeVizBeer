package com.qurux.coffeevizbeer.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.app.CvBApp;
import com.qurux.coffeevizbeer.events.ItemTapAdapterEvent;
import com.qurux.coffeevizbeer.events.ItemTapEvent;
import com.qurux.coffeevizbeer.helper.FireBaseHelper;
import com.qurux.coffeevizbeer.local.CvBContract;
import com.qurux.coffeevizbeer.ui.fragments.PostsDetailFragment;
import com.qurux.coffeevizbeer.ui.fragments.TabsLayoutFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends BaseActivity {

    private static final String tag = "tabLayout";
    //TODO: Handle cases for tablets also.
    boolean isTablet = false;
    private TabsLayoutFragment fragmentByTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CvBApp.getInstance().setFirebaseListeners();
//        Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
//        startActivity(dbmanager);
        FrameLayout detailContainer = (FrameLayout) findViewById(R.id.container_details);
        isTablet = detailContainer != null;
        fragmentByTag = (TabsLayoutFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (fragmentByTag == null) {
            fragmentByTag = new TabsLayoutFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container_posts, fragmentByTag, tag).commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ItemTapAdapterEvent event) {
        final Cursor dataCursor = event.getDataCursor();
        final int liked = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LIKED));
        final int bookmarked = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_BOOKMARKED));
        final String serverId = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_SERVER_ID));
        switch (event.getTapType()) {
            case ItemTapEvent.TAP_LIKED:
                handleLike(serverId, liked);
                break;
            case ItemTapEvent.TAP_BOOKMARKED:
                handleBookmark(serverId, bookmarked);
                break;
            case ItemTapEvent.TAP_READMORE:
                handleReadMore(dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry._ID)));
                break;
        }

    }

    private void handleReadMore(int id) {
        if (!isTablet) {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(DetailActivity.KEY_ID, id);
            startActivity(i);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_details, PostsDetailFragment.newInstance(id)).commit();
        }
    }

    private void handleBookmark(String serverId, Integer bookmarked) {
        int newBookmarked = 0;
        if (bookmarked == 0) {
            newBookmarked = 1;
        } else if (bookmarked == 1) {
            newBookmarked = 0;
        }
        FireBaseHelper.firebaseBookmark(this, serverId, newBookmarked);
    }

    private void handleLike(String serverId, Integer liked) {
        int newLike = 0;
        if (liked == 0) {
            newLike = 1;
        } else if (liked == 1) {
            newLike = 0;
        }
        FireBaseHelper.firebaseLike(this, serverId, newLike);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEvent();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterEvent();
    }
}
