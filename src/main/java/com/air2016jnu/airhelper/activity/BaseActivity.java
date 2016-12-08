package com.air2016jnu.airhelper.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.air2016jnu.airhelper.R;
import com.air2016jnu.airhelper.interfaces.ButtonAction;

public  abstract class BaseActivity extends AppCompatActivity {

    private LinearLayout bodyLayout;
    private View contentView;
    private Button lButton;
    private ButtonAction lBtnAction;
    private Button rButton;
    private ButtonAction rBtnAction;
    private TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initBaseView();
        onBaseCreate(savedInstanceState);



    }
    public abstract void onBaseCreate(Bundle bundle);

    private void initBaseView(){
         bodyLayout =(LinearLayout)findViewById(R.id.base_body);
         lButton = (Button)findViewById(R.id.title_bar_btn_l);
        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lBtnAction!=null){
                    lBtnAction.act();
                }
            }
        });
         rButton = (Button)findViewById(R.id.title_bar_btn_r);
        textTitle = (TextView)findViewById(R.id.title_bar_title);
    }
    public void setActivityTitle(String title){
        textTitle.setText(title);
    }
     public void setContentViewBody(int layoutId){
         contentView = getLayoutInflater().inflate(layoutId,null);
         if(bodyLayout.getChildCount()>10){
             bodyLayout.removeAllViews();
         }
         if (contentView!=null){
             ViewGroup.LayoutParams params = new ViewGroup.LayoutParams
                     (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
             bodyLayout.addView(contentView,params);
         }
     }
    public void setlBtnActionAction(ButtonAction action){
        lBtnAction=action;
    }


}
