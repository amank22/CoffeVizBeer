package com.qurux.coffeevizbeer.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.app.CvBApp;
import com.qurux.coffeevizbeer.events.ErrorEvent;
import com.qurux.coffeevizbeer.ui.fragments.TabsLayoutFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends BaseActivity {

    //TODO: Handle cases for tablets also.

    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CvBApp.getInstance().setFirebaseListeners();
//        Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
//        startActivity(dbmanager);
        errorText = (TextView) findViewById(R.id.error_text);
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container_posts, new TabsLayoutFragment()).commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ErrorEvent event) {
        event.setError(this, event.getError(), errorText);
    }
}
