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

public class CollectWeightThread extends Thread {

    /**
     * @Variables
     */
    private BluetoothChatUtil bluetoothChatUtil;
    private boolean endFlag;

    /**
     * @Methods
     */

    /*
     * @Constructor CollectWeightThread
     * @Param BluetoothChatUtil bluetoothChatUtil
     */
    public CollectWeightThread(BluetoothChatUtil bluetoothChatUtil) {
        this.bluetoothChatUtil = bluetoothChatUtil;
        endFlag = false;
    }

    /*
     * @Method Kill
     * @Return void
     * @Function To kill this thread
     */
    public void Kill() {
        endFlag = true;
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

        System.out.println("WeightThread start");

        while (!endFlag) {
            if (bluetoothChatUtil.getState() == BluetoothChatUtil.STATE_CONNECTED) {
                bluetoothChatUtil.write(ConstArgument.INSTRUCTION_WEIGHT.getBytes());
            }
            try {
                Thread.sleep(ConstArgument.WEIGHT_COLLECT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("WeightThread killed");
    }
}
