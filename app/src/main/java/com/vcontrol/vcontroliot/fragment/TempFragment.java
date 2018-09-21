package com.vcontrol.vcontroliot.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

/**
 * 温度计
 * Created by linxi on 2017/7/11.
 */

public class  TempFragment extends BaseFragment implements View.OnClickListener, EventNotifyHelper.NotificationCenterDelegate
{
    private final static String TAG = TempFragment.class.getSimpleName();


    private RadioGroup tempRadio;




    @Override
    public int getLayoutView()
    {
        return R.layout.fragment_sensor_temp;
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


        tempRadio = (RadioGroup) view.findViewById(R.id.temp_type);


        initView(view);

    }

    private void initView(final View view)
    {
        tempRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                View checkView = view.findViewById(checkedId);
                if (!checkView.isPressed())
                {
                    return;
                }
                String content = ConfigParams.SetWaterTempType;
                if (checkedId == R.id.temp_type_button)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "1");
                }
                else if (checkedId == R.id.temp_type_button1)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "2");

                }
                else if (checkedId == R.id.temp_type_button2)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "3");

                }

            }
        });


    }

    @Override
    public void initData()
    {


        SocketUtil.getSocketUtil().sendContent(ConfigParams.ReadDIANBIAO_SensorPara);
    }

    @Override
    public void setListener()
    {

    }

    @Override
    public void onClick(View v)
    {

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

        if (result.contains(ConfigParams.SetWaterTempType))
        {
            data = result.replaceAll(ConfigParams.SetWaterTempType, "").trim();
            if ("1".equals(data))
            {
                tempRadio.check(R.id.temp_type_button);
            }
            else if ("2".equals(data))
            {
                tempRadio.check(R.id.temp_type_button1);
            }
            else if ("3".equals(data))
            {
                tempRadio.check(R.id.temp_type_button2);
            }
            else
            {
                tempRadio.clearCheck();
            }
        }


    }
}

