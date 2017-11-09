package android.daehoshin.com.locationsharechat.domain.room;

import android.daehoshin.com.locationsharechat.common.DatabaseManager;
import android.daehoshin.com.locationsharechat.domain.user.Member;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daeho on 2017. 11. 8..
 */

public class Room {
    private String id;
    private String title;
    private long time;
    private long end_time;
    private String let;
    private String lan;
    private String loc_name;
    private String msg_count;

    public Room(){

    }

    @Exclude
    public void save(){
        if("".equals(id) || id == null) id = DatabaseManager.getRoomRef().push().getKey();

        DatabaseManager.getRoomRef(id).setValue(this);
    }

    public void getMember(final IRoomMemberCallback callback){
        DatabaseManager.getMemberRef(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Member> members = new ArrayList<>();

                for(DataSnapshot item : dataSnapshot.getChildren()) members.add(item.getValue(Member.class));

                callback.getMember(members);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.getMember(new ArrayList<Member>());
            }
        });
    }

    public void getMsg(final IRoomMsgCallback callback){
        DatabaseManager.getMsgRef(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Msg> msgs = new ArrayList<>();

                for(DataSnapshot item : dataSnapshot.getChildren()) msgs.add(item.getValue(Msg.class));

                callback.getMsg(msgs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.getMsg(new ArrayList<Msg>());
            }
        });
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public long getEnd_time() {
        return end_time;
    }
    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }
    public String getLet() {
        return let;
    }
    public void setLet(String let) {
        this.let = let;
    }
    public String getLan() {
        return lan;
    }
    public void setLan(String lan) {
        this.lan = lan;
    }
    public String getLoc_name() {
        return loc_name;
    }
    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }
    public String getMsg_count() {
        return msg_count;
    }
    public void setMsg_count(String msg_count) {
        this.msg_count = msg_count;
    }

    public interface IRoomMemberCallback{
        void getMember(List<Member> members);
    }
    public interface IRoomMsgCallback{
        void getMsg(List<Msg> msgs);
    }
}
