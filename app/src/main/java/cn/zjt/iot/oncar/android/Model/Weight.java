package cn.zjt.iot.oncar.android.Model;

/**
 * @author Mr Dk.
 * @version 2018.4.26
 * @since 2018.4.18
 */

public class Weight {

    private long Weight_time;
    private float Weight_value;

    public long getWeight_time() {
        return Weight_time;
    }

    public void setWeight_time(long weight_time) {
        Weight_time = weight_time;
    }

    public float getWeight_value() {
        return Weight_value;
    }

    public void setWeight_value(float weight_value) {
        Weight_value = weight_value;
    }
}
