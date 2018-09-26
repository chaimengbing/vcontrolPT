package com.vcontrol.vcontroliot.act;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.adapter.CustomAdapter;
import com.vcontrol.vcontroliot.adapter.CustomToAdapter;
import com.vcontrol.vcontroliot.fragment.ADFragment;
import com.vcontrol.vcontroliot.fragment.AmmeterFragment;
import com.vcontrol.vcontroliot.fragment.AmmeterSearchFragment;
import com.vcontrol.vcontroliot.fragment.AnalogQuantityFragment;
import com.vcontrol.vcontroliot.fragment.AtherPamarsFragment;
import com.vcontrol.vcontroliot.fragment.BleSearchFragment;
import com.vcontrol.vcontroliot.fragment.CameraFragment;
import com.vcontrol.vcontroliot.fragment.ChannelBEIFragment;
import com.vcontrol.vcontroliot.fragment.ChannelCENTERFragment;
import com.vcontrol.vcontroliot.fragment.ChannelFragment;
import com.vcontrol.vcontroliot.fragment.ChannelGMSFragment;
import com.vcontrol.vcontroliot.fragment.ChannelGPRSFragment;
import com.vcontrol.vcontroliot.fragment.ChannelSelectFragment;
import com.vcontrol.vcontroliot.fragment.CollectFragment;
import com.vcontrol.vcontroliot.fragment.CommBasicSearchFragment;
import com.vcontrol.vcontroliot.fragment.CommParamsFragment;
import com.vcontrol.vcontroliot.fragment.CommRtuChannelFragment;
import com.vcontrol.vcontroliot.fragment.CommRtuSysFragment;
import com.vcontrol.vcontroliot.fragment.CommSensorFragment;
import com.vcontrol.vcontroliot.fragment.ControlShowFragment;
import com.vcontrol.vcontroliot.fragment.DevicesFragment;
import com.vcontrol.vcontroliot.fragment.FlowFragment;
import com.vcontrol.vcontroliot.fragment.GroundADFragment;
import com.vcontrol.vcontroliot.fragment.GroundServerFragment;
import com.vcontrol.vcontroliot.fragment.GroundWaterBasicFragment;
import com.vcontrol.vcontroliot.fragment.GroundWaterSearchFragment;
import com.vcontrol.vcontroliot.fragment.LNewSearchFragment;
import com.vcontrol.vcontroliot.fragment.LNewSysPamarsFragment;
import com.vcontrol.vcontroliot.fragment.LSearchFragment;
import com.vcontrol.vcontroliot.fragment.LSysPamarsFragment;
import com.vcontrol.vcontroliot.fragment.LruSearchFragment;
import com.vcontrol.vcontroliot.fragment.LruSysPamarsFragment;
import com.vcontrol.vcontroliot.fragment.PressFragment;
import com.vcontrol.vcontroliot.fragment.RTUVersionFragment;
import com.vcontrol.vcontroliot.fragment.RainPamarsFragment;
import com.vcontrol.vcontroliot.fragment.RcmFunPamarsFragment;
import com.vcontrol.vcontroliot.fragment.RcmFunSearchFragment;
import com.vcontrol.vcontroliot.fragment.RcmSearchFragment;
import com.vcontrol.vcontroliot.fragment.RcmSysPamarsFragment;
import com.vcontrol.vcontroliot.fragment.SQFragment;
import com.vcontrol.vcontroliot.fragment.SearchDataFragment;
import com.vcontrol.vcontroliot.fragment.SearchFragment;
import com.vcontrol.vcontroliot.fragment.SoilSearchFragment;
import com.vcontrol.vcontroliot.fragment.SystemPamarsFragment;
import com.vcontrol.vcontroliot.fragment.TempFragment;
import com.vcontrol.vcontroliot.fragment.ValveControlRelayFragment;
import com.vcontrol.vcontroliot.fragment.ViewDialogFragment;
import com.vcontrol.vcontroliot.fragment.WQualitySearchFragment;
import com.vcontrol.vcontroliot.fragment.WaterPamarsFragment;
import com.vcontrol.vcontroliot.fragment.WaterPlanFragment;
import com.vcontrol.vcontroliot.fragment.WaterQualityFragment;
import com.vcontrol.vcontroliot.fragment.WeatherParamFragment;
import com.vcontrol.vcontroliot.fragment.YPTFragment;
import com.vcontrol.vcontroliot.fragment.YUNFragment;
import com.vcontrol.vcontroliot.fragment.ZWFragment;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.util.BleUtils;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.FgManager;
import com.vcontrol.vcontroliot.util.ProgressBarUtil;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;
import com.vcontrol.vcontroliot.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleConnCallback;

/**
 * Created by Vcontrol on 2016/11/24.
 */

public class HomeActivity extends BaseActivity implements PopupWindow.OnDismissListener, EventNotifyHelper.NotificationCenterDelegate, ViewDialogFragment.Callback {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private int currentType = 200;

    private LinearLayout titleLayout;
    private LinearLayout bottomLayout;
    private LinearLayout bottomenuLayout;
    private TextView rtuSetting;
    private TextView rtuSearch;
    private TextView rtuVersion;
    private int currentTab = 44;


    private LinearLayout setLayout;
    private TextView setTextView;
    private ImageView iconImageView;

    private ListView setListView;
    private ListView setToListView;
    private TextView sensorTextView;
    private LinearLayout sensorLayout;

    private CustomToAdapter setToAdapter;
    private CustomAdapter setAdapter;
    private String tabName = "";

    private PopupWindow popupWindow;
    private List<String> searchList;
    private List<String> setList;
    private List<String> setToList;
    private List<String> setToChannelList;
    private List<String> setToRcmList;
    private List<String> setToLruList;


    private SearchFragment gprsFragment = null;
    private ADFragment adFragment = null;
    private CommBasicSearchFragment commFragment = null;
    private LruSearchFragment LruSearch = null;
    private RcmSearchFragment rcmSearch = null;
    private RcmFunSearchFragment rcmFunSearch = null;
    private ChannelFragment channelFragment = null;
    ;
    private DevicesFragment devicesFragment = null;
    private LSearchFragment lSearchFragment = null;
    private ValveControlRelayFragment valveControlRelayFragment = null;

    private SoilSearchFragment soilSearchFragment;
    private WQualitySearchFragment wQualitySearchFragment;
    private AmmeterSearchFragment ammeterSearchFragment;
    private LNewSearchFragment lNewSearchFragment;


    private BleDevice bleDevice;

    //当前选项
    private int currentSel = 100;

    @Override
    public int getLayoutView() {
        return R.layout.fragment_rtu_basic;
    }

