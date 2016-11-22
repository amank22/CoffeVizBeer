package com.qurux.coffeevizbeer.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.ui.fragments.PostsDetailFragment;

public class DetailActivity extends BaseActivity {

    public static String KEY_ID = "key_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int _id = getIntent().getIntExtra(KEY_ID, -1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (_id != -1)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_details, PostsDetailFragment.newInstance(_id)).commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_detail;
    }
}
