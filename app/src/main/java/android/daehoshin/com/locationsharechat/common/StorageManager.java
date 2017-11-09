package android.daehoshin.com.locationsharechat.common;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.daehoshin.com.locationsharechat.Const.Consts.DIR_PROFILE;

/**
 * Created by daeho on 2017. 11. 8..
 */

public class StorageManager {

    private static StorageManager sm;
    public static StorageManager getInstance(){
        if(sm == null) sm = new StorageManager();

        return sm;
    }

    public static void uploadProfile(String uid, Uri uploadFile, final IUploadCallback callback){
        StorageReference riversRef = sm.stRef.child(DIR_PROFILE + "/" + uid + ".jpg");

        riversRef.putFile(uploadFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        callback.uploaded(true, taskSnapshot.getDownloadUrl());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        callback.uploaded(false, null);
                    }
                });
    }

    public static void downloadProfile(String uid, final IDownloadCallback callback){
        StorageReference riversRef = sm.stRef.child(DIR_PROFILE + "/" + uid + ".jpg");
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callback.downloaded(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.downloaded(null);
            }
        });
    }

    private StorageReference stRef;
    private StorageReference profileRef;
    private StorageManager(){
        stRef = FirebaseStorage.getInstance().getReference();
        profileRef = FirebaseStorage.getInstance().getReference();
    }

    public interface IUploadCallback{
        void uploaded(boolean isSuccess, Uri uri);
    }
    public interface IDownloadCallback{
        void downloaded(Uri uri);
    }
}
