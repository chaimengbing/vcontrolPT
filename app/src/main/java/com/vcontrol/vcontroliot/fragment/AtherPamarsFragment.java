package com.vcontrol.vcontroliot.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.ServiceUtils;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

/**
 * 其他参数
 * Created by Vcontrol on 2016/11/23.
 */

public class AtherPamarsFragment extends BaseFragment implements View.OnClickListener,EventNotifyHelper.NotificationCenterDelegate
{

    private Button collectTimeButton;
    private Button lvValuesButton;

    private EditText collectTimeEditText;
    private EditText lvValuesEditText;

    @Override
    public int getLayoutView()
    {
        return R.layout.fragment_sensor_ather;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_DATA);
    }

    @Override
    public void initComponentViews(View view)
    {
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_DATA);
        collectTimeButton = (Button) view.findViewById(R.id.collect_time_button);
        lvValuesButton = (Button) view.findViewById(R.id.lv_values_button);

        collectTimeEditText = (EditText) view.findViewById(R.id.collect_time_edittext);
        lvValuesEditText = (EditText) view.findViewById(R.id.lv_values_edittext);
    }

    @Override
    public void initData()
    {
        SocketUtil.getSocketUtil().sendContent(ConfigParams.ReadSensorPara2);
    }

    @Override
    public void setListener()
    {
        collectTimeButton.setOnClickListener(this);
        lvValuesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        String values = "";
        int num = -1;
        switch (view.getId())
        {
            case R.id.collect_time_button:
                values = collectTimeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(values))
                {
                    ToastUtil.showToastLong(getString(R.string.Collection_waiting_time_empty));
                    return;
                }
                num = Integer.parseInt(values);
                if (num < 0 || num > 300)
                {
                    ToastUtil.showToastLong(getString(R.string.waiting_time_enter));
                    return;
                }
                SocketUtil.getSocketUtil().sendContent(ConfigParams.SetSample_Delay_Time + ServiceUtils.getStr(values, 3));

                break;
            case R.id.lv_values_button:
                values = lvValuesEditText.getText().toString().trim();
                if (TextUtils.isEmpty(values))
                {
                    ToastUtil.showToastLong(getString(R.string.low_voltage_alarm_value_null));
                    return;
                }

                double temp = Double.parseDouble(values) * 100;
                int level = (int) temp;
                if (temp < 800 || temp > 2000)
                {
                    return;
                }
                SocketUtil.getSocketUtil().sendContent(ConfigParams.SetBatteryVoltLow + ServiceUtils.getStr(level+"", 4));
                break;

            default:
                break;
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args)
    {
        String result = (String) args[0];
        String content = (String) args[1];
        if (TextUtils.isEmpty(result) || TextUtils.isEmpty(content))
        {
            return;
        }
        setData(result);
    }


    private void setData(String result)
    {
        String data = "";
        if (result.contains(ConfigParams.SetSample_Delay_Time))
        {
            data = result.replaceAll(ConfigParams.SetSample_Delay_Time, "").trim();
            collectTimeEditText.setText(data);
        }
        else if (result.contains(ConfigParams.SetBatteryVoltLow.trim()))
        {
            data = result.replaceAll(ConfigParams.SetBatteryVoltLow.trim(), "").trim();
            if (ServiceUtils.isNumeric(data))
            {
                double level = Double.parseDouble(data) / 100.0;
                Log.info("AtherPamarsFragment", "level:" + level + ",data:" + data);
                lvValuesEditText.setText(String.valueOf(level));
            }
        }

    }
}
