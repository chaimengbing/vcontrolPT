package com.vcontrol.vcontroliot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vcontrol.vcontroliot.R;

/**
 * Created by Vcontrol on 2016/11/11.
 */

public class RecyclerAdapter extends RecyclerView.Adapter
{

    private Context context;
    private LayoutInflater inflater;
    private TextView ff;

    public RecyclerAdapter(Context context)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view = inflater.inflate(R.layout.item_recycler_layout, null);
        HeheViewHolder holder = new HeheViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 100;
    }

    class HeheViewHolder extends RecyclerView.ViewHolder
    {

        public HeheViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
