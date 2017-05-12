package com.air2016jnu.airhelper.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */

public class HumiEntity {


    /**
     * type : AllHumi
     * AllHumi : [{"Time":"8","Humi":"91.0"},{"Time":"7","Humi":"91.0"},{"Time":"6","Humi":"91.0"},{"Time":"5","Humi":"91.0"},{"Time":"4","Humi":"91.0"},{"Time":"3","Humi":"91.0"},{"Time":"2","Humi":"91.0"},{"Time":"1","Humi":"91.0"},{"Time":"0","Humi":"91.0"},{"Time":"23","Humi":"92.0"},{"Time":"22","Humi":"92.0"},{"Time":"21","Humi":"92.0"},{"Time":"20","Humi":"92.0"}]
     * HumiNow : 72.0
     * updateTime : 2017-4-28 0:31
     */

    private String type;
    private String HumiNow;
    private String updateTime;
    private List<AllHumiBean> AllHumi;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHumiNow() {
        return HumiNow;
    }

    public void setHumiNow(String HumiNow) {
        this.HumiNow = HumiNow;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<AllHumiBean> getAllHumi() {
        return AllHumi;
    }

    public void setAllHumi(List<AllHumiBean> AllHumi) {
        this.AllHumi = AllHumi;
    }

    public static class AllHumiBean {
        /**
         * Time : 8
         * Humi : 91.0
         */

        private String Time;
        private String Humi;

        public String getTime() {
            return Time;
        }

        public void setTime(String Time) {
            this.Time = Time;
        }

        public String getHumi() {
            return Humi;
        }

        public void setHumi(String Humi) {
            this.Humi = Humi;
        }
    }
}
