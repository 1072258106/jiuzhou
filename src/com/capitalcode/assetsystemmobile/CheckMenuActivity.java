package com.capitalcode.assetsystemmobile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.adapter.GridViewAdapter;
import com.liu.AssetsScan.async.CallEarliest;
import com.liu.AssetsScan.async.Callable;
import com.liu.AssetsScan.async.Callback;
import com.liu.AssetsScan.model.BaseDataModel;
import com.liu.AssetsScan.model.CheckBaseData;
import com.liu.AssetsScan.model.ListMsStockModel;
import com.liu.AssetsScan.model.LoginDataModel;
import com.liu.AssetsScan.model.MenuModel;
import com.liu.AssetsScan.model.MsStockModel;
import com.liu.AssetsScan.model.RequestRetModel;
import com.liu.AssetsScan.model.SaveCheckDataModel;
import com.liu.AssetsScan.util.ApiAddressHelper;
import com.liu.AssetsScan.util.Common;
import com.liu.AssetsScan.util.DatabaseHelper;
import com.liu.AssetsScan.util.HttpHelper;
import com.liu.AssetsScan.util.ListDataSave;
import com.liu.AssetsScan.util.StaticUtil;
import com.liu.AssetsScan.util.ToastUtil;

//盘点功能菜单界面
public class CheckMenuActivity extends BaseActivity implements OnClickListener {

