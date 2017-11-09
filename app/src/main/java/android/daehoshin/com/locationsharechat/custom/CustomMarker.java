package android.daehoshin.com.locationsharechat.custom;

import android.app.Activity;
import android.content.Context;
import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.util.FormatUtil;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 찍을 마커 커스텀
 * type
 *  - RoomList : List에서 RoomList 마커
 *  - FriendDetail : Detail에서 Friend 마커
 *  - RoomDetail : Detail에서 목적지 마커
 */

public class CustomMarker extends FrameLayout {
    // 마커에 let, lan 저장
    private String room_id;
    private String type;
    double let;
    double lan;
    private View marker;
    private TextView roomTitle;
    private TextView roomTime;

    public CustomMarker(@NonNull Context context, double let, double lan, String type){
        super(context);
        initView();
        this.let = let;
        this.lan = lan;
    }

    // 커스텀한 marker 뷰 init
    private void initView(){
        marker = LayoutInflater.from(getContext()).inflate(R.layout.marker_custom,null);
        roomTitle = marker.findViewById(R.id.roomTitle);
        roomTime = marker.findViewById(R.id.roomTime);
    }
    // title과 time, let, lan 등을 세팅
    public void setRoomId(String room_id){
        this.room_id = room_id;
    }
    public void setRoomTitle(String title){
        roomTitle.setText(title);
    }
    public void setRoomTime(String time){
        roomTime.setText(time);
    }
    public void setLet(double let){
        this.let = let;
    }
    public void setLan(double lan){
        this.lan = lan;
    }
    public String getRoomId(){
        return room_id;
    }
    public String getRoomTitle(){
        return roomTitle.getText().toString();
    }
    public String getRoomTime(){
        return roomTime.getText().toString();
    }
    public double getLet(){
        return let;
    }
    public double getLan(){
        return lan;
    }

    // 마커를 추가
    public Marker addMarker(GoogleMap googleMap){
        LatLng position = new LatLng(let,lan);
        // 마커 옵션 설정
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        // 아이콘을 변경
        switch (type){
            case "RoomList":
                // 1. 아이콘 : 일반 마커와 그림??
                break;
            case "FriendDetail":
                // 2. 아이콘 : 사람의 프로필과 이름
                break;
            case "RoomDetail":
                // 3. 아이콘 : 일반 마커와 그림
                break;
        }
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(FormatUtil.createDrawbleFromView(getContext(),marker)));
        return googleMap.addMarker(markerOptions);
    }

    public void goRoom(){
        marker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type){
                    case "RoomList":
                        // 1. room_id를 넘겨서 리스트로 이동
                        break;
                    case "FriendDetail":
                        // 2. 사람?의 정보를 보여줌
                        break;
                    case "RoomDetail":
                        // 3. 방 정보를 보여줌
                        break;
                }
            }
        });
    }
}
