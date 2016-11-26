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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Aman Kapoor on 25-11-2016.
 */

public class CvBUtil {

    public static void log(String msg) {
        Log.d("Aman", msg);
    }

    public static void checkPermissionRuntime(Activity activity, String permission, String message, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        requestCode);

                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    showMessageOKCancel(activity, message,
                            (dialog, which) -> ActivityCompat.requestPermissions(activity, new String[]{permission},
                                    requestCode));
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
}
