package com.vcontrol.vcontroliot.act;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.adapter.LeDeviceListAdapter;
import com.vcontrol.vcontroliot.util.BleUtils;
import com.vcontrol.vcontroliot.util.SystemBarTintManager;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleConnCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleReadCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;

public class BleDeviceListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static String TAG = BleDeviceListActivity.class.getName();


    protected SystemBarTintManager tintManager;

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
        BleUtils.getInstance();
        mBle = BleUtils.getBle();
        //3、检查蓝牙是否支持及打开
        checkBluetoothStatus();
    }


    private void showLoading() {
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
        if (mBle == null){
            mBle = BleUtils.getBle();
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
                hideLoading();
                showEmptyView();
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e(TAG, "onStop: ");
            hideLoading();
            showEmptyView();
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
                reScan();
                break;
            case R.id.send_button:
                synchronized (mBle.getLocker()) {
                    for (BleDevice device : list) {
                        sendData(device, new byte[]{1, 2, 3});
                    }
                }
                break;
            case R.id.read_button:
                synchronized (mBle.getLocker()) {
                    for (BleDevice device : list) {
                        read(device);
                    }
                }
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


    /*发送数据*/
    public void sendData(BleDevice device, byte[] data) {
        if (data == null) {
            ToastUtil.showLong(getApplicationContext(), "发送数据为空");
            return;
        }
        boolean result = mBle.write(device, data,
                new BleWriteCallback<BleDevice>() {
                    @Override
                    public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                        ToastUtil.showLong(getApplicationContext(), "发送数据成功");
                    }
                });
        if (!result) {
            ToastUtil.showLong(getApplicationContext(), "发送数据失败!");
        }
    }

    /*主动读取数据*/
    public void read(BleDevice device) {
        boolean result = mBle.read(device, new BleReadCallback<BleDevice>() {
            @Override
            public void onReadSuccess(BluetoothGattCharacteristic characteristic) {
                super.onReadSuccess(characteristic);
                byte[] data = characteristic.getValue();
                ToastUtil.showLong(getApplicationContext(), "onReadSuccess: " + Arrays.toString(data));
            }
        });
        if (!result) {
            Log.d(TAG, "读取数据失败!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBle != null) {
            mBle.destory(getApplicationContext());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra(UiEventEntry.NOTIFY_BASIC_NAME, getString(R.string.lru_3300));
        intent.putExtra(UiEventEntry.NOTIFY_BASIC_TYPE, UiEventEntry.LRU_BLE_3300);
        startActivity(intent);

//        final BleDevice device = mLeDeviceListAdapter.getDevice(i);
//        if (device == null) return;
//        if (mBle.isScanning()) {
//            mBle.stopScan();
//        }
//        if (device.isConnected()) {
//            mBle.disconnect(device);
//        } else if (!device.isConnectting()) {
//            //扫描到设备时   务必用该方式连接(是上层逻辑问题， 否则点击列表  虽然能够连接上，但设备列表的状态不会发生改变)
//            mBle.connect(device, connectCallback);
//            //此方式只是针对不进行扫描连接（如上，若通过该方式进行扫描列表的连接  列表状态不会发生改变）
////            mBle.connect(device.getBleAddress(), connectCallback);
//        }
    }


    /*设置通知的回调*/
    private BleNotiftCallback<BleDevice> bleNotiftCallback = new BleNotiftCallback<BleDevice>() {
        @Override
        public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            Log.e(TAG, "onChanged==uuid:" + uuid.toString());
            Log.e(TAG, "onChanged==address:" + device.getBleAddress());
            Log.e(TAG, "onChanged==data:" + Arrays.toString(characteristic.getValue()));
        }
    };

    /*连接的回调*/
    private BleConnCallback<BleDevice> connectCallback = new BleConnCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(final BleDevice device) {
            if (device.isConnected()) {
                /*连接成功后，设置通知*/
                mBle.startNotify(device, bleNotiftCallback);
            }
            Log.e(TAG, "onConnectionChanged: " + device.isConnected());
            mLeDeviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            ToastUtil.showLong(getApplicationContext(), "连接异常，异常状态码:" + errorCode);
        }
    };
}
