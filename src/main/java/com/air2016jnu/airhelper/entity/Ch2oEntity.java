package com.air2016jnu.airhelper.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */

public class Ch2oEntity {

    /**
     * type : AllCH2O
     * AllCH2O : [{"Time":"8","CH2O":"51"},{"Time":"7","CH2O":"51"},{"Time":"6","CH2O":"51"},{"Time":"5","CH2O":"51"},{"Time":"4","CH2O":"51"},{"Time":"3","CH2O":"51"},{"Time":"2","CH2O":"51"},{"Time":"1","CH2O":"51"},{"Time":"0","CH2O":"51"},{"Time":"23","CH2O":"43"},{"Time":"22","CH2O":"59"},{"Time":"21","CH2O":"58"},{"Time":"20","CH2O":"64"}]
     * CH2ONow : 19
     * updateTime : 2017-4-28 0:22
     */

    private String type;
    private String CH2ONow;
    private String updateTime;
    private List<AllCH2OBean> AllCH2O;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCH2ONow() {
        return CH2ONow;
    }

    public void setCH2ONow(String CH2ONow) {
        this.CH2ONow = CH2ONow;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<AllCH2OBean> getAllCH2O() {
        return AllCH2O;
    }

    public void setAllCH2O(List<AllCH2OBean> AllCH2O) {
        this.AllCH2O = AllCH2O;
    }

    public static class AllCH2OBean {
        /**
         * Time : 8
         * CH2O : 51
         */

        private String Time;
        private String CH2O;

        public String getTime() {
            return Time;
        }

        public void setTime(String Time) {
            this.Time = Time;
        }

        public String getCH2O() {
            return CH2O;
        }

        public void setCH2O(String CH2O) {
            this.CH2O = CH2O;
        }
    }
}
