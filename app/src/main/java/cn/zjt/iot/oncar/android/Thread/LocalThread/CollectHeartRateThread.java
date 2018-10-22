package cn.zjt.iot.oncar.android.Thread.LocalThread;

import cn.zjt.iot.oncar.android.Util.BluetoothChatUtil;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Fragment.BodyDataFragment;

/**
 * @author Mr Dk.
 * @version 2018.5.8
 * @see BodyDataFragment
 * @since 2018.5.8
 */

public class CollectHeartRateThread extends Thread {

    /**
     * @Variables
     */
    private BluetoothChatUtil bluetoothChatUtil;
    private boolean endFlag;
    private boolean paused;

    /**
     * @Methods
     */

    /*
     * @Constructor CollectHeartRateThread
     * @Param BluetoothChatUtil bluetoothChatUtil
     */
    public CollectHeartRateThread(BluetoothChatUtil bluetoothChatUtil) {
        this.bluetoothChatUtil = bluetoothChatUtil;
        endFlag = false;
        paused = false;
    }

    /*
     * @Method Kill
     * @Return void
     * @Function To kill this thread
     */
    public void Kill() {
        endFlag = true;
    }

    /*
     * @Method Pause
     * @Return void
     * @Function To pause this thread sending instructions by bluetooth
     */
    public void Pause() {
        paused = true;
    }

    /*
     * @Method Resume
     * @Return void
     * @Function To resume this thread sending instructions by bluetooth
     */
    public void Resume() {
        paused = false;
    }

    /**
     * @Override
     */

    /*
     * @Override run
     * @Return void
     * @LifeCycle Forever until being killed manually
     */
    @Override
    public void run() {

        System.out.println("HeartRateThread Start");

        try {
            Thread.sleep(ConstArgument.HEART_RATE_COLLECT_START_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!endFlag) {
            if (bluetoothChatUtil.getState() == BluetoothChatUtil.STATE_CONNECTED) {
                if (!paused) {
                    bluetoothChatUtil.write(ConstArgument.INSTRUCTION_HEART_RATE.getBytes());
                }
            }
            try {
                Thread.sleep(ConstArgument.HEART_RATE_COLLECT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("HeartRateThread killed");
    }
}
