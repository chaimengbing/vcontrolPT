package com.vcontrol.vcontroliot.act;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.adapter.LeDeviceListAdapter;
import com.vcontrol.vcontroliot.util.SystemBarTintManager;
import com.vcontrol.vcontroliot.util.ToastUtil;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;

public class BleDeviceListActivity extends BaseActivity {
    private static String TAG = BleDeviceListActivity.class.getName();


    protected SystemBarTintManager tintManager;

    @BindView(R.id.device_listview)
    ListView mListView;
    @BindView(R.id.no_device)
    TextView noDevice;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private Ble<BleDevice> mBle;


    @Override
    public int getLayoutView() {
        return R.layout.activity_bledevice_list;
    }

    @Override
    public void initViewData() {
        showToolbar();
        setTitleRightVisible(View.VISIBLE);
        setTitleMain("");
        setTitleName("选择设备");
        setTitleRight("扫描");
        setPrimary();


        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        mListView.setAdapter(mLeDeviceListAdapter);
        showEmptyView();
        //1、请求蓝牙相关权限
        requestPermission();
    }

    @Override
    public void initComponentViews() {
        ButterKnife.bind(this);
    }


    private void showEmptyView() {
        if (mLeDeviceListAdapter == null || mLeDeviceListAdapter.getCount() == 0) {
            noDevice.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            noDevice.setVisibility(View.GONE);

        }
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


    //初始化蓝牙
    private void initBle() {
        mBle = Ble.getInstance();
        Ble.Options options = new Ble.Options();
        options.logBleExceptions = true;//设置是否输出打印蓝牙日志
        options.throwBleException = true;//设置是否抛出蓝牙异常
        options.autoConnect = false;//设置是否自动连接
        options.scanPeriod = 12 * 1000;//设置扫描时长
        options.connectTimeout = 10 * 1000;//设置连接超时时长
        options.uuid_service = UUID.fromString("0000fee9-0000-1000-8000-00805f9b34fb");//设置主服务的uuid
        //options.uuid_services_extra = new UUID[]{UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")};//添加额外的服务（如电量服务，心跳服务等）
        options.uuid_write_cha = UUID.fromString("d44bc439-abfd-45a2-b575-925416129600");//设置可写特征的uuid
        //options.uuid_read_cha = UUID.fromString("d44bc439-abfd-45a2-b575-925416129601");//设置可读特征的uuid
        //ota相关 修改为你们自己的
       /* options.uuid_ota_service = UUID.fromString("0000fee8-0000-1000-8000-00805f9b34fb");
        options.uuid_ota_notify_cha = UUID.fromString("003784cf-f7e3-55b4-6c4c-9fd140100a16");
        options.uuid_ota_write_cha = UUID.fromString("013784cf-f7e3-55b4-6c4c-9fd140100a16");*/
        mBle.init(getApplicationContext(), options);
        //3、检查蓝牙是否支持及打开
        checkBluetoothStatus();
    }


    //检查蓝牙是否支持及打开
    private void checkBluetoothStatus() {
        // 检查设备是否支持BLE4.0
        if (!mBle.isSupportBle(this)) {
            ToastUtil.showShort(getApplicationContext(), R.string.ble_not_supported);
            finish();
        }
        if (!mBle.isBleEnable()) {
            //4、若未打开，则请求打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Ble.REQUEST_ENABLE_BT);
        } else {
            //5、若已打开，则进行扫描
            mBle.startScan(scanCallback);
        }
    }


    //扫描的回调
    BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(final BleDevice device, int rssi, byte[] scanRecord) {
            synchronized (mBle.getLocker()) {
                mLeDeviceListAdapter.addDevice(device);
                mLeDeviceListAdapter.notifyDataSetChanged();
                showEmptyView();
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e(TAG, "onStop: ");
        }
    };

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
                reScan();
                break;
            default:
                break;
        }
    }


    //重新扫描
    private void reScan() {
        if (mBle != null && !mBle.isScanning()) {
            mLeDeviceListAdapter.clear();
            mLeDeviceListAdapter.addDevices(mBle.getConnetedDevices());
            mBle.startScan(scanCallback);
        }
    }

    //请求权限
    private void requestPermission() {
        requestPermission(new String[]{Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                "请求蓝牙相关权限", new GrantedResult() {
                    @Override
                    public void onResult(boolean granted) {
                        if (granted) {
                            //2、初始化蓝牙
                            initBle();
                        } else {
                            finish();
                        }
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBle != null) {
            mBle.destory(getApplicationContext());
        }
    }
}
