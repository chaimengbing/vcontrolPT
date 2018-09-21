package com.vcontrol.vcontroliot.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.model.User;

import java.util.List;

/**
 * Created by linxi on 2018/2/7.
 */

public class ProjetAdapter extends BaseAdapter
{
    Context context;
    List<User> list;

    public ProjetAdapter(Context context, List<User> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final User tv = (User) getItem(position);
        ProjetAdapter.ViewHolder vh = null;
//        、
        if (convertView == null)
        {
            convertView = View.inflate(context, R.layout.item_project, null);
            vh = new ProjetAdapter.ViewHolder();
            vh.item_tv = (TextView) convertView.findViewById(R.id.item_project);
            convertView.setTag(vh);
        }
        else
        {
            vh = (ProjetAdapter.ViewHolder) convertView.getTag();
        }
        vh.item_tv.setText(tv.getName());
        return convertView;
    }

    class ViewHolder
    {
        TextView item_tv;
    }
}
