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

package com.prprinc.beeperalarm.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.prprinc.beeperalarm.R;
import com.prprinc.beeperalarm.SharedHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private static final String TAG = "HomeFragment";

    String filename_log = "beeperalam_datalog";
    ArrayList<HashMap<String,String>> data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //read from file
        data = new ArrayList<HashMap<String,String>>();
        File file_log = new File(getContext().getFilesDir(), filename_log);

        if(file_log.exists()) {

            Log.d(TAG, "File exist");
            FileReader file_reader_log = null;
            try {
                file_reader_log = new FileReader(file_log);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader bufferReaderLog = new BufferedReader(file_reader_log);
            StringBuilder stringbuilder = new StringBuilder();
            String line = "";

            Log.d(TAG, "first line:" + line);
            while (line != null) {
                try {
                    line = bufferReaderLog.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stringbuilder.append(line).append("\n");
                Log.d(TAG, "-" + line);
                if (line != null) {
                    JSONObject one_msg_line = null;
                    try {
                        one_msg_line = new JSONObject(line);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, one_msg_line.toString());

                    Map<String, String> filter_log = new HashMap<String, String>();
                    try {
                        filter_log.put("filter_name", one_msg_line.getString("filter_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        String sender_nr = one_msg_line.getString("sender");
                        String contact = SharedHelper.getContactName(sender_nr, getContext());
                        if (contact.equals(""))
                        {
                            filter_log.put("sender", sender_nr);
                        }
                        else
                        {
                            filter_log.put("sender", contact);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        filter_log.put("msg", one_msg_line.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        filter_log.put("time", one_msg_line.getString("time"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    data.add((HashMap<String, String>) filter_log);
                }
            }

            try {
                bufferReaderLog.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            //set string no logs
            final TextView textView = root.findViewById(R.id.text_home);
            textView.setText(getResources().getString(R.string.no_logs));
        }
        Collections.reverse(data);
        //end of read file

        //set Listview from ArrayList
        Log.d(TAG, "data:" + data.toString());
        Log.d(TAG, "data:" + data.toString());

        SimpleAdapter sa = new SimpleAdapter(getContext(), data,
                R.layout.alarm_list_log,
                new String[] { "filter_name","sender", "msg", "time" },
                new int[] {R.id.filter_name_msg, R.id.sender_msg, R.id.msg, R.id.time_msg});

        final ListView FilterListView = root.findViewById(R.id.msg_list);
        FilterListView.setAdapter(sa);

        //set Onlong click listener for deleting logs
        FilterListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                // TODO Auto-generated method stub
                Log.d(TAG, "in onLongClick");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(getResources().getString(R.string.delete_log_title));
                builder.setMessage(getResources().getString(R.string.delete_log_text));

                builder.setPositiveButton(getResources().getString(R.string.delete_yes), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    Log.d(TAG, "Yes");
                    dialog.dismiss();
                    File file_log = new File(getContext().getFilesDir(), filename_log);
                    if(file_log.exists())
                    {
                        file_log.delete();
                        reload();
                    }
                }
                });

                builder.setNegativeButton(getResources().getString(R.string.delete_no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        Log.d(TAG, "No");
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        return root;
    }

    public void reload() {
        // Refresh your fragment here
        Log.d(TAG, "Refreshing fragment");
        getParentFragmentManager().beginTransaction().detach(this).commit();
        getParentFragmentManager().beginTransaction().attach(this).commit();
    }
}