package com.air2016jnu.airhelper.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.air2016jnu.airhelper.utils.HttpWeather;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Random random = new Random();
    TextView tView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAppData();
        setDate();

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            Toast.makeText(this,"test2",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.humidity_condition) {


        } else if (id == R.id.dust_condition) {

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
                setTemperture();
                if(!checkBluetooth()){

                    startActivity(new Intent(MainActivity.this,BluetoothActivity.class));
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
    }
    private void initAppData(){
        HttpWeather weather = new HttpWeather(Info.getUserCity(), URLData.getWeather());
        weather.getWeatherJson(Info.getWeatherAllKey());
    }
    private void setDate(){
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

    }
    private boolean checkBluetooth(){
        return false;

    }
}
