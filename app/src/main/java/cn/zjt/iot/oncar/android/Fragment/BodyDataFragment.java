package cn.zjt.iot.oncar.android.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechSynthesizer;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Vector;

import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Activity.MainActivity;
import cn.zjt.iot.oncar.android.Model.User;
import cn.zjt.iot.oncar.android.Thread.LocalThread.AnalyseTemperatureThread;
import cn.zjt.iot.oncar.android.Thread.LocalThread.CollectAlcoholThread;
import cn.zjt.iot.oncar.android.Thread.LocalThread.AnalyseHeartRateThread;
import cn.zjt.iot.oncar.android.Thread.LocalThread.BluetoothConnectionCheckThread;
import cn.zjt.iot.oncar.android.Thread.LocalThread.CollectTemperatureThread;
import cn.zjt.iot.oncar.android.Thread.LocalThread.CollectHeartRateThread;
import cn.zjt.iot.oncar.android.Thread.LocalThread.HeartRateDisplayThread;
import cn.zjt.iot.oncar.android.Thread.LocalThread.CollectWeightThread;
import cn.zjt.iot.oncar.android.Thread.NetThread.DownloadHeartRateModelThread;
import cn.zjt.iot.oncar.android.Thread.NetThread.DownloadTemperatureModelThread;
import cn.zjt.iot.oncar.android.Thread.NetThread.TrainHeartRateModelThread;
import cn.zjt.iot.oncar.android.Thread.NetThread.TrainTemperatureModelThread;
import cn.zjt.iot.oncar.android.Thread.NetThread.UploadHeartRateThread;
import cn.zjt.iot.oncar.android.Thread.NetThread.UploadTemperatureThread;
import cn.zjt.iot.oncar.android.Thread.NetThread.UploadWeightThread;
import cn.zjt.iot.oncar.android.Util.BluetoothChatUtil;
import cn.zjt.iot.oncar.android.Util.ConstArgument;

/**
 * @author Mr Dk.
 * @version 2018.6.12
 * @see MainActivity
 * @since 2018.4.20
 */

public class BodyDataFragment extends Fragment {

    /**
     * @Variables
     */

    /*
     * @Variable User model
     */
    private User user;

    /*
     * @Variable Bluetooth
     */
    private BluetoothChatUtil bluetoothChatUtil;
    private ImageView imageViewBluetoothStatus;

    /*
     * @Variable Heart-rate animation
     */
    private int heartDuration = 2000;
    private AlphaAnimation riseAlphaAnimation;
    private AlphaAnimation downAlphaAnimation;
    private ScaleAnimation riseScaleAnimation;
    private ScaleAnimation downScaleAnimation;

    /*
     * @Variable Views for displaying
     */
    private TextView textViewHeartRate;
    private TextView textViewTemperature;
    private TextView textViewWeight;
    private TextView textViewBMI;
    private TextView textViewAlcohol;
    private ImageView imageViewHeartRate;
    private ImageView imageViewTemperature;
    private ImageView imageViewAlcohol;
    private ImageView imageViewBodyCheck;
    private DecimalFormat decimalFormat = new DecimalFormat("#.0");

    /*
     * @Variable View for interaction
     */
    private ProgressDialog progressDialog;

    /*
     * @Variable For controlling the data collecting thread
     */
    private boolean heartRateStart = false;
    private boolean temperatureStart = false;

    /*
     * @Variable Threads for sending instructions to STM-32
     */
    private CollectHeartRateThread collectHeartRateThread;
    private CollectTemperatureThread collectTemperatureThread;
    private CollectWeightThread collectWeightThread;
    private CollectAlcoholThread collectAlcoholThread;

    /*
     * @Variable Threads for downloading training model
     */
    private DownloadHeartRateModelThread downloadHeartRateModelThread;
    private DownloadTemperatureModelThread downloadTemperatureModelThread;

