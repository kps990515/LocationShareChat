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
import android.support.design.widget.Snackbar;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

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
    private DelteThis delteThis;

    private int location_Y;
    private int location_X;

    private Marker popUpMarker;
    private GoogleMap mMap;

    private String hour;
    private String minute;
    private String endString;

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

    public CustomMapPopup(@NonNull Context context, Double lat, Double lng, GoogleMap googleMap, String type) {
        super(context);
        delteThis = (DelteThis) context;
        initView();
        this.lat = lat+"";
        this.lng = lng+"";
        this.type = type;
        this.mMap = googleMap;
        popUpMarker = mMap.addMarker(MarkerUtil.createMarkerOptions(R.drawable.temp_room_icon, this.lat, this.lng));

        // 팝업 나타나는 위치 지정
        this.setPivotX(50);
        location_X = 0;
        location_Y = 20;
        init();
    }

    private void init(){
        setSpinnerHour();
        setSpinnerMin();
        setSpinnerEnd();
        setBtnMakeRoom();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.marker_popup, null);
        editTitle = view.findViewById(R.id.editTitle);
        spinnerHour = view.findViewById(R.id.spinnerHour);
        spinnerMin = view.findViewById(R.id.spinnerMin);
        spinnerEnd = view.findViewById(R.id.spinnerEnd);
        btnMakeRoom = view.findViewById(R.id.btnMakeRoom);

        addView(view);
    }


    /**
     * 스피너 세팅
     */
    private void setSpinnerHour(){
        int start = (int)FormatUtil.currentHourMin().get(Consts.CURRENT_HOUR);
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
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.min,android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMin.setAdapter(adapter);
        spinnerMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minute = (String) adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setSpinnerEnd(){
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.endTime,android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEnd.setAdapter(adapter);
        spinnerEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endString = (String) adapter.getItem(position);
                endString = endString.substring(0,1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 버튼 클릭시 Room을 생성
     */
    private void setBtnMakeRoom(){
        btnMakeRoom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                title =editTitle.getText().toString();
                if("".equals(title) || title == null){
                    Toast.makeText(getContext(), "방 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if("-1".equals(hour)){
                    Toast.makeText(getContext(), "만들 수 있는 시간이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    setTime(hour,minute,endString);
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
        room = new Room();
        room.setTitle(editTitle.getText().toString());
        room.setTime(time);
        room.setEnd_time(end_time);
        room.setLat(lat);
        room.setLng(lng);
        room.save();

        // 자신 밑에 room_id를 추가
        AuthManager.getInstance().getCurrentUser(new AuthManager.IAuthCallback() {
            @Override
            public void signinAnonymously(boolean isSuccessful) {

            }

            @Override
            public void getCurrentUser(UserInfo userInfo) {
                userInfo.addRoom(room.id);
                userInfo.save();

                // 맴버에 자신을 추가
                Member member = new Member(userInfo, room.id);
                member.save();
            }
        });

    }

    public void deletePopUpMarker(){
        popUpMarker.remove();
    }

    /**
     * 버튼 클릭시 popup을 삭제하고 마커를 추가
     */
    public interface DelteThis{
        public void deletePopUp(Room room, Marker popUpMarker);
    }

    public void setLocationY(){
        this.setY(location_Y);
    }
    public void setLocationX(){
        this.setX(location_X);
    }

}
