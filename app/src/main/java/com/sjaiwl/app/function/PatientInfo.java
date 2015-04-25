package com.sjaiwl.app.function;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by sjaiwl on 15/3/19.
 */
public class PatientInfo implements Serializable {
    private Integer id;
    private Integer patient_hospitalNumber;
    private String patient_name;
    private String patient_gender;
    private Integer patient_age;
    private Integer patient_height;
    private Integer patient_weight;
    private String patient_telephone;
    private String patient_idNumber;
    private String patient_residence;
    private String patient_situation;
    private String patient_url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPatient_hospitalNumber() {
        return patient_hospitalNumber;
    }

    public void setPatient_hospitalNumber(Integer patient_hospitalNumber) {
        this.patient_hospitalNumber = patient_hospitalNumber;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatient_gender() {
        return patient_gender;
    }

    public void setPatient_gender(String patient_gender) {
        this.patient_gender = patient_gender;
    }

    public Integer getPatient_age() {
        return patient_age;
    }

    public void setPatient_age(Integer patient_age) {
        this.patient_age = patient_age;
    }

    public Integer getPatient_height() {
        return patient_height;
    }

    public void setPatient_height(Integer patient_height) {
        this.patient_height = patient_height;
    }

    public Integer getPatient_weight() {
        return patient_weight;
    }

    public void setPatient_weight(Integer patient_weight) {
        this.patient_weight = patient_weight;
    }

    public String getPatient_telephone() {
        return patient_telephone;
    }

    public void setPatient_telephone(String patient_telephone) {
        this.patient_telephone = patient_telephone;
    }

    public String getPatient_idNumber() {
        return patient_idNumber;
    }

    public void setPatient_idNumber(String patient_idNumber) {
        this.patient_idNumber = patient_idNumber;
    }

    public String getPatient_residence() {
        return patient_residence;
    }

    public void setPatient_residence(String patient_residence) {
        this.patient_residence = patient_residence;
    }

    public String getPatient_situation() {
        return patient_situation;
    }

    public void setPatient_situation(String patient_situation) {
        this.patient_situation = patient_situation;
    }

    public String getPatient_url() {
        return patient_url;
    }

    public void setPatient_url(String patient_url) {
        this.patient_url = patient_url;
    }

    @Override
    public String toString() {
        return "PatientInfo{" +
                "patient_id=" + id +
                ", patient_hospitalNumber=" + patient_hospitalNumber +
                ", patient_name='" + patient_name + '\'' +
                ", patient_gender='" + patient_gender + '\'' +
                ", patient_age='" + patient_age + '\'' +
                ", patient_height='" + patient_height + '\'' +
                ", patient_weight='" + patient_weight + '\'' +
                ", patient_telephone='" + patient_telephone + '\'' +
                ", patient_idNumber='" + patient_idNumber + '\'' +
                ", patient_residence='" + patient_residence + '\'' +
                ", patient_situation='" + patient_situation + '\'' +
                ", patient_url='" + patient_url + '\'' +
                '}';
    }
}
