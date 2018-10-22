package cn.zjt.iot.oncar.android.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;
import cn.zjt.iot.oncar.android.Thread.NetThread.UpdateEMERContact1Thread;
import cn.zjt.iot.oncar.android.Thread.NetThread.UpdateEMERContact2Thread;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.ConstArgument;

/**
 * @author Mr Dk.
 * @version 2018.5.19
 * @see MineSettingFragment
 * @since 2018.5.8
 */

public class CreateEMERContactActivity extends Activity {

    /**
     * @Variables
     */

    /*
     * @Variables Data
     */
    private String EMERContact_1;
    private String EMERContact_2;
    private String ContactStatus;

    /*
     * @Variables Views
     */
    private ImageButton updateContactBack;
    private ImageButton updateContactSure;
    private EditText editTextCurrentContact;

    /*
     * @Variables Interaction Views
     */
    private ProgressDialog progressDialog;

    /**
     * @Override
     */

    /*
     * @Override onDestroy
     * @Return void
     * @Function Recycle the memory of this Activity
     */
    @Override
    protected void onDestroy() {
        UpdateContactHandler.removeCallbacksAndMessages(null);
        UpdateContactHandler = null;
        EMERContact_1 = null;
        EMERContact_2 = null;
        ContactStatus = null;
        updateContactBack = null;
        updateContactSure = null;
        editTextCurrentContact = null;
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
        setContentView(R.layout.activity_update_emercontact);

        ContactStatus = getIntent().getStringExtra("ContactStatus");
        EMERContact_1 = getIntent().getStringExtra("EMERContact_1");
        EMERContact_2 = getIntent().getStringExtra("EMERContact_2");

        InitViews();
        SetButtonEvents();
    }

    /**
     * @Methods
     */

    /*
     * @Method InitViews
     * @Return void
     * @Function Initialize Views in this Activity
     */
    private void InitViews() {
        updateContactBack = (ImageButton) findViewById(R.id.updateContactBack);
        updateContactSure = (ImageButton) findViewById(R.id.updateContactSure);
        editTextCurrentContact = (EditText) findViewById(R.id.editTextCurrentContact);
        progressDialog = new ProgressDialog(getApplicationContext());
    }

    /*
     * @Method SetButtonEvents
     * @Return void
     * @Function Set events for buttons
     */
    private void SetButtonEvents() {

        updateContactBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateContactSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (ContactStatus) {
                    case "00":
                        // No EMERContact now
                        // Update into EMERContact_1
                        if (editTextCurrentContact.getText().toString().equals("")) {
                            Toast.makeText(CreateEMERContactActivity.this, "紧急联系人不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateEMERContactActivity.this);
                            builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                            builder.setMessage("确认添加紧急联系人：" + editTextCurrentContact.getText().toString() + " 吗？");
                            builder.setNegativeButton(ConstArgument.ALERT_CANCEL, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                }
                            });
                            builder.setPositiveButton(ConstArgument.ALERT_SURE, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /*progressDialog.setTitle(R.string.alert_system_info);
                                    progressDialog.setMessage(ConstArgument.ALERT_CONNECTING);
                                    progressDialog.show();*/

                                    // Update EMERContact_1
                                    UpdateEMERContact1Thread updateEMERContact1Thread = new UpdateEMERContact1Thread(UpdateContactHandler);
                                    updateEMERContact1Thread.setId(getIntent().getIntExtra("id", -1));
                                    updateEMERContact1Thread.setEMERContact_1(editTextCurrentContact.getText().toString());
                                    updateEMERContact1Thread.start();
                                }
                            });

                            builder.create().show();
                            builder = null;
                        }
                        break;

                    case "01":
                        // Update into EMERContact_1
                        // Should not the same as EMERContact_2
                        if (editTextCurrentContact.getText().toString().equals("")) {
                            Toast.makeText(CreateEMERContactActivity.this, "紧急联系人不能为空", Toast.LENGTH_SHORT).show();
                        } else if (editTextCurrentContact.getText().toString().equals(EMERContact_2)) {
                            Toast.makeText(CreateEMERContactActivity.this, "当前号码与另一个紧急联系人重复", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateEMERContactActivity.this);
                            builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                            builder.setMessage("确认添加紧急联系人：" + editTextCurrentContact.getText().toString() + " 吗？");
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

                                    // Update EMERContact_1
                                    UpdateEMERContact1Thread updateEMERContact1Thread = new UpdateEMERContact1Thread(UpdateContactHandler);
                                    updateEMERContact1Thread.setId(getIntent().getIntExtra("id", -1));
                                    updateEMERContact1Thread.setEMERContact_1(editTextCurrentContact.getText().toString());
                                    updateEMERContact1Thread.start();
                                }
                            });

                            builder.create().show();
                            builder = null;
                        }
                        break;

                    case "10":
                        // Update into EMERContact_2
                        // Should not the same as EMERContact_1
                        if (editTextCurrentContact.getText().toString().equals("")) {
                            Toast.makeText(CreateEMERContactActivity.this, "紧急联系人不能为空", Toast.LENGTH_SHORT).show();
                        } else if (editTextCurrentContact.getText().toString().equals(EMERContact_1)) {
                            Toast.makeText(CreateEMERContactActivity.this, "当前号码与另一个紧急联系人重复", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateEMERContactActivity.this);
                            builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                            builder.setMessage("确认添加紧急联系人：" + editTextCurrentContact.getText().toString() + " 吗？");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                }
                            });
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setTitle(ConstArgument.ALERT_SYSTEM_INFO);
                                    progressDialog.setMessage(ConstArgument.ALERT_CONNECTING);
                                    progressDialog.show();

                                    // Update EMERContact_2
                                    UpdateEMERContact2Thread updateEMERContact2Thread = new UpdateEMERContact2Thread(UpdateContactHandler);
                                    updateEMERContact2Thread.setId(getIntent().getIntExtra("id", -1));
                                    updateEMERContact2Thread.setEMERContact_2(editTextCurrentContact.getText().toString());
                                    updateEMERContact2Thread.start();
                                }
                            });

                            builder.create().show();
                            builder = null;
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * @Handlers
     * @see UpdateEMERContact1Thread
     * @see UpdateEMERContact2Thread
     */

    /*
     * @Handler UpdateContactHandler
     * @Return The Internet status of creating operation
     */
    private Handler UpdateContactHandler = new Handler(new Handler.Callback() {
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
                            Toast.makeText(CreateEMERContactActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            getIntent().putExtra("ContactStatus", ContactStatus);

                            if (ContactStatus.equals("00") || ContactStatus.equals("01")) {
                                // Updated EMERContact_1
                                getIntent().putExtra("EMERContact_1", editTextCurrentContact.getText().toString());
                            } else if (ContactStatus.equals("10")) {
                                // Updated EMERContact_2
                                getIntent().putExtra("EMERContact_2", editTextCurrentContact.getText().toString());
                            }

                            setResult(ConstArgument.RESPONSE_CREATE_CONTACT, getIntent());
                            finish();
                        } else {
                            // Server ERROR
                            Toast.makeText(CreateEMERContactActivity.this, ConstArgument.ALERT_SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case ConstArgument.MSG_INTERNET_ERROR:
                    // Internet ERROR
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(CreateEMERContactActivity.this, ConstArgument.ALERT_CHECT_NETWORK, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