    @Override
    public void initViewData() {
        //显示toolbar
        showToolbar();
        setTitleName(rtuSetting.getText().toString().trim());

        rtuSetting.setOnClickListener(this);
        rtuSearch.setOnClickListener(this);
        rtuVersion.setOnClickListener(this);

        if (getIntent() != null) {
            String name = getIntent().getStringExtra(UiEventEntry.NOTIFY_BASIC_NAME);
            currentType = getIntent().getIntExtra(UiEventEntry.NOTIFY_BASIC_TYPE, UiEventEntry.WRU_1901);
            bleDevice = (BleDevice) getIntent().getSerializableExtra("device");
            setTitleMain(name);
        }

        if (currentType == UiEventEntry.WRU_2800 || currentType == UiEventEntry.WRU_2801 || currentType == UiEventEntry.WRU_2100) {
            Bundle bundle = new Bundle();
            bundle.putInt(UiEventEntry.CURRENT_RTU_NAME, currentType);
            currentSel = UiEventEntry.TAB_SETTING_SYS;
            turnToFragmentStack(R.id.detail_layout, SystemPamarsFragment.class, bundle);
            currentSel = UiEventEntry.TAB_SETTING_SYS;
        } else if (currentType == UiEventEntry.WRU_1901) {
            turnToFragmentStack(R.id.detail_layout, GroundWaterBasicFragment.class);
            setCurrentSel(UiEventEntry.TAB_GROUND_WATER_BASIC);
        } else if (currentType == UiEventEntry.RTU_2800 || currentType == UiEventEntry.RTU_2801) {

            turnToFragmentStack(R.id.detail_layout, CommRtuSysFragment.class);
            setCurrentSel(UiEventEntry.TAB_COMM_SYSTEM);
            currentSel = UiEventEntry.TAB_GROUND_WATER_BASIC;
        } else if (currentType == UiEventEntry.LRU_3000) {
            turnToFragmentStack(R.id.detail_layout, LruSysPamarsFragment.class);
            currentSel = UiEventEntry.TAB_LRU_SYS;
            titleLayout.setVisibility(View.GONE);
        } else if (currentType == UiEventEntry.RCM_2000) {
            turnToFragmentStack(R.id.detail_layout, RcmSysPamarsFragment.class);
            currentSel = UiEventEntry.TAB_RCM_SYS;
        } else if (currentType == UiEventEntry.LRU_3200) {
            currentSel = UiEventEntry.TAB_SETTING_SYS;
            turnToFragmentStack(R.id.detail_layout, LSysPamarsFragment.class);
            currentSel = UiEventEntry.TAB_SETTING_SYS;
            titleLayout.setVisibility(View.GONE);
        } else if (currentType == UiEventEntry.LRU_3100) {
            turnToFragmentStack(R.id.detail_layout, ControlShowFragment.class);
            currentSel = UiEventEntry.TAB_LRU_CONTROL;
            bottomenuLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        } else if (currentType == UiEventEntry.LRU_6000) {
            turnToFragmentStack(R.id.detail_layout, LNewSysPamarsFragment.class);
            currentSel = UiEventEntry.TAB_LRU_NEW_SETTING;
        } else if (currentType == UiEventEntry.LRU_BLE_3300) {
            Bundle data = new Bundle();
            data.putBoolean("isBleDevice", true);
            data.putSerializable("device", bleDevice);
            turnToFragmentStack(R.id.detail_layout, LNewSysPamarsFragment.class, data);
        }

        setTitleRightVisible(View.VISIBLE);
        setTitleRight(getString(R.string.update));
        initSetData();
        initSetToData();
        initSetChannelData();
        initSearchData();
        initSetToRcmData();
        initSetToLRUData();


        if (setList.size() > 0) {
            setTextView.setText(setList.get(0));
        }
        setLayout.setOnClickListener(this);

    }

    public void setCurrentSel(int currentSel) {
        this.currentSel = currentSel;
    }

    private void initSetToData() {

        setToList.add(getString(R.string.rain_pamars));
        setToList.add(getString(R.string.water_pamars));
        setToList.add(getString(R.string.water_plan));
        setToList.add(getString(R.string.camera));
        setToList.add(getString(R.string.shangqing));
        setToList.add(getString(R.string.gprs_plan));
        setToList.add(getString(R.string.zawei_plan));
        setToList.add(getString(R.string.ather_pamars));
        setToList.add(getString(R.string.Water_quality_parameters));
        setToList.add(getString(R.string.Meteorological_parameters));
        setToList.add(getString(R.string.Analog_settings));
        setToList.add(getString(R.string.Meter_settings));
        setToList.add("温度计");
        if (currentType == UiEventEntry.WRU_2100) {
            setToList.add(getString(R.string.Manometer));
        }

    }

    private void initSetChannelData() {

        setToChannelList.add(getString(R.string.channel_select));
        setToChannelList.add(getString(R.string.gprs_pamars));
        setToChannelList.add(getString(R.string.gms_pamars));
        setToChannelList.add(getString(R.string.bei_pamars));
        setToChannelList.add(getString(R.string.Central_station_address_setting));
        setToChannelList.add(getString(R.string.Channel_demonstration_settings));

    }


    private void initSetData() {
        if (currentType == UiEventEntry.WRU_2800 || currentType == UiEventEntry.WRU_2801 || currentType == UiEventEntry.WRU_2100) {
            setList.add(getString(R.string.system_params_setting));
            setList.add(getString(R.string.collect_setting));
            setList.add(getString(R.string.comm_params_setting));
            setList.add(getString(R.string.channel_setting));
            setList.add(getString(R.string.sensor_setting));
            setList.add(getString(R.string.ad_setting));
            setList.add(getString(R.string.Valve_control_relay_settings));
        } else if (currentType == UiEventEntry.WRU_1901) {
            setList.add(getString(R.string.groundwater_basic_setting));
            setList.add(getString(R.string.groundwater_server_setting));
            setList.add(getString(R.string.ad_setting));
            setList.add(getString(R.string.collect_setting));
        } else if (currentType == UiEventEntry.RTU_2801 || currentType == UiEventEntry.RTU_2800) {
            setList.add(getString(R.string.system_params_setting));
            setList.add(getString(R.string.comm_params_setting));
            setList.add(getString(R.string.sensor_setting));
            setList.add(getString(R.string.ad_setting));

        } else if (currentType == UiEventEntry.LRU_3100) {
            setList.add("RTU-1");
            setList.add("RTU-2");
            setList.add("RTU-3");
            setList.add("RTU-4");

        } else if (currentType == UiEventEntry.RCM_2000) {
            setList.add(getString(R.string.System_board_settings));
            setList.add(getString(R.string.Function_board_settings));
            setList.add(getString(R.string.PTZ_settings));
            setList.add(getString(R.string.sensor_setting));
        } else if (currentType == UiEventEntry.LRU_6000) {
            setList.add(getString(R.string.system_params_setting));
            setToChannelList.add(getString(R.string.Channel_demonstration_settings));
        } else if (currentType == UiEventEntry.LRU_BLE_3300) {
            setList.add(getString(R.string.system_params_setting));
            setList.add(getString(R.string.sensor_setting));
        }

    }

    private void initSetToRcmData() {

        setToRcmList.add(getString(R.string.rain_pamars));
        setToRcmList.add(getString(R.string.water_pamars));
        setToRcmList.add(getString(R.string.water_plan));
    }

    private void initSetToLRUData() {
        setToLruList.add(getString(R.string.water_plan));
        setToLruList.add(getString(R.string.shangqing));
        setToLruList.add(getString(R.string.zawei_plan));
    }

