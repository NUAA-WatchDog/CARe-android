package cn.zjt.iot.oncar.android.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.zjt.iot.oncar.android.Adapter.MainFragmentPagerAdapter;
import cn.zjt.iot.oncar.android.Fragment.BodyDataFragment;
import cn.zjt.iot.oncar.android.Fragment.EmergencyCallFragment;
import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.BluetoothChatUtil;
import cn.zjt.iot.oncar.android.Util.CleanLeakUtil;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Thread.LocalThread.BluetoothDiscoveryThread;
import cn.zjt.iot.oncar.android.Model.User;

/**
 * @author Mr Dk.
 * @version 2018.5.22
 * @see LogInActivity
 * @see BodyDataFragment
 * @see EmergencyCallFragment
 * @see MineSettingFragment
 * @since 2018.4.20
 */

public class MainActivity extends FragmentActivity {

    /**
     * @Variables
     */

    /*
     * @Variable Views for top bar
     */
    private ImageView batteryView;
    private ImageView bodyCheckView;
    private TextView timeTextView;
    private Calendar calendar;

    /*
     * @Variable For generating the ViewPager
     */
    private List<Fragment> fragmentList;
    private ViewPager mainViewPager;

    /*
     * @Variable Views for bottom button list
     */
    private ImageView buttonBodyData;
    private ImageView buttonEmergencyCall;
    private ImageView buttonMineSetting;
    private BodyDataFragment bodyDataFragment;
    private EmergencyCallFragment emergencyCallFragment;
    private MineSettingFragment mineSettingFragment;

    /*
     * @Variable user model
     */
    private User user;

    /*
     * @Variable For bluetooth operation
     */
    private BluetoothChatUtil bluetoothChatUtil;
    private BluetoothAdapter bluetoothAdapter;

    /*
     * @Variable Bluetooth discovery thread
     * @Thread bthDiscThread
     * @LifeCycle the same as MainActivity
     */
    private BluetoothDiscoveryThread bthDiscThread;

    /**
     * @Variable For TTS speaking
     * @OpenSource http://www.xfyun.cn/
     * @Support IFLYTEK CO.,LTD.
     */
    private SpeechSynthesizer speechSynthesizer;

    /**
     * @Override
     */

    /*
     * @Override onDestroy
     * @Return void
     * @Function Destroy this Activity
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);

        if (bthDiscThread.isAlive()) {
            bthDiscThread.Kill();
        }
        bthDiscThread = null;

        if (speechSynthesizer != null) {
            speechSynthesizer.destroy();
            speechSynthesizer = null;
        }
        SpeechUtility.getUtility().destroy();

        batteryView = null;
        bodyCheckView = null;
        timeTextView = null;
        calendar = null;
        fragmentList = null;
        mainViewPager = null;
        buttonBodyData = null;
        buttonEmergencyCall = null;
        buttonMineSetting = null;
        user = null;
        bluetoothChatUtil = null;
        bluetoothAdapter = null;
        bodyDataFragment = null;
        emergencyCallFragment = null;
        mineSettingFragment = null;

        CleanLeakUtil.fixInputMethodManagerLeak(MainActivity.this);

        super.onDestroy();
    }

    /*
     * @Override onActivityResult
     * @Param requestCode
     * @Param resultCode
     * @Param data
     * @Return void
     * @Function Get the Result from other Activities and send them into Fragments
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragmentList.get(2).onActivityResult(requestCode, resultCode, data);
        fragmentList.get(1).onActivityResult(requestCode, resultCode, data);
        fragmentList.get(0).onActivityResult(requestCode, resultCode, data);
    }

    /*
     * @Override onCreate
     * @Return void
     * @Function Create this Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (User) getIntent().getSerializableExtra("intent_user");

        InitViews();
        InitTTS();
        InitIntentFilter();
        InitBluetooth();
        GenerateTopBar();
        GenerateViewPager();
        GenerateButtonList();
    }

    /**
     * @Methods
     * @see BluetoothDiscoveryThread
     * @see BodyDataFragment
     * @see EmergencyCallFragment
     * @see MineSettingFragment
     * @see MainFragmentPagerAdapter
     */

