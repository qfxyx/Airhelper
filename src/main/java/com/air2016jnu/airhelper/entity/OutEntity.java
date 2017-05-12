package com.air2016jnu.airhelper.entity;

/**
 * Created by Administrator on 2017/5/3.
 */

public class OutEntity {

    /**
     * Temp : 25.0
     * Humi : 89.0
     * Dust : 0.312178
     */

    private String Temp;
    private String Humi;
    private String Dust;

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
}
