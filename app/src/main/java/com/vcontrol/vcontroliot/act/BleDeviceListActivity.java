package com.vcontrol.vcontroliot.act;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.util.SystemBarTintManager;
import com.vcontrol.vcontroliot.util.ToastUtil;

import butterknife.ButterKnife;

public class BleDeviceListActivity extends BaseActivity {


    protected SystemBarTintManager tintManager;

    @Override
    public int getLayoutView() {
        return R.layout.activity_bledevice_list;
    }

    @Override
    public void initViewData() {

    }

    @Override
    public void initComponentViews() {
        ButterKnife.bind(this);
        showToolbar();
        setTitleName("选择设备");
        setTitleRight("扫描");
        setPrimary();

    }

    /**
     * 设置主题颜色
     */
    public void setPrimary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);

        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.biaoti);
    }

    /**
     * 控制主题显示
     */
    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = this.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_main:
                finish();
                break;
            case R.id.title_right:
                ToastUtil.showShort(getApplicationContext(), "扫描");
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
