package com.example.administrator.HaweiMWCBluetoothLe;

/**
 * Created by pc on 2018/1/29.
 */

public abstract class ThreadCallback {
    public  String socketName;
    public  ThreadCallback(String socketName){
        this.socketName = socketName;
    }

    public ThreadCallback() {

    }
    public  void event(){

    }
    public  void started(ConnectThread connectThread){

    }
    public  void started(){

    }
    public  void fail(String msg){

    }
}