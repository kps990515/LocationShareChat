package android.daehoshin.com.locationsharechat.common;

/**
 * Created by daeho on 2017. 11. 11..
 */

public class CurrentLocation {
    private static double latitude;
    private static double longitude;

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        CurrentLocation.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        CurrentLocation.longitude = longitude;
    }
}
