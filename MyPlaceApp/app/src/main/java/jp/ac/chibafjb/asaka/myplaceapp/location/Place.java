package jp.ac.chibafjb.asaka.myplaceapp.location;

/**
 * 位置情報
 */
public class Place {

    public static final String DATE_STR_FORMAT = "%1$tF";

    // DBの主キー
    private long id;
    // 緯度
    private double latitude;
    // 経度
    private double longitude;
    // この位置が取得された時間（MS）
    private long time = 0L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
