package com.zhy.tree_view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.capitalcode.assetsystemmobile.BaseActivity;
import com.capitalcode.assetsystemmobile.R;
import com.liu.AssetsScan.model.CategoryModel;
import com.liu.AssetsScan.model.DeptModel;
import com.liu.AssetsScan.model.UserInfoModel;
import com.zhy.bean.FileBean;
import com.zhy.tree.bean.Node;
import com.zhy.tree.bean.TreeListViewAdapter;
import com.zhy.tree.bean.TreeListViewAdapter.OnTreeNodeClickListener;

public class ComplexSelectActivity extends BaseActivity
{
	//private List<Bean> mDatas = new ArrayList<Bean>();
	private List<FileBean> mDatas2 = new ArrayList<FileBean>();
	private ListView mTree;
	private TreeListViewAdapter mAdapter;

	private Map<String,String> mapInfo;

	public static Map<String,Object> item;
	public static List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();


	public static Map<String,Object> specialitem;
	public static BaseAdapter specialupdateadapter;

	public static BaseAdapter updateadapter;

	public static int checkid = -1;
	public static List<String> checkids = new ArrayList<String>();
	public static boolean isMultChoose = false;
	public static List<Map<String,String>> listchoose;


	private void chooseSubPeople(DeptModel model)
	{
		if( model.SubDept!= null )
		{
			for( DeptModel submodel : model.SubDept )
			{

				if( submodel.UserInfo != null )
				{
					for( UserInfoModel info : submodel.UserInfo )
					{
						checkids.add(String.valueOf(Integer.valueOf(info.UsertId)+10000));
					}
				}
				chooseSubPeople(submodel);

			}

		}
	}

	private void chooseSubDept(DeptModel model,boolean add)
	{
		if( model.SubDept!= null )
		{
			for( DeptModel submodel : model.SubDept )
			{
				if(add==true)
				{
					checkids.add(submodel.DeptId);
				}
				mapInfo.put(submodel.DeptId,submodel.DeptName);
				chooseSubDept(submodel,add);
			}			
		}
	}

	private void chooseSubCategory(CategoryModel model,boolean add)
	{
		if( model.SubCategory!= null )
		{
			for( CategoryModel submodel : model.SubCategory )
			{
				if(add==true)
				{				
					checkids.add(submodel.TypeId);
				}
				mapInfo.put(submodel.TypeId,submodel.TypeName);
				chooseSubCategory(submodel,add);
			}			
		}
	}



	private void allchoose(boolean add)
	{

		/*
		if( searchid.equals("useUser") )
		{
			for( DeptModel model : loginModel.lstDept)
			{
				if( model.UserInfo != null )
				{
					for( UserInfoModel info : model.UserInfo )
					{
						checkids.add(String.valueOf(Integer.valueOf(info.UsertId)+10000));

					}
				}

				chooseSubPeople(model);
			}

		}
		else*/ 
		if( searchid.equals("useDept") )
		{
			mapInfo = new HashMap<String,String>();
			for( DeptModel model : loginModel.lstDept)
			{
				if(add==true)
				{
					checkids.add(model.DeptId);
				}
				mapInfo.put(model.DeptId,model.DeptName);
				chooseSubDept(model,add);
			}

		}
		else if( searchid.equals("category") )
		{
			mapInfo = new HashMap<String,String>();
			for( CategoryModel model : basedataModel.Category )
			{
				if(add==true)
				{
					checkids.add(model.TypeId);
				}
				mapInfo.put(model.TypeId,model.TypeName);
				chooseSubCategory(model,add);

			}

		}
		else if( searchid.equals("Standard") && add == true )
		{

			String value = basedataModel.Standard;

			String[] allvalues = value.split("\\$");
			int id = 1;
			for( String str : allvalues)
			{				
				//Log.e("eeeeeeeeeeeeeee",str);
				//mDatas2.add(new FileBean(id++ , 0, str,false,null));
				checkids.add(String.valueOf(id++));
			}

		}


	}





