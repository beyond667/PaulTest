package com.paul.wallpaper;

import static com.paul.wallpaper.WallUtils.CURRENT;
import static com.paul.wallpaper.WallUtils.TIME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
    private int REQUEST_CODE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.transparentNavAndStatusBar(this);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkPermission();
        int time = SpUtils.getIntSP(this, TIME);
        if (time == 0) {
            //默认1分钟切换壁纸
            time = 60000;
            SpUtils.setIntSp(MainActivity.this, TIME, time);
        }
        binding.time.setText(time / 1000 / 60 + "");

        requestPermission();
        binding.next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                String str = binding.time.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    int time = Integer.parseInt(str) * 1000 * 60;
                    MyService.time = time;
                    SpUtils.setIntSp(MainActivity.this, TIME, time);
                }
                SpUtils.setIntSp(MainActivity.this, CURRENT, SpUtils.getIntSP(MainActivity.this, CURRENT) + 1);
                binding.bgLayout.setBackground(new BitmapDrawable(getResources(),WallUtils.getWallBitmap(MainActivity.this, false)));
                start();
            }
        });
        binding.before.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                String str = binding.time.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    int time = Integer.parseInt(str) * 1000 * 60;
                    MyService.time = time;
                    SpUtils.setIntSp(MainActivity.this, TIME, time);
                }
                SpUtils.setIntSp(MainActivity.this, CURRENT, SpUtils.getIntSP(MainActivity.this, CURRENT) - 1);

                binding.bgLayout.setBackground(new BitmapDrawable(getResources(),WallUtils.getWallBitmap(MainActivity.this, false)));
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

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        start();
        binding.shoucang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //移动到最爱文件夹
                WallUtils.moveCurrentToFavOrNot(MainActivity.this);
                setFav();
            }
        });
        binding.bgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.time.clearFocus();
            }
        });
    }
    private  void setFav(){
        String favPath = WallUtils.getPath(this,false);
        binding.fav.setSelected(favPath.contains("最爱"));
    }

    private void start() {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        setFav();
    }

    private void stop() {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定删除");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WallUtils.delete(MainActivity.this);
                binding.bgLayout.setBackground(new BitmapDrawable(getResources(),WallUtils.getWallBitmap(MainActivity.this, false)));
                start();
            }
        });
        builder.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();

    }

    private void setBg() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.bgLayout.setBackground(new BitmapDrawable(getResources(),WallUtils.getWallBitmap(MainActivity.this, false)));
            }
        }, 100);
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

    private AlertDialog dialog;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean havePermission = false;

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                dialog = new AlertDialog.Builder(this)
                        .setTitle("提示")//设置标题
                        .setMessage("请开启文件访问权限，否则无法正常使用本应用！")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivity(intent);
                            }
                        }).create();
                dialog.show();
            } else {
                havePermission = true;
                Log.i("swyLog", "Android 11以上，当前已有权限");
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    dialog = new AlertDialog.Builder(this)
                            .setTitle("提示")//设置标题
                            .setMessage("请开启文件访问权限，否则无法正常使用本应用！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                                }
                            }).create();
                    dialog.show();
                } else {
                    havePermission = true;
                    Log.i("swyLog", "Android 6.0以上，11以下，当前已有权限");
                }
            } else {
                havePermission = true;
                Log.i("swyLog", "Android 6.0以下，已获取权限");
            }
        }
    }
}