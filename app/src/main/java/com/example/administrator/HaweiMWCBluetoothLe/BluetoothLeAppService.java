package com.example.administrator.HaweiMWCBluetoothLe;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;


import android.annotation.SuppressLint;
import android.app.Service;
//api>=23
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanResult;
//
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.MediaStore;
import android.util.Log;

public class BluetoothLeAppService extends Service {
    private final static String C_UUID = "00001101-0000-1000-8000-00805F9B34FB";//6501ffff-aaaa-bbbb-cccc-10101F9B34FB
    private final static String MY_UUID = "abcdplmn-abzx-abcv-abbn-abcdefqwerty";
    private final String LOG_TAG = "BluetoothLeAppService";
    private ConnectThread connectThread;
    private BluetoothAdapter adapter;
    public final static int SERVICE_HANDLER_UPIMAGE= 1;
    public final static int SERVICE_CONNECTED =2;
    public final static int SERVICE_DISCONNECTED =3;
    public final static String SENSOR_MOVE="move";
    public final static String SENSOR_STOP="stop";
    public Handler servuceHandler;
    public Messenger activityHandler;
    private String sendMsg;
    private String ip;
    private Integer port;
    private ServerSocket serverSocket;
    private Socket socket;
    private String imagePath;
    private int num;
    private boolean isWhile;
    private boolean isSend;
    private boolean isBroadcast;
    private long size;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.i(LOG_TAG, "onBind");
        return null;
//        return new MyBinder();
    }

    public void onCreate() {
        super.onCreate();
        //前台信息
        sendMsg = "HaweiMWCBluetoothLe";

//        ip = getHostIP();
//        String ipstr = ip.replace(".", "");
//        port = 2700 + Integer.parseInt(ipstr.substring(ipstr.length() - 2, ipstr.length()));
//
//        createSocketService();
//
//        //接收广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SENSOR_MOVE);
        intentFilter.addAction(SENSOR_STOP);
        registerReceiver(receiver, intentFilter);
//
//        imagePath = "";
//        isSend = false;
//        isWhile = true;
//        isBroadcast = false;
//
//        Thread td = new Thread(new Runnable() {
//            @Override
//            public void run() {
                SensorManager manager = (SensorManager) BluetoothLeAppService.this.getSystemService(Service.SENSOR_SERVICE);
                MySensorEventListener listener = new MySensorEventListener(BluetoothLeAppService.this);
                manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);//TYPE_GRAVITY
//            }
//        });
//        td.start();

        //获取蓝牙
        adapter = BluetoothAdapter.getDefaultAdapter();
        BondedDevicesRemove();
    }
    private BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case SENSOR_MOVE:
                    Log.i("传感器","移动了");
                    if(connectThread != null)
                        connectThread.sendMessage("1");
                    break;
                case SENSOR_STOP:
                    Log.i("传感器","静止了");
                    if(connectThread != null)
                        connectThread.sendMessage("2");
                    break;
            }
        }
    };
    /**
     * 获取配对设备
     * */
    private void BondedDevicesRemove(){
        try {
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            if(devices.size()>0){
//                connect(devices.iterator().next());
                BluetoothDevice device = devices.iterator().next();
                //经典蓝牙
                connect(device);
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BondedDevicesRemove();
                    }
                },1000 * 10);
            }
        } catch (Exception e) {

        }
    }
        public  void connect(BluetoothDevice device) {
        Log.i(LOG_TAG,"connect");
        //连接蓝牙板
        ConnectThread ctd = new ConnectThread(device
                ,C_UUID
                ,threadHandler
                ,new ThreadCallback("externalbluetooth"){
                    @Override
                    public  void started(ConnectThread ct){
                        connectThread = ct;
                        connectThread.start();
                        SimpleDateFormat formatter  =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");
                        Date curDate =  new Date(System.currentTimeMillis());
//                        setData("("+formatter.format(curDate)+")蓝牙连接成功");
                        Log.i(LOG_TAG,"("+formatter.format(curDate)+")蓝牙连接成功");


                    }
                    @Override
                    public  void fail(String msg){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BondedDevicesRemove();
                            }
                        },1000 * 10);
                        SimpleDateFormat formatter  =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");
                        Date curDate =  new Date(System.currentTimeMillis());
//                        setData("("+formatter.format(curDate)+")蓝牙重新连接");
                        Log.i(LOG_TAG, "("+formatter.format(curDate)+")蓝牙重新连接");
                    }
                });
    }
    //线程回调事件
    public   Handler threadHandler = new Handler(){
        //回调信息
        //1:蓝牙板socket连接成功
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(LOG_TAG, ""+msg.arg1);
//            sendMsg = msg.arg1 + "";
            //startServiceExtras("蓝牙命令接收通知",sendMsg,"update");
            if(msg.arg1 <= 100){


            }
            //连接断开
            else if(msg.arg1 > 100){
                if(msg.arg1 == 101){
                    Log.i(LOG_TAG, "蓝牙断开");
                    SimpleDateFormat formatter  =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");
                    Date curDate =  new Date(System.currentTimeMillis());
                    setData("("+formatter.format(curDate)+")蓝牙断开");
                    //关闭蓝牙板设备线程
                    CloseThread();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BondedDevicesRemove();
                        }
                    },1000 * 10);

                }
            }

        }
    };
    private void CloseThread(){
        try {
            //关闭蓝牙板设备线程
            if(connectThread != null){
                connectThread.destroy();
                connectThread.interrupt();
                connectThread = null;
            }

        } catch (Exception e) {

        }
    }
