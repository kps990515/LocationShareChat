package android.daehoshin.com.locationsharechat.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kyung on 2017-11-08.
 */

public class FormatUtil {
    // 비트맵은 닫아야 하는 것 조심
    public static Bitmap createDrawbleFromView(Context context, View view){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        // view를 bitmap으로 전환
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     * time의 형식 : yyyy/MM/dd/HH/mm/ss
     * @param time
     * @return
     */
    public static long changeTimeFormatStringToLong(String time){
        long result = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        try {
            Date date = formatter.parse(time);
            result = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 시간과 분을 받아서 yyyy/MM/dd/HH/mm/ss 형식으로 변환
     * @param hour
     * @param minute
     * @return
     */
    public static String settingDateFormat(String hour, String minute){
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        long today = System.currentTimeMillis();
        result = sdf.format(today);
        result = result + "/" + hour + "/" + minute + "/00";
        return result;
    }
}
