/*
*  Copyright (C) 2016 TzuTaLin
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.tzutalin.example;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tzutalin.configio.ConfigIO;

import java.io.File;

import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextView mJsonTestTextView;
    private TextView mXmlTestTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJsonTestTextView = (TextView) findViewById(R.id.text_view_jsontest);
        mXmlTestTextView = (TextView) findViewById(R.id.text_view_xmltest);

        // Just use hugo to print log
        isExternalStorageWritable();
        isExternalStorageReadable();

        // For API 23+ you need to request the read/write permissions even if they are already in your manifest.
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            verifyStoragePermissions(this);
        }
        demo();
    }

    @DebugLog
    private void demo() {
        File sdcard = Environment.getExternalStorageDirectory();

        // Save configure to json format, and retrieve it
        {
            String targetPath = sdcard.getAbsolutePath() + File.separator + "config.json";
            ConfigIO configIO = ConfigIO.newInstance(targetPath);

            // Write
            ConfigIO.Writer writer = configIO.getWriter();
            writer.putString("test_str", "12345678")
                    .putBoolean("test_bool", true)
                    .putInt("test_int", 10)
                    .putFloat("test_float", 0.5f)
                    .putLong("test_long", 100000000L);

            // Blocking method
            writer.commit();
            // writer.apply() will save file async
            //writer.apply();

            // Read
            // It will load config from the file
            configIO.loadFromFile();
            String test_str = configIO.getString("test_str", "default_str");
            boolean test_bool = configIO.getBoolean("test_bool", false);
            int test_int = configIO.getInt("test_int", 0);
            float test_float = configIO.getFloat("test_float", 0);
            long test_long = configIO.getLong("test_long", 0);

            StringBuilder sb = new StringBuilder();
            sb.append("\njson config content:")
                    .append("\ntest_string:").append(test_str)
                    .append("\ntest_bool:").append(test_bool)
                    .append("\ntest_int:").append(test_int)
                    .append("\ntest_float:").append(test_float)
                    .append("\ntest_long:").append(test_long);
            mJsonTestTextView.setText(sb.toString());
        }

        // Save configuration to xml, and retrieve it
        {
            String targetPath = sdcard.getAbsolutePath() + File.separator + "config.xml";
            ConfigIO configIO = ConfigIO.newInstance(targetPath);

            // Write
            ConfigIO.Writer writer = configIO.getWriter();
            writer.putString("test_str", "12345678");
            writer.putBoolean("test_bool", true);
            writer.putInt("test_int", 10);
            writer.putFloat("test_float", 0.5f);
            writer.putLong("test_long", 100000000L);

            // Blocking method
            writer.commit();
            // writer.apply() will save file async
            //writer.apply();

            // Read
            // It will load config from the file
            configIO.loadFromFile();
            String test_str = configIO.getString("test_str", "default_str");
            boolean test_bool = configIO.getBoolean("test_bool", false);
            int test_int = configIO.getInt("test_int", 0);
            float test_float = configIO.getFloat("test_float", 0);
            long test_long = configIO.getLong("test_long", 0);

            StringBuilder sb = new StringBuilder();
            sb.append("\nxml config content:")
                    .append("\ntest_string:").append(test_str)
                    .append("\ntest_bool:").append(test_bool)
                    .append("\ntest_int:").append(test_int)
                    .append("\ntest_float:").append(test_float)
                    .append("\ntest_long:").append(test_long);
            mXmlTestTextView.setText(sb.toString());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            demo();
        }
    }

    /* Checks if external storage is available for read and write */
    @DebugLog
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    @DebugLog
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    @DebugLog
    private static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int write_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_persmission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (write_permission != PackageManager.PERMISSION_GRANTED || read_persmission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
