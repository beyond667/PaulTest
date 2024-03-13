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
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    //移动或移出收藏文件夹
    public static void moveCurrentToFavOrNot(Context context) {
        String fromFav = getPath(context,false);
        String toFav = "";
        current = SpUtils.getIntSP(context, CURRENT);
        File fromFile  = new File(fromFav);
        if(fromFav.contains("最爱")){
            File[] dirs = context.getExternalFilesDirs(null);
            if (dirs.length >= 2) {
                String[] ss = dirs[1].getPath().split("/");
                toFav  = ss[1] + "/" + ss[2] + "/mypaper/"+fromFile.getName();
                copyFile(fromFav,toFav);
            }

        }else{
            File[] dirs = context.getExternalFilesDirs(null);
            if (dirs.length >= 2) {
                String[] ss = dirs[1].getPath().split("/");
                File file = new File(ss[1] + "/" + ss[2] + "/mypaper/0最爱");
                if(!file.exists()){
                    file.mkdirs();
                }
                toFav  = file.getAbsolutePath()+"/"+fromFile.getName();
                copyFile(fromFav,toFav);
            }
        }
        paths.remove(current);
        paths.add(current,toFav);
        fromFile.delete();

    }

    public static boolean copyFile(String oldPathName, String newPathName) {
        try {
            File oldFile = new File(oldPathName);
            if (!oldFile.exists()) {
                Log.e("copyFile", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("copyFile", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("copyFile", "copyFile:  oldFile cannot read.");
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPathName);
            FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getIndex( int index) {
        if(paths.size()>index){
            return paths.get(index);
        }

        return "";
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
        Log.e(TAG, "filename:" + filename);
        return filename;
    }

    public static final String TIME = "time";
    public static final String CURRENT = "current";
    public static final String START_TIME = "start_time";
    public static final String ALREADY_WAIT_TIME = "already_wait_time";

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

    public static void delete(Context context) {
        String path = getPath(context, false);
        File file = new File(path);
        Log.e(TAG,"已删除"+file.getAbsolutePath());
        boolean delete = file.delete();
        if(delete){
            paths.remove(path);
        }
        Log.e(TAG,"已删除成功："+delete);
    }

}