    /*
     * @Variable Thread for analysing heart-rate and temperature
     */
    private Vector<Integer> heartRateVector = new Vector<>();
    private Vector<Float> temperatureVector = new Vector<>();
    private AnalyseHeartRateThread analyseHeartRateThread;
    private AnalyseTemperatureThread analyseTemperatureThread;

    /**
     * @Variable For TTS speaking
     * @OpenSource http://www.xfyun.cn/
     * @Support IFLYTEK CO.,LTD.
     */
    private SpeechSynthesizer speechSynthesizer;

    /**
     * @Override
     */

    /*
     * @Override onDestroyView
     * @Return void
     * @Return Destroy this Fragment
     */
    @Override
    public void onDestroyView() {

        // Disconnect with bluetooth
        bluetoothChatUtil.disconnect();
        bluetoothChatUtil.unregisterHandler();

        // Handler
        blueToothHandler.removeCallbacksAndMessages(null);
        blueToothHandler = null;
        heartRateDisplayHandler.removeCallbacksAndMessages(null);
        heartRateDisplayHandler = null;
        AnalyseHeartRateHandler.removeCallbacksAndMessages(null);
        AnalyseHeartRateHandler = null;
        checkBluetoothStateHandler.removeCallbacksAndMessages(null);
        checkBluetoothStateHandler = null;
        UploadHeartRateHandler.removeCallbacksAndMessages(null);
        UploadHeartRateHandler = null;
        UploadTemperatureHandler.removeCallbacksAndMessages(null);
        UploadTemperatureHandler = null;
        UploadWeightHandler.removeCallbacksAndMessages(null);
        UploadWeightHandler = null;

        // Train model REQUEST
        TrainHeartRateModelThread trainHeartRateModelThread = new TrainHeartRateModelThread();
        TrainTemperatureModelThread trainTemperatureModelThread = new TrainTemperatureModelThread();
        trainHeartRateModelThread.setId(user.getUser_id());
        trainTemperatureModelThread.setId(user.getUser_id());
        trainHeartRateModelThread.start();
        trainTemperatureModelThread.start();

        // Kill all working thread
        if (collectHeartRateThread.isAlive()) {
            collectHeartRateThread.Kill();
        }
        if (collectTemperatureThread.isAlive()) {
            collectTemperatureThread.Kill();
        }
        if (collectWeightThread.isAlive()) {
            collectWeightThread.Kill();
        }
        if (collectAlcoholThread.isAlive()) {
            collectAlcoholThread.Kill();
        }
        if (downloadHeartRateModelThread.isAlive()) {
            downloadHeartRateModelThread.Kill();
        }

        collectHeartRateThread = null;
        collectTemperatureThread = null;
        collectWeightThread = null;
        collectAlcoholThread = null;
        downloadHeartRateModelThread = null;
        downloadTemperatureModelThread = null;
        analyseHeartRateThread = null;
        analyseTemperatureThread = null;

        imageViewHeartRate = null;
        imageViewTemperature = null;
        imageViewAlcohol = null;
        textViewBMI = null;
        textViewHeartRate = null;
        textViewTemperature = null;
        textViewWeight = null;
        textViewAlcohol = null;
        imageViewBluetoothStatus = null;
        imageViewBodyCheck = null;
        progressDialog = null;
        heartRateVector = null;

        super.onDestroyView();
    }

    /*
     * @Override onActivityResult
     * @Param int requestCode
     * @Param int resultCode
     * @Param Intent data
     * @Return void
     * @Function Receive the result from other Activity
     * @Function Set BMI with height and weight
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstArgument.REQUEST_UPDATE_HEIGHT &&
                resultCode == ConstArgument.RESPONSE_UPDATE_HEIGHT) {
            SetBMI();
        }
    }

    /*
     * @Override onCreateView
     * @Param LayoutInflater inflater
     * @Param ViewGroup container
     * @Param Bundle savedInstanceState
     * @Return View
     * @Function Create Views for this Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_data, null);

        InitView(view);
        RegisterBluetoothHandler();
        InitHeartRateViewAnimation();
        InitWorkingThreads();
        StartBodyTest();

        return view;
    }

    /**
     * @Methods
     */

