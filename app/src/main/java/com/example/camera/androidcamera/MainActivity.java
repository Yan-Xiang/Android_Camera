package com.example.camera.androidcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private String debug = "debug";
    private Long startTime;
    private Handler handler = new Handler();

    private SensorManager sensorMgr;
    private Sensor sensor;
    private TextView tv;
    private Button starback, stopback;
    private Camera camera;
    private ImageView takepictureimg;
    private Button takepicturebtn, starecamerabtn, camera_rel;

    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(debug, "onCreate");



        //取得目前時間
        startTime = System.currentTimeMillis();
        //設定定時要執行的方法
        handler.removeCallbacks(updateTimer);
        //設定Delay的時間
        handler.postDelayed(updateTimer, 1000);

//=========================================================================================
        this.sensorMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        List<Sensor> list = this.sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION);
        if (list.isEmpty()) {
            Log.i(debug, "不支援傾斜感應器");
        }
        else {
            this.sensor = list.get(0);
            Log.i(debug, "取得傾斜感應器：" + this.sensor.getName());
        }

//===================================================================================

        starback = (Button) findViewById(R.id.starbackground);
        stopback = (Button) findViewById(R.id.stopbackground);
        starback.setOnClickListener(startClickListener);
        stopback.setOnClickListener(stopClickListener);

//==================================================================================

        starecamerabtn = (Button) findViewById(R.id.camerabtn);
        takepicturebtn = (Button) findViewById(R.id.takepicturebtn);
        camera_rel = (Button) findViewById(R.id.camera_rel);
        takepictureimg = (ImageView) findViewById(R.id.takeaimg);

        starecamerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(debug, "before Camera open");
                camera=Camera.open();
                try {

                    Camera.Parameters parameters=camera.getParameters();
                    parameters.setPictureFormat(PixelFormat.JPEG);
                    parameters.setPreviewSize(320, 240);
                    camera.setParameters(parameters);
                    //設置參數
                    camera.setPreviewDisplay(surfaceHolder);
//                    //鏡頭的方向和手機相差90度，所以要轉向
//                    //camera.setDisplayOrientation(90);
//                    //攝影頭畫面顯示在Surface上
                    camera.startPreview();
                }
                catch (IOException e) {
//
                    e.printStackTrace();
                }
                Log.i(debug, "after Camera open");
            }
        });
        takepicturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(debug, "before Camera take picture");
//
//                camera.takePicture(null, null, null,
//                    new Camera.PictureCallback() {
//                    @Override
//                    public void onPictureTaken(byte[] data, Camera camera) {
//                        Log.i(debug, "before startPreview");
//                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        takepictureimg.setImageBitmap(bm);
////                        camera.startPreview();
//                        Log.i(debug, "after set img");
//                    }
//                });


                camera.takePicture(null, null, jpeg);
//                camera.autoFocus(afcb);
//                camera.autoFocus(cb);
                Log.i(debug, "after Camera take picture");

            }
        });
        camera_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(debug, "before Camera release");
                camera.release();
                camera = null;
                Log.i(debug, "after Camera release");
            }
        });
//===================================================================

        surfaceView1=(SurfaceView)findViewById(R.id.surfaceView1);

        surfaceHolder=surfaceView1.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

    }
//    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            Log.i(debug, "before startPreview");
//            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
//            takepictureimg.setImageBitmap(bm);
//            camera.startPreview();
//            Log.i(debug, "after set img");
//        }
//    };
//    Camera.AutoFocusCallback cb = new Camera.AutoFocusCallback(){
//        @Override
//        public void onAutoFocus(boolean success, Camera camera) {
//            Log.i(debug, "before Camera AutoFocus     success? "+success);
//            if (success) {
//            camera.takePicture(null, null, jpeg);
//                Log.i(debug, "after Camera AutoFocus");
//
//            }
//    }};

