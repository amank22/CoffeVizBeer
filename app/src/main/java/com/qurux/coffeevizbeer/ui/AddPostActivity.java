package com.qurux.coffeevizbeer.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.helper.ThisThatView;

public class AddPostActivity extends BaseActivity {

    TextView titleThis, titleThat, author, remove, summary;
    TextView addDecp;
    ThisThatView thisThatView;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hides the soft keyboard on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        String user=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        author.setText(user);

    }

    private void initViews() {
        titleThis = (TextView) findViewById(R.id.post_item_title_this);
        titleThat = (TextView) findViewById(R.id.post_item_title_that);
        author = (TextView) findViewById(R.id.post_item_author);
        remove = (TextView) findViewById(R.id.post_item_remove_author);
        summary = (TextView) findViewById(R.id.post_item_summary);
        addDecp = (TextView) findViewById(R.id.post_item_read_more);
        thisThatView = (ThisThatView) findViewById(R.id.this_that_view_item);
        submit = (Button) findViewById(R.id.button_submit_post);
        String path = "res:/" + R.drawable.ic_add_plus;
        try {
            thisThatView.setImageToAllImages(new String[]{path,path});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_post;
    }
}
