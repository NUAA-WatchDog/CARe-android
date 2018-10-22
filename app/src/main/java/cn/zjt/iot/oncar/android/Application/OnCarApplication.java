package cn.zjt.iot.oncar.android.Application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author Mr Dk.
 * @version 2018.5.22
 * @since 2018.5.22
 */

public class OnCarApplication extends Application {

    /**
     * @Override
     */

    /*
     * @Override onCreate
     * @Return void
     */
    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }
}
