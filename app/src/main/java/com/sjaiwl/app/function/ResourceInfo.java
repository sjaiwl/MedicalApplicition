package com.sjaiwl.app.function;

import java.io.Serializable;

/**
 * Created by sjaiwl on 15/3/20.
 */
public class ResourceInfo implements Serializable {
    private Integer id;
    private Integer resource_type;
    private Integer user_id;
    private Integer suffer_id;
    private String patient_name;
    private String patient_url;
    private String resource_url;
    private String resource_size;
    private String resource_category;
    private String updated_at;
    private String resource_description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPatient_url() {
        return patient_url;
    }

    public void setPatient_url(String patient_url) {
        this.patient_url = patient_url;
    }

    public String getPatient_name() {
        return patient_name+SelectType(resource_type);
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }


    public Integer getResource_type() {
        return resource_type;
    }

    public void setResource_type(Integer resource_type) {
        this.resource_type = resource_type;
    }

    public Integer getSuffer_id() {
        return suffer_id;
    }

    public void setSuffer_id(Integer suffer_id) {
        this.suffer_id = suffer_id;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }

    public String getResource_size() {
        return resource_size;
    }

    public void setResource_size(String resource_size) {
        this.resource_size = resource_size;
    }

    public String getResource_category() {
        return resource_category;
    }

    public void setResource_category(String resource_category) {
        this.resource_category = resource_category;
    }

    public String getResource_description() {
        return resource_description;
    }

    public void setResource_description(String resource_description) {
        this.resource_description = resource_description;
    }

    public String SelectType(int type){
        String str=null;
        switch (type){
            case 1:
                str="的文档";
                break;
            case 2:
                str="的照片";
                break;
            case 3:
                str="的视频";
                break;
            case 4:
                str="的音频";
                break;
            default:
                break;
        }
        return  str;
    }

    @Override
    public String toString() {
        return "ResourceInfo{" +
                "id=" + id +
                ", resource_type=" + resource_type +
                ", user_id=" + user_id +
                ", suffer_id=" + suffer_id +
                ", patient_name='" + patient_name + '\'' +
                ", patient_url='" + patient_url + '\'' +
                ", resource_url='" + resource_url + '\'' +
                ", resource_size='" + resource_size + '\'' +
                ", resource_category='" + resource_category + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", resource_description='" + resource_description + '\'' +
                '}';
    }
}
