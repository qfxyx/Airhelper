package com.air2016jnu.airhelper.data;

/**
 * Created by Administrator on 2016/12/5.
 */
public class URLData {
    private static String weatherForecast ="https://free-api.heweather.com/v5/forecast";
    private static String weatherNow="https://free-api.heweather.com/v5/now";
    private static String weatherSuggesttion="https://free-api.heweather.com/v5/suggestion";
    private static String weather="https://free-api.heweather.com/v5/weather";

    public static String getWeatherForecast(){
        return weatherForecast;
    }
    public static String getWeatherNow(){
        return weatherNow;
    }
    public static String getWeatherSuggesttion(){
        return weatherSuggesttion;
    }
    public static String getWeather(){
        return weather;
    }
}
