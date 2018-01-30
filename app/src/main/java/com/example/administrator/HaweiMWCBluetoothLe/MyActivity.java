package com.example.administrator.HaweiMWCBluetoothLe;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends AppCompatActivity {
    private static  final int MY_PERMISSIONS_REQUEST_ALL  = 100;
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;
    private static final int BLUETOOTH_DISCOVERABLE_DURATION = 120;
    private BluetoothAdapter adapter;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    List<String> mPermissionList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        if (Build.VERSION.SDK_INT >= 23 ) {
            checkBluetoothPermission();
        }else{
            connectBluetooth();
        }
    }

    /**
     *判断位置信息是否开启
     * @param context
     * @return
     */
    public static boolean isLocationOpen(final Context context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsProvider;
    }
    /*
    校验权限
    */
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有多权限
            mPermissionList.clear();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(MyActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
                connectBluetooth();
            } else {//请求权限方法
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(MyActivity.this, permissions, MY_PERMISSIONS_REQUEST_ALL);
            }
        } else {
            //系统不高于6.0直接执行
            connectBluetooth();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ALL) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MyActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        // 权限拒绝，提示用户开启权限
                        finish();
                        System.exit(0);
                    }
                }
            }
            connectBluetooth();
        }
    }
//    private void connectBluetooth() {
//        startService(new Intent(MyActivity.this, BluetoothLeAppService.class));
//    }
    private void connectBluetooth() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled())
            turnOnBluetooth();
        else if (!isLocationOpen()) {
            Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(enableLocate, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
        else {
            startService(new Intent(MyActivity.this, BluetoothLeAppService.class));
            finish();
        }
    }
    /**
     *判断位置信息是否开启
     * @return
     */
    public boolean isLocationOpen(){
        LocationManager manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsProvider;
    }
    /**
     * 弹出系统弹框提示用户打开 Bluetooth
     */
    private void turnOnBluetooth(){
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);

        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        requestBluetoothOn
                .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 设置 Bluetooth 设备可见时间
        requestBluetoothOn.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                BLUETOOTH_DISCOVERABLE_DURATION);

        // 请求开启 Bluetooth
        this.startActivityForResult(requestBluetoothOn,
                REQUEST_CODE_BLUETOOTH_ON);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        if (requestCode == REQUEST_CODE_BLUETOOTH_ON)
        {
            switch (resultCode)
            {
                // 点击确认按钮
                case Activity.RESULT_OK:
                    if (!isLocationOpen()) {
                        Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableLocate, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    }else {
                        startService(new Intent(MyActivity.this, BluetoothLeAppService.class));
                        finish();
                    }
                    break;
                // 点击确认按钮
                case 120:
                    if (!isLocationOpen()) {
                        Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableLocate, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    }else {
                        startService(new Intent(MyActivity.this, BluetoothLeAppService.class));
                        finish();
                    }
                    break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED:
                    finish();
                    System.exit(0);
                    break;
                default:
                    startService(new Intent(MyActivity.this, BluetoothLeAppService.class));
                    finish();
                    break;
            }
        }
        else if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (!isLocationOpen()) {
                //若未开启位置信息功能，则退出该应用
                finish();
                System.exit(0);
            }else{
                startService(new Intent(MyActivity.this, BluetoothLeAppService.class));
                finish();
            }
        }
    }
}
