package com.example.administrator.HaweiMWCBluetoothLe;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import android.os.Message;

//import com.joanzapata.pdfview.PDFView;
//import com.squareup.okhttp.Request;
//
//import org.xwalk.core.JavascriptInterface;
//import org.xwalk.core.XWalkPreferences;
//import org.xwalk.core.XWalkResourceClient;
//import org.xwalk.core.XWalkSettings;
//import org.xwalk.core.XWalkUIClient;
//import org.xwalk.core.XWalkView;
//import org.xwalk.core.XWalkWebResourceRequest;
//import org.xwalk.core.XWalkWebResourceResponse;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ControlActivity extends AppCompatActivity{
    public final static int ACTIVITY_CREATESOCKET= 1;
//    public final static int ACTIVITY_HANDLER_UPIMAGECOMPLETE = 2;
    private static String mac = "";
    private static ProgressDialog mProgressDialog;
    private  FrameLayout frameLayout;
    private  TextView textView;
    private  ImageView img;
    private  ImageView imageS;
    private String TAG = "ControlActivity";
    private WebView webView;
    public Messenger servuceHandler;
    public Handler activityHandler;
    private String imagePath;
    private static String url = "/home/ChatClient";
    private String ip;
    private String port;
    private boolean sending;
    public final static String ServiceIP = "192.168.1.125";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Log.i("ControlActivity","onCreate");

    }
    private void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();

        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
        Uri uri = FileProvider.getUriForFile(this, "com.xykj.customview.fileprovider", file);
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 111);
    }
    private String GetIp(){
        File file = HasFile("ip.text");
        if(file != null){
            return file.getName().substring(0,file.getName().length()-5).replace("_",".");
        }else {
            File upDir = getAppDir();
            if(!upDir.exists())
                upDir.mkdir();
            try {
                file = new File(upDir,ServiceIP.replace(".","_")+ ".text");
//            file.mkdir();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write((ServiceIP).getBytes());
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ServiceIP.replace(".","_");
        }
    }
    public  File HasFile(String name){
        File upDir = getAppDir();
        if(upDir == null || upDir.length() == 0)
            return null;
        File[] list = upDir.listFiles();
        File ipFile = null;
        for (File file : list){
            if(file.getName().contains("192")) {
                ipFile = file;
                break;
            }
        }
        return  ipFile;
//        File file = new File(upDir+"/"+name);
//        if(file.exists())
//            return  file;
//        else
//            return null;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
//                if (resultCode == RESULT_OK) {
//                    Intent intent = new Intent("com.android.camera.action.CROP");
//                    intent.setDataAndType(imageUri, "image/*");
//                    intent.putExtra("scale", true);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    // 启动裁剪
//                    startActivityForResult(intent, 2);
//                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    if (resultCode == RESULT_OK) {
                        //判断手机系统版本号
                        if (Build.VERSION.SDK_INT > 19) {
                            //4.4及以上系统使用这个方法处理图片
                            handleImgeOnKitKat(data);
                        }else {
                            handleImageBeforeKitKat(data);
                        }
                    }
                }
                break;
//            case 3:
//                if (resultCode == RESULT_OK) {
//                    imageUri = data.getData();
//                    Intent intent = new Intent("com.android.camera.action.CROP");
//                    intent.setDataAndType(imageUri, "image/*");
//                    intent.putExtra("scale", true);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    // 启动裁剪
//                    startActivityForResult(intent, 4);
//                }
//                break;
//            case 4:
//                if (resultCode == RESULT_OK) {
//                    try {
//                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        // 将裁剪后的照片显示出来
//                        img.setImageBitmap(bitmap);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
        }
    }
    /**
     * 4.4及以上系统处理图片的方法
     * */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImgeOnKitKat(Intent data) {

        Uri uri = data.getData();
         if (DocumentsContract.isDocumentUri(this,uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式的id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }else if ("content".equalsIgnoreCase(uri.getScheme())) {
                //如果是content类型的uri，则使用普通方式处理
                imagePath = getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())) {
                //如果是file类型的uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
            //根据图片路径显示图片
            displayImage(imagePath);
        }
    }
    /**
     *4.4以下系统处理图片的方法
     * */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    /**
     * 根据图片路径显示图片的方法
     * */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;   //只去读图片的附加信息,不去解析真实的位图
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath,opts);
            opts.inSampleSize = calculateInSampleSize(opts,1280,720);
            opts.inJustDecodeBounds = false;//真正的去解析位图
            bitmap = BitmapFactory.decodeFile(imagePath, opts);
//            Log.e("压缩后占用内存：", bitmap.getByteCount() + "");
//            Log.e("压缩后图片高度：",bitmap.getHeight()+"");
//            Log.e("压缩后图片宽度：",bitmap.getWidth()+"");

