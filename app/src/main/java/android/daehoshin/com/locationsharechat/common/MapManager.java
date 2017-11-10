package android.daehoshin.com.locationsharechat.common;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.daehoshin.com.locationsharechat.constant.Consts;
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

import java.util.List;

/**
 * Created by daeho on 2017. 11. 7..
 */

public class MapManager implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleApiClient mapGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocation = null;
    // 디폴트로 좌표 값을 지정
    private double lastLat = 37.56;
    private double lastLng = 126.97;

    public MapManager(FragmentActivity fragmentActivity,int assign){
        setMapGoogleApiClient(fragmentActivity, assign);
        setLocationRequest();
    }
    // map이 fragment인 경우의 생성자 (appcompatActivity 혹은 fragmentActivity 내에서만 가능)
    public MapManager(Fragment fragmentMap){
        conControlGoogleApiClient((FragmentActivity) fragmentMap.getActivity(),true);
        setLocationRequest();
    }

    /**
     * GoogleApiClient 초기화 메소드 및 연결관련 메소드 설정
     */
    // GoogleApiClient를 초기화 (Map과 관련된 것)
    private void setMapGoogleApiClient(FragmentActivity fragmentActivity, int assign){
        // 사용하기 위해서 google-playservice gradle에 추가해야함
        if(mapGoogleApiClient == null) {
            mapGoogleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                    .enableAutoManage(fragmentActivity, assign, this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API) // 현재 위치(좌표) 가져오기 등 기능
                    .addApi(Places.GEO_DATA_API) // 자동완성, palce 상세정보 검색 등 기능
                    .addApi(Places.PLACE_DETECTION_API) // 가장 최근 android device가 있었던 place(장소?_건물)의 정보를 얻음
                    .build();
        }
    }

    // GoogleApiClient에 대한 연결 및 해제 요청
    public void conControlGoogleApiClient(FragmentActivity fragmentActivity, boolean connect){
        if(connect) {
            mapGoogleApiClient.connect();
        }
        else {
            mapGoogleApiClient.stopAutoManage(fragmentActivity);
            mapGoogleApiClient.disconnect();
        }
    }

    // GoogleApiClient에 연결 실패시 진행
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    // GoogleApiClient에 연결 성공시 진행
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
    }
    // GoogleApi 연결 지연시 진행
    @Override
    public void onConnectionSuspended(int cause) {
        if (cause == CAUSE_NETWORK_LOST) // 네트워크 에러
            Log.e("google play service", ": Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED) // 서비스가 disconnect
            Log.e("google play service", "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    /**
     * location의 업데이트 설정값(Const에 값들을 설정해야 할듯)
     */
    private void setLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Consts.LOCATION_INTERVAL_TIME);
        mLocationRequest.setFastestInterval(Consts.LOCATION_INTERVAL_TIME);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * 내 위치 찾는 메소드 실행 / 실행 중지
     * - 퍼미션을 미리 받을 것이기 때문에 권한체크 제외(어노테이션)
     */
    @SuppressLint("MissingPermission")
    public void startUpdateMyLocation(){
        LocationServices.FusedLocationApi.requestLocationUpdates(mapGoogleApiClient,mLocationRequest,this);
    }
    public void stopUpdateMyLocation(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mapGoogleApiClient,this);
    }

    /**
     * 내 최근 위치 정보 값을 불러오는 메소드
     * @return
     */
    @SuppressLint("MissingPermission")
    public void refreshMyRecentLocation(){
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mapGoogleApiClient);
        if(lastLocation != null) {
            lastLat = lastLocation.getLatitude();
            lastLng = lastLocation.getLongitude();
        }
    }
    /**
     * 지도에서 내 최근 위치로 이동하는 메소드 (커스텀하게 되면 사용)
     */
    public void moveToMyLocation(GoogleMap mMap){
        refreshMyRecentLocation();
        LatLng latLng;
        if(lastLocation != null){
            latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        } else {
            latLng = new LatLng(lastLat, lastLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Consts.Zoom_SIZE));
    }

    public void moveToClickLocation(GoogleMap mMap, LatLng latLng){
        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        mMap.animateCamera(center, 400, null);
    }
    public void moveCameraLocationZoom(GoogleMap mMap, LatLng latLng, int zoomSize){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomSize));
    }
    public void moveCameraMiddleLocationZoom(GoogleMap mMap, List<LatLng> latLngList, int zoomSize){
        if(latLngList.size()>0) {
            double middleLat = 0;
            double middleLng = 0;
            for (int i = 0; i < latLngList.size(); i++) {
                middleLat += latLngList.get(i).latitude;
                middleLng += latLngList.get(i).longitude;
            }
            middleLat = middleLat / (double)latLngList.size();
            middleLng = middleLng / (double)latLngList.size();
            Log.e("좌표","========================"+middleLat+" // "+middleLng);

            LatLng middleLatLng = new LatLng(middleLat,middleLng);

            moveCameraLocationZoom(mMap,middleLatLng,zoomSize);
        }
    }

    /**
     * location 찾는 리스너 설정 (내 위치 변할때마다 호출됨)
     * - location말고 google api용 리스너
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        lastLat = location.getLatitude();
        lastLng = location.getLongitude();
        lastLocation = location;
    }

    public double getLastLat(){
        return lastLat;
    }
    public double getLastLng(){
        return lastLng;
    }

    // 일단 위랑 중복.... 나중에 빼도 될듯===================//
    public Location getLastLocation(){
        return lastLocation;
    }

    /**
     * GoogleMap 에서 디바이스가 위치하는 곳으로 이동을 가능하게 세팅하는 메소드
     * - 기본적으로 제공해주는 메소드
     * @param mMap
     */
    @SuppressLint("MissingPermission")
    public void setMyLocation(GoogleMap mMap){
        if(mMap == null){
            return;
        }
        mMap.setMyLocationEnabled(true); // 내위치를 찍고, 버튼을 사용 가능하게 함
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // 현재 위치로 이동하는 버튼 활성화
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                // GPS 설정 유무에 따라 현재위치로 이동 메소드 혹은 GPS 요청 popup창 띄우는 메소드 넣을 것
                return false;
            }
        });
    }

    /**
     * 지도 터치 반응 메소드
     * - 팝업을 생성시키는 인터페이스 콜백 구현
     */
    public void longClick(GoogleMap mMap, final IMakeRoom makeRoom){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                makeRoom.makePopup(latLng);
            }
        });
    }
    public interface IMakeRoom{
        public void makePopup(LatLng latLng);
    }
}
