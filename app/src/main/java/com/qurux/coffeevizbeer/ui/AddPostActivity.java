package com.qurux.coffeevizbeer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.dialog.ImageChooserDialog;
import com.qurux.coffeevizbeer.events.ImageChooserEvent;
import com.qurux.coffeevizbeer.events.UploadFailEvent;
import com.qurux.coffeevizbeer.events.UploadSuccessEvent;
import com.qurux.coffeevizbeer.helper.CvBUtil;
import com.qurux.coffeevizbeer.helper.UploadDataHelper;
import com.qurux.coffeevizbeer.views.ThisThatView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import uz.shift.colorpicker.LineColorPicker;

public class AddPostActivity extends BaseActivity {

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private static final int MY_REQUEST_CODE = 4445;
    private static final int MY_REQUEST_CODE_STORAGE = 4444;
    private static final String CONFIG_KEY_IMG = "config_change_images";
    private static final String CONFIG_KEY_COLOR = "config_change_color";
    private static final String CONFIG_KEY_ANONYMOUS = "config_change_anonym";
    RelativeLayout titleLayout;
    private EditText titleThis, titleThat, summary, desc;
    private ImageButton remove;
    private TextView author;
    private ThisThatView thisThatView;
    private Button submit, addDecp;
    private ImageChooserDialog dialog;
    private String selectedImagePath[] = new String[2];
    private int position = ImageChooserDialog.OPTION_INVALID;
    private boolean isAnonymous = false;
    private LineColorPicker colorPicker;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hides the soft keyboard on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        if (savedInstanceState != null) {
            selectedImagePath = savedInstanceState.getStringArray(CONFIG_KEY_IMG);
            currentColor = savedInstanceState.getInt(CONFIG_KEY_COLOR, ContextCompat.getColor(this, R.color.colorPrimary));
            isAnonymous = savedInstanceState.getBoolean(CONFIG_KEY_ANONYMOUS, false);
            for (int i = 0; i < (selectedImagePath != null ? selectedImagePath.length : 0); i++) {
                if (selectedImagePath[i] != null) {
                    thisThatView.setImage(i, "file://" + selectedImagePath[i]);
                }
            }
        } else {
            currentColor = ContextCompat.getColor(this, R.color.colorPrimary);
        }
        setBackgroundColor();
        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        assert user != null;
        author.setText(user);
        SpannableString strikeThrough = new SpannableString(user);
        strikeThrough.setSpan(new StrikethroughSpan(), 0, user.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        checkAndSetUser(user, strikeThrough);
        setClickListeners(user, strikeThrough);
        colorPicker.setOnColorChangedListener(i -> {
            currentColor = i;
            setBackgroundColor();
        });
        checksStoragePermission();
    }

    private void setClickListeners(String user, SpannableString strikeThrough) {
        remove.setOnClickListener(view -> {
            isAnonymous = !isAnonymous;
            checkAndSetUser(user, strikeThrough);
        });
        thisThatView.setClickListenerForThisThat(new ThisThatView.OnClickListenerForThisThat() {
            @Override
            public void onThisClicked() {
                callDialog(ImageChooserDialog.OPTION_THIS);
            }

            @Override
            public void onThatClicked() {
                callDialog(ImageChooserDialog.OPTION_THAT);
            }
        });
        addDecp.setOnClickListener(view -> desc.setVisibility(View.VISIBLE));
        submit.setOnClickListener(view -> submitData());
    }

    private void submitData() {
//        if (!verifyData()) {
//            return;
//        }
        Bundle bundle = new Bundle();
        String title = titleThis.getText() + " viz " + titleThat.getText();
        bundle.putString(UploadDataHelper.KEY_TITLE, title);
        bundle.putString(UploadDataHelper.KEY_SUMMARY, summary.getText().toString());
        bundle.putString(UploadDataHelper.KEY_DESCRIPTION, desc.getText().toString());
        bundle.putString(UploadDataHelper.KEY_COLOR, "#" + Integer.toHexString(currentColor));
        bundle.putBoolean(UploadDataHelper.KEY_ANONYMOUS, isAnonymous);
        bundle.putStringArray(UploadDataHelper.KEY_LINKS, selectedImagePath);
        Intent service = new Intent(this, UploadDataHelper.class);
        service.putExtras(bundle);
        UploadDataHelper.handleUpdateIntent(this, service);
    }

