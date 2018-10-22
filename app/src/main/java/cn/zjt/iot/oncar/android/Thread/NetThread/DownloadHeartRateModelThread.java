package cn.zjt.iot.oncar.android.Thread.NetThread;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Filter.ModelHeartRateFileFilter;
import cn.zjt.iot.oncar.android.Util.SecurityUtil;
import cn.zjt.iot.oncar.android.Fragment.BodyDataFragment;
import cn.zjt.iot.oncar.android.Model.User;

/**
 * @author Mr Dk.
 * @version 2018.5.21
 * @see BodyDataFragment
 * @see ModelHeartRateFileFilter
 * @see org.apache.http
 * @since 2018.5.20
 */

public class DownloadHeartRateModelThread extends Thread {

    /**
     * @Variables
     */
    private int id;
    private int version;
    private boolean endFlag;
    private File fileDir;

    /**
     * @Methods
     */

    /*
     * @Constructor DownloadHeartRateModelThread
     * @Param User user
     * @Param File fileDir
     */
    public DownloadHeartRateModelThread(User user, File fileDir) {
        this.endFlag = false;
        this.id = user.getUser_id();
        this.version = user.getUser_hr_version();
        this.fileDir = fileDir;
    }

    /*
     * @Method Kill
     * @Return void
     * @Function Kill this thread
     */
    public void Kill() {
        endFlag = true;
    }

    /**
     * @Override
     */

    /*
     * @Override run
     * @Return void
     * @LifeCycle Forever until being killed manually
     */
    @Override
    public void run() {

        System.out.println("Download heart-rate MODEL start");

        // Find the model file
        ModelHeartRateFileFilter modelFileFilter = new ModelHeartRateFileFilter(id);
        File[] AllFiles = fileDir.listFiles(modelFileFilter);

        // File name of newest model
        StringBuilder NewestVersion = new StringBuilder();
        NewestVersion.append("model_heart_rate_");
        NewestVersion.append(id);
        NewestVersion.append("_");
        NewestVersion.append(version);
        NewestVersion.append(".txt");

        while (!endFlag) {
            if (version == 0) {
                // Newly registered user
                // Server does not have a model yet
                // Do not request

            } else if (AllFiles.length == 0 || !AllFiles[0].getName().equals(NewestVersion.toString())) {
                // NO MODEL FILE
                // or MODEL OUT OF DATE
                AndroidHttpClient androidHttpClient = AndroidHttpClient.newInstance("");
                HttpGet httpGet = new HttpGet(ConstArgument.HTTP_URL + "DownloadHeartRateModelServlet" + "?id=" + id);
                try {
                    // Request with HttpGet
                    httpGet.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ConstArgument.HTTP_TIME_OUT_LIMIT);
                    httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ConstArgument.HTTP_TIME_OUT_LIMIT);
                    HttpResponse httpResponse = androidHttpClient.execute(httpGet);

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        // Connection successful
                        HttpEntity responseEntity = httpResponse.getEntity();
                        JSONObject returnPack = new JSONObject(SecurityUtil.Decode(EntityUtils.toString(responseEntity, "UTF-8")));

                        if (returnPack.getBoolean("status")) {
                            // Receive model successfully
                            String model = returnPack.getString("model");

                            File new_model = new File(fileDir, NewestVersion.toString());
                            if (new_model.createNewFile()) {
                                PrintWriter pw = new PrintWriter(new_model);
                                pw.print(model);
                                pw.close();

                                if (AllFiles.length != 0) {
                                    // Delete older version
                                    AllFiles[0].delete();
                                }

                                Kill();
                            } else {
                                System.out.println("Create heart-rate Model file failure");
                            }
                        }
                    } else {
                        // Connection error
                        System.out.println(httpResponse.getStatusLine().getStatusCode());
                        System.out.println("Download heart-rate model ERROR");
                    }

                } catch (IOException e) {
                    // Time out
                    //e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (androidHttpClient != null) {
                        androidHttpClient.close();
                    }
                }
            }

            try {
                Thread.sleep(ConstArgument.DOWNLOAD_MODEL_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Download heart-rate MODEL end");
    }
}
