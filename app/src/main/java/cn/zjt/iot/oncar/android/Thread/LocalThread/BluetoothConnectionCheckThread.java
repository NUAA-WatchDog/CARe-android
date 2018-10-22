package cn.zjt.iot.oncar.android.Thread.LocalThread;

import android.os.Handler;

import cn.zjt.iot.oncar.android.Util.BluetoothChatUtil;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Fragment.BodyDataFragment;

/**
 * @author Mr Dk.
 * @version 2018.5.20
 * @see BodyDataFragment
 * @since 2018.5.19
 */

public class BluetoothConnectionCheckThread extends Thread {

    /**
     * @Variables
     */
    private Handler handler;
    private BluetoothChatUtil bluetoothChatUtil;

    /**
     * @Methods
     */

    /*
     * @Constructor CheckBluetoothConnectionThread
     * @Param Handler handler
     */
    public BluetoothConnectionCheckThread(Handler handler) {
        this.handler = handler;
    }

    /*
     * @Setter setBluetoothChatUtil
     * @Param BluetoothChatUtil bluetoothChatUtil
     * @Return void
     */
    public void setBluetoothChatUtil(BluetoothChatUtil bluetoothChatUtil) {
        this.bluetoothChatUtil = bluetoothChatUtil;
    }

    /**
     * @Override
     */

    /*
     * @Override run
     * @Return void
     * @LifeCycle End until bluetooth connected
     */
    @Override
    public void run() {
        while (true) {
            if (bluetoothChatUtil.getState() == BluetoothChatUtil.STATE_CONNECTED) {
                handler.sendEmptyMessage(ConstArgument.MSG_BLUETOOTH_CONNECTED);
                break;
            }
            try {
                Thread.sleep(ConstArgument.CHECK_BLUETOOTH_STATE_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
