package com.qurux.coffeevizbeer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.qurux.coffeevizbeer.R;

import org.greenrobot.eventbus.EventBus;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Aman Kapoor on 04-11-2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected FirebaseAuth mFirebaseAuth;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        checkLoginStatus(mFirebaseAuth.getCurrentUser());
        setContentView(getLayoutResource());
    }

    protected void initGoogleClient(GoogleApiClient.OnConnectionFailedListener listener) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, listener /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public GoogleApiClient getmGoogleApiClient(GoogleApiClient.OnConnectionFailedListener failedListener) {
        if (mGoogleApiClient == null) {
            initGoogleClient(failedListener);
        }
        return mGoogleApiClient;
    }

    protected void checkLoginStatus(FirebaseUser user) {
        if (user != null) {
            // Go back to the main activity
            if (this instanceof LoginActivity) {
                Intent i = new Intent(this, HomeActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        } else {
            if (!(this instanceof LoginActivity)) {
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus(mFirebaseAuth.getCurrentUser());
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    protected abstract int getLayoutResource();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
