package android.daehoshin.com.locationsharechat.domain.user;

import android.daehoshin.com.locationsharechat.common.DatabaseManager;
import android.daehoshin.com.locationsharechat.util.MarkerUtil;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daeho on 2017. 11. 8..
 */

public class Member extends BaseUser {
    public String id;
    public String msg_read;

    public Member(){

    }

    public Member(UserInfo userInfo, String roomid){
        uid = userInfo.uid;
        name = userInfo.name;
        lat = userInfo.lat;
        lng = userInfo.lng;

        id = roomid;
    }

    @Override
    public void save() {
        DatabaseManager.getMemberRef(id, uid).setValue(this);
    }

    @Exclude
    private boolean realtimeRunning = false;
    @Exclude
    private DatabaseReference realtimeRef = null;
    @Override
    void realtimeRefresh() {
        if(realtimeRunning) return;
        if(realtimeRef == null) realtimeRef = DatabaseManager.getUserRef(uid);
        realtimeRef.addValueEventListener(realtimeListener);

        realtimeRunning = true;
    }
    @Exclude
    private ValueEventListener realtimeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Member m = dataSnapshot.getValue(Member.class);
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

        }
    };

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
    public void removeMarker(){
        realtimeRunning = false;
        realtimeRef.removeEventListener(realtimeListener);

        for(Marker marker : markers) marker.remove();
        markers.clear();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg_read() {
        return msg_read;
    }

    public void setMsg_read(String msg_read) {
        this.msg_read = msg_read;
    }
}
