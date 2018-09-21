package com.vcontrol.vcontroliot.util;

import android.os.Environment;

public class CommConfig
{
	public static final int METHOD_SUCCESS = 1;

	public static boolean LOG_LOGCAT = true;// 日志输出到控制台
	public static boolean LOG_DEBUG = true;// 记录调试日志
	public static boolean LOG_OUTFILE = true;// 日志输出到文件
	public static int LOG_MAXSIZE = 100;// 日志最大容量，单位：m(1024 * 1024)
	public static String LOG_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vcontrol/";// 日志输出路径
	public static String LOG_NAME = "vcontrol.log";// 日志名称
	public static String LOG_SYSTEM_NAME = "system.log";// 系统日志名称
}
