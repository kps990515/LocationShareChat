package android.daehoshin.com.locationsharechat.domain.user;

import android.daehoshin.com.locationsharechat.common.DatabaseManager;

/**
 * Created by daeho on 2017. 11. 8..
 */

public class Member extends BaseUser {
    private String id;
    private String msg_read;

    public Member(){

    }

    @Override
    void save() {
        DatabaseManager.getMemberRef(id).setValue(this);
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
