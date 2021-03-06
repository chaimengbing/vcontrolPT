package com.vcontrol.vcontroliot.act;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.model.DaoMaster;
import com.vcontrol.vcontroliot.model.DaoSession;
import com.vcontrol.vcontroliot.model.Message;
import com.vcontrol.vcontroliot.model.MessageDao;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by apple on 2017/6/14.
 * 推送的弹窗
 */

public class JPushDialogActivity extends AppCompatActivity {

    //收到的消息
    @BindView(R.id.dialog_message)
    TextView dialogMessage;
    //取消
    @BindView(R.id.dialog_cancel)
    TextView dialogCancel;
    //保存
    @BindView(R.id.dialog_yes)
    TextView dialogYes;
    //数据库
    MessageDao messageDao;
    //接收数据
    String message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_jpush_message);
        ButterKnife.bind(this);
        initDbHelp();
        initView();

    }

    private void initView() {
        if (getIntent().hasExtra("MESSAGE"))
        message = getIntent().getStringExtra("MESSAGE");
        dialogMessage.setText(getString(R.string.Received_push)+message);
    }

    /*初始化数据库相关*/
    private void initDbHelp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(VcontrolApplication.getInstance(), "recluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        messageDao = daoSession.getMessageDao();
    }

    @OnClick({R.id.dialog_message, R.id.dialog_cancel, R.id.dialog_yes})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialog_message:
                break;
            case R.id.dialog_cancel:
                finish();
                break;
            case R.id.dialog_yes:
                Intent intent = new Intent(this, MessageActivity.class);
                SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy.MM.dd   HH:mm:ss");
                Date curDate =  new Date(System.currentTimeMillis());
                //获取当前时间
                String   str   =   formatter.format(curDate);
                messageDao.insert(new Message(null, getString(R.string.Manually) + str, message,null));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }
}