	private void initDatas(String searchid)
	{
		/*
		mDatas2.add(new FileBean(1, 0, "文件管理系统",false));
		mDatas2.add(new FileBean(2, 1, "游戏",false));
		mDatas2.add(new FileBean(3, 1, "文档",true));
		mDatas2.add(new FileBean(4, 1, "程序",false));
		mDatas2.add(new FileBean(5, 2, "war3",false));
		mDatas2.add(new FileBean(6, 2, "刀塔传奇",false));
		mDatas2.add(new FileBean(100, 6, "刀塔传奇2",true));

		mDatas2.add(new FileBean(7, 4, "面向对象",false));
		mDatas2.add(new FileBean(8, 4, "非面向对象",false));

		mDatas2.add(new FileBean(9, 7, "C++",false));
		mDatas2.add(new FileBean(10, 7, "JAVA",false));
		mDatas2.add(new FileBean(11, 7, "Javascript",true));
		mDatas2.add(new FileBean(12, 8, "C",false));
		 */

		if( searchid.equals("useUser") )
		{

			for( DeptModel model : loginModel.lstDept)
			{
				mDatas2.add(new FileBean(Integer.valueOf(model.DeptId) , 0, model.DeptName,false,model.SubDept,model.UserInfo));
				//setSubDept(model);
				/*
				if( model.UserInfo != null )
				{
					for( UserInfoModel info : model.UserInfo )
					{
						mDatas2.add(new FileBean(Integer.valueOf(info.UsertId)+10000 , Integer.valueOf(model.DeptId), info.UserName,true,null));
					}
				}
				 */
			}

		}
		else if( searchid.equals("useDept") )
		{
			for( DeptModel model : loginModel.lstDept)
			{
				mDatas2.add(new FileBean(Integer.valueOf(model.DeptId) , 0, model.DeptName,false,model.SubDept,null));
				//setSubDept(model);
				/*
				if( model.UserInfo != null )
				{
					for( UserInfoModel info : model.UserInfo )
					{
						mDatas2.add(new FileBean(Integer.valueOf(info.UsertId)+10000 , Integer.valueOf(model.DeptId), info.UserName,true,null));
					}
				}
				 */
			}

		}
		else if( searchid.equals("category") )
		{
			for( CategoryModel model : basedataModel.Category )
			{

				mDatas2.add(new FileBean(Integer.valueOf(model.TypeId) , 0, model.TypeName,false,model.SubCategory));

			}

		}
		else if( searchid.equals("Standard") )
		{

			String value = basedataModel.Standard;

			String[] allvalues = value.split("\\$");
			int id = 1;
			for( String str : allvalues)
			{				
				Log.e("eeeeeeeeeeeeeee",str);
				mDatas2.add(new FileBean(id++ , 0, str,false,null));
			}

		}





	}


	private void setSubDept(DeptModel model)
	{

		if( model.SubDept!= null )
		{
			for( DeptModel submodel : model.SubDept )
			{
				mDatas2.add(new FileBean(Integer.valueOf(submodel.DeptId) , Integer.valueOf(model.DeptId), submodel.DeptName,false,submodel.SubDept,submodel.UserInfo));
				//setSubDept(submodel);
				/*
				if( submodel.UserInfo != null )
				{
					for( UserInfoModel info : submodel.UserInfo )
					{
						mDatas2.add(new FileBean(Integer.valueOf(info.UsertId)+10000 , Integer.valueOf(submodel.DeptId), info.UserName,true,null));
					}
				}
				 */
			}

		}




	}






	@Override
	protected void Init(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void AppInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void DataInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void Destroy() {
		// TODO Auto-generated method stub

	}

	public static String searchid;

	@Override
	protected void ViewInit() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_complex_select);

		searchid = this.getIntent().getStringExtra("searchid");
		isMultChoose = this.getIntent().getBooleanExtra("isMultChoose", false);

		Button btn = (Button)this.findViewById(R.id.btn_all_choose);


