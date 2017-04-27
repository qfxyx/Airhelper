package com.air2016jnu.airhelper.service;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcuteBluetoothService extends Service {
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";;
    public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static String EXTRAS_DEVICE_RSSI = "RSSI";

    //蓝牙连接状态
    public static boolean isConnected = false;
    private String status = "disconnected";
    //蓝牙名字
    private String mDeviceName;
    //蓝牙地址
    private String mDeviceAddress;
    //蓝牙信号值
    private String mRssi;
    private Bundle b;
    private String rev_str = "";
    //蓝牙service,负责后台的蓝牙服务
    private static BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    //蓝牙特征值
    private static BluetoothGattCharacteristic target_chara = null;
    private Handler mhandler = new Handler();
    public ExcuteBluetoothService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        int retVal = super.onStartCommand(intent, flags, startId);
        b = intent.getExtras();
        //从意图获取显示的蓝牙信息
        //mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra("address");
        //mRssi = b.getString(EXTRAS_DEVICE_RSSI);
        System.out.println("启动中间服务成功！尝试启动蓝牙服务，蓝牙地址为："+mDeviceAddress);
		/* 启动蓝牙service */
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        gattServiceIntent.putExtra("address",mDeviceAddress);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        startService(gattServiceIntent);
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
        return retVal;

    }
    /* BluetoothLeService绑定的回调函数 */
    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service)
        {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize())
            {
               // Log.e(TAG, "Unable to initialize Bluetooth");
                System.out.println("中间服务:蓝牙服务初始化未准备好！");

            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            // 根据蓝牙地址，连接设备
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mBluetoothLeService = null;
        }

    };

    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
            {
                isConnected = true;
                status = "connected";
                //更新连接状态
                //updateConnectionState(status);
                System.out.println("BroadcastReceiver :" + "device connected");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
                    .equals(action))
            {
                isConnected = false;
                status = "disconnected";
                //更新连接状态
                // updateConnectionState(status);
                System.out.println("BroadcastReceiver :"
                        + "device disconnected");

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//发现GATT服务器
                    .equals(action))
            {
                // Show all the supported services and characteristics on the
                // user interface.
                //获取设备的所有蓝牙服务
                displayGattServices(mBluetoothLeService
                        .getSupportedGattServices());
                System.out.println("BroadcastReceiver :"
                        + "device SERVICES_DISCOVERED");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//有效数据
            {
                //处理发送过来的数据
                //displayData(intent.getExtras().getString(BluetoothLeService.EXTRA_DATA));
                System.out.println("BroadcastReceiver onData:"
                        + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }else if (BluetoothLeService.SEND_DATA.equals(action)){
                sendMessage(intent.getExtras().getString("sendToBle"));
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
    private void displayGattServices(List<BluetoothGattService> gattServices)
    {

        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = "unknown_service";
        String unknownCharaString = "unknown_characteristic";

        // 服务数据,可扩展下拉列表的第一级数据
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        // 特征数据（隶属于某一级服务下面的特征值集合）
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

        // 部分层次，所有特征值集合
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices)
        {

            // 获取服务列表
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            // 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。

            gattServiceData.add(currentServiceData);

            System.out.println("Service uuid:" + uuid);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

            // 从当前循环所指向的服务中读取特征值列表
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            // 对于当前循环所指向的服务中的每一个特征值
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();

                if (gattCharacteristic.getUuid().toString()
                        .equals(HEART_RATE_MEASUREMENT))
                {
                    // 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                    mhandler.postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            // TODO Auto-generated method stub
                            mBluetoothLeService
                                    .readCharacteristic(gattCharacteristic);
                        }
                    }, 200);

                    // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBluetoothLeService.setCharacteristicNotification(
                            gattCharacteristic, true);
                    target_chara = gattCharacteristic;
                    // 设置数据内容
                    // 往蓝牙模块写入数据
                    // mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                }
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic
                        .getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors)
                {
                    System.out.println("---descriptor UUID:"
                            + descriptor.getUuid());
                    // 获取特征值的描述
                    mBluetoothLeService.getCharacteristicDescriptor(descriptor);
                    // mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
                    // true);
                }

                gattCharacteristicGroupData.add(currentCharaData);
            }
            // 按先后顺序，分层次放入特征值集合中，只有特征值
            mGattCharacteristics.add(charas);
            // 构件第二级扩展列表（服务下面的特征值）
            gattCharacteristicData.add(gattCharacteristicGroupData);

        }

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //解除广播接收器
        unregisterReceiver(mGattUpdateReceiver);
        mBluetoothLeService = null;
    }

    private  void sendMessage(String test){
        if (target_chara!=null){
            target_chara.setValue(test);
            mBluetoothLeService.writeCharacteristic(target_chara);
        }else {
            System.out.print("target_chara为空！！！！");
        }
    }


}
