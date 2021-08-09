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

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;


import static android.content.Intent.ACTION_AUTO_REVOKE_PERMISSIONS;

public class MainActivity extends AppCompatActivity implements  MessageListener{

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 849;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private static final int ACTION_AUTO_REVOKE_PERMISSIONS_REQUEST_CODE = 5470;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        FloatingActionButton add_filter = findViewById(R.id.add_filter);
        //Register sms listener
        MessageReceiver.bindListener(this);


        add_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, EditFilter.class);
                myIntent.putExtra("filter_id", -1);


//                MainActivity.this.startActivityForResult(myIntent, 101);
                MainActivity.this.startActivity(myIntent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_info, R.id.nav_filter_list)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        //check for permissions
        checkPermission();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void messageReceived(String message) {
        Toast.makeText(this, "New Message Received: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return true;

    }

    // Function to check and request permission
    public void checkPermission()
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "permission denied, ask for permission:" + Manifest.permission.RECEIVE_SMS + ", " + Manifest.permission.READ_CONTACTS);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Permission already granted");
            checkAutoRevokePermission();
        }
        //check for "draw app over other apps" permission
        if (!Settings.canDrawOverlays(this)) {
            // check if Android Go edition
            if(isLowRamDevice())
            {
                Log.d(TAG, "Low RAM device detected");
                new AlertDialog.Builder(this)
                        .setTitle(R.string.permission_error)
                        .setMessage(R.string.draw_over_other_apps_permission)
                        .show();
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                    }
                }
            }

        } else {
            // Do as per your logic
            //do nothing permission granted
        }
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission is granted now, continue working");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }
                else if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "permission is denied now, explain why we need this");
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.permission_error)
                            .setMessage(R.string.sms_permission)
                            .show();
                }
                else if (grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "permission is denied now, explain why we need this");
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.permission_error)
                            .setMessage(R.string.phone_contact_permission)
                            .show();
                }
                checkAutoRevokePermission();
                return;

        }


    }
    public boolean isLowRamDevice ()
    {
        return ((ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).isLowRamDevice();
    }

    public void checkAutoRevokePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PackageManager pm = getPackageManager();
            boolean auto_revoke_enabled = pm.isAutoRevokeWhitelisted();
            if (auto_revoke_enabled) {
                Log.d(TAG, "autorevoke enabled");
            } else {
                Log.d(TAG, "autorevoke disabled");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(getResources().getString(R.string.auto_revoke_title));
                builder.setMessage(getResources().getString(R.string.auto_revoke_text));

                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Intent intent = new Intent(ACTION_AUTO_REVOKE_PERMISSIONS,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, ACTION_AUTO_REVOKE_PERMISSIONS_REQUEST_CODE);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }
}