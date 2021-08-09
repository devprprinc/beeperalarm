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

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ncorti.slidetoact.SlideToActView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AlarmActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Button btn_stop;
    TextView msg_text;
    TextView sndr_text;
    private static final String TAG = "AlarmActivity";
    AudioManager audioManager;
    int userVolume;
    int maxVolume;
    boolean test_mode = false;
    String alarm_msg;
    String alarm_sender;
    String filename_log = "beeperalam_datalog";
    String contact_name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        test_mode = getIntent().getBooleanExtra("test", false);

        Gson gson = new Gson();
        FilterObject alarm_filter = gson.fromJson(getIntent().getStringExtra("filter_json"), FilterObject.class);
        alarm_msg = getIntent().getStringExtra("message");
        alarm_sender = getIntent().getStringExtra("sender");
        contact_name = SharedHelper.getContactName(alarm_sender, this);



        Log.d(TAG, "alarm_filter name:" + alarm_filter.name );
        Log.d(TAG, "alarm_filter volume:" + String.valueOf(alarm_filter.volume ));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPreferences.getBoolean("enableGlobal", false);

        Log.d(TAG, "switchPref:" + String.valueOf(switchPref));
        Log.d(TAG, "sound_level:" + String.valueOf(alarm_filter.volume));

        setContentView(R.layout.activity_alarm);

        // AudioManager used to get and set different volume levels
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        Uri notification = Uri.parse(alarm_filter.ringtone_uri);

        mediaPlayer  = MediaPlayer.create(AlarmActivity.this, notification);
        mediaPlayer.setLooping(true);

        userVolume = audioManager.getStreamVolume(audioManager.STREAM_MUSIC); // current volume stream of device
        maxVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC); // max volume stream of device
        Log.d(TAG, "maxVolume:"+ String.valueOf(maxVolume));

        int volume = (int) (alarm_filter.volume) * maxVolume / 100; // set volume to: (percentage) * (max. volume of device) / (100%)
        Log.d(TAG, "Volume: " + String.valueOf(volume));
        audioManager.setStreamVolume(audioManager.STREAM_MUSIC, volume,0);
        mediaPlayer.setVolume(volume, volume);
        mediaPlayer.start();

        msg_text = (TextView)findViewById(R.id.messageText);
        msg_text.setText(alarm_msg);

        sndr_text = (TextView)findViewById(R.id.senderText);
        if(contact_name.equals(""))
        {
            sndr_text.setText(getResources().getString(R.string.alarm_sender)  + "\n" + alarm_sender );
        }
        else
        {
            sndr_text.setText(getResources().getString(R.string.alarm_sender)  + "\n" + contact_name);
        }


        btn_stop = (Button) findViewById(R.id.close_btn);
        btn_stop.setVisibility(View.GONE);

        SlideToActView sta = findViewById(R.id.seek_bar);

        if(!test_mode && alarm_filter.logging)
        {
            Log.d(TAG,"write to log file");
            String currentTime = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss", Locale.getDefault()).format(new Date());
            Log.d(TAG, "current time: " + currentTime);

            //construct JSON object
            JSONObject alarm_log = new JSONObject();
            try {
                alarm_log.put("filter_name", alarm_filter.name);
                alarm_log.put("sender", alarm_sender);
                alarm_log.put("msg", alarm_msg);
                alarm_log.put("time", currentTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //write JSON to file
            Log.d(TAG,"start writing to log file");
            File file_log = new File(getFilesDir(), filename_log);
            FileWriter file_writer_log = null;
            try {
                file_writer_log = new FileWriter(file_log, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bufferWriterLog = new BufferedWriter(file_writer_log);
            try {
                bufferWriterLog.write(alarm_log.toString() + "\n");
                Log.d(TAG, "writen to file:" + alarm_log.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferWriterLog.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        sta.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete( SlideToActView view) {
                Log.d(TAG, "slide completed");
                mediaPlayer.stop();
                audioManager.setStreamVolume(audioManager.STREAM_MUSIC, userVolume,0); // set to old volume stream

                mediaPlayer.reset();
                mediaPlayer.release();
                sta.setVisibility(View.GONE);
                btn_stop.setVisibility(View.VISIBLE);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
