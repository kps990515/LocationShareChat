package android.daehoshin.com.locationsharechat.room;

import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.common.MapManager;
import android.daehoshin.com.locationsharechat.constant.Consts;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.domain.user.Member;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class SettingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapManager mapManager;
    private Marker roomMarker;
    private Toolbar toolbar;

    private UserInfo currentUser;
    private Room currentRoom;
    private String room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        room_id = getIntent().getStringExtra(Consts.ROOM_ID);
        mapManager = new MapManager(this,2);
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
                return null;
            }
        });
    }

    private void loadCurrentUser(){
        AuthManager.getInstance().getCurrentUser(new AuthManager.IAuthCallback() {
            @Override
            public void signinAnonymously(boolean isSuccessful) {

            }

            @Override
            public void getCurrentUser(UserInfo userInfo) {
                currentUser = userInfo;
                currentUser.getRoom(room_id, new UserInfo.IUserInfoCallback() {
                    @Override
                    public void getRoom(Room room) {
                        currentRoom = room;
                        LatLng latLng = new LatLng(Double.parseDouble(currentRoom.getLat()), Double.parseDouble(currentRoom.getLng()));
                        mapManager.moveCameraLocationZoom(mMap, latLng, 12);

                        initView();
                        loadMember();
                    }
                });
            }
        });
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    private void loadMember(){
        currentRoom.getMember(new Room.IRoomMemberCallback() {
            @Override
            public void getMember(List<Member> members) {
                for(Member member : members){
                    if(currentUser.getUid().equals(member.getUid())) mMap.addMarker(currentUser.getMarker());
                    else mMap.addMarker(member.getMarker());
                }
            }
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
