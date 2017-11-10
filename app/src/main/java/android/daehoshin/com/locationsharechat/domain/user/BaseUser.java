package android.daehoshin.com.locationsharechat.domain.user;

import android.daehoshin.com.locationsharechat.common.StorageManager;
import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by daeho on 2017. 11. 8..
 * 사용자 Class
 */
abstract class BaseUser implements Serializable {
    public String uid;
    public String name;
    public String lat;
    public String lng;

    public BaseUser(){
        realtimeRefresh();
    }

    /**
     * firebase database에 저장
     */
    @Exclude
    abstract void save();

    @Exclude
    abstract void realtimeRefresh();

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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


}
