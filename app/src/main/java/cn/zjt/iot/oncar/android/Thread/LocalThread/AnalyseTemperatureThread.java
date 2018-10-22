package cn.zjt.iot.oncar.android.Thread.LocalThread;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import cn.zjt.iot.oncar.android.Filter.ModelHeartRateFileFilter;
import cn.zjt.iot.oncar.android.Filter.ModelTemperatureFileFilter;
import cn.zjt.iot.oncar.android.Fragment.BodyDataFragment;
import cn.zjt.iot.oncar.android.Model.User;
import cn.zjt.iot.oncar.android.Util.ConstArgument;

/**
 * @author Mr Dk.
 * @version 2018.6.12
 * @see BodyDataFragment
 * @since 2018.6.12
 */

public class AnalyseTemperatureThread extends Thread {

    /**
     * @Variables
     */

    /*
     * @Variable Temperature record
     */
    private Vector<Float> vector;

    /*
     * @Variable Model path
     */
    private File fileDir;

    private Handler handler;
    private User user;

    /**
     * @Methods
     */

    /*
     * @Constructor AnalyseTemperatureThread
     * @Param Vector<Float> vector
     * @Param File fileDir
     * @Param Handler handler
     * @Param User user
     */
    public AnalyseTemperatureThread(Vector<Float> vector, File fileDir, Handler handler, User user) {
        this.vector = vector;
        this.fileDir = fileDir;
        this.handler = handler;
        this.user = user;
    }

    /*
     * @Method GetAnalyseInput
     * @Return StringBuilder
     * @Function Build the string of input for SVM
     */
    private StringBuilder GetAnalyseInput() {
        StringBuilder temperatureRecord = new StringBuilder();
        for (int i = 0; i < vector.size(); i++) {
            temperatureRecord.append(' ');
            temperatureRecord.append(i + 1);
            temperatureRecord.append(':');
            temperatureRecord.append(vector.elementAt(i));
        }
        return temperatureRecord;
    }

    /**
     * @Override
     */

    /*
     * @Override run
     * @Return void
     * @LifeCycle One time running
     */
    @Override
    public void run() {

        System.out.println("Analyse start");
        Calendar startTime = Calendar.getInstance();

        // JUDGE EXISTENCE OF MODEL
        ModelTemperatureFileFilter modelFileFilter = new ModelTemperatureFileFilter(user.getUser_id());
        File[] AllFiles = fileDir.listFiles(modelFileFilter);

        if (user.getUser_tp_version() == 0) {
            // New user
            StringBuilder temperatureRecord = GetAnalyseInput();
            String uploadRecord = ConstArgument.TAG_NORMAL + temperatureRecord.toString();

            Message msg = handler.obtainMessage(ConstArgument.MSG_ANALYSE_NORMAL, uploadRecord);
            msg.sendToTarget();

        } else if (AllFiles.length == 0) {
            // No model
            handler.sendEmptyMessage(ConstArgument.MSG_ANALYSE_ERROR);
        } else {
            /*
             * MODEL EXISTS
             * INPUT : _index:data_index:data_index:data ... ...
             * OUTPUT : TAG
             */

            // Construct the input string
            StringBuilder temperatureRecord = GetAnalyseInput();
            String analyseInput = "0" + temperatureRecord;
            double analyseOutput = 0.0;
            System.out.println(analyseInput);

            // Analyse
            try {
                analyseOutput = svm_predict.main(fileDir, AllFiles[0].getName(), analyseInput);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String outputRecord = ((int) analyseOutput) + temperatureRecord.toString();
            System.out.println("Analyse result: " + analyseOutput);

            // Handle message
            Message msg = null;
            //System.out.println(analyseOutput + "tp----------");
            if ((int) analyseOutput == ConstArgument.TAG_NORMAL) {
                // NORMAL
                msg = handler.obtainMessage(ConstArgument.MSG_ANALYSE_NORMAL, outputRecord);
                msg.sendToTarget();
            } else {
                msg = handler.obtainMessage(ConstArgument.MSG_ANALYSE_ABNORMAL, outputRecord);
                msg.sendToTarget();
            }
            /*else if ((int) analyseOutput == ConstArgument.TAG_UPPER) {
                // UPPER
                msg = handler.obtainMessage(ConstArgument.MSG_ANALYSE_UPPER, outputRecord);
                msg.sendToTarget();
            } else if ((int) analyseOutput == ConstArgument.TAG_LOWER) {
                // LOWER
                msg = handler.obtainMessage(ConstArgument.MSG_ANALYSE_LOWER, outputRecord);
                msg.sendToTarget();
            }*/

            System.out.println("Analyse Time: " + (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) + "ms");
        }

        // Reset the Vector
        vector.clear();

        System.out.println("Analyse END");
    }
}
