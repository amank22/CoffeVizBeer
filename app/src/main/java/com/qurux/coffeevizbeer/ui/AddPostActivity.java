package com.qurux.coffeevizbeer.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.dialog.ImageChooserDialog;
import com.qurux.coffeevizbeer.events.ImageChooserEvent;
import com.qurux.coffeevizbeer.helper.ThisThatView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        author.setText(user);

        thisThatView.setClickListenerForThisThat(new ThisThatView.OnClickListenerForThisThat() {
            @Override
            public void onThisClicked() {
                callDialog(thisThatView.getDrawableAtPosition(0));
                Toast.makeText(AddPostActivity.this, "This clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onThatClicked() {
                callDialog(thisThatView.getDrawableAtPosition(1));
                Toast.makeText(AddPostActivity.this, "That clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callDialog(Drawable drawable) {
        FragmentManager fm = getSupportFragmentManager();
        ImageChooserDialog dialog = new ImageChooserDialog();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);
        dialog.show(fm, "fragment_image_chooser");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageChooserEvent event) {
        if (event.isImageClicked()) {
            Log.d("ImageDialog", "Image Clicked");
            //TODO:Open gallery or camera intent

        } else if (event.isAvatarClicked()) {
            Log.d("ImageDialog", "Avatar Clicked");
            //TODO:Create an bottomsheet to show all avatars
        }

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
            thisThatView.setImageToAllImages(new String[]{path, path});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_post;
    }
}
