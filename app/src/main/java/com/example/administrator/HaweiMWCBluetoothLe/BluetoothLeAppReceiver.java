package com.example.administrator.HaweiMWCBluetoothLe;

/**
 * Created by Administrator on 2016/12/21.
 */

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class BluetoothLeAppReceiver extends BroadcastReceiver {
    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT.equals(intent.getAction())){
//            // 启动完成
//            Intent localIntent = new Intent(context, Alarmreceiver.class);
//            localIntent.setAction("com.example.administrator.bluetooth.alarm.action");
//            PendingIntent sender = PendingIntent.getBroadcast(context, 0,
//                    localIntent, 0);
//            long firstime = SystemClock.elapsedRealtime();
//            AlarmManager am = (AlarmManager) context
//                    .getSystemService(Context.ALARM_SERVICE);
//            // 开机发送
//            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime  , sender);
            //开启APP
//            Intent mainActivityIntent = new Intent(context, ControlActivity.class);  // 要启动的Activity
//            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mainActivityIntent);

//            //屏幕唤醒
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
//                    | PowerManager.SCREEN_DIM_WAKE_LOCK, "mbright");//最后的参数是LogCat里用的Tag
//            wl.acquire();
//            wl.release();
//
//            //屏幕解锁
//            KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("munLock");//参数是LogCat里用的Tag
//            kl.disableKeyguard();
//
//            Intent backHome = new Intent(Intent.ACTION_MAIN);
//            backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            backHome.addCategory(Intent.CATEGORY_HOME);
//            context.startActivity(backHome);

//            kl.reenableKeyguard();

//            Intent it = new Intent();
//            it.setComponent(new ComponentName("com.example.administrator.wiko",
//                    "com.example.administrator.wiko.BluetoothLeNotifyServuce"));
//            intent.putExtra("title", "BluetoothLe");
//            intent.putExtra("msg", "BluetoothLe");
//            intent.putExtra("type", "create");
//            intent.putExtra("minutes",30 * 1000);
//            context.startService(intent);

//            Intent mainActivityIntent = new Intent(context, MyActivity.class);  // 要启动的Activity
//            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mainActivityIntent);



            //开机启动服务
            Intent it = new Intent();
            it.setComponent(new ComponentName("com.example.administrator.HaweiMWCBluetoothLe",
                    "com.example.administrator.HaweiMWCBluetoothLe.BluetoothLeAppService"));
            context.startService(it);
        }
//        else if(ACTION_DESTROY.equals(intent.getAction())){
//        	Intent service = new Intent(context, BluetoothLeAppService.class);
//            context.startService(service);
//        }
    }
}
