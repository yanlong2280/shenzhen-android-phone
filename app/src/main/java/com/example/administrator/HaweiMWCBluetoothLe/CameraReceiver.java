package com.example.administrator.HaweiMWCBluetoothLe;

/**
 * Created by Administrator on 2016/12/21.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CameraReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG", "intent======>>>>>>"+intent.getAction());
    }
}
