package com.air2016jnu.airhelper.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.data.ConfigurationHelper;
import com.air2016jnu.airhelper.data.Info;
import com.air2016jnu.airhelper.data.URLData;
import com.air2016jnu.airhelper.entity.WeatherAll;
import com.air2016jnu.airhelper.interfaces.ButtonAction;
import com.air2016jnu.airhelper.utils.BackButtonAction;
import com.air2016jnu.airhelper.utils.HttpWeather;
import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherActivity extends BaseActivity{

    private  int[] icons;
    private  String[] iconNames;
    private GridView gridView;
    private List<Map<String,Object>> gridViewList;
    private SimpleAdapter simpleAdapter;
    private TextView textView;

    MyBroadcastReceiver receiver;
    ProgressDialog waitingDialog;

    @Override
    public void onBaseCreate(Bundle bundle) {
        setContentViewBody(R.layout.activity_weather);
        initDatas();
        initViews();

    }

    private void initViews(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (waitingDialog==null){
                   craeteDialog();
               }
                waitingDialog.show();
                HttpWeather weather = new HttpWeather(Info.getUserCity(), URLData.getWeather());
                weather.getWeatherJson(Info.getWeatherAllKey());
            }
        });
         textView = (TextView) findViewById(R.id.weather_text);
        gridView=(GridView)findViewById(R.id.weather_gridview);
        String[] from = {"image","text"};
        int[] to ={R.id.image,R.id.text};
        simpleAdapter = new SimpleAdapter(this,gridViewList,R.layout.weather_item,from,to);
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                         ConfigurationHelper helper =new ConfigurationHelper
                                 (WeatherActivity.this,Info.getWeatherConfiKey());
                        String json = helper.getString(Info.getWeatherAllKey(),"noEffect");
                        textView.setText(json);

                        break;
                    case 1:
                    HttpWeather weather = new HttpWeather(Info.getUserCity(),URLData.getWeatherSuggesttion());
                        weather.getWeatherJson(Info.getWeatherAllKey());
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    default:
                        break;
                }
            }
        });

        setlBtnActionAction(new BackButtonAction(this));



    }
    private void initDatas(){

       icons= new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher,
               R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        iconNames=new String[]{"天气预报","实时天气","生活指数","灾害预警"};
        gridViewList=new ArrayList<>();
        for (int i=0;i<iconNames.length;i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image",icons[i]);
            map.put("text",iconNames[i]);
            gridViewList.add(map);
        }
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Info.weatherIsOk);
        registerReceiver(receiver,filter);

    }
    private class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            textView.setText("broadcast test");
            if(waitingDialog!=null&&waitingDialog.isShowing()){
                waitingDialog.dismiss();
            }
        }
    }
    @Override
    public void onDestroy(){
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    private void craeteDialog(){
        if (waitingDialog==null){
            waitingDialog=new ProgressDialog(WeatherActivity.this);
            waitingDialog.setTitle("正在更新");
            waitingDialog.setMessage("联网更新中...");
            waitingDialog.setCancelable(false);
        }
    }


}
