package android.daehoshin.com.locationsharechat.room;

import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.common.DatabaseManager;
import android.daehoshin.com.locationsharechat.domain.room.Msg;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.daehoshin.com.locationsharechat.Const.Consts.room_id;

public class RoomActivity extends AppCompatActivity {

    RecyclerView chatList;
    EditText edit_msg;
    FirebaseDatabase database;
    DatabaseReference msgRef;
    ChatAdapter adapter;

    private UserInfo currentUser;
    private Room currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        database = FirebaseDatabase.getInstance();

        final String roomid = getIntent().getStringExtra(room_id);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                        msgRef = DatabaseManager.getMsgRef(roomid);

                        initView();
                    }
                });
            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {

    }

    private void initView(){
        chatList = findViewById(R.id.chatList);
        edit_msg = findViewById(R.id.edit_msg);
        adapter = new ChatAdapter(currentUser.getUid());
        chatList.setAdapter(adapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Msg> data = new ArrayList<>();
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    Msg msg = item.getValue(Msg.class);

                    data.add(msg);
                }
                adapter.dataRefresh(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
