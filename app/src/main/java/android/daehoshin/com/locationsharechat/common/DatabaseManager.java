package android.daehoshin.com.locationsharechat.common;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by daeho on 2017. 11. 7..
 */

public class DatabaseManager {
    public static final String TB_USER = "user";

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




    private FirebaseDatabase database;
    private DatabaseReference userRef;

    private DatabaseManager(){
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(TB_USER);
    }
}
