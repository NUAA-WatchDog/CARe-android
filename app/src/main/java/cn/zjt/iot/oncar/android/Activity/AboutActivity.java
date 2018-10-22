package cn.zjt.iot.oncar.android.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;

/**
 * @author Mr Dk.
 * @since 2018.5.12
 * @version 2018.5.16
 * @see MineSettingFragment
 */

public class AboutActivity extends FragmentActivity {

    /**
     * @Variables
     */
    private ImageButton aboutBack;

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
        aboutBack = null;
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
        setContentView(R.layout.activity_about);

        FindViews();
        SetButtonEvent();
    }

    /**
     * @Methods
     */

    /*
     * @Method FindViews
     * @Return void
     * @Function Find views by ID
     */
    private void FindViews() {
        aboutBack = (ImageButton) findViewById(R.id.aboutBack);
    }

    /*
     * @Method SetButtonEvent
     * @Return void
     * @Function Set events for buttons
     */
    private void SetButtonEvent() {
        aboutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
