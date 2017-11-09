package android.daehoshin.com.locationsharechat.domain.user;

import android.daehoshin.com.locationsharechat.common.DatabaseManager;
import android.daehoshin.com.locationsharechat.util.MarkerUtil;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by daeho on 2017. 11. 8..
 */

public class Member extends BaseUser {
    public String id;
    public String msg_read;

    public Member(){

    }

    @Override
    public void save() {
        DatabaseManager.getMemberRef(id).setValue(this);
    }

    @Override
    void realtimeRefresh() {
        DatabaseManager.getUserRef(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Member m = dataSnapshot.getValue(Member.class);
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
