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

package com.prprinc.beeperalarm.ui.filterlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.prprinc.beeperalarm.EditFilter;
import com.prprinc.beeperalarm.FilterObject;
import com.prprinc.beeperalarm.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

public class FilterListFragment extends Fragment {
    private static final String TAG = "FilterListFragment";

    public static String FILTER_NAME = "filter_id";

    private FilterViewModel filterViewModel;

    private ArrayAdapter<String> FilterListArrayAdapter;
    ArrayList<String> entries;
    ArrayList<Integer> entryID;
    boolean allowRefresh = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        filterViewModel =
                new ViewModelProvider(this).get(FilterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_filter_list, container, false);

        allowRefresh = false;

        entries = new ArrayList<>();
        entryID = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String fromPrefs = sharedPreferences.getString("filter_list","");
        Log.d(TAG, "filter list from preference:" + fromPrefs);
        Gson gson_filter = new Gson();
        FilterObject[] filterObjects = gson_filter.fromJson(fromPrefs,FilterObject[].class);
        if(filterObjects != null)
        {
            Log.d(TAG, "Create new filter");
            for(int i=0; i<filterObjects.length;i++){
                entryID.add(filterObjects[i].getId());
                entries.add(filterObjects[i].getName());
                Log.d(TAG, "filter name:" + filterObjects[i].getName());
                Log.d(TAG, "filter id:" + String.valueOf(filterObjects[i].getId()));
            }
        }

        final ListView FilterListView = root.findViewById(R.id.view_filter_list);
        FilterListView.setOnItemClickListener(mFilterClickListener);
        FilterListView.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.filter_list_line, entries ));


        return root;
    }
    // Set up on-click listener for the list (nicked this - unsure)
    AdapterView.OnItemClickListener mFilterClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            Intent myIntent = new Intent(getActivity(), EditFilter.class);
            myIntent.putExtra(FILTER_NAME, entryID.get(arg2));
            startActivity(myIntent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "FilterListFragment onResume");
        if (allowRefresh)
        {
            Log.d(TAG, "FilterListFragment allowRefresh:true, set to false");
            allowRefresh = false;
            getParentFragmentManager().beginTransaction().detach(this).commit();
            getParentFragmentManager().beginTransaction().attach(this).commit();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "FilterListFragment onPause");
        if (!allowRefresh) {
            Log.d(TAG, "FilterListFragment allowRefresh:false, set to true");
            allowRefresh = true;
        }
    }
}