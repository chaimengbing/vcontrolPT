package com.vcontrol.vcontroliot.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

/**
 * Created by linxi on 2018/3/23.
 */

public class WQualitySearchFragment extends BaseFragment implements View.OnClickListener,EventNotifyHelper.NotificationCenterDelegate
{

    private static final String TAG = WQualitySearchFragment.class.getSimpleName();

    private TextView soilText;

    private StringBuffer currentSB = new StringBuffer();
    private String string = "";

    private String Temperature_Water ;
    private String CDNR ;
    private String CDN2R ;
    private String CDN3R ;
    private String CDN4R ;
    private String PH ;
    private String DO ;
    private String CHLA ;
    private String Phycocyanin ;
    private String COD ;
    private String NH4N ;
    @Override
    public void onClick(View v)
    {

    }

    @Override
    public int getLayoutView()
    {
        return R.layout.wq_search_fragment;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_DATA);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.NOTIFY_BUNDLE);

    }
    @Override
    public void initComponentViews(View view)
    {
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_DATA);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.NOTIFY_BUNDLE);


        soilText = (TextView) view.findViewById(R.id.wq_search);

        setData();
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

        if (result.contains(ConfigParams.Temperature_Water))
        {
            currentSB.insert(currentSB.indexOf(Temperature_Water) + Temperature_Water.length(), result.replaceAll(ConfigParams.Temperature_Water, "").trim());
        }
        else if (result.contains(ConfigParams.CDNR))
        {
            currentSB.insert(currentSB.indexOf(CDNR) + CDNR.length(), result.replaceAll(ConfigParams.CDNR, "").trim());
        }
        else if (result.contains(ConfigParams.CDN2R))
        {
            currentSB.insert(currentSB.indexOf(CDN2R) + CDN2R.length(), result.replaceAll(ConfigParams.CDN2R, "").trim());
        }
        else if (result.contains(ConfigParams.CDN3R))
        {
            currentSB.insert(currentSB.indexOf(CDN3R) + CDN3R.length(), result.replaceAll(ConfigParams.CDN3R, "").trim());
        }
        else if (result.contains(ConfigParams.CDN4R))
        {
            currentSB.insert(currentSB.indexOf(CDN4R) + CDN4R.length(), result.replaceAll(ConfigParams.CDN4R, "").trim());
        }
        else if (result.contains(ConfigParams.PH))
        {
            currentSB.insert(currentSB.indexOf(PH) + PH.length(), result.replaceAll(ConfigParams.PH, "").trim());
        }
        else if (result.contains(ConfigParams.DO))
        {
            currentSB.insert(currentSB.indexOf(DO) + DO.length(), result.replaceAll(ConfigParams.DO, "").trim());
        }
        else if (result.contains(ConfigParams.CHLA))
        {
            currentSB.insert(currentSB.indexOf(CHLA) + CHLA.length(), result.replaceAll(ConfigParams.CHLA, "").trim());
        }
        else if (result.contains(ConfigParams.Phycocyanin))
        {
            currentSB.insert(currentSB.indexOf(Phycocyanin) + Phycocyanin.length(), result.replaceAll(ConfigParams.Phycocyanin, "").trim());
        }
        else if (result.contains(ConfigParams.COD))
        {
            currentSB.insert(currentSB.indexOf(COD) + COD.length(), result.replaceAll(ConfigParams.COD, "").trim());
        }
        else if (result.contains(ConfigParams.NH4N))
        {
            currentSB.insert(currentSB.indexOf(NH4N) + NH4N.length(), result.replaceAll(ConfigParams.NH4N, "").trim());
        }

        soilText.setText(currentSB.toString());
    }


    public void setData()
    {

        Temperature_Water = getString(R.string.Water_Temperature);
        CDNR = getString(R.string.Conductance);
        CDN2R = getString(R.string.Conductance_2);
        CDN3R = getString(R.string.Conductance_3);
        CDN4R = getString(R.string.Conductance_4);
        PH = getString(R.string.PH_value);
        DO = getString(R.string.DO);
        CHLA = getString(R.string.Chlorophyll);
        Phycocyanin = getString(R.string.Blue_green_algae);
        COD = getString(R.string.Chemical_oxygen_demand);
        NH4N = getString(R.string.NH4N);

        String content = ConfigParams.Read_SHUIZHI_data;
        SocketUtil.getSocketUtil().sendContent(content);
        currentSB.delete(0, currentSB.length());

        currentSB.append(Temperature_Water);
        currentSB.append("\n");
        currentSB.append(CDNR);
        currentSB.append("\n");
        currentSB.append(CDN2R);
        currentSB.append("\n");
        currentSB.append(CDN3R);
        currentSB.append("\n");
        currentSB.append(CDN4R);
        currentSB.append("\n");
        currentSB.append(PH);
        currentSB.append("\n");
        currentSB.append(DO);
        currentSB.append("\n");
        currentSB.append(CHLA);
        currentSB.append("\n");
        currentSB.append(Phycocyanin);
        currentSB.append("\n");
        currentSB.append(COD);
        currentSB.append("\n");
        currentSB.append(NH4N);
        currentSB.append("\n");

        if (soilText != null && currentSB.length() > 0)
        {
            soilText.setText(currentSB.toString());
        }
    }
}
