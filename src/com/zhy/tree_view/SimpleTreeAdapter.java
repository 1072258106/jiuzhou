package com.zhy.tree_view;


import java.util.List;

import android.content.Context;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.capitalcode.assetsystemmobile.R;
import com.zhy.bean.FileBean;
import com.zhy.tree.bean.Node;
import com.zhy.tree.bean.TreeListViewAdapter;


public class SimpleTreeAdapter<T> extends TreeListViewAdapter<T>
{

	
	
	public SimpleTreeAdapter(ListView mTree, Context context, List<FileBean> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException
	{
		super(mTree, context, datas, defaultExpandLevel);
	}

	@Override
	public View getConvertView(final Node node , int position, View convertView, ViewGroup parent)
	{
		
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.id_treenode_icon);
			viewHolder.label = (TextView) convertView
					.findViewById(R.id.id_treenode_label);
			viewHolder.box =  (CheckBox) convertView
					.findViewById(R.id.cb);
			convertView.setTag(viewHolder);

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (node.getIcon() == -1)
		{
			viewHolder.icon.setVisibility(View.INVISIBLE);
		} else
		{
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.icon.setImageResource(node.getIcon());
		}

		viewHolder.label.setText(node.getName());
		
		
		if(ComplexSelectActivity.searchid.equals("useUser"))
		{
			if( node.isChild == true )
			{
				viewHolder.box.setVisibility(View.VISIBLE);
			}
			else
			{
				viewHolder.box.setVisibility(View.GONE);
			}
		}
		else
		{
			viewHolder.box.setVisibility(View.VISIBLE);
		}
		/*
		class MyBoxWatcher implements OnCheckedChangeListener
		{
			int id;
			MyBoxWatcher(int id)
			{
				this.id = id;
			}
			

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) 
			{
				// TODO Auto-generated method stub
                if(isChecked){  
                	checkid = id;
                }else{  
                    checkid = -1;
                }
                
                notifyDataSetChanged();
               
			}
		}
		
		viewHolder.box.setOnCheckedChangeListener(new MyBoxWatcher(node.getId()));
		*/
		
		/*
		viewHolder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView,  
                    boolean isChecked) {  
                // TODO Auto-generated method stub  
                if(isChecked){  
                	
                }else{  
                    
                }  
            }  
        }
		
		*/
		viewHolder.box.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				if( ComplexSelectActivity.isMultChoose == false )
				{
				
					if (node.getId() == ComplexSelectActivity.checkid)
					{
						ComplexSelectActivity.checkid = -1;
					}
					else 
					{
						ComplexSelectActivity.checkid = node.getId();
					}
				
				}
				else
				{
					if (ComplexSelectActivity.checkids.contains(String.valueOf(node.getId())))
					{
						for( String value : ComplexSelectActivity.checkids )
						{
							if( value.equals(String.valueOf(node.getId())))
							{
								ComplexSelectActivity.checkids.remove(value);
								break;
							}
							
						}

					}
					else 
					{
						ComplexSelectActivity.checkids.add(String.valueOf(node.getId()));
					}
					
					
				}
				
				notifyDataSetChanged();
			}
		}); 
		
		
		if( ComplexSelectActivity.isMultChoose == false )
		{
		
			if( node.getId() == ComplexSelectActivity.checkid )
			{
				viewHolder.box.setChecked(true);
			}
			else
			{
				viewHolder.box.setChecked(false);
			}
		
		}
		else
		{
			if( ComplexSelectActivity.checkids.contains(String.valueOf(node.getId())) )
			{
				viewHolder.box.setChecked(true);
			}
			else
			{
				viewHolder.box.setChecked(false);
			}
		}
		
		
		
		return convertView;
	}

	private final class ViewHolder
	{
		ImageView icon;
		TextView label;
		CheckBox box;
	}

}
