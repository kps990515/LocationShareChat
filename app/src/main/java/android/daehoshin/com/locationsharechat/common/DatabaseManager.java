package android.daehoshin.com.locationsharechat.common;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.daehoshin.com.locationsharechat.Const.Consts.TB_MEMBER;
import static android.daehoshin.com.locationsharechat.Const.Consts.TB_MSG;
import static android.daehoshin.com.locationsharechat.Const.Consts.TB_ROOM;
import static android.daehoshin.com.locationsharechat.Const.Consts.TB_USER;

/**
 * Created by daeho on 2017. 11. 7..
 */

public class DatabaseManager {

    private static DatabaseManager databaseManager;
    public static DatabaseManager getInstance(){
        if(databaseManager == null) databaseManager = new DatabaseManager();

        return databaseManager;
    }

    public static DatabaseReference getUserRef(){
        return getInstance().userRef;
    }

    public static DatabaseReference getUserRef(String uid){
        return getInstance().database.getReference(TB_USER + "/" + uid);
    }

    public static DatabaseReference getMemberRef(String roomid){
        return getInstance().database.getReference(TB_MEMBER + "/" + roomid);
    }

    public static DatabaseReference getRoomRef(){
        return getInstance().roomRef;
    }

    public static DatabaseReference getRoomRef(String roomid){
        return getInstance().database.getReference(TB_ROOM + "/" + roomid);
    }

    public static DatabaseReference getMsgRef(String roomid){
        return getInstance().database.getReference(TB_MSG + "/" + roomid);
    }

    public static DatabaseReference getMsgRef(String roomid, long idx){
        return getInstance().database.getReference(TB_MSG + "/" + roomid + "/" + idx);
    }


    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private DatabaseReference roomRef;

    private DatabaseManager(){
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(TB_USER);
        roomRef = database.getReference(TB_ROOM);
    }
}
