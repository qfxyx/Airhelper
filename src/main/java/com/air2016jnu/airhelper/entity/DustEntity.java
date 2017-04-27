package com.air2016jnu.airhelper.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/10.
 */
public class DustEntity {

    /**
     * type : AllDust
     * AllDust : [{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"},{"Time":"0","Dust":"0.000000"}]
     * DustNow : 0.001611
     * updateTime : 0:36
     */

    private String type;
    private String DustNow;
    private String updateTime;
    private List<AllDustBean> AllDust;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDustNow() {
        return DustNow;
    }

    public void setDustNow(String DustNow) {
        this.DustNow = DustNow;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<AllDustBean> getAllDust() {
        return AllDust;
    }

    public void setAllDust(List<AllDustBean> AllDust) {
        this.AllDust = AllDust;
    }

    public static class AllDustBean {
        /**
         * Time : 0
         * Dust : 0.000000
         */

        private String Time;
        private String Dust;

        public String getTime() {
            return Time;
        }

        public void setTime(String Time) {
            this.Time = Time;
        }

        public String getDust() {
            return Dust;
        }

        public void setDust(String Dust) {
            this.Dust = Dust;
        }
    }
}
