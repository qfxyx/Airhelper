package com.air2016jnu.airhelper.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
    private TextView cityName;
    private TextView dateText;
    private TextView temperature;
    private ImageView weatherPic;

    private WeatherAll weatherAll;
    private WeatherAll.HeWeather5Bean weather5Bean;

    private int SET_TEXT_FLAG = 0;

    MyBroadcastReceiver receiver;
    ProgressDialog waitingDialog;

    @Override
    public void onBaseCreate(Bundle bundle) {
        setContentViewBody(R.layout.activity_weather);
        initDatas();
        initViews();
        basicWeatherInit();
        setForecast();

    }

    private void initViews(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateWeather();

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
                         setForecast();
                        SET_TEXT_FLAG=0;

                        break;
                    case 1:
                        setWeatherNow();
                        SET_TEXT_FLAG=1;

                        break;
                    case 2:
                        setLifeSuggestion();
                        SET_TEXT_FLAG=2;

                        break;
                    case 3:
                        setAQI();
                        SET_TEXT_FLAG=3;

                        break;
                    default:
                        break;
                }
            }
        });

        setlBtnActionAction(new BackButtonAction(this));
        setrBtnActionAction(new ButtonAction() {
            @Override
            public void act() {
                changeCityDialog();
            }
        });
        setActivityTitle("在线天气");
        cityName=(TextView)findViewById(R.id.weather_cityName);
        dateText=(TextView)findViewById(R.id.weather_date);
        temperature=(TextView)findViewById(R.id.weather_temp);
        weatherPic=(ImageView)findViewById(R.id.weather_pic);



    }
    private void initDatas(){

       icons= new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher,
               R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        iconNames=new String[]{"天气预报","实时天气","生活指数","空气指数"};
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
            //update the bean when the json info updates
            jsonToBean();
            basicWeatherInit();
            if(waitingDialog!=null&&waitingDialog.isShowing()){
                waitingDialog.dismiss();
            }
            setInfoAfterUpdate();
            System.out.print("Update success !");
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
    private void jsonToBean(){
        ConfigurationHelper helper =new ConfigurationHelper
                (WeatherActivity.this,Info.getWeatherConfiKey());
        String json = helper.getString(Info.getWeatherAllKey(),"noEffect");
        if (!json.equals("noEffect")){
            Gson gson = new Gson();
            weatherAll = gson.fromJson(json,WeatherAll.class);
            weather5Bean = weatherAll.getHeWeather5().get(0);

        }
    }
    private void basicWeatherInit(){
        jsonToBean();
        if(BackJsonIsOK()){
            cityName.setText(weather5Bean.getBasic().getCity());
            dateText.setText("更新于 "+weather5Bean.getBasic().getUpdate().getLoc());
            //sometimes it will return null for the Tmp,not find a solution yet
            temperature.setText(weather5Bean.getNow().getTmp()+"°C");
            setWeatherPic();
        }



    }
    private void setForecast(){
        if (BackJsonIsOK()){
            setWeatherPic();
            List<WeatherAll.HeWeather5Bean.DailyForecastBean> dailyList =
                    weather5Bean.getDaily_forecast();
            StringBuilder builder = new StringBuilder();
            for (WeatherAll.HeWeather5Bean.DailyForecastBean bean:dailyList){
                builder.append("日期："+bean.getDate()+"\n");
                builder.append("温度："+bean.getTmp().getMin()+" - "+bean.getTmp().getMax()+"°C\n");
                builder.append("天气：白天-"+bean.getCond().getTxt_d()+"   夜晚-"+bean.getCond().getTxt_n());
                builder.append("\n湿度："+bean.getHum()+"   能见度："+bean.getVis()+"\n");
                builder.append("风向："+bean.getWind().getDir()+"   风力等级："+bean.getWind().getSc());
                builder.append("\n****************\n");

            }
            textView.setText(builder);
        }


    }
    private void setWeatherNow(){
        if (BackJsonIsOK()){
            setWeatherPic();
            StringBuilder builder = new StringBuilder();
            WeatherAll.HeWeather5Bean.NowBean nowBean = weather5Bean.getNow();
            builder.append("更新时间："+weather5Bean.getBasic().getUpdate().getLoc()+"\n");
            builder.append("天气状况："+nowBean.getCond().getTxt()+"\n");
            builder.append("实时温度："+nowBean.getTmp()+"\n");
            builder.append("体感温度："+nowBean.getFl()+"\n");
            builder.append("相对湿度："+nowBean.getHum()+"\n");
            builder.append("降水量："+nowBean.getPcpn()+"\n");
            builder.append("能见度："+nowBean.getVis()+"\n");
            builder.append("气压："+nowBean.getPres()+"\n");
            builder.append("风向："+nowBean.getWind().getDir()+"\n风力："+
                    nowBean.getWind().getSc()+"\n风速："+nowBean.getWind().getSpd());

            textView.setText(builder);
        }


    }
    private void setLifeSuggestion(){
        if (BackJsonIsOK()){
            setWeatherPic();
            StringBuilder builder = new StringBuilder();
            WeatherAll.HeWeather5Bean.SuggestionBean suggestion = weather5Bean.getSuggestion();
            builder.append("舒适度指数："+suggestion.getComf().getBrf()+"\n"+"详细描述："+suggestion.getComf().getTxt()+"\n\n");
            builder.append("紫外线指数："+suggestion.getUv().getBrf()+"\n"+"详细描述："+suggestion.getUv().getTxt()+"\n\n");
            builder.append("穿衣指数："+suggestion.getDrsg().getBrf()+"\n"+"详细描述："+suggestion.getDrsg().getTxt()+"\n\n");
            builder.append("感冒指数："+suggestion.getFlu().getBrf()+"\n"+"详细描述："+suggestion.getFlu().getTxt()+"\n\n");
            builder.append("运动指数："+suggestion.getSport().getBrf()+"\n"+"详细描述："+suggestion.getSport().getTxt()+"\n\n");
            builder.append("旅游指数："+suggestion.getTrav().getBrf()+"\n"+"详细描述："+suggestion.getTrav().getTxt()+"\n");
            textView.setText(builder);
        }

    }

    private void setAQI(){
        if (BackJsonIsOK()){
            StringBuilder stringBuilder = new StringBuilder();
            setWeatherNow();
            try {
                WeatherAll.HeWeather5Bean.AqiBean.CityBean aqiBean = weather5Bean.getAqi().getCity();
                stringBuilder.append("空气质量："+aqiBean.getQlty()+"\n");
                stringBuilder.append("AQI:"+aqiBean.getAqi()+"\n");
                stringBuilder.append("PM2.5："+aqiBean.getPm25()+"\n");
                stringBuilder.append("PM1010："+aqiBean.getPm10()+"\n");
                stringBuilder.append("SO2："+aqiBean.getSo2()+"\n");
                stringBuilder.append("NO2："+aqiBean.getNo2()+"\n");
                stringBuilder.append("CO:"+aqiBean.getCo()+"\n");
                stringBuilder.append("O3："+aqiBean.getO3()+"");
                textView.setText(stringBuilder);
            }catch (Exception ex){
                textView.setText("抱歉，当前城市暂没发布空气质量指数！");
            }
            }

    }
     private void setInfoAfterUpdate(){
         switch (SET_TEXT_FLAG){
             case 0:
                 setForecast();
                 break;
             case 1:
                 setWeatherNow();
                 break;
             case 2:
                 setLifeSuggestion();
                 break;
             case 3:
                 setAQI();
                 break;
             default:
                 break;
         }

     }
    private void changeCityDialog(){
        final EditText editText = new EditText(this);
        editText.setText(new ConfigurationHelper(this,Info.getWeatherConfiKey())
                .getString(Info.getUserCityKey(),Info.getDefaultCity()));
         new AlertDialog.Builder(this).setTitle("请输入城市！")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String city = editText.getText().toString().trim().replace("市","");
                        if (!city.equals("")){
                            ConfigurationHelper helper = new ConfigurationHelper
                                    (WeatherActivity.this,Info.getWeatherConfiKey());
                            helper.setString(Info.getUserCityKey(),city);
                            updateWeather();
                        }else {
                            Toast.makeText(WeatherActivity.this,"输入不能为空！",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .show();

    }
    private boolean BackJsonIsOK(){
        if (weather5Bean.getStatus().equals(Info.JosnBackOk)){
            return true ;
        }else if (weather5Bean.getStatus().equals(Info.cityIsWrong)){
            textView.setText("未知或错误城市。请点击右上角检查城市是否设置错误。" +
                    "\n温馨提示：县、市等无需加上，如平南县，输入平南即可。");
            return  false;
        }
        textView.setText("获取数据发生未知错误...");
        return false;
    }
    private void updateWeather(){
        if (waitingDialog==null){
            craeteDialog();
        }
        waitingDialog.show();
        HttpWeather weather = new HttpWeather(Info.getUserCity(), URLData.getWeather());
        weather.getWeatherJson(Info.getWeatherAllKey());
    }
    private void setWeatherPic(){
        try {
            String picNum = "w"+weather5Bean.getNow().getCond().getCode();
            int resId = getResources().getIdentifier(picNum,"drawable","com.air2016jnu.airhelper");
            Drawable drawable = getResources().getDrawable(resId);
            weatherPic.setBackground(drawable);
        }catch (Exception ex){

        }
    }

}
