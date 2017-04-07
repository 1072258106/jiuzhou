package com.zhy.tree.bean;

import java.util.List;

import com.liu.AssetsScan.model.CategoryModel;
import com.liu.AssetsScan.model.DeptModel;
import com.liu.AssetsScan.model.UserInfoModel;
import com.zhy.bean.FileBean;
import com.zhy.tree_view.ComplexSelectActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
/**
 * http://blog.csdn.net/lmj623565791/article/details/40212367
 * @author zhy
 *
 * @param <T>
 */
public abstract class TreeListViewAdapter<T> extends BaseAdapter
{

	protected Context mContext;
	/**
	 * 存储所有可见的Node
	 */
	protected List<Node> mNodes;
	protected LayoutInflater mInflater;
	/**
	 * 存储所有的Node
	 */
	public List<Node> mAllNodes;

	/**
	 * 点击的回调接口
	 */
	private OnTreeNodeClickListener onTreeNodeClickListener;

	public interface OnTreeNodeClickListener
	{
		void onClick(Node node, int position);
	}

	public void setOnTreeNodeClickListener(
			OnTreeNodeClickListener onTreeNodeClickListener)
	{
		this.onTreeNodeClickListener = onTreeNodeClickListener;
	}

	/**
	 * 
	 * @param mTree
	 * @param context
	 * @param datas
	 * @param defaultExpandLevel
	 *            默认展开几级树
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public TreeListViewAdapter(ListView mTree, Context context, List<FileBean> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException
	{
		mContext = context;
		/**
		 * 对所有的Node进行排序
		 */
		mAllNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);
		/**
		 * 过滤出可见的Node
		 */
		mNodes = TreeHelper.filterVisibleNode(mAllNodes);
		mInflater = LayoutInflater.from(context);

		/**
		 * 设置节点点击时，可以展开以及关闭；并且将ItemClick事件继续往外公布
		 */
		mTree.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				

				if (onTreeNodeClickListener != null)
				{
					onTreeNodeClickListener.onClick(mNodes.get(position),
							position);
				}
				
				expandOrCollapse(position);
			}

		});

	}

	/**
	 * 相应ListView的点击事件 展开或关闭某节点
	 * 
	 * @param position
	 */
	public void expandOrCollapse(int position)
	{
		Node n = mNodes.get(position);

		if (n != null)// 排除传入参数错误异常
		{
			if (!n.isLeaf())
			{

				if( ComplexSelectActivity.searchid.equals("useUser") || ComplexSelectActivity.searchid.equals("useDept") )
				{
				
					if( (n.submodel != null || n.info != null) && n.init == false  )
					{
						int count = 0;
						
						for( ;count<mAllNodes.size();count++)
						{
							Node item = mAllNodes.get(count);
							if( item.getId() == n.getId() )
							{
								break;
							}
						}
						
						
						if( n.submodel != null )
						{
							for( DeptModel submodel : n.submodel )
							{
								Node node = null;
								if(ComplexSelectActivity.searchid.equals("useUser"))
								{
									node = new Node(Integer.valueOf(submodel.DeptId),n.getId(),submodel.DeptName,false,submodel.SubDept,submodel.UserInfo);
								}
								else
								{
									node = new Node(Integer.valueOf(submodel.DeptId),n.getId(),submodel.DeptName,false,submodel.SubDept,null);
								}
								
								
								node.setExpand(false);
								node.setParent(n);
								n.getChildren().add(node);
								node.init = false;
								
								mAllNodes.add(++count,node);
							}
						}
						
						if(ComplexSelectActivity.searchid.equals("useUser"))
						{
						
							if( n.info != null )
							{
								for( UserInfoModel info : n.info )
								{
									Node node = new Node(Integer.valueOf(info.UsertId)+10000,n.getId(),info.UserName,true,null,null);
									n.getChildren().add(node);
									node.setParent(n);
									node.setExpand(false);
									node.init = false;
									
									mAllNodes.add(++count,node);
								}
							}
						
						}
						
						n.init = true;
					}
				
				
				}
				else //if( ComplexSelectActivity.searchid.equals("category"))
				{
					
					if( n.subCategory != null && n.init == false)
					{
						int count = 0;
						
						for( ;count<mAllNodes.size();count++)
						{
							Node item = mAllNodes.get(count);
							if( item.getId() == n.getId() )
							{
								break;
							}
						}
						
						

							for( CategoryModel submodel : n.subCategory )
							{
								Node node = null;
								if(ComplexSelectActivity.searchid.equals("useUser"))
								{
									node = new Node(Integer.valueOf(submodel.TypeId),n.getId(),submodel.TypeName,false,submodel.SubCategory);
								}
								else
								{
									node = new Node(Integer.valueOf(submodel.TypeId),n.getId(),submodel.TypeName,false,submodel.SubCategory);
								}
								
								
								node.setExpand(false);
								node.setParent(n);
								n.getChildren().add(node);
								node.init = false;
								
								mAllNodes.add(++count,node);
							}
						
						
						
						n.init = true;
					}
					

				}
				
				n.setExpand(!n.isExpand());
				
				
				mNodes = TreeHelper.filterVisibleNode(mAllNodes);
				notifyDataSetChanged();// 刷新视图
			}
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public int getCount()
	{
		return mNodes.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mNodes.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Node node = mNodes.get(position);
		convertView = getConvertView(node, position, convertView, parent);
		// 设置内边距
		convertView.setPadding(node.getLevel() * 30, 3, 3, 3);
		return convertView;
	}

	public abstract View getConvertView(Node node, int position,
			View convertView, ViewGroup parent);

}
