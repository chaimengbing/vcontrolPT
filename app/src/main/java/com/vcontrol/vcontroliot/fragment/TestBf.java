package com.vcontrol.vcontroliot.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.log.Log;

/**
 * Created by linxi on 2017/7/12.
 */

public class TestBf extends BaseFragment implements View.OnClickListener
{

    private Button testButton;
    private EditText testEdittext;

    @Override
    public int getLayoutView()
    {
        return R.layout.testsss;
    }

    @Override
    public void initComponentViews(View view)
    {
        testButton = (Button) view.findViewById(R.id.test_button);
        testEdittext = (EditText) view.findViewById(R.id.test_edittext);

        testButton.setOnClickListener(this);

    }

    @Override
    public void initData()
    {

    }

    @Override
    public void setListener()
    {

    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.test_button)
        {
            Log.info("tag","onClick" + testEdittext.getText());
        }
    }
}
