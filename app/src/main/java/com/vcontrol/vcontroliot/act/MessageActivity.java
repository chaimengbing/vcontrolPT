package com.vcontrol.vcontroliot.act;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.adapter.ItemTVAdapter;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.model.DaoMaster;
import com.vcontrol.vcontroliot.model.DaoSession;
import com.vcontrol.vcontroliot.model.Message;
import com.vcontrol.vcontroliot.model.MessageDao;
import com.vcontrol.vcontroliot.util.SystemBarTintManager;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Wu on 2017/5/10 0010 下午 1:30.
 * 描述：消息列表
 */
public class MessageActivity extends AppCompatActivity {
    public static String TAG = MessageActivity.class.getName();

    @BindView(R.id.message_lv)
    ListView messageLv;
    @BindView(R.id.message_back)
    ImageView mMessageBack;
    private ItemTVAdapter adapter;
    public List<Message> list;
    MessageDao messageDao;

    Context context;
    public int point;
    public static String textString;
    public static String time;
    public static String title;
    Message message;
    protected SystemBarTintManager tintManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        context = MessageActivity.this;
        initDbHelp();
        initview();
        setPrimary();

    }

    private void initview() {
        //查询所有
        list = messageDao.queryBuilder().list();
        //list倒序排列
        Collections.reverse(list);
        adapter = new ItemTVAdapter(context, list);
        messageLv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        messageLv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                startActivity(new Intent(MessageActivity.this,DisplayMessageActivity.class));
//                messageListDao.insert(list.get(position));
                textString = list.get(position).getContent();
                time = list.get(position).getTime();
                title = list.get(position).getTitle();
                Log.info(TAG,list.get(position).getContent());
                Log.info(TAG,list.get(position).toString());

            }
        });

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

    /*初始化数据库相关*/
    public  void initDbHelp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(VcontrolApplication.getInstance(), "recluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        messageDao = daoSession.getMessageDao();
    }

    @OnClick(R.id.message_back)
    public void onViewClicked() {
        startActivity(new Intent(MessageActivity.this,MainActivity.class));
    }
}
