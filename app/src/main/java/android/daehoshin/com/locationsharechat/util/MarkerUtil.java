package android.daehoshin.com.locationsharechat.util;

import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.domain.room.Room;
import android.daehoshin.com.locationsharechat.domain.user.Member;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by daeho on 2017. 11. 9..
 */

public class MarkerUtil {
    public static MarkerOptions createMarkerOptions(int resourceId, String let, String lan){
        LatLng position = new LatLng(Double.parseDouble(let), Double.parseDouble(lan));

        // 마커 옵션 설정
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(resourceId));

        return markerOptions;
    }

    public static MarkerOptions createMarkerOptions(UserInfo user){
        return createMarkerOptions(R.drawable.userinfoicon, user.getLat(), user.getLng());
    }

    public static MarkerOptions createMarkerOptions(Member member){
        return createMarkerOptions(R.drawable.membericon, member.getLat(), member.getLng());
    }

    public static MarkerOptions createMarkerOptions(Room room){
        return createMarkerOptions(R.drawable.roomicon, room.getLat(), room.getLng());
    }
}
