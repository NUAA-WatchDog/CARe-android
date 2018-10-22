package cn.zjt.iot.oncar.android.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;

/**
 * @author Mr Dk.
 * @version 2018.5.16
 * @see MineSettingFragment
 * @since 2018.5.12
 */

public class FeedbackActivity extends FragmentActivity {

    /**
     * @Variables
     */
    private ImageButton feedbackBack;

    /**
     * @Override
     */

    /*
     * @Override onDestroy()
     * @Return void
     * @Function Recycle the memory of this Activity
     */
    @Override
    protected void onDestroy() {
        feedbackBack = null;
        super.onDestroy();
    }

    /*
     * @Override onCreate(Bundle savedInstanceState)
     * @Return void
     * @Function Create this Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        FindViews();
        SetButtonEvent();
    }

    /**
     * @Methods
     */

    /*
     * @Method FindViews()
     * @Return void
     * @Function Find views by ID
     */
    private void FindViews() {
        feedbackBack = (ImageButton) findViewById(R.id.feedbackBack);
    }

    /*
     * @Method SetButtonEvent()
     * @Return void
     * @Function Set events for buttons
     */
    private void SetButtonEvent() {
        feedbackBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
