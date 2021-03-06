package com.vcontrol.vcontroliot.act;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.util.FgManager;


/**
 * 说明：父类的Activity
 */
public abstract class BaseActivity extends AppCompatActivity implements OnClickListener, Toolbar.OnMenuItemClickListener {
    private static final String TAG = BaseActivity.class.getName();
    /*Toolbar*/
    private Toolbar toolBar;
    /*一级标题*/
    private TextView titleMain;


    /*二级标题*/
    private TextView titleName;
    /*右边操作*/
    public TextView titleRight;

    public TextView getTitleRight() {
        return titleRight;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(getLayoutView());
            initComponentViews();
            initToolbar();
            initViewData();

        } catch (Exception e) {
            Log.exception(TAG, e);
        }
    }

    protected <T extends View> T getView(int resourcesId) {
        return (T) findViewById(resourcesId);
    }

    /*初始化toolbar*/
    private void initToolbar() {
        toolBar = getView(R.id.toolbar);
        toolBar.setTitle("");
        toolBar.setTitleTextColor(Color.WHITE);
        titleMain = getView(R.id.title_main);
        titleName = getView(R.id.title_name);
        titleRight = getView(R.id.title_right);

    }

    public void hideToolbar() {
        if (toolBar != null) {
            toolBar.setVisibility(View.GONE);
        }
    }


    public Toolbar getToolbar() {
        if (toolBar != null) {
            return toolBar;
        }
        return null;
    }

    public void showToolbar() {
        if (toolBar != null) {
            toolBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置title
     *
     * @param title ：title
     */
    public void setTitleMain(String title) {
        if (titleMain != null) {
            titleMain.setText(title);
            titleMain.setOnClickListener(this);
        }
    }

    public void setMainVisible(int visibile) {
        if (titleMain != null) {
            titleMain.setVisibility(visibile);
        }
    }

    public void setTitleName(String title) {
        if (titleName != null) {
            titleName.setText(title);
        }
    }

    public void setTitleRight(String title) {
        if (titleRight != null) {
            titleRight.setText(title);
            titleRight.setOnClickListener(this);
        }
    }

    /**
     * 设置右标题的显隐
     *
     * @param visibile
     */
    public void setTitleRightVisible(int visibile) {
        if (titleRight != null) {
            titleRight.setVisibility(visibile);
        }
    }

    public void setTitleftVisible(int visibile) {
        if (titleMain != null) {
            titleMain.setVisibility(visibile);
        }
    }

    /**
     * title右侧:图标类
     */
    protected void setRightRes() {
        //扩展menu
        toolBar.inflateMenu(R.menu.base_menu_main);
        //添加监听
        toolBar.setOnMenuItemClickListener(this);
    }


    public static abstract class GrantedResult implements Runnable {
        private boolean mGranted;

        public abstract void onResult(boolean granted);

        @Override
        public void run() {
            onResult(mGranted);
        }
    }


    /*---------------------------------------------------------------------------以下是android6.0动态授权的封装十分好用---------------------------------------------------------------------------*/
    private int mPermissionIdx = 0x10;//请求权限索引
    private SparseArray<GrantedResult> mPermissions = new SparseArray<>();//请求权限运行列表

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        GrantedResult runnable = mPermissions.get(requestCode);
        if (runnable == null) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            runnable.mGranted = true;
        }
        runOnUiThread(runnable);
    }

    public void requestPermission(String[] permissions, String reason, GrantedResult runnable) {
        if (runnable == null) {
            return;
        }
        runnable.mGranted = false;
        if (Build.VERSION.SDK_INT < 23 || permissions == null || permissions.length == 0) {
            runnable.mGranted = true;//新添加
            runOnUiThread(runnable);
            return;
        }
        final int requestCode = mPermissionIdx++;
        mPermissions.put(requestCode, runnable);

		/*
			是否需要请求权限
		 */
        boolean granted = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                granted = granted && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (granted) {
            runnable.mGranted = true;
            runOnUiThread(runnable);
            return;
        }

		/*
			是否需要请求弹出窗
		 */
        boolean request = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request = request && !shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!request) {
            final String[] permissionTemp = permissions;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(reason)
                    .setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(permissionTemp, requestCode);
                            }
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            GrantedResult runnable = mPermissions.get(requestCode);
                            if (runnable == null) {
                                return;
                            }
                            runnable.mGranted = false;
                            runOnUiThread(runnable);
                        }
                    }).create();
            dialog.show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, requestCode);
            }
        }
    }

    /**
     * 方法说明 : 初始化页面的布局
     *
     * @return void
     */
    public abstract int getLayoutView();

    /**
     * 方法说明 : 初始化页面显示数据
     *
     * @return void
     */
    public abstract void initViewData();

    /**
     * 方法说明 : 初始化页面控件的布局
     *
     * @return void
     */
    public abstract void initComponentViews();

    @Override
    protected void onResume() {
        super.onResume();
    }

//    @Override
//    public void onBackPressed()
//    {
//        return;
//    }


    public boolean turnToFragmentStack(int containerViewId, Class<? extends Fragment> toFragmentClass, Bundle args) {
        return FgManager.turnToFragmentStack(getFragmentManager(), containerViewId, toFragmentClass, args);
    }

    public boolean turnToFragmentStack(int containerViewId, Class<? extends Fragment> toFragmentClass) {
        return FgManager.turnToFragmentStack(getFragmentManager(), containerViewId, toFragmentClass, null);
    }

    public void clearFragmentStack(int containerViewId) {
        FgManager.clearFragmentStack(getFragmentManager(), containerViewId);
        FrameLayout vg = (FrameLayout) findViewById(containerViewId);
        if (vg != null) {
            vg.removeAllViews();
        }
    }

    public void clearFragmentStack(int[] containerViewIds) {
        clearFragmentStack(containerViewIds, null);
    }

    public void clearFragmentStack(int[] containerViewIds, FgManager.Callback callback) {
        FgManager.clearFragmentStack(getFragmentManager(), containerViewIds, callback);
        FrameLayout vg = null;
        for (int containerViewId : containerViewIds) {
            vg = (FrameLayout) findViewById(containerViewId);
            if (vg != null) {
                vg.removeAllViews();
            }
        }
    }

    public void backToPreFragment(int containerViewId) {
        backToPreFragment(containerViewId, null, 1);
    }

    public void backToPreFragment(int containerViewId, int backCount) {
        backToPreFragment(containerViewId, null, backCount);
    }

    private void backToPreFragment(int containerViewId, Bundle args, int backCount) {
        try {
            FgManager.backToPreFragment(getFragmentManager(), containerViewId, args, backCount);
        } catch (Exception e) {
            Log.exception(TAG, e);
        }
    }

    public void removeFragmentFromBackStack(int containerViewId, Class<? extends Fragment>... removeFragmentClass) {
        try {
            FgManager.removeFragmentFromBackStack(getFragmentManager(), containerViewId, removeFragmentClass);
        } catch (Exception e) {
            Log.exception(TAG, e);
        }
    }

}
