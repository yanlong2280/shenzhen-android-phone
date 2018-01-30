package com.example.administrator.HaweiMWCBluetoothLe;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by yanl on 2017/12/13.
 */

public class MySensorEventListener implements SensorEventListener {
    private Context context;
//    private double[] initValues;
    private int type;
    private boolean init;
    //保存上一次记录
    float lastX = 0;
    float lastY = 0;
    float lastZ = 0;

    //摇晃速度临界值
    private static final int SPEED_SHRESHOLD = 50;
    //两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 800;
    //上次检测时间
    private long lastUpdateTime;
    float tMax=1.0f;
    public MySensorEventListener(Context ct){
        this.context = ct;
        init = true;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
//        int sensorType = event.sensor.getType();
//        float[] values = event.values;
//        if(sensorType == Sensor.TYPE_ACCELEROMETER){
//            if(initValues == null) {
//                initValues = new double[3];
//                initValues[0] =Math.ceil(values[0]);
//                initValues[1] =Math.ceil(values[1]);
//                initValues[2] =Math.ceil(values[2]);
//                type = 1;
//            }
//            if ((Math.ceil(values[0]) > (initValues[0] + 1)) ||
//                            (Math.ceil(values[1]) > (initValues[1] + 1)) ||
//                            (Math.ceil(values[2]) > (initValues[2] + 1)))
//            {
//                if(type == 1) {
//                    type = 2;
//                    Intent intent = new Intent();
//                    intent.setAction(BluetoothLeAppService.SENSOR_MOVE);
//                    context.sendBroadcast(intent,null);
//                }
//            }else if((Math.ceil(values[0]) == initValues[0] ) ||
//                    (Math.ceil(values[1]) == initValues[1] ) ||
//                    (Math.ceil(values[2]) == initValues[2] )){
//                if(type == 2) {
//                    type = 1;
//                Intent intent = new Intent();
//                intent.setAction(BluetoothLeAppService.SENSOR_MOVE);
//                context.sendBroadcast(intent,null);
//                }
//            }else {
//                Intent intent = new Intent();
//                intent.setAction(BluetoothLeAppService.SENSOR_STOP);
//                context.sendBroadcast(intent,null);
//            }
//        }



        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        //现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        //两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;
        //判断是否达到了检测时间间隔
        if(timeInterval < UPTATE_INTERVAL_TIME)
            return;
        //现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;
        //获取加速度数值，以下三个值为重力分量在设备坐标的分量大小
        float x = event.values[SensorManager.DATA_X];

        float y = event.values[SensorManager.DATA_Y];

        float z = event.values[SensorManager.DATA_Z];

        if(init){
            init= false;
            lastX = x;
            lastY = y;
            lastZ = z;
            type=1;
        }

//        Log.e("msg", "x= "+ getVal(x)+",y= "+getVal(y)+",z= "+getVal(z));
//        Log.e("msg", "x= "+ getVal(lastX) + "(-1,"+ (getVal(lastX).doubleValue() - 1)+"|"+( getVal(lastX).doubleValue() +1)+")"  +",y= "+getVal(lastY)+",z= "+getVal(lastZ));
        if( (getVal(x).doubleValue() > getVal(lastX).doubleValue() + 0.2 || getVal(x).doubleValue() > getVal(lastX).doubleValue() - 0.2) &&
                (getVal(y).doubleValue() > getVal(lastY).doubleValue() + 0.2 || getVal(y).doubleValue() > getVal(lastY).doubleValue() - 0.2) &&
                (getVal(z).doubleValue() > getVal(lastZ).doubleValue() + 0.2 || getVal(z).doubleValue() > getVal(lastZ).doubleValue() - 0.2) ){
            if (type == 2) {
                type = 1;
//                Log.e("传感器", "静止");
                Intent intent = new Intent();
                intent.setAction(BluetoothLeAppService.SENSOR_STOP);
                context.sendBroadcast(intent,null);
            }
        }
        else {
            if (type == 1) {
                type = 2;
//                Log.e("传感器", "移动");
                Intent intent = new Intent();
                intent.setAction(BluetoothLeAppService.SENSOR_MOVE);
                context.sendBroadcast(intent,null);
            }
        }

        //   Log.e("msg", "x= "+x+" y= "+y);
        //    Log.e("msg", "x= "+x+" y= "+y+" z= "+z);

//        float absx = Math.abs(x);
//        float absy = Math.abs(y);
//        float absz = Math.abs(z);

//        if (absx > absy && absx > absz) {
//
//            if (x > tMax) {
//
//                Log.e("origen", "turn left");
//            } else if(x<-tMax){
//
//                Log.e("origen", "turn right");
//            }
//
//        }
//        else
//        if (absy > absx && absy > absz) {
//
//            if (y > tMax) {
//
//                Log.e("origen", "turn up");
//            } else if(y<-tMax){
//
//                Log.e("origen", "turn down");
//            }
//        }
//
//        else if (absz > absx && absz > absy) {
//            if (z > 0) {
//                Log.e("origen", "screen up");
//            } else {
//                Log.e("origen", "screen down");
//            }
//        }
//        else {
//
//            Log.e("origen", "unknow action");
//        }

        //获得x,y,z的变化值
//        float deltaX = x - lastX;
//        float deltaY = y - lastY;
//        float deltaZ = z - lastZ;
        //备份本次坐标
//        lastX = x;
//        lastY = y;
//        lastZ = z;
        //计算移动速度
//        double speed = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ)/timeInterval * 10000;

//
//        Log.e("msg", "x= "+ getVal(x)+",y= "+getVal(y)+",z= "+getVal(z));
//        Log.e("msg", "x= "+ getVal(lastX)+",y= "+getVal(lastY)+",z= "+getVal(lastZ));
//        if(speed >= SPEED_SHRESHOLD) {
//            Log.e("传感器", "移动");
//            Log.e("msg", "x= "+ getVal(x)+",y= "+getVal(y)+",z= "+getVal(z));
//            if (type == 1) {
//                type = 2;
//                Log.e("传感器", "移动");
//                Intent intent = new Intent();
//                intent.setAction(BluetoothLeAppService.SENSOR_MOVE);
//                context.sendBroadcast(intent,null);
//            }
//        }
//        else {
//            if (type == 2) {
//                type = 1;
//                Log.e("传感器", "静止");
//                Intent intent = new Intent();
//                intent.setAction(BluetoothLeAppService.SENSOR_STOP);
//                context.sendBroadcast(intent,null);
//            }
//        }

    }
    private BigDecimal getVal(float val){
        BigDecimal bd = new BigDecimal(val);
        bd = bd.setScale(1,BigDecimal.ROUND_HALF_UP);
        return  bd;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
