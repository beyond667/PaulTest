package com.paul.wallpaper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {
    public static void setIntSp(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("SharePreferencesFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("SharePreferencesFile", MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static long getLongSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("SharePreferencesFile", MODE_PRIVATE);
        return sp.getLong(key, 0L);
    }

    public static void setLongSp(Context context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences("SharePreferencesFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }
}
