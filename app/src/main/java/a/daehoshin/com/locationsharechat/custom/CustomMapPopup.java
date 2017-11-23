package a.daehoshin.com.locationsharechat.custom;

import android.content.Context;

import a.daehoshin.com.locationsharechat.common.AuthManager;
import a.daehoshin.com.locationsharechat.common.Constants;
import a.daehoshin.com.locationsharechat.domain.room.Room;
import a.daehoshin.com.locationsharechat.domain.user.Member;
import a.daehoshin.com.locationsharechat.util.FormatUtil;
import a.daehoshin.com.locationsharechat.util.MarkerUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
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

import static a.daehoshin.com.locationsharechat.custom.CustomMapPopup.PopupType.CREATE;

/**
 * Created by Kyung on 2017-11-09.
 */

public class CustomMapPopup extends FrameLayout {
    private View view;
    private EditText editTitle;
    private Spinner spinnerHour, spinnerMin, spinnerEnd;
    private Button btnMakeRoom;
    private IFinished iFinished;

    private int location_Y;
    private int location_X;

    private Marker popUpMarker;
    private GoogleMap mMap;

    // Room에 저장할 값
    private Room room;
    private String lat;
    private String lng;
    private String msg_count;

    // 생성인지 수정인지 설정
    // CREATE, UPDATE
    private PopupType popupType;

    // setting 혹은 list에서 수정
    public CustomMapPopup(@NonNull Context context, IFinished iFinished, Double lat, Double lng, GoogleMap googleMap, PopupType type) {
        super(context);

        this.iFinished = iFinished;
        this.popupType = type;
        this.mMap = googleMap;

        initView();

        setData(null, lat, lng);
        setMarker();

        init();
    }

    // setting 처음 때 수정할 수 있도록
    public CustomMapPopup(@NonNull Context context, IFinished iFinished, Double lat, Double lng, Room room, GoogleMap googleMap, PopupType type) {
        super(context);

        this.iFinished = iFinished;
        this.popupType = type;
        this.mMap = googleMap;

        initView();

        setData(room, lat, lng);
        setMarker();

        init();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(a.daehoshin.com.locationsharechat.R.layout.marker_popup, null);

        editTitle = view.findViewById(a.daehoshin.com.locationsharechat.R.id.editTitle);
        spinnerHour = view.findViewById(a.daehoshin.com.locationsharechat.R.id.spinnerHour);
        spinnerMin = view.findViewById(a.daehoshin.com.locationsharechat.R.id.spinnerMin);
        spinnerEnd = view.findViewById(a.daehoshin.com.locationsharechat.R.id.spinnerEnd);
        btnMakeRoom = view.findViewById(a.daehoshin.com.locationsharechat.R.id.btnMakeRoom);

        switch (popupType){
            case CREATE:
                btnMakeRoom.setText("모임 생성"); break;
            case UPDATE_PARTIAL:
            case UPDATE_TOTAL:
                btnMakeRoom.setText("모임 수정"); break;
        }

        addView(view);
    }

    private void init(){
        setSpinner();
        setBtnMakeRoom();
        setLocation();
    }

    private void setData(Room room, double lat, double lng){
        if(room == null){
            this.room = new Room();
            this.lat = lat + "";
            this.lng = lng + "";
        }
        else{
            this.room = room;
            editTitle.setText(room.title);

            switch (popupType){
                case UPDATE_PARTIAL:
                    this.lat = room.lat + "";
                    this.lng = room.lng + "";
                    break;
                case UPDATE_TOTAL:
                    this.lat = lat + "";
                    this.lng = lng + "";
                    break;
            }

            String hour = FormatUtil.extractHourMin(room.time).get(Constants.Custom_HOUR) + "";
            setSpinnerValue(spinnerHour, hour);

            String min = FormatUtil.extractHourMin(room.time).get(Constants.Custom_MIN) + "";
            setSpinnerValue(spinnerMin, min);

            String end = FormatUtil.extractHourMin(room.end_time - room.time).get(Constants.Custom_HOUR) + "";
            setSpinnerValue(spinnerHour, end);
        }
    }

