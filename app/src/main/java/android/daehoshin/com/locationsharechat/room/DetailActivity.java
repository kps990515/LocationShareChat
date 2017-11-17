package android.daehoshin.com.locationsharechat.room;

import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.common.Constants;
import android.daehoshin.com.locationsharechat.common.GoogleMapManager;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private GoogleMapManager mapManager;
    private Marker roomMarker;
    private Toolbar toolbar;

    private UserInfo currentUser;
    private Room currentRoom;
    private String room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();

        room_id = getIntent().getStringExtra(Constants.ROOM_ID);
        mapManager = new GoogleMapManager(this);

        initMap();
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detailMap);
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
                    return room.getInfoView(DetailActivity.this);
                }
                if(marker.getTag() instanceof UserInfo){
                    UserInfo userInfo = (UserInfo)marker.getTag();
                    return MarkerUtil.getInfoMemberView(DetailActivity.this,userInfo.name);
                }
                return null;
            }
        });
    }

    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private void loadCurrentUser(){
        AuthManager.getInstance().getCurrentUser(userInfo -> {
            currentUser = userInfo;

            currentUser.getRoom(room_id, room -> {
                    currentRoom = room;
                    LatLng latLng = new LatLng(Double.parseDouble(currentRoom.getLat()), Double.parseDouble(currentRoom.getLng()));
                    mapManager.zoomTo(mMap, latLng, 12);
                    roomMarker = currentRoom.addMarker(mMap);
                    roomMarker.showInfoWindow();

                    builder.include(roomMarker.getPosition());
                    initView();
                    loadMember();
            });
        });
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    private void loadMember(){
        currentRoom.getMember(members -> {
            for(int i = 0 ; i < members.size(); i++){
                Member member = members.get(i);
                Marker marker;

                if(currentUser.getUid().equals(member.getUid())) marker = currentUser.addMarker(mMap);
                else marker = member.addMarker(mMap);

                builder.include(marker.getPosition());
            }

            zoomToMembers();
        });
    }

    private void zoomToMembers(){
        LatLngBounds bounds = builder.build();
        mMap.setMaxZoomPreference(18.0f);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 140);
        mMap.setOnMapLoadedCallback(() -> mMap.moveCamera(cu));
    }

    /**
     * Appbar 메뉴 생성
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.action_close:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
