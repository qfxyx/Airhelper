package com.air2016jnu.airhelper.entity;

/**
 * Created by Administrator on 2017/4/21.
 */

public class AllEntity {

    /**
     * type : All
     * All : {"Temp":"26.0","Humi":"73.0","Dust":"0.002739","CH2O":"254"}
     * updateTime : 23:59
     */

    private String type;
    private AllBean All;
    private String updateTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AllBean getAll() {
        return All;
    }

    public void setAll(AllBean All) {
        this.All = All;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public static class AllBean {
        /**
         * Temp : 26.0
         * Humi : 73.0
         * Dust : 0.002739
         * CH2O : 254
         */

        private String Temp;
        private String Humi;
        private String Dust;
        private String CH2O;

        public String getTemp() {
            return Temp;
        }

        public void setTemp(String Temp) {
            this.Temp = Temp;
        }

        public String getHumi() {
            return Humi;
        }

        public void setHumi(String Humi) {
            this.Humi = Humi;
        }

        public String getDust() {
            return Dust;
        }

        public void setDust(String Dust) {
            this.Dust = Dust;
        }

        public String getCH2O() {
            return CH2O;
        }

        public void setCH2O(String CH2O) {
            this.CH2O = CH2O;
        }
    }
}
