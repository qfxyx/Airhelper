package com.air2016jnu.airhelper.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.air2016jnu.airhelper.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/4.
 */
public class MyLineChart extends LinearLayout {

    LineChart mChart;
    LineDataSet set1;
    public MyLineChart(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        LayoutInflater.from(context).inflate(R.layout.line_chart,this);
        mChart = (LineChart)findViewById(R.id.line_chart);

    }
    public void setData(float[] xData,float[] yData,String chartDes) {

        ArrayList<Entry> values = new ArrayList<Entry>();
        if (xData.length!=yData.length){
            return;
        }
        for (int i = 0; i < xData.length; i++) {
            values.add(new Entry( xData[i],yData[i]));
        }



        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, chartDes);
            set1.setDrawIcons(false);
            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);


            if (Utils.getSDKInt() >= 18) {
                set1.setFillColor(Color.GREEN);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }

    public void setxTags(final String[] tags){
        if (tags==null){
            return;
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return tags[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            //@Override
            // public int getDecimalDigits() {  return 0; }
        };
        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
    }
    // 设置显示的样式
    public void showChart() {
        mChart.setDrawBorders(false);  //是否在折线图上添加边框
        // enable / disable grid background
        mChart.setDrawGridBackground(false); // 是否显示表格颜色
        mChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

        // enable touch gestures
        mChart.setTouchEnabled(true); // 设置是否可以触摸

        // enable scaling and dragging
        mChart.setDragEnabled(true);// 是否可以拖拽
        mChart.setScaleEnabled(true);// 是否可以缩放

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);//

        mChart.setBackgroundColor(Color.WHITE);// 设置背景

        // get the legend (only possible after setting data)
        Legend mLegend = mChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        // modify the legend ...
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(8f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
//      mLegend.setTypeface(mTf);// 字体
        mChart.animateX(2500); // 立即执行的动画,x轴
    }
    public void setYDescription(String des){
        // no description text
        Description description = new Description();
        description.setText(des);
        mChart.setDescription(description);// 数据描述

    }
    public void setLineDatasetColor(int color){
        if (set1!=null){
            set1.setColor(color);
        }
    }
    public void setLineDatasetCicleColor(int color){
        if (set1!=null){
            set1.setCircleColor(color);
        }
    }
    public void setChartBackgroud(int color){
        mChart.setBackgroundColor(color);// 设置背景
    }
    public void setDrawBorders(boolean b){
        mChart.setDrawBorders(b);
    }
    public void setLegend(Legend.LegendForm legendForm,int color){
        Legend mLegend = mChart.getLegend(); // 设置比例图标示
        // modify the legend ...
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        mLegend.setForm(legendForm);// 样式
        mLegend.setFormSize(8f);// 字体
        mLegend.setTextColor(color);// 颜色
//      mLegend.setTypeface(mTf);// 字体
    }

    public void setDatasetFillColor(int color){
        if (set1!=null){
            set1.setFillColor(color);
        }

    }
    public void setDatasetLabel(String label){

        if (set1!=null){
            set1.setLabel(label);
        }
    }
    public void setDatasetTextColor(int color){
        if (set1!=null){
            set1.setValueTextColor(color);

        }
    }
    public  void setChartBackgroud(Drawable drawable){
        mChart.setBackground(drawable);
    }
    public void  setChartBackgroudColor(int color){
        mChart.setBackgroundColor(color);
    }

    public void setChartDescription(Description description){
        mChart.setDescription(description);
    }

}
