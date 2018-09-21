package com.vcontrol.vcontroliot.act;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.fragment.ClientFragment;
import com.vcontrol.vcontroliot.fragment.EquipmentFragment;
import com.vcontrol.vcontroliot.fragment.SystemFragment;
import com.vcontrol.vcontroliot.jpushdemo.ExampleUtil;
import com.vcontrol.vcontroliot.jpushdemo.LocalBroadcastManager;
import com.vcontrol.vcontroliot.util.DensityUtil;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;
import com.vcontrol.vcontroliot.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends BaseActivity implements EventNotifyHelper.NotificationCenterDelegate, AdapterView.OnItemClickListener
{
    private static final String TAG = MainActivity.class.getName();

    private RadioGroup radioGroup;
    private RadioButton radioEquipment,radioSystem;
    private Button buttonClient;

    private Fragment fragment_system;
    private Fragment fragment_equipment;
    private Fragment fragment_client;

    private List<Fragment> list;
    private FrameLayout frameLayout;
    private long exitTime = 0;
    public static boolean isForeground = false;
    private FragmentManager fragmentManager;


    protected com.readystatesoftware.systembartint.SystemBarTintManager tintManager;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }

    @Override
    public int getLayoutView()
    {
        return R.layout.activity_main;
    }

    @Override
    public void initViewData()
    {
        showToolbar();
        setTitleftVisible(View.GONE);
        setTitleName(getString(R.string.customer_service));
        initView();
        setPrimary();
        init();
        registerMessageReceiver();

        Display display = getWindowManager().getDefaultDisplay();
        System.out.println("vivo----width-display :" + display.getWidth());
        System.out.println("vivo----heigth-display :" + display.getHeight());

    }

    /**
     * 设置主题颜色
     */
    public void setPrimary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);

            tintManager = new com.readystatesoftware.systembartint.SystemBarTintManager(MainActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.biaoti);
        }
    }

    /**
     * 控制主题显示
     */
    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = this.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init(){
        JPushInterface.init(getApplicationContext());
    }



    public void initView()
    {

        //创建Fragment对象及集合
        fragment_client=new ClientFragment();
        fragment_equipment=new EquipmentFragment();
        fragment_system=new SystemFragment();

        //将Fragment对象添加到list中
        list=new ArrayList<>();
        list.add(fragment_client);
        list.add(fragment_equipment);
        list.add(fragment_system);
        //设置RadioGroup开始时设置的按钮，设置第一个按钮为默认值


        //初始时向容器中添加第一个Fragment对象
        addFragment(fragment_client);
        buttonClient.setSelected(true);

    }

    //向Activity中添加Fragment的方法
    public void addFragment(Fragment fragment) {

        //获得Fragment管理器
        fragmentManager = getSupportFragmentManager();
        //使用管理器开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //使用事务替换Fragment容器中Fragment对象
        fragmentTransaction.replace(R.id.framelayout,fragment);
        //提交事务，否则事务不生效
        fragmentTransaction.commit();
    }

//    public void showFragment(int index) {
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//
//        // 想要显示一个fragment,先隐藏全部fragment。防止重叠
//        hideFragments(ft);
//
//        switch (index) {
//            case 1:
//                // 假设fragment1已经存在则将其显示出来
//                if (fragment_client != null)
//                    ft.show(fragment_client);
//                    // 否则是第一次切换则加入fragment1，注意加入后是会显示出来的。replace方法也是先remove后add
//                else {
//                    fragment_client = new ClientFragment();
//                    ft.add(R.id.framelayout, fragment_client);
//                }
//                break;
//            case 2:
//                if (fragment_equipment != null)
//                    ft.show(fragment_equipment);
//                else {
//                    fragment_equipment = new EquipmentFragment();
//                    ft.add(R.id.framelayout, fragment_equipment);
//                }
//                break;
//            case 3:
//                if (fragment_system != null)
//                    ft.show(fragment_system);
//                else {
//                    fragment_system = new SystemFragment();
//                    ft.add(R.id.framelayout, fragment_system);
//                }
//                break;
//        }
//        ft.commit();
//    }
//
//    // 当fragment已被实例化，就隐藏起来
//    public void hideFragments(FragmentTransaction ft) {
//        if (fragment_client != null)
//            ft.hide(fragment_client);
//        if (fragment_equipment != null)
//            ft.hide(fragment_equipment);
//        if (frs\ != null)
//            ft.hide(fragment3);
//        if (fragment4 != null)
//            ft.hide(fragment4);
//    }



    @Override
    public void initComponentViews()
    {
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.CONNCT_OK);
        EventNotifyHelper.getInstance().addObserver(this,UiEventEntry.CONNCT_FAIL);
        VcontrolApplication.getInstance().addActivity(this);
        ToastUtil.setCurrentContext(getApplication());
        UiUtils.height  = DensityUtil.getDisplayHeightPixels(this);
        UiUtils.width = DensityUtil.getDisplayWidthPixels(this);

        com.vcontrol.vcontroliot.log.Log.info(TAG,"width:" + UiUtils.width + ",height：" + UiUtils.height);

        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        //找到四个按钮
        radioEquipment= (RadioButton) findViewById(R.id.radio_equipment);
        radioSystem= (RadioButton) findViewById(R.id.radio_system);
        buttonClient = (Button) findViewById(R.id.client);

        setListener();

    }

    private void setListener()
    {
        //设置按钮点击监听
        radioSystem.setOnClickListener(this);
        radioEquipment.setOnClickListener(this);
        buttonClient.setOnClickListener(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 按下返回键退出APP
     */
    private void exitApp()
    {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000)
        {
            ToastUtil.showToastLong(getString(R.string.Press_again_to_exit_the_program));
            exitTime = System.currentTimeMillis();
        }
        else
        {
            SocketUtil.closeSocket();
            VcontrolApplication.getInstance().exit();

            finish();
            System.exit(0);
        }
    }



    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        return false;
    }

    @Override
    public void onClick(View v)
    {

        //我们根据参数的id区别不同按钮
        //不同按钮对应着不同的Fragment对象页面
        switch (v.getId()){
            case R.id.radio_system:
                addFragment(fragment_system);
                buttonClient.setSelected(false);
                setTitleName(radioSystem.getText().toString());
                break;
            case R.id.radio_equipment:
                addFragment(fragment_equipment);
                buttonClient.setSelected(false);
                setTitleName(radioEquipment.getText().toString());
                break;
            case R.id.client:
                addFragment(fragment_client);
                buttonClient.setSelected(true);
                radioEquipment.setChecked(false);
                radioSystem.setChecked(false);
                setTitleName(getString(R.string.customer_service));
                break;
            default:
                break;
        }

    }


    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.vcontrol.vcontroliot.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
//                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e){
            }
        }
    }



    @Override
    public void didReceivedNotification(int id, Object... args)
    {

    }
}