		if( isMultChoose == false )
		{
			String strcheckid = this.getIntent().getStringExtra("realvalue");
			if( strcheckid==null || strcheckid.length() == 0 )
			{
				checkid = -1;
			}
			else
			{try{


				checkid = Integer.valueOf(strcheckid);

			}catch(NumberFormatException e){

				//					Toast.makeText(context, "非法操作，请还原数据！！", Toast.LENGTH_SHORT).show();
			}

			}

			btn.setVisibility(View.GONE);
		}
		else
		{
			/*
			String strcheckids = this.getIntent().getStringExtra("realvalues");
			if( strcheckids == null || strcheckids.length() == 0 )
			{
				checkids = new ArrayList<String>();
			}
			else
			{
				checkids = Arrays.asList(strcheckids.split(","));
			}
			 */
			checkids.clear();
			for( Map<String,String> map : listchoose )
			{
				String id = (String)map.get("id");
				checkids.add(id);
			}

			btn.setVisibility(View.VISIBLE);

			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Button btn = (Button)v;

					String text = btn.getText().toString();
					if( text.equals("全选"))
					{
						btn.setText("全不选");
						checkids.clear();

						allchoose(true);


					}
					else
					{
						btn.setText("全选");
						checkids.clear();
					}

					mAdapter.notifyDataSetChanged();
				}
			});
		}



		btn = (Button)this.findViewById(R.id.btn_title_left);
		btn.setVisibility(View.GONE);


		btn = (Button)this.findViewById(R.id.btn_title_right);
		btn.setVisibility(View.VISIBLE);
		btn.setText("确认");

		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if( isMultChoose == false )
				{

					if( checkid == -1 )
					{
						new AlertDialog.Builder(ComplexSelectActivity.this).setTitle("提示")//设置对话框标题  		  
						.setMessage("请选择一项!")//设置显示的内容  
						.setPositiveButton("是",new DialogInterface.OnClickListener() {//添加确定按钮  
							@Override  
							public void onClick(DialogInterface dialog, int which) 
							{//确定按钮的响应事件  

								return;
							}  
						}).show();//在按键响应事件中显示此对话框

						return;
					}




					for( Node node : (List<Node>)mAdapter.mAllNodes )
					{
						if( node.getId() == checkid )
						{
							item.put("value", node.getName());
							if( "useUser".equals(searchid) )
							{
								item.put("realvalue", String.valueOf(node.getId()-10000));
								String getid = (String)item.get("id");
								if( getid.equals("useUserId") && specialitem != null && specialupdateadapter != null)
								{
									specialitem.put("realvalue", String.valueOf(node.getParent().getId()));
									specialitem.put("value", String.valueOf(node.getParent().getName()));
									specialupdateadapter.notifyDataSetChanged();
								}


								for( Map<String,Object> one: items )
								{
									String attchid = (String)one.get("id");
									if( attchid.equals("Int240"))
									{
										one.put("realvalue", String.valueOf(node.getId()-10000));
										one.put("value", String.valueOf(node.getName()));
									}
									else
									{
										one.put("realvalue", String.valueOf(node.getParent().getId()));
										one.put("value", String.valueOf(node.getParent().getName()));
									}
								}

							}
							else
							{
								item.put("realvalue", String.valueOf(node.getId()));
							}
							updateadapter.notifyDataSetChanged();

							break;
						}
					}

				}
				else
				{
					allchoose(false);

					listchoose.clear();
					if( mapInfo == null || searchid.equals("Standard") )
					{

						for( Node node : (List<Node>)mAdapter.mAllNodes )
						{
							String id = String.valueOf(node.getId());

							if( checkids.contains(id) )
							{


								Map<String,String> map = new HashMap<String,String>();
								map.put("id", id);
								map.put("value", node.getName());

								listchoose.add(map);
							}
						}

					}
					else
					{
						for(String id : checkids)
						{
							Map<String,String> map = new HashMap<String,String>();
							map.put("id", id);
							map.put("value", mapInfo.get(id));

							listchoose.add(map);
						}


					}

					updateadapter.notifyDataSetChanged();

				}














				finish();
			}
		});



		LinearLayout ll = (LinearLayout)this.findViewById(R.id.tv_back);
		ll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});


		TextView tv = (TextView)this.findViewById(R.id.tv_title_name);
		tv.setText(this.getIntent().getStringExtra("title"));

		initDatas(searchid);
		mTree = (ListView) findViewById(R.id.id_tree);

		//mTree.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		try
		{
			mAdapter = new SimpleTreeAdapter<FileBean>(mTree, this, mDatas2, 1);
			mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener()
			{
				@Override
				public void onClick(Node node, int position)
				{
					if (node.isLeaf())
					{
						/*
						Toast.makeText(getApplicationContext(), node.getName(),
								Toast.LENGTH_SHORT).show();
						 */		
						/*
						item.put("value", node.getName());



						finish();
						updateadapter.notifyDataSetChanged();

						 */

						if( checkid != node.getId() )
						{
							checkid = node.getId();
						}
						else
						{
							checkid = -1;
						}

						mAdapter.notifyDataSetChanged();

					}
				}

			});

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		mTree.setAdapter(mAdapter);
	}

	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub

	}

}
