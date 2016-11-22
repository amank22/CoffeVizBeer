package com.qurux.coffeevizbeer.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Aman Kapoor on 17-11-2016.
 * Defines table and column names for the coffee vz beer posts database.
 */

public class CvBContract {

    public static final String CONTENT_AUTHORITY = "com.qurux.coffeevizbeer.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CvB_POSTS = "posts";

    public static final class PostsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CvB_POSTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CvB_POSTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CvB_POSTS;

        // Table name
        public static final String TABLE_NAME = "cvb_posts";

        public static final String COLUMN_SERVER_ID = "post_server_id";
        public static final String COLUMN_TITLE = "post_title";
        public static final String COLUMN_SUMMARY = "post_summary";
        public static final String COLUMN_DESCRIPTION = "post_description";
        public static final String COLUMN_LINK_THIS = "post_link_this";
        public static final String COLUMN_LINK_THAT = "post_link_that";
        public static final String COLUMN_AUTHOR = "post_author";
        public static final String COLUMN_AUTHOR_ID = "post_author_id";
        public static final String COLUMN_DATE = "post_date";
        public static final String COLUMN_LIKED = "post_liked";
        public static final String COLUMN_BOOKMARKED = "post_bookmarked";
        public static final String COLUMN_COLOR = "post_color";

        public static Uri buildPostsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

}
