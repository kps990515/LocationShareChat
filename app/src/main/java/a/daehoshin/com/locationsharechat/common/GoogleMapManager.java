package a.daehoshin.com.locationsharechat.common;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daeho on 2017. 11. 15..
 */

public class GoogleMapManager {
    private static int assignCount = 0;

    private FragmentActivity fragmentActivity;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private boolean useRealtimeLocation = false;
    public boolean isUseRealtimeLocation() {
        return useRealtimeLocation;
    }

    private double lat;
    public double getLat() {
        return lat;
    }
    private double lng;
    public double getLng() {
        return lng;
    }


    /**
     * location 변경될때 실행해주는 callback들 설정
     */
    private List<ILocationChanged> iLocationChangeds = new ArrayList<>();
    public void addLocationChanged(ILocationChanged iLocationChanged){
        iLocationChangeds.add(iLocationChanged);
    }
    public void removeLocationChanged(ILocationChanged iLocationChanged){
        iLocationChangeds.remove(iLocationChanged);
    }



    public GoogleMapManager(FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;

        init();
    }
    public GoogleMapManager(Fragment fragment){
        this.fragmentActivity = (FragmentActivity) fragment.getActivity();

        init();
    }

    private void init(){
        setMapGoogleApiClient();
        setLocationRequest();

        Location loc = getLastLocation();
        if(loc != null) {
            lat = loc.getLatitude();
            lng = loc.getLongitude();
        }
    }

    /**
     * GoogleApiClient 초기화 메소드 및 연결관련 메소드 설정
     * 사용하기 위해서 google-playservice gradle에 추가해야함
     */
    private void setMapGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, assignCount++, connectionFailedListener)
                .addConnectionCallbacks(connectionCallbacks)
                .addApi(LocationServices.API) // 현재 위치(좌표) 가져오기 등 기능
                .addApi(Places.GEO_DATA_API) // 자동완성, palce 상세정보 검색 등 기능
                .addApi(Places.PLACE_DETECTION_API) // 가장 최근 android device가 있었던 place(장소?_건물)의 정보를 얻음
                .build();
    }

    public void connect(){
        if(!googleApiClient.isConnected()) googleApiClient.connect();
    }
    public void disconnect(){
        if(googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(fragmentActivity);
            googleApiClient.disconnect();
        }
    }

    /**
     * location의 업데이트 설정값(Const에 값들을 설정해야 할듯)
     */
    private void setLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.LOCATION_INTERVAL_TIME);
        locationRequest.setFastestInterval(Constants.LOCATION_INTERVAL_TIME);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * GoogleMap 에서 디바이스가 위치하는 곳으로 이동을 가능하게 세팅하는 메소드
     * - 기본적으로 제공해주는 메소드
     * @param googleMap
     */
    @SuppressLint("MissingPermission")
    public void setMyLocation(GoogleMap googleMap){
        googleMap.setMyLocationEnabled(true); // 내위치를 찍고, 버튼을 사용 가능하게 함
        googleMap.getUiSettings().setMyLocationButtonEnabled(true); // 현재 위치로 이동하는 버튼 활성화=
//        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//            @Override
//            public boolean onMyLocationButtonClick() {
//                // GPS 설정 유무에 따라 현재위치로 이동 메소드 혹은 GPS 요청 popup창 띄우는 메소드 넣을 것
//                return false;
//            }
//        });
    }

    /**
     * 내 위치 찾는 메소드 실행 / 실행 중지
     * - 퍼미션을 미리 받을 것이기 때문에 권한체크 제외(어노테이션)
     */
    @SuppressLint("MissingPermission")
    public void setRealtimeLocation(boolean use){
        if(useRealtimeLocation == use) return;

        if(use) LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        else LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);

        useRealtimeLocation = use;
    }

    @SuppressLint("MissingPermission")
    public Location getLastLocation(){
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    public LatLng getLatLng(){
        return new LatLng(lat, lng);
    }







    public void zoomTo(GoogleMap googleMap, LatLng latLng, float zoomSize){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomSize));
    }
    public void zoomToLocation(GoogleMap googleMap, float zoomSize){
        zoomTo(googleMap, getLatLng(), zoomSize);
    }
    public void moveTo(GoogleMap googleMap, LatLng latLng){
        CameraUpdate cu = CameraUpdateFactory.newLatLng(latLng);
        googleMap.animateCamera(cu, 400, null);
    }
    public void moveToLocation(GoogleMap googleMap){
        moveTo(googleMap, getLatLng());
    }



    /**
     * 연결 실패시 발생
     */
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    };

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int cause) {
            if (cause == CAUSE_NETWORK_LOST) // 네트워크 에러
                Log.e("google play service", ": Google Play services " +
                        "connection lost.  Cause: network lost.");
            else if (cause == CAUSE_SERVICE_DISCONNECTED) // 서비스가 disconnect
                Log.e("google play service", "onConnectionSuspended():  Google Play services " +
                        "connection lost.  Cause: service disconnected");
        }
    };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lng = location.getLongitude();

            for(ILocationChanged l : iLocationChangeds) l.callback(location);
        }
    };

    public interface ILocationChanged{
        void callback(Location location);
    }
}
