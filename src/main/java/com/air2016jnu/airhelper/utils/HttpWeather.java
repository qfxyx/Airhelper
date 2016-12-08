package com.air2016jnu.airhelper.utils;

import android.app.Application;
import android.content.Intent;

import com.air2016jnu.airhelper.activity.MainActivity;
import com.air2016jnu.airhelper.data.ConfigurationHelper;
import com.air2016jnu.airhelper.data.Info;
import com.air2016jnu.airhelper.data.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/12/6.
 */
public class HttpWeather {

    String queryURL="";
    private String res="noEffect";

    public HttpWeather(String city,String url){
        String temp = URLEncoder.encode(city);
        queryURL=url+"?"+"city="+temp+"&"+"key="+Info.getWeatherKey();
        System.out.println(queryURL);
    }
    public void getWeatherJson(final String storeKey){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(queryURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(3000);
                    if (httpURLConnection.getResponseCode()==200){
                        InputStream is =httpURLConnection.getInputStream();
                        ByteArrayOutputStream out=new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len =0;
                        while ((len=is.read(buffer))!=-1){
                            out.write(buffer,0,len);
                        }
                        res = out.toString();
                        Thread.sleep(500);
                        out.close();
                        is.close();

                    }

                }catch (Exception ex){

                }finally {
                    ConfigurationHelper helper = new ConfigurationHelper
                            (MyApplication.getContext(),Info.getWeatherConfiKey());
                    helper.setString(storeKey,res);
                    Intent intent = new Intent();
                    intent.setAction(Info.weatherIsOk);
                    MyApplication.getContext().sendBroadcast(intent);
                }
            }
        }).start();

    }


}
