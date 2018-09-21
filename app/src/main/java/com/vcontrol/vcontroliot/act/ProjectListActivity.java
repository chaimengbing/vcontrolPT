package com.vcontrol.vcontroliot.act;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.adapter.ProjetAdapter;
import com.vcontrol.vcontroliot.model.DaoMaster;
import com.vcontrol.vcontroliot.model.DaoSession;
import com.vcontrol.vcontroliot.model.User;
import com.vcontrol.vcontroliot.model.UserDao;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.SystemBarTintManager;
import com.vcontrol.vcontroliot.util.UiEventEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by linxi on 2018/2/7.
 */

public class ProjectListActivity extends AppCompatActivity
{

    public static final String TAG = ProjectListActivity.class.getName();
    @BindView(R.id.project_listview)
    ListView messageLv;
    @BindView(R.id.message_back)
    ImageView mMessageBack;
    private ProjetAdapter adapter;
    public List<User> list;
    UserDao userDao;

    Context context;
    public int point;
    public String textString;
    User name;
    protected SystemBarTintManager tintManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_activity);
        ButterKnife.bind(this);
        context = ProjectListActivity.this;
        initDbHelp();
        initview();
        setPrimary();

    }

    private void initview() {
        //查询所有
        list = userDao.queryBuilder().list();
        //list倒序排列
        Collections.reverse(list);
        //分割list 展示
        if(list.size()>0)
        {
            List<User>  userList =new ArrayList<>();
            String name = list.get(0).getName();
            String[] split = name.split(",");
            for(String sp:split)
            {
                User u = new User();
                u.setName(sp);
                userList.add(u);
            }
            if(adapter == null)
            {
                adapter = new ProjetAdapter(context, userList);
                messageLv.setAdapter(adapter);

            }
            else
            {
                adapter.notifyDataSetChanged();
            }


        }
//        removeDuplicateWithOrder(list);
//        adapter = new ProjetAdapter(context, list);
//        messageLv.setAdapter(adapter);
//        adapter.notifyDataSetChanged();


    }

    public static void removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        System.out.println( " remove duplicate " + list);
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
    private void initDbHelp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(VcontrolApplication.getInstance(), "recluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();
    }

    private void backMain()
    {
        SocketUtil.getSocketUtil().closeSocketClient();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(UiEventEntry.NOTIFY_NOWEL, true);
        startActivity(intent);
        userDao.deleteAll();
    }

    @OnClick(R.id.message_back)
    public void onViewClicked() {
        backMain();
    }
}

