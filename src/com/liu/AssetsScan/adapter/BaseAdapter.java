package com.liu.AssetsScan.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;

/**
 * �Զ���Adapter�Ļ��࣬����������Adapter��Ӧ�ü̳��Ա���
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<T> data;

	public BaseAdapter(Context context, List<T> data) {
		super();
		setContext(context);
		setData(data);
		setLayoutInflater();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("����Context������Ϊnull������");
		}
		this.context = context;
	}

	public LayoutInflater getLayoutInflater() {
		return layoutInflater;
	}

	public void setLayoutInflater() {
		if (context == null) {
			throw new RuntimeException("û�л�ȡ����Ч��Context������");
		}
		layoutInflater = LayoutInflater.from(this.context);
	}

	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		if (data == null) {
			data = new ArrayList<T>();
		}
		this.data = data;
	}
	
	@Override
	public int getCount() {
		return data!=null?data.size():0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
