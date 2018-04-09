package com.perusudroid.myservicepro.backgroundservice;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by Perusudroid on 4/9/2018.
 */

public class TaskHandler extends Handler {


    private static final String TAG = TaskHandler.class.getSimpleName();

    public TaskHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.d(TAG, "handleMessage: ");
        Messenger messenger = null;
        Bundle extras = msg.getData();

        if(extras != null){
            messenger = (Messenger) extras.get("Messenger");
        }

        synchronized (this){

            try {
                Thread.sleep(2000);
                wait(2000);
                if(messenger != null){
                    Message message = Message.obtain();
                    message.obj = "Yep! data received";
                    messenger.send(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }

    }
}
