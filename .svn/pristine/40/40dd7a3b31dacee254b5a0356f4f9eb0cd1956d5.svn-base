package com.vcontrol.vcontroliot.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.vcontrol.vcontroliot.log.Log;


public class DensityUtil
{
    private static String TAG = DensityUtil.class.getSimpleName();

    /**
     * 方法说明 :将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dpValue
     * @return
     * @author chaimb
     * @Date 2015-6-25
     */
    public static int dip2px(Context context, float dpValue)
    {
        try
        {
            if (context == null)
            {
                return (int) dpValue;
            }
            else
            {
                final float scale = context.getResources().getDisplayMetrics().density;
                return (int) (dpValue * scale + 0.5f);
            }
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
        return (int) dpValue;
    }

    /**
     * 方法说明 :将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param context
     * @param pxValue
     * @return
     * @author chaimb
     * @Date 2015-6-25
     */
    public static int px2dip(Context context, float pxValue)
    {
        try
        {
            if (context == null)
            {
                return (int) pxValue;
            }
            else
            {
                final float scale = context.getResources().getDisplayMetrics().density;
                return (int) (pxValue / scale + 0.5f);
            }
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
        return (int) pxValue;
    }

    /**
     * 方法说明 :返回屏幕宽度
     *
     * @param context
     * @return
     * @author chaimb
     * @Date 2015-6-25
     */
    public static int getDisplayWidthPixels(Activity context)
    {
        try
        {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int widthPixels = dm.widthPixels;
            return widthPixels;
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
        return 0;
    }

    /**
     * 方法说明 :返回屏幕高度
     *
     * @param context
     * @return
     * @author chaimb
     * @Date 2015-6-25
     */
    public static int getDisplayHeightPixels(Activity context)
    {
        try
        {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int widthPixels = dm.heightPixels;
            return widthPixels;
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
        return 0;
    }

}