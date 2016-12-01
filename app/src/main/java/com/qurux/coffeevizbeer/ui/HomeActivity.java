package com.qurux.coffeevizbeer.ui;

import android.os.Bundle;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.app.CvBApp;
import com.qurux.coffeevizbeer.ui.fragments.TabsLayoutFragment;

public class HomeActivity extends BaseActivity {

    //TODO: Handle cases for tablets also.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CvBApp.getInstance().setFirebaseListeners();
//        Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
//        startActivity(dbmanager);
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container_posts, new TabsLayoutFragment()).commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }
}
