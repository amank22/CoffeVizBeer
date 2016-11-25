package com.qurux.coffeevizbeer.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPostActivity extends BaseActivity {

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private static final int MY_REQUEST_CODE = 4445;
    private static final int MY_REQUEST_CODE_STORAGE = 4444;
    TextView titleThis, titleThat, author, remove, summary;
    TextView addDecp;
    ThisThatView thisThatView;
    Button submit;
    Bitmap bitmap;
    ImageChooserDialog dialog;
    private Intent pictureActionIntent = null;
    private String selectedImagePath;
    private int position = ImageChooserDialog.OPTION_INVALID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hides the soft keyboard on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        author.setText(user);
        SpannableString underlineRemove = new SpannableString(remove.getText());
        underlineRemove.setSpan(new UnderlineSpan(), 0, remove.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        remove.setText(underlineRemove);
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
        checksStoragePermission();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_post;
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
        pictureActionIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        selectedImagePath = image.getAbsolutePath();
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

        bitmap = null;

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode != RESULT_OK) {
                File f = new File(selectedImagePath);
                Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_LONG).show();
                f.delete();
            } else {
                File f = new File(selectedImagePath);
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
                selectedImagePath = c.getString(columnIndex);
                c.close();
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == RESULT_OK && selectedImagePath != null) {
            dialog.dismiss();
            Log.d("aman", "onActivityResult: " + selectedImagePath);
            File file = new File(selectedImagePath);
            Log.d("aman", "onActivityResult:File=" + file.getName());
            thisThatView.setImage(position, "file://" + selectedImagePath);
        }
    }

    public void checksStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MyApp", "SDK >= 23");
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_STORAGE);

                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showMessageOKCancel(getString(R.string.allow_storage),
                            (dialog, which) -> ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_REQUEST_CODE_STORAGE));
                }
            }
        } else {
            Log.d("MyApp", "Android < 6.0");
        }
    }


    public void checksCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MyApp", "SDK >= 23");
            if (this.checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_REQUEST_CODE);

                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showMessageOKCancel(getString(R.string.allow_camera),
                            (dialog, which) -> ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.CAMERA},
                                    MY_REQUEST_CODE));
                }
            } else {
                dispatchTakePictureIntent();
            }
        } else {
            Log.d("MyApp", "Android < 6.0");
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
