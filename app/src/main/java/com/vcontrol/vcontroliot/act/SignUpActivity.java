package com.vcontrol.vcontroliot.act;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.jpushdemo.MyReceiver;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.RegexUtils;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.SystemBarTintManager;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;
import com.vcontrol.vcontroliot.util.UrlConstance;
import com.vcontrol.vcontroliot.view.CleanEditText;

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

import cn.jpush.android.api.JPushInterface;


/**
 * Created by linxi on 2017/11/9.
 */

public class SignUpActivity extends BaseActivity implements EventNotifyHelper.NotificationCenterDelegate,AdapterView.OnItemClickListener,UrlConstance
{
    protected SystemBarTintManager tintManager;

    private static final String TAG = "SignUpActivity";
    private String url1="http://47.104.107.184/vcontrolCloud/RegisterMobile?";//服务器接口地址

    // 界面控件
    private CleanEditText phoneEdit;
    private CleanEditText passwordEdit;
    private CleanEditText userNameEdit;
    private CleanEditText companyEdit;
    private CleanEditText projectEdit;
    private CleanEditText ispassworEdit;
    private CleanEditText emilEdit;
    private Button createAccountButton;
    private Button cleanButton1;
    private Button cleanButton2;
    private Button cleanButton3;
    private Button cleanButton4;
    private Button cleanButton5;
    private Button cleanButton6;
    private Button cleanButton7;


    public static String phoneString = "";
    String result = "";

    public SignUpActivity()
    {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }

    @Override
    public int getLayoutView()
    {
        return R.layout.signup_fragment;
    }

    @Override
    public void initViewData()
    {
        showToolbar();
        setTitleMain("");
        setTitleName(getString(R.string.registered));
        setPrimary();

    }

    /**
     * 设置主题颜色
     */
    public void setPrimary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);

            tintManager = new SystemBarTintManager(this);
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



    private void initViews() {

        cleanButton1 = (Button) findViewById(R.id.clean_one);
        cleanButton1.setOnClickListener(this);
        cleanButton2 = (Button) findViewById(R.id.clean_two);
        cleanButton2.setOnClickListener(this);
        cleanButton3 = (Button) findViewById(R.id.clean_three);
        cleanButton3.setOnClickListener(this);
        cleanButton4 = (Button) findViewById(R.id.clean_four);
        cleanButton4.setOnClickListener(this);
        cleanButton5 = (Button) findViewById(R.id.clean_five);
        cleanButton5.setOnClickListener(this);
        cleanButton6 = (Button) findViewById(R.id.clean_six);
        cleanButton6.setOnClickListener(this);
        cleanButton7 = (Button) findViewById(R.id.clean_seven);
        cleanButton7.setOnClickListener(this);

        createAccountButton = getView(R.id.btn_create_account);
        createAccountButton.setOnClickListener(this);// 下一步
        userNameEdit = getView(R.id.et_email_phone);
        emilEdit = getView(R.id.et_phone);
        phoneEdit = getView(R.id.et_emil);
        companyEdit = getView(R.id.et_company);
        projectEdit = getView(R.id.et_project);
        passwordEdit = getView(R.id.et_password);
        ispassworEdit = getView(R.id.et_ispassword);
        ispassworEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    try {
                        commit();
                    } catch (IOException | JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                return false;
            }
        });


    }

    private void commit() throws IOException, JSONException
    {
        final String phone = phoneEdit.getText().toString().trim();
        final String userName = userNameEdit.getText().toString().trim();
        final String company = companyEdit.getText().toString().trim();
        final String project = projectEdit.getText().toString().trim();
        final String emil = emilEdit.getText().toString().trim();
        final String password = passwordEdit.getText().toString().trim();
        final String ispassword = ispassworEdit.getText().toString().trim();

        final String regID;
        if (!MyReceiver.regID.equals(""))
        {
            regID = MyReceiver.regID;
        }
        else {
            regID = VcontrolApplication.getRegistrationId();
        }
        Log.d(TAG,phone);
        Log.d(TAG,userName);
        Log.d(TAG,company);
        Log.d(TAG,project);
        TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        phoneString = phone;

        final String imei = mTm.getDeviceId();
        String rid = JPushInterface.getRegistrationID(getApplicationContext());

        if (checkInput(emil,project,userName,company,phone,password,ispassword) == true)
        {
            createAccountButton.setOnClickListener(new View.OnClickListener()
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
                        NameValuePair pair2 = new BasicNameValuePair("mobileID", regID);
                        NameValuePair pair3 = new BasicNameValuePair("email",emil);
                        NameValuePair pair4 = new BasicNameValuePair("companyName",company);
                        NameValuePair pair5 = new BasicNameValuePair("userName",userName);
                        NameValuePair pair6 = new BasicNameValuePair("projectName",project);
                        NameValuePair pair7 = new BasicNameValuePair("passWord",password);
                        Log.d(TAG,phone);

                        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
                        pairList.add(pair1);
                        pairList.add(pair2);
                        pairList.add(pair3);
                        pairList.add(pair4);
                        pairList.add(pair5);
                        pairList.add(pair6);
                        pairList.add(pair7);
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
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }



                        }
                    }.start();
