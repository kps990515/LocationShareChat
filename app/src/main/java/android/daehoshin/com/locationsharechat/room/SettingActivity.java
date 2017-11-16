package android.daehoshin.com.locationsharechat.room;

import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.common.GoogleMapManager;
import android.daehoshin.com.locationsharechat.common.Constants;
import android.daehoshin.com.locationsharechat.custom.CustomMapPopup;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.domain.user.Member;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.daehoshin.com.locationsharechat.util.MarkerUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class SettingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private GoogleMapManager mapManager;
    private Marker roomMarker;
    private Toolbar toolbar;

    private FrameLayout popUpStage;
    private CustomMapPopup popUpCustomMap;

    private UserInfo currentUser;
    private Room currentRoom;
    private String room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        room_id = getIntent().getStringExtra(Constants.ROOM_ID);
        mapManager = new GoogleMapManager(this);
        initMap();
    }
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.settingMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadCurrentUser();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if(marker.getTag() instanceof Room){
                    Room room = (Room)marker.getTag();
                    return room.getInfoView(SettingActivity.this);
                }
                if(marker.getTag() instanceof UserInfo){
                    UserInfo userInfo = (UserInfo)marker.getTag();
                    return MarkerUtil.getInfoMemberView(SettingActivity.this,userInfo.name);
                }
                return null;
            }
        });

        mMap.setOnMapLongClickListener(latLng -> {
                popUpStage.setVisibility(View.VISIBLE);
                mapManager.moveTo(mMap, latLng);

                popUpStage.addView(createCustomMapPopup(latLng, CustomMapPopup.PopupType.UPDATE_TOTAL));
            });
    }

    private void loadCurrentUser(){
        AuthManager.getInstance().getCurrentUser(userInfo -> {
            currentUser = userInfo;
            currentUser.getRoom(room_id, room -> {
                    initView();

                    currentRoom = room;
                    LatLng latLng = new LatLng(Double.parseDouble(currentRoom.getLat()), Double.parseDouble(currentRoom.getLng()));
                    mMap.setOnMapLoadedCallback(() -> mapManager.zoomTo(mMap, latLng, 12));
                    currentRoom.addMarker(mMap);

                    popUpStage.setVisibility(View.VISIBLE);

                    popUpStage.addView(createCustomMapPopup(latLng, CustomMapPopup.PopupType.UPDATE_PARTIAL));
                    loadMember();
                });
        });
    }

    private CustomMapPopup createCustomMapPopup(LatLng latLng, CustomMapPopup.PopupType type){
        if(popUpCustomMap != null) popUpCustomMap.recycle();

        popUpCustomMap = new CustomMapPopup(this
                , room -> {
                            popUpStage.removeAllViews();
                            popUpStage.setVisibility(View.GONE);
                            updateRoom(room);
                  }
                , latLng.latitude
                , latLng.longitude
                , currentRoom
                , mMap
                , type);

        return popUpCustomMap;
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        setPopUpStage();
    }

    private void loadMember(){
        currentRoom.getMember(new Room.IRoomMemberCallback() {
            @Override
            public void getMember(List<Member> members) {
                for(Member member : members){
                    if(currentUser.getUid().equals(member.getUid())) currentUser.addMarker(mMap);
                    else member.addMarker(mMap);
                }
            }
        });
    }

    private void updateRoom(Room room){
        if(room == null) return;
        roomMarker = room.addMarker(mMap);
        roomMarker.showInfoWindow();
    }

    private void setPopUpStage() {
        popUpStage = findViewById(R.id.popUpStage);
        popUpStage.setVisibility(View.GONE);

        popUpStage.setOnClickListener(v -> {
            popUpStage.setVisibility(View.GONE);
            popUpStage.removeAllViews();
        });
    }

    /**
     * Appbar 메뉴 생성
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    /**
     * Appbar 메뉴 선택 이벤트
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_close:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
