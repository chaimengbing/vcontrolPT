package com.vcontrol.vcontroliot.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.util.BleUtils;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.ServiceUtils;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

import cn.com.heaton.blelibrary.ble.BleDevice;

/**
 * 闸位计
 * <p>
 * Created by Vcontrol on 2016/11/23.
 */

public class ZWFragment extends BaseFragment implements View.OnClickListener, EventNotifyHelper.NotificationCenterDelegate {

    private RadioGroup planTypeGroup;
    private RadioGroup modelTypeGroup;
    private RadioGroup plan485Group;
    private EditText addValueEditText;
    private EditText rangeEditText;
    private Button addValueButton;
    private Button rangeButton;

    private boolean isBleDevice = false;
    private BleDevice bleDevice = null;

    @Override
    public int getLayoutView() {
        return R.layout.fragment_sensor_zw;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_DATA);
    }

    @Override
    public void initComponentViews(View view) {
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_DATA);


        if (getArguments() != null) {
            isBleDevice = getArguments().getBoolean("isBleDevice");
            bleDevice = (BleDevice) getArguments().getSerializable("device");
        }

        addValueEditText = (EditText) view.findViewById(R.id.zha_add_value);
        rangeEditText = (EditText) view.findViewById(R.id.model_zha_range);
        addValueButton = (Button) view.findViewById(R.id.zha_add_value_button);
        rangeButton = (Button) view.findViewById(R.id.model_zha_range_button);
        planTypeGroup = (RadioGroup) view.findViewById(R.id.zha_plan_type);
        modelTypeGroup = (RadioGroup) view.findViewById(R.id.model_zha_type);
        plan485Group = (RadioGroup) view.findViewById(R.id.zha_plan_485);

        initView(view);
    }


    private void sendData(String content) {
        if (isBleDevice) {
            BleUtils.getInstance().sendData(bleDevice, content.getBytes());
        } else {
            SocketUtil.getSocketUtil().sendContent(content);
        }
    }


    private void initView(final View view) {
        // 闸位计类型
        planTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View checkView = view.findViewById(checkedId);
                String content = ConfigParams.SetGatePosition_Type;
                if (!checkView.isPressed()) {
                    return;
                }
                if (checkedId == R.id.zha_plan_type_button) {
                    sendData(content + "1");
                } else if (checkedId == R.id.zha_plan_type_button2) {
                    sendData(content + "2");
                } else if (checkedId == R.id.zha_plan_type_button3) {
                    sendData(content + "3");
                }
            }
        });

        // 模拟量闸位计类型
        modelTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View checkView = view.findViewById(checkedId);
                String content = ConfigParams.SetAnaGatePositionType;
                if (!checkView.isPressed()) {
                    return;
                }
                if (checkedId == R.id.model_zha_type_button) {
                    sendData(content + "0");
                } else if (checkedId == R.id.model_zha_type_button2) {
                    sendData(content + "1");
                } else if (checkedId == R.id.model_zha_type_button3) {
                    sendData(content + "2");
                } else if (checkedId == R.id.model_zha_type_button4) {
                    sendData(content + "3");
                }
            }
        });

        // 485闸位计
        plan485Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View checkView = view.findViewById(checkedId);
                String content = ConfigParams.SetGatePosition485Type;
                if (!checkView.isPressed()) {
                    return;
                }
                if (checkedId == R.id.zha_plan_485_button) {
                    sendData(content + "1");
                } else if (checkedId == R.id.zha_plan_485_button2) {
                    sendData(content + "2");
                } else if (checkedId == R.id.zha_plan_485_button3) {
                    sendData(content + "3");
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void setListener() {
        addValueButton.setOnClickListener(this);
        rangeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String zhawei = "";
        String content = "";
        switch (view.getId()) {
            case R.id.zha_add_value_button:
                zhawei = addValueEditText.getText().toString().trim();
                content = ConfigParams.Setzhaweijiabao + ServiceUtils.getDouble2(zhawei);
                sendData(content);
                break;
            case R.id.model_zha_range_button:
                zhawei = rangeEditText.getText().toString().trim();
                content = ConfigParams.SetAnaGatePositionRange + ServiceUtils.getDouble2(zhawei);
                sendData(content);
                break;

            default:
                break;
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        String result = (String) args[0];
        if (TextUtils.isEmpty(result)) {
            return;
        }
        setData(result);
    }

    private void setData(String result) {
        String data = "";
        if (result.contains(ConfigParams.SetGatePosition_Type)) {
            data = result.replaceAll(ConfigParams.SetGatePosition_Type, "").trim();
            if ("1".equals(data)) {
                planTypeGroup.check(R.id.zha_plan_type_button);
            } else if ("2".equals(data)) {
                planTypeGroup.check(R.id.zha_plan_type_button2);
            } else if ("3".equals(data)) {
                planTypeGroup.check(R.id.zha_plan_type_button3);
            }
        } else if (result.contains(ConfigParams.SetAnaGatePositionType)) {
            data = result.replaceAll(ConfigParams.SetAnaGatePositionType, "").trim();
            if ("0".equals(data)) {
                modelTypeGroup.check(R.id.model_zha_type_button);
            } else if ("1".equals(data)) {
                modelTypeGroup.check(R.id.model_zha_type_button2);
            } else if ("2".equals(data)) {
                modelTypeGroup.check(R.id.model_zha_type_button3);
            } else if ("3".equals(data)) {
                modelTypeGroup.check(R.id.model_zha_type_button4);
            }
        } else if (result.contains(ConfigParams.SetGatePosition485Type)) {
            data = result.replaceAll(ConfigParams.SetGatePosition485Type, "").trim();
            if ("1".equals(data)) {
                plan485Group.check(R.id.zha_plan_485_button);
            } else if ("2".equals(data)) {
                plan485Group.check(R.id.zha_plan_485_button2);
            } else if ("3".equals(data)) {
                plan485Group.check(R.id.zha_plan_485_button3);
            }
        } else if (result.contains(ConfigParams.Setzhaweijiabao)) {
            data = result.replaceAll(ConfigParams.Setzhaweijiabao, "").trim();
            addValueEditText.setText(data);
        } else if (result.contains(ConfigParams.SetAnaGatePositionRange)) {
            data = result.replaceAll(ConfigParams.SetAnaGatePositionRange, "").trim();
            rangeEditText.setText(data);
        }


    }
}