    private boolean verifyData() {
        return false;
    }

    private void checkAndSetUser(String user, SpannableString strikeThrough) {
        if (!isAnonymous) {
            remove.setImageResource(R.drawable.ic_vector_remove_user);
            author.setText(user);
        } else {
            remove.setImageResource(R.drawable.ic_vector_add_user);
            author.setText(strikeThrough);
        }
    }

    private void setBackgroundColor() {
        thisThatView.setBackgroundColor(currentColor);
        titleLayout.setBackgroundColor(currentColor);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(CONFIG_KEY_IMG, selectedImagePath);
        outState.putInt(CONFIG_KEY_COLOR, currentColor);
        outState.putBoolean(CONFIG_KEY_ANONYMOUS, isAnonymous);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_post;
    }

    private void initViews() {
        titleThis = (EditText) findViewById(R.id.post_item_title_this);
        titleThat = (EditText) findViewById(R.id.post_item_title_that);
        titleLayout = (RelativeLayout) findViewById(R.id.title_layout_add);
        author = (TextView) findViewById(R.id.post_item_author);
        desc = (EditText) findViewById(R.id.post_editText_description);
        remove = (ImageButton) findViewById(R.id.post_item_remove_author);
        summary = (EditText) findViewById(R.id.post_item_summary);
        addDecp = (Button) findViewById(R.id.post_item_read_more);
        thisThatView = (ThisThatView) findViewById(R.id.this_that_view_item);
        colorPicker = (LineColorPicker) findViewById(R.id.picker);
        submit = (Button) findViewById(R.id.button_submit_post);
        for (int i = 0; i < thisThatView.getHolder().size(); i++) {
            thisThatView.getHolder().get(i).getHierarchy().setPlaceholderImage(null);
        }
        String path = "res:/" + R.drawable.ic_add_plus;
        try {
            thisThatView.setImageToAll(new String[]{path, path});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callDialog(int position) {
        this.position = position;
        FragmentManager fm = getSupportFragmentManager();
        dialog = ImageChooserDialog.getInstance(position);
        dialog.show(fm, "fragment_image_chooser-" + position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageChooserEvent event) {
        switch (event.getSelectedOption()) {
            case ImageChooserEvent.OPTION_CAMERA:
                Log.d("ImageDialog", "Camera Clicked");
                //TODO:Open camera intent
                checksCameraPermission();
                break;
            case ImageChooserEvent.OPTION_GALLERY:
                Log.d("ImageDialog", "Gallery Clicked");
                //TODO:Open gallery intent
                openGalleryIntent();
                break;
            case ImageChooserEvent.OPTION_AVATAR:
                Log.d("ImageDialog", "Avatar Clicked");
                //TODO:Create an bottomsheet to show all avatars
                break;
        }

    }

    private void openGalleryIntent() {
        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        selectedImagePath[position] = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.qurux.coffeevizbeer.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode != RESULT_OK) {
                File f = new File(selectedImagePath[position]);
                Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_LONG).show();
                f.delete();
            } else {
                File f = new File(selectedImagePath[position]);
                Log.d("aman", "onActivityResult: " + f.getTotalSpace() + ":" + f.getUsableSpace());
            }
        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                if (c == null) {
                    return;
                }
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath[position] = c.getString(columnIndex);
                c.close();
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == RESULT_OK && selectedImagePath[position] != null) {
            dialog.dismiss();
            thisThatView.setImage(position, "file://" + selectedImagePath[position]);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadFailEvent event) {
        //TODO: alert and restart the upload
        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadSuccessEvent event) {
        Intent i = new Intent(this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void checksStoragePermission() {
        CvBUtil.checkPermissionRuntime(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                getString(R.string.allow_storage), MY_REQUEST_CODE_STORAGE);
        CvBUtil.checkPermissionRuntime(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getString(R.string.allow_storage), MY_REQUEST_CODE_STORAGE);
    }


    public void checksCameraPermission() {
        CvBUtil.checkPermissionRuntime(this, Manifest.permission.CAMERA, getString(R.string.allow_camera), MY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, R.string.camera_perm_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
