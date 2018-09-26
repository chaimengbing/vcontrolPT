package com.vcontrol.vcontroliot.act;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.callback.BaseAutoLayoutActivity;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.model.LoginCheckBean;
import com.vcontrol.vcontroliot.service.AdPresenterImpl;
import com.vcontrol.vcontroliot.util.AdContract;
import com.vcontrol.vcontroliot.util.NetUtils;
import com.vcontrol.vcontroliot.util.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by zone on 2016/7/18.
 */
public class AdActivity extends BaseAutoLayoutActivity implements AdContract.View {
    @BindView(R.id.tv_second)
    TextView tvSecond;
    @BindView(R.id.layout_skip)
    LinearLayout layoutSkip;
    int timeCount = 0;
    boolean continueCount = true;
    @BindView(R.id.iv_advertising)
    ImageView ivAdvertising;
    private LoginCheckBean loginCheckBean;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            goBleView();
//            countNum();
//            if (continueCount) {
//                handler.sendMessageDelayed(handler.obtainMessage(-1), 1000);
//            }
        }
    };

    private void goBleView() {
        Intent intent = new Intent(AdActivity.this, BleDeviceListActivity.class);
        startActivity(intent);
        finish();
    }

    private AdPresenterImpl pAd;
    private int initTimeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ad);
        ButterKnife.bind(this);
        layoutSkip.setVisibility(View.INVISIBLE);
        handler.sendMessageDelayed(handler.obtainMessage(-1), 1000);
    }


    private void getImage() {
        pAd = new AdPresenterImpl();
        pAd.attachView(this);
        initTimeCount = 6;
        loginCheckBean = new LoginCheckBean();
        if (NetUtils.isConnected(AdActivity.this)) {
            pAd.getLoginCheck();
        }
        handler.sendMessageDelayed(handler.obtainMessage(-1), 1000);
    }


    @OnClick({R.id.iv_advertising, R.id.layout_skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_advertising:
                String url = (String) SpUtils.get(this, "adUrl", "http://www.wvcontrol.com.cn/index.php?c=article&a=type&tid=139");
                if (!url.equals("null")) {
                    continueCount = false;
                    Intent intent = new Intent(AdActivity.this, WebViewActivity.class);
                    intent.putExtra("title", "");
                    intent.putExtra("url", url);
                    intent.putExtra("from", "advertising");
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.layout_skip:
                continueCount = false;
                toNextActivity();
                finish();
                break;
        }
    }


    private int countNum() {//数秒
        timeCount++;
        if (timeCount == 3) {//数秒，超过3秒后如果没有网络，则进入下一个页面
            if (!NetUtils.isConnected(AdActivity.this)) {
                continueCount = false;
                toNextActivity();
                finish();
            }
            if (!loginCheckBean.isPlayAd()) {//如果服务端不允许播放广告，则直接进入下一个页面
                continueCount = false;
                toNextActivity();
                finish();
            } else {//如果播放，则获取本地的图片
                ivAdvertising.setVisibility(View.VISIBLE);
                layoutSkip.setVisibility(View.VISIBLE);
            }
        }
        if (timeCount == initTimeCount) {
            continueCount = false;
            toNextActivity();
            finish();
        }
        return timeCount;
    }

    public void toNextActivity() {//根据是否保存有 token 判断去登录界面还是主界面
        Intent intent = null;
        String token = (String) SpUtils.get(AdActivity.this, "token", "");
        if (TextUtils.isEmpty(token)) {
            intent = new Intent(AdActivity.this, MainActivity.class);
        } else {
            intent = new Intent(AdActivity.this, MainActivity.class);
            VcontrolApplication.setToken(token);
        }
        startActivity(intent);
        finish();
    }


    @Override
    public void setAdTime(int count) {
        initTimeCount = count + 3;
    }

    @Override
    public void setLoginCheckBean(LoginCheckBean loginCheckBean) {
        this.loginCheckBean = loginCheckBean;
    }

    @Override
    public void setAdImg(Bitmap bitmap) {
        if (bitmap != null) {
            ivAdvertising.setImageBitmap(bitmap);
        } else {//加强用户体验，如果是获取到的bitmap为null，则直接跳过
            continueCount = false;
            toNextActivity();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pAd != null) {
            pAd.detachView();
        }
    }
}

