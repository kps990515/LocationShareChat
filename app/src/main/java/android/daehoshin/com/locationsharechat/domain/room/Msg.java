package android.daehoshin.com.locationsharechat.domain.room;

import android.daehoshin.com.locationsharechat.common.DatabaseManager;

import com.google.firebase.database.Exclude;

/**
 * Created by daeho on 2017. 11. 8..
 */

public class Msg {
    public String id;
    public long idx;
    public String uid;
    public String name;
    public long time;
    public String type;
    public String message;

    public Msg(){

    }

    @Exclude
    public void save(){
        DatabaseManager.getMsgRef(id, idx).setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
