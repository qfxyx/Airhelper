package com.air2016jnu.airhelper.data;

import android.text.StaticLayout;

/**
 * Created by Administrator on 2016/12/5.
 */
public class Info {

    private static String weatherKey ="d6b96cb399d043728d94720891fe61c9";
    private static String weatherConfiKey="weatherConfigure";
    private static String weatherAllKey="allJsonDetail";
    private static String defaultCity="广州";
    private static String UserCityKey="UserCity";

    public static String bleBackDataKey="received_data_form_ble";
    public static String bleNowKey="data_from_ble_now";
    public static String bleTempKeyX="data_from_ble_tempX";
    public static String bleTempKeyY="data_from_ble_tempY";
    public static String bleTempUpdate="data_from_ble_tempU";
    public static String bleTempNow ="data_from_ble_tempN";
    public static String bleDustKeyX="data_from_ble_dustX";
    public static String bleDustKeyY="data_from_ble_dustY";
    public static String weatherIsOk="WEATHER_RES_OK";
    public static String JosnBackOk="ok";
    public static String cityIsWrong="unknown city";
    //commamd
    public static String commandTemp = "#temp";
    public static String commandHumitity = "#humi";
    public static String commandAllnow = "#now";
    public static String commandDust = "#dust";
    public static String commandCh20 = "#ch2o";

    public static String getWeatherKey(){
        return weatherKey;
    }
    public static String getWeatherConfiKey(){
        return weatherConfiKey;
    }
    public static String getWeatherAllKey(){
        return weatherAllKey;
    }
    public static String getDefaultCity(){
        return defaultCity;
    }
    public static String getUserCityKey(){

        return UserCityKey;
    }
    public static String getUserCity(){
        ConfigurationHelper helper = new ConfigurationHelper(MyApplication.getContext(),weatherConfiKey);
        return helper.getString(UserCityKey,defaultCity);
    }

}
