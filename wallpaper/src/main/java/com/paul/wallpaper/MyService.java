package com.paul.wallpaper;

import static com.paul.wallpaper.WallUtils.CURRENT;
import static com.paul.wallpaper.WallUtils.TIME;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private static final String TAG = "MyService";

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mWidth = dm.widthPixels;
        mHeight = 2400;
        initTime();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationChannel channel = new NotificationChannel("service", "test", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder service = new NotificationCompat.Builder(this, "service");
        service.setContentTitle("我的世界我做主");
        service.setContentText("啊哈哈哈哈哈哈");
        service.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = service.getNotification();

        startForeground(1, notification);

        handler.removeCallbacks(runnable);
        loop();
        WallUtils.setPaper(this,false);
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static int time = 6000;

    public static int mWidth;
    public static int mHeight;

    private void initTime() {
        time = SpUtils.getIntSP(this, TIME);
        if (time == 0) {
            time = 6000;
            SpUtils.setIntSp(this, TIME, time);
        }
        WallUtils.current = SpUtils.getIntSP(this, CURRENT);
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    private void loop() {
        time = SpUtils.getIntSP(this, TIME);
        handler.postDelayed(runnable, time);
    }

    private Runnable runnable =new Runnable() {
        public void run() {
            WallUtils.setPaper(MyService.this,true);
            loop();
        }
    };

}