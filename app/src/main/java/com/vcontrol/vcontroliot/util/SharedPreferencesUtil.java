package com.vcontrol.vcontroliot.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Administrator on 2015/11/11.
 */
public class SharedPreferencesUtil {
    private static String CONFIG = "config";
    /**
     * ISFIRST 是第一次进入
     */
    public static String STATUS = "status";
    public static String USERNAME = "username";
    private static SharedPreferences sharedPreferences;

    private static SharedPreferences getSPInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences;
    }

    /**
     * 保存
     * Created by beifeitu on 16/12/21.
     */
    public static void save(Context context, String key, Object value) {
        getSPInstance(context);

        if (value instanceof String) {
            sharedPreferences.edit().putString(key, (String) value).commit();
        } else if (value instanceof Integer) {
            sharedPreferences.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Boolean) {
            sharedPreferences.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Float) {
            sharedPreferences.edit().putFloat(key, (Float) value).commit();
        } else if (value instanceof Long) {
            sharedPreferences.edit().putLong(key, (Long) value).commit();
        }
    }

    //存
    public static void saveStringData(Context context, String key, String value) {
        getSPInstance(context);

        sharedPreferences.edit().putString(key, value).commit();
    }

    //取
    public static String getStringData(Context context, String key, String defValue) {
        getSPInstance(context);
        return sharedPreferences.getString(key, defValue);
    }

    public static Float getFloatData(Context context, String key, Float defValue) {
        getSPInstance(context);
        return sharedPreferences.getFloat(key, defValue);
    }


    //存布尔
    public static void saveBooleanData(Context context, String key, Boolean value) {
        getSPInstance(context);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    //取布尔
    public static Boolean getBooleanData(Context context, String key, Boolean defValue) {
        getSPInstance(context);
        return sharedPreferences.getBoolean(key, defValue);
    }


    //存int类型
    public static void saveIntData(Context context, String key, int value) {
        getSPInstance(context);
        sharedPreferences.edit().putInt(key, value).commit();

    }

    //取
    public static int getIntData(Context context, String key, int defValue) {
        getSPInstance(context);
        return sharedPreferences.getInt(key, defValue);
    }

    //取long值
    public static long getLongData(Context context, String key, long defValue) {
        getSPInstance(context);
        return sharedPreferences.getLong(key, defValue);
    }


    /**
     * 获取token
     *
     * @return
     */
    public static String getToken(String defaul) {
        String token = sharedPreferences.getString("token", defaul);
        return token;
    }

    /**
     * 保存json 信息
     *
     * @return
     */
    public static <T> void saveJson(Context context, T provincial) {
        try {
            String provincialStr = new Gson().toJson(provincial);
            save(context, provincial.getClass().getName(), provincialStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 json信息
     *
     * @return
     */
    public static <T> T getFromJson(Context context, Class<T> clazz) {
        try {
            String json = getStringData(context, clazz.getName(), "");
            return new Gson().fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
