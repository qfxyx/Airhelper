package com.air2016jnu.airhelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.data.ConfigurationHelper;
import com.air2016jnu.airhelper.data.Info;
import com.air2016jnu.airhelper.entity.Ch2oEntity;
import com.air2016jnu.airhelper.entity.DustEntity;
import com.air2016jnu.airhelper.entity.TempEntity;
import com.air2016jnu.airhelper.service.BluetoothLeService;
import com.air2016jnu.airhelper.service.ExcuteBluetoothService;
import com.air2016jnu.airhelper.utils.BackButtonAction;
import com.air2016jnu.airhelper.utils.ConmonUtils;
import com.air2016jnu.airhelper.views.MyLineChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AirQualityActivity extends BaseActivity {

    private List<Float> xData = new ArrayList<>();
    private List<Float> yData = new ArrayList<>();
    private List<String> xTas = new ArrayList<>();
    MyLineChart myLineChart ;
    String ch2o="";
    private String updateTime;
    private String dataType;
    private String nowCh2o;
    private ConfigurationHelper helper;
    private boolean initDataIsOK = false;
    TextView nowTextTv ;
    TextView updateTimeTv;
    TextView updateTipTv;
    ImageButton updateBtn;

    @Override
    public void onBaseCreate(Bundle savedInstanceState) {
        setContentViewBody(R.layout.activity_air_quality);
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
        initData();
        initView();

    }
    private void initData(){
        helper = new ConfigurationHelper(AirQualityActivity.this, Info.bleBackDataKey);
        xData  = helper.getFloatList(Info.bleCh2oKeyX,13);
        yData  = helper.getFloatList(Info.bleCh2oKeyY,13);
        if(ConmonUtils.isDataOk(xData,yData)){
            ConmonUtils.dealXYData(xData,yData,xTas);
            initDataIsOK =true;
        }
        setlBtnActionAction(new BackButtonAction(AirQualityActivity.this));
        setActivityTitle("甲醛浓度");

    }
    private void initView(){
        nowTextTv =(TextView)findViewById(R.id.analysis_now);
        updateTimeTv=(TextView)findViewById(R.id.analysis_update_time);
        updateTipTv =(TextView)findViewById(R.id.analysis_tips);

        myLineChart = (MyLineChart)findViewById(R.id.air_lineChart);
        if (initDataIsOK){
            myLineChart.setxTags(xTas,xData);
            myLineChart.setData(xData,yData,"过去十二小时甲醛浓度变化表");
            nowTextTv.setText(helper.getString(Info.bleCh20Now,"暂无"));
            updateTimeTv.setText(helper.getString(Info.bleCh2oUpdate,"暂无"));
        }
        myLineChart.setYDescription("变化趋势");
        myLineChart.showChart();
        myLineChart.setChartBackgroud(Color.GRAY);
        myLineChart.setDatasetFillColor(Color.rgb(0,255,255));

        updateBtn = (ImageButton)findViewById(R.id.analysis_sync_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch2o="";
                if (ExcuteBluetoothService.isConnected){
                    ConmonUtils.sendBroadcastForData(AirQualityActivity.this,Info.commandCh20);
                    updateBtn.setVisibility(View.INVISIBLE);
                    updateTipTv.setText("更新中...");
                }else {
                    ConmonUtils.ShowToast(AirQualityActivity.this,"蓝牙未连接或已断开",Toast.LENGTH_SHORT);
                }
            }
        });


    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
            {

                //更新连接状态
                //updateConnectionState(status);
                System.out.println("BroadcastReceiver :" + "device connected");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
                    .equals(action))
            {

                //更新连接状态
                // updateConnectionState(status);
                System.out.println("BroadcastReceiver :"
                        + "device disconnected");

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//发现GATT服务器
                    .equals(action))
            {

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//有效数据
            {
                //处理发送过来的数据
                String data = intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
                if (data.startsWith("*")){
                    System.out.printf("data coming ----------------"+ data);
                    ch2o=ch2o+data.replace("*","");

                }else if (data.endsWith("#")){
                    ch2o=ch2o+data.replace("#","");
                    refleshChart(ch2o);

                }else {
                    ch2o=ch2o+data;
                }


            }
        }
    };
    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.SEND_DATA);
        return intentFilter;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private void parseData(String json){
        Ch2oEntity ch2oEntity;
        Gson gson = new Gson();
        xData.clear();
        yData.clear();
        xTas.clear();
        ch2oEntity = gson.fromJson(json,Ch2oEntity.class);
        nowCh2o =ch2oEntity.getCH2ONow();
        dataType =ch2oEntity.getType();
        updateTime =ch2oEntity.getUpdateTime();
        List<Ch2oEntity.AllCH2OBean> allTemp = ch2oEntity.getAllCH2O();
        for (Ch2oEntity.AllCH2OBean bean:allTemp){
            xData.add(Float.valueOf(bean.getTime()));
            yData.add(Float.valueOf(bean.getCH2O()));
        }
    }


    private void refleshChart(String json){
        try {
            parseData(json);
            ConmonUtils.reverseList(xData);
            ConmonUtils.reverseList(yData);
            updateBtn.setVisibility(View.VISIBLE);
            updateTipTv.setText("");
            if (ConmonUtils.isDataOk(xData,yData)){
                helper.setFloatList(Info.bleCh2oKeyX,xData,13);
                ConmonUtils.dealXYData(xData,yData,xTas);
                myLineChart.setData(xData,yData,updateTime);
                myLineChart.setxTags(xTas,xData);
                myLineChart.updateChart();
                updateTimeTv.setText(updateTime);
                nowTextTv.setText(nowCh2o);
                helper.setFloatList(Info.bleCh2oKeyY,yData,13);
                helper.setString(Info.bleCh2oUpdate,updateTime);
                helper.setString(Info.bleCh20Now,nowCh2o);
            }else {
                ConmonUtils.ShowToast(AirQualityActivity.this,"检测仪返回数据有误",Toast.LENGTH_SHORT);
            }


        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(AirQualityActivity.this,"检测仪返回数据有误",Toast.LENGTH_SHORT);
            updateBtn.setVisibility(View.VISIBLE);
            updateTipTv.setText("");

        }

    }

}
