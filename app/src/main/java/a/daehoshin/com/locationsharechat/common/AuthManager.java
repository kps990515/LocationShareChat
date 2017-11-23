package a.daehoshin.com.locationsharechat.common;

import android.app.Activity;
import android.content.Context;
import a.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by daeho on 2017. 11. 7..
 *
 * 로그인 관련 Util
 */
public class AuthManager {
    private static AuthManager authManager;
    public static AuthManager getInstance(){
        if(authManager == null) authManager = new AuthManager();

        return authManager;
    }

    private FirebaseAuth auth;
    private FirebaseUser fUser;
    private UserInfo currentUser;
    private AuthManager(){
        auth = FirebaseAuth.getInstance();
        setCurrentUser();
    }

    private void setCurrentUser(){
        fUser = auth.getCurrentUser();
    }

    /**
     * 현재 사용자 받아오기
     * @param callback 현재 UserInfo 반환(로그인 안된경우 null)
     */
    public void getCurrentUser(final IGetCurrentUser callback){
        if(fUser == null) {
            callback.result(null);
            return;
        }

        if(currentUser != null){
            callback.result(currentUser);
            return;
        }

        DatabaseManager.getUserRef(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(UserInfo.class);
                callback.result(currentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.result(null);
            }
        });
    }

    /**
     * 익명 로그인 처리
     * @param callback 성공여부 반환
     */
    public void signInAnonymously(final Context context, final String nickname, final Uri profileUri, final ISigninAnonymously callback){
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    setCurrentUser();

                    UserInfo ui = new UserInfo();
                    ui.setUid(fUser.getUid());
                    ui.setName(nickname);
                    ui.setLat(CurrentLocation.getLatitude() + "");
                    ui.setLng(CurrentLocation.getLongitude() + "");
                    ui.save();

                    if(profileUri != null) {
                        StorageManager.uploadProfile(context, ui.getUid(), profileUri);
                    }
                }

                callback.result(task.isSuccessful());
            }
        });
    }

    public void signout(final Activity activity){
        getCurrentUser(userInfo -> {
            auth.signOut();
            DatabaseManager.delete(activity, userInfo);
        });
    }


    /**
     * 로그인 성공여부 callback interface
     */
    public interface ISigninAnonymously{
        void result(boolean isSuccessful);
    }

    /**
     * 로그인 사용자정보 callback interface
     */
    public interface IGetCurrentUser{
        void result(UserInfo userInfo);
    }
}
