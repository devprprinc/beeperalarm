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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.content.SharedPreferences;


import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log; //Debug

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageReceiver";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private static MessageListener mListener;
    boolean enable_beeper_alarm;
    String string_sms_match;
    String[] string_sms_match_multi;
    boolean start_alarm = false;
    int filter_idx;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                enable_beeper_alarm = sharedPreferences.getBoolean("enableGlobal", true);
                string_sms_match =sharedPreferences.getString("sms_text_content", "None");
                Log.d(TAG, "string parser:" + string_sms_match);


                Log.d(TAG, "string parser split:" + Arrays.toString(string_sms_match_multi));

                if(enable_beeper_alarm) {
                    //get all filters form shared preference
                    String fromPrefs = sharedPreferences.getString("filter_list","");
                    Log.d(TAG, "filter list from preference:" + fromPrefs);
                    Gson gson_filter = new Gson();
                    FilterObject[] filterObjects = gson_filter.fromJson(fromPrefs,FilterObject[].class);
                    if(filterObjects != null)
                    {
                        Log.d(TAG, "Checking filters...");
                        Log.d(TAG, "Nr of filters:" + String.valueOf(filterObjects.length));
                        for(int i=0; i<filterObjects.length; i++){
                            Log.d(TAG, "Checking if match for filter name:" + filterObjects[i].getName());
                            Log.d(TAG, "Checking if match for filter id:" + String.valueOf(filterObjects[i].getId()));

                            Log.d(TAG, "string_sms_match_multi:" + Arrays.toString(string_sms_match_multi));
                            if(filterObjects[i].enable) { //if filter is enabled
                                Log.d(TAG, "filter enabled");
                                string_sms_match_multi = filterObjects[i].getTrigger_text().split(";");
                                for (int j = 0; j <string_sms_match_multi.length; j++){
                                    if (message.contains(string_sms_match_multi[j]) && sender.contains(filterObjects[i].getPhone())) {
                                        start_alarm = true;
                                        filter_idx = i;
                                        Log.d(TAG, "Filter detected!!!");
                                        break;
                                    }
                                }
                            }
                            if ( start_alarm ) {
                                Log.d(TAG, "Activate alarm!");
                                //display notification
                                String channelId = "BeeperAlarm_ch_ID";
                                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);

                                notificationBuilder.setAutoCancel(false)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setWhen(System.currentTimeMillis())
                                        .setSmallIcon(R.drawable.ic_beeperalarm_notification)
                                        .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                        .setContentTitle(context.getApplicationInfo().loadLabel(context.getPackageManager()).toString())
                                        .setContentText(message)
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText(message))
                                        .setContentInfo("Info");

                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                // Since android Oreo notification channel is needed.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel(channelId,
                                            "BeeperAlarm channel ID",
                                            NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(channel);
                                }
                                Random random = new Random();
                                Integer notification_id = random.nextInt(9999 - 1000) + 1000;

                                notificationManager.notify(notification_id, notificationBuilder.build());

                                //start Alarm activity
                                Intent myIntent = new Intent();
                                myIntent.setClassName("com.prprinc.beeperalarm", "com.prprinc.beeperalarm.AlarmActivity");
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                myIntent.putExtra("message", message);
                                myIntent.putExtra("sender", sender);
                                myIntent.putExtra("test", false);

                                Gson gson = new Gson();
                                String filter_json = gson.toJson(filterObjects[filter_idx]);
                                myIntent.putExtra("filter_json", filter_json);

                                context.startActivity(myIntent);
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
}
