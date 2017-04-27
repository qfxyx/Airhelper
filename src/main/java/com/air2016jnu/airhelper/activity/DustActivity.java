package com.air2016jnu.airhelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.data.ConfigurationHelper;
import com.air2016jnu.airhelper.data.Info;
import com.air2016jnu.airhelper.entity.DustEntity;
import com.air2016jnu.airhelper.entity.TempEntity;
import com.air2016jnu.airhelper.service.BluetoothLeService;
import com.air2016jnu.airhelper.service.ExcuteBluetoothService;
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
    @Override
    public void onBaseCreate(Bundle bundle){
        setContentViewBody(R.layout.activity_dust);
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
        initData();
        initView();

    }
    private void initData(){
        helper = new ConfigurationHelper(DustActivity.this, Info.bleBackDataKey);
        xData  = helper.getFloatList(Info.bleTempKeyX,13);
        yData  = helper.getFloatList(Info.bleTempKeyY,13);
        if(isDataOk(xData,yData)){
            dealXYData(xData,yData,xTas);
        }
        /*
        int iTemp = (new Random()).nextInt(23);
        for (int i = iTemp;i<iTemp+13;i++){
            xData.add((float)i%24);
            yData.add((float)(new Random()).nextInt(100));
        }
        dealXYData(xData,yData,xTas);
        for (String s:xTas){
            //System.out.println();
            System.out.println(s);
        }
        */


    }
    private void initView(){
        myLineChart = (MyLineChart)findViewById(R.id.dust_lineChart);
       if (isDataOk(xData,yData)){
           myLineChart.setData(xData,yData,"dust");
           myLineChart.setxTags(xTas,xData);
       }
        myLineChart.setYDescription("变化趋势");
        myLineChart.showChart();
        Button updateBtn = (Button)findViewById(R.id.dust_update_button);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dust="";
                if (ExcuteBluetoothService.isConnected){
                    sendBroadcastForData(Info.commandDust);
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
                    try {
                        parseData(dust);
                        dealXYData(xData,yData,xTas);
                        myLineChart.setData(xData,yData,updateTime);
                        myLineChart.updateChart();
                        helper.setFloatList(Info.bleTempKeyX,xData,13);
                        helper.setFloatList(Info.bleTempKeyY,yData,13);
                    }catch (Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(DustActivity.this,"检测仪返回数据有误",Toast.LENGTH_SHORT);

                    }


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

    private void dealXYData(List<Float> xData,List<Float> yData,List<String> xTas){
        float[] tempData = new float[xData.size()];
        int flag = Integer.MAX_VALUE;
        for (int i = 0;i<xData.size()-1;i++){
            if (xData.get(i)>xData.get(i+1)){
                flag=i+1;
                tempData[i]=xData.get(i);
                for (int j=flag;j<xData.size();j++){
                    tempData[j] = xData.get(j)+24;
                }
                break;
            }
            tempData[i] = xData.get(i);
        }
        if (flag!=Integer.MAX_VALUE){
            xData.clear();
            for (int i = 0;i<tempData.length;i++){
                xData.add(tempData[i]);
            }
        }

        if (flag!=Integer.MAX_VALUE){
            for (int i=0;i<flag;i++){
                xTas.add((int)((float)xData.get(i))+"时");
            }
            for (int i =flag;i<xData.size();i++){
                xTas.add((int)((float)xData.get(i))%24+"时");
            }
        }else {
            for (int i=0;i<xData.size();i++){
                xTas.add((int)((float)xData.get(i))+"时");
            }
        }
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


    //this method needs to be tested
    private boolean isDataOk(List<Float> x,List<Float> y){
        if (x.size()!=y.size()&&x.size()!=13){
            return false;
        }
        int numBreak = Integer.MAX_VALUE;
        for (int i=0;i<x.size()-1;i++){
            if (x.get(i+1)-x.get(i)!=1){
                if (x.get(i+1)==0){
                    numBreak = i+1;
                    break;
                }else {
                    return false;
                }
            }
        }
        if (numBreak!=Integer.MAX_VALUE){
            for (int i=numBreak;i<x.size()-1;i++){
                if ((x.get(i+1)-x.get(i))!=1){
                    return false;
                }
            }
        }

        return true ;
    }

    private  void sendBroadcastForData(String command){
        Intent intent = new Intent(BluetoothLeService.SEND_DATA);
        intent.putExtra("sendToBle",command);
        sendBroadcast(intent);
    }

}
