package cn.zjt.iot.oncar.android.Thread.LocalThread;

import android.bluetooth.BluetoothAdapter;

import cn.zjt.iot.oncar.android.Util.BluetoothChatUtil;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Activity.MainActivity;

/**
 * @author Mr Dk.
 * @version 2018.5.18
 * @see MainActivity
 * @since 2018.5.15
 */

public class BluetoothDiscoveryThread extends Thread {

    /**
     * @Variables
     */
    private BluetoothChatUtil bluetoothChatUtil;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean endFlag;

    /**
     * @Methods
     */

    /*
     * @Constructor BluetoothDiscoveryThread
     * @Param BluetoothChatUtil bluetoothChatUtil
     * @Param BluetoothAdapter mBluetoothAdapter
     */
    public BluetoothDiscoveryThread(BluetoothChatUtil bluetoothChatUtil, BluetoothAdapter mBluetoothAdapter) {
        this.bluetoothChatUtil = bluetoothChatUtil;
        this.mBluetoothAdapter = mBluetoothAdapter;
        endFlag = true;
    }

    /*
     * @Method Kill
     * @Return void
     * @Function To kill this Thread
     */
    public void Kill() {
        endFlag = false;
    }

    /*
     * @Method DiscoveryDevices
     * @Return void
     * @Function Start the discovery of bluetooth
     */
    private void DiscoveryDevices() {
        if (mBluetoothAdapter.isDiscovering()) {
            //如果正在扫描则返回
            System.out.println("DISCOVERING");
            return;
        }
        // 扫描蓝牙设备
        mBluetoothAdapter.startDiscovery();
        System.out.println("START DISCOVERY");
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
        while (endFlag) {
            try {
                if (bluetoothChatUtil.getState() != BluetoothChatUtil.STATE_CONNECTED) {
                    DiscoveryDevices();
                }
                Thread.sleep(ConstArgument.BLUETOOTH_DISCOVERY_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
