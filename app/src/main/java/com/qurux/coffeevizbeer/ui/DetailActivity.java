package com.qurux.coffeevizbeer.ui;

import android.os.Bundle;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.ui.fragments.PostsDetailFragment;

public class DetailActivity extends BaseActivity {

    public static String KEY_ID = "key_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int _id = getIntent().getIntExtra(KEY_ID, -1);
        if (_id != -1) {
            PostsDetailFragment frag = (PostsDetailFragment) getSupportFragmentManager().findFragmentByTag("Detail_frag");
            if (frag == null) {
                frag = PostsDetailFragment.newInstance(_id);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_details, frag, "Detail_frag").commit();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_detail;
    }
}
