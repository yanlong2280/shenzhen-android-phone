package com.example.administrator.HaweiMWCBluetoothLe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/12/21.
 */

public class Alarmreceiver extends BroadcastReceiver {
    private final String ACTION_DESTROY = "com.example.administrator.HaweiMWCBluetoothLe.alarm.action";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction() == null || intent.getAction().equals(ACTION_DESTROY)) {
            Intent i = new Intent();
            i.setClass(context, BluetoothLeAppService.class);
            // 启动service
            // 多次调用startService并不会启动多个service 而是会多次调用onStart
            context.startService(i);
        }
    }
}