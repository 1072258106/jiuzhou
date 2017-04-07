package com.zhy.tree.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.capitalcode.assetsystemmobile.R;
import com.zhy.bean.FileBean;
import com.zhy.tree_view.ComplexSelectActivity;

/**
 * http://blog.csdn.net/lmj623565791/article/details/40212367
 * @author zhy
 *
 */
public class TreeHelper
{
	/**
	 * 传入我们的普通bean，转化为我们排序后的Node
	 * 
	 * @param datas
	 * @param defaultExpandLevel
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> List<Node> getSortedNodes(List<FileBean> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException

	{
		List<Node> result = new ArrayList<Node>();
		// 将用户数据转化为List<Node>
		List<Node> nodes = convetData2Node(datas);
		// 拿到根节点
		//List<Node> rootNodes = getRootNodes(nodes);
		// 排序以及设置Node间关系
		for (Node node : nodes)
		{
			addNode(result, node, defaultExpandLevel, 1);
		}
		return result;
	}

	/**
	 * 过滤出所有可见的Node
	 * 
	 * @param nodes
	 * @return
	 */
	public static List<Node> filterVisibleNode(List<Node> nodes)
	{
		List<Node> result = new ArrayList<Node>();
		
		
		

		for (Node node : nodes)
		{
			// 如果为跟节点，或者上层目录为展开状态
			if ( node.isRoot() || node.isParentExpand())
			{
				setNodeIcon(node);
				result.add(node);
			}
		}
		return result;
	}

	/**
	 * 将我们的数据转化为树的节点
	 * 
	 * @param datas
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static <T> List<Node> convetData2Node(List<FileBean> datas)
			throws IllegalArgumentException, IllegalAccessException

	{
		List<Node> nodes = new ArrayList<Node>();
		Node node = null;

		for (FileBean t : datas)
		{
			if( ComplexSelectActivity.searchid.equals("useUser") || ComplexSelectActivity.searchid.equals("useDept") )
			{
				node = new Node(t.id, t.parentId, t.name,t.isChild,t.submodel,t.info);
			}
			else //if( ComplexSelectActivity.searchid.equals("category"))
			{
				node = new Node(t.id, t.parentId, t.name,t.isChild,t.subCategory);
			}
			
			
			nodes.add(node);
		}

		/**
		 * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
		 */
		/*
		for (int i = 0; i < nodes.size(); i++)
		{
			Node n = nodes.get(i);
			for (int j = i + 1; j < nodes.size(); j++)
			{
				Node m = nodes.get(j);
				if (m.getpId() == n.getId())
				{
					n.getChildren().add(m);
					m.setParent(n);
				} else if (m.getId() == n.getpId())
				{
					m.getChildren().add(n);
					n.setParent(m);
				}
			}
		}
		*/
		// 设置图片
		for (Node n : nodes)
		{
			setNodeIcon(n);
		}
		return nodes;
	}

	private static List<Node> getRootNodes(List<Node> nodes)
	{
		List<Node> root = new ArrayList<Node>();
		for (Node node : nodes)
		{
			if (node.isRoot())
				root.add(node);
		}
		return root;
	}

	/**
	 * 把一个节点上的所有的内容都挂上去
	 */
	private static void addNode(List<Node> nodes, Node node,
			int defaultExpandLeval, int currentLevel)
	{

		nodes.add(node);
/*
		if (defaultExpandLeval >= currentLevel)
		{
			node.setExpand(true);
		}
		else
*/		
		{
			node.setExpand(false);
		}
		
		if (node.isLeaf())
			return;
		for (int i = 0; i < node.getChildren().size(); i++)
		{
			addNode(nodes, node.getChildren().get(i), defaultExpandLeval,
					currentLevel + 1);
		}
	}

	/**
	 * 设置节点的图标
	 * 
	 * @param node
	 */
	private static void setNodeIcon(Node node)
	{
		if( ComplexSelectActivity.searchid.equals("useUser") || ComplexSelectActivity.searchid.equals("useDept") )
		{
		
			if ( (node.submodel!=null || node.info != null )  && node.isExpand())
			{
				node.setIcon(R.drawable.tree_ex);
			} else if (  ( node.submodel!=null || node.info != null ) && !node.isExpand() )
			{
				node.setIcon(R.drawable.tree_ec);
			} else
			{
				node.setIcon(-1);
			}
		
		}
		else //if( ComplexSelectActivity.searchid.equals("category"))
		{
			if ( node.subCategory != null && node.isExpand() )
			{
				node.setIcon(R.drawable.tree_ex);
			} 
			else if ( node.subCategory != null && !node.isExpand() )
			{
				node.setIcon(R.drawable.tree_ec);
			} 
			else
			{
				node.setIcon(-1);
			}
			
		}

	}

}
