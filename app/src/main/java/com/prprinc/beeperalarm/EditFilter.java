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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;


public class EditFilter extends AppCompatActivity {

    public static String FILTER_NAME = "filter_id";
    private static final String TAG = "EditFilterActivity";

    EditText filter_name;
    Button select_ringtone_button, save_btn, test_btn, delete_btn;
    Uri ringtone_uri;
    SeekBar volume_seekbar;
    TextView volume_text;

    FilterObject edit_filter = new FilterObject();
    String toPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_filter);
        int filter_id;
        filter_id = getIntent().getIntExtra(FILTER_NAME, -1);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditFilter.this);
        String fromPrefs = sharedPreferences.getString("filter_list","");
        Gson gson_filter = new Gson();
        FilterObject[] filterObjects = gson_filter.fromJson(fromPrefs,FilterObject[].class);
        filter_name = (EditText) findViewById(R.id.filter_name_text);
        Log.d(TAG, "INTENT edit_filter_name: " + filter_id);



        if (filter_id == -1)
        {
            Log.d(TAG, "Create new filter");
        }
        else
        {
            for(int i=0; i<filterObjects.length;i++){
                if(filter_id == filterObjects[i].getId())
                {
                    Log.d(TAG, "filter detected id:" +String.valueOf(filter_id));
                    edit_filter = filterObjects[i];
                }
            }
        }

        set_filter_view(edit_filter);

        select_ringtone_button = findViewById(R.id.select_ringtone);
        save_btn = findViewById(R.id.save_profile);
        test_btn = findViewById(R.id.test_profile);
        delete_btn = findViewById(R.id.delete_profile);
        volume_text = findViewById(R.id.volume_text);
        volume_seekbar = findViewById(R.id.volume_selecter);

        select_ringtone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone_uri);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtone_uri);
                //TODO: startActivityForResult is deprecated
                startActivityForResult(intent , RingtoneManager.TYPE_RINGTONE);
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "save btn pressed");
                get_filter_view(edit_filter);
                Log.d(TAG, "parser text: " + edit_filter.trigger_text);
                if(check_settings(edit_filter))
                {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditFilter.this);
                    String fromPrefs = sharedPreferences.getString("filter_list","");
                    Gson gson_filter = new Gson();
                    FilterObject[] filterObjects = gson_filter.fromJson(fromPrefs,FilterObject[].class);
                    filter_name = (EditText) findViewById(R.id.filter_name_text);
                    Log.d(TAG, "edit_filter_name: " + filter_id);
                    if(filterObjects == null)
                    {
                        Log.d(TAG, "empty list");
                        //we have empty filter list...
                        edit_filter.id = (int) (System.currentTimeMillis() & 0xfffffff);
                        filterObjects = new  FilterObject[1];
                        filterObjects[0] = edit_filter;
                        toPrefs = gson_filter.toJson(filterObjects);

                        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                        prefsEditor.putString("filter_list", toPrefs);
                        prefsEditor.commit();
                    }
                    else
                    {
                        if (filter_id == -1 )
                        {
                            //add new filter
                            Log.d(TAG, "Create new filter");
                            FilterObject[] filterObjects_new = new FilterObject[filterObjects.length + 1];
                            for(int i=0; i<filterObjects.length;i++){
                                filterObjects_new[i] = filterObjects[i];
                            }
                            //check and select new id
                            edit_filter.id = (int) (System.currentTimeMillis() & 0xfffffff);
                            //TODO check if id is already present in one object

                            filterObjects_new[filterObjects.length] = edit_filter;
                            filterObjects = filterObjects_new;
                        }
                        else
                        {
                            //edit existing filter
                            for(int i=0; i<filterObjects.length;i++){
                                if(filter_id == filterObjects[i].getId())
                                {
                                    filterObjects[i] = edit_filter;
                                }
                            }
                        }
                        Log.d(TAG, "NOT empty list:" + fromPrefs);

                        toPrefs = gson_filter.toJson(filterObjects);

                        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                        prefsEditor.putString("filter_list", toPrefs);
                        prefsEditor.commit();
                    }
                    Log.d(TAG, "New pref filter_list:" + toPrefs);
                    Log.d(TAG, "Filter list len:" + String.valueOf(filterObjects.length));

                    finish();
                }
            }
        });
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "test btn pressed");
                get_filter_view(edit_filter);
                if(check_settings(edit_filter))
                {
                    Intent myIntent = new Intent(EditFilter.this, AlarmActivity.class);
                    myIntent.putExtra("message", getResources().getString(R.string.test_msg_cnt));
                    myIntent.putExtra("sender", getResources().getString(R.string.test_msg_sender));
                    myIntent.putExtra("test", true);
                    Gson gson = new Gson();
                    String filter_json = gson.toJson(edit_filter);
                    myIntent.putExtra("filter_json", filter_json);

                    EditFilter.this.startActivity(myIntent);
                }

            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "delete btn pressed");
                if(filter_id != -1)
                {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditFilter.this);
                    String fromPrefs = sharedPreferences.getString("filter_list","");
                    Gson gson_filter = new Gson();
                    FilterObject[] filterObjects = gson_filter.fromJson(fromPrefs,FilterObject[].class);
                    FilterObject[] filterObjects_new = new FilterObject[filterObjects.length - 1];
                    filter_name = (EditText) findViewById(R.id.filter_name_text);
                    Log.d(TAG, "delete edit_filter_id: " + filter_id);

                    int j = 0;
                    for(int i=0; i<filterObjects.length;i++){

                        if(filter_id != filterObjects[i].getId())
                        {
                            filterObjects_new[j] = filterObjects[i];
                            j++;
                        }
                    }
                    toPrefs = gson_filter.toJson(filterObjects_new);

                    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                    prefsEditor.putString("filter_list", toPrefs);
                    prefsEditor.commit();
                }
                finish();
            }
        });
        volume_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar volume_seekbar, int progress, boolean fromUser) {
                //Update textview value
                volume_text.setText(getResources().getString(R.string.volume) + " (" + progress + "%)");
            }

            public void onStartTrackingTouch(SeekBar volume_seekbar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar volume_seekbar) {
                // Update your preferences only when the user has finished moving the seek bar
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RingtoneManager.TYPE_RINGTONE:
                if (resultCode == RESULT_OK) {
                    get_filter_view(edit_filter);
                    ringtone_uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    final Ringtone ringtone_ringtone = RingtoneManager.getRingtone(this, ringtone_uri);
                    edit_filter.ringtone_uri = ringtone_uri.toString();
                    edit_filter.ringtone_name = ringtone_ringtone.getTitle(this);
                    set_filter_view(edit_filter);
                    Log.d(TAG,"Uri = " + edit_filter.ringtone_uri + " and title = " + edit_filter.ringtone_name);
                }
                break;
        }
    } // onActivityResult

    public void set_filter_view(FilterObject filter_obj)
    {
        ((EditText)findViewById(R.id.filter_name_text)).setText(filter_obj.name);
        ((EditText)findViewById(R.id.phone_nr)).setText(filter_obj.phone);
        ((EditText)findViewById(R.id.trigger_string)).setText(filter_obj.trigger_text);
        ((CheckBox)findViewById(R.id.enable_filter)).setChecked(filter_obj.enable);
        ((CheckBox)findViewById(R.id.enable_logging)).setChecked(filter_obj.logging);
        ((CheckBox)findViewById(R.id.enable_vibrating)).setChecked(filter_obj.vibration);
        ((SeekBar)findViewById(R.id.volume_selecter)).setProgress(filter_obj.volume);
        ((TextView)findViewById(R.id.volume_text)).setText(getResources().getString(R.string.volume) + " (" + filter_obj.volume + "%)");

        ((TextView)findViewById(R.id.curent_ringtone)).setText(filter_obj.ringtone_name);
    }
    public void get_filter_view(FilterObject filter_obj)
    {
        filter_obj.name = ((EditText)findViewById(R.id.filter_name_text)).getText().toString();
        filter_obj.phone = ((EditText)findViewById(R.id.phone_nr)).getText().toString();
        filter_obj.trigger_text = ((EditText)findViewById(R.id.trigger_string)).getText().toString();
        filter_obj.enable = ((CheckBox)findViewById(R.id.enable_filter)).isChecked();
        filter_obj.logging = ((CheckBox)findViewById(R.id.enable_logging)).isChecked();
        filter_obj.vibration = ((CheckBox)findViewById(R.id.enable_vibrating)).isChecked();
        filter_obj.volume = ((SeekBar)findViewById(R.id.volume_selecter)).getProgress();
    }
    public boolean check_settings(FilterObject filter_obj)
    {
        if (filter_obj.name.length() != 0 && (filter_obj.trigger_text.length() != 0 || filter_obj.phone.length() != 0) ) {
            return true;
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_setting_filter)
                    .setMessage(R.string.error_setting_filter_msg)
                    .show();
            return false;
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState");
        savedInstanceState.putString("ringtone_name", edit_filter.ringtone_name);
        savedInstanceState.putString("ringtone_uri", edit_filter.ringtone_uri);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        if(savedInstanceState!=null)
        {
            get_filter_view(edit_filter);
            edit_filter.ringtone_name = savedInstanceState.getString("ringtone_name");
            edit_filter.ringtone_uri = savedInstanceState.getString("ringtone_uri");
            set_filter_view(edit_filter);
        }
    }
}
