package com.vcontrol.vcontroliot.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.act.MessageActivity;
import com.vcontrol.vcontroliot.act.ProjectListActivity;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.model.DaoMaster;
import com.vcontrol.vcontroliot.model.DaoSession;
import com.vcontrol.vcontroliot.model.TipButton;
import com.vcontrol.vcontroliot.model.User;
import com.vcontrol.vcontroliot.model.UserDao;
import com.vcontrol.vcontroliot.util.SharedPreferencesUtil;
import com.vcontrol.vcontroliot.util.ToastUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by linxi on 2017/10/13.
 *
 */

public class ClientFragment extends Fragment implements View.OnClickListener
{
    private String url1 = "http://47.104.107.184/vcontrolCloud/getCheckedProject?";//服务器接口地址
    private static String TAG = ClientFragment.class.getName();

    private LinearLayout layoutOne;
    private LinearLayout layoutTow;
    private ImageButton imageButtonDown;
    private ImageButton imageButtonUp;
    public static LinearLayout layoutThree;
    public static LinearLayout layoutFour;
    private ImageButton imageButtonDown2;
    private ImageButton imageButtonUp2;

    private RadioButton valve_control;
    private RadioButton valve_control2;
    private RadioButton camera;
    private RadioButton camera2;
    private RadioButton gate;
    private RadioButton gate2;
    private RadioButton Other_platforms;

    private RadioButton yun;
    private RadioButton yun2;
    private RadioButton shouhou;
    private RadioButton message;
    private RadioButton message2;
    private RadioButton guanwang;
    private RadioButton guanwang2;
    private RadioButton weixin;
    private RadioButton weixin2;
    private RadioButton other;
    private RadioButton project;

    UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_client, container, false);
        initView(view);
        initDbHelp();

        return view;
    }


    public void initView(View view)
    {

        layoutOne = (LinearLayout) view.findViewById(R.id.layout_button1);
        layoutTow = (LinearLayout) view.findViewById(R.id.layout_button2);
        imageButtonUp = (ImageButton) view.findViewById(R.id.image_up);
        imageButtonDown = (ImageButton) view.findViewById(R.id.image_down);
        layoutThree = (LinearLayout) view.findViewById(R.id.layout_button3);
        layoutFour = (LinearLayout) view.findViewById(R.id.layout_button4);
        imageButtonUp2 = (ImageButton) view.findViewById(R.id.image_up2);
        imageButtonDown2 = (ImageButton) view.findViewById(R.id.image_down2);
        valve_control = (RadioButton) view.findViewById(R.id.valve_control);
        valve_control2 = (RadioButton) view.findViewById(R.id.valve_control2);
        camera = (RadioButton) view.findViewById(R.id.camera);
        camera2 = (RadioButton) view.findViewById(R.id.camera2);
        gate = (RadioButton) view.findViewById(R.id.gate);
        gate2 = (RadioButton) view.findViewById(R.id.gate2);
        Other_platforms = (RadioButton) view.findViewById(R.id.Other_platforms);
        yun = (RadioButton) view.findViewById(R.id.yun);
        yun2 = (RadioButton) view.findViewById(R.id.yun2);
        guanwang = (RadioButton) view.findViewById(R.id.guanwang);
        guanwang2 = (RadioButton) view.findViewById(R.id.guanwang2);
        shouhou = (RadioButton) view.findViewById(R.id.shouhou);
        message = (RadioButton) view.findViewById(R.id.message);
        message2 = (RadioButton) view.findViewById(R.id.message2);
        weixin = (RadioButton) view.findViewById(R.id.weixin);
        weixin2 = (RadioButton) view.findViewById(R.id.weixin2);
        other = (RadioButton) view.findViewById(R.id.other);
        project = (RadioButton) view.findViewById(R.id.project_list);


        imageButtonDown.setOnClickListener(this);
        imageButtonUp.setOnClickListener(this);
        imageButtonDown2.setOnClickListener(this);
        imageButtonUp2.setOnClickListener(this);
        valve_control.setOnClickListener(this);
        valve_control2.setOnClickListener(this);
        camera.setOnClickListener(this);
        camera2.setOnClickListener(this);
        gate.setOnClickListener(this);
        gate2.setOnClickListener(this);
        Other_platforms.setOnClickListener(this);
        yun.setOnClickListener(this);
        yun2.setOnClickListener(this);
        shouhou.setOnClickListener(this);
        message.setOnClickListener(this);
        message2.setOnClickListener(this);
        guanwang.setOnClickListener(this);
        guanwang2.setOnClickListener(this);
        weixin.setOnClickListener(this);
        weixin2.setOnClickListener(this);
        other.setOnClickListener(this);
        project.setOnClickListener(this);

    }

    String phone;

    private void commit() throws IOException, JSONException
    {
        phone = SharedPreferencesUtil.getStringData(getActivity(), SharedPreferencesUtil.USERNAME, "");


//        Log.d(TAG,phone);

        project.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /**
                 * 开辟一个子线程访问网络，否则会抛出异常
                 */
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        NameValuePair pair1 = new BasicNameValuePair("mobileNum", phone);
                        startActivity(new Intent(getActivity(), ProjectListActivity.class));

                        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
                        pairList.add(pair1);
                        try
                        {

                            HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
                            // URl是接口地址
                            HttpPost httpPost = new HttpPost(url1);
                            // 将请求体内容加入请求中
                            httpPost.setEntity(requestHttpEntity);
                            // 需要客户端对象来发送请求
                            HttpClient httpClient = new DefaultHttpClient();

                            // 发送请求
                            HttpResponse response = httpClient.execute(httpPost);
                            // 显示响应
                            showResponseResult(response);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                }.start();
//
            }
        });


    }

    /**
     * 显示响应结果到命令行和TextView
     *
     * @param response
     */
    private void showResponseResult(HttpResponse response)
    {
        if (null == response)
        {
            return;
        }

        HttpEntity httpEntity = response.getEntity();
        try
        {
            InputStream inputStream = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            String result1 = "";
            String line = "";
            while (null != (line = reader.readLine()))
            {
                result1 += line;

            }

            System.out.println(result1);
            /**
             * 把服务器返回的结果 发送到hanlder中，在子线程中是不允许更新ui的
             */
            hanlder.obtainMessage(0, result1).sendToTarget();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private Handler hanlder = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    String qq = (String) msg.obj;
                    if (qq.equals("手机号不能为空"))
                    {
                        ToastUtil.showToastLong(getString(R.string.Phone_number_empty));
                    }
                    else
                    {
                        if(qq!=null)
                        {
                        userDao.deleteAll(); //删除数据
                        userDao.insert(new User(null, qq));//插入新数据
                        Log.info(TAG, qq);
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /*初始化数据库相关*/
    private void initDbHelp()
    {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(VcontrolApplication.getInstance(), "recluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.image_down:
                layoutOne.setVisibility(View.GONE);
                layoutTow.setVisibility(View.VISIBLE);
                break;
            case R.id.image_up:
                layoutTow.setVisibility(View.GONE);
                layoutOne.setVisibility(View.VISIBLE);
                break;
            case R.id.image_down2:
                layoutThree.setVisibility(View.GONE);
                layoutFour.setVisibility(View.VISIBLE);
                break;
            case R.id.image_up2:
                layoutFour.setVisibility(View.GONE);
                layoutThree.setVisibility(View.VISIBLE);
                break;
            case R.id.valve_control2:
                getWeb("http://47.94.21.209:8080/valve");
                break;
            case R.id.valve_control:
                getWeb("http://47.94.21.209:8080/valve");
                break;
            case R.id.gate:
                getWeb("http://222.128.104.199:15009/sluiceGate/index");
                break;
            case R.id.gate2:
                getWeb("http://222.128.104.199:15009/sluiceGate/index");
                break;
            case R.id.camera:
                getWeb("http://222.128.104.199:15009/vidicon/login");
                break;
            case R.id.camera2:
                getWeb("http://222.128.104.199:15009/vidicon/login");
                break;
            case R.id.Other_platforms:
//                getWeb("http://www.wvcontrol.com.cn/");
                getWeb("http://www.swyq.cn/");
                break;
            case R.id.yun:
                getWeb("http://47.104.107.184");
                break;
            case R.id.yun2:
                getWeb("http://47.104.107.184");
                break;
            case R.id.shouhou:
//                getWeb("http://www.wvcontrol.com.cn/");
                getWeb("http://www.swyq.cn/");
                break;
            case R.id.message:
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                TipButton.mTipOn = false;
                break;
            case R.id.message2:
                Intent intent2 = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent2);
                TipButton.mTipOn = false;
                break;
            case R.id.guanwang:
//                getWeb("http://www.wvcontrol.com.cn/");
                getWeb("http://www.swyq.cn/");
                break;
            case R.id.guanwang2:
//                getWeb("http://www.wvcontrol.com.cn/");
                getWeb("http://www.swyq.cn/");
                break;
            case R.id.weixin:
//                getWeb("http://www.wvcontrol.com.cn/");
                getWeb("http://www.swyq.cn/");
                break;
            case R.id.weixin2:
//                getWeb("http://www.wvcontrol.com.cn/");
                getWeb("http://www.swyq.cn/");
                break;
            case R.id.other:
//                getWeb("http://www.wvcontrol.com.cn/");
                getWeb("http://www.swyq.cn/");
                break;
            case R.id.project_list:
                try
                {
                    commit();
                    if (phone != null)
                    {
                        project.setChecked(true);
                    }
                } catch (IOException | JSONException e)
                {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }

    }

    public void getWeb(String uri)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

}
