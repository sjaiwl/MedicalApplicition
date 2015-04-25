package com.sjaiwl.app.function;

import java.io.Serializable;

/**
 * Created by sjaiwl on 15/3/19.
 */
public class UserInfo implements Serializable {
    public static UserInfo user = null;
    public Integer doctor_id;
    public String doctor_name;
    public String doctor_url;
    public String doctor_password;
    public String doctor_gender;
    public String doctor_birthday;
    public String doctor_job;
    public String doctor_department;
    public String doctor_telephone;

    public static void setUserInfo(UserInfo u) {
        UserInfo.user = u;
    }

    public static void setEmpty() {
        user = null;
    }

    public static boolean isEmpty() {
        if (user == null) {
            return true;
        }
        return false;
    }

    public Integer getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Integer doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public String getDoctor_department() {
        return doctor_department;
    }

    public void setDoctor_department(String doctor_department) {
        this.doctor_department = doctor_department;
    }

    public String getDoctor_job() {

        return doctor_job;
    }

    public void setDoctor_job(String doctor_job) {
        this.doctor_job = doctor_job;
    }

    public String getDoctor_birthday() {

        return doctor_birthday;
    }

    public void setDoctor_birthday(String doctor_birthday) {
        this.doctor_birthday = doctor_birthday;
    }

    public String getDoctor_gender() {
        return doctor_gender;
    }

    public void setDoctor_gender(String doctor_gender) {
        this.doctor_gender = doctor_gender;
    }

    public String getDoctor_password() {

        return doctor_password;
    }

    public void setDoctor_password(String doctor_password) {
        this.doctor_password = doctor_password;
    }

    public String getDoctor_url() {

        return doctor_url;
    }

    public void setDoctor_url(String doctor_url) {
        this.doctor_url = doctor_url;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctor_telephone() {
        return doctor_telephone;
    }

    public void setDoctor_telephone(String doctor_telephone) {
        this.doctor_telephone = doctor_telephone;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "doctor_id=" + doctor_id +
                ", doctor_name='" + doctor_name + '\'' +
                ", doctor_url='" + doctor_url + '\'' +
                ", doctor_password='" + doctor_password + '\'' +
                ", doctor_gender='" + doctor_gender + '\'' +
                ", doctor_birthday='" + doctor_birthday + '\'' +
                ", doctor_job='" + doctor_job + '\'' +
                ", doctor_department='" + doctor_department + '\'' +
                ", doctor_telephone='" + doctor_telephone + '\'' +
                '}';
    }
}
