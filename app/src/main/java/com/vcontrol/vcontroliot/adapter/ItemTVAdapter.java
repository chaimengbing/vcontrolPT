package com.vcontrol.vcontroliot.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.model.Message;

import java.util.List;


/**
 * Created by Wu on 2017/5/10 0010 下午 3:52.
 * 描述：
 */
public class ItemTVAdapter extends BaseAdapter {
    Context context;
    List<Message> list;
    public static String title;
    public static String time;
    public static String message;
    public ItemTVAdapter(Context context, List<Message> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message tv = (Message) getItem(position);
        ViewHolder vh = null;
        title = tv.getTitle();
        message = tv.getContent();
        time = tv.getTime();
//        、
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_textview, null);
            vh = new ViewHolder();
            vh.item_tv = (TextView) convertView.findViewById(R.id.item_textview);
            vh.item_title = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.item_tv.setText(tv.getTime());
        vh.item_title.setText(tv.getTitle());
        return convertView;
    }
    class ViewHolder{
        TextView item_tv,item_title;
    }
}