//    private synchronized void selectImage(){
//        if(!isBroadcast) {
//            Log.i(LOG_TAG, "摇一摇");
//            isBroadcast = true;
//            String newPath = "";
////            while (isWhile) {
//            if (!isSend) {
//                newPath = getRecentlyPhotoPath(BluetoothLeAppService.this);
//                setData("图片路径:"+newPath);
//                if (!imagePath.equals(newPath) && newPath.contains("jpg") && socket != null && size > 0) {
//                    isSend = true;
//                    imagePath = newPath;
//                    sendFile();
//                }
//            }
//            isBroadcast = false;
//        }
//    }
//    private  void sendFile() {
//        Thread th1 = new Thread(new Runnable() {
//            int index = 1;
//
//            //
//            @Override
//            public void run() {
////                                        Log.i(LOG_TAG, "客户端接入");
//                try {
//                    socket.setReceiveBufferSize(1024 * 1024 * 10);
//                    socket.setSendBufferSize(1024 * 1024 * 10);
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
//                DataOutputStream ps = null;
//                try {
//                    //判断图片，竖屏不投
//                    BitmapFactory.Options opts = new BitmapFactory.Options();
//                    opts.inJustDecodeBounds = true;   //只去读图片的附加信息,不去解析真实的位图
//                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath,opts);
//                    if(opts.outWidth>opts.outHeight) {
//                        File file = new File(imagePath);
//                        ps = new DataOutputStream(socket.getOutputStream());
//                        String leng = "00000000000000000000";
//                        String le = file.length() + "";
//                        le = leng.substring(0, 20 - le.length()) + le;
//                        ps.write(le.getBytes());
//                        String indstr = "000";
//                        String ind = index + "";
//                        ind = indstr.substring(0, 3 - ind.length()) + ind;
//                        ps.write(ind.getBytes());
//                        index++;
//                        if (index > num)
//                            index = 1;
//                        DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
//                        byte[] buf = new byte[1024 * 1024 * 10];
//                        while (true) {
//                            int read = 0;
//                            if (fis != null)
//                                read = fis.read(buf);
//                            if (read == -1)
//                                break;
//                            Log.i(LOG_TAG, "开 始写入:" + read);
//                            setData("开 始写入:" + read);
//                            ps.write(buf, 0, read);
//                            Log.i(LOG_TAG, ps.size() + "");
//                        }
//                        ps.flush();
//                        fis.close();
//                        ps.close();
//                        socket.close();
//                        socket = null;
//                        Log.i(LOG_TAG, "结束写入");
//                        setData("结束写入");
//                    }
//                    isSend = false;
////                                            socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        th1.start();
//    }
    private void setData(String data){
        File upDir = getAppDir();
        File file = new File(upDir+"/"+"log.txt");
        data = "--------------------------------------------\r\n" + data;
        if(file.exists()){
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                    out.write(data);
                out.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }else {
            if(!upDir.exists())
                upDir.mkdir();
            try {
                file = new File(upDir+"/"+"log.txt");
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data.getBytes());
                outputStream.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }
    /**
     * 获取APP目录
     * */
    public static File getAppDir() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            File a = Environment.getDataDirectory();
            File c = Environment.getDownloadCacheDirectory();
            File resDir = new File(sdcardDir + "/LaurelsScent");
            return resDir;
        }
        return null;
    }

