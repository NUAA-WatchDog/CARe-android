package cn.zjt.iot.oncar.android.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.zjt.iot.oncar.android.Thread.NetThread.RegisterThread;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.CleanLeakUtil;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Model.User;

/**
 * @author Mr Dk.
 * @version 2018.5.2
 * @see LogInActivity
 * @since 2018.5.2
 */

public class RegisterActivity extends FragmentActivity {

    /**
     * @Variables
     */

    /*
     * @Variable User model
     */
    private User user;

    /*
     * @Variable Top buttons
     */
    private ImageButton registerBack;
    private ImageButton registerSure;

    /*
     * @Variable User information container Views
     */
    private EditText registerUsername;
    private EditText registerNickname;
    private EditText registerPassword;
    private EditText registerPasswordCheck;
    private EditText registerContact_1;
    private EditText registerContact_2;
    private EditText registerHeight;

    /*
     * @Variable Interaction View
     */
    private ProgressDialog progressDialog;

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
        RegisterHandler.removeCallbacksAndMessages(null);
        RegisterHandler = null;
        user = null;
        registerBack = null;
        registerSure = null;
        registerUsername = null;
        registerNickname = null;
        registerPassword = null;
        registerPasswordCheck = null;
        registerContact_1 = null;
        registerContact_2 = null;
        registerHeight = null;
        progressDialog = null;

        CleanLeakUtil.fixInputMethodManagerLeak(RegisterActivity.this);

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
        setContentView(R.layout.activity_register);

        InitViews();
        ButtonEvents();
    }

    /**
     * @Methods
     * @see RegisterThread
     */

    /*
     * @Method InitViews
     * @Return void
     * @Function Initializing Views in this Activity
     */
    private void InitViews() {
        registerBack = (ImageButton) findViewById(R.id.registerBack);
        registerSure = (ImageButton) findViewById(R.id.registerSure);
        registerUsername = (EditText) findViewById(R.id.registerUsername);
        registerNickname = (EditText) findViewById(R.id.registerNickname);
        registerPassword = (EditText) findViewById(R.id.registerPassword);
        registerPasswordCheck = (EditText) findViewById(R.id.registerPasswordCheck);
        registerContact_1 = (EditText) findViewById(R.id.registerContact_1);
        registerContact_2 = (EditText) findViewById(R.id.registerContact_2);
        registerHeight = (EditText) findViewById(R.id.registerHeight);
        progressDialog = new ProgressDialog(RegisterActivity.this);
    }

    /*
     * @Method ButtonEvents
     * @Return void
     * @Function Set events for buttons
     */
    private void ButtonEvents() {
        registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!registerPassword.getText().toString().equals(registerPasswordCheck.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                } else if (registerUsername.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (registerNickname.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else if (registerPassword.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    user = new User();
                    user.setUser_name(registerUsername.getText().toString());
                    user.setUser_nickname(registerNickname.getText().toString());
                    user.setUser_password(registerPassword.getText().toString());
                    if (registerContact_1.getText().toString().equals("") &&
                            registerContact_2.getText().toString().equals("")) {
                        user.setUser_EMERcontact_1("");
                        user.setUser_EMERcontact_2("");
                    } else if (registerContact_1.getText().toString().equals("")) {
                        user.setUser_EMERcontact_1(registerContact_2.getText().toString());
                    } else if (registerContact_2.getText().toString().equals("")) {
                        user.setUser_EMERcontact_1(registerContact_1.getText().toString());
                    } else {
                        user.setUser_EMERcontact_1(registerContact_1.getText().toString());
                        user.setUser_EMERcontact_2(registerContact_2.getText().toString());
                    }
                    if (registerHeight.getText().toString().equals("")) {
                        user.setUser_height(-1);
                    } else {
                        user.setUser_height(Integer.parseInt(registerHeight.getText().toString()));
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                    builder.setMessage("确认核对好你的注册信息了吗？");
                    builder.setNegativeButton(ConstArgument.ALERT_CANCEL, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    builder.setPositiveButton(ConstArgument.ALERT_SURE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setTitle(ConstArgument.ALERT_SYSTEM_INFO);
                            progressDialog.setMessage(ConstArgument.ALERT_CONNECTING);
                            progressDialog.show();

                            // Start RegisterThread
                            RegisterThread registerThread = new RegisterThread(RegisterHandler);
                            registerThread.setUser(user);
                            registerThread.start();
                        }
                    });

                    builder.create().show();
                }
            }
        });
    }

    /**
     * @Handlers
     */

    /*
     * @Handler RegisterHandler
     * @Function Return the Internet status of registering operation
     */
    private Handler RegisterHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConstArgument.MSG_INTERNET_SUCCESS:
                    JSONObject returnPack = (JSONObject) msg.obj;

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        if (returnPack.getBoolean("status")) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, ConstArgument.ALERT_SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case ConstArgument.MSG_INTERNET_ERROR:
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this, ConstArgument.ALERT_CHECT_NETWORK, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
