package com.air2016jnu.airhelper.activity;

import android.os.Bundle;

import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.utils.BackButtonAction;

/**
 * Created by Administrator on 2017/5/2.
 */

public class HelpActivity extends BaseActivity {
    @Override
    public void onBaseCreate(Bundle bundle){
        initView();
    }
    private void initView(){
        setContentViewBody(R.layout.help_activity);
        setActivityTitle("帮助");
        setlBtnActionAction(new BackButtonAction(this));

    }
}