    /*
     * @Setter setSpeechSynthesizer
     * @Param SpeechSynthesizer speechSynthesizer
     * @Return void
     */
    public void setSpeechSynthesizer(SpeechSynthesizer speechSynthesizer) {
        this.speechSynthesizer = speechSynthesizer;
    }

    /*
     * @Setter setUser
     * @Param User user
     * @Return void
     */
    public void setUser(User user) {
        this.user = user;
    }

    /*
     * @Setter setBluetoothChatUtil
     * @Param BluetoothChatUtil bluetoothChatUtil
     * @Return void
     */
    public void setBluetoothChatUtil(BluetoothChatUtil bluetoothChatUtil) {
        this.bluetoothChatUtil = bluetoothChatUtil;
    }

    /*
     * @Method InitView
     * @Param View view
     * @Return void
     * @Function Initializing Views in this Fragment
     */
    private void InitView(View view) {
        imageViewHeartRate = (ImageView) view.findViewById(R.id.imageViewHeartRate);
        imageViewTemperature = (ImageView) view.findViewById(R.id.imageViewTemperature);
        imageViewAlcohol = (ImageView) view.findViewById(R.id.imageViewAlcohol);
        textViewBMI = (TextView) view.findViewById(R.id.textViewBMI);
        textViewHeartRate = (TextView) view.findViewById(R.id.textViewHeartRate);
        textViewTemperature = (TextView) view.findViewById(R.id.textViewTemperature);
        textViewWeight = (TextView) view.findViewById(R.id.textViewWeight);
        textViewAlcohol = (TextView) view.findViewById(R.id.textViewAlcohol);
        imageViewBluetoothStatus = (ImageView) getActivity().findViewById(R.id.imageViewBluetoothStatus);
        imageViewBodyCheck = (ImageView) getActivity().findViewById(R.id.imageViewBodyCheckStatus);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
    }

    /*
     * @Method RegisterBluetoothHandler
     * @Return void
     * @Function To register handler for bluetooth
     * @See blueToothHandler
     */
    private void RegisterBluetoothHandler() {
        bluetoothChatUtil.registerHandler(blueToothHandler);
    }

    /*
     * @Method InitHeartRateViewAnimation
     * @Return void
     * @Function Initializing animation of heart-rate View
     */
    private void InitHeartRateViewAnimation() {
        riseAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        downAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        riseScaleAnimation = new ScaleAnimation(
                0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        downScaleAnimation = new ScaleAnimation(
                1.0f, 0.5f, 1.0f, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
    }

    /*
     * @Method InitWorkingThreads
     * @Return void
     * @Function Initializing working threads
     */
    private void InitWorkingThreads() {
        collectHeartRateThread = new CollectHeartRateThread(bluetoothChatUtil);
        collectTemperatureThread = new CollectTemperatureThread(bluetoothChatUtil);
        collectWeightThread = new CollectWeightThread(bluetoothChatUtil);
        collectAlcoholThread = new CollectAlcoholThread(bluetoothChatUtil);

        downloadHeartRateModelThread = new DownloadHeartRateModelThread(user, getActivity().getFilesDir());
        downloadTemperatureModelThread = new DownloadTemperatureModelThread(user, getActivity().getFilesDir());
        downloadHeartRateModelThread.start();
        downloadTemperatureModelThread.start();
    }

    /*
     * @Method StartBodyTest
     * @Return void
     * @Function Show a AlertDialog to start body check
     */
    public void StartBodyTest() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(ConstArgument.TAG_HEALTH_TIP);
        builder.setIcon(R.drawable.body_check_no);
        builder.setMessage(ConstArgument.TAG_HEALTH_MSG);
        builder.setCancelable(false);
        builder.setNegativeButton(ConstArgument.TAG_IGNORE_CHECK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StartRealTimeCollection();
            }
        });
        builder.setPositiveButton(ConstArgument.TAG_START_CHECK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (collectHeartRateThread.isAlive()) {
                    collectHeartRateThread.Pause();
                }
                if (collectTemperatureThread.isAlive()) {
                    collectTemperatureThread.Pause();
                }

                progressDialog.setTitle(ConstArgument.ALERT_SYSTEM_INFO);
                progressDialog.setMessage("正在等待蓝牙连接...");
                progressDialog.show();
                if (speechSynthesizer != null) {
                    speechSynthesizer.startSpeaking("正在连接蓝牙，请稍后。", null);
                }

                // Check the connection state of bluetooth
                BluetoothConnectionCheckThread BluetoothConnectionCheckThread =
                        new BluetoothConnectionCheckThread(checkBluetoothStateHandler);
                BluetoothConnectionCheckThread.setBluetoothChatUtil(bluetoothChatUtil);
                BluetoothConnectionCheckThread.start();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }

