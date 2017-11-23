package a.daehoshin.com.locationsharechat.domain.user;

import a.daehoshin.com.locationsharechat.common.DatabaseManager;
import a.daehoshin.com.locationsharechat.domain.room.Room;
import a.daehoshin.com.locationsharechat.util.MarkerUtil;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

    @Exclude
    private boolean realtimeRunning = false;
    @Override
    void realtimeRefresh() {
        if(realtimeRunning) return;
        DatabaseManager.getUserRef(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo m = dataSnapshot.getValue(UserInfo.class);
                if(m != null) {
                    lat = m.getLat();
                    lng = m.getLng();
                }

                for(Marker marker : markers) {
                    if (lat != null && lng != null) {
                        marker.setPosition(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UserInfo", databaseError.getMessage());
            }
        });
        realtimeRunning = true;
    }

    @Exclude
    private List<Marker> markers = new ArrayList<>();
    @Exclude
    public Marker addMarker(GoogleMap googleMap){
        realtimeRefresh();
        Marker marker = googleMap.addMarker(MarkerUtil.createMarkerOptions(this));
        marker.setTag(this);
        markers.add(marker);
        return marker;
    }

    @Exclude
    public String[] getRoomIds(){
        if("".equals(room) || room == null) return new String[]{};

        return room.split(",");
    }

    @Exclude
    public void updateLocation(double lat, double lng){
        this.lat = lat + "";
        this.lng = lng + "";
        save();

        for(String roomid : getRoomIds()){
            Member member = new Member(this, roomid);
            DatabaseManager.getMemberRef(roomid, uid).setValue(member);
        }
    }

    public void addRoom(Room newRoom){
        if(room == null) room = "";
        if(room.length() > 0) room += ",";
        if(room.indexOf(newRoom.getId()) >= 0) return;
        room += newRoom.getId();

        rooms.add(newRoom);
    }

    public void removeRoom(String roomId){
        room = room.replace(roomId, "");
        room = room.replace(",,", ",");
        if(",".equals(room)) room = "";
    }

    @Exclude
    private List<Room> rooms = new ArrayList<>();

    public void getRoom(String roomId, IGetRooms callback){
        for(Room room : rooms){
            if(room.getId().equals(roomId)) {
                callback.callback(room);
                return;
            }
        }

        DatabaseManager.getRoomRef(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                rooms.add(room);
                callback.callback(room);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.callback(null);
            }
        });
    }

    public interface IGetRooms{
        void callback(Room room);
    }
}
