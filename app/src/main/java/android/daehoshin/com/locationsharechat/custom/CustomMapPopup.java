package android.daehoshin.com.locationsharechat.custom;

import android.content.Context;
import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.util.FormatUtil;
import android.support.annotation.NonNull;
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

    private String hour;
    private String minute;
    private String endString;

    // Room에 저장할 값
    private String title;
    private long time;
    private long end_time;
    private String lat;
    private String lng;
    private String loc_name;
    private String msg_count;

    public CustomMapPopup(@NonNull Context context, Double lat, Double lng) {
        super(context);
        delteThis = (DelteThis) context;
        initView();
        this.lat = lat+"";
        this.lng = lng+"";

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
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.hour,android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHour.setAdapter(adapter);
        spinnerHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour = (String) adapter.getItem(position);
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
                } else {
                    setTime(hour,minute,endString);
                    Log.e("확인",title + " // " + time + " // " + end_time + " // " + lat + " // " + lng);
//                    makeThisRoom();
                    delteThis.deletePopUp();
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
        Room room = new Room();
        room.setTitle(editTitle.getText().toString());
        room.setTime(time);
        room.setEnd_time(end_time);
        room.setLat(lat);
        room.setLng(lng);
        room.setLoc_name("");
        room.setMsg_count("");
        room.save();
    }

    /**
     * 버튼 클릭시 popup을 삭제하기 위한 인터페이스
     */
    public interface DelteThis{
        public void deletePopUp();
    }

}
