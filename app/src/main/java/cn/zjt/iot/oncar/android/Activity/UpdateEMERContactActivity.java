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

import cn.zjt.iot.oncar.android.Thread.NetThread.UpdateEMERContact1Thread;
import cn.zjt.iot.oncar.android.Thread.NetThread.UpdateEMERContact2Thread;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Fragment.EmergencyCallFragment;

/**
 * @author Mr Dk.
 * @version 2018.5.7
 * @see EmergencyCallFragment
 * @since 2018.5.6
 */

public class UpdateEMERContactActivity extends Activity {

    /**
     * @Variables
     */

    /*
     * @Variable Views
     */
    private ImageButton updateContactBack;
    private ImageButton updateContactSure;
    private EditText editTextCurrentContact;

    /*
     * @Variable Data
     */
    private int id;
    private int updateTag;
    private String EMERcontact;

    /*
     * @Variable View for interaction
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
        UpdateContactHandler.removeCallbacksAndMessages(null);
        UpdateContactHandler = null;
        updateContactBack = null;
        updateContactSure = null;
        editTextCurrentContact = null;
        EMERcontact = null;
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

        id = getIntent().getIntExtra("id", -1);
        updateTag = getIntent().getIntExtra("updateTag", -1);
        if (updateTag == 1) {
            EMERcontact = getIntent().getStringExtra("EMERcontact_1");
        } else if (updateTag == 2) {
            EMERcontact = getIntent().getStringExtra("EMERcontact_2");
        }

        InitViews();
        editTextCurrentContact.setHint(EMERcontact);
        SetButtonEvents();
    }

    /**
     * @Methods
     * @see UpdateEMERContact1Thread
     * @see UpdateEMERContact2Thread
     */

    /*
     * @Method InitViews
     * @Return void
     * @Function Initializing Views in this Activity
     */
    private void InitViews() {
        updateContactBack = (ImageButton) findViewById(R.id.updateContactBack);
        updateContactSure = (ImageButton) findViewById(R.id.updateContactSure);
        editTextCurrentContact = (EditText) findViewById(R.id.editTextCurrentContact);
        progressDialog = new ProgressDialog(UpdateEMERContactActivity.this);
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
                getIntent().putExtra("updateTag", -1);
                setResult(ConstArgument.RESPONSE_UPDATE_CONTACT, getIntent());
                finish();
            }
        });

        updateContactSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextCurrentContact.getText().toString().equals("")) {
                    Toast.makeText(UpdateEMERContactActivity.this, "紧急联系人不能为空", Toast.LENGTH_SHORT).show();
                } else if (editTextCurrentContact.getText().toString().equals(EMERcontact)) {
                    Toast.makeText(UpdateEMERContactActivity.this, "紧急联系人的信息没有变哦", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateEMERContactActivity.this);
                    builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                    builder.setMessage("确认将紧急联系人修改为：" + editTextCurrentContact.getText().toString() + " 吗？");
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

                            // Net thread
                            if (updateTag == 1) {
                                UpdateEMERContact1Thread updateEMERContact1Thread = new UpdateEMERContact1Thread(UpdateContactHandler);
                                updateEMERContact1Thread.setId(id);
                                updateEMERContact1Thread.setEMERContact_1(editTextCurrentContact.getText().toString());
                                updateEMERContact1Thread.start();
                            } else if (updateTag == 2) {
                                UpdateEMERContact2Thread updateEMERContact2Thread = new UpdateEMERContact2Thread(UpdateContactHandler);
                                updateEMERContact2Thread.setId(id);
                                updateEMERContact2Thread.setEMERContact_2(editTextCurrentContact.getText().toString());
                                updateEMERContact2Thread.start();
                            }
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
     * @Handler UpdateContactHandler
     * @Function Return the Internet Status of updating operation
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
                            getIntent().putExtra("updateTag", updateTag);
                            getIntent().putExtra("updateContact", editTextCurrentContact.getText().toString());
                            setResult(ConstArgument.RESPONSE_UPDATE_CONTACT, getIntent());
                            finish();
                        } else {
                            Toast.makeText(UpdateEMERContactActivity.this, ConstArgument.ALERT_SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case ConstArgument.MSG_INTERNET_ERROR:
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(UpdateEMERContactActivity.this, ConstArgument.ALERT_CHECT_NETWORK, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
