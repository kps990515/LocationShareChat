package android.daehoshin.com.locationsharechat.constant;

/**
 * Created by user on 2017-11-09.
 */

public class Consts {
    public static final String DYNAMICLINK_BASE_URL = "szyx6.app.goo.gl";

    public static final String ROOM_ID = "room_id";
    public static final String CURRENT_USER_UID = "current_user_uid";
    public static final String IS_SIGNIN = "is_signin";

    public static final String TB_USER = "user";
    public static final String TB_MEMBER = "member";
    public static final String TB_ROOM = "room";
    public static final String TB_MSG = "msg";
    public static final String DIR_PROFILE = "profile";

    public static final int CAMERA_PERMISSION_REQ = 902;
    public static final int CAMERA_REQ = 800;
    public static final int GALLERY_REQ = 801;
    public static final int LOGIN_REQ = 900;
    public static final int PERMISSION_REQ = 901;

    // 업데이트 시간
    public static final int LOCATION_INTERVAL_TIME = 100;
    public static final int LOCATION_INTERVAL_METER = 100;

    // 줌 크기
    public static final int Zoom_SIZE = 12;

    // 서비스 관련 중지
    public static final String Thread_START = "thread_start";
    public static final String Thread_STOP = "thread_stop";
    public static final int LOCATION_UPDATE_START = 700;
    public static final int LOCATION_UPDATE_STOP = 701;

    // 시간 및 분
    public static final String Custom_HOUR = "custom_hour";
    public static final String Custom_MIN = "custom_min";

    // 방 생성 및 수정에 대한 값
    public static final String ROOM_CREATE = "create_room";
    public static final String ROOM_UPDATE_NOTLOC = "update_room_partial";
    public static final String ROOM_UPDATE_TOTAL = "update_room_total";
}
