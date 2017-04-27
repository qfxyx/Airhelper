package com.air2016jnu.airhelper.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/9.
 */
public class TempEntity {


    /**
     * type : AllTemp
     * AllTemp : [{"Time":"0","Temp":"25.0"},{"Time":"20","Temp":"27.0"},{"Time":"1","Temp":"28.4"},{"Time":"1","Temp":"28.0"},{"Time":"1","Temp":"28.0"}]
     * tempNow : 25.0
     * updateTime : 23:27
     */

    private String type;
    private String tempNow;
    private String updateTime;
    private List<AllTempBean> AllTemp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTempNow() {
        return tempNow;
    }

    public void setTempNow(String tempNow) {
        this.tempNow = tempNow;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<AllTempBean> getAllTemp() {
        return AllTemp;
    }

    public void setAllTemp(List<AllTempBean> AllTemp) {
        this.AllTemp = AllTemp;
    }

    public static class AllTempBean {
        /**
         * Time : 0
         * Temp : 25.0
         */

        private String Time;
        private String Temp;

        public String getTime() {
            return Time;
        }

        public void setTime(String Time) {
            this.Time = Time;
        }

        public String getTemp() {
            return Temp;
        }

        public void setTemp(String Temp) {
            this.Temp = Temp;
        }
    }
}