//            new Handler().post(new Runnable() {
//                @Override
//                public void run() {
//                    center(true,true);
//                }
//            });
            img.setImageBitmap(bitmap);

        }else {
//            ToastUtil.showShort(this,"failed to get image");
        }
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.e("inSampleSize：",inSampleSize+"");
        return inSampleSize;
    }
    /**
     * 通过uri和selection来获取真实的图片路径
     * */
    private String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private Bitmap center(Bitmap bitmap, boolean horizontal, boolean vertical)
    {

        Matrix m = new Matrix();
        //m.set(matrix);
        RectF rect = new RectF(0, 0, img.getWidth(), img.getHeight());
        m.mapRect(rect);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (vertical)
        {
            int screenHeight = frameLayout.getHeight();  //手机屏幕分辨率的高度
//            int screenHeight = 400;
            if (height < screenHeight)
            {
                deltaY = (screenHeight - height)/2 - rect.top;
            }else if (rect.top > 0)
            {
                deltaY = -rect.top;
            }else if (rect.bottom < screenHeight)
            {
                deltaY = frameLayout.getHeight() - rect.bottom;
            }
        }

        if (horizontal)
        {
            int screenWidth = frameLayout.getWidth();  //手机屏幕分辨率的宽度
//            int screenWidth = 400;
            if (width < screenWidth)
            {
                deltaX = (screenWidth - width)/2 - rect.left;
            }else if (rect.left > 0)
            {
                deltaX = -rect.left;
            }else if (rect.right < screenWidth)
            {
                deltaX = screenWidth - rect.right;
            }
        }
        //matrix.postTranslate(deltaX, deltaY);

        Bitmap composedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(composedBitmap);
        cv.drawBitmap(bitmap, deltaX, deltaY, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return composedBitmap;
    }
    public static void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable  bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }
    //获取MAC地址
    public static String getMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
    private  int  getFileNum(String path){
        return 1;
    }

    class JSInterface {
        //JS需要调用的方法
        @JavascriptInterface
        public void sendLog(String log){
            if(log.contains("连接中断")){
                Message message = Message.obtain();
                message.what = BluetoothLeAppService.SERVICE_DISCONNECTED;
                message.replyTo = new Messenger(activityHandler);
                try {
                    servuceHandler.send(message);
                } catch (RemoteException e) {
                }
                activityHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    textView.setText("Disconnected");
                    imageS.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    sending = true;
                    }
                });
            }else {
                Message message = Message.obtain();
                message.what = BluetoothLeAppService.SERVICE_CONNECTED;
                message.replyTo = new Messenger(activityHandler);
                try {
                    servuceHandler.send(message);
                } catch (RemoteException e) {
                }
//                activityHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        imageS.setVisibility(View.VISIBLE);
//                        textView.setVisibility(View.GONE);
//                        sending = true;
//                    }
//                });
            }
            Log.i("ControlActivity", log);
        }
        //队列通知
        @JavascriptInterface
        public void noticeQueue(String  json){
            int index = 0;
            boolean has = false;
            String[] list = json.split(",");
            for(String m : list){
                if(m.equals(mac)) {
                    has = true;
                    break;
                }
                index++;
            }
            final int finalIndex = index;
            final boolean finalHas = has;
            activityHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(finalHas) {
                        if (finalIndex == 0) {
                            imageS.setVisibility(View.GONE);
                            textView.setText("Projection...");
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            imageS.setVisibility(View.GONE);
                            textView.setText("Wait:" + finalIndex);
                            textView.setVisibility(View.VISIBLE);
                        }
                        sending = false;
                    }else {
                        imageS.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                        sending = true;
                    }
                }
            });
        }
    }
    private  void dialogShow(final String title,final String msg){
        if(mProgressDialog == null)
            mProgressDialog = ProgressDialog.show(ControlActivity.this, title, msg, true);
        else {
            mProgressDialog.setMessage(msg);
            mProgressDialog.setTitle(title);
        }
    }
    private String getName(String file){
        String[] str=file.split("/");
        String name=str[str.length-1];
        return name.toLowerCase();
    }

    @Override
    public void onDestroy() {
//    	stopService(intent);
//        sfc.destroy();
        super.onDestroy();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        //OkHttpClientManager
        android.os.Process.killProcess(android.os.Process.myPid());
        Log.i(TAG, "onDestory called.");
    }

    //Activity创建或者从后台重新回到前台时被调用
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called.");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
//        if (WebView != null) {
//            WebView.onNewIntent(intent);
//        }
        setIntent(intent);
        Log.i(TAG, "onNewIntent called.");
    }

    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
//        if(!sfc.isSurfaceCreated)
//        {
//            sfc.CreateSurface();
//        }
        Log.i(TAG, "onRestart called.");
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.resumeTimers();
        }
        Log.i(TAG, "onResume called.");
    }

    //Activity窗口获得或失去焦点时被调用,在onResume之后或onPause之后
    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	Log.i(TAG, "onWindowFocusChanged called.");
    }*/

    //Activity被覆盖到下面或者锁屏时被调用
    @Override
    protected void onPause() {
        super.onPause();
//        if (xWalkWebView != null) {
//            xWalkWebView.pauseTimers();
//            xWalkWebView.onHide();
//        }
        if (webView != null) {
            webView.pauseTimers();
        }
//    	sfc.destroy();
        Log.i(TAG, "onPause called.");
        //有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
    }

    //退出当前Activity或者跳转到新Activity时被调用
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called.");
    }

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState ");
        super.onSaveInstanceState(outState);
    }
    //阻止BACK输入
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
    /**
     * Activity被系统杀死后再重建时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity.
     * 这两种情况下onRestoreInstanceState都会被调用,在onStart之后.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }
    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }
}
