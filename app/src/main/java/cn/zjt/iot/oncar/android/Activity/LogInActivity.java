package cn.zjt.iot.oncar.android.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;
import cn.zjt.iot.oncar.android.Thread.NetThread.LogInThread;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Util.ConstDefinition;
import cn.zjt.iot.oncar.android.Model.User;

/**
 * @author Mr Dk.
 * @version 2018.5.22
 * @see MainActivity
 * @see MineSettingFragment
 * @since 2018.5.4
 */

public class LogInActivity extends FragmentActivity {

    /**
     * @Variables
     */

    /*
     * @Variable Views for log in
     */
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonExit;
    private Button buttonRegister;
    private Button buttonLogIn;

    /*
     * @Variable progressDialog
     * @Function View for interaction
     */
    private ProgressDialog progressDialog;

    /*
     * @Variable bluetoothAdapter
     * @Function Adapter for turning on bluetooth
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * @Override
     */

    /*
     * @Override onDestroy
     * @Return void
     * @Function Recycle the memory of views
     */
    @Override
    protected void onDestroy() {
        LogInHandler.removeCallbacksAndMessages(null);
        LogInHandler = null;
        editTextUsername = null;
        editTextPassword = null;
        buttonExit = null;
        buttonRegister = null;
        buttonLogIn = null;
        progressDialog = null;

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter = null;

        super.onDestroy();
    }

    /*
     * @Override onCreate
     * @Return void
     * @Function Create this Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        InitViews();
        OpenBluetooth();
        SetButtonEvents();
    }

    /**
     * @Methods
     * @see RegisterActivity
     * @see LogInThread
     */

    /*
     * @Method InitViews
     * @Return void
     * @Function Initializing the views
     */
    private void InitViews() {
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        progressDialog = new ProgressDialog(LogInActivity.this);
    }

    /*
     * @Method OpenBluetooth
     * @Return void
     * @Function Turning on bluetooth
     */
    private void OpenBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //设备不支持蓝牙
            Toast.makeText(getApplicationContext(), "设备不支持蓝牙", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        //判断蓝牙是否开启
        if (!bluetoothAdapter.isEnabled()) {
            //蓝牙未开启
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        }
    }

    /*
     * @Method SetButtonEvents
     * @Return void
     * @Function Set events for buttons
     */
    private void SetButtonEvents() {

        // Exiting APP
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                builder.setTitle(ConstArgument.TAG_HEALTH_TIP);
                builder.setMessage("确认要退出应用吗？");
                builder.setNegativeButton(ConstArgument.ALERT_CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton(ConstArgument.ALERT_SURE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                builder.create().show();
            }
        });

        // Registering
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Log In
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextUsername.getText().toString().equals("")) {
                    Toast.makeText(LogInActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (editTextPassword.getText().toString().equals("")) {
                    Toast.makeText(LogInActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(LogInActivity.this, "请开启蓝牙功能", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle(ConstArgument.TAG_HEALTH_TIP);
                    progressDialog.setMessage("正在登录...");
                    progressDialog.show();

                    // Start log in thread
                    LogInThread logInThread = new LogInThread(LogInHandler);
                    logInThread.setUsername(editTextUsername.getText().toString());
                    logInThread.setPassword(editTextPassword.getText().toString());
                    logInThread.start();
                }
            }
        });
    }

    /**
     * @Handlers
     * @see MainActivity
     */

    /*
     * @Handler LogInHandler
     * @Function Return the user information through Internet
     * @Function Jump into MainActivity
     */
    private Handler LogInHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConstArgument.MSG_INTERNET_SUCCESS:
                    // Connect successful
                    JSONObject returnPack = (JSONObject) msg.obj;

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        switch (returnPack.getInt("status")) {
                            case ConstDefinition.USER_NOT_EXIST:
                                Toast.makeText(LogInActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                                break;
                            case ConstDefinition.PASSWORD_INCORRECT:
                                Toast.makeText(LogInActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                break;
                            case ConstDefinition.LOGIN_SUCCESS:
                                User user = new User();
                                user.setUser_name(editTextUsername.getText().toString());
                                user.setUser_password(editTextPassword.getText().toString());

                                user.setUser_id(returnPack.getInt("id"));
                                user.setUser_nickname(returnPack.getString("nickname"));
                                if (returnPack.has("EMERcontact_1")) {
                                    user.setUser_EMERcontact_1(returnPack.getString("EMERcontact_1"));
                                }
                                if (returnPack.has("EMERcontact_2")) {
                                    user.setUser_EMERcontact_2(returnPack.getString("EMERcontact_2"));
                                }
                                user.setUser_height(returnPack.getInt("height"));
                                user.setUser_hr_version(returnPack.getInt("hr_version"));
                                user.setUser_tp_version(returnPack.getInt("tp_version"));

                                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                intent.putExtra("intent_user", user);
                                startActivity(intent);

                                finish();

                                break;
                            default:
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ConstArgument.MSG_INTERNET_ERROR:
                    // Network Error
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(LogInActivity.this, R.string.alert_check_network, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
