package com.paul.wallpaper;

import static com.paul.wallpaper.WallUtils.ALREADY_WAIT_TIME;
import static com.paul.wallpaper.WallUtils.CURRENT;
import static com.paul.wallpaper.WallUtils.DEFAULT_NAME;
import static com.paul.wallpaper.WallUtils.TIME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paul.wallpaper.bean.WallChangeEvent;
import com.paul.wallpaper.databinding.ActivityMainBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private ActivityMainBinding binding;
    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.transparentNavAndStatusBar(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        WallUtils.mWidth = dm.widthPixels;
        WallUtils.mHeight = 2400;

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
                setNextOrBefore(1);
            }
        });
        binding.before.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                setNextOrBefore(-1);
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

        start(false);
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
                binding.skip.clearFocus();
            }
        });
        binding.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.llInfo.getVisibility() == View.VISIBLE) {
                    binding.llInfo.setVisibility(View.GONE);
                } else {
                    binding.llInfo.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.changePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> paths = WallUtils.listFiles(MainActivity.this);
                paths.add(0,DEFAULT_NAME);
                showPopupWindow(paths);
            }
        });

        binding.btSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skipStr = binding.skip.getText().toString();
                if(TextUtils.isEmpty(skipStr)){
                    return;
                }
                int skip = Integer.parseInt(skipStr);
                if(skip>=WallUtils.paths.size()){
                    Toast.makeText(MainActivity.this,"请输入正确的数字0-"+WallUtils.paths.size(),Toast.LENGTH_SHORT).show();
                }else{
                    SpUtils.setIntSp(MainActivity.this, CURRENT, skip);
                    SpUtils.setLongSp(MainActivity.this, ALREADY_WAIT_TIME, 0);
                    setCurrent();
                    start(true);
                }
            }
        });

        binding.skip.setOnFocusChangeListener(onFocusChangeListener);
        binding.time.setOnFocusChangeListener(onFocusChangeListener);
    }
    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            }
        }
    };
    private void showPopupWindow(ArrayList<String> paths) {
        View view = LayoutInflater.from(this).inflate(R.layout.files_pop,null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        FileListAdapter scoreTeamAdapter = new FileListAdapter(paths);
        recyclerView.setAdapter(scoreTeamAdapter);
        scoreTeamAdapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onClickItem(int position, String fileName) {
                Log.e(TAG,"select:"+fileName);
                if(!fileName.equals(DEFAULT_NAME)){
                    WallUtils.selectPath = fileName;
                }else{
                    WallUtils.selectPath = "";
                }

                scoreTeamAdapter.notifyDataSetChanged();
                WallUtils.clear(MainActivity.this);
                WallUtils.initPathByCacheIfNeed(MainActivity.this);
                setCurrent();
                start(true);
            }
        });
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(binding.llInfo);
    }
    private void setNextOrBefore(int add) {
        String str = binding.time.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            int time = Integer.parseInt(str) * 1000 * 60;
            MyService.waitTime = time;
            SpUtils.setIntSp(MainActivity.this, TIME, time);
        }

        SpUtils.setIntSp(MainActivity.this, CURRENT, SpUtils.getIntSP(MainActivity.this, CURRENT) + add);
        SpUtils.setLongSp(MainActivity.this, ALREADY_WAIT_TIME, 0);
        setCurrent();
        start(true);
    }

    private void setFav() {
        String favPath = WallUtils.getPath(this, false);
        binding.fav.setSelected(favPath.contains("最爱"));
    }

    private void setCurrent() {
        int index = SpUtils.getIntSP(MainActivity.this, CURRENT);
        int realIndex = WallUtils.realIndex(this,index);
        binding.index.setText(String.valueOf(realIndex));
        binding.path.setText(WallUtils.getPathByIndex(realIndex));
        binding.selectPath.setText(TextUtils.isEmpty(WallUtils.selectPath)?DEFAULT_NAME:WallUtils.selectPath);
        binding.selectPathSize.setText(WallUtils.paths.size()+"");
        setFav();
        binding.bgLayout.setBackground(new BitmapDrawable(getResources(), WallUtils.getWallBitmap(MainActivity.this, false)));
    }

    private void start(boolean needRefresh) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("needRefresh",needRefresh);
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
                binding.bgLayout.setBackground(new BitmapDrawable(getResources(), WallUtils.getWallBitmap(MainActivity.this, false)));
                start(true);
            }
        });
        builder.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
        setCurrent();
    }

    private void setBg() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.bgLayout.setBackground(new BitmapDrawable(getResources(), WallUtils.getWallBitmap(MainActivity.this, false)));
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
                    LogUtils.i("swyLog", "Android 6.0以上，11以下，当前已有权限");
                }
            } else {
                havePermission = true;
                LogUtils.i("swyLog", "Android 6.0以下，已获取权限");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWallChangeEvent(WallChangeEvent event){
        LogUtils.e(TAG, "onWallChangeEvent");
        setCurrent();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}