package com.qurux.coffeevizbeer.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.qurux.coffeevizbeer.bean.Post;
import com.qurux.coffeevizbeer.local.CvBContract;
import com.qurux.coffeevizbeer.widget.PostsWidget;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Aman Kapoor on 07-12-2016.
 */

class DbOpsAsync extends AsyncTask<DataSnapshot, Void, Void> {

    static final int INSERT = 1;
    static final int UPDATE = 2;
    static final int REMOVE = 3;

    private Context context;
    private int type;

    DbOpsAsync(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    private static void addRowsToMap(@NonNull Context context, Map<String, Post> posts) {
        Cursor already = context.getContentResolver().query(CvBContract.PostsEntry.CONTENT_URI,
                new String[]{CvBContract.PostsEntry.COLUMN_SERVER_ID},
                null, null, null);
        if (already == null) {
            return;
        }
        try {
            while (already.moveToNext()) {
                posts.put(already.getString(0), new Post());
            }
        } finally {
            already.close();
        }
    }

    private static ContentValues createContentValue(String key, Post value) {
        ContentValues cv = new ContentValues();
        cv.put(CvBContract.PostsEntry.COLUMN_SERVER_ID, key);
        cv.put(CvBContract.PostsEntry.COLUMN_TITLE, value.getTitle());
        cv.put(CvBContract.PostsEntry.COLUMN_SUMMARY, value.getSummary());
        cv.put(CvBContract.PostsEntry.COLUMN_DESCRIPTION, value.getDescription());
        cv.put(CvBContract.PostsEntry.COLUMN_LINK_THIS, value.getLinkThis());
        cv.put(CvBContract.PostsEntry.COLUMN_LINK_THAT, value.getLinkThat());
        cv.put(CvBContract.PostsEntry.COLUMN_AUTHOR, value.getAuthor());
        cv.put(CvBContract.PostsEntry.COLUMN_AUTHOR_ID, value.getAuthorId());
        cv.put(CvBContract.PostsEntry.COLUMN_DATE, value.getDate());
        cv.put(CvBContract.PostsEntry.COLUMN_COLOR, value.getColor());
        return cv;
    }

    @Override
    protected Void doInBackground(DataSnapshot... dataSnapshots) {
        Post post = dataSnapshots[0].getValue(Post.class);
        switch (type) {
            case REMOVE:
                context.getContentResolver().delete(CvBContract.PostsEntry.CONTENT_URI, CvBContract.PostsEntry.COLUMN_SERVER_ID + "=?",
                        new String[]{dataSnapshots[0].getKey()});
                break;
            case INSERT:
                Map<String, Post> posts = new LinkedHashMap<>();
                addRowsToMap(context, posts);
                if (!posts.containsKey(dataSnapshots[0].getKey())) {
                    ContentValues cv = createContentValue(dataSnapshots[0].getKey(), post);
                    context.getContentResolver().insert(CvBContract.PostsEntry.CONTENT_URI, cv);
                }
                break;
            case UPDATE:
                ContentValues cv = createContentValue(dataSnapshots[0].getKey(), post);
                context.getContentResolver().update(CvBContract.PostsEntry.CONTENT_URI, cv,
                        CvBContract.PostsEntry.COLUMN_SERVER_ID + "=?", new String[]{dataSnapshots[0].getKey()});
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        PostsWidget.sendRefreshBroadcast(context);
    }
}
