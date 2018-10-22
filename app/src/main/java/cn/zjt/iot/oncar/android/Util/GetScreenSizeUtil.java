package cn.zjt.iot.oncar.android.Util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author Mr Dk.
 * @version 2018.4.19
 * @since 2018.4.19
 */

public class GetScreenSizeUtil {

    /**
     * @Methods
     */

    /*
     * @Method GetScreenHeight
     * @Param Context context
     * @Return int
     */
    public static int GetScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /*
     * @Method GetScreenWidth
     * @Param Context context
     * @Return int
     */
    public static int GetScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
