package com.liu.AssetsScan.adapter;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;

import com.capitalcode.assetsystemmobile.R;


/**
 * Created by zhangshuai on 15/9/27.
 */
public class GridViewAdapter extends android.widget.BaseAdapter {

    private Context mContext;
    private ArrayList<HashMap<String, Object>> mImageItem;

    public GridViewAdapter(Context mContext,ArrayList<HashMap<String, Object>> mImageItem){
        this.mContext = mContext;
        this.mImageItem = mImageItem;
    }

    @Override
    public int getCount() {
        return mImageItem.size();
    }

    @Override
    public Object getItem(int i) {
        return mImageItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        HashMap<String, Object> map = mImageItem.get(i);
        if (view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.main_item,null);
            holder.image = (ImageView) view.findViewById(R.id.ItemImage);
            holder.title = (TextView) view.findViewById(R.id.ItemText);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed},
                mContext.getResources().getDrawable((Integer) (mImageItem.get(i).get("ItemImageSelect"))));
        drawable.addState(new int[]{android.R.attr.state_focused},
                mContext.getResources().getDrawable((Integer) (mImageItem.get(i).get("ItemImageSelect"))));

        drawable.addState(new int[]{-android.R.attr.state_pressed , -android.R.attr.state_focused},
                mContext.getResources().getDrawable((Integer) (mImageItem.get(i).get("ItemImage"))));
        holder.image.setBackgroundDrawable(drawable);
        holder.title.setText((String) (mImageItem.get(i).get("ItemText")));

        return view;
    }

    class ViewHolder{
        ImageView image;
        TextView title;
    }
}
