package com.tvstorm.susagent;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Timer;
import java.util.TimerTask;
import static android.util.Log.d;
import static android.util.Log.e;

public class MainActivity extends AppCompatActivity {

    private final String TAG = AppConstants.TAG;

    private String mFcmToken = null;
    private TimerTask timerTask = null;

    public TextView fcmView;
    public TextView titleView;
    public TextView bodyView;
    public TextView actionView;
    public TextView paramView;

    Intent foregroundServiceIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fcmView = findViewById(R.id.fcmText);
        titleView = findViewById(R.id.title);
        bodyView = findViewById(R.id.body);
        actionView = findViewById(R.id.action);
        paramView = findViewById(R.id.param);

        getTokenFCM();
    }

    private void getTokenFCM() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(taskComplete());
    }

    private OnCompleteListener<String> taskComplete() {
        return task -> {
            if (!task.isSuccessful()) {
                e(TAG, "Get Token Failed ::" + task.getException());

                long delay = 10;
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        d(TAG, "onlyGetTokenRetry START !!");
                        d(TAG, "GetToken Retry :: " + delay + "s delay");
                        getTokenFCM();
                    }
                };
                new Timer().schedule(timerTask, delay * 1000);
            } else {
                mFcmToken = task.getResult();
                cancelTimer();
            }
            String msgToken = "FCM Token :: " + mFcmToken;
            d(TAG, msgToken);
            fcmView.setText(msgToken);
        };
    }

    private void cancelTimer() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

//    private void startService() {
//        if (MainService.serviceIntent == null) {
//            foregroundServiceIntent = new Intent(this, MainService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(foregroundServiceIntent);
//                Log.i(TAG, "startForegroundService");
//            } else {
//                startService(foregroundServiceIntent);
//                Log.i(TAG, "startService");
//            }
//        } else {
//            foregroundServiceIntent = MainService.serviceIntent;
//            Log.i(TAG, "Ignore foregroundServiceIntent");
//        }
//    }

    private void startFcmService() {
        Log.d(TAG, "Start FcmService!!");
        Intent intent = new Intent(getApplicationContext(), FcmService.class);
        startService(intent);
        FcmService fcmService = FcmService.getInstance();
        bindService(intent, fcmService.mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        d(TAG, "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart in MainActivity");
        startFcmService();
//        Intent intent = new Intent(getApplicationContext(), FcmService.class);
//        FcmService fcmService = FcmService.getInstance();
//        bindService(intent, fcmService.mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(), FcmService.class);
        stopService(intent);
        d(TAG, "onDestroy");
    }
}