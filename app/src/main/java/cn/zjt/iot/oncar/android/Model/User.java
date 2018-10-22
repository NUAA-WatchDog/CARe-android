package cn.zjt.iot.oncar.android.Model;

import java.io.Serializable;

/**
 * @author Mr Dk.
 * @version 2018.6.13
 * @since 2018.4.18
 */

public class User implements Serializable {

    private int User_id;
    private String User_name;
    private String User_nickname;
    private String User_password;
    private String User_EMERcontact_1;
    private String User_EMERcontact_2;
    private int User_height;
    private int User_hr_version;
    private int User_tp_version;

    public int getUser_id() {
        return User_id;
    }

    public void setUser_id(int user_id) {
        User_id = user_id;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getUser_nickname() {
        return User_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        User_nickname = user_nickname;
    }

    public String getUser_password() {
        return User_password;
    }

    public void setUser_password(String user_password) {
        User_password = user_password;
    }

    public String getUser_EMERcontact_1() {
        return User_EMERcontact_1;
    }

    public void setUser_EMERcontact_1(String user_EMERcontact_1) {
        User_EMERcontact_1 = user_EMERcontact_1;
    }

    public String getUser_EMERcontact_2() {
        return User_EMERcontact_2;
    }

    public void setUser_EMERcontact_2(String user_EMERcontact_2) {
        User_EMERcontact_2 = user_EMERcontact_2;
    }

    public int getUser_height() {
        return User_height;
    }

    public void setUser_height(int user_height) {
        User_height = user_height;
    }

    public int getUser_hr_version() {
        return User_hr_version;
    }

    public void setUser_hr_version(int user_hr_version) {
        User_hr_version = user_hr_version;
    }

    public int getUser_tp_version() {
        return User_tp_version;
    }

    public void setUser_tp_version(int user_tp_version) {
        User_tp_version = user_tp_version;
    }
}
