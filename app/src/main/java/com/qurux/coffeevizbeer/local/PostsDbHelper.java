package com.qurux.coffeevizbeer.local;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.qurux.coffeevizbeer.local.CvBContract.PostsEntry;

import java.util.ArrayList;

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

    //Helper
    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }
}
