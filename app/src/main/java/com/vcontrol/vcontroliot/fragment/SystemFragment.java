package com.vcontrol.vcontroliot.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.act.LoginActivity;
import com.vcontrol.vcontroliot.model.UpDataInfo;
import com.vcontrol.vcontroliot.util.CheckVersion;
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
 */

public class SystemFragment extends Fragment implements View.OnClickListener
{
    public static final String TAG = SystemFragment.class.getSimpleName();
    private String url1 = "http://47.104.107.184/vcontrolCloud/UserMobileLoginOut?";//服务器接口地址

    private TextView appVers;
    private TextView version;
    private TextView btnDownload;
    private TextView appNewVersion;

    private static Switch aSwitch;
    private static Switch aSwitch2;
    private static Switch aSwitch3;
    private static Switch aSwitch4;
    private static Switch aSwitch5;
    private static Switch aSwitch6;
    private static Switch aSwitch7;
    private static Switch aSwitch8;
    private static Switch aSwitch9;
    private static Switch aSwitch10;
    public TextView phoneString = null;
    private Button dropOut;
    public static LinearLayout dropLayout;
    public static String spKeyUser = "UserName";
    private String spName = "userInfo";
    private SharedPreferences spUser = null;


    private EquipmentFragment equipmentFragment;
    public static Button loginButton;
    private UpDataInfo upDataInfo;

