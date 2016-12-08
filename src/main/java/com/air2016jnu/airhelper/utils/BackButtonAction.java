package com.air2016jnu.airhelper.utils;

import android.app.Activity;
import android.content.Context;

import com.air2016jnu.airhelper.data.MyApplication;
import com.air2016jnu.airhelper.interfaces.ButtonAction;

/**
 * Created by Administrator on 2016/12/9.
 */
public class BackButtonAction implements ButtonAction {
    Context myContext;
    public BackButtonAction(Context context){
        myContext=context;
    }
    @Override
    public void act() {
     try {
         ((Activity)myContext).finish();
     }catch (Exception ex){
     }
    }
}
