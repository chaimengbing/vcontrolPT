package com.vcontrol.vcontroliot.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.adapter.SimpleSpinnerAdapter;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.ServiceUtils;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

/**
 * 压力计
 * Created by linxi on 2017/7/11.
 */

public class  PressFragment extends BaseFragment implements View.OnClickListener, EventNotifyHelper.NotificationCenterDelegate
{
    private final static String TAG = PressFragment.class.getSimpleName();

    private Button modelButton;
    private EditText pressNumEdittext;

    private RadioGroup press485Radio;
    private RadioGroup pressRadio;

    private Spinner pressSpinner;
    private String[] pressItems;
    private SimpleSpinnerAdapter pressAdapter;


    @Override
    public int getLayoutView()
    {
        return R.layout.fragment_sensor_press;
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

        pressSpinner = (Spinner) view.findViewById(R.id.flow_485_spinner);

        modelButton = (Button) view.findViewById(R.id.press_model_button);
        pressRadio = (RadioGroup) view.findViewById(R.id.press_type);
        press485Radio = (RadioGroup) view.findViewById(R.id.press485_type);
        pressNumEdittext = (EditText) view.findViewById(R.id.press_model_edittext);

        initView(view);

    }

    private void initView(final View view)
    {
        pressRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                View checkView = view.findViewById(checkedId);
                if (!checkView.isPressed())
                {
                    return;
                }
                String content = ConfigParams.SetPressType;
                if (checkedId == R.id.press_type_button)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "1");
                }
                else if (checkedId == R.id.press_type_button1)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "2");

                }
                else if (checkedId == R.id.press_type_button2)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "3");

                }

            }
        });

        press485Radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                View checkView = view.findViewById(checkedId);
                if (!checkView.isPressed())
                {
                    return;
                }
                String content = ConfigParams.SetPress485Type;
                if (checkedId == R.id.press485_type_button1)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "1");
                }

            }
        });
    }

    @Override
    public void initData()
    {

        pressItems = getResources().getStringArray(R.array.Pressure_manufacturers);
        pressAdapter = new SimpleSpinnerAdapter(getActivity(), R.layout.simple_spinner_item, pressItems);
        pressSpinner.setAdapter(pressAdapter);

        SocketUtil.getSocketUtil().sendContent(ConfigParams.ReadSensorPara2);
    }

    @Override
    public void setListener()
    {
        modelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.press_model_button)
        {
            String num = pressNumEdittext.getText().toString().trim();
            if (TextUtils.isEmpty(num))
            {
                ToastUtil.showToastLong(getString(R.string.analog_range_empt));
                return;
            }

            double temp = Double.parseDouble(num) * 100;
            int level = (int) temp;
            if (level < 0 || level > 99999)
            {
                return;
            }

            SocketUtil.getSocketUtil().sendContent(ConfigParams.SetAnaPressRange + ServiceUtils.getStr(level + "", 5));
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args)
    {
        if (id == UiEventEntry.READ_DATA)
        {

            String result = (String) args[0];
            String content = (String) args[1];
            if (TextUtils.isEmpty(result))
            {
                return;
            }
            setData(result);
        }

    }

    private void setData(String result)
    {
        String data = "";
        if (result.contains(ConfigParams.SetPress485Type))
        {
            data = result.replaceAll(ConfigParams.SetPress485Type, "").trim();
            Log.info(TAG,"data:" + data);
            if (ServiceUtils.isNumeric(data))
            {
                int i = Integer.parseInt(data);
                pressSpinner.setSelection(i - 1,false);

                pressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        pressAdapter.setSelectedItem(position);
                        String content = ConfigParams.SetPress485Type + ServiceUtils.getStr(position + 1 + "", 2);
                        SocketUtil.getSocketUtil().sendContent(content);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }
        else if (result.contains(ConfigParams.SetPressType))
        {
            data = result.replaceAll(ConfigParams.SetPressType, "").trim();
            if ("1".equals(data))
            {
                pressRadio.check(R.id.press_type_button);
            }
            else if ("2".equals(data))
            {
                pressRadio.check(R.id.press_type_button1);
            }
            else if ("3".equals(data))
            {
                pressRadio.check(R.id.press_type_button2);
            }
            else
            {
                pressRadio.clearCheck();
            }
        }
        else if (result.contains(ConfigParams.SetPress485Type))
        {
            data = result.replaceAll(ConfigParams.SetPress485Type, "").trim();
            if ("1".equals(data))
            {
                press485Radio.check(R.id.press485_type_button1);
            }
            else
            {
                press485Radio.clearCheck();
            }
        }
        else if (result.contains(ConfigParams.SetAnaPressRange))
        {
            data = result.replaceAll(ConfigParams.SetAnaPressRange, "").trim();
            if (ServiceUtils.isNumeric(data))
            {
                double level = Double.parseDouble(data) / 100.0;
                Log.info(TAG, "level:" + level + ",data:" + data);
                pressNumEdittext.setText(String.valueOf(level));
            }
        }
    }
}
