package com.qurux.coffeevizbeer.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.local.CvBContract;

/**
 * Created by Aman Kapoor on 04-12-2016.
 */
public class PostsRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        CvBContract.PostsEntry.CONTENT_URI,
                        new String[]{
                                CvBContract.PostsEntry._ID,
                                CvBContract.PostsEntry.COLUMN_SERVER_ID,
                                CvBContract.PostsEntry.COLUMN_TITLE,
                                CvBContract.PostsEntry.COLUMN_SUMMARY,
                                CvBContract.PostsEntry.COLUMN_COLOR,
                                CvBContract.PostsEntry.COLUMN_AUTHOR
                        },
                        null,
                        null,
                        CvBContract.PostsEntry._ID + " DESC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_item);
                // Bind data to the views
                views.setTextViewText(R.id.widget_title, data.getString(data.getColumnIndex
                        (CvBContract.PostsEntry.COLUMN_TITLE)).replace("::", " "));
                views.setTextViewText(R.id.widget_author, data.getString(data.getColumnIndex
                        (CvBContract.PostsEntry.COLUMN_AUTHOR)));
                views.setTextViewText(R.id.widget_summary, data.getString(data.getColumnIndex
                        (CvBContract.PostsEntry.COLUMN_SUMMARY)));
                Intent fillInIntent = new Intent();
//                fillInIntent.putExtra(Widget.EXTRA_LIST_VIEW_ROW_NUMBER, position);
                views.setOnClickFillInIntent(R.id.widget_item_frame, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data != null && data.moveToPosition(position)) {
                    return data.getLong(0);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
