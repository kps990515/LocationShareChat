package android.daehoshin.com.locationsharechat.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.daehoshin.com.locationsharechat.constant.Consts;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 위치 정보에 대한 것을 관리하는 Manager (LocationManager는 이미 있어서 다시 정의)
 * - 아마 필요하지 않을수도..... (서비스시?? 엑티비티 사용 안할때 사용??? 일단 생각...)
 */

public class CustomLocationManager implements LocationListener {
    private UserInfo currentUser = null;

    private LocationManager locationManager;
    private Location lastLocation;
    private double lastLat;
    private double lastLng;

    private LocationThread thread;
    private static boolean threadCheck = false;
    private static boolean runFlag = true;

    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    public CustomLocationManager(Context context){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        thread = new LocationThread(handler);
    }

    public void startCheckGPS(){
        runFlag= true;
        if(!thread.isAlive()) {
            thread.start();
        }
        startUpdateLocation();
    }

    public void stopCheckGPS(){
        runFlag = false;
        stopUpdateLocation();
    }

    /**
     * 내 위치를 자동으로 업데이트 해주는 메소드 (시작, 끝을 선언해준다,)
     * - GPS 혹은 Network가 켜져 있어야 실행
     * - 시간은 milisecond 단위, 거리는 meter단위로 넣어준다. (Const에 지정해야 할듯)
     */
    @SuppressLint("MissingPermission")
    public void startUpdateLocation(){
        if(gps_enabled || network_enabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Consts.LOCATION_INTERVAL_TIME, Consts.LOCATION_INTERVAL_METER, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,Consts.LOCATION_INTERVAL_TIME, Consts.LOCATION_INTERVAL_METER, this);
        } else {
            // popup 으로 GPS 혹은 network 설정할지 요청?? 안하면 start 하지 않음
        }
        Log.e("startUpdateLocation","=======================================" + gps_enabled + " // "+ network_enabled);

        if(currentUser == null){
            AuthManager.getInstance().getCurrentUser(new AuthManager.IAuthCallback() {
                @Override
                public void signinAnonymously(boolean isSuccessful) {

                }

                @Override
                public void getCurrentUser(UserInfo userInfo) {
                    currentUser = userInfo;
                }
            });
        }
    }
    public void stopUpdateLocation(){
        locationManager.removeUpdates(this);
        Log.e("stopUpdateLocation","=======================================");
    }

    /**
     * locationManager에 달리는 리스너 정의
     * - onlocationchange가 거리가 바뀔때 호출된다. (stop 하게 되면 호출되지 않는다.)
     */
    @Override
    public void onLocationChanged(Location location) {
        lastLat = location.getLatitude();
        lastLng = location.getLongitude();
        CurrentLocation.setLatitude(lastLat);
        CurrentLocation.setLongitude(lastLng);
        lastLocation = location;
        Log.e("onLocationChanged","============="+lastLat + " //" + lastLng);

        if(currentUser != null){
            currentUser.updateLocation(lastLat, lastLng);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Consts.LOCATION_UPDATE_START:
                    startUpdateLocation();
                    break;
                case Consts.LOCATION_UPDATE_STOP:
                    stopUpdateLocation();
                    break;
            }
        }
    };


    private class LocationThread extends Thread{
        Handler handler;
        boolean check;
        public LocationThread(Handler handler){
            this.handler = handler;
            check = gps_enabled || network_enabled;
        }
        public void run(){
            while(runFlag){
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(check != (gps_enabled || network_enabled)){
                    check = gps_enabled || network_enabled;
                    Message msg = new Message();
                    if(check){
                        msg.what = Consts.LOCATION_UPDATE_START;
                        handler.sendMessage(msg);
                    } else {
                        msg.what = Consts.LOCATION_UPDATE_STOP;
                        handler.sendMessage(msg);
                    }
                }
                else {
                    Log.e("ThreadContinue",gps_enabled + " // "+ network_enabled);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
