package android.daehoshin.com.locationsharechat.common;

import android.daehoshin.com.locationsharechat.domain.UserInfo;
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
    private FirebaseUser currentUser;
    private AuthManager(){
        auth = FirebaseAuth.getInstance();
        setCurrentUser();
    }

    private void setCurrentUser(){
        currentUser = auth.getCurrentUser();

        if(currentUser != null){

        }
    }

    /**
     * 현재 사용자 받아오기
     * @param callback 현재 UserInfo 반환(로그인 안된경우 null)
     */
    public void getCurrentUser(final IAuthCallback callback){
        if(currentUser == null) {
            callback.getCurrentUser(null);
            return;
        }

        DatabaseManager.getUserRef(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.getCurrentUser(dataSnapshot.getValue(UserInfo.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.getCurrentUser(null);
            }
        });
    }

    /**
     * 익명 로그인 처리
     * @param callback 성공여부 반환
     */
    public void signInAnonymously(final String nickname, final IAuthCallback callback){
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    setCurrentUser();

                    UserInfo ui = new UserInfo(currentUser.getUid());
                    ui.setName(nickname);
                    ui.save();
                }

                callback.signinAnonymously(task.isSuccessful());


            }
        });
    }

    public void signout(){
        DatabaseManager.getUserRef(currentUser.getUid()).removeValue();
        auth.signOut();
    }

    /**
     * 로그인 관련 Callback interface
     */
    public interface IAuthCallback{
        void signinAnonymously(boolean isSuccessful);
        void getCurrentUser(UserInfo userInfo);
    }
}
