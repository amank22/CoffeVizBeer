package com.qurux.coffeevizbeer.ui;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.bean.Post;
import com.qurux.coffeevizbeer.helper.FireBaseHelper;
import com.qurux.coffeevizbeer.ui.fragments.TabsLayoutFragment;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends BaseActivity {

    //TODO: Handle cases for tablets also.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container_posts, new TabsLayoutFragment()).commit();
//
//        addNewPost();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    private void addNewPost() {
        Map<String, Boolean> likes = new HashMap<>();
        likes.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
        Post post = new Post("Trial viz Production", "local/avatar1", "local/avatar2", "Short summary", "long description", "Aman Kapoor",
                FirebaseAuth.getInstance().getCurrentUser().getUid(), "15-08-16 02:45:46", likes, "#363636");
        FireBaseHelper.addPostToServer(post);
    }
}
