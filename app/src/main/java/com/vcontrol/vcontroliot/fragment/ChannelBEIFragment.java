package com.vcontrol.vcontroliot.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.ServiceUtils;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

/**
 * 北斗设置
 * Created by Vcontrol on 2017/03/27.
 */

public class ChannelBEIFragment extends BaseFragment implements View.OnClickListener, EventNotifyHelper.NotificationCenterDelegate
{
    private LinearLayout company;
    private TextView channel1;
    private TextView channel2;
    private TextView channel3;
    private TextView channel4;

    private EditText num1;
    private EditText num2;
    private EditText num3;
    private EditText num4;

    private Button set1;
    private Button set2;
    private Button set3;
    private Button set4;

    private RadioGroup beiDouGroup;

    @Override
    public int getLayoutView()
    {
        return R.layout.fragment_setting_channel_gms;
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
        num1 = (EditText) view.findViewById(R.id.num_1_edittext);
        num2 = (EditText) view.findViewById(R.id.num_2_edittext);
        num3 = (EditText) view.findViewById(R.id.num_3_edittext);
        num4 = (EditText) view.findViewById(R.id.num_4_edittext);

        set1 = (Button) view.findViewById(R.id.gms_1_button);
        set2 = (Button) view.findViewById(R.id.gms_2_button);
        set3 = (Button) view.findViewById(R.id.gms_3_button);
        set4 = (Button) view.findViewById(R.id.gms_4_button);

        company = (LinearLayout) view.findViewById(R.id.beidou_company);
        channel1 = (TextView) view.findViewById(R.id.channel_1);
        channel2 = (TextView) view.findViewById(R.id.channel_2);
        channel3 = (TextView) view.findViewById(R.id.channel_3);
        channel4 = (TextView) view.findViewById(R.id.channel_4);

        company.setVisibility(View.VISIBLE);
        channel1.setText(getString(R.string.Beidou_channel_1));
        channel2.setText(getString(R.string.Beidou_channel_2));
        channel3.setText(getString(R.string.Beidou_channel_3));
        channel4.setText(getString(R.string.Beidou_channel_4));

        beiDouGroup = (RadioGroup) view.findViewById(R.id.beidou_radiogroup);

        initView(view);

    }

    private void initView(final View view)
    {
        beiDouGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                View checkView = view.findViewById(checkedId);
                if (!checkView.isPressed())
                {
                    return;
                }
                String content = ConfigParams.SetBeidouType;
                if (checkedId == R.id.beidou_button)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "00");
                }
                else if (checkedId == R.id.beidou_button_2)
                {
                    SocketUtil.getSocketUtil().sendContent(content + "01");

                }

            }
        });
    }

    @Override
    public void initData()
    {
        SocketUtil.getSocketUtil().sendContent(ConfigParams.ReadCommPara3);
    }

    @Override
    public void setListener()
    {
        set1.setOnClickListener(this);
        set2.setOnClickListener(this);
        set3.setOnClickListener(this);
        set4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        String num;
        String content = "";
        switch (view.getId())
        {
            case R.id.gms_1_button:
                num = num1.getText().toString().trim();
                if (TextUtils.isEmpty(num))
                {
                    ToastUtil.showToastLong(getString(R.string.compass_number_cannot_be_empty));
                    return;
                }

                content = ConfigParams.SetBeiDou + "1" + " " + ServiceUtils.getStr(num, 6);
                break;
            case R.id.gms_2_button:
                num = num2.getText().toString().trim();
                if (TextUtils.isEmpty(num))
                {
                    ToastUtil.showToastLong(getString(R.string.compass_number_cannot_be_empty));
                    return;
                }

                content = ConfigParams.SetBeiDou + "2" + " " + ServiceUtils.getStr(num, 6);
                break;
            case R.id.gms_3_button:
                num = num3.getText().toString().trim();
                if (TextUtils.isEmpty(num))
                {
                    ToastUtil.showToastLong(getString(R.string.compass_number_cannot_be_empty));
                    return;
                }

                content = ConfigParams.SetBeiDou + "3" + " " + ServiceUtils.getStr(num, 6);
                break;
            case R.id.gms_4_button:
                num = num4.getText().toString().trim();
                if (TextUtils.isEmpty(num))
                {
                    ToastUtil.showToastLong(getString(R.string.compass_number_cannot_be_empty));
                    return;
                }

                content = ConfigParams.SetBeiDou + "4" + " " + ServiceUtils.getStr(num, 6);
                break;
        }

        SocketUtil.getSocketUtil().sendContent(content);
    }


    @Override
    public void didReceivedNotification(int id, Object... args)
    {
        String result = (String) args[0];
        String content = (String) args[1];
        if (TextUtils.isEmpty(result))
        {
            return;
        }
        setData(result);
    }

    private void setData(String result)
    {
        // 北斗信道 1 2 3 4
        if (result.contains(ConfigParams.SetBeiDou + "1"))
        {
            num1.setText(result.replaceAll(ConfigParams.SetBeiDou + "1", "").trim());
        }
        else if (result.contains(ConfigParams.SetBeiDou + "2"))
        {
            num2.setText(result.replaceAll(ConfigParams.SetBeiDou + "2", "").trim());
        }
        else if (result.contains(ConfigParams.SetBeiDou + "3"))
        {
            num3.setText(result.replaceAll(ConfigParams.SetBeiDou + "3", "").trim());
        }
        else if (result.contains(ConfigParams.SetBeiDou + "4"))
        {
            num4.setText(result.replaceAll(ConfigParams.SetBeiDou + "4", "").trim());
        }
        else if (result.contains(ConfigParams.SetBeidouType.trim()))
        {// 北斗厂家
            String data = result.replaceAll(ConfigParams.SetBeidouType, "").trim();
            if (data.equals("0"))
            {
                beiDouGroup.check(R.id.beidou_button);
            }
            else
            {
                beiDouGroup.check(R.id.beidou_button_2);
            }
        }
    }

}
