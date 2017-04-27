package com.air2016jnu.airhelper.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.air2016jnu.airhelper.service.BluetoothLeService;

import java.util.List;

/**
 * Created by Administrator on 2017/4/26.
 */

public class ConmonUtils {
    //unit:one hundred millisecond
    public static void sleep(int duration){
        try {
            Thread.sleep(duration*100);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static  <T> void reverseList(List<T> list){
        int size = list.size();
        int i =0,j=size-1;
        while (i<j){
          T temp = list.get(i);
          list.set(i,list.get(j)) ;
          list.set(j,temp);
          i++;
          j--;
        }
    }
    public static void dealXYData(List<Float> xData,List<Float> yData,List<String> xTas){
        float[] tempData = new float[xData.size()];
        int flag = Integer.MAX_VALUE;
        for (int i = 0;i<xData.size()-1;i++){
            if (xData.get(i)>xData.get(i+1)){
                flag=i+1;
                tempData[i]=xData.get(i);
                for (int j=flag;j<xData.size();j++){
                    tempData[j] = xData.get(j)+24;
                }
                break;
            }
            tempData[i] = xData.get(i);
        }
        if (flag!=Integer.MAX_VALUE){
            xData.clear();
            for (int i = 0;i<tempData.length;i++){
                xData.add(tempData[i]);
            }
        }

        if (flag!=Integer.MAX_VALUE){
            for (int i=0;i<flag;i++){
                xTas.add((int)((float)xData.get(i))+"时");
            }
            for (int i =flag;i<xData.size();i++){
                xTas.add((int)((float)xData.get(i))%24+"时");
            }
        }else {
            for (int i=0;i<xData.size();i++){
                xTas.add((int)((float)xData.get(i))+"时");
            }
        }
    }
    //this method needs to be tested
    public static boolean isDataOk(List<Float> x,List<Float> y){
        if (x.size()!=y.size()&&x.size()!=13){
            return false;
        }
        int numBreak = Integer.MAX_VALUE;
        for (int i=0;i<x.size()-1;i++){
            if (x.get(i+1)-x.get(i)!=1){
                if (x.get(i+1)==0){
                    numBreak = i+1;
                    break;
                }else {
                    return false;
                }
            }
        }
        if (numBreak!=Integer.MAX_VALUE){
            for (int i=numBreak;i<x.size()-1;i++){
                if ((x.get(i+1)-x.get(i))!=1){
                    return false;
                }
            }
        }
        return true ;
    }
    public static void ShowToast(Context context,String tips,int duration){
        Toast.makeText(context,tips,duration).show();
    }
    public static void sendBroadcastForData(Context context,String command){
        Intent intent = new Intent(BluetoothLeService.SEND_DATA);
        intent.putExtra("sendToBle",command);
        context.sendBroadcast(intent);
    }

}