//    private   String getRecentlyPhotoPath(Context context) {
//        String searchPath = MediaStore.Files.FileColumns.DATA + " LIKE '%" + "/DCIM/Camera/" + "%' ";
//        Uri uri = MediaStore.Files.getContentUri("external");
//        Cursor cursor = context.getContentResolver().query(
//                uri, new String[]{MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE}, searchPath, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
//        String filePath = "";
//        if (cursor != null && cursor.moveToFirst()) {
//            filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
//            size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
//            //Log.i(LOG_TAG, "图片大小："+size);
//        }
//        if (!cursor.isClosed()) {
//            cursor.close();
//        }
//        return filePath;
//    }
//    private synchronized   void createSocketService(){
//        if(serverSocket == null) {
//            try {
//                Log.i(LOG_TAG, "创建socket服务");
//                serverSocket = new ServerSocket(port);
//                serverSocket.setReceiveBufferSize(1024 * 1024 * 10);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Thread th = new Thread(new Runnable() {
//                private int index=1;
//                @Override
//                public void run() {
//                    while (true){
//                        try{
//                            if(socket == null) {
//                                socket = serverSocket.accept();
//                                Log.i(LOG_TAG, "客户端接入");
//                            }
//                        }
//                        catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            th.start();
//        }
//    }
//
//    /**
//     * 获取ip地址
//     * @return
//     */
//    public static String getHostIP() {
//
//        String hostIp = null;
//        try {
//            Enumeration nis = NetworkInterface.getNetworkInterfaces();
//            InetAddress ia = null;
//            while (nis.hasMoreElements()) {
//                NetworkInterface ni = (NetworkInterface) nis.nextElement();
//                Enumeration<InetAddress> ias = ni.getInetAddresses();
//                while (ias.hasMoreElements()) {
//                    ia = ias.nextElement();
//                    if (ia instanceof Inet6Address) {
//                        continue;// skip ipv6
//                    }
//                    String ip = ia.getHostAddress();
//                    if (!"127.0.0.1".equals(ip)) {
//                        hostIp = ia.getHostAddress();
//                        break;
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            Log.i("yao", "SocketException");
//            e.printStackTrace();
//        }
//        return hostIp;
//
//    }
    @Override
    @SuppressWarnings("ResourceType")
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG,"onStartCommand");
        flags = START_STICKY;

        startServiceExtras("MWC",sendMsg,"create");
        return super.onStartCommand(intent, flags, startId);
//    	return START_STICKY;
    }
    private void startServiceExtras(String title,String msg,String type){
        Intent intent = new Intent(this, BluetoothLeNotifyServuce.class);
        intent.putExtra("title", title);
        intent.putExtra("msg", msg);
        intent.putExtra("type", type);
        startService(intent);
    }
    private void openActivity(){
        Intent intent = new Intent(this, ControlActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("messenger",new Messenger(servuceHandler));
        startActivity(intent);
    }
    /**
     * 解除绑定服务时调用
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    /* 当内存不够时执行改方法 */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        onDestroy();// 注销该service
    }

    /* 当从新尝试绑定时执行 */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i(LOG_TAG, "onRebind");
    }
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(LOG_TAG, "onStart");
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
//        try {
//            if(socket!=null) {
//                socket.close();
//                socket = null;
//            }
//            if(serverSocket!=null) {
//                serverSocket.close();
//                serverSocket = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.i(LOG_TAG,"onDestroy");
        CloseThread();
        super.onDestroy();
    }
}
