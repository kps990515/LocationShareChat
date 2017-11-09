package android.daehoshin.com.locationsharechat.custom;

import android.content.Context;
import android.daehoshin.com.locationsharechat.R;
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

    private String hour;
    private String minute;
    private String end_time;

    public CustomMapPopup(@NonNull Context context) {
        super(context);
        initView();
        setSpinnerHour();
        setSpinnerMin();
        setSpinnerEnd();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.marker_popup, null);
        editTitle = view.findViewById(R.id.editTitle);
        spinnerHour = view.findViewById(R.id.spinnerHour);
        spinnerMin = view.findViewById(R.id.spinnerMin);
        spinnerEnd = view.findViewById(R.id.spinnerEnd);
        btnMakeRoom = view.findViewById(R.id.btnMakeRoom);
    }

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
                end_time = (String) adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setBtnMakeRoom(){
        btnMakeRoom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Room을 생성 시킴
            }
        });
    }
}
