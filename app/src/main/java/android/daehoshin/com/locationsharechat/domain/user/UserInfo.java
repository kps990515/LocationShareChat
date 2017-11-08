package android.daehoshin.com.locationsharechat.domain.user;

import android.daehoshin.com.locationsharechat.common.DatabaseManager;
import android.daehoshin.com.locationsharechat.domain.room.Room;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by daeho on 2017. 11. 8..
 * 사용자 Class
 */
public class UserInfo extends BaseUser {
    private String room;

    public UserInfo(){

    }

    /**
     * firebase database에 저장
     */
    @Exclude
    public void save(){
        DatabaseManager.getUserRef(uid).setValue(this);
    }

    public String[] getRoomIds(){
        return room.split(",");
    }

    public void addRoom(String roomId){
        if(room.length() > 0) room += ",";
        room += "," + roomId;
    }

    public void getRoom(String roomId, final IUserInfoCallback callback){
        DatabaseManager.getRoomRef(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.getRoom(dataSnapshot.getValue(Room.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.getRoom(null);
            }
        });
    }

    public interface IUserInfoCallback{
        void getRoom(Room room);
    }
}
