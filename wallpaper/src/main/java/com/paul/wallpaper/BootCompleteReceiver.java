package com.paul.wallpaper;

import static com.paul.wallpaper.WallUtils.ALREADY_WAIT_TIME;
import static com.paul.wallpaper.WallUtils.START_TIME;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e(TAG, "收到action" + intent.getAction());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context.getApplicationContext(), MyService.class);
            context.startService(serviceIntent);
        } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            long startTime = SpUtils.getLongSP(context, START_TIME);
            long already = SpUtils.getLongSP(context, ALREADY_WAIT_TIME);
            long wait = System.currentTimeMillis() - startTime + already;
            LogUtils.e(TAG, "收到action 已wait：" + wait);
            SpUtils.setLongSp(context, ALREADY_WAIT_TIME, wait);
            Intent serviceIntent = new Intent(context.getApplicationContext(), MyService.class);
            serviceIntent.putExtra("stop",true);
            context.startService(serviceIntent);
        }

    }
}