	static public List<MenuModel> listmenu = new ArrayList<MenuModel>();
	static public String StockRightCode="";
	static String flag = "no";
	public List<CheckBaseData> baseDatas;
	String title;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StaticUtil.setSpinner("部门盘点");
	}
	
	@Override
	public void onCreate(Bundle paramBundle) {
		// TODO Auto-generated method stub
		super.onCreate(paramBundle);
		Intent intent = new Intent(this,NotificationMonitorService.class);
		startService(intent);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent(this,NotificationMonitorService.class);
		stopService(intent);
	}
	
	@SuppressWarnings("rawtypes")
	void getBaseDada(String strBase) {
		try {
			//建立存储数据的集合
			baseDatas = new ArrayList<CheckBaseData>();
			SharedPreferences sp = this.getSharedPreferences("BaseDatas", Context.MODE_PRIVATE);
			//清空已存储的数据
			SharedPreferences.Editor editor = sp.edit();
			editor.clear();
			editor.commit();
			//获取基础数据
			basedataModel = new BaseDataModel();
			JSONObject respJson = new JSONObject(strBase);
			JSONObject basedata = respJson.getJSONObject("BaseData");
			//获取盘点下载字段
			JSONObject StockLabel = basedata.getJSONObject("StockLabel");
			{
				Iterator keys = StockLabel.keys();
				CheckBaseData checkBaseData;
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String value = StockLabel.get(key).toString();
					
					checkBaseData = new CheckBaseData();
					checkBaseData.setKey(key);
					checkBaseData.setValue(value);
					baseDatas.add(checkBaseData);
					
					basedataModel.StockLabel.put(key, value);
				}
			}
			//存储到数据提供者
			String string =  gson.toJson(baseDatas);
			Log.i("字段", string);
			editor.putString("BaseDatas", string);
			editor.commit();
			
		} catch (ParseException e) {
			
		} catch (JSONException e) {
			
		}

		if (m_pDialog != null) {
			m_pDialog.hide();
		}
	}

	@Override
	protected void AppInit() {
		SharedPreferences sp = this.getSharedPreferences("logindata",Context.MODE_PRIVATE);

		String saveDir = Environment.getExternalStorageDirectory() + "/webapi";
		File dir = new File(saveDir);
		if (dir.exists()) {
			String fileName = saveDir + "/" + "webapi.txt";

			try {
				File file = new File(fileName);
				FileInputStream inputFile = new FileInputStream(file);
				byte[] buffer = new byte[inputFile.available()];
				inputFile.read(buffer);

				if (buffer.length > 0) {
					ApiAddressHelper.SERVERHOST = EncodingUtils.getString(
							buffer, "UTF-8").trim();

					SharedPreferences.Editor editor = sp.edit();
					editor.putString( "webapi", ApiAddressHelper.SERVERHOST);
					editor.commit();
					Log.e("fileapi", ApiAddressHelper.SERVERHOST);
				}
				else{
					ApiAddressHelper.SERVERHOST = sp.getString("webapi", "http://58.134.112.6");
				}
				inputFile.close();
			} catch (Exception e) {
				ApiAddressHelper.SERVERHOST = sp.getString("webapi", "http://58.134.112.6");
			}
		}
		else{
			ApiAddressHelper.SERVERHOST = sp.getString("webapi", "http://58.134.112.6");
		}

		String flag = sp.getString("flagLogin", null);
		mobile = sp.getString("mobile", "");
		pwd = sp.getString("pwd", "");
		if (mobile.length() > 0 && pwd.length() > 0) {
			String strlogin = sp.getString("logindata" + mobile, "");
			if (strlogin.length() > 0) {
				try {
					loginModel = gson.fromJson(strlogin,
							new TypeToken<LoginDataModel>() {
					}.getType());
				} catch (JsonSyntaxException localJsonSyntaxException) {
				}
			}
		}

		if (flag == null || flag.equals("No")) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			return;
		}
	}

	@Override
	protected void DataInit() {

		if (loginModel == null) {
			return;
		}

		if (mobile != null && mobile.length() > 0) {
			SharedPreferences sp = this.getSharedPreferences("basedata",
					Context.MODE_PRIVATE);

			String strbase = sp.getString("basedata" + mobile, "");

			if (strbase.length() > 0) {
				this.getBaseDada(strbase);
				return;
			}
		}

		if (basedataModel == null) {
			this.param.clear();

			doAsync(new CallEarliest<String>() {
				public void onCallEarliest() throws Exception {

					m_pDialog = new ProgressDialog(CheckMenuActivity.this);
					m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					m_pDialog.setMessage("正在获取基础数据...");
					m_pDialog.setCancelable(false);
					m_pDialog.show();

				}
			}, new Callable<String>() {
				public String call() throws Exception {
					return HttpHelper.getInstance(context).Post(
							ApiAddressHelper.getBaseDataUrl(), param);
				}
			}, new Callback<String>() {
				public void onCallback(String res) {
					Log.e("basedata", res);
					if (res.equals("")) {
						m_pDialog.hide();
						Common.ShowInfo(context, "网络故障");
						return;
					}

					SharedPreferences sp = CheckMenuActivity.this
							.getSharedPreferences("basedata",
									Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("basedata" + mobile, res);
					editor.commit();

					CheckMenuActivity.this.getBaseDada(res);
				}
			});
		}
	}

	@Override
	protected void ViewInit() {
		// TODO Auto-generated method stub

		setContentView(R.layout.activity_menu);
		RelativeLayout rl = (RelativeLayout)this.findViewById(R.id.rlcode);
		rl.setVisibility(View.VISIBLE);
		LinearLayout ll = (LinearLayout) this.findViewById(R.id.tv_back);
		if (ll != null) {
			ll.setVisibility(View.GONE);
		}
		Button btn = (Button) this.findViewById(R.id.btn_title_left);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(this);

		btn = (Button) this.findViewById(R.id.btn_title_right);
		btn.setText("注销");
		btn.setOnClickListener(this);

		TextView tv = (TextView) this.findViewById(R.id.tv_title_name);
		tv.setText("资产盘点系统");

		GridView gv = (GridView) this.findViewById(R.id.gridview);
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map01 = new HashMap<String, Object>();
		map01.put("ItemImage", R.drawable.check_download);// 添加图像资源的ID
		map01.put("ItemImageSelect", R.drawable.check_download01);
		map01.put("ItemText", "盘点下载");// 按序号做ItemText
		map01.put("menuid", "offline_download");// 按序号做ItemText
		lstImageItem.add(map01);

		HashMap<String, Object> map02 = new HashMap<String, Object>();
		map02.put("ItemImage", R.drawable.lixian_check);// 添加图像资源的ID
		map02.put("ItemImageSelect", R.drawable.lixian_check01);
		map02.put("ItemText", "离线盘点");// 按序号做ItemText
		map02.put("menuid", "offline_check");// 按序号做ItemText
		lstImageItem.add(map02);

		HashMap<String, Object> map03 = new HashMap<String, Object>();
		map03.put("ItemImage", R.drawable.check_statistics);// 添加图像资源的ID
		map03.put("ItemImageSelect", R.drawable.check_statistics01);
		map03.put("ItemText", "盘点统计");// 按序号做ItemText
		map03.put("menuid", "offline_statistics");// 按序号做ItemText
		lstImageItem.add(map03);

		HashMap<String, Object> map04 = new HashMap<String, Object>();
		map04.put("ItemImage", R.drawable.check_upload);// 添加图像资源的ID
		map04.put("ItemImageSelect", R.drawable.check_upload01);
		map04.put("ItemText", "盘点上传");// 按序号做ItemText
		map04.put("menuid", "offline_upload");// 按序号做ItemText
		lstImageItem.add(map04);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.people_check);// 添加图像资源的ID
		map.put("ItemImageSelect", R.drawable.people_check01);
		map.put("ItemText", "个人核查");// 按序号做ItemText
		map.put("menuid", "people_check");// 按序号做ItemText
		lstImageItem.add(map);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("ItemImage", R.drawable.people_upload);// 添加图像资源的ID
		map2.put("ItemImageSelect", R.drawable.people_upload01);
		map2.put("ItemText", "推送消息");// 按序号做ItemText
		map2.put("menuid", "getui");// 按序号做ItemText
		lstImageItem.add(map2);

		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("ItemImage", R.drawable.people_center);// 添加图像资源的ID
		map3.put("ItemImageSelect", R.drawable.people_center01);
		map3.put("ItemText", "个人中心");// 按序号做ItemText
		map3.put("menuid", "local_personalcenter");// 按序号做ItemText
		lstImageItem.add(map3);

		GridViewAdapter adapter = new GridViewAdapter(this,lstImageItem);
		// 添加并且显示
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new ItemClickListener());
		
	}

	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub

	}

	class  ItemClickListener implements OnItemClickListener  
	{  
		public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened   
				View arg1,//The view within the AdapterView that was clicked  
				int arg2,//The position of the view in the adapter  
				long arg3//The row id of the item that was clicked  
				) 
		{  
			EditText et = (EditText)CheckMenuActivity.this.findViewById(R.id.codevalue);
			StockRightCode = et.getText().toString();

			@SuppressWarnings("unchecked")
			HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);  
			String MenuId = (String)item.get("menuid");
			title = (String)item.get("ItemText");
			CheckMenuActivity.this.MenuId = MenuId;
			if(  MenuId.equals("offline_download") )
			{
				getBatch(MenuId);
//				Intent intent = new Intent(CheckMenuActivity.this, CheckChooseActivity.class);
//
//				intent.putExtra("MenuId", MenuId);
//				intent.putExtra("title", title);
//				CheckMenuActivity.this.startActivity(intent);
			}
			else if(  MenuId.equals("offline_check") || MenuId.equals("offline_statistics"))
			{
				SharedPreferences sp = CheckMenuActivity.this.getSharedPreferences("checkdata", Context.MODE_PRIVATE);
				String strlist = sp.getString("check"+mobile, null);

				if( strlist != null  && strlist.equals("[]")==false)
				{
					List<SaveCheckDataModel> listSave = gson
							.fromJson(
									strlist,
									new TypeToken<List<SaveCheckDataModel>>() {
									}.getType());

					if( CheckChooseActivity.listBatch == null )
					{
						CheckChooseActivity.listBatch = new ListMsStockModel();
					}


					if( CheckChooseActivity.listBatch.MsStock != null )
					{
						CheckChooseActivity.listBatch.MsStock.clear();
					}
					else
					{
						CheckChooseActivity.listBatch.MsStock = new ArrayList<MsStockModel>();
					}

					for( SaveCheckDataModel model : listSave )
					{
						CheckChooseActivity.listBatch.MsStock.add(model.MsStock);
					}

					Intent intent = new Intent(CheckMenuActivity.this, CheckChooseActivity.class);
					intent.putExtra("MenuId", MenuId);
					intent.putExtra("title", title);
					CheckMenuActivity.this.startActivity(intent);	
				}
			}
			else if( MenuId.equals("offline_upload")){
				SharedPreferences sp = CheckMenuActivity.this.getSharedPreferences("checkdata", Context.MODE_PRIVATE);
				String strlist = sp.getString("check"+mobile, null);
				SharedPreferences sp2 = CheckMenuActivity.this.getSharedPreferences("peoplecheckdata", Context.MODE_PRIVATE);
				String strlist2 = sp2.getString("check"+mobile, null);
				if( (strlist != null && !strlist.equals("[]")) || (strlist2 !=null && !strlist2.equals("[]")))
				{
					List<SaveCheckDataModel> listSave = gson
							.fromJson(
									strlist,
									new TypeToken<List<SaveCheckDataModel>>() {
									}.getType());

//					List<SaveCheckDataModel> listSave2 = gson
//							.fromJson(
//									strlist2,
//									new TypeToken<List<SaveCheckDataModel>>() {
//									}.getType());

					if( CheckChooseActivity.listBatch == null )
					{
						CheckChooseActivity.listBatch = new ListMsStockModel();
					}


					if( CheckChooseActivity.listBatch.MsStock != null )
					{
						CheckChooseActivity.listBatch.MsStock.clear();
					}
					else
					{
						CheckChooseActivity.listBatch.MsStock = new ArrayList<MsStockModel>();
					}
					if( strlist != null){
						for( SaveCheckDataModel model : listSave )
						{
							CheckChooseActivity.listBatch.MsStock.add(model.MsStock);
						}
					}
//					if(strlist2 != null){
//						for( SaveCheckDataModel model : listSave2 )
//						{
//							CheckChooseActivity.listBatch.MsStock.add(model.MsStock);
//						}
//					}

					Intent intent = new Intent(CheckMenuActivity.this, CheckChooseActivity.class);
					intent.putExtra("MenuId", MenuId);
					intent.putExtra("title", title);
					CheckMenuActivity.this.startActivity(intent);	
				}
			}
			else if( MenuId.equals("people_check")){
				SharedPreferences sp = CheckMenuActivity.this.getSharedPreferences("peoplecheckdata", Context.MODE_PRIVATE);
				String strlist = sp.getString("check"+mobile, null);
				if(strlist!=null && !strlist.equals("[]")){
					Intent intent = new Intent(CheckMenuActivity.this, PeopleCheckActivity.class);
					intent.putExtra("MenuId", MenuId);
					intent.putExtra("title", title);

					CheckMenuActivity.this.startActivity(intent);
				}
			}
			else if(MenuId.equals("getui")){
				Intent intent = new Intent(CheckMenuActivity.this, GetuiActivity.class);
				intent.putExtra("MenuId", MenuId);
				intent.putExtra("title", title);
				CheckMenuActivity.this.startActivity(intent);
			}else if (MenuId.equals("local_personalcenter")) {
				Intent intent = new Intent(CheckMenuActivity.this,
						PersonalCenterActivity.class);
				CheckMenuActivity.this.startActivity(intent);
			}
		}  
	}

	private void getBatch(String MenuId)
	{
		CheckMenuActivity.this.Prepare(param);
		if( MenuId.equals("online_check") || MenuId.equals("online_statistics") )
		{
			param.put("MenuId", "419");
		}
		else
		{
			param.put("MenuId", "104");
		}
		param.put("StockRightCode", StockRightCode);

		
		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {

				m_pDialog = new ProgressDialog(CheckMenuActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在获取数据...");
				m_pDialog.setCancelable(false);
				m_pDialog.show();

			}
		}, new Callable<String>() {
			public String call() throws Exception {
				return HttpHelper.getInstance(context).Post(
						ApiAddressHelper.getOnLineBatch(), param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {
				m_pDialog.hide();
				if (res.equals("")) {
					return;
				}
				try {
					RequestRetModel<ListMsStockModel> model = gson
							.fromJson(
									res,
									new TypeToken<RequestRetModel<ListMsStockModel>>() {
									}.getType());
					if (model != null) 
					{
						CheckChooseActivity.listBatch = model.rspcontent;
//						
					}
				} catch (JsonSyntaxException localJsonSyntaxException) {
				}
				Intent intent = new Intent(CheckMenuActivity.this, CheckChooseActivity.class);

				intent.putExtra("MenuId", "offline_download");
				intent.putExtra("title", title);
				CheckMenuActivity.this.startActivity(intent);
			}
		});
	}

	//获取部门盘点批次信息
	private void getOnLineBatch()
	{
		CheckMenuActivity.this.Prepare(param);
		param.put("MenuId", "419");
		param.put("StockRightCode", StockRightCode);

		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {

				m_pDialog = new ProgressDialog(CheckMenuActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在获取数据...");
				m_pDialog.setCancelable(false);
				m_pDialog.show();

			}
		}, new Callable<String>() {
			public String call() throws Exception {
				return HttpHelper.getInstance(context).Post(
						ApiAddressHelper.getOnLineBatch(), param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {
				m_pDialog.hide();
				if (res.equals("")) {

					Common.ShowInfo(context, "网络故障");
					return;
				}
				try {
					RequestRetModel<ListMsStockModel> model = gson
							.fromJson(
									res,
									new TypeToken<RequestRetModel<ListMsStockModel>>() {
									}.getType());
					if (model != null) 
					{
						CheckChooseActivity.listBatch = model.rspcontent;
					} 
					else 
					{
						ToastUtil.showToast(context, "网络异常");
					}
				} catch (JsonSyntaxException localJsonSyntaxException) {
				}
			}
		});
	}
	
	private void getCheckBatch()
	{
		CheckMenuActivity.this.Prepare(param);
		param.put("MenuId", "419");
		param.put("StockRightCode", StockRightCode);

		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {

			}
		}, new Callable<String>() {
			public String call() throws Exception {
				return HttpHelper.getInstance(context).Post(
						ApiAddressHelper.getCheckBatch(), param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {
				if (res.equals("")) {

					return;
				}
				try {
					RequestRetModel<ListMsStockModel> model = gson
							.fromJson(
									res,
									new TypeToken<RequestRetModel<ListMsStockModel>>() {
									}.getType());
					if (model != null){
						CheckChooseActivity.checkBatch = model.rspcontent;
						flag ="ok";
					} 
				} catch (JsonSyntaxException localJsonSyntaxException) {
				}
			}
		});
	}

	@Override
	protected void Init(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	void createIdToName(MenuModel model) {
		if (model != null) {
			mapIdToName.put(model.MenuId, model.MenuName);

			if (model.SubMenu != null) {
				for (MenuModel submodel : model.SubMenu) {
					createIdToName(submodel);
				}
			}

		}
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_title_right: {
			new AlertDialog.Builder(CheckMenuActivity.this)
			.setTitle("提示")
			// 设置对话框标题
			.setMessage("确认要注销吗？")
			// 设置显示的内容
			.setPositiveButton("是",
					new DialogInterface.OnClickListener() {// 添加确定按钮
				@Override
				public void onClick(DialogInterface dialog,
						int which) {// 确定按钮的响应事件
					cancel();
					return;
				}
			})
			.setNegativeButton("否",
					new DialogInterface.OnClickListener() {// 添加返回按钮
				@Override
				public void onClick(DialogInterface dialog,
						int which) {// 响应事件

				}
			}).show();// 在按键响应事件中显示此对话框
		}
		break;
		
		//刷新按钮监听
		case R.id.btn_title_left:
			new AlertDialog.Builder(CheckMenuActivity.this)
			.setTitle("提示")
			// 设置对话框标题
			.setMessage("确认要刷新基础数据吗？")
			// 设置显示的内容
			.setPositiveButton("是",
					new DialogInterface.OnClickListener() {// 添加确定按钮
				@Override
				public void onClick(DialogInterface dialog,
						int which) {// 确定按钮的响应事件
					login();
				}
				
			})
			.setNegativeButton("否",
					new DialogInterface.OnClickListener() {// 添加返回按钮
				@Override
				public void onClick(DialogInterface dialog,
						int which) {// 响应事件

				}
			}).show();// 在按键响应事件中显示此对话框
			break;
		}
	}
	
	//注销方法
	private void cancel(){
		SharedPreferences sp = CheckMenuActivity.this.getSharedPreferences("logindata", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
//		editor.putString( "logindata"+mobile, null);
		editor.putString("flagLogin", "No");
		editor.commit();
		CheckChooseActivity.listBatch = null;
		CheckChooseActivity.checkBatch = null;
		
		Intent intent = new Intent(CheckMenuActivity.this,LoginActivity.class);
		startActivity(intent);
		finish();
	}

	//刷新方法
	private void login() {
		CheckMenuActivity.this.param.clear();
		CheckMenuActivity.this.param.put("loginname",mobile);
		CheckMenuActivity.this.param.put("pwd", pwd);
		CheckMenuActivity.this.param.put("maccode", 
				deviceId);

		doAsync(new CallEarliest<String>() {
			public void onCallEarliest()
					throws Exception {

				m_pDialog = new ProgressDialog(CheckMenuActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在获取登录数据...");
				m_pDialog.setCancelable(false);
				m_pDialog.show();

			}
		}, new Callable<String>() {
			public String call() throws Exception {
				return HttpHelper.getInstance(
						context).Post(
								ApiAddressHelper
								.getLoginUrl(),
								param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {

				if (res.equals("")) {
					m_pDialog.hide();
					Common.ShowInfo(context, "网络故障");
					return;
				}
				try {
					loginModel = gson
							.fromJson(
									res,
									new TypeToken<LoginDataModel>() {
									}.getType());
					if (loginModel != null) {

						SharedPreferences sp = CheckMenuActivity.this.getSharedPreferences(
								"logindata",Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("mobile",mobile);
						editor.putString("pwd", pwd);
						editor.putString("logindata"+ mobile,res);
						editor.commit();

						doAsync(new CallEarliest<String>() {
							public void onCallEarliest() throws Exception {
								m_pDialog.setMessage("正在获取基础数据...");
							}
						}, new Callable<String>() {
							public String call()throws Exception {
								return HttpHelper.getInstance(context).Post(
										ApiAddressHelper.getBaseDataUrl(),param);
							}
						}, new Callback<String>() {
							public void onCallback(String res) {
								if (res.equals("")) {
									m_pDialog.hide();Common.ShowInfo(context,"网络故障");
									return;
								}

								SharedPreferences sp = CheckMenuActivity.this.getSharedPreferences(
												"basedata",Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								editor.putString("basedata"+ mobile,res);
								editor.commit();
								CheckMenuActivity.this.getBaseDada(res);
							}
						});

					} else {
						m_pDialog.hide();
						Common.ShowInfo(context,"登陆异常");
					}
				} catch (JsonSyntaxException localJsonSyntaxException) {
					m_pDialog.hide();
					Log.e("iws","Login json转换错误 e:"+ localJsonSyntaxException);
				}
			}
		});
	}

	@Override
	protected void Destroy() {
		// TODO Auto-generated method stub
		
	}
	
}
