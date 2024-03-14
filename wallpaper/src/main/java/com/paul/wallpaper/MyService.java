package com.paul.wallpaper;

import static com.paul.wallpaper.WallUtils.ALREADY_WAIT_TIME;
import static com.paul.wallpaper.WallUtils.CURRENT;
import static com.paul.wallpaper.WallUtils.START_TIME;
import static com.paul.wallpaper.WallUtils.TIME;
import static com.paul.wallpaper.WallUtils.current;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.paul.wallpaper.bean.WallChangeEvent;

import org.greenrobot.eventbus.EventBus;

public class MyService extends Service {
    private static final String TAG = "MyService";

    private BootCompleteReceiver bootCompleteReceiver;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        WallUtils.mWidth = dm.widthPixels;
        WallUtils.mHeight = 2400;
        initTime();

        registSreenStatusReceiver();
    }

    private void registSreenStatusReceiver() {
        bootCompleteReceiver = new BootCompleteReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(bootCompleteReceiver, screenStatusIF);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e(TAG, "onStartCommand");
        showNotification();

        //灭屏后需remove runnable定时器
        boolean stop = intent.getBooleanExtra("stop",false);
        if(stop){
            LogUtils.e(TAG, "needRefresh:灭屏后stop");
            handler.removeCallbacks(runnable);
            return START_STICKY;
        }
        //只有传needRefresh=true才刷，避免多次重复刷导致桌面壁纸闪黑屏
        //目前只有点上一张/下一张才会传此值去刷新
        //还有就是时间到了刷下一张
        if(intent.getBooleanExtra("needRefresh",false)){
            LogUtils.e(TAG, "needRefresh");
            WallUtils.setPaper(MyService.this, false);
        }

        //根据已等待时长来延迟发送
        long alreadyWaitTime = SpUtils.getLongSP(this, ALREADY_WAIT_TIME);
        LogUtils.e(TAG, "need:" + (waitTime - alreadyWaitTime));
        if(waitTime - alreadyWaitTime>0){
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, waitTime - alreadyWaitTime);
        }else{
            handler.postDelayed(runnable, 0);
        }

        //重新写入startTime
        SpUtils.setLongSp(this, START_TIME, System.currentTimeMillis());

        return START_STICKY;
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            WallUtils.setPaper(MyService.this, true);
            SpUtils.setLongSp(MyService.this, ALREADY_WAIT_TIME, 0);
            showNotification();
            handler.postDelayed(runnable, waitTime);
            EventBus.getDefault().post(new WallChangeEvent(current));
        }
    };

    private void showNotification() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationChannel channel = new NotificationChannel("service", "test", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder service = new NotificationCompat.Builder(this, "service");
        service.setContentTitle(SpUtils.getIntSP(this, CURRENT)+"" );
        service.setContentIntent(pi);
        service.setContentText(WallUtils.getPathByIndex(SpUtils.getIntSP(this, CURRENT)));
        service.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = service.getNotification();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    public static int waitTime = 6000;



    private void initTime() {
        waitTime = SpUtils.getIntSP(this, TIME);
        if (waitTime == 0) {
            waitTime = 6000;
            SpUtils.setIntSp(this, TIME, waitTime);
        }
        WallUtils.current = SpUtils.getIntSP(this, CURRENT);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "SERVICE onDestroy");
        super.onDestroy();
        unregisterReceiver(bootCompleteReceiver);
    }
}