//
                }
            });


        }
    }
    /**
     * 显示响应结果到命令行和TextView
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
            hanlder.obtainMessage(0,result1).sendToTarget();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private Handler hanlder=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String qq= (String) msg.obj;
                    Log.d(TAG,qq);
                    if (qq.equals("注册成功"))
                    {
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    ToastUtil.showToastLong(qq);

                    break;
            }
            super.handleMessage(msg);
        }
    };


private boolean checkInput(String emil, String project, String userName, String company, String phone,String password,String ispassword) {
        if (TextUtils.isEmpty(phone)) { // 电话号码为空
            ToastUtil.showShort(this, R.string.tip_phone_can_not_be_empty);
        } else if (!RegexUtils.checkMobile(phone)) { // 电话号码格式有误
                ToastUtil.showShort(this, R.string.tip_phone_regex_not_right);
            }  else if (password == null || password.trim().equals("")) {
                Toast.makeText(this, R.string.tip_password_can_not_be_empty,
                        Toast.LENGTH_LONG).show();
            }else if (password.length() < 6 || password.length() > 10
                    || TextUtils.isEmpty(password)) { // 密码格式
                ToastUtil.showShort(this,
                        R.string.tip_please_input_6_32_password);
            }else if (ispassword == null || ispassword.trim().equals("")){
                Toast.makeText(this, R.string.tip_password_can_not_be_empty,
                        Toast.LENGTH_LONG).show();
            }else if (ispassword.length() < 6 || ispassword.length() > 10
                    || TextUtils.isEmpty(ispassword)) { // 密码格式
                ToastUtil.showShort(this,
                        R.string.tip_please_input_6_32_password);
            }else if (!password.trim().equals(ispassword.trim())){
            ToastUtil.showShort(this, getString(R.string.Two_input_match));
            }else if (TextUtils.isEmpty(userName)){
                ToastUtil.showShort(this, getString(R.string.User_name_empty));
            }
//            else if (TextUtils.isEmpty(company)){
//                ToastUtil.showShort(this, getString(R.string.company_empty));
//            }
            else if (TextUtils.isEmpty(emil))
            {
                ToastUtil.showShort(this, getString(R.string.Email_empty));
            }
            else if (!RegexUtils.checkEmail(emil))
            {
                ToastUtil.showShort(this,getString(R.string.Bad_mailbox_format));
            }
            else  {
                return true;
            }
        return false;
    }




    private void backMain()
    {
        SocketUtil.getSocketUtil().closeSocketClient();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(UiEventEntry.NOTIFY_NOWEL, true);
        startActivity(intent);
    }
    @Override
    public void initComponentViews()
    {
        initViews();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        return false;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_main:
                backMain();
                break;
            case R.id.btn_create_account:
                try {
                    commit();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.clean_one:
                userNameEdit.setText("");
                break;
            case R.id.clean_two:
                companyEdit.setText("");
                break;
            case R.id.clean_three:
                phoneEdit.setText("");
                break;
            case R.id.clean_four:
                projectEdit.setText("");
                break;
            case R.id.clean_five:
                passwordEdit.setText("");
                break;
            case R.id.clean_six:
                ispassworEdit.setText("");
                break;
            case R.id.clean_seven:
                emilEdit.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args)
    {

    }
}
