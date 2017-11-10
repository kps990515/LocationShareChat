package android.daehoshin.com.locationsharechat.util;

import android.app.Activity;
import android.content.Context;
import android.daehoshin.com.locationsharechat.constant.Consts;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * 시간과 분, end-time을 받아서 yyyy/MM/dd/HH/mm/ss 형식으로 변환
     * @param hour
     * @param minute
     * @param plusHour
     * @return
     */
    public static String settingDateFormat(String hour, String minute, String plusHour){
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        Calendar day = Calendar.getInstance();
        int checkHour = Integer.parseInt(plusHour) + Integer.parseInt(hour);
        if(checkHour>23){
            checkHour -=24;
            day.add(Calendar.DATE,1);
        }
        Log.e("day",sdf.format(day.getTime()));

        result = sdf.format(day.getTime());
        result = result + "/" + checkHour + "/" + minute + "/00";

        Log.e("result",result);
        return result;
    }


    /**
     * System.currentTimeMillis() 을 long형으로 받아서 23:15형식으로 변환
     * @param time
     * @return
     */
    public static String changeTimeFormatLongToString(long time){
        DateFormat df = new SimpleDateFormat("HH:mm");
        String str_time = df.format(time);
        return str_time;
    }

    public static String changeTimeFormatLongToString(long time, String format){
        DateFormat df = new SimpleDateFormat(format);
        String str_time = df.format(time);
        return str_time;
    }

    /**
     * long형으로 time을 맡아 시간 및 분을 Map<조건,int>형으로 반환
     * @return
     */
    public static Map extractHourMin(long time){
        Map<String, Integer> result = new HashMap<>();
        SimpleDateFormat sdfH =new SimpleDateFormat("HH");
        SimpleDateFormat sdfM =new SimpleDateFormat("mm");
        int hour = Integer.parseInt(sdfH.format(time));
        int min = Integer.parseInt(sdfM.format(time));
        result.put(Consts.Custom_HOUR, hour);
        result.put(Consts.Custom_MIN, min);
        return result;
    }
}
