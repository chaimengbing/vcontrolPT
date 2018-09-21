package com.vcontrol.vcontroliot.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.model.MainInfo;

import java.util.ArrayList;
import java.util.List;

public class GridMainAdapter extends BaseAdapter
{
	private Context context;
	private List<MainInfo> list = null;

	public GridMainAdapter(Context context)
	{
		this.context = context;
		this.list = new ArrayList<>();

	}

	public void setData(List<MainInfo> list)
	{
		if (list != null && list.size() > 0)
		{
			this.list.clear();
			this.list = list;
		}
	}

	@Override
	public int getCount()
	{
		if (list == null || list.size() == 0)
		{
			return 0;
		}
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
		ViewHolder holder = null;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_main, null);
			holder.itemMain = (TextView) convertView.findViewById(R.id.item_main);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.itemMain.setText(list.get(position).getName());
//		holder.itemMain.setTextSize(TypedValue.COMPLEX_UNIT_PX,40);
		holder.itemMain.setTextColor(Color.parseColor("#323232"));

		Drawable drawable = context.getResources().getDrawable(list.get(position).getDrawable());
		holder.itemMain.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

		return convertView;
	}



	class ViewHolder
	{
		TextView itemMain;
	}

}
