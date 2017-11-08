package android.daehoshin.com.locationsharechat;

import android.Manifest;
import android.content.Intent;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.domain.UserInfo;
import android.daehoshin.com.locationsharechat.user.SigninActivity;
import android.daehoshin.com.locationsharechat.util.PermissionUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RoomListActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int LOGIN_REQ = 900;
    public static final int PERMISSION_REQ = 901;
    public static final String[] Permission = new String[] {
              Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.ACCESS_COARSE_LOCATION
            , android.Manifest.permission.CAMERA
            , android.Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private GoogleMap mMap;
    private PermissionUtil pUtil;

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        checkPermission();
    }

    /**
     * 권한 체크
     */
    private void checkPermission(){
        pUtil = new PermissionUtil(PERMISSION_REQ, Permission);
        pUtil.check(this, new PermissionUtil.IPermissionGrant() {
            @Override
            public void run() {
                checkSignin();
            }

            @Override
            public void fail() {
                finish();
            }
        });
    }

    /**
     * Signin 체크
     */
    private void checkSignin(){
        AuthManager.getInstance().getCurrentUser(new AuthManager.IAuthCallback() {
            @Override
            public void signinAnonymously(boolean isSuccessful) {

            }

            @Override
            public void getCurrentUser(UserInfo userInfo) {
                if(userInfo == null) {
                    Intent intent = new Intent(RoomListActivity.this, SigninActivity.class);
                    startActivityForResult(intent, LOGIN_REQ);
                }
                else initMap();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case LOGIN_REQ:
                switch (resultCode){
                    case RESULT_OK: initMap(); break;
                    case RESULT_CANCELED: finish(); break;
                }
                break;
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progress.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
