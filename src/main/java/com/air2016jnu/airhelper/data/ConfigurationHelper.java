package com.air2016jnu.airhelper.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/12/7.
 */
public class ConfigurationHelper {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public ConfigurationHelper(Context context,String name){
        preferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        editor=preferences.edit();
    }
    public String getString(String key,String defValue){
        return preferences.getString(key,defValue);
    }
    public void setString(String key,String value){
        editor.putString(key,value);
        editor.commit();
    }
    public int getInt(String key,int defValue){
        return preferences.getInt(key,defValue);
    }
    public void setInt(String key,int value){
        editor.putInt(key,value);
        editor.commit();
    }
    public Boolean getBoolean(String key,Boolean defValue){
        return preferences.getBoolean(key,defValue);
    }
    public void setBoolean(String key,Boolean value){
        editor.putBoolean(key,value);
        editor.commit();
    }
}
