package com.qurux.coffeevizbeer.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.bean.Post;
import com.qurux.coffeevizbeer.local.CvBContract;
import com.qurux.coffeevizbeer.widget.PostsWidget;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Aman Kapoor on 19-11-2016.
 */

public class FireBaseHelper {

    public static void addPostToServer(Post post, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Post");
        String postKey = postRef.push().getKey();
        postRef.child(postKey).setValue(post, completionListener);
    }

    public static void likePostOnServer(String key, String userId, boolean state, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Post").child(key).child("likes");
        Map<String, Object> like = new LinkedHashMap<>();
        like.put(userId, state);
        postRef.updateChildren(like, completionListener);
    }

    public static void bookmarkPostOnServer(String key, String userId, boolean state, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("bookmarked");
        Map<String, Object> like = new LinkedHashMap<>();
        like.put(key, state);
        postRef.updateChildren(like, completionListener);
    }

    public static void addPosttoUserOnServer(String key, String userId, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("posts");
        Map<String, Object> post = new LinkedHashMap<>();
        post.put(key, true);
        postRef.updateChildren(post, completionListener);
    }

    @NonNull
    public static UploadTask storeFileToServer(Context context, File file, FirebaseUser user, String date) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl(context.getString(R.string.firebase_storage_link));
        String fileName = user.getDisplayName() + date + file.getName();
        StorageReference imagesRef = storageRef.child("post_images").child(fileName);
        Uri fileUri = Uri.fromFile(file);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        return imagesRef.putFile(fileUri, metadata);
    }

    public static void setNewPostListener(Context context) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Post");
        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addSinglePostToDb(dataSnapshot, context);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateSinglePostToDb(dataSnapshot, context);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeSinglePostFromDb(dataSnapshot, context);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                Log.d("aman-moved", post.getTitle());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postRef.addChildEventListener(childEventListener);
    }

    private static void removeSinglePostFromDb(DataSnapshot dataSnapshot, Context context) {
        Post post = dataSnapshot.getValue(Post.class);
        context.getContentResolver().delete(CvBContract.PostsEntry.CONTENT_URI, CvBContract.PostsEntry.COLUMN_SERVER_ID + "=?",
                new String[]{dataSnapshot.getKey()});
        Log.d("aman-removed", post.getTitle());
        PostsWidget.sendRefreshBroadcast(context);
    }

    private static void updateSinglePostToDb(DataSnapshot dataSnapshot, Context context) {
        Post post = dataSnapshot.getValue(Post.class);
        ContentValues cv = createContentValue(dataSnapshot.getKey(), post);
        context.getContentResolver().update(CvBContract.PostsEntry.CONTENT_URI, cv, CvBContract.PostsEntry.COLUMN_SERVER_ID + "=?",
                new String[]{dataSnapshot.getKey()});
        Log.d("aman-changed", post.getTitle());
        PostsWidget.sendRefreshBroadcast(context);
    }

    private static void addSinglePostToDb(DataSnapshot dataSnapshot, Context context) {
        Map<String, Post> posts = new LinkedHashMap<>();
        addRowsToMap(context, posts);
        Post post = dataSnapshot.getValue(Post.class);
        if (!posts.containsKey(dataSnapshot.getKey())) {
            ContentValues cv = createContentValue(dataSnapshot.getKey(), post);
            context.getContentResolver().insert(CvBContract.PostsEntry.CONTENT_URI, cv);
            PostsWidget.sendRefreshBroadcast(context);
            posts.put(dataSnapshot.getKey(), post);
        } else {
            PostsWidget.sendRefreshBroadcast(context);
            Log.d("aman", "onChildAdded: Already in LinkedHashMap");
        }
    }

    private static void addRowsToMap(@NonNull Context context, Map<String, Post> posts) {
        Cursor already = context.getContentResolver().query(CvBContract.PostsEntry.CONTENT_URI,
                new String[]{CvBContract.PostsEntry.COLUMN_SERVER_ID},
                null, null, null);
        CvBUtil.log("starting local db old posts check");
        if (already == null) {
            CvBUtil.log("old posts null");
            return;
        }
        try {
            while (already.moveToNext()) {
                CvBUtil.log("old post id:" + already.getString(0));
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

    public static void firebaseLike(Context context, String serverId, int state) {
        FireBaseHelper.likePostOnServer(serverId, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                intToBoolean(state),
                (databaseError, databaseReference) -> {
                    ContentValues cv = new ContentValues(1);
                    if (databaseError != null) {
                        return;
                    }
                    cv.put(CvBContract.PostsEntry.COLUMN_LIKED, state);
                    context.getContentResolver().update(CvBContract.PostsEntry.CONTENT_URI, cv,
                            CvBContract.PostsEntry.COLUMN_SERVER_ID + "=?",
                            new String[]{serverId});
                });
    }

    public static void firebaseBookmark(Context context, String serverId, int state) {
        FireBaseHelper.bookmarkPostOnServer(serverId, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                intToBoolean(state),
                (databaseError, databaseReference) -> {
                    ContentValues cv = new ContentValues(1);
                    if (databaseError != null) {
                        return;
                    }
                    cv.put(CvBContract.PostsEntry.COLUMN_BOOKMARKED, state);
                    context.getContentResolver().update(CvBContract.PostsEntry.CONTENT_URI, cv,
                            CvBContract.PostsEntry.COLUMN_SERVER_ID + "=?",
                            new String[]{serverId});
                });
    }

    private static boolean intToBoolean(int afterClick) {
        return afterClick != 0;
    }
}
