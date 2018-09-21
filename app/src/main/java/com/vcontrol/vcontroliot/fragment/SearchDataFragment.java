package com.vcontrol.vcontroliot.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.ServiceUtils;
import com.vcontrol.vcontroliot.util.SocketUtil;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

/**
 * Created by linxi on 2017/5/31.
 */

public class SearchDataFragment extends BaseFragment implements View.OnClickListener, EventNotifyHelper.NotificationCenterDelegate
{

    private static final String TAG = SearchDataFragment.class.getSimpleName();
    private TextView beginTime;
    private TextView finishTime;
    private TextView historyTextView;
    private Button searchButton;
    private String begin = "";
    private String finish = "";


    @Override
    public int getLayoutView()
    {
        return R.layout.fragment_search_data;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_DATA);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.SELECT_TIME);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_CURRENT_HISTORY);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_HISTORY_SUCCESS);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_RESULT_ERROR);
    }

    @Override
    public void initComponentViews(View view)
    {
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_DATA);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.SELECT_TIME);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_CURRENT_HISTORY);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_HISTORY_SUCCESS);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_RESULT_ERROR);


        beginTime = (TextView) view.findViewById(R.id.begin_time_textview);
        finishTime = (TextView) view.findViewById(R.id.finish_time_textview);
        historyTextView = (TextView) view.findViewById(R.id.read_history_textview);

        searchButton = (Button) view.findViewById(R.id.search_button);
    }

    @Override
    public void initData()
    {
        ServiceUtils.getServiceUtils().initContent();
    }

    @Override
    public void setListener()
    {

        beginTime.setOnClickListener(this);
        finishTime.setOnClickListener(this);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.begin_time_textview:
                ServiceUtils.getServiceUtils().seletSearchDate(getActivity(), 1);
                break;
            case R.id.finish_time_textview:
                ServiceUtils.getServiceUtils().seletSearchDate(getActivity(), 2);
                break;
            case R.id.search_button:
                if (TextUtils.isEmpty(begin) || TextUtils.isEmpty(finish))
                {
                    ToastUtil.showToastLong(getString(R.string.Start_end_time_empty));
                    return;
                }
                SocketUtil.getSocketUtil().startReceHostory();
                String content = ConfigParams.RDDATATIME + begin + " " + finish;
                SocketUtil.getSocketUtil().sendContent(content);
                break;

            default:
                break;
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args)
    {
        if (id == UiEventEntry.READ_DATA)
        {

        }
        else if (id == UiEventEntry.SELECT_TIME)
        {
            int type = (int) args[2];
            String time = (String) args[0];
            if (type == 1)
            {
                beginTime.setText(time);
                begin = (String) args[1];
            }
            else if (type == 2)
            {
                finishTime.setText(time);
                finish = (String) args[1];
            }
        }
        else if (id == UiEventEntry.READ_CURRENT_HISTORY)
        {
            String result = (String) args[0];
            historyTextView.setText(getString(R.string.Reading_the_number) + result + getString(R.string.Packet_data));
        }
        else if (id == UiEventEntry.READ_HISTORY_SUCCESS)
        {
            historyTextView.setText(getString(R.string.Read_complete));
        }
        else if (id == UiEventEntry.READ_RESULT_ERROR)
        {
            historyTextView.setText(getString(R.string.Time_error));
        }
    }
}
