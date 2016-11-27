package com.qurux.coffeevizbeer.app;

import android.app.Application;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.bean.User;
import com.qurux.coffeevizbeer.helper.CvBUtil;
import com.qurux.coffeevizbeer.helper.FireBaseHelper;

import java.util.HashSet;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Aman Kapoor on 04-11-2016.
 */

public class CvBApp extends Application {

    private static CvBApp sInstance;
    private User userExtra;

    public CvBApp() {
    }

    public static CvBApp getInstance() {
        if (sInstance == null) {
            sInstance = new CvBApp();
        }
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                // other setters
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(this, config);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/CharisSILR.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setFirebaseListeners();
        sInstance = this;
    }

    public void setFirebaseListeners() {
        CvBUtil.log("starting firebase listeners");
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            CvBUtil.log("started with firebase listeners");
            getCurrentExtraUser();
            setListenerForNewPosts();
        }
    }

    public User getUserExtra() {
        return userExtra;
    }

    private void getCurrentExtraUser() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        DatabaseReference cUserRef = userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        cUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userExtra = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setListenerForNewPosts() {
        CvBUtil.log("starting firebase Posts Listeners");
        FireBaseHelper.setNewPostListener(sInstance);
    }
}