package com.qurux.coffeevizbeer.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.qurux.coffeevizbeer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Aman Kapoor on 25-11-2016.
 */

public class CvBUtil {


    public static void log(String msg) {
        Log.d("Aman", msg);
    }

    /**
     * Checks the runtime permission introduced in Android M and if not given asks for it.
     *
     * @param activity    Associated Activity for requesting permission.
     *                    Activity handles the {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
     * @param permission  The permission string to grant access to.
     * @param message     Message for the dialog for asking permission
     * @param requestCode Request code for permission granting and referencing.
     */
    public static void checkPermissionRuntime(Activity activity, String permission, String message, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {

                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    showMessageOKCancel(activity, message,
                            (dialog, which) -> ActivityCompat.requestPermissions(activity, new String[]{permission},
                                    requestCode));
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{permission},
                            requestCode);
                }
            }
        } else {
            Log.d("MyApp", "Android < 6.0");
        }
    }


    private static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * Compress the file and return the compressed photo
     *
     * @return photoFile
     */
    public static File PhotoCompressor(File photoFile) {
        Bitmap b = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

        int originalWidth = b.getWidth();
        int originalHeight = b.getHeight();
        int boundWidth = 400;
        int boundHeight = 400;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        //check if the image needs to be scale width
        if (originalWidth > boundWidth) {
            //scale width to fit
            newWidth = boundWidth;
            //scale height to maintain aspect ratio
            newHeight = (newWidth * originalHeight) / originalWidth;
        }

        //now check if we need to scale even the new height
        if (newHeight > boundHeight) {
            //scale height to fit instead
            newHeight = boundHeight;
            //scale width to maintain aspect ratio
            newWidth = (newHeight * originalWidth) / originalHeight;
        }
        log("Original Image:" + originalHeight + " x" + originalWidth);
        log("New Image:" + newHeight + " x" + newWidth);
        try {
            Bitmap out = Bitmap.createScaledBitmap(b, newWidth, newHeight, true);
            out = straightenFile(out, photoFile.getAbsolutePath());
            FileOutputStream fOut;
            fOut = new FileOutputStream(photoFile);
            out.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (OutOfMemoryError exception) {
            log("OutofMemory excpetion" + exception);
            exception.printStackTrace();
        } catch (FileNotFoundException e) {
            log("File not found excpetion" + e);
            e.printStackTrace();
        } catch (IOException e) {
            log("IO exception excpetion" + e);
            e.printStackTrace();
        }
        return photoFile;
    }

    public static Bitmap straightenFile(Bitmap bitmap, String absPath) throws IOException {
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(absPath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    public static boolean checkLocalRegex(Context context, String link) {
        return link.matches(context.getString(R.string.regex_local));
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static File createDestinationFile(Context context) throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }


    /**
     * Get Avatar resource id at position
     *
     * @param position position at which avatar is(starts with 1)
     * @return Resource id
     */
    public static int getAvatarResId(int position) {
        switch (position) {
            case 1:
                return R.drawable.ic_avatar1;
            case 2:
                return R.drawable.ic_avatar2;
            case 3:
                return R.drawable.ic_avatar3;
            case 4:
                return R.drawable.ic_avatar4;
            case 5:
                return R.drawable.ic_avatar5;
            case 6:
                return R.drawable.ic_avatar6;
            case 7:
                return R.drawable.ic_avatar7;
            case 8:
                return R.drawable.ic_avatar8;
            case 9:
                return R.drawable.ic_avatar9;
            case 10:
                return R.drawable.ic_avatar10;
            case 11:
                return R.drawable.ic_avatar11;
            case 12:
                return R.drawable.ic_avatar12;
            case 13:
                return R.drawable.ic_avatar13;
            case 14:
                return R.drawable.ic_avatar14;
            case 15:
                return R.drawable.ic_avatar15;
            case 16:
                return R.drawable.ic_avatar16;
            case 17:
                return R.drawable.ic_avatar17;
            default:
                return -1;
        }
    }
}
