package com.vcontrol.vcontroliot.util;

import android.content.Context;

import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.view.CustomProgressDialog;

/**
 * 说明：等待框
 * 
 */
public class ProgressBarUtil
{
	private static final String TAG = ProgressBarUtil.class.getSimpleName();
	private static CustomProgressDialog progressDialog;

	public static void showProgressDialog(Context context, String title, String message)
	{
		Log.debug(TAG, "showProgressDialog");
		dismissProgressDialog();
		progressDialog = new CustomProgressDialog(context, message);
		progressDialog.show();
	}

	public static void showProgressDialog(Context context, String title, int textId)
	{
		String message = context.getString(textId);
		showProgressDialog(context, title, message);
	}

	public static void dismissProgressDialog()
	{
		Log.debug(TAG, "dismissProgressDialog");
		if (progressDialog != null)
		{
			// progressDialog 关联的activity 关闭后，progressDialog并没有付空
			try
			{
				progressDialog.cancel();
			}
			catch (Exception e)
			{
			}
			finally
			{
				progressDialog = null;
			}
		}
	}
}
