package android.daehoshin.com.locationsharechat.common;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by daeho on 2017. 11. 8..
 */

public class StorageManager {
    //private static final String DIR_

    private static StorageManager sm;
    public static StorageManager getInstance(){
        if(sm == null) sm = new StorageManager();

        return sm;
    }

    public static void upload(File uploadFile){
        Uri file = Uri.fromFile(uploadFile);
        StorageReference riversRef = sm.stRef.child("images/rivers.jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    public static void load(){
//        File localFile = File.createTempFile("images", "jpg");
//        sm.stRef.getFile(localFile)
//                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Successfully downloaded data to local file
//                        // ...
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle failed download
//                // ...
//            }
//        });
    }

    private StorageReference stRef;
    private StorageReference profileRef;
    private StorageManager(){
        stRef = FirebaseStorage.getInstance().getReference();
        profileRef = FirebaseStorage.getInstance().getReference();
    }
}
