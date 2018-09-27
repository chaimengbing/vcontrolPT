package com.vcontrol.vcontroliot.act;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.adapter.LeDeviceListAdapter;
import com.vcontrol.vcontroliot.util.BleUtils;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.SystemBarTintManager;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;
import com.vcontrol.vcontroliot.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;

public class BleDeviceListActivity extends BaseActivity implements AdapterView.OnItemClickListener, EventNotifyHelper.NotificationCenterDelegate {
    private static String TAG = BleDeviceListActivity.class.getName();


    protected SystemBarTintManager tintManager;
    private long exitTime = 0;

    @BindView(R.id.device_listview)
    ListView mListView;
    @BindView(R.id.no_device)
    TextView noDevice;
    @BindView(R.id.loadingview)
    LinearLayout loadingView;
    @BindView(R.id.send_button)
    Button sendButton;
    @BindView(R.id.read_button)
    Button readButton;


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
        setMainVisible(View.GONE);
        setTitleName("选择设备");
        setTitleRight("扫描");
        setPrimary();


        showLoading();
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        mListView.setAdapter(mLeDeviceListAdapter);
        mListView.setOnItemClickListener(this);
        sendButton.setOnClickListener(this);
        readButton.setOnClickListener(this);
        //1、请求蓝牙相关权限
        requestPermission();
    }

    @Override
    public void initComponentViews() {
        ButterKnife.bind(this);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.NOTIFY_BLE_CONNECT_SUCCESS);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.NOTIFY_BLE_CONNECT_FAIL);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.NOTIFY_BLE_CONNECT_STOP);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.NOTIFY_BLE_SCAN_SUCCESS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.NOTIFY_BLE_CONNECT_SUCCESS);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.NOTIFY_BLE_CONNECT_FAIL);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.NOTIFY_BLE_CONNECT_STOP);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.NOTIFY_BLE_SCAN_SUCCESS);
        if (mBle != null) {
            mBle.destory(getApplicationContext());
        }
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
        //3、检查蓝牙是否支持及打开
        checkBluetoothStatus();
    }


    private void showLoading() {
        noDevice.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }


    //检查蓝牙是否支持及打开
    private void checkBluetoothStatus() {
        // 检查设备是否支持BLE4.0
//        if (!mBle.isSupportBle(this)) {
//            ToastUtil.showShort(getApplicationContext(), R.string.ble_not_supported);
//            finish();
//        }
        if (!BleUtils.getInstance().isBleEnable()) {
            //4、若未打开，则请求打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Ble.REQUEST_ENABLE_BT);
        } else {
            //5、若已打开，则进行扫描
            BleUtils.getInstance().startScan();
        }
    }


    //扫描的回调
    BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(final BleDevice device, int rssi, byte[] scanRecord) {
            synchronized (mBle.getLocker()) {

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
        List<BleDevice> list = Ble.getInstance().getConnetedDevices();
        switch (view.getId()) {
            case R.id.title_main:
                finish();
                break;
            case R.id.title_right:
                showLoading();
                BleUtils.getInstance().startScan();
                break;
//            case R.id.send_button:
//                synchronized (mBle.getLocker()) {
//                    for (BleDevice device : list) {
//                        sendData(device, new byte[]{1, 2, 3});
//                    }
//                }
//                break;
//            case R.id.read_button:
//                synchronized (mBle.getLocker()) {
//                    for (BleDevice device : list) {
//                        read(device);
//                    }
//                }
//                break;
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
        } else {
            checkBluetoothStatus();
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


//    /*发送数据*/
//    public void sendData(BleDevice device, byte[] data) {
//        if (data == null) {
//            ToastUtil.showLong(getApplicationContext(), "发送数据为空");
//            return;
//        }
//        boolean result = mBle.write(device, data,
//                new BleWriteCallback<BleDevice>() {
//                    @Override
//                    public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
//                        ToastUtil.showLong(getApplicationContext(), "发送数据成功");
//                    }
//                });
//        if (!result) {
//            ToastUtil.showLong(getApplicationContext(), "发送数据失败!");
//        }
//    }
//
//    /*主动读取数据*/
//    public void read(BleDevice device) {
//        boolean result = mBle.read(device, new BleReadCallback<BleDevice>() {
//            @Override
//            public void onReadSuccess(BluetoothGattCharacteristic characteristic) {
//                super.onReadSuccess(characteristic);
//                byte[] data = characteristic.getValue();
//                ToastUtil.showLong(getApplicationContext(), "onReadSuccess: " + Arrays.toString(data));
//            }
//        });
//        if (!result) {
//            Log.d(TAG, "读取数据失败!");
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BleDevice device = mLeDeviceListAdapter.getDevice(i);
        if (device == null) return;
        if (BleUtils.getInstance().isScanning()) {
            BleUtils.getInstance().stopScan();
        }
        BleUtils.getInstance().connectDevice(device);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 按下返回键退出APP
     */
    private void exitApp() {
        // 判断2次点击事件时间
        Log.e(TAG, "exitApp::time:" + exitTime);
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showLong(getApplicationContext(), getString(R.string.Press_again_to_exit_the_program));
            exitTime = System.currentTimeMillis();
        } else {
            VcontrolApplication.getInstance().exit();

            finish();
            System.exit(0);
        }
    }


    private void toBleView(BleDevice bleDevice) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra(UiEventEntry.NOTIFY_BASIC_NAME, getString(R.string.lru_3300));
        intent.putExtra(UiEventEntry.NOTIFY_BASIC_TYPE, UiEventEntry.LRU_BLE_3300);
        intent.putExtra("device", bleDevice);
        startActivity(intent);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == UiEventEntry.NOTIFY_BLE_CONNECT_SUCCESS) {
            BleDevice bleDevice = (BleDevice) args[0];
            if (bleDevice != null) {
                toBleView(bleDevice);
            }
        } else if (id == UiEventEntry.NOTIFY_BLE_SCAN_SUCCESS) {
            BleDevice bleDevice = (BleDevice) args[0];
            if (bleDevice != null) {
                mLeDeviceListAdapter.addDevice(bleDevice);
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
            hideLoading();
            showEmptyView();

        } else if (id == UiEventEntry.NOTIFY_BLE_CONNECT_FAIL) {

        } else if (id == UiEventEntry.NOTIFY_BLE_CONNECT_STOP) {
            hideLoading();
            showEmptyView();
        }
    }
}
