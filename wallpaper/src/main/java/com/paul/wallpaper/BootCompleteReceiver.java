package com.paul.wallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"收到action"+intent.getAction());
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())||Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Toast.makeText(context.getApplicationContext(), "收到广播"+intent.getAction(),Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(context.getApplicationContext(), MyService.class);
            context.startService(serviceIntent);
//        }else{
//            Log.e(TAG,"关闭服务");
//            Intent serviceIntent = new Intent(context, MyService.class);
//            context.stopService(serviceIntent);
        }
    }
}
