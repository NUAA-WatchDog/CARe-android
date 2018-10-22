package cn.zjt.iot.oncar.android.Model;

/**
 * @author Mr Dk.
 * @version 2018.4.26
 * @since 2018.4.18
 */

public class HeartRate {

    private long HeartRate_time;
    private int HeartRate_value;

    public long getHeartRate_time() {
        return HeartRate_time;
    }

    public void setHeartRate_time(long heartRate_time) {
        HeartRate_time = heartRate_time;
    }

    public int getHeartRate_value() {
        return HeartRate_value;
    }

    public void setHeartRate_value(int heartRate_value) {
        HeartRate_value = heartRate_value;
    }

}
