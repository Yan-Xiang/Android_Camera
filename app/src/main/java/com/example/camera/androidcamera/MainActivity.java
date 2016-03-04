package com.example.camera.androidcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {
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
//        handler.postDelayed(updateTimer, 1000);

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
                camera = Camera.open();
                Log.i(debug, "after Camera open");
            }
        });
        takepicturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(debug, "before Camera take picture");

                camera.takePicture(null, null, jpeg);
                camera.autoFocus(cb);
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

    }
    Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(debug, "before startPreview");

            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            takepictureimg.setImageBitmap(bm);
            camera.startPreview();

            Log.i(debug, "after set img");
        }

    };
    Camera.AutoFocusCallback cb = new Camera.AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.i(debug, "before Camera AutoFocus     success? "+success);
            if (success) {
            camera.takePicture(null, null, jpeg);
                Log.i(debug, "after Camera AutoFocus");

            }
    }};

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
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 方位角，就是手機頭的朝向，北為0，東為90，餘類推，實做結果不準
        // float val = event.values[0];
        // 南北向旋轉，手機水平放置螢幕朝向上（0）、頭朝上（-90）
        // 頭向下（90）、水平放置螢幕朝向下（-180/180）
        // float val = event.values[1];
        // 東西向旋轉，手機水平放置螢幕朝向上（0）、向右翻螢幕朝右（-90）
        // 向左翻螢幕朝左（90）、水平放置螢幕朝向下（0）
        // float val = event.values[2];
//        this.insert2Tv("方位角：" + event.values[0] + "南北向：" + event.values[1]
//                + "東西向：" + event.values[2]);
        Log.i(debug, "方位角：" + event.values[0] + "  南北向：" + event.values[1] + "  東西向：" + event.values[2]);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}
