package a.daehoshin.com.locationsharechat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import a.daehoshin.com.locationsharechat.common.AuthManager;
import a.daehoshin.com.locationsharechat.common.Constants;
import a.daehoshin.com.locationsharechat.common.DatabaseManager;
import a.daehoshin.com.locationsharechat.common.GoogleMapManager;
import a.daehoshin.com.locationsharechat.custom.CustomMapPopup;
import a.daehoshin.com.locationsharechat.domain.room.Room;
import a.daehoshin.com.locationsharechat.domain.user.Member;
import a.daehoshin.com.locationsharechat.domain.user.UserInfo;
import a.daehoshin.com.locationsharechat.room.RoomActivity;
import a.daehoshin.com.locationsharechat.service.LocationService;
import a.daehoshin.com.locationsharechat.user.SigninActivity;
import a.daehoshin.com.locationsharechat.util.MarkerUtil;
import a.daehoshin.com.locationsharechat.util.PermissionUtil;


public class RoomListActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String[] Permission = new String[] {
              Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.ACCESS_COARSE_LOCATION };
    private PermissionUtil pUtil;

    private GoogleMap mMap;
    private GoogleMapManager mapManager;

    private FrameLayout progress;

    private FrameLayout popUpStage;
    private CustomMapPopup popUpCustomMap;

    private Intent serviceIntent;

    private UserInfo currentUser;

    private boolean useInvite = false;
    private String inviteRoomId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        progress = findViewById(R.id.progress);
        Toolbar toolbar = findViewById(R.id.toolbar);
        getDelegate().setSupportActionBar(toolbar);

        setPopUpStage();
        mapManager = new GoogleMapManager(this);
        serviceIntent = new Intent(this, LocationService.class);

        checkPermission();


    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);

        super.onDestroy();
    }

    private void checkDynamicLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();

                        if(deepLink.getPathSegments().size() == 2){
                            switch (deepLink.getPathSegments().get(0)){
                                case "invite":
                                    useInvite = true;
                                    inviteRoomId = deepLink.getPathSegments().get(1);

                                    if(mMap != null) setInvite();

                                    break;
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.d("RoomListActivity", "getDynamicLink:onFailure", e));
    }

    /**
     * 권한 체크
     */
    private void checkPermission(){
        pUtil = new PermissionUtil(Constants.PERMISSION_REQ, Permission);
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
        AuthManager.getInstance().getCurrentUser(userInfo -> {
            if(userInfo == null) {
                Intent intent = new Intent(RoomListActivity.this, SigninActivity.class);
                startActivityForResult(intent, Constants.LOGIN_REQ);
            }
            else {
                currentUser = userInfo;
                initMap();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Constants.LOGIN_REQ:
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
        serviceIntent.setAction(Constants.Thread_START);
        startService(serviceIntent);

        checkDynamicLink();

        mapManager.zoomTo(mMap, new LatLng(Double.parseDouble(currentUser.getLat()), Double.parseDouble(currentUser.getLng())), Constants.Zoom_SIZE);

        mMap.setOnMapLongClickListener(latLng -> {
            popUpStage.setVisibility(View.VISIBLE);

            mapManager.moveTo(mMap, latLng);

            popUpStage.setOnClickListener(v -> popUpStage.setVisibility(View.GONE));

            popUpStage.addView(createCustomMapPopup(latLng, CustomMapPopup.PopupType.CREATE));
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                if(marker.getTag() instanceof Room){
                    Room room = (Room)marker.getTag();
                    return room.getInfoView(RoomListActivity.this);
                }
                if(marker.getTag() instanceof UserInfo){
                    UserInfo userInfo = (UserInfo)marker.getTag();
                    return MarkerUtil.getInfoMemberView(RoomListActivity.this,userInfo.name);
                }
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        mMap.setOnInfoWindowClickListener(marker -> {
                if(marker.getTag() instanceof Room) {
                    Room room = (Room) marker.getTag();
                    showRoom(room.getId());
            }
        });

        addUser(currentUser);

        if(useInvite) setInvite();
        else loadData();

        mapManager.setMyLocation(mMap);
        progress.setVisibility(View.GONE);

        findViewById(R.id.appBarLayout).setVisibility(View.VISIBLE);
    }

    private void setInvite(){
        DatabaseManager.getRoomRef(inviteRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Room inviteRoom = dataSnapshot.getValue(Room.class);
                Member member = new Member(currentUser, inviteRoomId);
                member.save();

                currentUser.addRoom(inviteRoom);
                loadData();

                showRoom(inviteRoomId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private CustomMapPopup createCustomMapPopup(LatLng latLng, CustomMapPopup.PopupType type){
        if(popUpCustomMap != null) popUpCustomMap.recycle();

        popUpCustomMap = new CustomMapPopup(this
                , room -> {
                    popUpStage.setVisibility(View.GONE);
                    addRoom(room);
                }
                ,latLng.latitude
                ,latLng.longitude
                , mMap
                , CustomMapPopup.PopupType.CREATE);

        return popUpCustomMap;
    }

    private void showRoom(String roomId){
        Intent intent = new Intent(RoomListActivity.this, RoomActivity.class);
        intent.putExtra(Constants.ROOM_ID, roomId);
        startActivity(intent);
    }

    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private void loadData(){
        AuthManager.getInstance().getCurrentUser(userInfo -> {
            currentUser = userInfo;
            final String roomIds[] = currentUser.getRoomIds();
            if(roomIds.length == 0) mapManager.moveToLocation(mMap);
            else {
                for (int i = 0; i < roomIds.length; i++) {
                    final int finalI = i;
                    currentUser.getRoom(roomIds[i], room -> {
                        addRoom(room);

                        if (finalI == roomIds.length - 1) {
                            LatLngBounds bounds = builder.build();
                            mMap.setMaxZoomPreference(18.0f);
                            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 90);
                            mMap.setOnMapLoadedCallback(() -> mMap.animateCamera(cu, 600, null));
                        }
                    });
                }
            }
        });
    }

    private void addRoom(Room room){
        if(room == null) return;
        Marker marker = room.addMarker(mMap);
        builder.include(marker.getPosition());
    }

    private void addUser(UserInfo userInfo){
        if(userInfo == null) return;
        Marker marker = userInfo.addMarker(mMap);
        marker.showInfoWindow();
        builder.include(marker.getPosition());
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
                intent.putExtra(Constants.IS_SIGNIN, false);
                startActivity(intent);
                break;
            case R.id.menu_signout:
                AuthManager.getInstance().signout(this);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapManager.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapManager.disconnect();
    }
}
