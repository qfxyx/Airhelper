package com.air2016jnu.airhelper.data;

import android.text.StaticLayout;

/**
 * Created by Administrator on 2016/12/5.
 */
public class Info {
    //online weather
    private static String weatherKey ="d6b96cb399d043728d94720891fe61c9";
    private static String weatherConfiKey="weatherConfigure";
    private static String weatherAllKey="allJsonDetail";
    private static String defaultCity="广州";
    private static String UserCityKey="UserCity";
    public static String weatherIsOk="WEATHER_RES_OK";
    public static String JosnBackOk="ok";
    public static String cityIsWrong="unknown city";
    //ble key word
    public static String bleBackDataKey="received_data_form_ble";
    //ble now key
    public static String bleNowKey="data_from_ble_now";
    //temperature
    public static String bleTempKeyX="data_from_ble_tempX";
    public static String bleTempKeyY="data_from_ble_tempY";
    public static String bleTempUpdate="data_from_ble_tempU";
    public static String bleTempNow ="data_from_ble_tempN";
    //dust
    public static String bleDustKeyX="data_from_ble_dustX";
    public static String bleDustKeyY="data_from_ble_dustY";
    public static String bleDustUpdate="data_from_ble_dustU";
    public static String bleDustNow="data_from_ble_dustN";
    //ch2o
    public static String bleCh2oKeyX="data_from_ble_ch2oX";
    public static String bleCh2oKeyY="data_from_ble_ch2oY";
    public static String bleCh2oUpdate="data_from_ble_ch2oU";
    public static String bleCh20Now="data_from_ble_ch2oN";

    //humi
    public static String bleHumiNow="data_from_ble_humiN";
    public static String bleHumiUpdate="data_from_ble_humiU";
    public static String bleHumiKeyX="data_from_ble_humiX";
    public static String bleHumiKeyY="data_from_ble_humiY";
    //out data
    public static String bleOutKey="data_from_ble_out";
    //ble command
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
