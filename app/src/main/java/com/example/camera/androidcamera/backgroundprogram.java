package com.example.camera.androidcamera;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;
import java.util.List;


/**
 * Created by fufamilyDeskWin7 on 2016/2/29.
 */
public class backgroundprogram extends Service {
    private String debug = "debug";

    private Handler handler = new Handler();
    private SensorManager sensorMgr;
    private Sensor sensor;
    SensorEventListener SensorListen;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(debug, "onStart");

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> list = this.sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION);
        if (list.isEmpty()) {
//            this.insert2Tv("不支援傾斜感應器");
            Log.i(debug, "不支援傾斜感應器");
        }
        else {
            this.sensor = list.get(0);
//            this.insert2Tv("取得傾斜感應器：" + this.sensor.getName());
            Log.i(debug, "取得傾斜感應器：" + this.sensor.getName());
        }



        handler.postDelayed(showTime, 1000);
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(debug, "onDestroy");
        handler.removeCallbacks(showTime);
        super.onDestroy();
    }

    private Runnable showTime = new Runnable() {
        public void run() {
            Log.i(debug, "run");
            //log目前時間
            Log.i("time:", new Date().toString());


            if (sensor != null) {
//            this.insert2Tv("registerListener...");
                Log.i(debug, "registerListener...");

                sensorMgr.registerListener(SensorListen = new SensorEventListener() {
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
                        Log.i(debug, "方位角：" + event.values[0] + "  南北向：" + event.values[1] + "  東西向：" + event.values[2]);
                        sensorMgr.unregisterListener(this);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }



            handler.postDelayed(this, 1000);
        }
    };

}
