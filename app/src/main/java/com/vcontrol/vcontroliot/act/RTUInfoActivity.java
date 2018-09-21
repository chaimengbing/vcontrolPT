package com.vcontrol.vcontroliot.act;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.ToastUtil;

/**
 * Created by linxi on 2017/4/18.
 */

public class RTUInfoActivity extends BaseActivity
{

    private static final String TAG = RTUInfoActivity.class.getSimpleName();

    private EditText rtuNumEdittext;
    private Button infoButton;

    @Override
    public int getLayoutView()
    {
        return R.layout.activity_rtu_info;
    }

    @Override
    public void initViewData()
    {
        showToolbar();
        setTitleName(getString(R.string.status_search));
    }

    @Override
    public void initComponentViews()
    {
        VcontrolApplication.getInstance().addActivity(this);
        VcontrolApplication.setCurrentContext(getApplicationContext());
        ToastUtil.setCurrentContext(getApplication());

        rtuNumEdittext = (EditText) findViewById(R.id.rtu_num_edittext);
        infoButton = (Button) findViewById(R.id.info__button);

        infoButton.setOnClickListener(this);



    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        return false;
    }

    @Override
    public void onClick(View v)
    {
        String path = "";
        if (v.getId() == R.id.info__button)
        {
            String num = rtuNumEdittext.getText().toString().trim();
            path = ConfigParams.RTU_INFO_URL + num;


//            OkGo.get(path).tag(this)
//                    .execute(new DialogCallback<RTUResponse<RTUModel>>(this) {
//                        @Override
//                        public void onSuccess(RTUResponse<RTUModel> responseData, Call call, Response response) {
//                            Log.info(TAG,responseData.data.getMessageName());
//                        }
//
//                        @Override
//                        public void onError(Call call, Response response, Exception e) {
//                            super.onError(call, response, e);
//                        }
//                    });
        }
    }
}
