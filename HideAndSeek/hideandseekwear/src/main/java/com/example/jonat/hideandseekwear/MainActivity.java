package com.example.jonat.hideandseekwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView command1;
    private TextView command2;

    public static final String ACTION_RECEIVE_PROFILE_INFO = "RECEIVE_PROFILE_INFO";
    public static final String PROFILE_USERNAME = "PROFILE_USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        command1 = (TextView) findViewById(R.id.command1);
        command2 = (TextView) findViewById(R.id.command2);

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String command = intent.getStringExtra("data");
                command1.setText(command);

            }
        }, new IntentFilter("COMMAND"));


        // Enables Always-on
        setAmbientEnabled();
    }
}
