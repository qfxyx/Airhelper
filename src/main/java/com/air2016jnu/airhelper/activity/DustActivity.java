package com.air2016jnu.airhelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.data.ConfigurationHelper;
import com.air2016jnu.airhelper.data.Info;
import com.air2016jnu.airhelper.entity.DustEntity;
import com.air2016jnu.airhelper.service.BluetoothLeService;
import com.air2016jnu.airhelper.service.ExcuteBluetoothService;
import com.air2016jnu.airhelper.utils.BackButtonAction;
import com.air2016jnu.airhelper.utils.ConmonUtils;
import com.air2016jnu.airhelper.views.MyLineChart;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DustActivity extends BaseActivity {
    private List<Float> xData = new ArrayList<>();
    private List<Float> yData = new ArrayList<>();
    private List<String> xTas = new ArrayList<>();
    MyLineChart myLineChart ;
    String dust="";
    private String updateTime;
    private String dataType;
    private String nowDust;
    private ConfigurationHelper helper;
    private boolean initDataIsOK = false;
    TextView nowTextTv ;
    TextView updateTimeTv;
    TextView updateTipTv;
    ImageButton updateBtn;
    @Override
    public void onBaseCreate(Bundle bundle){
        setContentViewBody(R.layout.activity_dust);
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
        initData();
        initView();

    }
    private void initData(){
        helper = new ConfigurationHelper(DustActivity.this, Info.bleBackDataKey);
        xData  = helper.getFloatList(Info.bleDustKeyX,13);
        yData  = helper.getFloatList(Info.bleDustKeyY,13);
        if(ConmonUtils.isDataOk(xData,yData)){
            ConmonUtils.dealXYData(xData,yData,xTas);
            initDataIsOK=true;
        }
        setlBtnActionAction(new BackButtonAction(DustActivity.this));
        setActivityTitle("粉尘浓度");

    }
    private void initView(){
        nowTextTv =(TextView)findViewById(R.id.analysis_now);
        updateTimeTv=(TextView)findViewById(R.id.analysis_update_time);
        updateTipTv =(TextView)findViewById(R.id.analysis_tips);
        myLineChart = (MyLineChart)findViewById(R.id.dust_lineChart);
        if (initDataIsOK){
            myLineChart.setData(xData,yData,"过去十二小时粉尘浓度变化表");
            myLineChart.setxTags(xTas,xData);
            nowTextTv.setText(helper.getString(Info.bleDustNow,"暂无"));
            updateTimeTv.setText(helper.getString(Info.bleDustUpdate,"暂无"));
        }

        myLineChart.setYDescription("变化趋势");
        myLineChart.setDatasetFillColor(Color.rgb(21,212,202));
        myLineChart.showChart();
        updateBtn = (ImageButton)findViewById(R.id.analysis_sync_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dust="";
                if (ExcuteBluetoothService.isConnected){
                    ConmonUtils.sendBroadcastForData(DustActivity.this,Info.commandDust);
                    updateBtn.setVisibility(View.INVISIBLE);
                    updateTipTv.setText("更新中...");
                }else {
                    Toast.makeText(DustActivity.this,"蓝牙未连接或已断开",Toast.LENGTH_SHORT).show();
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
                    dust=dust+data.replace("*","");

                }else if (data.endsWith("#")){
                    dust=dust+data.replace("#","");
                    refleshChart(dust);

                }else {
                    dust=dust+data;
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
        DustEntity dustEntity;
        Gson gson = new Gson();
        xData.clear();
        yData.clear();
        xTas.clear();
        dustEntity = gson.fromJson(json,DustEntity.class);
        nowDust =dustEntity.getDustNow();
        updateTime =dustEntity.getUpdateTime();
        List<DustEntity.AllDustBean> allDust = dustEntity.getAllDust();
        for (DustEntity.AllDustBean bean:allDust){
            xData.add(Float.valueOf(bean.getTime()));
            yData.add(Float.valueOf(bean.getDust()));
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
                helper.setFloatList(Info.bleDustKeyX,xData,13);
                ConmonUtils.dealXYData(xData,yData,xTas);
                myLineChart.setData(xData,yData,updateTime);
                myLineChart.setxTags(xTas,xData);
                myLineChart.updateChart();
                updateTimeTv.setText(updateTime);
                nowTextTv.setText(nowDust);

                helper.setFloatList(Info.bleDustKeyY,yData,13);
                helper.setString(Info.bleDustUpdate,updateTime);
                helper.setString(Info.bleDustNow,nowDust);
            }else {
                ConmonUtils.ShowToast(DustActivity.this,"检测仪返回数据有误",Toast.LENGTH_SHORT);
            }


        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(DustActivity.this,"检测仪返回数据有误",Toast.LENGTH_SHORT);
            updateBtn.setVisibility(View.VISIBLE);
            updateTipTv.setText("");

        }

    }

}
