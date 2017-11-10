package android.daehoshin.com.locationsharechat.room;

import android.content.Intent;
import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.common.DatabaseManager;
import android.daehoshin.com.locationsharechat.common.MapManager;
import android.daehoshin.com.locationsharechat.common.StorageManager;
import android.daehoshin.com.locationsharechat.domain.room.Msg;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.domain.user.Member;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static android.daehoshin.com.locationsharechat.constant.Consts.ROOM_ID;

public class RoomActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapManager mapManager;
    private RecyclerView chatList;
    private EditText edit_msg;
    private ChatAdapter adapter;

    private UserInfo currentUser;
    private Room currentRoom;
    private String roomid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomid = getIntent().getStringExtra(ROOM_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapManager = new MapManager(this,1);

        initMap();
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.roomMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadCurrentUser();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(RoomActivity.this, DetailActivity.class);
                intent.putExtra(ROOM_ID, roomid);
                startActivity(intent);
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
                currentUser.getRoom(roomid, new UserInfo.IUserInfoCallback() {
                    @Override
                    public void getRoom(Room room) {
                        currentRoom = room;
                        LatLng latLng = new LatLng(Double.parseDouble(currentRoom.getLat()), Double.parseDouble(currentRoom.getLng()));
                        mapManager.moveCameraLocationZoom(mMap, latLng, 12);
                        mMap.addMarker(currentRoom.getMarker());

                        initView();
                        loadMember();
                        setRealtimeMsgLitener();
                    }
                });
            }
        });
    }

    private void initView(){
        chatList = findViewById(R.id.chatList);
        edit_msg = findViewById(R.id.edit_msg);
        adapter = new ChatAdapter(currentUser.getUid());
        chatList.setAdapter(adapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadMember(){
        currentRoom.getMember(new Room.IRoomMemberCallback() {
            @Override
            public void getMember(List<Member> members) {
                for(Member member : members){
                    if(currentUser.getUid() == member.getUid()) mMap.addMarker(currentUser.getMarker());
                    else mMap.addMarker(member.getMarker());

                    member.getProfile(new StorageManager.IDownloadCallback() {
                        @Override
                        public void downloaded(String id, Uri uri) {
                            adapter.addProfile(id, uri);
                        }
                    });
                }
            }
        });
    }

    private void setRealtimeMsgLitener(){
        currentRoom.getRealtimeMsg(new Room.IRoomMsgCallback() {
            @Override
            public void getRealtimeMsg(Msg msg) {
                adapter.addMsg(msg);
                chatList.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    public void setSupportActionBar(Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    public void type(View view){

    }

    public void send(View view){
        String text = edit_msg.getText().toString();
        if(text != null && !"".equals(text)) {
            Msg msg = new Msg();
            msg.setId(roomid);
            msg.setIdx(adapter.getItemCount() + 1);
            msg.setUid(currentUser.getUid());
            msg.setName(currentUser.getName());
            msg.setTime(System.currentTimeMillis());
            msg.setType("text");
            msg.setMessage(text);
            msg.save();

            edit_msg.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menu = item.getItemId();
        switch(menu){
            case R.id.menu_getout:
                DatabaseManager.leaveRoom(currentUser,roomid);
                finish();
                break;
            case R.id.menu_invite:
                break;
            case R.id.menu_member:

                break;
            case R.id.menu_setting:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
