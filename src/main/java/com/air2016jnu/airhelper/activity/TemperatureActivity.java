package com.air2016jnu.airhelper.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.data.ConfigurationHelper;
import com.air2016jnu.airhelper.data.Info;
import com.air2016jnu.airhelper.entity.TempEntity;
import com.air2016jnu.airhelper.service.BluetoothLeService;
import com.air2016jnu.airhelper.service.ExcuteBluetoothService;
import com.air2016jnu.airhelper.utils.BackButtonAction;
import com.air2016jnu.airhelper.utils.ConmonUtils;
import com.air2016jnu.airhelper.views.MyLineChart;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TemperatureActivity extends BaseActivity {
    private List<Float> xData = new ArrayList<>();
    private List<Float> yData = new ArrayList<>();
    private List<String> xTas = new ArrayList<>();
    MyLineChart myLineChart ;
    String temp="";
    private String updateTime;
    private String dataType;
    private String nowTemp;
    private  ConfigurationHelper helper;
    private boolean initDataIsOK = false;
    TextView nowTextTv ;
    TextView updateTimeTv;
    TextView updateTipTv;
    ImageButton updateBtn;
    @Override
   public void onBaseCreate(Bundle bundle){
        setContentViewBody(R.layout.activity_temperature);
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
        initData();
        initView();


    }
    private void initData(){
        helper = new ConfigurationHelper(TemperatureActivity.this, Info.bleBackDataKey);
        xData  = helper.getFloatList(Info.bleTempKeyX,13);
        yData  = helper.getFloatList(Info.bleTempKeyY,13);
        if(ConmonUtils.isDataOk(xData,yData)){
            ConmonUtils.dealXYData(xData,yData,xTas);
            initDataIsOK =true;
        }


    }
    private void initView(){
        nowTextTv =(TextView)findViewById(R.id.analysis_now);
        updateTimeTv=(TextView)findViewById(R.id.analysis_update_time);
        updateTipTv =(TextView)findViewById(R.id.analysis_tips);
        setlBtnActionAction(new BackButtonAction(TemperatureActivity.this));
        setActivityTitle("温度变化");
        myLineChart = (MyLineChart)findViewById(R.id.tem_lineChart);
        if (initDataIsOK){
            myLineChart.setData(xData,yData,"过去十二小时温度变化表");
            myLineChart.setxTags(xTas,xData);
            nowTextTv.setText(helper.getString(Info.bleTempNow,"暂无"));
            updateTimeTv.setText(helper.getString(Info.bleTempUpdate,"暂无"));
        }
        myLineChart.setYDescription("变化趋势");
        myLineChart.showChart();
        updateBtn = (ImageButton)findViewById(R.id.analysis_sync_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp="";
                if (ExcuteBluetoothService.isConnected){
                    ConmonUtils.sendBroadcastForData(TemperatureActivity.this,Info.commandTemp);
                    updateBtn.setVisibility(View.INVISIBLE);
                    updateTipTv.setText("更新中...");
                }else {
                    Toast.makeText(TemperatureActivity.this,"蓝牙未连接或已断开",Toast.LENGTH_SHORT).show();
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
                    temp=temp+data.replace("*","");

                }else if (data.endsWith("#")){
                    temp=temp+data.replace("#","");
                    refleshChart(temp);

                }else {
                    temp=temp+data;
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
        TempEntity tempEntity ;
        Gson gson = new Gson();
        xData.clear();
        yData.clear();
        xTas.clear();
        tempEntity = gson.fromJson(json,TempEntity.class);
        nowTemp =tempEntity.getTempNow();
        dataType =tempEntity.getType();
        updateTime =tempEntity.getUpdateTime();
        List<TempEntity.AllTempBean> allTemp = tempEntity.getAllTemp();
        for (TempEntity.AllTempBean bean:allTemp){
            xData.add(Float.valueOf(bean.getTime()));
            yData.add(Float.valueOf(bean.getTemp()));
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
                //先存x的数据，因为接下来还会对其修改
                helper.setFloatList(Info.bleTempKeyX,xData,13);
                ConmonUtils.dealXYData(xData,yData,xTas);
                myLineChart.setData(xData,yData,"过去十二小时温度变化表");
                myLineChart.updateChart();
                myLineChart.setxTags(xTas,xData);
                updateTimeTv.setText(updateTime);
                nowTextTv.setText(nowTemp+"°C");
                helper.setFloatList(Info.bleTempKeyY,yData,13);
                helper.setString(Info.bleTempUpdate,updateTime);
                helper.setString(Info.bleTempNow,nowTemp);
            }else {
                ConmonUtils.ShowToast(TemperatureActivity.this,"检测仪返回数据有误",Toast.LENGTH_SHORT);
            }


        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(TemperatureActivity.this,"检测仪返回数据有误",Toast.LENGTH_SHORT);
            updateBtn.setVisibility(View.VISIBLE);
            updateTipTv.setText("");

        }

    }

}