    /*
     * @Method InitViews
     * @Return void
     * @Function Initializing Views
     */
    private void InitViews() {
        batteryView = (ImageView) findViewById(R.id.batteryView);
        bodyCheckView = (ImageView) findViewById(R.id.imageViewBodyCheckStatus);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        buttonBodyData = (ImageView) findViewById(R.id.buttonBodyData);
        buttonEmergencyCall = (ImageView) findViewById(R.id.buttonEmergencyCall);
        buttonMineSetting = (ImageView) findViewById(R.id.buttonMineSetting);
        mainViewPager = (ViewPager) findViewById(R.id.mainViewPager);
    }

    /*
     * @Method InitTTS
     * @Return void
     * @Function Initializing TTS service
     */
    private void InitTTS() {
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=5a996b86");
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(MainActivity.this, null);
        if (speechSynthesizer != null) {
            speechSynthesizer.startSpeaking("您好，" + user.getUser_nickname() + "。请开始您的体检吧。", null);
        }
    }

    /*
     * @Method InitIntentFilter
     * @Return void
     * @Function Register an IntentFilter to receive system broadcast
     * @see broadcastReceiver
     */
    private void InitIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    /*
     * @Method InitBluetooth
     * @Return void
     * @Function Initializing bluetooth service and start discovery thread
     */
    private void InitBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothChatUtil = BluetoothChatUtil.getInstance(getApplicationContext());

