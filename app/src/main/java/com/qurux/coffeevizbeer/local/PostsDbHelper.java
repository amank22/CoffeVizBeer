package com.qurux.coffeevizbeer.local;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qurux.coffeevizbeer.local.CvBContract.PostsEntry;

/**
 * Created by Aman Kapoor on 17-11-2016.
 */

public class PostsDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "posts.db";

    public PostsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public PostsDbHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_POSTS_TABLE = "CREATE TABLE " + PostsEntry.TABLE_NAME + " (" +
                PostsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PostsEntry.COLUMN_SERVER_ID + " INTEGER UNIQUE NOT NULL, " +
                PostsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_SUMMARY + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_DESCRIPTION + " TEXT, " +
                PostsEntry.COLUMN_LINK_THIS + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_LINK_THAT + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_AUTHOR_ID + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                PostsEntry.COLUMN_COLOR + " TEXT DEFAULT '#363636', " +
                PostsEntry.COLUMN_LIKED + " INTEGER DEFAULT 0, " +
                PostsEntry.COLUMN_BOOKMARKED + " INTEGER DEFAULT 0" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PostsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
