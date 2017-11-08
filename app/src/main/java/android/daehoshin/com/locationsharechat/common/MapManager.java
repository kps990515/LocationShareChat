package android.daehoshin.com.locationsharechat.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by daeho on 2017. 11. 7..
 */

public class MapManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mapGoogleApiClient;
    private LocationManager locationManager;
    Context context;

    public MapManager(FragmentActivity fragmentActivity){
        setMapGoogleApiClient(fragmentActivity);
        context = fragmentActivity;
    }

    public void loadMyLocation(){

    }

    // GoogleApiClient를 초기화 (Map과 관련된 것)
    private void setMapGoogleApiClient(FragmentActivity fragmentActivity){
        // 사용하기 위해서 google-playservice gradle에 추가해야함
        mapGoogleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity,this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API) // 자동완성, palce 상세정보 검색 등의 기능
                .addApi(Places.PLACE_DETECTION_API) // 가장 최근 android device가 있었던 place의 정보를 얻음
                .build();
        conControlGoogleApiClient(true);
    }
    // GoogleApiClient에 대한 연결 및 해제 요청
    public void conControlGoogleApiClient(boolean connect){
        if(connect)
            mapGoogleApiClient.connect();
        else
            mapGoogleApiClient.disconnect();
    }
    // GoogleApi 연결 실패시 진행
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    // GoogleApi 연결 성공시 진행
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
     * GoogleMap에서 디바이스가 위치하는 곳으로 이동을 가능하게 하는 메소드
     // 퍼미션을 미리 받을 것이기 때문에 권한체크 제외
     * @param mMap
     */
    @SuppressLint("MissingPermission")
    public void setMyLocation(GoogleMap mMap){
        if(mMap == null){
            return;
        }
        mMap.setMyLocationEnabled(true); // 내위치 이동 가능하게 함
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // 현재 위치로 이동하는 버튼 활성화
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                // GPS 설정 유무에 따라 현재위치로 이동 메소드 혹은 GPS 요청 popup창 띄우는 메소드 넣을 것
                Toast.makeText(context, "Click!!!!!!!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
