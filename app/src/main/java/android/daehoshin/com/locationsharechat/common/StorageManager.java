package android.daehoshin.com.locationsharechat.common;

import android.content.Context;
import android.daehoshin.com.locationsharechat.util.FormatUtil;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;

/**
 * Created by daeho on 2017. 11. 8..
 */

public class StorageManager {

    private static StorageManager sm;
    public static StorageManager getInstance(){
        if(sm == null) sm = new StorageManager();

        return sm;
    }

    public static void uploadProfile(Context context, String uid, Uri uploadFile){
        StorageReference riversRef = getInstance().stRef.child(Constants.DIR_PROFILE + "/" + uid + ".jpg");

        try {
            uploadFile = FormatUtil.decodeUri(context, uploadFile, 1024);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        riversRef.putFile(uploadFile)
                .addOnSuccessListener(taskSnapshot -> {})
                .addOnFailureListener(exception -> {});
    }
    public static void uploadProfile(Context context, String uid, Uri uploadFile, final IUploadCallback callback){
        StorageReference riversRef = getInstance().stRef.child(Constants.DIR_PROFILE + "/" + uid + ".jpg");

        try {
            uploadFile = FormatUtil.decodeUri(context, uploadFile, 1024);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        riversRef.putFile(uploadFile)
                .addOnSuccessListener(taskSnapshot -> callback.uploaded(true, taskSnapshot.getDownloadUrl()))
                .addOnFailureListener(exception -> callback.uploaded(false, null));
    }

    public static void downloadProfile(final String uid, IDownloadCallback callback){
        StorageReference riversRef = getInstance().stRef.child(Constants.DIR_PROFILE + "/" + uid + ".jpg");
        riversRef.getDownloadUrl()
                .addOnSuccessListener(uri -> callback.downloaded(uid, uri))
                .addOnFailureListener(e -> callback.downloaded(uid, null));
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
        void downloaded(String id, Uri uri);
    }
}
