package android.daehoshin.com.locationsharechat.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.CurrentLocationManager;
import android.daehoshin.com.locationsharechat.common.Constants;
import android.os.IBinder;

/**
 * 앱이 켜질 때 시작
 */
public class LocationService extends Service {
    private static final int FLAG = 4342;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();

        if(intent != null){
            String action = intent.getAction();
            switch (action){
                case Constants.Thread_START:
                    CurrentLocationManager.updateLocationStart(this);
                    break;
                case Constants.Thread_STOP:
                    CurrentLocationManager.updateLocationStop();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // Foreground 서비스하기
    private boolean isRunningNoti = false;
    private void startNotification(){
        if(isRunningNoti) return;

        int icon = R.mipmap.ic_launcher_round;

        // Foreground 서비스에서 보여질 Notification 만들기
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(icon)
                .setContentTitle("어디야")
                .setContentText("내 위치 공유중");
        startForeground(FLAG, builder.build());

        isRunningNoti = true;
    }

    @Override
    public void onDestroy() {
        CurrentLocationManager.updateLocationStop();
        stopForeground(true);

        super.onDestroy();
    }
}
