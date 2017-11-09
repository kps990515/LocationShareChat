package android.daehoshin.com.locationsharechat.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Map;

/**
 * 위치 정보에 대한 것을 관리하는 Manager (LocationManager는 이미 있어서 다시 정의)
 * - 아마 필요하지 않을수도..... (서비스시?? 엑티비티 사용 안할때 사용??? 일단 생각...)
 */

public class CustomLocationManager implements LocationListener {

    private LocationManager locationManager;
    private Location lastLocation;
    private double lastLat;
    private double lastLan;

    Map map = null;


    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    public CustomLocationManager(Context context){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * 내 위치를 자동으로 업데이트 해주는 메소드 (시작, 끝을 선언해준다,)
     * - GPS 혹은 Network가 켜져 있어야 실행
     * - 시간은 milisecond 단위, 거리는 meter단위로 넣어준다. (Const에 지정해야 할듯)
     */
    @SuppressLint("MissingPermission")
    public void stratUpdateLocation(){
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(gps_enabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 1, this);
        } else if(network_enabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 1, this);
        } else {
            // popup 으로 GPS 혹은 network 설정할지 요청?? 안하면 start 하지 않음
        }
    }
    public void stopUpdateLocation(){
        locationManager.removeUpdates(this);
    }

    /**
     * locationManager에 달리는 리스너 정의
     * - onlocationchange가 거리가 바뀔때 호출된다. (stop 하게 되면 호출되지 않는다.)
     */
    @Override
    public void onLocationChanged(Location location) {
        lastLat = location.getLatitude();
        lastLan = location.getLongitude();
        lastLocation = location;

        AuthManager.getInstance().getCurrentUser(new AuthManager.IAuthCallback() {
            @Override
            public void signinAnonymously(boolean isSuccessful) {

            }

            @Override
            public void getCurrentUser(UserInfo userInfo) {
                userInfo.setLat(lastLat + "");
                userInfo.setLng(lastLan + "");
                userInfo.save();
            }
        });
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
}
