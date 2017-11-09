package android.daehoshin.com.locationsharechat.domain.user;

import android.daehoshin.com.locationsharechat.common.StorageManager;
import android.net.Uri;

import com.google.firebase.database.Exclude;

/**
 * Created by daeho on 2017. 11. 8..
 * 사용자 Class
 */
abstract class BaseUser {
    protected String uid;
    protected String name;
    protected String let;
    protected String lan;

    public BaseUser(){

    }

    /**
     * firebase database에 저장
     */
    @Exclude
    abstract void save();

    @Exclude
    public void uploadProfile(Uri profileUri){
        StorageManager.uploadProfile(uid, profileUri, new StorageManager.IUploadCallback() {
            @Override
            public void uploaded(boolean isSuccess, Uri uri) {

            }
        });
    }

    @Exclude
    public void getProfile(StorageManager.IDownloadCallback callback){
        StorageManager.downloadProfile(uid, callback);
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

}
