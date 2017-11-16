package android.daehoshin.com.locationsharechat.common;

import android.annotation.SuppressLint;
import android.content.Context;
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
public class CurrentLocationManager implements LocationListener {
    public static int UPDATE_TIME_GAP = 2000;

    private static CurrentLocationManager clm = null;
    public static void updateLocationStart(Context context){
        if(clm == null) clm = new CurrentLocationManager(context);

        clm.start();
    }
    public static void updateLocationStop(){
        if(clm != null) clm.stop();
    }



    private UserInfo currentUser = null;
    private LocationManager locationManager;
    private boolean isRunningThread = false;
    private boolean isRunning = false;

    private CurrentLocationManager(){

    }

    private CurrentLocationManager(Context context){
        if(currentUser == null) AuthManager.getInstance().getCurrentUser(userInfo -> currentUser = userInfo);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    private void start(){
//        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isRunningThread = true;
        new Thread(()->{
            while (isRunningThread){
                Message msg = new Message();

                if(isRunning) msg.what = Constants.LOCATION_UPDATE_STOP;
                else msg.what = Constants.LOCATION_UPDATE_START;

                handler.sendMessage(msg);

                try {
                    Thread.sleep(UPDATE_TIME_GAP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stop(){
        isRunningThread = false;
        locationManager.removeUpdates(this);
    }

    /**
     * locationManager에 달리는 리스너 정의
     * - onlocationchange가 거리가 바뀔때 호출된다. (stop 하게 되면 호출되지 않는다.)
     */
    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        CurrentLocation.setLatitude(lat);
        CurrentLocation.setLongitude(lng);

        if(currentUser != null) currentUser.updateLocation(lat, lng);

        Log.d("onLocationChanged", "lat:" + lat + " / lng:" + lng);
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.LOCATION_UPDATE_START:
                    startService();
                    break;
                case Constants.LOCATION_UPDATE_STOP:
                    stopService();
                    break;
            }
        }
    };

    /**
     * 내 위치를 자동으로 업데이트 해주는 메소드 (시작, 끝을 선언해준다,)
     * - GPS 혹은 Network가 켜져 있어야 실행
     * - 시간은 milisecond 단위, 거리는 meter단위로 넣어준다. (Const에 지정해야 할듯)
     */
    @SuppressLint("MissingPermission")
    private void startService(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.LOCATION_INTERVAL_TIME, Constants.LOCATION_INTERVAL_METER, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,Constants.LOCATION_INTERVAL_TIME, Constants.LOCATION_INTERVAL_METER, this);
        isRunning = true;
    }
    private void stopService(){
        locationManager.removeUpdates(this);
        isRunning = false;
    }
}
