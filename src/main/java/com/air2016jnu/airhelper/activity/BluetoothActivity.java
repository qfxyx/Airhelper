package com.air2016jnu.airhelper.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.utils.BackButtonAction;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/5.
 */
public class BluetoothActivity extends BaseActivity {

    String activityTitle = "连接蓝牙";
    // 扫描蓝牙按钮
    private Button scan_btn;
    // 蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    // 蓝牙信号强度
    private ArrayList<Integer> rssis;
    // 自定义Adapter
    LeDeviceListAdapter mleDeviceListAdapter;
    // listview显示扫描到的蓝牙信息
    ListView lv;
    // 描述扫描蓝牙的状态
    private boolean mScanning;
    private boolean scan_flag=true;
    private Handler mHandler;
    int REQUEST_ENABLE_BT = 1;
    // 蓝牙扫描时间
    private static final long SCAN_PERIOD = 10000;

    @Override
    public void onBaseCreate(Bundle bundle) {
        initView();
        init_ble();
    }

    private void initView() {
        setContentViewBody(R.layout.bluetooth_layout);
        scan_btn = (Button) findViewById(R.id.bluetooth_btn);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEvent();
            }
        });
        setlBtnActionAction(new BackButtonAction(this));
        setActivityTitle(activityTitle);
        lv=(ListView)findViewById(R.id.bluetooth_lv);
        mleDeviceListAdapter =new LeDeviceListAdapter();
        lv.setAdapter(mleDeviceListAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id)
            {
                // TODO Auto-generated method stub
                final BluetoothDevice device = mleDeviceListAdapter
                        .getDevice(position);
                if (device == null)
                    return;
                final Intent intent = new Intent(BluetoothActivity.this,
                        BluetoothSendActivity.class);
                intent.putExtra(BluetoothSendActivity.EXTRAS_DEVICE_NAME,
                        device.getName());
                intent.putExtra(BluetoothSendActivity.EXTRAS_DEVICE_ADDRESS,
                        device.getAddress());
                intent.putExtra(BluetoothSendActivity.EXTRAS_DEVICE_RSSI,
                        rssis.get(position).toString());
                if (mScanning)
                {
					/* 停止扫描设备 */
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }

                try
                {
                    // 启动Ble_Activity
                    startActivity(intent);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    // TODO: handle exception
                }

            }
        });
        mHandler = new Handler();

    }
    private void init_ble()
    {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes Bluetooth adapter.
        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 打开蓝牙权限
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    private void btnEvent() {
        if (scan_flag)
        {
            mleDeviceListAdapter = new LeDeviceListAdapter();
            lv.setAdapter(mleDeviceListAdapter);
            scanLeDevice(true);
        } else
        {

            scanLeDevice(false);
            scan_btn.setText("扫描设备");
        }

    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;

        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            rssis = new ArrayList<Integer>();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device, int rssi) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                rssis.add(rssi);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            rssis.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * 重写getview
         **/
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            // General ListView optimization code.
            // 加载listview每一项的视图
            view = mInflator.inflate(R.layout.bluetooth_list_item, null);
            // 初始化三个textview显示蓝牙信息
            TextView deviceAddress = (TextView) view
                    .findViewById(R.id.tv_deviceAddr);
            TextView deviceName = (TextView) view
                    .findViewById(R.id.tv_deviceName);
            TextView rssi = (TextView) view.findViewById(R.id.tv_rssi);

            BluetoothDevice device = mLeDevices.get(i);
            deviceAddress.setText(device.getAddress());
            deviceName.setText(device.getName());
            rssi.setText("" + rssis.get(i));

            return view;
        }
    }
    /**
     * @Title: scanLeDevice
     * @Description: TODO(扫描蓝牙设备 )
     * @param enable
     *            (扫描使能，true:扫描开始,false:扫描停止)
     * @return void
     * @throws
     */
    private void scanLeDevice(final boolean enable)
    {
        if (enable)
        {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mScanning = false;
                    scan_flag = true;
                    scan_btn.setText("扫描设备");
                    Log.i("SCAN", "stop.....................");
                    Log.i("stopExcute", "stop.........stop............");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
			/* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
            Log.i("SCAN", "begin.....................");
            Log.i("StartExcute", "start.........start............");
            mScanning = true;
            scan_flag = false;
            scan_btn.setText("停止扫描");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else
        {
            Log.i("Stop", "stoping................");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            scan_flag = true;
        }

    }

    /**
     * 蓝牙扫描回调函数 实现扫描蓝牙设备，回调蓝牙BluetoothDevice，可以获取name MAC等信息
     *
     * **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord)
        {
            // TODO Auto-generated method stub

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
					/* 讲扫描到设备的信息输出到listview的适配器 */
                    mleDeviceListAdapter.addDevice(device, rssi);
                    mleDeviceListAdapter.notifyDataSetChanged();
                }
            });

            System.out.println("Address:" + device.getAddress());
            System.out.println("Name:" + device.getName());
            System.out.println("rssi:" + rssi);

        }
    };

}