        bthDiscThread = new BluetoothDiscoveryThread(bluetoothChatUtil, bluetoothAdapter);
        bthDiscThread.start();
    }

    /*
     * @Method GenerateTopBar
     * @Return void
     * @Function Generating views on top bar
     */
    private void GenerateTopBar() {
        // Time View
        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        timeTextView.setText(simpleDateFormat.format(new Date(calendar.getTimeInMillis())));

        // Body check View
        bodyCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bodyCheckView.getTag().toString().equals("no")) {
                    mainViewPager.setCurrentItem(0);
                    SelectBodyDataFragment();
                    bodyDataFragment.StartBodyTest();
                } else {
                    Toast.makeText(MainActivity.this, "您已进行过体检", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
     * @Method GenerateViewPager
     * @Return void
     * @Function Generating three main Fragments and join them into the ViewPager
     * @Function Set necessary parameters for Fragments
     * @Function Set OnPageChangeListener for ViewPager
     * @See SelectBodyDataFragment
     * @See SelectEmergencyCallFragment
     * @See SelectMineSettingFragment
     */
    private void GenerateViewPager() {

        bodyDataFragment = new BodyDataFragment();
        emergencyCallFragment = new EmergencyCallFragment();
        mineSettingFragment = new MineSettingFragment();

        bodyDataFragment.setUser(user);
        emergencyCallFragment.setUser(user);
        mineSettingFragment.setUser(user);

        bodyDataFragment.setBluetoothChatUtil(bluetoothChatUtil);
        bodyDataFragment.setSpeechSynthesizer(speechSynthesizer);

        fragmentList = new ArrayList<>();
        fragmentList.add(bodyDataFragment);
        fragmentList.add(emergencyCallFragment);
        fragmentList.add(mineSettingFragment);

        MainFragmentPagerAdapter pagerAdapter =
                new MainFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mainViewPager.setAdapter(pagerAdapter);
        mainViewPager.setOffscreenPageLimit(3);

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        SelectBodyDataFragment();
                        break;
                    case 1:
                        SelectEmergencyCallFragment();
                        break;
                    case 2:
                        SelectMineSettingFragment();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*
     * @Method GenerateButtonList
     * @Return void
     * @Function Set events for buttons
     * @See GenerateViewPager
     * @See SelectBodyDataFragment
     * @See SelectEmergencyCallFragment
     * @See SelectMineSettingFragment
     * @Color SELECTED : #ffffff
     * @Color UNSELECTED : #8a8a8a
     */
    private void GenerateButtonList() {

        buttonBodyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectBodyDataFragment();
                mainViewPager.setCurrentItem(0, true);
            }
        });

        buttonEmergencyCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectEmergencyCallFragment();
                mainViewPager.setCurrentItem(1, true);
            }
        });

        buttonMineSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectMineSettingFragment();
                mainViewPager.setCurrentItem(2, true);
            }
        });
    }

    /*
     * @Method SelectBodyDataFragment
     * @Return void
     * @Function Change the button status while selecting BodyDataFragment
     */
    private void SelectBodyDataFragment() {
        buttonBodyData.setImageResource(R.drawable.icon_data);
        buttonEmergencyCall.setImageResource(R.drawable.icon_call_gray);
        buttonMineSetting.setImageResource(R.drawable.icon_body_gray);
    }

    /*
     * @Method SelectEmergencyCallFragment
     * @Return void
     * @Function Change the button status while selecting EmergencyCallFragment
     */
    private void SelectEmergencyCallFragment() {
        buttonBodyData.setImageResource(R.drawable.icon_data_gray);
        buttonEmergencyCall.setImageResource(R.drawable.icon_call);
        buttonMineSetting.setImageResource(R.drawable.icon_body_gray);
    }

    /*
     * @Method SelectMineSettingFragment
     * @Return void
     * @Function Change the button status while selecting MineSettingFragment
     */
    private void SelectMineSettingFragment() {
        buttonBodyData.setImageResource(R.drawable.icon_data_gray);
        buttonEmergencyCall.setImageResource(R.drawable.icon_call_gray);
        buttonMineSetting.setImageResource(R.drawable.icon_body);
    }


    /**
     * @BroadcastReceivers
     */

    /*
     * @BroadcastReceiver broadcastReceiver
     * @Function Receiving system broadcast
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                timeTextView.setText(simpleDateFormat.format(new Date(calendar.getTimeInMillis())));

            } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                int quantity = 100 * level / scale;
                quantity = quantity / 10 * 10;

                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        switch (quantity) {
                            case 100:
                                batteryView.setImageResource(R.drawable.charging100);
                                break;
                            case 90:
                                batteryView.setImageResource(R.drawable.charging90);
                                break;
                            case 80:
                                batteryView.setImageResource(R.drawable.charging80);
                                break;
                            case 70:
                                batteryView.setImageResource(R.drawable.charging70);
                                break;
                            case 60:
                                batteryView.setImageResource(R.drawable.charging60);
                                break;
                            case 50:
                                batteryView.setImageResource(R.drawable.charging50);
                                break;
                            case 40:
                                batteryView.setImageResource(R.drawable.charging40);
                                break;
                            case 30:
                                batteryView.setImageResource(R.drawable.charging30);
                                break;
                            case 20:
                                batteryView.setImageResource(R.drawable.charging20);
                                break;
                            case 10:
                                batteryView.setImageResource(R.drawable.charging10);
                                break;
                            case 0:
                                batteryView.setImageResource(R.drawable.charging10);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        switch (quantity) {
                            case 100:
                                batteryView.setImageResource(R.drawable.battery100);
                                break;
                            case 90:
                                batteryView.setImageResource(R.drawable.battery90);
                                break;
                            case 80:
                                batteryView.setImageResource(R.drawable.battery80);
                                break;
                            case 70:
                                batteryView.setImageResource(R.drawable.battery70);
                                break;
                            case 60:
                                batteryView.setImageResource(R.drawable.battery60);
                                break;
                            case 50:
                                batteryView.setImageResource(R.drawable.battery50);
                                break;
                            case 40:
                                batteryView.setImageResource(R.drawable.battery40);
                                break;
                            case 30:
                                batteryView.setImageResource(R.drawable.battery30);
                                break;
                            case 20:
                                batteryView.setImageResource(R.drawable.battery20);
                                break;
                            case 10:
                                batteryView.setImageResource(R.drawable.battery10);
                                break;
                            case 0:
                                batteryView.setImageResource(R.drawable.battery10);
                                break;
                            default:
                                break;
                        }
                        break;
                }

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (scanDevice == null || scanDevice.getName() == null) {
                    return;
                }

                //蓝牙设备名称
                String name = scanDevice.getName();
                String address = scanDevice.getAddress();

                System.out.println("FOUND:" + name + " " + address);

                if (address != null && address.equals(ConstArgument.BLUETOOTH_ADDR)) {
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothChatUtil.connect(scanDevice);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 扫描完毕
                //Toast.makeText(getApplicationContext(), "扫描完毕", Toast.LENGTH_SHORT).show();
                System.out.println("DISCOVERY FINISHED");
            }
        }
    };
}