    @Override
    public void initComponentViews() {
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_RESULT_ERROR);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_RESULT_OK);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.CONNCT_AGAIN);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.CONNCT_OK);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.CONNCT_FAIL);

        VcontrolApplication.getInstance().addActivity(this);

        ToastUtil.setCurrentContext(getApplication());
        rtuSetting = (TextView) findViewById(R.id.rtu_setting);
        rtuSearch = (TextView) findViewById(R.id.rtu_search);
        rtuVersion = (TextView) findViewById(R.id.rtu_version);

        titleLayout = (LinearLayout) findViewById(R.id.ll_layout);
        bottomenuLayout = (LinearLayout) findViewById(R.id.bottom_menu_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        setLayout = (LinearLayout) findViewById(R.id.ll_setting);
        setTextView = (TextView) findViewById(R.id.set_textview);
        iconImageView = (ImageView) findViewById(R.id.icon_default);

        setList = new ArrayList<>();
        setToList = new ArrayList<>();
        setToChannelList = new ArrayList<>();
        searchList = new ArrayList<>();
        setToRcmList = new ArrayList<>();
        setToLruList = new ArrayList<>();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.debug(TAG, "onDestroy::");
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_RESULT_OK);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_RESULT_ERROR);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.CONNCT_AGAIN);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.CONNCT_OK);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.CONNCT_FAIL);
    }

    private void selRtu(TextView textView, int id) {

        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.bottomblack));
            textView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(id), null, null);
        }
    }

    private void nolRtu(TextView textView, int id) {
        if (textView != null) {
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(id), null, null);
        }
    }

    @Override
    public void onClick(View view) {
//        bundle.clear();
        String rtuDetail = "";
        Bundle bundle = new Bundle();
        bundle.putInt(UiEventEntry.CURRENT_RTU_NAME, currentType);
        bundle.putInt(UiEventEntry.CURRENT_SEARCH, UiEventEntry.TAB_SEARCH_BASIC);
        switch (view.getId()) {
            case R.id.rtu_setting:
                SearchFragment gprsFragment = (SearchFragment) FgManager.getFragment(SearchFragment.class);
                if (gprsFragment != null && gprsFragment.isVisible()) {
                    gprsFragment.stopUpdate();
                }

                rtuDetail = rtuSetting.getText().toString().trim();
                setTitleName(rtuDetail);
                selRtu(rtuSetting, R.mipmap.rtu_setting_sel);
                nolRtu(rtuSearch, R.mipmap.rtu_search);
                nolRtu(rtuVersion, R.mipmap.rtu_version);


                titleLayout.setVisibility(View.VISIBLE);
                if (setList.size() > 0) {
                    setTextView.setText(setList.get(0));
                }
                currentTab = UiEventEntry.TAB_SETTING;

                if (currentType == UiEventEntry.WRU_2800 || currentType == UiEventEntry.WRU_2801 || currentType == UiEventEntry.WRU_2100) {
                    currentSel = UiEventEntry.TAB_SETTING_SYS;

                    turnToFragmentStack(R.id.detail_layout, SystemPamarsFragment.class, bundle);
                } else if (currentType == UiEventEntry.WRU_1901) {
                    turnToFragmentStack(R.id.detail_layout, GroundWaterBasicFragment.class);
                    setCurrentSel(UiEventEntry.TAB_GROUND_WATER_BASIC);
                } else if (currentType == UiEventEntry.RTU_2800 || currentType == UiEventEntry.RTU_2801) {

                    turnToFragmentStack(R.id.detail_layout, CommRtuSysFragment.class);
                    setCurrentSel(UiEventEntry.TAB_COMM_SYSTEM);
                } else if (currentType == UiEventEntry.LRU_3000) {

                    turnToFragmentStack(R.id.detail_layout, LruSysPamarsFragment.class);
                    setCurrentSel(UiEventEntry.TAB_LRU_SYS);
                    titleLayout.setVisibility(View.GONE);
                } else if (currentType == UiEventEntry.LRU_3200) {
                    turnToFragmentStack(R.id.detail_layout, LSysPamarsFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SETTING_SYS);
                    titleLayout.setVisibility(View.GONE);
                } else if (currentType == UiEventEntry.RCM_2000) {

                    turnToFragmentStack(R.id.detail_layout, RcmSysPamarsFragment.class);
                    setCurrentSel(UiEventEntry.TAB_RCM_SYS);
                } else if (currentType == UiEventEntry.LRU_6000) {

                    turnToFragmentStack(R.id.detail_layout, LNewSysPamarsFragment.class);
                    setCurrentSel(UiEventEntry.TAB_LRU_NEW_SETTING);
                } else if (currentType == UiEventEntry.LRU_BLE_3300) {
                    Bundle data = new Bundle();
                    data.putBoolean("isBleDevice", true);
                    data.putSerializable("device", bleDevice);
                    turnToFragmentStack(R.id.detail_layout, LNewSysPamarsFragment.class, data);
                }
                updateRight();
                setTitleRightVisible(View.VISIBLE);

                if (setList.size() == 1) {
                    iconImageView.setVisibility(View.GONE);
                } else {
                    iconImageView.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.rtu_search:
                rtuDetail = rtuSearch.getText().toString().trim();
                setTitleName(rtuDetail);

                selRtu(rtuSearch, R.mipmap.rtu_search_sel);
                nolRtu(rtuSetting, R.mipmap.rtu_setting);
                nolRtu(rtuVersion, R.mipmap.rtu_version);


                titleLayout.setVisibility(View.VISIBLE);
                if (searchList.size() > 0) {
                    setTextView.setText(searchList.get(0));
                }
                currentTab = UiEventEntry.TAB_SEARCH;


                if (currentType == UiEventEntry.WRU_2800 || currentType == UiEventEntry.WRU_2801 || currentType == UiEventEntry.WRU_2100) {
                    currentSel = UiEventEntry.TAB_SEARCH_BASIC;
                    SearchFragment fragment = (SearchFragment) FgManager.getFragment(SearchFragment.class);
                    if (!fragment.isVisible()) {
                        turnToFragmentStack(R.id.detail_layout, SearchFragment.class, bundle);
                    }
                } else if (currentType == UiEventEntry.WRU_1901) {
                    turnToFragmentStack(R.id.detail_layout, GroundWaterSearchFragment.class);
                    setCurrentSel(UiEventEntry.TAB_GROUND_WATER_ALL);
                } else if (currentType == UiEventEntry.RTU_2801 || currentType == UiEventEntry.RTU_2800) {
                    turnToFragmentStack(R.id.detail_layout, CommBasicSearchFragment.class);
                    setCurrentSel(UiEventEntry.TAB_COMM_BASIC);
                } else if (currentType == UiEventEntry.LRU_3000) {
                    turnToFragmentStack(R.id.detail_layout, LruSearchFragment.class);
                    setCurrentSel(UiEventEntry.TAB_LRU_SEARCH);
                    titleLayout.setVisibility(View.GONE);
                } else if (currentType == UiEventEntry.RCM_2000) {
                    turnToFragmentStack(R.id.detail_layout, RcmSearchFragment.class);
                    setCurrentSel(UiEventEntry.TAB_RCM_SEARCH);
                } else if (currentType == UiEventEntry.LRU_3200) {
                    turnToFragmentStack(R.id.detail_layout, LSearchFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SEARCH_LRU_BASIC);
                    titleLayout.setVisibility(View.GONE);
                } else if (currentType == UiEventEntry.LRU_6000) {
                    turnToFragmentStack(R.id.detail_layout, LNewSearchFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SEARCH_LRU_NEW);
                    titleLayout.setVisibility(View.GONE);
                } else if (currentType == UiEventEntry.LRU_BLE_3300) {
                    Bundle data = new Bundle();
                    data.putBoolean("isBleDevice", true);
                    data.putSerializable("device", bleDevice);
                    turnToFragmentStack(R.id.detail_layout, BleSearchFragment.class, data);
                    titleLayout.setVisibility(View.GONE);
                }

                updateRight();
                setTitleRightVisible(View.VISIBLE);

                if (searchList.size() == 1) {
                    iconImageView.setVisibility(View.GONE);
                } else {
                    iconImageView.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.rtu_version:
                rtuDetail = rtuVersion.getText().toString().trim();
                setTitleName(rtuDetail);

                selRtu(rtuVersion, R.mipmap.rtu_version_sel);
                nolRtu(rtuSetting, R.mipmap.rtu_setting);
                nolRtu(rtuSearch, R.mipmap.rtu_search);

                titleLayout.setVisibility(View.GONE);

                currentTab = UiEventEntry.TAB_VERSION;
                currentSel = UiEventEntry.TAB_SETTING_VERSION;
                bundle.putBoolean("isBleDevice", true);
                bundle.putSerializable("device", bleDevice);
                turnToFragmentStack(R.id.detail_layout, RTUVersionFragment.class, bundle);

                setTitleRightVisible(View.GONE);
                break;

            case R.id.title_main:
                backMain();
                break;
            case R.id.title_right:
                //刷新或重新连接Socket
                Log.debug(TAG, "update::");
                if (getString(R.string.re_connect).equals(titleRight.getText().toString().trim())) {
                    if (currentType == UiEventEntry.LRU_BLE_3300) {
                        BleUtils.getBle().connect(bleDevice, connectCallback);
                    } else {
                        SocketUtil.getSocketUtil().connectRTU(ConfigParams.IP, ConfigParams.PORT);
                    }
                } else {
                    updateData();
                }
                break;
            case R.id.ll_setting:
                iconImageView.setImageResource(R.mipmap.icon_sel);
                showPopupWindow(setLayout);
                break;
            case R.id.sensor_textview:
                sensorLayout.setVisibility(View.GONE);
                setListView.setVisibility(View.VISIBLE);
                break;

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backMain();
    }

    private void backMain() {
        if (currentType == UiEventEntry.LRU_BLE_3300) {
            this.finish();
        } else {
            SocketUtil.getSocketUtil().closeSocketClient();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(UiEventEntry.NOTIFY_NOWEL, true);
            startActivity(intent);
        }

//        moveTaskToBack(true);
    }


    private void sendData(String content) {
        if (currentType == UiEventEntry.LRU_BLE_3300) {
            BleUtils.getInstance().sendData(bleDevice, content.getBytes());
        } else {
            SocketUtil.getSocketUtil().sendContent(content);
        }
    }

    private void updateData() {

        if (titleRight == null) {
            return;
        }

        if (currentTab == UiEventEntry.TAB_SETTING) {
            setingData();
        } else if (currentTab == UiEventEntry.TAB_SEARCH) {
            searchData();
        } else if (currentTab == UiEventEntry.TAB_VERSION) {

        }
    }

    private void setingData() {
        switch (currentSel) {
            case UiEventEntry.TAB_CHANNEL_SELECT:
                sendData(ConfigParams.ReadCommPara1);
                break;
            case UiEventEntry.TAB_CHANNEL_GPRS:
            case UiEventEntry.TAB_CHANNEL_GMS:
                sendData(ConfigParams.ReadCommPara2);
                break;
            case UiEventEntry.TAB_CHANNEL_BEI:
                sendData(ConfigParams.ReadCommPara3);
                break;
            case UiEventEntry.TAB_CHANNEL_CENTER:
                sendData(ConfigParams.ReadCommPara4);
                break;
            case UiEventEntry.TAB_CHANNEL_YPT:
                sendData(ConfigParams.READYUN);
                break;
            case UiEventEntry.TAB_SETTING_SYS:
                sendData(ConfigParams.ReadSystemPara);
                break;
            case UiEventEntry.TAB_SETTING_VALVA:
                sendData(ConfigParams.ReadSystemPara6);
                break;
            case UiEventEntry.TAB_SETTING_COLLECT:
                sendData(ConfigParams.ReadSystemPara);
                break;
            case UiEventEntry.TAB_SETTING_COMM:
                sendData(ConfigParams.ReadCommPara1);
                break;
            //温度计
            case UiEventEntry.TAB_SENSOR_TEMP:
                sendData(ConfigParams.ReadTemp_SensorPara);
                break;
            case UiEventEntry.TAB_SETTING_CHANNEL:
                if (channelFragment == null) {
                    channelFragment = (ChannelFragment) FgManager.getFragment(ChannelFragment.class);
                }

                if (channelFragment != null) {
                    channelFragment.refreshData();
                }
                break;
            case UiEventEntry.TAB_SENSOR_RAIN:
            case UiEventEntry.TAB_SENSOR_WATER_PARAMS:
                sendData(ConfigParams.ReadSensorPara1);
                break;
            case UiEventEntry.TAB_SENSOR_Water_Quality:
                sendData(ConfigParams.ReadWaterQuality);
                break;
            case UiEventEntry.TAB_SENSOR_Ameeter:
                sendData(ConfigParams.ReadDIANBIAO_SensorPara);
                break;
            case UiEventEntry.TAB_SENSOR_Weather_Param:
                sendData(ConfigParams.ReadWeatherParam);
                break;
            case UiEventEntry.TAB_SENSOR_WATER_PLAN:
            case UiEventEntry.TAB_SENSOR_CAMERA:
            case UiEventEntry.TAB_SENSOR_ATHER:
            case UiEventEntry.TAB_SENSOR_PRESS:
            case UiEventEntry.TAB_SETTING_VERSION:
                sendData(ConfigParams.ReadSensorPara2);
                break;

            case UiEventEntry.TAB_SENSOR_SQ:
                sendData(ConfigParams.ReadMoisture_SensorPara);
                break;
            case UiEventEntry.TAB_SENSOR_AQ:
                sendData(ConfigParams.ReadAna_SensorPara);
                break;
            case UiEventEntry.TAB_SENSOR_FLOW:
                sendData(ConfigParams.ReadSensorPara2);
                break;

            case UiEventEntry.TAB_SETTING_AD:
                if (adFragment == null) {
                    adFragment = (ADFragment) FgManager.getFragment(ADFragment.class);
                }
                if (adFragment.isVisible()) {
                    adFragment.setData();
                }
                break;
            default:
                break;
        }
    }

    private void searchData() {
        if (currentType == UiEventEntry.WRU_2800 || currentType == UiEventEntry.WRU_2801 || currentType == UiEventEntry.WRU_2100) {
            switch (currentSel) {
                case UiEventEntry.TAB_SEARCH_BASIC:
                    if (gprsFragment == null) {
                        gprsFragment = (SearchFragment) FgManager.getFragment(SearchFragment.class);
                    }
                    if (gprsFragment != null && gprsFragment.isVisible()) {
                        gprsFragment.setData();
                    }
                    break;
                case UiEventEntry.TAB_SEARCH_GPRS:
                case UiEventEntry.TAB_SEARCH_CAMERA:
                    if (currentSel == UiEventEntry.TAB_SEARCH_CAMERA) {
                        sendData(ConfigParams.SENDPIC);
                        sendData(ConfigParams.SEND2PIC);

                    }
                    if (gprsFragment == null) {
                        gprsFragment = (SearchFragment) FgManager.getFragment(SearchFragment.class);
                    }
                    if (gprsFragment != null && gprsFragment.isVisible()) {
                        gprsFragment.updateData();
                    }
                    break;

                case UiEventEntry.TAB_SEARCH_SENSOR:
                    if (gprsFragment == null) {
                        gprsFragment = (SearchFragment) FgManager.getFragment(SearchFragment.class);
                    }
                    if (gprsFragment.isVisible()) {
                        gprsFragment.setData();
                    }
                    break;
                case UiEventEntry.TAB_SEARCH_SIOL:
                    if (soilSearchFragment == null) {
                        soilSearchFragment = (SoilSearchFragment) FgManager.getFragment(SoilSearchFragment.class);
                    }
                    if (soilSearchFragment.isVisible()) {
                        soilSearchFragment.setData();
                    }
                    break;
                case UiEventEntry.TAB_SEARCH_WQ:
                    if (wQualitySearchFragment == null) {
                        wQualitySearchFragment = (WQualitySearchFragment) FgManager.getFragment(WQualitySearchFragment.class);
                    }
                    if (wQualitySearchFragment.isVisible()) {
                        wQualitySearchFragment.setData();
                    }
                    break;
                case UiEventEntry.TAB_SEARCH_Ammeter:
                    if (ammeterSearchFragment == null) {
                        ammeterSearchFragment = (AmmeterSearchFragment) FgManager.getFragment(WQualitySearchFragment.class);
                    }
                    if (ammeterSearchFragment.isVisible()) {
                        ammeterSearchFragment.setData();
                    }
                    break;
                case UiEventEntry.TAB_SEARCH_READ_IMAGE:
                    ProgressBarUtil.showProgressDialog(HomeActivity.this, "", getString(R.string.Receiving_pictures));
                    SocketUtil.getSocketUtil().startReceImage();
                    sendData(ConfigParams.ReadImage + "000");
                    break;
                case UiEventEntry.TAB_SEARCH_RADATA:
                    break;
            }
        } else if (currentType == UiEventEntry.LRU_3200) {
            if (currentSel == UiEventEntry.TAB_SETTING_SYS) {//全部参数  常规设置
                sendData(ConfigParams.ReadNetCfg);
            } else if (currentSel == UiEventEntry.TAB_SEARCH_LRU_BASIC) {

                if (lSearchFragment == null) {
                    lSearchFragment = (LSearchFragment) FgManager.getFragment(LSearchFragment.class);
                }
                if (lSearchFragment != null && lSearchFragment.isVisible()) {
                    lSearchFragment.setData();
                }
            }
        } else if (currentType == UiEventEntry.WRU_1901) {
            GroundServerFragment serverFragment = null;
            GroundWaterSearchFragment gprsFragment = null;
            if (getString(R.string.collect_ad_lv).equals(titleRight.getText().toString().trim())) {
                sendData(ConfigParams.ReadBattery);
            } else {

                if (currentSel == UiEventEntry.TAB_GROUND_WATER_ALL) {//全部参数  常规设置
                    sendData(ConfigParams.ReadData);
                    if (gprsFragment == null) {
                        gprsFragment = (GroundWaterSearchFragment) FgManager.getFragment(GroundWaterSearchFragment.class);
                    }
                    if (gprsFragment != null && gprsFragment.isVisible()) {
                        gprsFragment.setData();
                    }
                } else if (currentSel == UiEventEntry.TAB_GROUND_WATER_BASIC) {//服务器设置
                    sendData(ConfigParams.ReadData);

                } else if (currentSel == UiEventEntry.TAB_GROUND_WATER_SERVER) {//服务器设置
                    if (serverFragment == null) {
                        serverFragment = (GroundServerFragment) FgManager.getFragment(GroundServerFragment.class);
                    }
                    if (serverFragment != null && serverFragment.isVisible()) {
                        serverFragment.initData();
                    }
                }
            }

        } else if (currentType == UiEventEntry.RTU_2800 || currentType == UiEventEntry.RTU_2801) {
            if (currentSel == UiEventEntry.TAB_COMM_SYSTEM) {//全部参数  常规设置
                sendData(ConfigParams.ReadParameter);
            } else if (currentSel == UiEventEntry.TAB_COMM_COMM) {
                sendData(ConfigParams.ReadStaticIP);

            } else if (currentSel == UiEventEntry.TAB_COMM_BASIC) {
                if (commFragment == null) {
                    commFragment = (CommBasicSearchFragment) FgManager.getFragment(CommBasicSearchFragment.class);
                }
                if (commFragment != null && commFragment.isVisible()) {
                    commFragment.setData();
                }
            }
        } else if (currentType == UiEventEntry.LRU_6000) {
            if (titleRight == null) {
                return;
            }
            if (currentSel == UiEventEntry.TAB_LRU_NEW_SETTING) {//全部参数  常规设置
                sendData(ConfigParams.ReadParameter);
            } else if (currentSel == UiEventEntry.TAB_LRU_NEW_CHANNEL_SETTING) {
                sendData(ConfigParams.ReadStaticIP);

            } else if (currentSel == UiEventEntry.TAB_SEARCH_LRU_NEW) {
                if (lNewSearchFragment == null) {
                    lNewSearchFragment = (LNewSearchFragment) FgManager.getFragment(LNewSearchFragment.class);
                }
                if (lNewSearchFragment != null && lNewSearchFragment.isVisible()) {
                    lNewSearchFragment.setData();
                }
            }
        } else if (currentType == UiEventEntry.LRU_3000) {
            if (currentSel == UiEventEntry.TAB_LRU_SYS) {//全部参数  常规设置
                sendData(ConfigParams.ReadParameters);
            } else if (currentSel == UiEventEntry.TAB_LRU_SEARCH) {

                if (LruSearch == null) {
                    LruSearch = (LruSearchFragment) FgManager.getFragment(LruSearchFragment.class);
                }
                if (LruSearch != null && LruSearch.isVisible()) {
                    LruSearch.setData();
                }
            }
        } else if (currentType == UiEventEntry.RCM_2000) {
            if (currentSel == UiEventEntry.TAB_RCM_SYS) {//全部参数  常规设置
                sendData(ConfigParams.ReadData);
            } else if (currentSel == UiEventEntry.TAB_RCM_SEARCH) {

                if (rcmSearch == null) {
                    rcmSearch = (RcmSearchFragment) FgManager.getFragment(RcmSearchFragment.class);
                }
                if (rcmSearch != null && rcmSearch.isVisible()) {
                    rcmSearch.setData();
                }
            } else if (currentSel == UiEventEntry.TAB_RCM_FUN_SYS) {//全部参数  常规设置
                sendData(ConfigParams.ReadFunctionData);
            } else if (currentSel == UiEventEntry.TAB_RCM_YUN) {//云台设置
                sendData(ConfigParams.ReadYUNStatus);
            } else if (currentSel == UiEventEntry.TAB_RCM_FUN_SEARCH) {

                if (rcmFunSearch == null) {
                    rcmFunSearch = (RcmFunSearchFragment) FgManager.getFragment(RcmFunSearchFragment.class);
                }
                if (rcmFunSearch != null && rcmFunSearch.isVisible()) {
                    rcmFunSearch.setData();
                }
            } else {//RCM传感器更新
                switch (currentSel) {
                    case UiEventEntry.TAB_SENSOR_RAIN:
                    case UiEventEntry.TAB_SENSOR_WATER_PARAMS:
                        sendData(ConfigParams.ReadSensorPara1);
                        break;
                    case UiEventEntry.TAB_SENSOR_WATER_PLAN:
                        sendData(ConfigParams.ReadSensorPara2);
                        break;
                }

            }
        } else if (currentType == UiEventEntry.LRU_BLE_3300) {
            BleSearchFragment bleSearchFragment = (BleSearchFragment) FgManager.getFragment(BleSearchFragment.class);
            if (bleSearchFragment != null && bleSearchFragment.isVisible()) {
                bleSearchFragment.setData();
            }
        }
    }

    /*连接的回调*/
    private BleConnCallback<BleDevice> connectCallback = new BleConnCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(final BleDevice device) {
            if (device.isConnected()) {
                /*连接成功后，设置通知*/
            }
            ToastUtil.showLong(getApplicationContext(), "连接成功");
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            ToastUtil.showLong(getApplicationContext(), "连接异常，异常状态码:" + errorCode);
        }
    };


    private void initSearchData() {
        if (currentType == UiEventEntry.WRU_2800 || currentType == UiEventEntry.WRU_2801 || currentType == UiEventEntry.WRU_2100) {
            searchList.add(getString(R.string.basic_search));
            searchList.add(getString(R.string.comm_status_search));
            searchList.add(getString(R.string.camera_search));
            searchList.add(getString(R.string.sensor_status_search));
            searchList.add(getString(R.string.read_image));
            searchList.add(getString(R.string.historical_data));
            searchList.add(getString(R.string.Soil_inquiry));
            searchList.add(getString(R.string.Water_quality_inquiry));
            searchList.add(getString(R.string.Electricity_meter_inquiry));
        } else if (currentType == UiEventEntry.WRU_1901) {
            searchList.add(getString(R.string.groundwater_all));
        } else if (currentType == UiEventEntry.RTU_2800 || currentType == UiEventEntry.RTU_2801) {
            searchList.add(getString(R.string.Basic_display));
        } else if (currentType == UiEventEntry.RCM_2000) {
            searchList.add(getString(R.string.System_board_parameter));
            searchList.add(getString(R.string.Function_board_parameter));

        }

    }

    View contentView = null;

    public void showPopupWindow(View anchor) {

        if (popupWindow == null) {
            contentView = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.windows_popupwindow, null);
            popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            setListView = (ListView) contentView.findViewById(R.id.set_listview);
            setToListView = (ListView) contentView.findViewById(R.id.set_to_listview);
            sensorTextView = (TextView) contentView.findViewById(R.id.sensor_textview);
            sensorLayout = (LinearLayout) contentView.findViewById(R.id.sensor_layout);
            sensorTextView.setOnClickListener(this);

            popupWindow.setOnDismissListener(this);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new PaintDrawable());
        }

        if (currentTab == UiEventEntry.TAB_SEARCH) {
            setAdapter = new CustomAdapter(getApplicationContext(), searchList);
            sensorLayout.setVisibility(View.GONE);
            setListView.setVisibility(View.VISIBLE);
        } else {
            setAdapter = new CustomAdapter(getApplicationContext(), setList);
        }
        setListView.setAdapter(setAdapter);

        if (currentType == UiEventEntry.WRU_2800 || currentType == UiEventEntry.WRU_2801 || currentType == UiEventEntry.WRU_2100) {//RTU
            handRTU(setListView);
        } else if (currentType == UiEventEntry.LRU_3200) {
//            handRTU1(setListView);
        } else if (currentType == UiEventEntry.WRU_1901) {//地下水
            handGroundWater(setListView);
        } else if (currentType == UiEventEntry.RTU_2800 || currentType == UiEventEntry.RTU_2801) {//通用RTU
            handCommRTU(setListView);
        } else if (currentType == UiEventEntry.RCM_2000) {//一体化摄像头
            handRcmRTU(setListView);
        } else if (currentType == UiEventEntry.LRU_6000) {//一体化摄像头
            handLRURTU(setListView);
        } else if (currentType == UiEventEntry.LRU_BLE_3300) {//蓝牙
            handBLERTU(setListView);
        }


        if (Build.VERSION.SDK_INT < 24) {
            popupWindow.showAsDropDown(anchor);
        } else {
            int[] location = calculatePopWindowPos(anchor, contentView);
//            int measuredHeight = anchor.getMeasuredHeight();
//            anchor.getLocationOnScreen(location);
            // 适配 android 7.0
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1]);
        }

    }

    private static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (UiUtils.height - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = UiUtils.width - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = UiUtils.width - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }


    private void handCommRTU(final ListView setListView) {
        setListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentTab == UiEventEntry.TAB_SEARCH) {
                } else {
                    tabName = setList.get(i);
                    if (i == 0) {//系统参数设置
                        turnToFragmentStack(R.id.detail_layout, CommRtuSysFragment.class);
                        setCurrentSel(UiEventEntry.TAB_COMM_SYSTEM);
                        setTitleRightVisible(View.VISIBLE);
                    } else if (i == 1) {//通讯参数
                        turnToFragmentStack(R.id.detail_layout, CommRtuChannelFragment.class);
                        setCurrentSel(UiEventEntry.TAB_COMM_COMM);
                        setTitleRightVisible(View.VISIBLE);
                    } else if (i == 2) {//传感器设置
                        setTitleRightVisible(View.GONE);
                        turnToFragmentStack(R.id.detail_layout, CommSensorFragment.class);
                        setCurrentSel(UiEventEntry.TAB_COMM_SENSOR);
                    } else if (i == 3) {//传感器设置
                        setTitleRightVisible(View.GONE);
                        turnToFragmentStack(R.id.detail_layout, ADFragment.class);
                        setCurrentSel(UiEventEntry.TAB_SETTING_AD);
                    }
                }

                setTextView.setText(tabName);
                popupWindow.dismiss();

            }
        });
    }

    private void handLRURTU(final ListView setListView) {
        setListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentTab == UiEventEntry.TAB_SEARCH) {
                } else {
                    tabName = setList.get(i);
                    if (i == 0) {//系统参数设置
                        turnToFragmentStack(R.id.detail_layout, LNewSysPamarsFragment.class);
                        setCurrentSel(UiEventEntry.TAB_LRU_NEW_SETTING);
                        setTitleRightVisible(View.VISIBLE);
                    } else if (i == 1) {//通讯参数
                        dialogEditText1();
                        setTitleRightVisible(View.VISIBLE);
                    }

                }

                setTextView.setText(tabName);
                popupWindow.dismiss();

            }
        });
    }

    private void handBLERTU(final ListView setListView) {
        setListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentTab == UiEventEntry.TAB_SEARCH) {
                } else {
                    tabName = setList.get(i);
                    if (i == 0) {//系统参数设置
                        Bundle data = new Bundle();
                        data.putBoolean("isBleDevice", true);
                        data.putSerializable("device", bleDevice);
                        turnToFragmentStack(R.id.detail_layout, LNewSysPamarsFragment.class, data);
                        setCurrentSel(UiEventEntry.TAB_LRU_NEW_SETTING);
                        setTitleRightVisible(View.VISIBLE);
                        popupWindow.dismiss();
                    } else if (sensorLayout.getVisibility() == View.GONE && i == 1) {//传感器
                        handSensorBLELRU();
                    }
                }
                setTextView.setText(tabName);
            }
        });
    }

    private void handRcmRTU(final ListView setListView) {
//        if ( setListView != null)
        setListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentTab == UiEventEntry.TAB_SEARCH) {
                    tabName = searchList.get(i);
                    if (i == 0) {//系统板参数
                        turnToFragmentStack(R.id.detail_layout, RcmSearchFragment.class);
                        setCurrentSel(UiEventEntry.TAB_RCM_SEARCH);
                    } else if (i == 1) {//功能板参数
                        turnToFragmentStack(R.id.detail_layout, RcmFunSearchFragment.class);
                        setCurrentSel(UiEventEntry.TAB_RCM_FUN_SEARCH);
                    } else if (i == 2) {//功能参数
                        turnToFragmentStack(R.id.detail_layout, YUNFragment.class);
                        setCurrentSel(UiEventEntry.TAB_RCM_FUN_SEARCH);
                    }
                } else {
                    tabName = setList.get(i);
                    if (i == 0) {//系统板参数设置
                        turnToFragmentStack(R.id.detail_layout, RcmSysPamarsFragment.class);
                        setCurrentSel(UiEventEntry.TAB_RCM_SYS);
                    } else if (i == 1) {//功能板参数
                        turnToFragmentStack(R.id.detail_layout, RcmFunPamarsFragment.class);
                        setCurrentSel(UiEventEntry.TAB_RCM_FUN_SYS);
                    } else if (i == 2) {//云台参数
                        turnToFragmentStack(R.id.detail_layout, YUNFragment.class);
                        setCurrentSel(UiEventEntry.TAB_RCM_YUN);
                    } else if (i == 3) {
                        setTitleRightVisible(View.VISIBLE);

                        if (sensorLayout.getVisibility() == View.GONE) {
                            //传感器设置
                            handSensorRCM();
                        }

                    }
                }
                setTextView.setText(tabName);
                if (i != 3) {

                    popupWindow.dismiss();
                }

            }

        });

    }

    private void handGroundWater(final ListView setListView) {
        setListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentTab == UiEventEntry.TAB_SEARCH) {
                } else {
                    tabName = setList.get(i);
                    setTitleRight(getString(R.string.update));
                    if (i == 0) {//常规设置
                        turnToFragmentStack(R.id.detail_layout, GroundWaterBasicFragment.class);
                        setCurrentSel(UiEventEntry.TAB_GROUND_WATER_BASIC);
                    } else if (i == 1) {//服务器
                        turnToFragmentStack(R.id.detail_layout, GroundServerFragment.class);
                        setCurrentSel(UiEventEntry.TAB_GROUND_WATER_SERVER);
                    } else if (i == 2) {//AD设置
                        setTitleRight(getString(R.string.collect_ad_lv));
                        turnToFragmentStack(R.id.detail_layout, GroundADFragment.class);
                        setCurrentSel(UiEventEntry.TAB_GROUND_WATER_AD);
                    } else if (i == 3) {
                        turnToFragmentStack(R.id.detail_layout, CollectFragment.class);
                        setCurrentSel(UiEventEntry.TAB_SETTING_COLLECT);
                    }
                }

                setTextView.setText(tabName);
                popupWindow.dismiss();

            }
        });
    }

    private void handSensor() {
        sensorTextView.setText(getString(R.string.sensor_setting));
        sensorLayout.setVisibility(View.VISIBLE);
        setListView.setVisibility(View.GONE);
        setToAdapter = new CustomToAdapter(getApplicationContext(),
                setToList);
        setToListView.setAdapter(setToAdapter);
        setToListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                if (position == UiEventEntry.NOTIFY_SENSOR_RAIN) {
                    turnToFragmentStack(R.id.detail_layout, RainPamarsFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_RAIN);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_WATER_PARAMS) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_WATER_PARAMS);
                    turnToFragmentStack(R.id.detail_layout, WaterPamarsFragment.class);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_WATER_PLAN) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_WATER_PLAN);
                    turnToFragmentStack(R.id.detail_layout, WaterPlanFragment.class);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_CAMERA) {
                    turnToFragmentStack(R.id.detail_layout, CameraFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_CAMERA);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_SQ) {
                    turnToFragmentStack(R.id.detail_layout, SQFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_SQ);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_FLOW) {
                    turnToFragmentStack(R.id.detail_layout, FlowFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_FLOW);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_ZW) {
                    turnToFragmentStack(R.id.detail_layout, ZWFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_ZW);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_ATHER) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_ATHER);
                    turnToFragmentStack(R.id.detail_layout, AtherPamarsFragment.class);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_Water_Quality) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_Water_Quality);
                    turnToFragmentStack(R.id.detail_layout, WaterQualityFragment.class);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_Weather_Param) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_Weather_Param);
                    turnToFragmentStack(R.id.detail_layout, WeatherParamFragment.class);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_AQ) {
                    turnToFragmentStack(R.id.detail_layout, AnalogQuantityFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_AQ);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_Ammeter) {
                    turnToFragmentStack(R.id.detail_layout, AmmeterFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_Ameeter);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_PRESS) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_PRESS);
                    turnToFragmentStack(R.id.detail_layout, PressFragment.class);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_TEMP) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_TEMP);
                    turnToFragmentStack(R.id.detail_layout, TempFragment.class);
                }

                popupWindow.dismiss();
                tabName = setToList.get(position);
            }
        });
    }

    private void handSensorBLELRU() {
        sensorTextView.setText(getString(R.string.sensor_setting));
        sensorLayout.setVisibility(View.VISIBLE);
        setListView.setVisibility(View.GONE);
        setToAdapter = new CustomToAdapter(getApplicationContext(),
                setToLruList);
        setToListView.setAdapter(setToAdapter);
        setToListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                Bundle data = new Bundle();
                data.putBoolean("isBleDevice", true);
                data.putSerializable("device", bleDevice);
                if (position == 0) {
                    turnToFragmentStack(R.id.detail_layout, WaterPlanFragment.class, data);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_WATER_PLAN);
                } else if (position == 1) {
                    turnToFragmentStack(R.id.detail_layout, SQFragment.class, data);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_SQ);
                } else if (position == 2) {
                    turnToFragmentStack(R.id.detail_layout, ZWFragment.class, data);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_ZW);
                }
                popupWindow.dismiss();
                tabName = setToLruList.get(position);
            }
        });
    }


    private void handSensorRCM() {
        sensorTextView.setText(getString(R.string.sensor_setting));
        sensorLayout.setVisibility(View.VISIBLE);
        setListView.setVisibility(View.GONE);
        setToAdapter = new CustomToAdapter(getApplicationContext(),
                setToRcmList);
        setToListView.setAdapter(setToAdapter);
        setToListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                if (position == UiEventEntry.NOTIFY_SENSOR_RAIN) {
                    turnToFragmentStack(R.id.detail_layout, RainPamarsFragment.class);
                    setCurrentSel(UiEventEntry.TAB_SENSOR_RAIN);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_WATER_PARAMS) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_WATER_PARAMS);
                    turnToFragmentStack(R.id.detail_layout, WaterPamarsFragment.class);
                } else if (position == UiEventEntry.NOTIFY_SENSOR_WATER_PLAN) {
                    setCurrentSel(UiEventEntry.TAB_SENSOR_WATER_PLAN);
                    turnToFragmentStack(R.id.detail_layout, WaterPlanFragment.class);
                }
                popupWindow.dismiss();
                tabName = setToRcmList.get(position);
            }
        });
    }

    private void handChannel() {
        sensorTextView.setText(getString(R.string.channel_setting));
        sensorLayout.setVisibility(View.VISIBLE);
        setListView.setVisibility(View.GONE);
        setToAdapter = new CustomToAdapter(getApplicationContext(),
                setToChannelList);
        setToListView.setAdapter(setToAdapter);
        setToListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                if (position == UiEventEntry.NOTIFY_CHANNEL_SELECT) {
                    turnToFragmentStack(R.id.detail_layout, ChannelSelectFragment.class);
                    setCurrentSel(UiEventEntry.TAB_CHANNEL_SELECT);
                } else if (position == UiEventEntry.NOTIFY_CHANNEL_GPRS) {
                    setCurrentSel(UiEventEntry.TAB_CHANNEL_GPRS);
                    turnToFragmentStack(R.id.detail_layout, ChannelGPRSFragment.class);
                } else if (position == UiEventEntry.NOTIFY_CHANNEL_GMS) {
                    setCurrentSel(UiEventEntry.TAB_CHANNEL_GMS);
                    turnToFragmentStack(R.id.detail_layout, ChannelGMSFragment.class);
                } else if (position == UiEventEntry.NOTIFY_CHANNEL_BEI) {
                    turnToFragmentStack(R.id.detail_layout, ChannelBEIFragment.class);
                    setCurrentSel(UiEventEntry.TAB_CHANNEL_BEI);
                } else if (position == UiEventEntry.NOTIFY_CHANNEL_CENTER) {
                    turnToFragmentStack(R.id.detail_layout, ChannelCENTERFragment.class);
                    setCurrentSel(UiEventEntry.TAB_CHANNEL_CENTER);
                } else if (position == UiEventEntry.NOTIFY_CHANNEL_YPT) {
//                    turnToFragmentStack(R.id.detail_layout, ChannelCENTERFragment.class);
//                    ViewDialogFragment viewDialogFragment = new ViewDialogFragment();
//                    viewDialogFragment.show(getFragmentManager());
                    sensorTextView.setText(getString(R.string.channel_setting));
                    sensorLayout.setVisibility(View.VISIBLE);
                    setListView.setVisibility(View.GONE);
                    setToAdapter = new CustomToAdapter(getApplicationContext(),
                            setToChannelList);
                    setToListView.setAdapter(setToAdapter);
                    dialogEditText();
                }
                popupWindow.dismiss();
                tabName = setToChannelList.get(position);
                setTextView.setText(tabName);
            }
        });
    }

    private void handRTU(final ListView setListView) {
        if (setListView != null) {
            setListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if (currentTab == UiEventEntry.TAB_SEARCH) {
                        tabName = searchList.get(position);

                        if (position == UiEventEntry.NOTIFY_SEARCH_RDDATA) {
                            Bundle bundle1 = new Bundle();
                            bundle1.putInt(UiEventEntry.CURRENT_RTU_NAME, currentType);
                            setCurrentSel(UiEventEntry.TAB_SEARCH_RADATA);
                            turnToFragmentStack(R.id.detail_layout, SearchDataFragment.class, bundle1);
                        } else {
                            Bundle bundle = new Bundle();
                            if (gprsFragment == null) {
                                gprsFragment = (SearchFragment) FgManager.getFragment(SearchFragment.class);
                            }
                            if (gprsFragment != null && gprsFragment.isVisible()) {
                                gprsFragment.stopUpdate();
                            }

                            if (position == UiEventEntry.NOTIFY_SEARCH_BASIC) {
                                setCurrentSel(UiEventEntry.TAB_SEARCH_BASIC);
                                bundle.putInt(UiEventEntry.CURRENT_SEARCH, UiEventEntry.TAB_SEARCH_BASIC);
                                turnToFragmentStack(R.id.detail_layout, SearchFragment.class, bundle);
                            } else if (position == UiEventEntry.NOTIFY_SEARCH_GPRS) {
                                setCurrentSel(UiEventEntry.TAB_SEARCH_GPRS);
                                bundle.putInt(UiEventEntry.CURRENT_SEARCH, UiEventEntry.TAB_SEARCH_GPRS);
                                turnToFragmentStack(R.id.detail_layout, SearchFragment.class, bundle);
                            } else if (position == UiEventEntry.NOTIFY_SEARCH_CAMERA) {
                                bundle.putInt(UiEventEntry.CURRENT_SEARCH, UiEventEntry.TAB_SEARCH_CAMERA);
                                setCurrentSel(UiEventEntry.TAB_SEARCH_CAMERA);
                                turnToFragmentStack(R.id.detail_layout, SearchFragment.class, bundle);
                            } else if (position == UiEventEntry.NOTIFY_SEARCH_SENSOR) {
                                bundle.putInt(UiEventEntry.CURRENT_SEARCH, UiEventEntry.TAB_SEARCH_SENSOR);
                                setCurrentSel(UiEventEntry.TAB_SEARCH_SENSOR);
                                turnToFragmentStack(R.id.detail_layout, SearchFragment.class, bundle);
                            } else if (position == UiEventEntry.NOTIFY_SEARCH_READ_IMAGE) {
                                bundle.putInt(UiEventEntry.CURRENT_SEARCH, UiEventEntry.TAB_SEARCH_READ_IMAGE);
                                setCurrentSel(UiEventEntry.TAB_SEARCH_READ_IMAGE);
                                turnToFragmentStack(R.id.detail_layout, SearchFragment.class, bundle);
                            } else if (position == UiEventEntry.NOTIFY_SEARCH_BASIC_SOIL) {
                                setCurrentSel(UiEventEntry.TAB_SEARCH_SIOL);
                                turnToFragmentStack(R.id.detail_layout, SoilSearchFragment.class, bundle);
                            } else if (position == UiEventEntry.NOTIFY_SEARCH_BASIC_WQUALITY) {
                                setCurrentSel(UiEventEntry.TAB_SEARCH_WQ);
                                turnToFragmentStack(R.id.detail_layout, WQualitySearchFragment.class, bundle);
                            } else if (position == UiEventEntry.NOTIFY_SEARCH_BASIC_AMMETER) {
                                setCurrentSel(UiEventEntry.TAB_SEARCH_Ammeter);
                                turnToFragmentStack(R.id.detail_layout, AmmeterSearchFragment.class, bundle);
                            }
                            updateRight();
                        }

                        setTextView.setText(tabName);
                        popupWindow.dismiss();
                    } else {
                        tabName = setList.get(position);
                        if (parent.getAdapter() instanceof CustomAdapter) {
                            setTitleRightVisible(View.VISIBLE);
                            if (sensorLayout.getVisibility() == View.GONE && position == 4) {//传感器设置
                                handSensor();
                            } else if ((sensorLayout.getVisibility() == View.GONE && position == 3) && (currentType == UiEventEntry.WRU_2800 || currentType == UiEventEntry.WRU_2100)) {//信道设置
                                handChannel();
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putInt(UiEventEntry.CURRENT_RTU_NAME, currentType);
                                setTitleRight(getString(R.string.update));
                                if (position == UiEventEntry.NOTIFY_SYSTEM_PAMARS) {
                                    setCurrentSel(UiEventEntry.TAB_SETTING_SYS);
                                    turnToFragmentStack(R.id.detail_layout, SystemPamarsFragment.class, bundle);
                                } else if (position == UiEventEntry.NOTIFY_COLLECT) {
                                    turnToFragmentStack(R.id.detail_layout, CollectFragment.class, bundle);
                                    setCurrentSel(UiEventEntry.TAB_SETTING_COLLECT);
                                } else if (position == UiEventEntry.NOTIFY_COMM_PAMARS) {
                                    turnToFragmentStack(R.id.detail_layout, CommParamsFragment.class, bundle);
                                    setCurrentSel(UiEventEntry.TAB_SETTING_COMM);
                                } else if (position == UiEventEntry.NOTIFY_CHANNEL_PAMARS) {

                                    setCurrentSel(UiEventEntry.TAB_SETTING_CHANNEL);
                                    turnToFragmentStack(R.id.detail_layout, ChannelFragment.class, bundle);
                                } else if (position == UiEventEntry.NOTIFY_AD_PAMARS) {
                                    setCurrentSel(UiEventEntry.TAB_SETTING_AD);
                                    turnToFragmentStack(R.id.detail_layout, ADFragment.class, bundle);
                                } else if (position == UiEventEntry.NOTIFY_VR_PAMARS) {
                                    setCurrentSel(UiEventEntry.TAB_SETTING_VALVA);
                                    turnToFragmentStack(R.id.detail_layout, ValveControlRelayFragment.class, bundle);
                                }
                                popupWindow.dismiss();
                            }
                        }
                        setTextView.setText(tabName);
                    }

                }
            });
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onDismiss() {
        iconImageView.setImageResource(R.mipmap.icon_default);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == UiEventEntry.READ_RESULT_OK) {
            String result = "";
            if (args[1] != null) {
                result = (String) args[1];
            }
            if (!TextUtils.isEmpty(result)) {
                if (result.equals(ConfigParams.RESETALL)) {
                    ToastUtil.showToastLong(getString(R.string.device_returned_factory) + getString(R.string.Please_click) + "'" + getString(R.string.Save_settings_and_restart) + "'" + getString(R.string.Button_restart_device));
                } else if (result.equals(ConfigParams.ResetUnit) || result.equals(ConfigParams.RESETALL) || result.equals(ConfigParams.RESETUNIT)) {
                    ToastUtil.showToastLong(getString(R.string.device_is_restarting));
                } else if (result.equals(ConfigParams.RESETALL10)) {
                    ToastUtil.showToastLong(getString(R.string.five_minutes));
                } else {
                    ToastUtil.showToastLong(getString(R.string.Set_successfully));
                }
            }

        } else if (id == UiEventEntry.CONNCT_AGAIN || id == UiEventEntry.CONNCT_FAIL) {
            setTitleRight(getString(R.string.re_connect));
            if (gprsFragment != null && gprsFragment.isVisible()) {
                gprsFragment.stopUpdate();
            }
            if (lSearchFragment != null && lSearchFragment.isVisible()) {
                lSearchFragment.stopUpdate();
            }
        } else if (id == UiEventEntry.CONNCT_OK) {
            updateRight();
        } else if (id == UiEventEntry.READ_RESULT_ERROR) {
            ToastUtil.showToastLong(getString(R.string.Set_error));
        }
    }

    private void updateRight() {
        setTitleRightVisible(View.VISIBLE);
        if (currentSel == UiEventEntry.TAB_SEARCH_GPRS) {
            setTitleRight(getString(R.string.comm_test));

        } else if (currentSel == UiEventEntry.TAB_SEARCH_CAMERA) {
            setTitleRight(getString(R.string.send_img));
        } else if (currentSel == UiEventEntry.TAB_GROUND_WATER_AD) {
            setTitleRight(getString(R.string.collect_ad_lv));
        } else if (currentSel == UiEventEntry.TAB_SEARCH_READ_IMAGE) {
            setTitleRight(getString(R.string.read_image));
        } else if (currentSel == UiEventEntry.TAB_SEARCH_RADATA) {
            setTitleRight(getString(R.string.Receive_historical_data));
            setTitleRightVisible(View.GONE);
        } else {
            boolean isConnect = false;
            if (currentType == UiEventEntry.LRU_BLE_3300) {
                isConnect = bleDevice != null && bleDevice.isConnected() ? true : false;
            } else {
                isConnect = SocketUtil.getSocketUtil().isConnected();
            }

            if (isConnect) {
                setTitleRight(getString(R.string.update));
            } else {
                setTitleRight(getString(R.string.re_connect));
            }
        }
    }

    @Override
    public void onClick(String userName, String password) {
        Toast.makeText(HomeActivity.this, getString(R.string.username) + userName + getString(R.string.passWord) + password, Toast.LENGTH_SHORT).show();
    }

    private void dialogEditText() {
        final EditText editText = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, 3);
        builder.setTitle(getString(R.string.password1));

