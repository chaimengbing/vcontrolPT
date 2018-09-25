package com.vcontrol.vcontroliot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.vcontrol.vcontroliot.jpushdemo.Logger;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.model.DaoMaster;
import com.vcontrol.vcontroliot.model.SQLiteOpenHelper;
import com.vcontrol.vcontroliot.util.CommConfig;
import com.vcontrol.vcontroliot.util.LocaleUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vcontrol on 2016/11/11.
 */

public class VcontrolApplication extends Application {
    private static final String TAG = VcontrolApplication.class.getSimpleName();
    public static volatile Handler applicationHandler = null;
    private SQLiteOpenHelper helper;
    private DaoMaster master;
    public static String registrationId;


    private static Context context = null;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        VcontrolApplication.token = token;
    }

    private static String token = "";





    /**
     * Activity列表
     */
    private List<Activity> activityList = new LinkedList<Activity>();

    /**
     * 全局唯一实例
     */
    private static VcontrolApplication instance;

    /**
     * 获取类实例对象
     *
     * @return VcontrolApplication
     */
    public static VcontrolApplication getInstance()
    {
        if (null == instance)
        {
            instance = new VcontrolApplication();
        }
        return instance;
    }

    public static Context getCurrentContext() {
        return context;
    }

    public static void setCurrentContext(Context context) {
        VcontrolApplication.context = context;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.startLogService(CommConfig.LOG_FILEPATH, CommConfig.LOG_NAME, CommConfig.LOG_MAXSIZE);
        Log.info(TAG, "init VcontrolApplication");
        Logger.d(TAG, "[ExampleApplication] onCreate");
        Locale _UserLocale= LocaleUtils.getUserLocale(this);
        LocaleUtils.updateLocale(this, _UserLocale);
        context = getApplicationContext();

        applicationHandler = new Handler(this.getMainLooper());
        instance = this;


        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        registrationId = JPushInterface.getRegistrationID(this);
        Log.info("1099", "run:--------->registrationId： " + registrationId);

        updateDB();
        //数据库调试。建议打包上线时关闭
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale _UserLocale=LocaleUtils.getUserLocale(this);
        //系统语言改变了应用保持之前设置的语言
        if (_UserLocale != null) {
            Locale.setDefault(_UserLocale);
            Configuration _Configuration = new Configuration(newConfig);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                _Configuration.setLocale(_UserLocale);
            } else {
                _Configuration.locale =_UserLocale;
            }
            getResources().updateConfiguration(_Configuration, getResources().getDisplayMetrics());
        }
    }


    /**
     * 保存Activity到现有列表中
     *
     * @param activity
     */
    public void addActivity(Activity activity)
    {
        activityList.add(activity);
    }

    /**
     * 关闭保存的Activity
     */
    public void exit()
    {
        if (activityList != null)
        {
            Log.debug(TAG, "exit::activityList:" + activityList.size());
            Activity activity;
            for (int i = 0; i < activityList.size(); i++)
            {
                activity = activityList.get(i);

                if (activity != null)
                {
                    if (!activity.isFinishing())
                    {
                        activity.finish();
                    }
                    activity = null;
                }
            }

            activityList.clear();
        }
    }
    public static String getRegistrationId() {
        return registrationId;
    }

    public static void setRegistrationId(String registrationId) {
        VcontrolApplication.registrationId = registrationId;

    }



    private void updateDB() {
        //是否开启调试
        MigrationHelper.DEBUG = true;
        helper = new SQLiteOpenHelper(this
                , "recluse-db");
        master = new DaoMaster(helper.getWritableDb());
    }
}