    public static String mainOne = "1";
    public static String mainTwo = "1";
    public static String mainThree = "1";
    public static String mainFour = "1";
    public static String mainFive = "1";
    public static String mainSix = "1";
    public static String mainSeven = "1";
    public static String mainEight = "1";
    public static String mainNine = "1";
    public static String mainTen = "1";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_system, container, false);
        initView(view);

        return view;
    }

    private void initView(View view)
    {
        spUser = getActivity().getSharedPreferences(spName, Context.MODE_PRIVATE);
        appVers = (TextView) view.findViewById(R.id.app_ver);
        version = (TextView) view.findViewById(R.id.version);
        appNewVersion = (TextView) view.findViewById(R.id.version1);
        aSwitch = (Switch) view.findViewById(R.id.switch_button1);
        aSwitch2 = (Switch) view.findViewById(R.id.switch_button2);
        aSwitch3 = (Switch) view.findViewById(R.id.switch_button3);
        aSwitch4 = (Switch) view.findViewById(R.id.switch_button4);
        aSwitch5 = (Switch) view.findViewById(R.id.switch_button5);
        aSwitch6 = (Switch) view.findViewById(R.id.switch_button6);
        aSwitch7 = (Switch) view.findViewById(R.id.switch_button7);
        aSwitch8 = (Switch) view.findViewById(R.id.switch_button8);
        aSwitch9 = (Switch) view.findViewById(R.id.switch_button9);
        aSwitch10 = (Switch) view.findViewById(R.id.switch_button10);
        loginButton = (Button) view.findViewById(R.id.login_button);
        btnDownload = (TextView) view.findViewById(R.id.app_ver1);
        phoneString = (TextView) view.findViewById(R.id.phone_string);
        dropOut = (Button) view.findViewById(R.id.drop_out);
        dropLayout = (LinearLayout) view.findViewById(R.id.layout_image3);

        version.setText(getAppVersion());
        appNewVersion.setText(getAppVersion1());
        aSwitch.setOnClickListener(this);
        aSwitch2.setOnClickListener(this);
        aSwitch3.setOnClickListener(this);
        aSwitch4.setOnClickListener(this);
        aSwitch5.setOnClickListener(this);
        aSwitch6.setOnClickListener(this);
        aSwitch7.setOnClickListener(this);
        aSwitch8.setOnClickListener(this);
        aSwitch9.setOnClickListener(this);
        aSwitch10.setOnClickListener(this);
        dropOut.setOnClickListener(this);

        loginButton.setOnClickListener(this);
        setLister();
        setLister2();
        setLister3();
        setLister4();
        setLister5();
        setLister6();
        setLister7();
        setLister8();
        setLister9();
        setLister10();

        if (LoginActivity.loginString != 0)
        {
            dropLayout.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }
        else
        {
            dropLayout.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }


//        if (!phoneString.getText().toString().equals(""))
//        {
//            phone = phoneString.getText().toString();
//        }else {
//            phoneString.setText(phone);
//        }
    }

    public static String setLister()
    {
        if (aSwitch != null)
        {

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainOne = "0";
                        }
                        else
                        {
                            mainOne = "1";

                        }
                    }
                }

            });
        }


        return mainOne;
    }

    public static String setLister2()
    {
        if (aSwitch2 != null)
        {
            aSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch2.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainTwo = "0";
                        }
                        else
                        {
                            mainTwo = "1";

                        }
                    }
                }

            });
        }

        return mainTwo;

    }

    public static String setLister3()
    {
        if (aSwitch3 != null)
        {
            aSwitch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch3.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainThree = "0";
                        }
                        else
                        {
                            mainThree = "1";

                        }
                    }
                }

            });
        }

        return mainThree;
    }

    public static String setLister4()
    {
        if (aSwitch4 != null)
        {
            aSwitch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch4.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainFour = "0";
                        }
                        else
                        {
                            mainFour = "1";

                        }
                    }
                }

            });
        }

        return mainFour;
    }

    public static String setLister5()
    {
        if (aSwitch5 != null)
        {
            aSwitch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch5.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainFive = "0";
                        }
                        else
                        {
                            mainFive = "1";

                        }
                    }
                }

            });
        }

        return mainFive;
    }

    public static String setLister6()
    {
        if (aSwitch6 != null)
        {
            aSwitch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch6.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainSix = "0";
                        }
                        else
                        {
                            mainSix = "1";

                        }
                    }
                }

            });
        }

        return mainSix;
    }

    public static String setLister7()
    {
        if (aSwitch7 != null)
        {
            aSwitch7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch7.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainSeven = "0";
                        }
                        else
                        {
                            mainSeven = "1";

                        }
                    }
                }

            });
        }

        return mainSeven;
    }

    public static String setLister8()
    {
        if (aSwitch8 != null)
        {
            aSwitch8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch8.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainEight = "0";
                        }
                        else
                        {
                            mainEight = "1";

                        }
                    }
                }

            });
        }

        return mainEight;
    }

    public static String setLister9()
    {
        if (aSwitch9 != null)
        {
            aSwitch9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch9.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainNine = "0";
                        }
                        else
                        {
                            mainNine = "1";

                        }
                    }
                }

            });
        }

        return mainNine;
    }

    public static String setLister10()
    {
        if (aSwitch10 != null)
        {
            aSwitch10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (aSwitch10.isPressed())
                    {

                        if (!isChecked)
                        {
                            mainTen = "0";
                        }
                        else
                        {
                            mainTen = "1";

                        }
                    }
                }

            });
        }

        return mainTen;
    }
    private void setDropLayout()
    {

    }


    private String getAppVersion()
    {
        String versionName = null;

        try
        {
            versionName = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0).versionName;
        } catch (Exception e)
        {

        }
        return versionName;
    }

    private String getAppVersion1()
    {
        String versionName = null;

        try
        {
//            versionName = getActivity().getPackageManager().getPackageInfo(
//                    getActivity().getPackageName(), 0).;
            versionName = upDataInfo.versionName;
        } catch (Exception e)
        {

        }
        return versionName;
    }

    private void commit() throws IOException, JSONException
    {
        final String phone = phoneString.getText().toString().trim();


        Log.d(TAG, phone);

        dropOut.setOnClickListener(new View.OnClickListener()
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
                        Log.d(TAG, phone);

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

            com.vcontrol.vcontroliot.log.Log.info(TAG, phoneString.getText().toString());
            switch (msg.what)
            {
                case 0:
                    String qq = (String) msg.obj;
                    Log.d(TAG, qq);
                    if (qq.equals("注销成功"))
                    {
                        SystemFragment.loginButton.setVisibility(View.VISIBLE);
                        dropLayout.setVisibility(View.GONE);
                        ToastUtil.showToastLong(getString(R.string.Exited));
                        LoginActivity.loginString = 0;
                    }
                    else
                    {
                        ToastUtil.showToastLong(getString(R.string.Failed_exit));
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onResume()
    {
        phoneString.setText(SharedPreferencesUtil.getStringData(getActivity(), SharedPreferencesUtil.USERNAME, ""));
        super.onResume();
    }


    @Override
    public void onClick(View v)
    {
        String userid = phoneString.getText().toString().trim();
        SharedPreferences.Editor editor = spUser.edit();
        editor.putString(spKeyUser, userid);
        editor.commit();
        switch (v.getId())
        {
            case R.id.login_button:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.drop_out:
                try
                {
                    commit();
                } catch (IOException | JSONException e)
                {
                    e.printStackTrace();
                }
                break;
        }

    }

    private View.OnClickListener listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.app_ver1:
                    //在这里做检查业务逻辑
                    CheckVersion checkVersion = new CheckVersion();
                    new Thread(checkVersion).start();
                    break;
            }
        }
    };
}
