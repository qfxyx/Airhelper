package com.air2016jnu.airhelper.data;

/**
 * Created by Administrator on 2016/12/5.
 */
public class Info {

    private static String weatherKey ="d6b96cb399d043728d94720891fe61c9";
    private static String weatherConfiKey="weatherConfigure";
    private static String weatherAllKey="allJsonDetail";
    private static String defaultCity="广州";
    private static String UserCityKey="UserCity";

    public static String weatherIsOk="WEATHER_RES_OK";

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
