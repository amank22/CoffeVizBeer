package com.qurux.coffeevizbeer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.bean.User;

import me.grantland.widget.AutofitTextView;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Aman";
    private SignInButton mSignInButton;
    private TextView errorText;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spanToTitle();
        // Assign fields
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        errorText = (TextView) findViewById(R.id.login_error_text);
        loader = (ProgressBar) findViewById(R.id.login_loader);

        // Set click listeners
        mSignInButton.setOnClickListener(this);
    }

    private void spanToTitle() {
        AutofitTextView title = (AutofitTextView) findViewById(R.id.text_login_title);
        SpannableString span = getSpanForTitle();
        title.setText(span, TextView.BufferType.NORMAL);
    }

    @NonNull
    private SpannableString getSpanForTitle() {
        SpannableString span = new SpannableString(getString(R.string.app_name));
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary))
                , 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorTextGrey))
                , 7, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent))
                , 11, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    private void handleFirebaseAuthResult(AuthResult authResult) {
        if (authResult != null) {
            // Welcome the user
            FirebaseUser user = authResult.getUser();
            checkLoginStatus(user);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                errorText.setText(null);
                loader.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(getmGoogleApiClient(this));
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                loader.setVisibility(View.INVISIBLE);
                errorText.setText(R.string.error_google_signin);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    loader.setVisibility(View.INVISIBLE);
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        errorText.setText(R.string.error_firebase_failed);
                    } else {
                        addDummyUser(task.getResult().getUser().getUid());
                        handleFirebaseAuthResult(task.getResult());
                    }
                });
    }

    private void addDummyUser(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        userRef.child(uid).setValue(new User());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        loader.setVisibility(View.INVISIBLE);
        errorText.setText(R.string.error_play_service);
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
