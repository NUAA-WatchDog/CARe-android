package cn.zjt.iot.oncar.android.Util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/*
 * @OpenSource
 */

/**
 * 该类的工作:建立和管理蓝牙连接。
 * 共有三个线程。AcceptThread 线程用来监听 Socket 连接（服务端使用）.
 * ConnectThread 线程用来连接 ServerSocket（客户端使用）。
 * ConnectedThread 线程用来处理 Socket 发送、接收数据。（客户端和服务端共用）
 */

public class BluetoothChatUtil {

    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // 服务名 SDP
    private static final String SERVICE_NAME = "BluetoothChat";
    // uuid SDP
    private static final UUID SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 蓝牙适配器
    private final BluetoothAdapter bluetoothAdapter;
    private Handler bluetoothHandler;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int bluetoothState;
    private static BluetoothChatUtil bluetoothChatUtil;
    private BluetoothDevice connectedBluetoothDevice;

    //常数，指示当前的连接状态
    public static final int STATE_NONE = 0;             // 当前没有可用的连接
    public static final int STATE_LISTEN = 1;           // 现在侦听传入的连接
    public static final int STATE_CONNECTING = 2;       // 现在开始连接
    public static final int STATE_CONNECTED = 3;        // 现在连接到远程设备
    public static final int STATE_CONNECT_FAILURE = 4; //连接失败
    public static final int MESSAGE_DISCONNECTED = 5;
    public static final int STATE_CHANGE = 6;
    public static final int MESSAGE_READ = 7;
    public static final int MESSAGE_WRITE = 8;

    public static final String DEVICE_NAME = "device_name";
    public static final String READ_MSG = "read_msg";

    /**
     * @param context
     * @constructor
     */
    private BluetoothChatUtil(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothState = STATE_NONE;
    }

    public static BluetoothChatUtil getInstance(Context context) {
        if (null == bluetoothChatUtil) {
            bluetoothChatUtil = new BluetoothChatUtil(context);
        }
        return bluetoothChatUtil;
    }

    public void registerHandler(Handler handler) {
        bluetoothHandler = handler;
    }

    public void unregisterHandler() {
        bluetoothHandler = null;
    }

    /**
     * @param state 整数定义当前连接状态
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + bluetoothState + " -> " + state);
        bluetoothState = state;

        // 给新状态的处理程序，界面活性可以更新
        if (bluetoothHandler != null) {
            bluetoothHandler.obtainMessage(STATE_CHANGE, state, -1).sendToTarget();
        }
    }

    /**
     * 返回当前的连接状态。
     */
    public synchronized int getState() {
        return bluetoothState;
    }

    public BluetoothDevice getConnectedDevice() {
        return connectedBluetoothDevice;
    }

    /**
     * 开始聊天服务。特别 AcceptThread 开始
     * 开始服务器模式。
     */
    public synchronized void startListen() {
        if (D) Log.d(TAG, "start");
        // 取消任何线程正在运行的连接
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        // 启动线程来监听一个 BluetoothServerSocket
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    /**
     * 开始 ConnectThread启动连接到远程设备。
     *
     * @param device 连接的蓝牙设备
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);
        // 取消任何线程试图建立连接
        if (bluetoothState == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }
        // 取消任何线程正在运行的连接
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        //启动线程连接到远程设备
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * 开始ConnectedThread开始管理一个蓝牙连接,传输、接收数据.
     *
     * @param socket socket连接
     * @param device 已连接的蓝牙设备
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");
        //取消任何线程正在运行的连接
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        // 启动线程管理连接和传输
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        connectedBluetoothDevice = device;

        if (bluetoothHandler != null) {
            Message msg = bluetoothHandler.obtainMessage(STATE_CONNECTED);
            Bundle bundle = new Bundle();
            bundle.putString(DEVICE_NAME, device.getName());
            msg.setData(bundle);
            bluetoothHandler.sendMessage(msg);
            setState(STATE_CONNECTED);
        }
    }

    /**
     * 停止所有的线程
     */
    public synchronized void disconnect() {
        if (D) Log.d(TAG, "disconnect");
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        //创建临时对象
        ConnectedThread r;
        // 同步副本的 ConnectedThread
        synchronized (this) {
            if (bluetoothState != STATE_CONNECTED) return;
            r = connectedThread;
        }
        // 执行写同步
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // 发送失败的信息带回 Activity
        if (bluetoothHandler != null) {
            Message msg = bluetoothHandler.obtainMessage(STATE_CONNECT_FAILURE);
            bluetoothHandler.sendMessage(msg);
        }
        connectedBluetoothDevice = null;
        setState(STATE_NONE);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // 发送失败的信息带回 Activity
        if (bluetoothHandler != null) {
            Message msg = bluetoothHandler.obtainMessage(MESSAGE_DISCONNECTED);
            bluetoothHandler.sendMessage(msg);
        }
        connectedBluetoothDevice = null;
        setState(STATE_NONE);
    }

    /**
     * 本线程 侦听传入的连接。
     * 它运行直到连接被接受（或取消）。
     */
    private class AcceptThread extends Thread {
        // 本地服务器套接字
        private final BluetoothServerSocket mServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // 创建一个新的侦听服务器套接字
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID);
                //tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN acceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;
            // 循环，直到连接成功
            while (bluetoothState != STATE_CONNECTED) {
                try {
                    // 这是一个阻塞调用 返回成功的连接
                    // mServerSocket.close()在另一个线程中调用，可以中止该阻塞
                    socket = mServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }
                // 如果连接被接受
                if (socket != null) {
                    synchronized (BluetoothChatUtil.this) {
                        switch (bluetoothState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // 正常情况。启动ConnectedThread。
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // 没有准备或已连接。新连接终止。
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END acceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * 本线程用来连接设备
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // 得到一个bluetoothsocket
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(SERVICE_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
                mmSocket = null;
            }
        }

        public void run() {
            Log.i(TAG, "BEGIN connectThread");
            try {
                // socket 连接,该调用会阻塞，直到连接成功或失败
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                try {//关闭这个socket
                    mmSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return;
            }
            // 启动连接线程
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * 本线程server 和client共用.
     * 它处理所有传入和传出的数据。
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // 获得bluetoothsocket输入输出流
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "没有创建临时sockets", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            // 监听输入流
            while (true) {
                try {
                    /*byte[] buffer = new byte[1024];
                    int bytes = mmInStream.read(buffer);*/

                    byte[] buffer = new byte[6];
                    for (int i = 0; i < 6; i++) {
                        buffer[i] = (byte) mmInStream.read();
                        //System.out.println(i);
                    }

                    if (bluetoothHandler != null) {
                        Message msg = bluetoothHandler.obtainMessage(MESSAGE_READ);
                        Bundle bundle = new Bundle();
                        bundle.putByteArray(READ_MSG, buffer);
                        msg.setData(bundle);
                        bluetoothHandler.sendMessage(msg);
                    }

                } catch (IOException e) {
                    //Log.e(TAG, "Stop Receiving", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * 向外发送。
         *
         * @param buffer 发送的数据
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                /*
                ----------------------
                 */
                String temp = new String(buffer);
                Integer integer = Integer.valueOf(temp, 16);

                //System.out.println("WRITE " + Integer.toHexString(integer) + " " + buffer.length + "B");
                /*
                --------------------
                 */
                // 分享发送的信息到Activity
                bluetoothHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
