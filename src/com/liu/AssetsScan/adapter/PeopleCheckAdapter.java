package com.liu.AssetsScan.adapter;

import java.util.List;

import com.capitalcode.assetsystemmobile.R;
import com.liu.AssetsScan.model.Getui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PeopleCheckAdapter extends BaseAdapter<Getui>{

	public PeopleCheckAdapter(Context context, List<Getui> data) {
		super(context, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 获取需要被显示的数据
		Getui checkTable = getData().get(position);
		// 加载模板，准备承载数据的控件
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = getLayoutInflater().inflate(R.layout.people_check_adapter, null);
			holder.zzbh = (TextView) convertView.findViewById(R.id.tv_getui_zzbh);
			holder.zzlb = (TextView) convertView.findViewById(R.id.tv_getui_zzlb);
			holder.zzflag = (TextView) convertView.findViewById(R.id.tv_getui_zzflag);
			holder.contents = (TextView) convertView.findViewById(R.id.tv_getui_content);

			convertView.setTag(holder); 
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 3. 向模板中填充数据
		holder.zzbh.setText(checkTable.getZzbh());
		holder.zzlb.setText(checkTable.getZzlb());
		holder.zzflag.setText(checkTable.getReadflag());
		holder.contents.setText(checkTable.getContents());

		// 4. 返回
		return convertView;
	}

	private class ViewHolder{
		TextView zzbh;
		TextView zzlb;
		TextView zzflag;
		TextView contents;
	}
}
