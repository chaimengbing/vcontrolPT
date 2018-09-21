package com.vcontrol.vcontroliot.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

/**
 * Created by Vcontrol on 2016/11/23.
 */

public class LruSearchFragment extends BaseFragment implements EventNotifyHelper.NotificationCenterDelegate
{
    private static final String TAG = LruSearchFragment.class.getSimpleName();


    private byte[] head = new byte[8];


    private TextView resultTextView;

    private String SetPhysicalAddress ;
    private String StandbyInterval ;
    private String LoraOverTime ;
    private String LoRaAddress ;
    private String TransparentAddress ;
//    private String PhyChannel = "物理信道：";
//    private String AirVelocity = "空中速率：";
    /**
     //设置物理地址0x55，	数据域4个字节，低字节在先
     public static final String SetPhysicalAddress = "SetPhysicalAddress ";
     //    StandbyInterval XXXX  待机时间间隔
     public static final String StandbyInterval = "StandbyInterval ";
     //    LoraOverTime XX 超时时间
     public static final String LoraOverTime = "LoraOverTime ";
     //    LoRaAddress XX  ID
     public static final String LoRaAddress = "LoRaAddress  ";
     //    TransparentAddress XX  透传地址
     public static final String TransparentAddress = "TransparentAddress ";
     //    PhyChannel XX   物理信道
     public static final String PhyChannel = "PhyChannel ";
     //    AirVelocity XX  空中速率
     public static final String AirVelocity = "AirVelocity ";
     //    ReadParameters  读取参数
     public static final String ReadParameters = "ReadParameters ";
     //    初始化  FactoryInitialization
     public static final String FactoryInitialization = "FactoryInitialization ";
     */


    private ScrollView resultScroll;
    private StringBuffer currentSB = new StringBuffer();


    @Override
    public int getLayoutView()
    {
        return R.layout.fragment_search;
    }

    @Override
    public void initComponentViews(View view)
    {
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_DATA);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.NOTIFY_BUNDLE);

        resultTextView = (TextView) view.findViewById(R.id.result_data_textview);
        resultScroll = (ScrollView) view.findViewById(R.id.result_scroll);


        setData();

    }


    private byte[] getCheck()
    {
        byte[] check = new byte[2];
        check[0] = 0;
        check[1] = 0;

        for (int i = 0; i< head.length;i++)
        {
            check[0] += head[i];
            check[1] ^= head[i];
        }
        Log.info(TAG,"check[0]:" + check[0] + ",check[1]:" + check[1]);
        return check;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.NOTIFY_BUNDLE);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_DATA);
    }

    @Override
    public void initData()
    {

    }


    @Override
    public void setListener()
    {
    }

    @Override
    public void didReceivedNotification(int id, Object... args)
    {
        if (id == UiEventEntry.NOTIFY_BUNDLE)
        {
            setData();
        }
        else if (id == UiEventEntry.READ_DATA)
        {
            String result = (String) args[0];
            String content = (String) args[1];
            if (TextUtils.isEmpty(result) || TextUtils.isEmpty(content))
            {
                return;
            }
            readData(result, content);
        }
    }

    private void readData(String result, String content)
    {

        String data = "";
        if (result.contains(ConfigParams.Serial))
        {
            currentSB.insert(currentSB.indexOf(SetPhysicalAddress) + SetPhysicalAddress.length(), result.replaceAll(ConfigParams.Serial, "").trim());
        }
        else if (result.contains(ConfigParams.NetInfo))
        {
            data = result.replaceAll(ConfigParams.NetInfo, "").trim();
            currentSB.insert(currentSB.indexOf(LoraOverTime) + LoraOverTime.length(), data);
        }
        else if (result.contains(ConfigParams.Battery))
        {
            currentSB.insert(currentSB.indexOf(StandbyInterval) + StandbyInterval.length(), result.replaceAll(ConfigParams.Battery, "").trim());
        }
        else if (result.contains(ConfigParams.Flows))
        {
            currentSB.insert(currentSB.indexOf(LoRaAddress) + LoRaAddress.length(), result.replaceAll(ConfigParams.Flows, "").trim());
        }
        else if (result.contains(ConfigParams.Pressure))
        {
            currentSB.insert(currentSB.indexOf(TransparentAddress) + TransparentAddress.length(), result.replaceAll(ConfigParams.Pressure, "").trim());
        }

        resultTextView.setText(currentSB.toString());
    }


    public void setData()
    {

        SetPhysicalAddress = getString(R.string.Hardware_serial_number);
        StandbyInterval = getString(R.string.Battery_voltage);
        LoraOverTime = getString(R.string.registration_message);
        LoRaAddress = getString(R.string.Current_water_volume);
        TransparentAddress = getString(R.string.Current_pressure);

        String content = ConfigParams.ReadParameters;
        SocketUtil.getSocketUtil().sendContent(content);
        currentSB.delete(0, currentSB.length());
        resultScroll.setVisibility(View.VISIBLE);
        currentSB.append(SetPhysicalAddress);
        currentSB.append("\n");
        currentSB.append(StandbyInterval);
        currentSB.append("\n");
        currentSB.append(LoraOverTime);
        currentSB.append("\n");
        currentSB.append(LoRaAddress);
        currentSB.append("\n");
        currentSB.append(TransparentAddress);
        currentSB.append("\n");

        if (resultTextView != null && currentSB.length() > 0)
        {
            resultTextView.setText(currentSB.toString());
        }
    }
}
