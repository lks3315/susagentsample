package com.tvstorm.susagent;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class FcmService extends FirebaseMessagingService {

    private final String TAG = AppConstants.TAG;

    boolean useWorkManager = true;

    public static FcmService getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final FcmService INSTANCE = new FcmService();
    }

    public FcmService() {
        if (connected == null) {
            connected = new SingleLiveEvent<>();
        }
        connected.setValue(false);
    }

    public LiveData<Boolean> connectedLiveData() {
        return connected;
    }

    SingleLiveEvent<Boolean> connected;
    Context mContext;
    ContentResolver resolver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FcmService onCreate");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(TAG, "From : " + remoteMessage.getFrom());
        if (remoteMessage.getTtl() > 0) {
            Log.d(TAG, "Message getTtl : " + remoteMessage.getTtl());
        }
        if (remoteMessage.getCollapseKey() != null) {
            Log.d(TAG, "Message getCollapseKey : " + remoteMessage.getCollapseKey());
        }

        setTextView(remoteMessage);

        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload : " + remoteMessage.getData());

            if (useWorkManager) {
//                scheduleJob(remoteMessage);
            } else {
                handleNow(remoteMessage);
            }
        }
    }

    private void setTextView(@NonNull RemoteMessage remoteMessage) {
        String title = "Title :: " + remoteMessage.getNotification().getTitle();
        String body = "Body :: " + remoteMessage.getNotification().getBody();
        String action = "Action :: " + remoteMessage.getData().get("action");
        String param = "Param :: " + remoteMessage.getData().get("param");

        Log.d(TAG, title);
        Log.d(TAG, body);
        Log.d(TAG, action);
        Log.d(TAG, param);

        MainActivity main = new MainActivity();
        main.titleView.setText(title);
        main.bodyView.setText(body);
        main.actionView.setText(action);
        main.paramView.setText(param);
    }

    private void scheduleJob(RemoteMessage remoteMessage) {
        Gson gson = new Gson();
        String remoteCommand = gson.toJson(remoteMessage.getData());

//        Data data = new Data.Builder().putString(AppConstants.REMOTE_COMMAND, remoteCommand).build();
        Data data = new Data.Builder().putString(AppConstants.REMOTE_COMMAND, remoteCommand).build();
        Log.d(TAG, remoteCommand);

        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FcmWorker.class)
                .setInputData(data)
                .build();

        OneTimeWorkRequest rebornWorker = new OneTimeWorkRequest.Builder(RebornWorker.class)
                .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.beginWith(work).enqueue();
        workManager.beginWith(rebornWorker).enqueue();
    }

    private void handleNow(RemoteMessage remoteMessage) {
        Log.i(TAG, "handleNow, Short lived task is done.");
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed onNewToken :: " + token);
    }

    public final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Boolean isConnected = connected.getValue();
            Log.d(TAG, "isConnected Service : " + isConnected);

            Log.d(TAG, "onServiceConnected");
            connected.setValue(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            connected.setValue(false);
            initService();
        }
    };

    private void initService() {
        if (mContext == null) {
            Log.e(TAG, "initService mContext == null");
        }

        mContext.unbindService(mConnection);
        mContext = null;
        resolver = null;
    }
}
