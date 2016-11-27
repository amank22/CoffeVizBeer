package com.qurux.coffeevizbeer.local;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Aman Kapoor on 17-11-2016.
 */

public class PostsProvider extends ContentProvider {

    static final int POST = 1;
    static final int POST_WITH_ID = 2;
    static final int POST_FOR_SEARCH = 3;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder queryBuilder;
    private static final String sPostByIdSelection =
            CvBContract.PostsEntry.TABLE_NAME +
                    "." + CvBContract.PostsEntry._ID + " = ? ";
    //posts title has the text
    private static final String sPostsBySearchSelection =
            "LOWER(" + CvBContract.PostsEntry.COLUMN_TITLE + ") LIKE ? OR " +
                    "LOWER(" + CvBContract.PostsEntry.TABLE_NAME + "." + CvBContract.PostsEntry.COLUMN_SUMMARY + ") LIKE ? OR " +
                    "LOWER(" + CvBContract.PostsEntry.TABLE_NAME + "." + CvBContract.PostsEntry.COLUMN_DESCRIPTION + ") LIKE ? ";

    static {
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(CvBContract.PostsEntry.TABLE_NAME);
    }

    private PostsDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CvBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CvBContract.PATH_CvB_POSTS, POST);
        matcher.addURI(authority, CvBContract.PATH_CvB_POSTS + "/#", POST_WITH_ID);
        matcher.addURI(authority, CvBContract.PATH_CvB_POSTS + "/*", POST_FOR_SEARCH);
        return matcher;
    }

    private Cursor getAllPosts(String[] projection, String selection, String[] selectionArgs,
                               String sortOrder) {

        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPostsById(
            Uri uri, String[] projection, String sortOrder) {
        String id = uri.getLastPathSegment();
        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sPostByIdSelection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor searchPostsByKey(
            Uri uri, String[] projection, String sortOrder) {
        String key = "%" + uri.getLastPathSegment().toLowerCase() + "%";
        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sPostsBySearchSelection,
                new String[]{key, key, key},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PostsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POST:
                return CvBContract.PostsEntry.CONTENT_TYPE;
            case POST_FOR_SEARCH:
                return CvBContract.PostsEntry.CONTENT_TYPE;
            case POST_WITH_ID:
                return CvBContract.PostsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case POST:
                retCursor = getAllPosts(projection, selection, selectionArgs, sortOrder);
                break;
            case POST_FOR_SEARCH:
                retCursor = searchPostsByKey(uri, projection, sortOrder);
                break;
            case POST_WITH_ID:
                retCursor = getPostsById(uri, projection, sortOrder);
                Log.d("DetailClass", "query: " + uri.toString() + ":" + retCursor.getCount());
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POST:
                long _id = db.insert(CvBContract.PostsEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = CvBContract.PostsEntry.buildPostsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case POST:
                rowsDeleted = db.delete(
                        CvBContract.PostsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case POST:
                rowsUpdated = db.update(CvBContract.PostsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POST:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CvBContract.PostsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}