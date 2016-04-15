package com.tzutalin.example;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.tzutalin.configio.ConfigIO;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + "config.json";

        ConfigIO configIO = new ConfigIO(getApplicationContext());
        configIO.init(targetPath);

        // Write
        ConfigIO.Writer writer = configIO.getWriter();
        writer.putString("test", "123");
        writer.putBoolean("test2", true);

        // Read

        String test_str = configIO.getString("test", "default_str");
        boolean test_bool = configIO.getBoolean("test2", false);
    }
}
