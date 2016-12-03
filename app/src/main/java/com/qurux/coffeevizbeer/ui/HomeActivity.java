package com.qurux.coffeevizbeer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.app.CvBApp;
import com.qurux.coffeevizbeer.events.ReadMoreEvent;
import com.qurux.coffeevizbeer.ui.fragments.PostsDetailFragment;
import com.qurux.coffeevizbeer.ui.fragments.TabsLayoutFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends BaseActivity {

    private static final String tag = "tabLayout";
    //TODO: Handle cases for tablets also.
    boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CvBApp.getInstance().setFirebaseListeners();
//        Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
//        startActivity(dbmanager);
        FrameLayout detailContainer = (FrameLayout) findViewById(R.id.container_details);
        isTablet = detailContainer != null;
        final Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragmentByTag == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_container_posts, new TabsLayoutFragment(), tag).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_container_posts, fragmentByTag, tag).commit();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReadMoreEvent event) {
        int id = event.getCursorId();
        if (!isTablet) {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(DetailActivity.KEY_ID, id);
            startActivity(i);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_details, PostsDetailFragment.newInstance(id)).commit();
        }
    }

}