    /*
     * @Method StartRealTimeCollection
     * @Return void
     * @Function Start working thread to collect data
     * @Called while body check is skipped
     */
    private void StartRealTimeCollection() {
        if (collectHeartRateThread.isAlive()) {
            collectHeartRateThread.Resume();
        } else {
            collectHeartRateThread.start();
        }

        if (collectTemperatureThread.isAlive()) {
            collectTemperatureThread.Resume();
        } else {
            collectTemperatureThread.start();
        }
    }

    /*
     * @Method DealWithWeight
     * @Param int data
     * @Return void
     * @Function Convert from original data to weight
     * @Function Show weight on Views
     * @Function Set BMI on Views
     * @Function Upload weight to server
     * @see bluetoothHandler
     */

    /**
     * @see UploadWeightThread
     */
    private void DealWithWeight(int data) {

        // Convert to "KG"
        Float weight = (float) data / 1000.0f;

        if (weight > 30.0 && weight < 180.0) {
            textViewWeight.setText(decimalFormat.format(weight));
            imageViewBodyCheck.setImageResource(R.drawable.body_check_ok);
            imageViewBodyCheck.setTag("ok");
            SetBMI();

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (collectWeightThread.isAlive()) {
                collectWeightThread.Kill();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(ConstArgument.TAG_HEALTH_TIP);
            builder.setMessage(ConstArgument.TAG_CHECK_END);
            builder.setIcon(R.drawable.alcohol_ok);
            builder.setCancelable(false);
            builder.setPositiveButton(ConstArgument.TAG_SURE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StartRealTimeCollection();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
            if (speechSynthesizer != null) {
                speechSynthesizer.startSpeaking(ConstArgument.TAG_CHECK_END, null);
            }

            /*// Upload
            UploadWeightThread uploadWeightThread = new UploadWeightThread(UploadWeightHandler);
            uploadWeightThread.setId(user.getUser_id());
            uploadWeightThread.setWeight(weight);
            uploadWeightThread.start();*/
        }

        System.out.println("WEIGHT DATA:" + data);
        System.out.println("WEIGHT CAST:" + weight);
    }

    /*
     * @Method DealWithAlcohol
     * @Param int data
     * @Return void
     * @Function Judge from data
     * @Called in body check
     * @See SetBMI
     */
    private void DealWithAlcohol(int data) {

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (data == 0) {
            // Alcohol normal
            imageViewAlcohol.setImageResource(R.drawable.alcohol_ok);
            textViewAlcohol.setText("<");

            builder.setTitle(ConstArgument.TAG_WEIGHT_CHECK);
            builder.setMessage(ConstArgument.TAG_WEIGHT_TIP);
            builder.setIcon(R.drawable.scale);
            builder.setCancelable(false);
            builder.setNegativeButton(ConstArgument.TAG_IGNORE_CHECK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StartRealTimeCollection();
                }
            });
            builder.setPositiveButton(ConstArgument.TAG_START_CHECK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.setMessage("体重数据采集中...");
                    progressDialog.show();
                    /*if (speechSynthesizer != null) {
                        speechSynthesizer.startSpeaking("", null);
                    }*/

                    collectWeightThread.start();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
            if (speechSynthesizer != null) {
                speechSynthesizer.startSpeaking(ConstArgument.TAG_WEIGHT_TIP, null);
            }

        } else {
            // Too much alcohol
            imageViewAlcohol.setImageResource(R.drawable.alcohol_no);
            imageViewBodyCheck.setImageResource(R.drawable.body_check_dangerous);
            imageViewBodyCheck.setTag("dangerous");
            textViewAlcohol.setText("≥");

            builder.setTitle(ConstArgument.TAG_WARNING);
            builder.setMessage(ConstArgument.TAG_WARNING_TIP);
            builder.setCancelable(false);
            builder.setIcon(R.drawable.alcohol_no);
            builder.setNegativeButton(ConstArgument.TAG_EXIT, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            builder.setPositiveButton(ConstArgument.TAG_SURE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StartRealTimeCollection();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
            if (speechSynthesizer != null) {
                speechSynthesizer.startSpeaking(ConstArgument.TAG_WARNING_TIP, null);
            }

            // Make the title of the AlertDialog RED
            try {
                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object mAlertController = mAlert.get(alertDialog);
                Field mMessage = mAlertController.getClass().getDeclaredField("mTitleView");
                mMessage.setAccessible(true);
                TextView mMessageView = (TextView) mMessage.get(mAlertController);
                mMessageView.setTextColor(Color.RED);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * @Method DealWithTemperature
     * @Param int data
     * @Return void
     * @Function Convert from original data to temperature
     * @Function Show on display View
     * @Function Upload temperature record
     */

    /**
     * @see UploadTemperatureThread
     */
    private void DealWithTemperature(int data) {

        // Temperature in "°C"
        Float temperature = (float) data / 100f;

        if (temperature > 1.0f && temperature < 45.0f) {

            if (!temperatureStart) {
                temperatureStart = true;
                imageViewTemperature.setImageResource(R.drawable.temperature_normal);
            } else {
                if (temperature < Float.parseFloat(textViewTemperature.getText().toString())) {
                    imageViewTemperature.setImageResource(R.drawable.temperature_cold);
                } else if (temperature > Float.parseFloat(textViewTemperature.getText().toString())) {
                    imageViewTemperature.setImageResource(R.drawable.temperature_hot);
                } else {
                    imageViewTemperature.setImageResource(R.drawable.temperature_normal);
                }
            }

            textViewTemperature.setText(decimalFormat.format(temperature));

            temperatureVector.add(temperature);
            if (temperatureVector.size() >= ConstArgument.TEMPERATURE_ANALYSE_AMOUNT) {

                // Start analysing
                analyseTemperatureThread = new AnalyseTemperatureThread(
                        temperatureVector,
                        getActivity().getFilesDir(),
                        AnalyseTemperatureHandler,
                        user
                );
                analyseTemperatureThread.start();
            }
        }

        System.out.println("TEMPERATURE DATA:" + data);
        System.out.println("TEMPERATURE CAST:" + temperature);
    }

    /*
     * @Method DealWithHeartRate
     * @Param int data
     * @Return void
     * @Function Show heart-rate on Views
     * @Function Start analysing thread while the number of records is enough
     */

    /**
     * @see AnalyseHeartRateThread
     */
    private void DealWithHeartRate(int data) {
        if (data > 30 && data < 200) {

            // For data display
            textViewHeartRate.setText(Integer.toString(data));
            // For animation display
            if (data > 150) {
                heartDuration = 1000 * 60 / 150;
            } else {
                heartDuration = 1000 * 60 / data;
            }
            // For analysing
            heartRateVector.add(data);

            if (!heartRateStart) {
                heartRateStart = true;
                heartRateDisplayHandler.sendEmptyMessage(ConstArgument.MSG_ANIMATION_SMALLER);
            }

            if (heartRateVector.size() >= ConstArgument.HEART_RATE_ANALYSE_AMOUNT) {

                // Start analysing
                analyseHeartRateThread = new AnalyseHeartRateThread(
                        heartRateVector,
                        getActivity().getFilesDir(),
                        AnalyseHeartRateHandler,
                        user
                );
                analyseHeartRateThread.start();
            }
        }

        System.out.println("HEART-RATE:" + data);
    }

    /*
     * @Method SetBMI
     * @Return void
     * @Function Set the BMI of user with height and weight
     * @See DealWithWeight
     */
    private void SetBMI() {
        if (!textViewWeight.getText().toString().equals("/") &&
                user.getUser_height() != -1) {
            Float weight = Float.parseFloat(textViewWeight.getText().toString());
            Float BMI = weight / (user.getUser_height() * user.getUser_height() / 10000.0f);
            textViewBMI.setText(ConstArgument.BMI_FORMAT + decimalFormat.format(BMI));

            //System.out.println(weight);
            //System.out.println(user.getUser_height());
            //System.out.println(BMI);
        }
    }

    /**
     * @Handlers
     */

    /*
     * @Handler checkBluetoothStateHandler
     * @Function Called back while bluetooth is connected
     * @Function To Start alcohol collect
     * @See StartBodyTest
     */
    /**
     * @see CollectAlcoholThread
     */
    private Handler checkBluetoothStateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == ConstArgument.MSG_BLUETOOTH_CONNECTED) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(ConstArgument.TAG_ALCOHOL_CHECK);
                builder.setMessage(ConstArgument.TAG_ALCOHOL_TIP);
                builder.setIcon(R.drawable.alcohol_unknown);
                builder.setCancelable(false);
                builder.setNegativeButton(ConstArgument.TAG_IGNORE_CHECK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StartRealTimeCollection();
                    }
                });
                builder.setPositiveButton(ConstArgument.TAG_START_CHECK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage(ConstArgument.TAG_ALCOHOL_END);
                        progressDialog.show();
                        if (speechSynthesizer != null) {
                            speechSynthesizer.startSpeaking(ConstArgument.TAG_ALCOHOL_END, null);
                        }

                        collectAlcoholThread.start();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
                if (speechSynthesizer != null) {
                    speechSynthesizer.startSpeaking(ConstArgument.TAG_ALCOHOL_TIP, null);
                }
            }
            return false;
        }
    });

