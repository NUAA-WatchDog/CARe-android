package cn.zjt.iot.oncar.android.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.zjt.iot.oncar.android.Thread.NetThread.UpdateHeightThread;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;

/**
 * @author Mr Dk.
 * @since 2018.5.1
 * @version 2018.5.13
 * @see MineSettingFragment
 */

public class UpdateHeightActivity extends FragmentActivity {

    /**
     * @Variables
     */

    /*
     * @Variable Views
     */
    private ImageButton updateHeightBack;
    private ImageButton updateHeightSure;
    private NumberPicker numberPickerHeight;
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
        UpdateHeightHandler.removeCallbacksAndMessages(null);
        UpdateHeightHandler = null;
        updateHeightBack = null;
        updateHeightSure = null;
        numberPickerHeight = null;
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
        setContentView(R.layout.activity_update_height);

        // Get original height
        int height = getIntent().getIntExtra("height", 0);

        InitViews();
        InitNumberPicker(height);
        SetButtonsEvents(height);
    }

    /**
     * @Methods
     * @see UpdateHeightThread
     */

    /*
     * @Method InitViews
     * @Return void
     * @Function Initializing Views
     */
    private void InitViews() {
        updateHeightBack = (ImageButton) findViewById(R.id.updateHeightBack);
        updateHeightSure = (ImageButton) findViewById(R.id.updateHeightSure);
        numberPickerHeight = (NumberPicker) findViewById(R.id.numberPickerHeight);
        progressDialog = new ProgressDialog(UpdateHeightActivity.this);
    }

    /*
     * @Method InitNumberPicker
     * @Param originalHeight
     * @Return void
     * @Function Initializing NumberPicker
     */
    private void InitNumberPicker(int originalHeight) {
        numberPickerHeight.setMinValue(ConstArgument.MIN_HEIGHT);
        numberPickerHeight.setMaxValue(ConstArgument.MAX_HEIGHT);
        numberPickerHeight.setWrapSelectorWheel(false);

        if (originalHeight < ConstArgument.MIN_HEIGHT) {
            numberPickerHeight.setValue(ConstArgument.MIN_HEIGHT);
        } else if (originalHeight > ConstArgument.MAX_HEIGHT) {
            numberPickerHeight.setValue(ConstArgument.MAX_HEIGHT);
        } else {
            numberPickerHeight.setValue(originalHeight);
        }
    }

    /*
     * @Method SetButtonsEvents
     * @Param originalHeight
     * @Return void
     * @Function Set events for button
     */
    private void SetButtonsEvents(final int originalHeight) {
        updateHeightBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateHeightSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (originalHeight == numberPickerHeight.getValue()) {
                    Toast.makeText(UpdateHeightActivity.this, "您咩有更新身高", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateHeightActivity.this);
                    builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                    builder.setMessage("确认将您的身高修改为 " + numberPickerHeight.getValue() + " cm 吗？");
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

                            // Start updating thread
                            UpdateHeightThread updateHeightThread = new UpdateHeightThread(UpdateHeightHandler);
                            updateHeightThread.setId(getIntent().getIntExtra("id", -1));
                            updateHeightThread.setHeight(numberPickerHeight.getValue());
                            updateHeightThread.start();
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
     * @Handler UpdateHeightHandler
     * @Function Return the Internet status of updating operation
     */
    private Handler UpdateHeightHandler = new Handler(new Handler.Callback() {
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
                            Toast.makeText(UpdateHeightActivity.this, "修改身高成功", Toast.LENGTH_SHORT).show();
                            setResult(ConstArgument.RESPONSE_UPDATE_HEIGHT, getIntent().putExtra("height", numberPickerHeight.getValue()));
                            finish();
                        } else {
                            Toast.makeText(UpdateHeightActivity.this, "修改身高失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case ConstArgument.MSG_INTERNET_ERROR:
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(UpdateHeightActivity.this, ConstArgument.ALERT_CHECT_NETWORK, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
