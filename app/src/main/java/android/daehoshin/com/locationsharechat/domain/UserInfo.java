package android.daehoshin.com.locationsharechat.domain;

import android.daehoshin.com.locationsharechat.common.DatabaseManager;

import com.google.firebase.database.Exclude;

/**
 * Created by daeho on 2017. 11. 8..
 * 사용자 Class
 */
public class UserInfo {
    private String uid;
    private String name;
    private String let = "";
    private String lan = "";
    private String profile_filename = "";

    public UserInfo(){

    }
    public UserInfo(String uid){
        this.uid = uid;
    }

    /**
     * firebase database에 저장
     */
    @Exclude
    public void save(){
        DatabaseManager.getUserRef(uid).setValue(this);
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

    public String getProfile_filename() {
        return profile_filename;
    }

    public void setProfile_filename(String profile_filename) {
        this.profile_filename = profile_filename;
    }
}
