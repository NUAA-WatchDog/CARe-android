package cn.zjt.iot.oncar.android.Thread.LocalThread;

import android.os.Handler;

import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Fragment.BodyDataFragment;

/**
 * @author Mr Dk.
 * @version 2018.4.20
 * @see BodyDataFragment
 * @since 2018.4.20
 */

public class HeartRateDisplayThread extends Thread {

    /**
     * @Variables
     */
    private Handler handler;
    private int sleepTime;

    /**
     * @Methods
     */

    /*
     * @Constructor HeartRateDisplayThread
     * @Param Handler handler
     * @Param int sleepTime
     */
    public HeartRateDisplayThread(Handler handler, int sleepTime) {
        this.handler = handler;
        this.sleepTime = sleepTime;
    }

    /*
     * @Getter getSleepTime
     * @Return int
     */
    public int getSleepTime() {
        return sleepTime;
    }

    /*
     * @Setter setSleepTime
     * @Param int sleepTime
     * @Return void
     */
    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * @Override
     */

    /*
     * @Override run
     * @Return void
     * @LifeCycle One time running
     */
    @Override
    public void run() {
        try {
            Thread.sleep(sleepTime);
            handler.sendEmptyMessage(ConstArgument.MSG_ANIMATION_BIGGER);
            Thread.sleep(sleepTime);
            handler.sendEmptyMessage(ConstArgument.MSG_ANIMATION_SMALLER);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
