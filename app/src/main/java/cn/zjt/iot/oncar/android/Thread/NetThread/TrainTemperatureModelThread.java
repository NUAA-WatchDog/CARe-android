package cn.zjt.iot.oncar.android.Thread.NetThread;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;

import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;
import cn.zjt.iot.oncar.android.Util.ConstArgument;

/**
 * @author Mr Dk.
 * @version 2018.6.12
 * @see MineSettingFragment
 * @since 2018.6.12
 */

public class TrainTemperatureModelThread extends Thread {

    /**
     * @Variables
     */
    private int id;

    /**
     * @Methods
     */

    /*
     * @Setter setId
     * @Param int id
     * @Return void
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @Override
     */

    /*
     * @Override run
     * @Return void
     */
    @Override
    public void run() {

        System.out.println("Train temperature REQUEST");

        AndroidHttpClient androidHttpClient = AndroidHttpClient.newInstance("");
        HttpGet httpGet = new HttpGet(ConstArgument.HTTP_URL + "TrainTemperatureModelServlet" + "?id=" + id);
        try {
            // Request
            httpGet.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ConstArgument.HTTP_TIME_OUT_LIMIT);
            httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ConstArgument.HTTP_TIME_OUT_LIMIT);
            HttpResponse httpResponse = androidHttpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // Connection successful
                System.out.println("TrainHeartRate REQUEST submit SUCCESSFULLY");
            } else {
                // Connection error
                System.out.println(httpResponse.getStatusLine().getStatusCode());
                System.out.println("TrainHeartRate REQUEST submit ERROR");
            }

        } catch (IOException e) {
            // Time out
            //e.printStackTrace();
        } finally {
            if (androidHttpClient != null) {
                androidHttpClient.close();
            }
        }

        System.out.println("Train temperature END");
    }
}
