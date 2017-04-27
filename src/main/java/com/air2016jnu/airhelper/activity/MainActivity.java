package com.air2016jnu.airhelper.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.data.ConfigurationHelper;
import com.air2016jnu.airhelper.data.Info;
import com.air2016jnu.airhelper.data.URLData;
import com.air2016jnu.airhelper.entity.AllEntity;
import com.air2016jnu.airhelper.service.BluetoothLeService;
import com.air2016jnu.airhelper.service.ExcuteBluetoothService;
import com.air2016jnu.airhelper.utils.HttpWeather;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Random random = new Random();
    TextView tView ;
    private String bleName="" ;
    private String bleAddress="" ;
    private String targetBleName = "BT05";
    private String showString = "";
    int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler ;
    // 蓝牙扫描时间
    private static final long SCAN_PERIOD = 10000;
    private int tipsControl = 0;

    //显示数据字段
    String updateTime="";
    String humi="";
    String dust="";
    String ch2o="";
    String temp="";
    boolean dataFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAppData();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        setIconEnable(menu,true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_bluetooth) {
            startActivity(new Intent(this,BluetoothActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.air_condition) {
         startActivity(new Intent(MainActivity.this,AirQualityActivity.class));
        } else if (id == R.id.tem_condition) {
            startActivity(new Intent(MainActivity.this,TemperatureActivity.class));
        } else if (id == R.id.humidity_condition) {
            startActivity(new Intent(MainActivity.this,HumiActivity.class));

        } else if (id == R.id.dust_condition) {
           startActivity(new Intent(MainActivity.this,DustActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_help) {

        } else if (id==R.id.weather_condition){
            startActivity(new Intent(MainActivity.this,WeatherActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("空气检测仪");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ExcuteBluetoothService.isConnected){
                    sendBroadcastForData(Info.commandAllnow);
                    tView.setText("\n\n数据更新中...");
                }else {
                    showTips("蓝牙尚未连接或连接已断开！",Toast.LENGTH_SHORT);
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tView=(TextView)findViewById(R.id.temperature);
        tView.setText((new ConfigurationHelper(this,Info.bleBackDataKey).getString(Info.bleNowKey,"")));
        setNavBarDate();
    }
    private void initAppData(){
        HttpWeather weather = new HttpWeather(Info.getUserCity(), URLData.getWeather());
        weather.getWeatherJson(Info.getWeatherAllKey());
        mHandler = new Handler();
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
        if (initBle()){
            scanLeDevice();
        }

    }


    private void setNavBarDate(){
        Calendar calendar =Calendar.getInstance();
        int date =calendar.get(Calendar.DATE);
        int month= calendar.get(Calendar.MONTH)+1;
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        Log.d(".MainActivity",String.valueOf(date) );
        TextView tv1 = (TextView)findViewById(R.id.nav_Week);
        TextView tv2 = (TextView)findViewById(R.id.nav_date);
        String weekStr ="";
        switch (week){
            case 1:
                weekStr="星期日";
                break;
            case 2:
                weekStr="星期一";
                break;
            case 3:
                weekStr="星期二";
                break;
            case 4:
                weekStr="星期三";
                break;
            case 5:
                weekStr="星期四";
                break;
            case 6:
                weekStr="星期五";
                break;
            default:
                weekStr="星期六";
                break;
        }
        tv1.setText(weekStr);
        tv2.setText(""+year+"-"+month+"-"+date);


    }
    /*
    private void setTemperture(){
        int temperture =random.nextInt(15)+20;
        int humitity = random.nextInt(10)+50;
        int qualityChoose = random.nextInt(5);
        String quality ="";
        switch (qualityChoose){
            case 1:
                quality="极好";
                break;
            case 2:
                quality="较好";
                break;
            case 3:
                quality="一般";
                break;
            case 4:
                quality="较差";
                break;
            case 5:
                quality="极差";
            default:
                quality="较好";
        }
        String res = "温度："+temperture+"℃\n"+"湿度："+humitity+"%\n"+"空气质量："
                +quality;
        tView.setText(res);

    } */

    private boolean initBle(){
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
         return false ;
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
        return true ;
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
            System.out.println("Address:" + device.getAddress());
            System.out.println("Name:" + device.getName());
            System.out.println("rssi:" + rssi);
            if (device==null){
                return;
            }
            if (device.getName()==null){
                return;
            }
            if (device.getName().equals(targetBleName)){
                bleName = device.getName();
                bleAddress = device.getAddress();
                Intent intent = new Intent(MainActivity.this, ExcuteBluetoothService.class);
                intent.putExtra("address",bleAddress);
                System.out.println("发现目标设备，尝试进行自动连接");

                if (tipsControl==0){
                    showTips("发现目标设备，尝试进行自动连接",Toast.LENGTH_SHORT);
                    startService(intent);
                }
                tipsControl++;

            }

        }
    };

    private void scanLeDevice() {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.i("SCAN", "stop.....................");
                    Log.i("stopExcute", "stop.........stop............");
                    if (bleName.isEmpty()|bleName.equals("")){
                        //showTips("没有扫描到设备，请点击右上角进入手动扫描页面",Toast.LENGTH_LONG);
                    }
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
			/* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
            Log.i("SCAN", "begin.....................");
            Log.i("StartExcute", "start.........start............");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    private void showTips(String tips,int time){
        Toast.makeText(MainActivity.this,tips,time).show();
    }
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
                tView.setText("\n\n数据更新中...");
               mHandler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       sendBroadcastForData(Info.commandAllnow);

                   }
               },3000);
                //更新连接状态
                showTips("连接成功",Toast.LENGTH_SHORT);
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
                String data =intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
                if (data.startsWith("*")){
                    dataFlag=true;
                    showString="";
                    System.out.printf("data coming ----------------"+ data);
                    showString=showString+data.replace("*","");

                }else if (data.endsWith("!")){
                    showString=showString+data.replace("!","");
                    dataFlag=false;
                    showData(showString);



                }else if (dataFlag){
                    showString=showString+data;
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
    private  void sendBroadcastForData(String command){
        Intent intent = new Intent(BluetoothLeService.SEND_DATA);
        intent.putExtra("sendToBle",command);
        sendBroadcast(intent);
    }
    private void setIconEnable(Menu menu, boolean enable)
    {
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);

            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private AllEntity getAllEntity(String json){
        AllEntity allEntity ;
        Gson gson = new Gson();
        allEntity = gson.fromJson(json,AllEntity.class);
        return  allEntity;
    }
    private void showData(String json){
        AllEntity allEntity = getAllEntity(json);
        updateTime = allEntity.getUpdateTime();
        temp =allEntity.getAll().getTemp();
        dust=allEntity.getAll().getDust();
        ch2o=allEntity.getAll().getCH2O();
        humi=allEntity.getAll().getHumi();
        String show="\n温  度: "+temp+"\n粉尘浓度: "+dust+"\n甲醛浓度: "+ch2o+"\n湿  度: "+humi+"\n更新于 : "+updateTime;
        (new ConfigurationHelper(this,Info.bleBackDataKey)).setString(Info.bleNowKey,show);
        tView.setText(show);

    }
}
