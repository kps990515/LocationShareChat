package android.daehoshin.com.locationsharechat.domain.user;

import android.daehoshin.com.locationsharechat.common.DatabaseManager;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.util.MarkerUtil;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by daeho on 2017. 11. 8..
 * 사용자 Class
 */
public class UserInfo extends BaseUser {
    public String room;

    public UserInfo(){

    }

    /**
     * firebase database에 저장
     */
    @Exclude
    public void save(){
        DatabaseManager.getUserRef(uid).setValue(this);
    }

    @Override
    void realtimeRefresh() {
        DatabaseManager.getUserRef(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo m = dataSnapshot.getValue(UserInfo.class);
                if(m != null) {
                    lat = m.getLat();
                    lng = m.getLng();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Exclude
    public MarkerOptions getMarker(){
        return MarkerUtil.createMarkerOptions(this);
    }

    @Exclude
    public String[] getRoomIds(){
        if("".equals(room) || room == null) return new String[]{};

        return room.split(",");
    }

    public void addRoom(String roomId){
        if(room == null) room = "";
        if(room.length() > 0) room += ",";
        room += roomId;
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
