package com.vcontrol.vcontroliot.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.act.BleDeviceListActivity;
import com.vcontrol.vcontroliot.act.HomeActivity;
import com.vcontrol.vcontroliot.act.RTUInfoActivity;
import com.vcontrol.vcontroliot.adapter.GridMainAdapter;
import com.vcontrol.vcontroliot.model.MainInfo;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.LocaleUtils;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by linxi on 2017/10/13.
 */

public class EquipmentFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private GridView mainGrid;
    private List<MainInfo> data = new ArrayList<>();

    @BindView(R.id.lru_3300)
    TextView lru3300;
    @BindView(R.id.layout_button2)
    LinearLayout layout2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipment, container, false);
        ButterKnife.bind(this, view);
        initView(view);

        return view;
    }

    private void initView(View view) {

        mainGrid = (GridView) view.findViewById(R.id.grid_main);

        VcontrolApplication.setCurrentContext(getActivity());
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.CONNCT_OK);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.CONNCT_FAIL);

        if (data != null) {
            data = new ArrayList<>();
        }
//
        GridMainAdapter adapter = new GridMainAdapter(getActivity());


        MainInfo main1 = new MainInfo(UiEventEntry.WRU_1901, R.mipmap.wru_1901, getString(R.string.wru_1801));
        MainInfo main2 = new MainInfo(UiEventEntry.WRU_2100, R.mipmap.wru_2100, getString(R.string.wru_2100));
        MainInfo main3 = new MainInfo(UiEventEntry.WRU_2800, R.mipmap.wru_2800, getString(R.string.wru_2800));
        MainInfo main4 = new MainInfo(UiEventEntry.WRU_2801, R.mipmap.wru_2800, getString(R.string.wru_2801));
        MainInfo main5 = new MainInfo(UiEventEntry.RTU_2800, R.mipmap.wru_2800, getString(R.string.rtu_2800));
        MainInfo main6 = new MainInfo(UiEventEntry.RTU_2801, R.mipmap.wru_2800, getString(R.string.rtu_2801));
        MainInfo main8 = new MainInfo(UiEventEntry.LRU_3000, R.mipmap.lru_3000, getString(R.string.lru_3000));
        MainInfo main7 = new MainInfo(UiEventEntry.RCM_2000, R.mipmap.vcm_2000, getString(R.string.rcm_2000));
        MainInfo main9 = new MainInfo(UiEventEntry.LRU_3200, R.mipmap.lru_9000, "LRU-9000");
        MainInfo main10 = new MainInfo(UiEventEntry.LRU_6000, R.mipmap.lru_9000, "LRU-6000");


        if (!SystemFragment.setLister().equals("0")) {
            data.add(main1);
        }
        if (!SystemFragment.setLister2().equals("0")) {
            data.add(main2);
        }
        if (!SystemFragment.setLister3().equals("0")) {
            data.add(main3);
        }
        if (!SystemFragment.setLister4().equals("0")) {
            data.add(main4);
        }
        if (!SystemFragment.setLister5().equals("0")) {
            data.add(main5);
        }
        if (!SystemFragment.setLister6().equals("0")) {
            data.add(main6);
        }
        if (!SystemFragment.setLister7().equals("0")) {
            data.add(main7);
        }
        if (!SystemFragment.setLister8().equals("0")) {
            data.add(main8);
        }
        if (!SystemFragment.setLister9().equals("0")) {
            data.add(main9);
        }
        if (!SystemFragment.setLister10().equals("0")) {
            data.add(main10);
        }
//


//        data.add(main1);
//        data.add(main2);
//        data.add(main3);
//        data.add(main4);
//        data.add(main5);
//        data.add(main6);
//        data.add(main7);
//        data.add(main8);
//        data.add(main9);
        adapter.setData(data);
        mainGrid.setAdapter(adapter);

        setListener();


    }


    @OnClick(R.id.lru_3300)
    void bleConnect(View view) {
        Intent intent = new Intent(getActivity(), BleDeviceListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id) {
            case R.id.chinese:
                if (LocaleUtils.needUpdateLocale(getActivity(), LocaleUtils.LOCALE_CHINESE)) {
                    LocaleUtils.updateLocale(getActivity(), LocaleUtils.LOCALE_CHINESE);
                }
                break;
            case R.id.english:
                if (LocaleUtils.needUpdateLocale(getActivity(), LocaleUtils.LOCALE_ENGLISH)) {
                    LocaleUtils.updateLocale(getActivity(), LocaleUtils.LOCALE_ENGLISH);

                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.CONNCT_FAIL);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.CONNCT_OK);
    }

    private void setListener() {

        mainGrid.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            SocketUtil.getSocketUtil().connectRTU(ConfigParams.IP, ConfigParams.GROUND_PORT);
        } else {
            SocketUtil.getSocketUtil().connectRTU(ConfigParams.IP, ConfigParams.PORT);
        }


        if (position == 10) {
            Intent intent = new Intent(getActivity(), RTUInfoActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.putExtra(UiEventEntry.NOTIFY_BASIC_NAME, data.get(position).getName());
            intent.putExtra(UiEventEntry.NOTIFY_BASIC_TYPE, data.get(position).getId());
            startActivity(intent);
        }
    }
}
