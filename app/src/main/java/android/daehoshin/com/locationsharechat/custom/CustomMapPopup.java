package android.daehoshin.com.locationsharechat.custom;

import android.content.Context;
import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.constant.Consts;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.domain.user.Member;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.daehoshin.com.locationsharechat.util.FormatUtil;
import android.daehoshin.com.locationsharechat.util.MarkerUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Kyung on 2017-11-09.
 */

public class CustomMapPopup extends FrameLayout {

    private View view;
    private EditText editTitle;
    private Spinner spinnerHour;
    private Spinner spinnerMin;
    private Spinner spinnerEnd;
    private Button btnMakeRoom;
    private IDelteThis delteThis;

    private int location_Y;
    private int location_X;

    private Marker popUpMarker;
    private GoogleMap mMap;

    private String hour = null;
    private String minute = null;
    private String endString = null;

    // Room에 저장할 값
    private Room room;
    private String title;
    private long time;
    private long end_time;
    private String lat;
    private String lng;
    private String loc_name;
    private String msg_count;

    // 생성인지 수정인지 설정
    // CREATE, UPDATE
    private String type;

    // setting 혹은 list에서 수정
    public CustomMapPopup(@NonNull Context context, Double lat, Double lng, GoogleMap googleMap, String type) {
        super(context);
        delteThis = (IDelteThis) context;
        this.type = type;
        initView();

        room = new Room();
        this.lat = lat+"";
        this.lng = lng+"";
        this.mMap = googleMap;
        popUpMarker = mMap.addMarker(MarkerUtil.createMarkerOptions(R.drawable.temp_room_icon, this.lat, this.lng));

        init();
    }
    // setting 처음 때 수정할 수 있도록
    public CustomMapPopup(@NonNull Context context, Double lat, Double lng, Room room, GoogleMap googleMap, String type) {
        super(context);
        delteThis = (IDelteThis) context;
        this.type = type;
        initView();
        this.room = room;
        editTitle.setText(room.title);
        switch (type){
            case Consts.ROOM_UPDATE_NOTLOC:
                this.lat = room.lat+"";
                this.lng = room.lng+"";
                break;
            case Consts.ROOM_UPDATE_TOTAL:
                this.lat = lat+"";
                this.lng = lng+"";
                break;
        }
        this.mMap = googleMap;
        popUpMarker = mMap.addMarker(MarkerUtil.createMarkerOptions(R.drawable.temp_room_icon, this.lat, this.lng));

        hour = FormatUtil.extractHourMin(room.time).get(Consts.Custom_HOUR)+"";
        minute = FormatUtil.extractHourMin(room.time).get(Consts.Custom_MIN)+"";
        endString = FormatUtil.extractHourMin(room.end_time-room.time).get(Consts.Custom_HOUR)+"";

        init();
    }

    private void init(){
        setSpinnerHour();
        setSpinnerMin();
        setSpinnerEnd();
        setBtnMakeRoom();

        // 팝업 나타나는 위치 지정
        this.setPivotX(50);
        location_X = 0;
        location_Y = 20;
        setX(location_X);
        setX(location_Y);
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.marker_popup, null);
        editTitle = view.findViewById(R.id.editTitle);
        spinnerHour = view.findViewById(R.id.spinnerHour);
        spinnerMin = view.findViewById(R.id.spinnerMin);
        spinnerEnd = view.findViewById(R.id.spinnerEnd);
        btnMakeRoom = view.findViewById(R.id.btnMakeRoom);
        if(Consts.ROOM_CREATE.equals(type)) btnMakeRoom.setText("모임 생성");
        else btnMakeRoom.setText("모임 수정");

        addView(view);
    }


    /**
     * 스피너 세팅
     */
    private void setSpinnerHour(){
        int start = (int)FormatUtil.extractHourMin(System.currentTimeMillis()).get(Consts.Custom_HOUR);
        int size = 23 - start-1;
        start = start+2;

        String[] data;
        if(size>0){
            data = new String[size];
            for(int i=start ; i<=23 ; i++) {
                if (i < 10) data[i-start] = "0";
                else data[i-start] = "";
                data[i-start] += i;
            }
        } else {
                data = new String[1];
                data[0] = "-1";
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHour.setAdapter(adapter);
        // 수정 요청이면 스피너 변수 미리 세팅
        checkSpinnerValue(spinnerHour,data,hour);
        spinnerHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setSpinnerMin(){
        String data[] = new String[6];
        for(int i=0 ; i<6 ; i++){
            data[i] = "" + i +"0";
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMin.setAdapter(adapter);
        // 수정 요청이면 스피너 변수 미리 세팅
        checkSpinnerValue(spinnerMin,data,minute);
        spinnerMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minute = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setSpinnerEnd(){
        String data[] = new String[3];
        for(int i=0 ; i<3 ; i++){
            int t = i+1;
            data[i] = t +" 시간 뒤 삭제";
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEnd.setAdapter(adapter);
        // 수정 요청이면 스피너 변수 미리 세팅
        checkSpinnerValue(spinnerEnd,data,endString);
        spinnerEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endString = adapter.getItem(position);
                endString = endString.substring(0,1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 수정시 스피너의 초기값을 세팅
     * @param spinner
     * @param data
     * @param value
     */
    private void checkSpinnerValue(Spinner spinner, String data[], String value){
        if(!Consts.ROOM_CREATE.equals(type)){
            for(int i=0 ; i<data.length ; i++){
                if(data[i].equals(value)){
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    /**
     * 버튼 클릭시 Room을 생성
     */
    private void setBtnMakeRoom(){
        btnMakeRoom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                title = editTitle.getText().toString();
                if ("".equals(title) || title == null) {
                    Toast.makeText(getContext(), "방 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if ("-1".equals(hour)) {
                    Toast.makeText(getContext(), "만들 수 있는 시간이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    setTime(hour, minute, endString);
                    makeThisRoom();
                    delteThis.deletePopUp(room, popUpMarker);
                    Toast.makeText(getContext(), "모임이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Time을 포멧에 맞춰 세팅
     * @param hour
     * @param minute
     * @param endString
     */
    private void setTime(String hour, String minute, String endString){
        String timeTemp = FormatUtil.settingDateFormat(hour,minute,"0");
        String timeTemp_end = FormatUtil.settingDateFormat(hour,minute,endString);
        time = FormatUtil.changeTimeFormatStringToLong(timeTemp);
        end_time = FormatUtil.changeTimeFormatStringToLong(timeTemp_end);
    }

    /**
     * Room을 생성
     */
    private void makeThisRoom(){
        // 룸을 생성
        room.setTitle(editTitle.getText().toString());
        room.setTime(time);
        room.setEnd_time(end_time);
        room.setLat(lat);
        room.setLng(lng);

        if(Consts.ROOM_CREATE.equals(type)) {
            room.save();
            // 자신 밑에 room_id를 추가
            AuthManager.getInstance().getCurrentUser(new AuthManager.IAuthCallback() {
                @Override
                public void signinAnonymously(boolean isSuccessful) {

                }

                @Override
                public void getCurrentUser(UserInfo userInfo) {
                    userInfo.addRoom(room);
                    userInfo.save();

                    // 맴버에 자신을 추가
                    Member member = new Member(userInfo, room.id);
                    member.save();
                }
            });
        } else {
            room.save();
        }
    }

    // 마커 삭제
    public void deletePopUpMarker(){
        popUpMarker.remove();
    }
    /**
     * 버튼 클릭시 popup을 삭제하고 마커를 추가
     */
    public interface IDelteThis{
        public void deletePopUp(Room room, Marker popUpMarker);
    }


}
