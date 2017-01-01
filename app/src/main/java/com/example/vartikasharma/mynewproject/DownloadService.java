package com.example.vartikasharma.mynewproject;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends Service {
    private static final String TAG = "HelloService";

    private boolean isRunning  = false;
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
        // This method is invoked when the service is called.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform your long running operations here.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {


                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    if(isRunning){
                        Log.i(TAG, "Service running");
                    }
                }

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
