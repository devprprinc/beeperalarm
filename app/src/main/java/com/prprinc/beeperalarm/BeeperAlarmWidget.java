/*
 *     This file is part of BeeperAlarm app.
 *
 *     BeeperAlarm app is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     BeeperAlarm app is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with BeeperAlarm app.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.prprinc.beeperalarm;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * Implementation of App Widget functionality.
 */
public class BeeperAlarmWidget extends AppWidgetProvider {
    private static final String TAG = "BeeperAlarmWidget";

    public static final String UPDATE_STATUS = "com.prprinc.beeperalarm.CHANGE_ENABLE";
    public static RemoteViews views;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){
        updateWidgetState(context, "");
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String str = intent.getAction();
        if (intent.getAction().equals(UPDATE_STATUS)) {
            updateWidgetState(context, str);
        }
        else
        {
            super.onReceive(context, intent);
        }
    }
    static void updateWidgetState(Context context, String parameter)
    {
        RemoteViews localRemoteViews = buildUpdate(context, parameter);
        ComponentName localComponentName = new ComponentName(context, BeeperAlarmWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(localComponentName, localRemoteViews);
    }
    private static RemoteViews buildUpdate(Context context, String parameter)
    {
        views = new RemoteViews(context.getPackageName(), R.layout.beeper_alarm_widget);
        Intent active = new Intent(context, BeeperAlarmWidget.class);
        active.setAction(UPDATE_STATUS);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        views.setOnClickPendingIntent(R.id.widget_icon, actionPendingIntent);
        views.setOnClickPendingIntent(R.id.appwidget_text, actionPendingIntent);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean switchPref = sharedPreferences.getBoolean("enableGlobal", false);

        if (parameter.equals(UPDATE_STATUS)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (switchPref) {
                //disable filters
                views.setImageViewResource(R.id.widget_icon, R.drawable.icon_red);
                views.setTextViewText(R.id.appwidget_text, context.getResources().getString(R.string.widget_disable));
                editor.putBoolean("enableGlobal", false);
            } else {
                //enable filters
                views.setImageViewResource(R.id.widget_icon, R.drawable.icon_green);
                views.setTextViewText(R.id.appwidget_text, context.getResources().getString(R.string.widget_enable));
                editor.putBoolean("enableGlobal", true);
            }
            editor.apply();
        }
        else
        {
            //set icon for first time
            if(switchPref)
            {
                views.setImageViewResource(R.id.widget_icon, R.drawable.icon_green);
                views.setTextViewText(R.id.appwidget_text, context.getResources().getString(R.string.widget_enable));
            }
            else
            {
                views.setImageViewResource(R.id.widget_icon, R.drawable.icon_red);
                views.setTextViewText(R.id.appwidget_text, context.getResources().getString(R.string.widget_disable));
            }
        }

        return views;
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