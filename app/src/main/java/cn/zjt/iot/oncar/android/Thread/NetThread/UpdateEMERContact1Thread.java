package cn.zjt.iot.oncar.android.Thread.NetThread;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Util.SecurityUtil;
import cn.zjt.iot.oncar.android.Activity.UpdateEMERContactActivity;

/**
 * @author Mr Dk.
 * @version 2018.4.22
 * @see UpdateEMERContactActivity
 * @see org.apache.http
 * @since 2018.4.22
 */

public class UpdateEMERContact1Thread extends Thread {

    /**
     * @Variables
     */
    private Handler handler;
    private int id;
    private String EMERContact_1;

    /**
     * @Methods
     */

    /*
     * @Constructor UpdateEMERContact1Thread
     * @Param Handler handler
     */
    public UpdateEMERContact1Thread(Handler handler) {
        this.handler = handler;
    }

    /*
     * @Setter setId
     * @Param int id
     * @Return void
     */
    public void setId(int id) {
        this.id = id;
    }

    /*
     * @Setter setEMERContact_1
     * @Param String EMERContact_1
     * @Return void
     */
    public void setEMERContact_1(String EMERContact_1) {
        this.EMERContact_1 = EMERContact_1;
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
        AndroidHttpClient androidHttpClient = AndroidHttpClient.newInstance("");
        HttpPost httpPost = new HttpPost(ConstArgument.HTTP_URL + "UpdateContact1Servlet");
        try {
            // Upload data
            JSONObject inputPack = new JSONObject();
            inputPack.put("id", id);
            inputPack.put("EMERcontact_1", EMERContact_1);

            HttpEntity httpEntity = new StringEntity(SecurityUtil.Encode(inputPack.toString()) + '\n', "UTF-8");
            httpPost.setEntity(httpEntity);
            httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ConstArgument.HTTP_TIME_OUT_LIMIT);
            httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ConstArgument.HTTP_TIME_OUT_LIMIT);
            HttpResponse httpResponse = androidHttpClient.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // Connection successful
                HttpEntity responseEntity = httpResponse.getEntity();
                JSONObject returnPack = new JSONObject(SecurityUtil.Decode(EntityUtils.toString(responseEntity, "UTF-8")));
                Message msg = handler.obtainMessage(ConstArgument.MSG_INTERNET_SUCCESS, returnPack);
                msg.sendToTarget();
            } else {
                // Connection error
                handler.sendEmptyMessage(ConstArgument.MSG_INTERNET_ERROR);
            }

        } catch (IOException | JSONException e) {
            // Time out
            handler.sendEmptyMessage(ConstArgument.MSG_INTERNET_ERROR);
            //e.printStackTrace();
        } finally {
            if (androidHttpClient != null) {
                androidHttpClient.close();
            }
        }
    }
}
