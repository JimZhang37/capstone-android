package com.dong.edu;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dong.edu.R;
import com.dong.edu.data.Sprint;
import com.dong.edu.util.FetchDataFromFirebaseService;

import java.util.List;

import static com.dong.edu.util.FetchDataFromFirebaseService.ACTION_FETCH_DATA;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                List<Sprint> sprints,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text0, context.getResources().getString(R.string.appwidget_text));

        views.setTextViewText(R.id.appwidget_text1,"1: " + sprints.get(0).getSprintName());
        views.setTextViewText(R.id.appwidget_text2,"2: " + sprints.get(1).getSprintName());
        views.setTextViewText(R.id.appwidget_text3,"3: " + sprints.get(2).getSprintName());

        Intent intent = new Intent(context, FetchDataFromFirebaseService.class);
        intent.setAction(ACTION_FETCH_DATA);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.appwidget_text0, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        FetchDataFromFirebaseService.startActionFetchData(context);
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, "abc",appWidgetId);
//        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, List<Sprint> sprints, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, sprints,appWidgetId);
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
}