//=====================================================================================
    private Button.OnClickListener startClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            Log.i(debug, "start");
            //啟動服務
            Intent intent = new Intent(MainActivity.this, backgroundprogram.class);
            startService(intent);
            onPause();
        }
    };

    private Button.OnClickListener stopClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            Log.i(debug, "stop");
            //停止服務
            Intent intent = new Intent(MainActivity.this, backgroundprogram.class);
            stopService(intent);
            onResume();

        }
    };


    //固定要執行的方法
    private Runnable updateTimer = new Runnable() {
        public void run() {
            Log.i(debug, "updateTimer run");
            final TextView time = (TextView) findViewById(R.id.timer);
            Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過分鐘數
            Long minius = (spentTime / 1000) / 60;
            //計算目前已過秒數
            Long seconds = (spentTime / 1000) % 60;
            time.setText(minius + ":" + seconds);

            //自動對焦
//            camera.autoFocus(afcb);
            camera.takePicture(null, null, jpeg);
            handler.postDelayed(this, 1000);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (this.sensor != null) {
//            this.insert2Tv("registerListener...");
            Log.i(debug, "registerListener...");

//            this.sensorMgr.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (this.sensor != null) {
//            this.insert2Tv("unregisterListener...");
            Log.i(debug, "unregisterListener...");

//            this.sensorMgr.unregisterListener(this, this.sensor);
        }
    }
    Camera.PictureCallback jpeg =new Camera.PictureCallback(){

        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
            //byte數组轉換成Bitmap
            takepictureimg.setImageBitmap(bmp);
            //拍下圖片顯示在下面的ImageView裡
//            FileOutputStream fop;
//            try {
//                fop=new FileOutputStream("/sdcard/dd.jpg");
//                //實例化FileOutputStream，參數是生成路徑
//                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fop);
//                //壓缩bitmap寫進outputStream 參數：輸出格式  輸出質量  目標OutputStream
//                //格式可以為jpg,png,jpg不能存儲透明
//                fop.close();
//                System.out.println("拍照成功");
//                //關閉流
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//                System.out.println("FileNotFoundException");
//
//            } catch (IOException e) {
//
//                e.printStackTrace();
//                System.out.println("IOException");
//            }
            camera.startPreview();
            //需要手動重新startPreview，否則停在拍下的瞬間
        }

    };
    //自動對焦監聽式
    Camera.AutoFocusCallback afcb= new Camera.AutoFocusCallback(){

        public void onAutoFocus(boolean success, Camera camera) {

//            if(success){
                //對焦成功才拍照
                camera.takePicture(null, null, jpeg);
//            }
        }


    };
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera=Camera.open();
        try {

            Camera.Parameters parameters=camera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPreviewSize(320, 240);
            camera.setParameters(parameters);
            //設置參數
            camera.setPreviewDisplay(surfaceHolder);
            //鏡頭的方向和手機相差90度，所以要轉向
            //camera.setDisplayOrientation(90);
            //攝影頭畫面顯示在Surface上
            camera.startPreview();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        //關閉預覽
        camera.release();
    }
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        // 方位角，就是手機頭的朝向，北為0，東為90，餘類推，實做結果不準
//        // float val = event.values[0];
//        // 南北向旋轉，手機水平放置螢幕朝向上（0）、頭朝上（-90）
//        // 頭向下（90）、水平放置螢幕朝向下（-180/180）
//        // float val = event.values[1];
//        // 東西向旋轉，手機水平放置螢幕朝向上（0）、向右翻螢幕朝右（-90）
//        // 向左翻螢幕朝左（90）、水平放置螢幕朝向下（0）
//        // float val = event.values[2];
////        this.insert2Tv("方位角：" + event.values[0] + "南北向：" + event.values[1]
////                + "東西向：" + event.values[2]);
//        Log.i(debug, "方位角：" + event.values[0] + "  南北向：" + event.values[1] + "  東西向：" + event.values[2]);
//    }
//
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }



}
