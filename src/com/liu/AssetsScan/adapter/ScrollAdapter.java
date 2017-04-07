package com.liu.AssetsScan.adapter;

import java.util.List;
import java.util.Map;

import com.capitalcode.assetsystemmobile.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ScrollAdapter extends SimpleAdapter {

	private List<? extends Map<String, ?>> datas;
	private int res;
	private String[] from;
	private int[] to;
	private Context context;

	public ScrollAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource,
			String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.datas = data;
		this.res = resource;
		this.from = from;
		this.to = to;
	}
	
	OnClickListener listener;
	public void setOnClickListener(OnClickListener listener)
	{
		this.listener = listener;
	}
	
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Map<String,String> map = (Map<String,String>)datas.get(position);
		String lastbutton = map.get("lastbutton");
		
		if (v == null) {
			v = LayoutInflater.from(context).inflate(res, null);

			View[] views = new View[to.length];
			
			for (int i = 0; i < to.length; i++) {
				View tv = v.findViewById(to[i]);
				tv.setVisibility(View.VISIBLE);
				views[i] = tv;
			}

			v.setTag(views);
		}
		
		
		/*
		if( lastbutton != null )
		{
			Button btn = (Button)v.findViewById(R.id.btn_finish);
			btn.setVisibility(View.VISIBLE);
			btn.setTag(position);
			btn.setOnClickListener(listener); 
		}
		else
		{
			Button btn = (Button)v.findViewById(R.id.btn_finish);
			btn.setVisibility(View.GONE);
			btn.setTag(position);
			btn.setOnClickListener(listener); 
		}
*/
		View[] holders = (View[]) v.getTag();
		int len = holders.length;
		for (int i = 0; i < len; i++) {
			((TextView) holders[i]).setText(this.datas.get(position)
					.get(from[i]).toString());
			
		if( this.datas.get(position)
					.get(from[i]).toString().equals("维修完成"))
		{
			((TextView) holders[i]).setTextColor(Color.rgb(255, 255, 255));
			((TextView) holders[i]).setClickable(true);
			((TextView) holders[i]).setBackgroundResource(R.drawable.btn_normal_selector);
			((TextView) holders[i]).setTag(position);
			((TextView) holders[i]).setOnClickListener(listener); 
		}
		else
		{
			((TextView) holders[i]).setBackgroundColor(Color.rgb(255, 255, 255));
			((TextView) holders[i]).setTextColor(0xFF464d4c);
		}
			
		}
		return v;
	}
}
