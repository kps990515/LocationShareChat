package android.daehoshin.com.locationsharechat;

import android.Manifest;
import android.content.Intent;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.common.MapManager;
import android.daehoshin.com.locationsharechat.constant.Consts;
import android.daehoshin.com.locationsharechat.custom.CustomMapPopup;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.daehoshin.com.locationsharechat.room.RoomActivity;
import android.daehoshin.com.locationsharechat.service.LocationService;
import android.daehoshin.com.locationsharechat.user.SigninActivity;
import android.daehoshin.com.locationsharechat.util.PermissionUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import static android.daehoshin.com.locationsharechat.constant.Consts.IS_SIGNIN;
import static android.daehoshin.com.locationsharechat.constant.Consts.LOGIN_REQ;
import static android.daehoshin.com.locationsharechat.constant.Consts.PERMISSION_REQ;
import static android.daehoshin.com.locationsharechat.constant.Consts.ROOM_ID;

public class RoomListActivity extends AppCompatActivity implements OnMapReadyCallback, CustomMapPopup.IDelteThis {

    public static final String[] Permission = new String[] {
              Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.ACCESS_COARSE_LOCATION };

    private GoogleMap mMap;
    private PermissionUtil pUtil;

    private FrameLayout progress;

    private FrameLayout popUpStage;
    CustomMapPopup customMapPopup;
    MapManager mapManager;
    Intent serviceIntent;

    private UserInfo currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkDynamicLink();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        progress = findViewById(R.id.progress);
        Toolbar toolbar = findViewById(R.id.toolbar);
        getDelegate().setSupportActionBar(toolbar);

        setPopUpStage();
        mapManager = new MapManager(this,0);
        serviceIntent = new Intent(this, LocationService.class);

        checkPermission();
    }

    private void checkDynamicLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Toast.makeText(RoomListActivity.this, deepLink.getHost(), Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("RoomListActivity", "getDynamicLink:onFailure", e);
                    }
                });
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
                else {
                    currentUser = userInfo;

                    initMap();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case LOGIN_REQ:
                switch (resultCode){
                    case RESULT_OK: checkSignin(); break;
                    case RESULT_CANCELED: finish(); break;
                }
                break;
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        serviceIntent.setAction(Consts.Thread_START);
        startService(serviceIntent);

        mapManager.longClick(mMap, new MapManager.IMakeRoom() {
            @Override
            public void makePopup(LatLng latLng) {
                popUpStage.setVisibility(View.VISIBLE);
                customMapPopup = new CustomMapPopup(RoomListActivity.this,latLng.latitude,latLng.longitude, mMap, Consts.ROOM_CREATE);
                mapManager.moveToClickLocation(mMap,latLng);
                popUpStage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUpStage.setVisibility(View.GONE);
                        customMapPopup.deletePopUpMarker();
                    }
                });
                popUpStage.addView(customMapPopup);
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                if(marker.getTag() instanceof Room){
                    Room room = (Room)marker.getTag();
                    return room.getInfoView(RoomListActivity.this);
                }
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTag() instanceof Room) {
                    Room room = (Room) marker.getTag();
                    Intent intent = new Intent(RoomListActivity.this, RoomActivity.class);
                    intent.putExtra(ROOM_ID, room.id);
                    startActivity(intent);
                }
            }
        });

        addUser(currentUser);
        loadData();

        progress.setVisibility(View.GONE);

        findViewById(R.id.appBarLayout).setVisibility(View.VISIBLE);
    }

    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private void loadData(){
        AuthManager.getInstance().getCurrentUser(new AuthManager.IAuthCallback() {
            @Override
            public void signinAnonymously(boolean isSuccessful) {

            }

            @Override
            public void getCurrentUser(UserInfo userInfo) {
                currentUser = userInfo;
                final String roomIds[] = currentUser.getRoomIds();
                if(roomIds.length == 0) mapManager.moveToMyLocation(mMap);
                else {
                    for (int i = 0; i < roomIds.length; i++) {
                        final int finalI = i;
                        currentUser.getRoom(roomIds[i], new UserInfo.IUserInfoCallback() {
                            @Override
                            public void getRoom(Room room) {
                                addRoom(room);
                                if (finalI == roomIds.length - 1) {
                                    LatLngBounds bounds = builder.build();
                                    mMap.setMaxZoomPreference(18.0f);
                                    final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 90);
                                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                        @Override
                                        public void onMapLoaded() {
                                            mMap.animateCamera(cu, 600, null);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void addRoom(Room room){
        if(room == null) return;
        Marker marker = room.addMarker(mMap);
        marker.showInfoWindow();
        builder.include(marker.getPosition());
    }

    private void addUser(UserInfo userInfo){
        if(userInfo == null) return;
        Marker marker = userInfo.addMarker(mMap);
        marker.showInfoWindow();
    }

    @Override
    public void deletePopUp(Room room, Marker PopUpMarker) {
        popUpStage.removeView(customMapPopup);
        popUpStage.setVisibility(View.GONE);
        PopUpMarker.remove();
        addRoom(room);
    }

    private void setPopUpStage(){
        popUpStage = findViewById(R.id.popUpStage);
        popUpStage.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.roomlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menu = item.getItemId();
        switch(menu){
            case R.id.menu_profile:
                Intent intent = new Intent(this, SigninActivity.class);
                intent.putExtra(IS_SIGNIN, false);
                startActivity(intent);
                break;
            case R.id.menu_signout:
                AuthManager.getInstance().signout();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }







    //======== 임시용 버튼(삭제 할 것) 생성=================================


    boolean checkService = false;
    public void onService(View view){
        if(checkService){
            serviceIntent.setAction(Consts.Thread_START);
            startService(serviceIntent);
            checkService = false;
        } else{
            stopService(serviceIntent);
            checkService = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapManager.conControlGoogleApiClient(this,true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapManager.conControlGoogleApiClient(this,false);
    }
}
