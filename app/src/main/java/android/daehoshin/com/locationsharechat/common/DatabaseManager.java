package android.daehoshin.com.locationsharechat.common;

import android.app.Activity;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by daeho on 2017. 11. 7..
 */

public class DatabaseManager {

    private static DatabaseManager databaseManager;
    public static DatabaseManager getInstance(){
        if(databaseManager == null) databaseManager = new DatabaseManager();

        return databaseManager;
    }

    public static DatabaseReference getUserRef(){
        return getInstance().userRef;
    }
    public static DatabaseReference getUserRef(String uid){
        return getInstance().database.getReference(Constants.TB_USER + "/" + uid);
    }

    public static DatabaseReference getMemberRef(String roomid){
        return getInstance().database.getReference(Constants.TB_MEMBER + "/" + roomid);
    }
    public static DatabaseReference getMemberRef(String roomid, String uid){
        return getInstance().database.getReference(Constants.TB_MEMBER + "/" + roomid + "/" + uid);
    }

    public static DatabaseReference getRoomRef(){
        return getInstance().roomRef;
    }
    public static DatabaseReference getRoomRef(String roomid){
        return getInstance().database.getReference(Constants.TB_ROOM + "/" + roomid);
    }

    public static DatabaseReference getMsgRef(String roomid){
        return getInstance().database.getReference(Constants.TB_MSG + "/" + roomid);
    }
    public static DatabaseReference getMsgRef(String roomid, long idx){
        return getInstance().database.getReference(Constants.TB_MSG + "/" + roomid + "/" + idx);
    }

    public static void leaveRoom(UserInfo userInfo, String roomid){
        userInfo.getRoom(roomid, room -> {
            userInfo.removeRoom(roomid);
            userInfo.save();
            getMemberRef(roomid, userInfo.getUid()).removeValue();

            getMemberRef(roomid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() == 0) delete(room);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });

    }
    public static void delete(Room room){
        getRoomRef(room.getId()).removeValue();
        getMemberRef(room.getId()).removeValue();
        getMsgRef(room.getId()).removeValue();
    }
    public static void delete(final Activity activity, UserInfo userInfo){
        getUserRef(userInfo.getUid()).removeValue();
        for(final String roomid : userInfo.getRoomIds()){
            getMemberRef(roomid, userInfo.getUid()).removeValue();
            final String temp = roomid;
            getMemberRef(temp).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() == 0){
                        getRoomRef(temp).removeValue();
                        getMemberRef(temp).removeValue();
                        getMsgRef(temp).removeValue();
                    }
                    activity.finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private DatabaseReference roomRef;

    private DatabaseManager(){
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(Constants.TB_USER);
        roomRef = database.getReference(Constants.TB_ROOM);
    }
}
