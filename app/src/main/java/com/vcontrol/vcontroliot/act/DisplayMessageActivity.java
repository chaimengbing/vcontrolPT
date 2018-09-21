package com.vcontrol.vcontroliot.act;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;

/**
 * Created by linxi on 2018/2/5.
 */

public class DisplayMessageActivity extends BaseActivity
{


    public static TextView title;
    public static TextView time;
    public static TextView atv;
    private ImageView imageView;


    @Override
    public int getLayoutView()
    {
        return R.layout.display_message;
    }

    @Override
    public void initViewData()
    {

    }

    @Override
    public void initComponentViews()
    {
        atv = (TextView) findViewById(R.id.display_message);
        title = (TextView) findViewById(R.id.title_display);
        time = (TextView) findViewById(R.id.time_display);
        imageView = (ImageView) findViewById(R.id.display_back);
        title.setText(MessageActivity.title);
        time.setText(MessageActivity.time);
        atv.setText(MessageActivity.textString);
        imageView.setOnClickListener(this);


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
            case R.id.display_back:
                startActivity(new Intent(DisplayMessageActivity.this,MessageActivity.class));
                break;
            default:
                break;
        }
    }
}
