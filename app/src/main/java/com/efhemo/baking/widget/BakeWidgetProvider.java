package com.efhemo.baking.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.efhemo.baking.DetailActivity;
import com.efhemo.baking.R;

import java.util.ArrayList;

import static com.efhemo.baking.widget.UpdateService.FROM_ACTIVITY_INGREDIENTS_LIST;

public class BakeWidgetProvider extends AppWidgetProvider {


    public static String REMOTEVIEW_INGREDIENT_LIST="REMOTEVIEW_INGREDIENT_LIST";
    public static String REMOTEVIEW_BUNDLE="REMOTEVIEW_BUNDLE";

    static ArrayList<String> ingredientsList = new ArrayList<>();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        //call activity when widget is clicked, but resume activity from stack so you do not pass intent.extras afresh
        Intent appIntent = new Intent(context, DetailActivity.class);
        appIntent.addCategory(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, WidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    /*
    * Called in response to the AppWidgetManager.ACTION_APPWIDGET_UPDATE and
    * AppWidgetManager.ACTION_APPWIDGET_RESTORED broadcasts when this AppWidget provider is
    * being asked to provide RemoteViews for a set of AppWidgets.
    *
    * */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        updateBakingWidgets(context, appWidgetManager, appWidgetIds);
    }

    public static void updateBakingWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
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
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakeWidgetProvider.class));

        final String action = intent.getAction();

        if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            ingredientsList = intent.getExtras().getStringArrayList(FROM_ACTIVITY_INGREDIENTS_LIST);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
            //Now update all widgets
            BakeWidgetProvider.updateBakingWidgets(context, appWidgetManager, appWidgetIds);
            super.onReceive(context, intent);
        }

        super.onReceive(context, intent);
    }

}