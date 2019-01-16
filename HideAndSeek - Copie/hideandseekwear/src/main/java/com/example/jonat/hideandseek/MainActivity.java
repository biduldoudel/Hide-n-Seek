package com.example.jonat.hideandseek;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonat.hideandseek.R;

public class MainActivity extends WearableActivity {

    private TextView command1;
    private TextView command2;

    public static final String ACTION_RECEIVE_PROFILE_INFO = "RECEIVE_PROFILE_INFO";
    public static final String PROFILE_USERNAME = "PROFILE_USERNAME";

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        command1 = (TextView) findViewById(R.id.command1);
        command2 = (TextView) findViewById(R.id.command2);

        vibrator = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);


        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String command = intent.getStringExtra("data");
                command2.setText(command1.getText());
                command1.setText(command);
                vibrator.vibrate(250);

            }
        }, new IntentFilter("COMMAND"));


        // Enables Always-on
        setAmbientEnabled();
    }
}
