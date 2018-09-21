package com.vcontrol.vcontroliot.fragment.fragment_things;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.vcontrol.vcontroliot.R;
import android.view.ViewGroup;

/**
 * Created by linxi on 2017/10/13.
 */

public class EquipmentThing extends Fragment implements OnClickListener
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_equipment,container,false);
        initView(view);

        return view;
    }
    private void initView(View view){

    }
    @Override
    public void onClick(View v)
    {

    }
}
