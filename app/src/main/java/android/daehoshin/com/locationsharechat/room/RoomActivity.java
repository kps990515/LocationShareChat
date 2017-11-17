package android.daehoshin.com.locationsharechat.room;

import android.content.Intent;
import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.common.Constants;
import android.daehoshin.com.locationsharechat.common.DatabaseManager;
import android.daehoshin.com.locationsharechat.common.GoogleMapManager;
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
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class RoomActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMapManager mapManager;
    private RecyclerView chatList;
    private EditText edit_msg;
    private ChatAdapter adapter;

    private UserInfo currentUser;
    private Room currentRoom;
    private String roomid;

    private FrameLayout popUpLayout;
    private CustomMemberPopup customMemberPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomid = getIntent().getStringExtra(Constants.ROOM_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        getDelegate().setSupportActionBar(toolbar);

        mapManager = new GoogleMapManager(this);

        initMap();
    }

    private void initMap(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.roomMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        loadCurrentUser();

        mMap.setOnMapClickListener(latLng -> {
            Intent intent = new Intent(RoomActivity.this, DetailActivity.class);
            intent.putExtra(Constants.ROOM_ID, roomid);
            startActivity(intent);

            overridePendingTransition(R.anim.anim_slide_in_top,R.anim.anim_slide_out_bottom);
        });
    }

    private void loadCurrentUser(){
        AuthManager.getInstance().getCurrentUser(userInfo -> {
            currentUser = userInfo;
            currentUser.getRoom(roomid, room -> {
                currentRoom = room;

                getSupportActionBar().setTitle(currentRoom.getTitle());
                LatLng latLng = new LatLng(Double.parseDouble(currentRoom.getLat()), Double.parseDouble(currentRoom.getLng()));

                mMap.setOnMapLoadedCallback(() -> mapManager.zoomTo(mMap, latLng, 12));
                currentRoom.addMarker(mMap);

                initView();
                loadMember();
                setRealtimeMsgLitener();
            });
        });
    }

    private void initView(){
        chatList = findViewById(R.id.chatList);
        edit_msg = findViewById(R.id.edit_msg);

        adapter = new ChatAdapter(currentUser.getUid());
        chatList.setAdapter(adapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        popUpLayout = findViewById(R.id.popUpLayout);
        customMemberPopup = new CustomMemberPopup(this);
    }

    private void loadMember(){
        currentRoom.getMember(members -> {
            for(Member member : members){
                if(currentUser.getUid().equals(member.getUid())) currentUser.addMarker(mMap);
                else member.addMarker(mMap);

                member.getProfile((id, uri) -> {
                    adapter.addProfile(id, uri);
                    customMemberPopup.addMember(id,uri,member.getName());
                });
            }
        });
    }

    private void setRealtimeMsgLitener(){
        currentRoom.getRealtimeMsg(msg -> {
            adapter.addMsg(msg);
            chatList.scrollToPosition(adapter.getItemCount() - 1);
        });
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
    protected void onDestroy() {
        if(currentRoom != null) currentRoom.msgsClear();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(popUpLayout.getVisibility() == View.VISIBLE) popUpLayout.setVisibility(View.GONE);
        else super.onBackPressed();
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
                DatabaseManager.leaveRoom(currentUser, roomid);
                currentRoom.removeMarker();
                finish();
                break;
            case R.id.menu_invite: invite(); break;
            case R.id.menu_member:
                popUpLayout.setVisibility(View.VISIBLE);
                popUpLayout.addView(customMemberPopup);
                customMemberPopup.setVisibility(View.VISIBLE);
                popUpLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUpLayout.setVisibility(View.GONE);
                        popUpLayout.removeView(customMemberPopup);
                        customMemberPopup.setVisibility(View.GONE);
                    }
                });
                break;
            case R.id.menu_setting:
                Intent intent = new Intent(RoomActivity.this, SettingActivity.class);
                intent.putExtra(Constants.ROOM_ID, roomid);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void invite(){
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://test.com/invite/" + currentRoom.getId() + "/"))
                .setDynamicLinkDomain(Constants.DYNAMICLINK_BASE_URL)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLink.getUri())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Uri shortLink = task.getResult().getShortLink();
                        Uri flowchartLink = task.getResult().getPreviewLink();

                        Intent sendIntent = new Intent();
                        String msg = shortLink.toString();
                        sendIntent.setAction(Intent.ACTION_SENDTO);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    } else {
                        // Error
                        // ...
                    }
                });
    }
}
