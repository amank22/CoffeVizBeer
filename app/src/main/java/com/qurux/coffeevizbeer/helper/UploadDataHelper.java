package com.qurux.coffeevizbeer.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.app.CvBApp;
import com.qurux.coffeevizbeer.bean.Post;
import com.qurux.coffeevizbeer.events.UploadFailEvent;
import com.qurux.coffeevizbeer.events.UploadSuccessEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Aman Kapoor on 26-11-2016.
 */

public class UploadDataHelper {

    public static final String KEY_LINKS = "key_array_links";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_COLOR = "key_color";
    public static final String KEY_ANONYMOUS = "key_is_anonymous";
    public static final String KEY_SUMMARY = "key_summary";
    public static final String KEY_DESCRIPTION = "key_description";

    public static void handleUpdateIntent(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String title = extras.getString(KEY_TITLE);
        String[] links = extras.getStringArray(KEY_LINKS);
        String summary = extras.getString(KEY_SUMMARY);
        String desc = extras.getString(KEY_DESCRIPTION);
        String color = extras.getString(KEY_COLOR);
        Boolean isAnonymous = extras.getBoolean(KEY_ANONYMOUS);
        DateFormat df = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss", Locale.getDefault());
        String date = df.format(Calendar.getInstance().getTime());
        String author = "Anonymous";
        String authorId = "Anonymous";
        if (!isAnonymous) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                author = user.getDisplayName();
                authorId = user.getUid();
            }
        }
        CvBUtil.log("OnHandleIntent");
        if (links != null)
            storeImagesOnServer(context, links[0], links[1], title, summary, desc, author, authorId, date, color);
    }

    private static void storeImagesOnServer(Context context, String thisLink, String thatLink, String title, String summary,
                                            String longDesc, String author, String authorId, String dateTime, String color) {
        String[] serverLinks = new String[2];
        if (checkLocalRegex(context, thisLink)) {
            serverLinks[0] = thisLink;
            if (checkLocalRegex(context, thatLink)) {
                serverLinks[1] = thatLink;
                addNewPost(title, serverLinks[0], serverLinks[1],
                        summary, longDesc, author, authorId, dateTime, color);
            } else {
                getUploadTask(serverLinks, context, thatLink, 1, null, -1, title, summary, longDesc, author, authorId, dateTime, color);
            }
        } else {
            getUploadTask(serverLinks, context, thisLink, 0, thatLink, 1, title, summary, longDesc, author, authorId, dateTime, color);
        }
    }

    private static void getUploadTask(String[] serverLinks, Context context, String Link, int position, String nextLink, int nextPos, String title, String summary,
                                      String longDesc, String author, String authorId, String dateTime, String color) {
        UploadTask task = FireBaseHelper.
                storeFileToServer(context, new File(Link), FirebaseAuth.getInstance().getCurrentUser(), dateTime);
        task.addOnFailureListener(e -> {
            Uri failUri = task.getSnapshot().getUploadSessionUri();
            EventBus.getDefault().post(new UploadFailEvent(failUri, position));
            CvBUtil.log("Task:" + position + ": failed:" + e.getMessage());
        }).addOnSuccessListener(taskSnapshot -> {
            serverLinks[position] = task.getResult().getDownloadUrl().toString();
            CvBUtil.log("Task:" + position + ": success:" + serverLinks[position]);
            if (nextLink != null) {
                if (!checkLocalRegex(context, nextLink))
                    getUploadTask(serverLinks, context, nextLink, nextPos, null, -1, title, summary, longDesc, author, authorId, dateTime, color);
                else {
                    serverLinks[nextPos] = nextLink;
                    addNewPost(title, serverLinks[0], serverLinks[1],
                            summary, longDesc, author, authorId, dateTime, color);
                }
            } else {
                addNewPost(title, serverLinks[0], serverLinks[1],
                        summary, longDesc, author, authorId, dateTime, color);
            }
        }).addOnCompleteListener(task1 -> CvBUtil.log("Task:" + position + " Completed" + task1.isSuccessful()));
    }

    private static boolean checkLocalRegex(Context context, String link) {
        return link.matches(context.getString(R.string.regex_local));
    }

    private static void addNewPost(String title, String linkThis, String linkThat, String summary,
                                   String longDesc, String author, String authorId, String dateTime, String color) {
        EventBus.getDefault().post(new UploadSuccessEvent());
        Post post = new Post(title, linkThis, linkThat, summary, longDesc, author, authorId, dateTime, color);
        FireBaseHelper.addPostToServer(post, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                String key = databaseReference.getKey();
                updateUserOnServer(key);
            }
        });
    }

    private static void updateUserOnServer(String key) {
        FireBaseHelper.addPosttoUserOnServer(key, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                (databaseError, databaseReference) -> {
                    if (databaseError == null && CvBApp.getInstance().getUserExtra() != null)
                        CvBApp.getInstance().getUserExtra().getPosts().put(key, true);
                });
    }
}
