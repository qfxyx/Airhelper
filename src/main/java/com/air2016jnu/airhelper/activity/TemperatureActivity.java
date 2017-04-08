package com.air2016jnu.airhelper.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.views.MyLineChart;
import com.github.mikephil.charting.charts.LineChart;

public class TemperatureActivity extends BaseActivity {
    private float[] xData = {1.0f,2.0f,3.0f,4.0f,5.0f,6.0f,7.0f,8.0f,9.0f,10.0f} ;
    private float[] yData ={21.0f,22.0f,23.0f,44.0f,35.0f,26.0f,77.0f,8.0f,19.0f,10.0f};
    private String[] xTas ;
    MyLineChart myLineChart ;
    @Override
   public void onBaseCreate(Bundle bundle){
        setContentViewBody(R.layout.activity_temperature);
        initData();
        initView();

    }
    private void initData(){
        xTas= new String[]{"x", "v","l","r","e","w","q","p","t","a","c"};


    }
    private void initView(){
        myLineChart = (MyLineChart)findViewById(R.id.tem_lineChart);
        myLineChart.setData(xData,yData,"temperature");
        myLineChart.setYDescription("变化趋势");
        myLineChart.setxTags(xTas);
        myLineChart.showChart();





    }

}