    /*
     * @Handler heartRateDisplayHandler
     * @Function Start heart-rate animation according to duration
     */
    private Handler heartRateDisplayHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (imageViewHeartRate != null) {
                if (msg.what == ConstArgument.MSG_ANIMATION_BIGGER) {

                    riseAlphaAnimation.setDuration(heartDuration);
                    riseScaleAnimation.setDuration(heartDuration);
                    imageViewHeartRate.clearAnimation();

                    AnimationSet animationSet = new AnimationSet(true);
                    animationSet.addAnimation(riseAlphaAnimation);
                    animationSet.addAnimation(riseScaleAnimation);
                    imageViewHeartRate.startAnimation(animationSet);
                } else if (msg.what == ConstArgument.MSG_ANIMATION_SMALLER) {

                    downAlphaAnimation.setDuration(heartDuration);
                    downScaleAnimation.setDuration(heartDuration);
                    imageViewHeartRate.clearAnimation();

                    AnimationSet animationSet = new AnimationSet(true);
                    animationSet.addAnimation(downAlphaAnimation);
                    animationSet.addAnimation(downScaleAnimation);
                    imageViewHeartRate.startAnimation(animationSet);

                    HeartRateDisplayThread heartRateDisplayThread =
                            new HeartRateDisplayThread(heartRateDisplayHandler, heartDuration);
                    heartRateDisplayThread.start();
                }
            }
            return false;
        }
    });

    /*
     * @Handler blueToothHandler
     * @Function For bluetooth status display
     * @Function Dealing with message received by bluetooth
     * @See DealWithHeartRate
     * @See DealWithTemperature
     * @See DealWithWeight
     * @See DealWithAlcohol
     */
    private Handler blueToothHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothChatUtil.STATE_CONNECTED:
                    String deviceName = msg.getData().getString(BluetoothChatUtil.DEVICE_NAME);
                    System.out.println("CONNECTED:" + deviceName);
                    Toast.makeText(getContext(), "传感器连接成功", Toast.LENGTH_SHORT).show();
                    imageViewBluetoothStatus.setImageResource(R.drawable.bluetooth);

                    break;

                case BluetoothChatUtil.STATE_CONNECT_FAILURE:
                    System.out.println("CONNECT FAILURE");
                    Toast.makeText(getContext(), "传感器连接失败", Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothChatUtil.MESSAGE_DISCONNECTED:
                    //Toast.makeText(getContext(), "传感器连接断开", Toast.LENGTH_SHORT).show();
                    System.out.println("DISCONNECT");
                    if (imageViewBluetoothStatus != null) {
                        imageViewBluetoothStatus.setImageResource(R.drawable.bluetoothoff);
                    }

                    break;

                case BluetoothChatUtil.MESSAGE_READ: {
                    byte[] buf = msg.getData().getByteArray(BluetoothChatUtil.READ_MSG);

                    /*
                     * buf[0] ---- Address
                     */

                    /*
                     * buf[2][3][4][5] ---- Data
                     * <ATTENTION><Data stored in little endian>
                     * int data = buf[5] buf[4] buf[3] buf[2]
                     */
                    int data = 0;
                    for (int i = 5; i > 1; i--) {
                        int temp = buf[i];
                        temp &= 0x000000ff;
                        data |= temp;

                        if (i != 2) {
                            data <<= 8;
                        }
                    }

                    // Decode
                    for (int i = 7; i >= 0; i--) {
                        if (ConstArgument.Direction[i]) {
                            data = (data << ConstArgument.Key[i]) |
                                    (data >>> (32 - ConstArgument.Key[i]));
                        } else {
                            data = (data >>> ConstArgument.Key[i]) |
                                    (data << (32 - ConstArgument.Key[i]));
                        }
                    }

                    /*
                     * buf[1] ---- Data type
                     *
                     * '1' ==== Weight
                     * '2' ==== Alcohol
                     * '3' ====
                     * '4' ==== Temperature
                     * '5' ==== HeartRate
                     */
                    switch (buf[1]) {
                        case '1':
                            // Weight
                            DealWithWeight(data);
                            break;
                        case '2':
                            // Alcohol
                            DealWithAlcohol(data);
                            break;
                        case '4':
                            // Temperature
                            DealWithTemperature(data);
                            break;
                        case '5':
                            // HeartRate
                            DealWithHeartRate(data);
                            break;
                        default:
                            break;
                    }

                    break;
                }
                case BluetoothChatUtil.MESSAGE_WRITE: {
                    byte[] buf = (byte[]) msg.obj;

                    //String str = new String(buf, 0, buf.length);
                    //System.out.println(str);

                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });


    /*
     * @Handler UploadWeightHandler
     * @Function Status of uploading weight record
     */
    /**
     * @see UploadWeightThread
     */
    private Handler UploadWeightHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConstArgument.MSG_INTERNET_SUCCESS:
                    break;
                case ConstArgument.MSG_INTERNET_ERROR:
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /*
     * @Handler UploadTemperatureHandler
     * @Function Status of uploading temperature record
     */
    /**
     * @see UploadTemperatureThread
     */
    private Handler UploadTemperatureHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConstArgument.MSG_INTERNET_SUCCESS:
                    System.out.println("TEMP UPLOAD SUCCESS");
                    break;
                case ConstArgument.MSG_INTERNET_ERROR:
                    System.out.println("TEMP UPLOAD ERROR");
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /*
     * @Handler UploadHeartRateHandler
     * @Function Status of uploading heart-rate record
     */
    /**
     * @see UploadHeartRateThread
     */
    private Handler UploadHeartRateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConstArgument.MSG_INTERNET_SUCCESS:
                    System.out.println("HEART UPLOAD SUCCESS");
                    break;
                case ConstArgument.MSG_INTERNET_ERROR:
                    System.out.println("HEART UPLOAD FAILURE");
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /*
     * @Handler AnalyseHeartRateHandler
     * @Function Receiving analysing result of heart-rate
     * @Function Uploading heart-rate record
     * @Function Providing TTS speaking according to result
     */
    /**
     * @see UploadHeartRateThread
     * @see AnalyseHeartRateThread
     */
    private Handler AnalyseHeartRateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConstArgument.MSG_ANALYSE_NORMAL:
                case ConstArgument.MSG_ANALYSE_UPPER:
                case ConstArgument.MSG_ANALYSE_LOWER:
                case ConstArgument.MSG_ANALYSE_ABNORMAL:
                    // Upload result and record
                    UploadHeartRateThread uploadHeartRateThread
                            = new UploadHeartRateThread(UploadHeartRateHandler);
                    uploadHeartRateThread.setId(user.getUser_id());
                    uploadHeartRateThread.setHeartRate((String) msg.obj);
                    uploadHeartRateThread.start();

                    // For TTS speaking
                    //System.out.println(msg.what + "----------------------");
                    if (msg.what == ConstArgument.MSG_ANALYSE_NORMAL) {
                        //speechSynthesizer.startSpeaking("心率正常", null);
                    } else {
                        speechSynthesizer.startSpeaking("心率异常", null);
                        System.out.println("心率异常");

                        if (user.getUser_EMERcontact_1() != null && user.getUser_EMERcontact_1() != "") {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.getUser_EMERcontact_1()));
                            startActivity(intent);
                        } else if (user.getUser_EMERcontact_2() != null && user.getUser_EMERcontact_2() != "") {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.getUser_EMERcontact_2()));
                            startActivity(intent);
                        }

                    } /*else if (msg.what == ConstArgument.MSG_ANALYSE_UPPER) {
                        speechSynthesizer.startSpeaking("心率偏高", null);
                    } else if (msg.what == ConstArgument.MSG_ANALYSE_LOWER) {
                        speechSynthesizer.startSpeaking("心率偏低", null);
                    }*/

                    break;

                case ConstArgument.MSG_ANALYSE_ERROR:
                    // NO MODEL FOUND
                    Toast.makeText(getActivity(), "未找到模型", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /*
     * @Handler AnalyseTemperatureHandler
     * @Function Receiving analysing result of temperature
     * @Function Uploading temperature record
     * @Function Providing TTS speaking according to result
     */
    /**
     * @see UploadTemperatureThread
     * @see AnalyseTemperatureThread
     */
    private Handler AnalyseTemperatureHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ConstArgument.MSG_ANALYSE_NORMAL:
                case ConstArgument.MSG_ANALYSE_UPPER:
                case ConstArgument.MSG_ANALYSE_LOWER:
                case ConstArgument.MSG_ANALYSE_ABNORMAL:
                    // Upload result and record
                    UploadTemperatureThread uploadTemperatureThread = new UploadTemperatureThread(UploadTemperatureHandler);
                    uploadTemperatureThread.setId(user.getUser_id());
                    uploadTemperatureThread.setTemperature((String) msg.obj);
                    uploadTemperatureThread.start();

                    // For TTS speaking
                    //System.out.println(msg.what + "----------------------");
                    if (msg.what == ConstArgument.MSG_ANALYSE_NORMAL) {
                        //speechSynthesizer.startSpeaking("温度正常", null);
                    } else {
                        speechSynthesizer.startSpeaking("温度异常", null);
                        System.out.println("温度异常");
                    } /*else if (msg.what == ConstArgument.MSG_ANALYSE_UPPER) {
                        speechSynthesizer.startSpeaking("温度偏高", null);
                    } else if (msg.what == ConstArgument.MSG_ANALYSE_LOWER) {
                        speechSynthesizer.startSpeaking("温度偏低", null);
                    }*/

                    break;

                case ConstArgument.MSG_ANALYSE_ERROR:
                    // NO MODEL FOUND
                    Toast.makeText(getActivity(), "未找到模型", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
