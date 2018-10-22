package cn.zjt.iot.oncar.android.Util;

import cn.zjt.iot.oncar.R;

/**
 * @author Mr Dk.
 * @version 2018.6.13
 * @since 2018.4.16
 */

public class ConstArgument {

    public static final String BLUETOOTH_ADDR = "00:18:E5:03:76:24";
    public static final int BLUETOOTH_DISCOVERY_INTERVAL = 10000;
    public static final String TAG = "MESSAGE";

    public static final int HEART_RATE_COLLECT_INTERVAL = 3000;
    public static final int WEIGHT_COLLECT_INTERVAL = 1000;
    public static final int TEMPERATURE_COLLECT_INTERVAL = 3000;
    public static final int ALCOHOL_COLLECT_INTERVAL = 1000;
    public static final int DOWNLOAD_MODEL_INTERVAL = 30000;
    public static final int CHECK_BLUETOOTH_STATE_INTERVAL = 1000;
    public static final int HEART_RATE_COLLECT_START_DELAY = 1000;
    public static final int TEMPERATURE_COLLECT_START_DELAY = 2000;
    public static final String INSTRUCTION_HEART_RATE = "150";
//    public static final String INSTRUCTION_HEART_RATE = "160";
    public static final String INSTRUCTION_WEIGHT = "110";
    public static final String INSTRUCTION_TEMPERATURE = "140";
    public static final String INSTRUCTION_ALCOHOL = "120";
    public static final String BMI_FORMAT = "BMI | ";
    public static final int HEART_RATE_ANALYSE_AMOUNT = 5;
    public static final int TEMPERATURE_ANALYSE_AMOUNT = 5;

    public static final int MSG_ANIMATION_BIGGER = 1;
    public static final int MSG_ANIMATION_SMALLER = 2;

    public static final int MAX_HEIGHT = 230;
    public static final int MIN_HEIGHT = 100;

    public static final String ALERT_TITLE_CHECK = "确认信息";
    public static final String ALERT_CHECT_NETWORK = "请检查网络连接";
    public static final String ALERT_SERVER_ERROR = "服务器错误";
    public static final String ALERT_SYSTEM_INFO = "系统信息";
    public static final String ALERT_CONNECTING = "正在连接...";
    public static final String ALERT_SURE = "确认";
    public static final String ALERT_CANCEL = "取消";

    public static final int REQUEST_UPDATE_NICKNAME = 10000;
    public static final int RESPONSE_UPDATE_NICKNAME = 10001;
    public static final int REQUEST_UPDATE_HEIGHT = 10002;
    public static final int RESPONSE_UPDATE_HEIGHT = 10003;
    public static final int REQUEST_CREATE_CONTACT = 10004;
    public static final int RESPONSE_CREATE_CONTACT = 10005;
    public static final int REQUEST_UPDATE_CONTACT = 10006;
    public static final int RESPONSE_UPDATE_CONTACT = 10007;

    private static final String HTTP_URL_SERVER = "http://20.191.140.238:8080/OnCarSERVER/";
    private static final String HTTP_URL_EMULATOR = "http://192.168.27.2:8080/OnCarSERVER/";
    public static final String HTTP_URL = HTTP_URL_SERVER;
    public static final int HTTP_TIME_OUT_LIMIT = 8000;

    public static final int TAG_UPPER = 1;
    public static final int TAG_NORMAL = 0;
    public static final int TAG_LOWER = -1;
    public static final int TAG_ABNORMAL = 1;

    public static final int MSG_INTERNET_SUCCESS = 10;
    public static final int MSG_INTERNET_ERROR = 11;
    public static final int MSG_ANALYSE_ERROR = 12;
    public static final int MSG_ANALYSE_NORMAL = 13;
    public static final int MSG_ANALYSE_UPPER = 14;
    public static final int MSG_ANALYSE_LOWER = 15;
    public static final int MSG_BLUETOOTH_CONNECTED = 16;
    public static final int MSG_ANALYSE_ABNORMAL = 17;

    public static final String TAG_HEALTH_TIP = "提示";
    public static final String TAG_HEALTH_MSG = "为了您的行车安全\n我们极力建议您在驾驶前进行一次体检\n以便让您了解目前您是否适合驾驶";
    public static final String TAG_IGNORE_CHECK = "跳过体检";
    public static final String TAG_START_CHECK = "开始检测";
    public static final String TAG_ALCOHOL_CHECK = "酒精浓度检测";
    public static final String TAG_ALCOHOL_TIP = "请点击“开始检测”，并对车载酒精传感器呼气";
    public static final String TAG_ALCOHOL_END = "请对车载酒精传感器呼气\n并在呼气结束后按下采样按钮";
    public static final String TAG_WEIGHT_CHECK = "体重检测";
    public static final String TAG_WEIGHT_TIP = "为确保体重测量准确\n请您静坐于座位上并尽可能保持身体稳定";
    public static final String TAG_WARNING = "警告";
    public static final String TAG_WARNING_TIP = "检测到您体内酒精浓度过高\n我们极不建议您继续驾驶";
    public static final String TAG_CHECK_END = "您的体检已经完成\n开始享受您的驾驶吧！";
    public static final String TAG_SURE = "开始驾驶";
    public static final String TAG_EXIT = "退出";

    public static final String[] MenuText = {
            "更换昵称",
            "更改身高",
            "帮助与反馈",
            "关于"
    };
    public static final String[] MenuOutText = {
            "切换账号"
    };
    public static final int[] MenuIcon = {
            R.drawable.nickname,
            R.drawable.height,
            R.drawable.feedback,
            R.drawable.about
    };
    public static int[] MenuOutIcon = {
            R.drawable.switch_acount
    };

    public static int SOSIcon[] = {R.drawable.ambulance, R.drawable.fireman, R.drawable.policeman};
    public static String SOSItem[] = {"120", "119", "110"};

    public static int Key[] = {5, 15, 2, 4, 9, 11, 16, 3};
    public static boolean Direction[] = {false, false, true, false, true, true, true, false};
}
