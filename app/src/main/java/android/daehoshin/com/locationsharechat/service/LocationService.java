package android.daehoshin.com.locationsharechat.service;

import android.app.Service;
import android.content.Intent;
import android.daehoshin.com.locationsharechat.common.CustomLocationManager;
import android.daehoshin.com.locationsharechat.constant.Consts;
import android.os.IBinder;
import android.util.Log;

/**
 * 앱이 켜질 때 시작
 */
public class LocationService extends Service {

    CustomLocationManager customLocationManager = null;

    public LocationService() { }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate","=======================================");
        customLocationManager = new CustomLocationManager(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            Log.e("onStartCommand","======================================="+action);
            switch (action){
                case Consts.Thread_START:
                    customLocationManager.startCheckGPS();
                    break;
                case Consts.Thread_STOP:
                    customLocationManager.stopCheckGPS();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        if(customLocationManager != null){
            customLocationManager.stopCheckGPS();
            customLocationManager = null;
        }
        super.onDestroy();
        Log.e("onDestroy","=======================================");
    }
}
