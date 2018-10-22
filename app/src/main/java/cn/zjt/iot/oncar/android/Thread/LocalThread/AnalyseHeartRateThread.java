package cn.zjt.iot.oncar.android.Thread.LocalThread;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Filter.ModelHeartRateFileFilter;
import cn.zjt.iot.oncar.android.Fragment.BodyDataFragment;
import cn.zjt.iot.oncar.android.Model.User;

/**
 * @author Mr Dk.
 * @version 2018.5.19
 * @see BodyDataFragment
 * @since 2018.5.19
 */

public class AnalyseHeartRateThread extends Thread {

    /**
     * @Variables
     */

    /*
     * @Variable Heart-rate record
     */
    private Vector<Integer> vector;

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
     * @Constructor AnalyseHeartRateThread
     * @Param Vector<Integer> vector
     * @Param File fileDir
     * @Param Handler handler
     * @Param User user
     */
    public AnalyseHeartRateThread(Vector<Integer> vector, File fileDir, Handler handler, User user) {
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
        StringBuilder heartRecord = new StringBuilder();
        for (int i = 0; i < vector.size(); i++) {
            heartRecord.append(' ');
            heartRecord.append(i + 1);
            heartRecord.append(':');
            heartRecord.append(vector.elementAt(i));
        }
        return heartRecord;
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
        ModelHeartRateFileFilter modelFileFilter = new ModelHeartRateFileFilter(user.getUser_id());
        File[] AllFiles = fileDir.listFiles(modelFileFilter);

        if (user.getUser_hr_version() == 0) {
            // New user
            StringBuilder heartRecord = GetAnalyseInput();
            String uploadRecord = ConstArgument.TAG_NORMAL + heartRecord.toString();

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
            StringBuilder heartRecord = GetAnalyseInput();
            String analyseInput = "1" + heartRecord;
            double analyseOutput = 0.0;

            // Analyse
            try {
                analyseOutput = svm_predict.main(fileDir, AllFiles[0].getName(), analyseInput);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String outputRecord = ((int) analyseOutput) + heartRecord.toString();
            System.out.println("Analyse result: " + analyseOutput);

            // Handle message
            Message msg = null;
            //System.out.println(analyseOutput + "hr----------");
            if ((int) analyseOutput == ConstArgument.TAG_NORMAL) {
                // NORMAL
                msg = handler.obtainMessage(ConstArgument.MSG_ANALYSE_NORMAL, outputRecord);
                msg.sendToTarget();
            } else {
                msg = handler.obtainMessage(ConstArgument.MSG_ANALYSE_ABNORMAL, outputRecord);
                msg.sendToTarget();
            } /*else if ((int) analyseOutput == ConstArgument.TAG_UPPER) {
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