//        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(editText);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(HomeActivity.this, editText.getText().toString() + "", Toast.LENGTH_LONG).show();
//                handChannel();
                if (editText.getText().toString().equals("9527")) {
                    turnToFragmentStack(R.id.detail_layout, YPTFragment.class);
                    setCurrentSel(UiEventEntry.TAB_CHANNEL_YPT);
                } else {
                    ToastUtil.showToastLong(getString(R.string.Password_error));
                    turnToFragmentStack(R.id.detail_layout, ChannelSelectFragment.class);
                    setCurrentSel(UiEventEntry.TAB_CHANNEL_SELECT);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                turnToFragmentStack(R.id.detail_layout, ChannelSelectFragment.class);
                setCurrentSel(UiEventEntry.TAB_CHANNEL_SELECT);
            }
        });
        builder.create().show();
    }

    private void dialogEditText1() {
        final EditText editText = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, 3);
        builder.setTitle(getString(R.string.password1));

//        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(editText);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(HomeActivity.this, editText.getText().toString() + "", Toast.LENGTH_LONG).show();
//                handChannel();
                if (editText.getText().toString().equals("9527")) {
                    turnToFragmentStack(R.id.detail_layout, YPTFragment.class);
                    setCurrentSel(UiEventEntry.TAB_LRU_NEW_CHANNEL_SETTING);
                } else {
                    ToastUtil.showToastLong(getString(R.string.Password_error));
                    turnToFragmentStack(R.id.detail_layout, LNewSysPamarsFragment.class);
                    setCurrentSel(UiEventEntry.TAB_LRU_NEW_SETTING);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                turnToFragmentStack(R.id.detail_layout, LNewSysPamarsFragment.class);
                setCurrentSel(UiEventEntry.TAB_LRU_NEW_SETTING);
            }
        });
        builder.create().show();
    }

}
