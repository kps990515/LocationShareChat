package android.daehoshin.com.locationsharechat.room;

import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
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
import android.view.View;
import android.widget.EditText;

import java.util.List;

import static android.daehoshin.com.locationsharechat.constant.Consts.ROOM_ID;

public class RoomActivity extends AppCompatActivity {

    RecyclerView chatList;
    EditText edit_msg;
//    FirebaseDatabase database;
//    DatabaseReference msgRef;
    ChatAdapter adapter;

    private UserInfo currentUser;
    private Room currentRoom;
    private String roomid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
//        database = FirebaseDatabase.getInstance();

        roomid = getIntent().getStringExtra(ROOM_ID);

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

                        //msgRef = DatabaseManager.getMsgRef(roomid);
                        initView();
                        currentRoom.getMsg(new Room.IRoomMsgCallback() {
                            @Override
                            public void getMsg(Msg msg) {
                                adapter.addMsg(msg);
                            }
                        });

                        currentRoom.getMember(new Room.IRoomMemberCallback() {
                            @Override
                            public void getMember(List<Member> members) {
                                for(Member member : members){
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
                });
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
            msg.setIdx(adapter.getItemCount());
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

    private void initView(){
        chatList = findViewById(R.id.chatList);
        edit_msg = findViewById(R.id.edit_msg);
        adapter = new ChatAdapter(currentUser.getUid());
        chatList.setAdapter(adapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));
    }
}
