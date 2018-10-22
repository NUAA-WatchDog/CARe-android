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

import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;
import cn.zjt.iot.oncar.android.Thread.NetThread.UpdateNicknameThread;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.ConstArgument;

/**
 * @author Mr Dk.
 * @version 2018.5.3
 * @see MineSettingFragment
 * @since 2018.4.29
 */

public class UpdateNicknameActivity extends FragmentActivity {

    /**
     * @Variables
     */
    private ImageButton updateNicknameBack;
    private ImageButton updateNicknameSure;
    private EditText editTextCurrentNickname;
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
        UpdateNickNameHandler.removeCallbacksAndMessages(null);
        UpdateNickNameHandler = null;
        updateNicknameBack = null;
        updateNicknameSure = null;
        editTextCurrentNickname = null;
        progressDialog = null;
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
        setContentView(R.layout.activity_update_nickname);

        InitViews();
        InitializeEditText();
        SetButtonEvents();
    }

    /**
     * @Methods
     * @see UpdateNicknameThread
     */

    /*
     * @Method InitViews
     * @Return void
     * @Function Initializing Views
     */
    private void InitViews() {
        updateNicknameBack = (ImageButton) findViewById(R.id.updateNicknameBack);
        updateNicknameSure = (ImageButton) findViewById(R.id.updateNicknameSure);
        editTextCurrentNickname = (EditText) findViewById(R.id.editTextCurrentNickname);
        progressDialog = new ProgressDialog(UpdateNicknameActivity.this);
    }

    /*
     * @Method InitializeEditText
     * @Return void
     * @Function Set the hint of EditText with user's nickname
     */
    private void InitializeEditText() {
        editTextCurrentNickname.setHint(getIntent().getStringExtra("nickname"));
    }

    /*
     * @Method SetButtonEvents
     * @Return void
     * @Function Set events for buttons
     */
    private void SetButtonEvents() {
        updateNicknameBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateNicknameSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextCurrentNickname.getText().toString().equals("")) {
                    Toast.makeText(UpdateNicknameActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else if (editTextCurrentNickname.getText().toString().equals(getIntent().getStringExtra("nickname"))) {
                    Toast.makeText(UpdateNicknameActivity.this, "您咩有更新昵称", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateNicknameActivity.this);
                    builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                    builder.setMessage("确认将您的昵称修改为 " + editTextCurrentNickname.getText() + " 吗？");
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

                            // Update nickname
                            UpdateNicknameThread updateNicknameThread = new UpdateNicknameThread(UpdateNickNameHandler);
                            updateNicknameThread.setId(getIntent().getIntExtra("id", -1));
                            updateNicknameThread.setNickname(editTextCurrentNickname.getText().toString());
                            updateNicknameThread.start();
                        }
                    });

                    builder.create().show();
                    builder = null;
                }
            }
        });
    }

    /**
     * @Handlers
     */

    /*
     * @Handler UpdateNickNameHandler
     * @Function Return the Internet status of updating operation
     */
    private Handler UpdateNickNameHandler = new Handler(new Handler.Callback() {
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
                            Toast.makeText(UpdateNicknameActivity.this, "修改昵称成功", Toast.LENGTH_SHORT).show();
                            setResult(ConstArgument.RESPONSE_UPDATE_NICKNAME, getIntent().putExtra("nickname", editTextCurrentNickname.getText().toString()));
                            finish();
                        } else {
                            Toast.makeText(UpdateNicknameActivity.this, "修改昵称失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case ConstArgument.MSG_INTERNET_ERROR:
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(UpdateNicknameActivity.this, ConstArgument.ALERT_CHECT_NETWORK, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
