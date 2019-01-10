package com.example.jonat.hideandseek;

import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;





import static android.app.Service.START_NOT_STICKY;

public class WearService extends WearableListenerService {


    public static final String INFO = "INFO";

    // Tag for Logcat
    private final String TAG = this.getClass().getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
    }


    // TO SEND INFORMATION
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // If no action defined, return
        if (intent.getAction() == null) return START_NOT_STICKY;

        // Match against the given action
        ACTION_SEND action = ACTION_SEND.valueOf(intent.getAction());

        switch (action) {
            case INFO:
                String info = intent.getStringExtra(INFO);
                sendMessage(info, intent.getStringExtra(INFO));
                break;

            default:
                Log.w(TAG, "Unknown action");
                break;
        }
        return START_NOT_STICKY;

    }

    private void sendMessage(String message, String path, final String nodeId) {
        // Sends a message through the Wear API
        Wearable.getMessageClient(this).sendMessage(nodeId, path, message.getBytes())
                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.v(TAG, "Sent message to " + nodeId + ". Result = " + integer);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Message not sent. " + e.getMessage());
            }
        });
    }

    private void sendMessage(String message, String path) {
        // Send message to ALL connected nodes
        sendMessageToNodes(message, path);
    }

    void sendMessageToNodes(final String message, final String path) {
        Toast.makeText(getApplicationContext(), "SendMessageToNodes " + message, Toast
                .LENGTH_SHORT).show();
        // Lists all the nodes (devices) connected to the Wear API
        Wearable.getNodeClient(this).getConnectedNodes().addOnCompleteListener(new OnCompleteListener<List<Node>>() {
            @Override
            public void onComplete(@NonNull Task<List<Node>> listTask) {
                List<Node> nodes = listTask.getResult();
                for (Node node : nodes) {
                    Log.v(TAG, "Try to send message to a specific node");
                    WearService.this.sendMessage(message, path, node.getId());
                }
            }
        });
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    public enum ACTION_SEND {
        MESSAGE, INFO
    }

}
