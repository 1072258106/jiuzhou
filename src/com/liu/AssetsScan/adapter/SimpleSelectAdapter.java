package com.liu.AssetsScan.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.capitalcode.assetsystemmobile.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SimpleSelectAdapter extends android.widget.BaseAdapter {

	
	private Context context;
	private List<Map<String,String>> list;
	private LayoutInflater mInflater;
	
	
	public SimpleSelectAdapter(Context context, List<Map<String,String>> list) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return (long)arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		HashMap<String,String> map = (HashMap<String,String>)list.get(arg0);
		String value = (String)map.get("value");
		
		if (convertView == null) 
		{
			convertView = this.mInflater.inflate(R.layout.item_select, null);
		}
		else
		{
			
		}
		
		TextView name = ((TextView) convertView.findViewById(R.id.name));
		
		
		name.setText(value);

		return convertView;
	}

}
