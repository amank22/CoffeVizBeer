package com.qurux.coffeevizbeer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.helper.FireBaseHelper;
import com.qurux.coffeevizbeer.ui.HomeActivity;

/**
 * Implementation of App Widget functionality.
 */
public class PostsWidget extends AppWidgetProvider {

    private static final String REFRESH_ACTION = "com.qurux.coffeevizbeer.appwidget.action.REFRESH";
    private static final String KEY_NEWDATA = "KEY_NEWDATA";

    private static boolean status = false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.posts_widget);
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_frame, pendingIntent);
        views.setRemoteAdapter(R.id.posts_widget_list, new Intent(context, PostsRemoteViewService.class));
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            views.setTextViewText(R.id.widget_static_text, context.getString(R.string.widget_please_login));
        } else {
            views.setTextViewText(R.id.widget_static_text, context.getString(R.string.appwidget_text));
        }
        Intent intentSync = new Intent(context, PostsWidget.class);
        intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE); //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
        PendingIntent pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT); //You need to specify a proper flag for the intent. Or else the intent will become deleted.
        views.setOnClickPendingIntent(R.id.widget_refresh_button, pendingSync);
        Intent startActivityIntent = new Intent(context, HomeActivity.class);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.posts_widget_list, startActivityPendingIntent);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.posts_widget_list);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(REFRESH_ACTION);
        intent.setComponent(new ComponentName(context, PostsWidget.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        final String action = intent.getAction();
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.posts_widget);
        if (REFRESH_ACTION.equals(action)) {
            // refresh all your widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, PostsWidget.class);
            int[] appWidgetIds = mgr.getAppWidgetIds(cn);
            remoteViews.setViewVisibility(R.id.widget_refresh_button, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_refresh_button_loading, View.VISIBLE);
            status = true;
            onUpdate(context, mgr, appWidgetIds);
        } else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            FireBaseHelper.setNewPostListener(context);
            remoteViews.setViewVisibility(R.id.widget_refresh_button, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_refresh_button_loading, View.GONE);
            status = false;

            ComponentName cn = new ComponentName(context, PostsWidget.class);
            (AppWidgetManager.getInstance(context)).updateAppWidget(cn, remoteViews);

        }
    }
}

