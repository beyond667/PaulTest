package com.paul.wallpaper;

import static com.paul.wallpaper.WallUtils.CURRENT;
import static com.paul.wallpaper.WallUtils.TIME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.paul.wallpaper.databinding.ActivityMainBinding;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private ActivityMainBinding binding;
    private int REQUEST_CODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int time =SpUtils.getIntSP(this,TIME);
        if(time==0){
            //默认1分钟切换壁纸
            time = 60000;
            SpUtils.setIntSp(MainActivity.this, TIME, time);
        }
        binding.time.setText(time/1000/60+"");

        requestPermission();
        binding.next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                String str = binding.time.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    int time = Integer.parseInt(str) * 1000*60;
                    MyService.time = time;
                    SpUtils.setIntSp(MainActivity.this, TIME, time);
                }
                SpUtils.setIntSp(MainActivity.this, CURRENT, SpUtils.getIntSP(MainActivity.this,CURRENT)+1);
                binding.bg.setImageBitmap(WallUtils.getWallBitmap(MainActivity.this,false));
                start();
            }
        });
        binding.before.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                String str = binding.time.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    int time = Integer.parseInt(str) * 1000*60;
                    MyService.time = time;
                    SpUtils.setIntSp(MainActivity.this, TIME, time);
                }
                SpUtils.setIntSp(MainActivity.this, CURRENT, SpUtils.getIntSP(MainActivity.this,CURRENT)-1);

                binding.bg.setImageBitmap(WallUtils.getWallBitmap(MainActivity.this,false));
                start();
            }
        });
        binding.stop.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                try {
                    WallpaperManager wpm = (WallpaperManager) getSystemService(Context.WALLPAPER_SERVICE);
                    if (wpm != null) {
                        wpm.setResource(R.drawable.ic_launcher_background);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Failed to set wallpaper: " + e);
                }
                stop();
            }
        });
        start();
    }

    private void start() {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    private void stop() {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }



    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();

    }
    private void setBg(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.bg.setImageBitmap(WallUtils.getWallBitmap(MainActivity.this,false));
            }
        },100);
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                setBg();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                setBg();
            } else {
                Toast.makeText(this, "请在设置中允许该应用的存储权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}