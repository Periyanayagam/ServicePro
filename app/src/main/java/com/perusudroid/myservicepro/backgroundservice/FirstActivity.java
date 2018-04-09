package com.perusudroid.myservicepro.backgroundservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.perusudroid.myservicepro.R;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isBound = false;
    private OneService mService;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message != null) {
                Toast.makeText(mService, ""+message.obj, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        findViewById(R.id.btnSend).setOnClickListener(this);
        doBindService();
    }

    private void doBindService() {
        if (!isBound) {
            Intent intent = new Intent(this, OneService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                if (mService != null) {
                    Messenger messenger = new Messenger(mHandler);
                    Intent intent = new Intent();
                    intent.putExtra("Messenger", messenger);
                    mService.doSendMsg(intent);
                }
                break;
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            OneService.LocalBinder binder = (OneService.LocalBinder) iBinder;
            mService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
