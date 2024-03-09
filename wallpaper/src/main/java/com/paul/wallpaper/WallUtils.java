package com.paul.wallpaper;

import static com.paul.wallpaper.MyService.mHeight;
import static com.paul.wallpaper.MyService.mWidth;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WallUtils {


    private static String TAG = "WallUtils";

    public static Bitmap centerCrop(Bitmap srcBitmap, int desWidth, int desHeight) {
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        int newWidth = srcWidth;
        int newHeight = srcHeight;
        float srcRate = (float) srcWidth / srcHeight;
        float desRate = (float) desWidth / desHeight;
        int dx = 0, dy = 0;
        if (srcRate == desRate) {
            return srcBitmap;
        } else if (srcRate > desRate) {
            newWidth = (int) (srcHeight * desRate);
            dx = (srcWidth - newWidth) / 2;
        } else {
            newHeight = (int) (srcWidth / desRate);
            dy = (srcHeight - newHeight) / 2;
        }
        //创建目标Bitmap，并用选取的区域来绘制
        Bitmap desBitmap = Bitmap.createBitmap(srcBitmap, dx, dy, newWidth, newHeight);
        return desBitmap;
    }

    public static List<String> paths = new ArrayList<>();
    public static int current = 0;

    public static void  getAllFile(File file){
        File[] list = file.listFiles();
        if (list == null) return ;
        for (File f : list) {
            if (f.isDirectory()) {
                getAllFile(f);
            } else {
                paths.add(f.getAbsolutePath());
            }
        }
    }

    public static String getPath(Context context, boolean add) {
        String filename = "";
        try {
            if (paths.size() == 0) {
                File[] dirs = context.getExternalFilesDirs(null);
                if (dirs.length >= 2) {
                    String[] ss = dirs[1].getPath().split("/");
                    File file = new File(ss[1] + "/" + ss[2] + "/mypaper");
                    File[] files = file.listFiles();
                    for (File file1 : files) {
                        if(file1.isDirectory()){
                            getAllFile(file1);
                        }else{
                            paths.add(file1.getAbsolutePath());
                        }

                    }
                } else {
                    return "";
                }

            }
            current = SpUtils.getIntSP(context, CURRENT);
            if (add) {
                current++;
            }
            if (current >= paths.size() || current < 0) {
                current = 0;
                SpUtils.setIntSp(context, CURRENT, current);
                paths.clear();
                return getPath(context, false);
            }
            SpUtils.setIntSp(context, CURRENT, current);

            filename = paths.get(current);
        } catch (Exception e) {
            e.printStackTrace();
            paths.clear();
            current = 0;
            SpUtils.setIntSp(context, CURRENT, current);
            return getPath(context,add);

        }
        Log.e("==", "========111======filename==" + filename);
        return filename;
    }

    public static final String TIME = "time";
    public static final String CURRENT = "current";

    public static Bitmap getWallBitmap(Context context, boolean autoAdd) {
        String path = getPath(context, autoAdd);
        Bitmap mBitmap = BitmapFactory.decodeFile(path); //path为绝对路径
        return centerCrop(mBitmap, mWidth, mHeight);
    }

    public static void setPaper(Context context, boolean autoAdd) {
        try {
            Bitmap mBitmap = getWallBitmap(context, autoAdd);
            WallpaperManager wpm = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
            if (wpm != null) {
                //第一个参数是Bitmap对象，第二个参数是截取图片的大小矩形，第三个参数是是否备份
                if (mBitmap != null) {
                    wpm.suggestDesiredDimensions(mWidth, mHeight);
                    wpm.setBitmap(mBitmap, new Rect(0, 0, mWidth, mHeight), true);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to set wallpaper: " + e);
        }
    }

}
