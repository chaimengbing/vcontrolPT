package com.vcontrol.vcontroliot.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;
import android.util.Log;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;

import java.util.Arrays;
import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleReadCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;

public class BleUtils {

    private static String TAG = "BleUtils";


    private static BleUtils bleUtils = null;

    private static Ble<BleDevice> mBle = null;


    private BleUtils() {

    }


    public static BleUtils getInstance() {
        if (bleUtils == null) {
            bleUtils = new BleUtils();
            getBle();
        }
        return bleUtils;
    }


    public static Ble<BleDevice> getBle() {
        if (mBle == null) {
            mBle = Ble.getInstance();
            Ble.Options options = new Ble.Options();
            options.logBleExceptions = true;//设置是否输出打印蓝牙日志
            options.throwBleException = true;//设置是否抛出蓝牙异常
            options.autoConnect = false;//设置是否自动连接
            options.scanPeriod = 12 * 1000;//设置扫描时长
            options.connectTimeout = 10 * 1000;//设置连接超时时长
            options.uuid_service = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");//设置主服务的uuid
            //options.uuid_services_extra = new UUID[]{UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")};//添加额外的服务（如电量服务，心跳服务等）
            options.uuid_write_cha = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");//设置可写特征的uuid
            options.uuid_read_cha = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");//设置可读特征的uuid
            //ota相关 修改为你们自己的
        /*  options.uuid_ota_service = UUID.fromString("0000fee8-0000-1000-8000-00805f9b34fb");
            options.uuid_ota_notify_cha = UUID.fromString("003784cf-f7e3-55b4-6c4c-9fd140100a16");
            options.uuid_ota_write_cha = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");*/
            mBle.init(VcontrolApplication.getCurrentContext(), options);
        }
        return mBle;
    }


    /**
     * 发送数据
     */
    public void sendData(final BleDevice device, final byte[] data) {
        if (data == null) {
            ToastUtil.showLong(VcontrolApplication.getCurrentContext(), "发送数据为空");
            return;
        }
        if (device == null) {
            ToastUtil.showLong(VcontrolApplication.getCurrentContext(), "请选择设备");
            return;
        }
        boolean result = false;
        if (mBle != null) {
            result = mBle.write(device, data,
                    new BleWriteCallback<BleDevice>() {
                        @Override
                        public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                            ToastUtil.showLong(VcontrolApplication.getCurrentContext(), "发送数据成功");
                            readData(device, data);
                        }
                    });
        }
        if (!result) {
            ToastUtil.showLong(VcontrolApplication.getCurrentContext(), "发送数据失败!");
        }
    }

    /**
     * 主动读取数据
     */
    public void readData(BleDevice device, final byte[] sendData) {
        boolean result = false;
        if (mBle != null) {
            result = mBle.read(device, new BleReadCallback<BleDevice>() {
                @Override
                public void onReadSuccess(BluetoothGattCharacteristic characteristic) {
                    super.onReadSuccess(characteristic);
                    byte[] data = characteristic.getValue();

                    if (sendData == data) {
                        return;
                    }
                    String result1 = data.toString();
                    ToastUtil.showLong(VcontrolApplication.getCurrentContext(), "onReadSuccess: " + result1);
                    if (TextUtils.isEmpty(result1)) {
                        return;
                    }
                    String[] res = result1.split("\r\n");

                    for (String result : res) {
                        if (result != null && result.toUpperCase().contains("OK")) {
                            EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_RESULT_OK, result, sendData.toString());
                        } else if (result != null && result.toUpperCase().contains("ERROR")) {
                            EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_RESULT_ERROR, result, sendData.toString());
                        } else if (result != null && result.contains("Not Started")) {// System Not Started
                            ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.Device_again_later));
                        } else {
                            EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_DATA, result, sendData.toString());
                        }
                    }
                }
            });
        }
        if (!result) {
            Log.d(TAG, "读取数据失败!");
        }
    }


    /**
     * 重新扫描
     */
    public void reScan() {
        if (mBle != null) {
            mBle.startScan(scanCallback);
        }
    }


    BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(final BleDevice device, int rssi, byte[] scanRecord) {
            if (mBle != null) {
                synchronized (mBle.getLocker()) {
                }
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e(TAG, "onStop: ");
        }
    };


}