    private void setSpinnerValue(Spinner spinner, String val){
        Adapter adapter = spinner.getAdapter();
        for(int i = 0; i < adapter.getCount(); i++){
            if(val.equals(adapter.getItem(i)+"")) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void setMarker(){
        popUpMarker = mMap.addMarker(MarkerUtil.createMarkerOptions(a.daehoshin.com.locationsharechat.R.drawable.temp_room_icon, this.lat, this.lng));
    }

    private List<String> createItem(int start, int max, int count, String suffix){
        List<String> data = new ArrayList<>();

        for(int i = 0; i < count; i++){
            data.add(start++ + suffix + "");
            if(start == max) start = 0;
        }

        return data;
    }
    private ArrayAdapter<String> createArrayAdapter(int start, int max, int count, String suffix){
        List<String> data = createItem(start, max, count, suffix);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    /**
     * 스피너 세팅
     */
    private void setSpinner(){
        int start = (int)FormatUtil.extractHourMin(System.currentTimeMillis()).get(Constants.Custom_HOUR);
        start = start + 1;

        spinnerHour.setAdapter(createArrayAdapter(start, 24, 24, ""));
        spinnerMin.setAdapter(createArrayAdapter(0, 60, 60, ""));
        spinnerEnd.setAdapter(createArrayAdapter(1, 4, 3, " 시간 뒤"));
    }

    /**
     * 버튼 클릭시 Room을 생성
     */
    private void setBtnMakeRoom() {
        btnMakeRoom.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            if ("".equals(title) || title == null) {
                Toast.makeText(getContext(), "방 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            String hour = spinnerHour.getSelectedItem().toString();
            String min = spinnerMin.getSelectedItem().toString();
            String end = spinnerEnd.getSelectedItem().toString().substring(0, 1);

            makeRoom(hour, min, end);
            popUpMarker.remove();

            iFinished.callback(room);
        });
    }

    /**
     * 팝업 나타나는 위치 지정
     */
    private void setLocation(){
        this.setPivotX(50);
        location_X = 0;
        location_Y = 20;
        setX(location_X);
        setX(location_Y);
    }

    /**
     * Time을 포멧에 맞춰 세팅
     * @param hour
     * @param minute
     * @return
     */
    private long stringToLongTime(String hour, String minute){
        String timeTemp = FormatUtil.settingDateFormat(hour, minute, "0");
        return FormatUtil.changeTimeFormatStringToLong(timeTemp);
    }

    /**
     * Time을 포멧에 맞춰 세팅
     * @param hour
     * @param minute
     * @param end
     * @return
     */
    private long stringToEndLongTime(String hour, String minute, String end){
        String timeTemp = FormatUtil.settingDateFormat(hour, minute, end);
        return FormatUtil.changeTimeFormatStringToLong(timeTemp);
    }

    /**
     * Room을 생성
     */
    private void makeRoom(String hour, String min, String end){
        // 룸을 생성
        room.setTitle(editTitle.getText().toString());
        room.setTime(stringToLongTime(hour, min));
        room.setEnd_time(stringToEndLongTime(hour, min, end));
        room.setLat(lat);
        room.setLng(lng);

        if(popupType.equals(CREATE)) {
            room.save();
            // 자신 밑에 room_id를 추가
            AuthManager.getInstance().getCurrentUser(userInfo -> {
                userInfo.addRoom(room);
                userInfo.save();

                // 맴버에 자신을 추가
                Member member = new Member(userInfo, room.id);
                member.save();
            });
        }
        else room.save();

        Toast.makeText(getContext(), "모임이 생성되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void recycle(){
        popUpMarker.remove();

        iFinished.callback(room);
    }

    /**
     * 버튼 클릭시 popup을 삭제
     */
    public interface IFinished{
        void callback(Room room);
    }

    public enum PopupType{
        CREATE, UPDATE_PARTIAL, UPDATE_TOTAL
    }
}
