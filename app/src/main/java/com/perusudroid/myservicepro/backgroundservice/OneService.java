package com.perusudroid.myservicepro.backgroundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by Perusudroid on 4/9/2018.
 */

public class OneService extends Service {

    private static String TAG = OneService.class.getSimpleName();
    private IBinder iBinder = new LocalBinder();
    private TaskHandler taskHandler;
    private Looper mLooper;

    public void doSendMsg(Intent intent){
        Message message = Message.obtain();
        message.setData(intent.getExtras());
        if(taskHandler != null){
            //taskHandler.handleMessage(message); // app freezes
            taskHandler.sendMessage(message);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        //Alternative to IntentService
        HandlerThread thread = new HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mLooper = thread.getLooper();
        taskHandler = new TaskHandler(mLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public class LocalBinder extends Binder {
        OneService getService(){
            return OneService.this;
        }
    }
}
