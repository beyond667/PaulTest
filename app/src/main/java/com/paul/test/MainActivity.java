package com.paul.test;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import java.util.TooManyListenersException;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    Button goFreedomButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        goFreedomButton = findViewById(R.id.go_freedom);
        goFreedomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent("com.on.systemUi.start.voice");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                Log.e("===","========111==0="+checkSIMExist(0));
                Log.e("===","========111==1="+checkSIMExist(1));
//                startActivity(new Intent(MainActivity.this,SecondActivity.class));
//                Intent intentSend = new Intent("com.on.systemUi.start.voice.function");
//                intentSend.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intentSend);

//                showDialog();
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.wits.filemanager", "com.wits.filemanager.FileMainActivity"));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);


                //                Intent intent = new Intent("com.wits.systemui.show_volume");
//                sendBroadcast(intent);
//                startMemProfiler();
            }
        });

    }

    //检测双sim卡是否存在
    //type : 0 （卡1），1（卡2）
    public boolean checkSIMExist(int type) {
        boolean result = false;
        SubscriptionManager mSubscriptionManager = null;
            mSubscriptionManager = SubscriptionManager.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("sim", "checkSIMExist: false no permission");
            return false;
        }
        SubscriptionInfo sub = null;
            sub = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(type);

        if (null != sub) {
            result = true;
        }

        Log.d("sim", "checkSIMExist result: "+result);
        return result;
    }

    private Dialog mDialog;
    private Window mWindow;
    private ViewGroup mDialogView;

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("===","==========444======onresume");
        //子线程更新UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                goFreedomButton.setText("新的");
            }
        }).start();



    }

    private void showDialog(){
        mDialog = new Dialog(this);
        mWindow = mDialog.getWindow();
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        //mWindow.addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY);
        //[BUGFIX]-Del-BEGIN by yuanguangming@witstech.cn, 2023/7/28, for Task#17589 Reference M610 Volume Dialog transparent
//        mWindow.setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY);
//        mWindow.setWindowAnimations(com.android.internal.R.style.Animation_Toast);
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.format = PixelFormat.TRANSLUCENT;
        lp.setTitle("===");
        lp.windowAnimations = -1;
        //[BUGFIX]-Mod-BEGIN by yuanguangming@witstech.cn, 2023/7/28, for Task#17589 for layout of volume dialog
        //lp.gravity = mContext.getResources().getInteger(R.integer.volume_dialog_gravity);
        lp.gravity = Gravity.CENTER;
        //[BUGFIX]-Mod-END by yuanguangming@witstech.cn, 2023/7/28, for Task#17589 for layout of volume dialog

        mWindow.setAttributes(lp);
        mWindow.setLayout(WRAP_CONTENT, WRAP_CONTENT);



    }



    private void startMemProfiler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    displayMemory();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void displayMemory() {
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        Log.i(TAG, "系统剩余内存:" + (info.availMem / (1024 * 1024)) + "M");
        Log.i(TAG, "系统是否处于低内存运行：" + info.lowMemory);
        Log.i(TAG, "当系统剩余内存低于" + (info.threshold / (1024 * 1024)) + "M" + "时就看成低内存运行");
        Log.i(TAG, "系统已经分配的native内存：" + (Debug.getNativeHeapAllocatedSize() / (1024 * 1024)) + "M");
        Log.i(TAG, "系统还剩余的native内存：" + (Debug.getNativeHeapFreeSize() / (1024 * 1024)) + "M");
        Log.i(TAG, "系统的所有native内存大小：" + (Debug.getNativeHeapSize() / (1024 * 1024)) + "M");
    }

}