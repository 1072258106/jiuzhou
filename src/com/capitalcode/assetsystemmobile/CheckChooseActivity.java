package com.capitalcode.assetsystemmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.adapter.PeopleCheckAdapter;
import com.liu.AssetsScan.async.CallEarliest;
import com.liu.AssetsScan.async.Callable;
import com.liu.AssetsScan.async.Callback;
import com.liu.AssetsScan.model.DetailCheckModel;
import com.liu.AssetsScan.model.Getui;
import com.liu.AssetsScan.model.ListMsStockModel;
import com.liu.AssetsScan.model.MsStockModel;
import com.liu.AssetsScan.model.OverageAssets;
import com.liu.AssetsScan.model.RequestRetModel;
import com.liu.AssetsScan.model.SaveCheckDataModel;
import com.liu.AssetsScan.model.StockRightCodeModel;
import com.liu.AssetsScan.util.ApiAddressHelper;
import com.liu.AssetsScan.util.DatabaseHelper;
import com.liu.AssetsScan.util.HttpHelper;
import com.liu.AssetsScan.util.ListDataSave;
import com.liu.AssetsScan.util.StaticUtil;
import com.liu.AssetsScan.util.ToastUtil;

//控制盘点下载，明细，上传，盘点等功能界面
public class CheckChooseActivity extends BaseActivity {

	private static List<HashMap<String, Object>> MsAssetDatas;
	public static ListMsStockModel listBatch;	//下载的部门盘点批次集合
	public static ListMsStockModel checkBatch;  //下载的个人核查批次集合
	private List<String> BatchList;
	private TextView BatchMemo;
	private TextView BatchState;
	
	private TextView StockTimeStart;
	private SQLiteDatabase batchSqlData;
	private List<Getui> list;
	private List<SaveCheckDataModel> listSaveBatch;
	private ListView listView;
	
	private Spinner mSpinner;
	private String peoplebatchNumble; //已经下载的个人核查批次编号
	
	private ListDataSave saveBatch;	//保存已经下载的批次编号偏好设置
	private String spName = "DownloadBatch"; //偏好设置的名字
	
	private String spPeopleName = "DownPeopleBatch";
	
	private Spinner spinner;
	
	private String zzbh;
	private String zzlb;
	private List<OverageAssets> overageAssets;
	private Button left_checkChoose,right_checkChoose,single_checkChoose;
	private List<Map<String,Object>> UpLoadMsAsset = new ArrayList<Map<String,Object>>();
	private String batchid;
	//用于下载识别的批次编号
	private String batchNumber;
	//用于监听listView
	private String listenerFlag = "自动";
	
	private String[] mItems; //用于保存盘点类别的数组
	private String[] BatchmItems;	//用于保存访问网络获取的资产编号

	//存储未读的个人核查批次集合
	private List<String> PeopleBatchs;
	
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

	@Override
	protected void ViewInit() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_check_choose);
		BatchMemo = (TextView) CheckChooseActivity.this.findViewById(R.id.tv_show);
		BatchState = (TextView) CheckChooseActivity.this.findViewById(R.id.tv_state);
		StockTimeStart = (TextView) CheckChooseActivity.this.findViewById(R.id.tv_time);
		left_checkChoose = (Button) findViewById(R.id.btn_left_checkChoose_onlone);
		right_checkChoose = (Button) findViewById(R.id.btn_right_checkChoose_onlone);
		single_checkChoose = (Button) findViewById(R.id.btn_single_checkChoose_onlone);

		openSQL();
		creatTable();
		batchSqlData.close();
		getCheckBatch();
		//初始化下载批次编号集合
		BatchList = new ArrayList<String>();
		saveBatch = new  ListDataSave(this, spName);
		
		if(listBatch!=null){
			mItems = new String[2];
			mItems[0] = "部门盘点";
			mItems[1] = "个人核查";
		}else{
			mItems = new String[1];
			mItems[0] = "个人核查";
		}

		if(MenuId.equals("offline_upload")){
			StaticUtil.upload = "上传";
		}else{
			StaticUtil.upload = "其他";
		}

		TextView tv = (TextView) this.findViewById(R.id.tv_title_name);
		tv.setText(this.getIntent().getStringExtra("title"));

		Button btn = (Button) this.findViewById(R.id.btn_title_right);
		if (MenuId.equals("online_check") || MenuId.equals("online_statistics")
				|| MenuId.equals("offline_download")) {

			btn.setVisibility(View.VISIBLE);
			btn.setText("获取授权码");

			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(StaticUtil.getSpinner().equals("个人核查")){
						return;
					}
					CheckChooseActivity.this.Prepare(param);
					param.put("MenuId", "104");

					int position = mSpinner.getSelectedItemPosition();
					MsStockModel model = listBatch.MsStock.get(position);

					Map<String, String> map = new HashMap<String, String>();
					map.put("BatchId", model.BatchId);

					param.put("MsStock", map);

					doAsync(new CallEarliest<String>() {
						public void onCallEarliest() throws Exception {

							m_pDialog = new ProgressDialog(
									CheckChooseActivity.this);
							m_pDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							m_pDialog.setMessage("正在获取数据...");
							m_pDialog.setCancelable(false);
							m_pDialog.show();

						}
					}, new Callable<String>() {
						public String call() throws Exception {
							return HttpHelper.getInstance(context)
									.Post(ApiAddressHelper.getStockRightCode(),
											param);
						}
					}, new Callback<String>() {
						public void onCallback(String res) {
							m_pDialog.hide();
							if (res.equals("")) {

								ToastUtil.showToast(context, "网络故障");
								return;
							}
							try {
								RequestRetModel<StockRightCodeModel> model = gson
										.fromJson(
												res,
												new TypeToken<RequestRetModel<StockRightCodeModel>>() {
												}.getType());
								if (model != null) {
									String StockRightCode = model.rspcontent.StockRightCode;
									RelativeLayout rl = (RelativeLayout) CheckChooseActivity.this
											.findViewById(R.id.author);
									rl.setVisibility(View.VISIBLE);

									TextView tv = (TextView) CheckChooseActivity.this
											.findViewById(R.id.tv_author);
									tv.setText(StockRightCode);
								} else {

									ToastUtil.showToast(context, "网络异常");
								}
							} catch (JsonSyntaxException localJsonSyntaxException) {

							}
						}
					});
				}
			});
		} else {
			btn.setVisibility(View.GONE);
		}

		String flagRead;
		if(MenuId.equals("offline_download")){
			flagRead = "未读";
			getDataforSpinner(flagRead);
		}
		if(MenuId.equals("offline_upload")){
			flagRead = "已读";
			getDataforSpinner(flagRead);
		}
		//这里为显示盘点下载，离线盘点，盘点统计，盘点上传的批次信息
		mSpinner = (Spinner) findViewById(R.id.spinner_choose);
		//设置批次编号
		setBatchNumber();

		if (MenuId.equals("online_check") || MenuId.equals("online_statistics")) {
			if (MenuId.equals("online_check")) {
				single_checkChoose.setText("盘点");
			} else {
				single_checkChoose.setText("明细");
			}

			single_checkChoose.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int position = mSpinner.getSelectedItemPosition();
					MsStockModel model = listBatch.MsStock.get(position);

					if (MenuId.equals("online_check")) {
						Intent intent = new Intent(CheckChooseActivity.this,
								CheckActivity.class);


						intent.putExtra("MenuId", MenuId);
						intent.putExtra("BatchId", model.BatchId);

						CheckChooseActivity.this.startActivity(intent);
					} else if (MenuId.equals("online_statistics")) {
						//判断是否有批次存在
						if(listBatch.MsStock.isEmpty()){
							return;
						}
						Intent intent = new Intent(CheckChooseActivity.this,
								CheckDetailActivity.class);
						intent.putExtra("MenuId", MenuId);
						intent.putExtra("BatchId", model.BatchId);

						CheckChooseActivity.this.startActivity(intent);
					}

				}
			});

		} else {

			if (MenuId.equals("offline_download")) {
				left_checkChoose.setVisibility(View.GONE);
				right_checkChoose.setVisibility(View.GONE);
				single_checkChoose.setVisibility(View.VISIBLE);

				listView = (ListView) this.findViewById(R.id.lv_getui_check);
				listView.setVisibility(View.VISIBLE);
				setDataForSpinner(mItems);//设置部门盘点或者个人核查
				setDataForList();
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						//设置个人盘点或者部门盘点
						String[] CheckmItems = new String[1];

						//设置盘点批次号
						zzbh = list.get(position).getZzbh();
						zzlb = list.get(position).getZzlb();
						listenerFlag = "监听";
						CheckmItems[0] = zzlb;
						setDataForSpinner(CheckmItems);
					}
				});

				single_checkChoose.setText("下载");
				single_checkChoose.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Log.i("资产类别", StaticUtil.getSpinner());
						if(StaticUtil.getSpinner().equals("部门盘点")){
							downloadBatch();
						}
						if(StaticUtil.getSpinner().equals("个人核查")){
							//							downloadBatch();
							downloadPeople();
						}
					}
				});

			} else if (MenuId.equals("offline_check")) {
				left_checkChoose.setVisibility(View.VISIBLE);
				single_checkChoose.setVisibility(View.GONE);
				left_checkChoose.setText("盘点");
				left_checkChoose.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						//如果没有批次则不能进行盘点						
						if(listBatch.MsStock.isEmpty()){
							return;
						}
						int position = mSpinner.getSelectedItemPosition();
						MsStockModel model = listBatch.MsStock.get(position);

						Intent intent = new Intent(CheckChooseActivity.this,
								CheckActivity.class);


						intent.putExtra("MenuId", MenuId);
						intent.putExtra("BatchId", model.BatchId);

						CheckChooseActivity.this.startActivity(intent);
					}
				});

			} else if (MenuId.equals("offline_statistics")) {
				left_checkChoose.setVisibility(View.VISIBLE);
				single_checkChoose.setVisibility(View.GONE);
				left_checkChoose.setText("删除");
				left_checkChoose.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						//判断是否存在该批次
						if(listBatch.MsStock.isEmpty()){
							BatchMemo.setText(null);
							BatchState.setText(null);
							StockTimeStart.setText(null);
							ToastUtil.showToast(CheckChooseActivity.this, "没有可以删除的批次");
							return;
						}
						//提示是否删除
						dialog();
					}
				});

			} else if (MenuId.equals("offline_upload")) {
				left_checkChoose.setVisibility(View.VISIBLE);
				single_checkChoose.setVisibility(View.GONE);
				left_checkChoose.setText("上传");
				setDataForSpinner(mItems);//设置部门盘点或者个人核查
				left_checkChoose.setOnClickListener(new View.OnClickListener() {

					@SuppressWarnings("unused")
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						int position = mSpinner.getSelectedItemPosition();
						SharedPreferences sp;
						String strflag = "盘点";
						if(StaticUtil.getSpinner().equals("个人核查")){
							strflag = "核查";
							sp = CheckChooseActivity.this
									.getSharedPreferences("peoplecheckdata",
											Context.MODE_PRIVATE);
						}else {
							sp = CheckChooseActivity.this
									.getSharedPreferences("checkdata",
											Context.MODE_PRIVATE);
						}
						String strlist = sp.getString("check" + mobile, null);
						if(strlist!=null){
							SharedPreferences.Editor editor = sp.edit();

							List<SaveCheckDataModel> listSave = gson.fromJson(
									strlist,
									new TypeToken<List<SaveCheckDataModel>>() {
									}.getType());

							for (SaveCheckDataModel item : listSave) 
							{
								if (item.MsStock.BatchNumer.equals(batchNumber)) 
								{
									//上传盘盈资产
									overageAssets = item.MsPYInfo;

									String string2 = gson.toJson(overageAssets);
									Log.i("上传的盘盈资产", string2);
									batchid = item.MsStock.BatchId;

									for (HashMap<String, Object> t : item.MsAsset) 
									{
										if( t.get("inventoryStateName").equals("盘亏") == false &&
												t.get("inventoryStateName").equals("未核查") == false &&
												t.get("inventoryStateName").equals("盘盈") == false)
										{
											Map<String,Object> map = new HashMap<String,Object>();
											map.put("AssetCode", t.get("assetCode"));
											map.put("SerialNumber", t.get("str9"));
											map.put("StockMemo", t.get("StockMemo"));
											map.put("AssetId", t.get("assetId"));
											map.put("ImgGuid",t.get("ImgGuid"));
											if(StaticUtil.getSpinner().equals("部门盘点")){
												map.put("StockState", "已盘");
											}
											if(StaticUtil.getSpinner().equals("个人核查")){
												map.put("StockState", "已核查");
											}

											UpLoadMsAsset.add(map);
										}
									}

									if( UpLoadMsAsset.size() == 0 && (overageAssets==null || overageAssets.size()==0) )
									{
										new AlertDialog.Builder(CheckChooseActivity.this).setTitle("提示")//设置对话框标题  		  
										.setMessage("该批次未"+strflag+"过，不需要上传!")//设置显示的内容  
										.setPositiveButton("是",new DialogInterface.OnClickListener() {//添加确定按钮  
											@Override  
											public void onClick(DialogInterface dialog, int which) 
											{//确定按钮的响应事件  

											}  
										}).show();//在按键响应事件中显示此对话框
										break;

									}
								}
							}
							//上传盘盈资产信息
							if(overageAssets!=null){
								uploadOverage();
							}

							//上传已盘的资产
							if(UpLoadMsAsset.size()!=0){
								uploadAsset();
							}

							if(UpLoadMsAsset.size()==0){
								return;
							}
						}
					}
				});

			}

			if(MenuId!="offline_download"){
				right_checkChoose.setVisibility(View.VISIBLE);
			}
			right_checkChoose.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//判断是否有可查看批次
					if(listBatch.MsStock.isEmpty()){
						ToastUtil.showToast(context, "没有可以查看的批次信息");
						return;
					}
					if(StaticUtil.getSpinner().equals("个人核查")){
						ToastUtil.showToast(context, "请在个人核查界面查看批次信息");
						return;
					}
					int position = mSpinner.getSelectedItemPosition();
					MsStockModel model = listBatch.MsStock.get(position);

					SharedPreferences sp = CheckChooseActivity.this.getSharedPreferences("checkdata", Context.MODE_PRIVATE);
					String strlist = sp.getString("check"+mobile, null);
					if( strlist != null )
					{
						List<SaveCheckDataModel> listSave = gson
								.fromJson(
										strlist,
										new TypeToken<List<SaveCheckDataModel>>() {
										}.getType());

						for( SaveCheckDataModel item :  listSave )
						{
							if(item.MsStock.BatchId.equals(model.BatchId))
							{
								Intent intent = new Intent(CheckChooseActivity.this,
										CheckDetailActivity.class);

								intent.putExtra("MenuId", "offline_statistics");
								intent.putExtra("BatchId", model.BatchId);
								Log.i("传递给明细界面的批次编号", batchNumber);
								intent.putExtra("BatchNumber", batchNumber);
								CheckChooseActivity.this.startActivity(intent);
								break;
							}
						}
					}
				}
			});
		}
	}

	//为个推消息设置数据
	//查询未读的推送消息显示在listview
	private void setDataForList() {
		list = new ArrayList<Getui>();
		String userName = loginModel.LoginUser.UserName;
		DatabaseHelper helper = new DatabaseHelper(CheckChooseActivity.this, "MessageTwo.db");
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query("MessageTable", new String[]{
				"message",
				"zzlb",
				"zzbh"
		},"zzflag=? and user=?", new String[]{"未读",userName}, null, null, null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				Getui getui = new Getui();
				getui.setContents(cursor.getString(0));
				getui.setZzlb(cursor.getString(1));
				getui.setZzbh(cursor.getString(2));
				getui.setReadflag("未读");
				list.add(getui);
			}
			cursor.close();
		}
		if(list!=null){
			PeopleCheckAdapter adapter = new PeopleCheckAdapter(CheckChooseActivity.this, list);
			listView.setAdapter(adapter);
		}
	}

	//更细推送消息状态（已读，未读）
	private void updatafordata(String zzlb,String zzbh) {
		String userName = loginModel.LoginUser.UserName;
		DatabaseHelper helper = new DatabaseHelper(CheckChooseActivity.this, "MessageTwo.db");
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();  
		values.put("zzflag", "已读"); 
		database.update("MessageTable", values, "zzlb=? and zzbh=? and user=?", new String[] { zzlb,zzbh,userName });  
	}

	//获取未读的个人核查资产编号
	private void getDataforSpinner(String flag){
		PeopleBatchs = new ArrayList<String>();
		String userName = loginModel.LoginUser.UserName;
		DatabaseHelper helper = new DatabaseHelper(CheckChooseActivity.this, "MessageTwo.db");
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query("MessageTable", new String[]{
				"zzbh"
		},"zzflag=? and user=? and zzlb=?", new String[]{flag,userName,"个人核查"}, null, null, null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				String batch = cursor.getString(0);
				PeopleBatchs.add(batch);
			}
			cursor.close();
		}
	}

	//个人核查与部门盘点选择适配
	private void setDataForSpinner(String[] mItems){
		RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.check_lei);
		layout.setVisibility(View.VISIBLE);
		spinner = (Spinner) this.findViewById(R.id.spinner_chech_lei);
		ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mItems);

		spinner.setAdapter(_Adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				String str = parent.getItemAtPosition(position).toString();
				Log.i("选中的批次类", str);
				if(str.equals("部门盘点")){
					//设置部门盘点的批次集合
					StaticUtil.setSpinner("部门盘点");
					setBatchNumber();
				}
				if(str.equals("个人核查")){
					//设置个人核查批次集合
					StaticUtil.setSpinner("个人核查");
					setBatchNumber();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
	}

	//批次信息设置
	private void setDataForBatchNumber(String[] Items){
		final SharedPreferences sp = this.getSharedPreferences(spPeopleName, Context.MODE_PRIVATE);
		ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Items);

		mSpinner.setAdapter(_Adapter);
		String string = gson.toJson(Items);
		Log.i("解析的什么", string);
		if(string.equals("[]")){
			setDataforDown(null,null);
			return;
		}
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				batchNumber = parent.getItemAtPosition(position).toString();
				Log.i("选中的批次编号", batchNumber);
				if(StaticUtil.getSpinner().equals("部门盘点")){
					MsStockModel stockModel = null;
					if(listBatch!=null){
						for(MsStockModel model:listBatch.MsStock){
							if(model.BatchNumer.equals(batchNumber)){
								stockModel = model;
							}
						}
					}
					//根据批次编号设置界面数据
					setDataforDown(batchNumber,stockModel);
				}
				if(StaticUtil.getSpinner().equals("个人核查")){
					MsStockModel stockModel = null;
					if(MenuId.equals("offline_download")){
						if(checkBatch!=null && checkBatch.MsStock.size()>0){
							for(MsStockModel model:checkBatch.MsStock){
								if(model.BatchNumer.equals(batchNumber)){
									stockModel = model;
								}
							}
						}
					}
					if(MenuId.equals("offline_upload")){
						List<MsStockModel> stockModels = new ArrayList<MsStockModel>();
						String string = sp.getString(spPeopleName+mobile, null);
						stockModels = gson.fromJson(
								string,
								new TypeToken<List<MsStockModel>>() {
								}.getType());
						if(list!=null && list.size()>0){
							for(MsStockModel model:stockModels){
								if(model.BatchNumer.equals(batchNumber)){
									stockModel = model;
								}
							}
						}
					}
					//根据批次编号设置界面数据
					setDataforDown(batchNumber,stockModel);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub

	}

	private void openSQL(){
		batchSqlData = this.openOrCreateDatabase("batchSqlData", MODE_PRIVATE, null);
	}

	private void creatTable(){
		String CREATE_TABLE = "create table if not exists BatchTable (" +
				"batchnumble TEXT PRIMARY KEY," +	//批次编号
				//				"batchid TEXT ," +		//批次id
				"people TEXT"+	//操作人
				");"; 
		batchSqlData.execSQL(CREATE_TABLE);
	}

	//从选择的批次编号来更新界面数据
	private void setDataforDown(String str,MsStockModel model) {
		peoplebatchNumble = str;
		if(model==null || str==null || str==""){
			BatchMemo.setText(null);
			BatchState.setText(null);
			StockTimeStart.setText(null);
			return;
		}
		BatchMemo.setText(model.BatchMemo);
		BatchState.setText(model.BatchState);
		StockTimeStart.setText(model.StockTimeStart);
	}

	//设置批次编号
	private void setBatchNumber(){
		//去除listBatch.MsStock里面的重复元素
		if(StaticUtil.getSpinner().equals("部门盘点") && listBatch!=null){
			if(listBatch.MsStock!=null){
				for(int i=0;i<listBatch.MsStock.size();i++){
					for(int j=listBatch.MsStock.size()-1;j>i;j--){
						if(listBatch.MsStock.get(i).BatchNumer.equals(listBatch.MsStock.get(j).BatchNumer)){
							listBatch.MsStock.remove(i);
						}
					}
				}
			}

			//去除盘点下载界面 已下载的批次
			if(MenuId.equals("offline_download")){
				BatchList = saveBatch.getDataList(spName+mobile);
				if(!listBatch.MsStock.isEmpty()){
					for(int i=0;i<listBatch.MsStock.size();i++){
						for(int j=0;j<BatchList.size();j++){
							if(listBatch.MsStock.get(i).BatchNumer.equals(BatchList.get(j))){
								listBatch.MsStock.remove(i);
							}
						}
					}
				}
			}
			
			BatchmItems = new String[listBatch.MsStock.size()];
			int n = 0;
			for (MsStockModel model : listBatch.MsStock) {
				BatchmItems[n++] = model.BatchNumer;
			}
			if(listenerFlag == "监听"){
				//需要将指定批次编号移到第一位
				for(int i=0;i<BatchmItems.length;i++){  
		            if(BatchmItems[i].equals(zzbh)){
		            	String string = BatchmItems[0];
		            	BatchmItems[0] = zzbh;
		            	BatchmItems[i] = string;
		            	break;
		            }
		        } 
				listenerFlag = "自动";
			}
			
			setDataForBatchNumber(BatchmItems);
			return;
		}
		
//		String people = "on"; //1：读取数据库的批次信息；2：读取已下载的批次信息
//		if(MenuId.equals("offline_download")){
//			people = "on";
//		}else{
//			people = "off";
//		}
		if(StaticUtil.getSpinner().equals("个人核查")){
			
			List<MsStockModel> stockModels = new ArrayList<MsStockModel>();
			SharedPreferences sp = this.getSharedPreferences(spPeopleName, Context.MODE_PRIVATE);
			String string = sp.getString(spPeopleName+mobile, null);
			stockModels = gson.fromJson(
					string,
					new TypeToken<List<MsStockModel>>() {
					}.getType());
			
			String[] strings = new String[0];
			if(MenuId.equals("offline_download")){
				if(checkBatch!=null && !checkBatch.MsStock.isEmpty()){
					//去除已经下载过的个人核查批次
					if(stockModels!=null){
						for(int i=0;i<checkBatch.MsStock.size();i++){
							for(int j=0;j<stockModels.size();j++){
								if(checkBatch.MsStock.get(i).BatchNumer.equals(stockModels.get(j).BatchNumer)){
									checkBatch.MsStock.remove(i);
								}
							}
						}
					}
					strings = new String[checkBatch.MsStock.size()];
					int n = 0;
					for (MsStockModel model : checkBatch.MsStock) {
						strings[n++] = model.BatchNumer;
					}
				}
			}
			if(MenuId.equals("offline_upload")){
				if(stockModels!=null){
					strings = new String[stockModels.size()];
					int n = 0;
					for (MsStockModel model : stockModels) {
						strings[n++] = model.BatchNumer;
					}
				}
			}
			
			if(listenerFlag == "监听"){
				//需要将指定批次编号移到第一位
				for(int i=0;i<strings.length;i++){  
		            if(strings[i].equals(zzbh)){
		            	String string2 = strings[0];
		            	strings[0] = zzbh;
		            	strings[i] = string2;
		            	break;
		            }
		        } 
				listenerFlag = "自动";
			}
			setDataForBatchNumber(strings);		
		}
	}

	//批次信息初始化与删除
	private void resetBatch(int position,int flag){

		//删除批次下资产信息
		MsStockModel model = listBatch.MsStock.get(position);

		SharedPreferences sp = CheckChooseActivity.this.getSharedPreferences("checkdata",Context.MODE_PRIVATE);
		String strlist = sp.getString("check" + mobile, null);
		SharedPreferences.Editor editor = sp.edit();

		List<SaveCheckDataModel> listSave = gson.fromJson(
				strlist,
				new TypeToken<List<SaveCheckDataModel>>() {
				}.getType());

		for (SaveCheckDataModel item : listSave) {
			if (item.MsStock.BatchId.equals(model.BatchId)) {
				List<HashMap<String, Object>> delitems = new ArrayList<HashMap<String,Object>>();
				for (HashMap<String, Object> t : item.MsAsset) {
					//资产全删除
					if(flag==1){
						delitems.add(t);
					}
					//资产初始化
					if(flag==2){
						if (t.get("inventoryStateName").equals("已盘")) {
							t.put("inventoryStateName", "盘亏");
							ToastUtil.showToast(CheckChooseActivity.this, "初始化成功");
						}
					}  
					if (t.get("inventoryStateName").equals("盘盈")) {
						delitems.add(t);
					}
				}
				item.MsAsset.removeAll(delitems);
				break;
			}
		}
		if(flag==1){
			//删除批次信息
			String BatchNumber = listBatch.MsStock.get(position).BatchNumer;
			deleteBatch(BatchNumber);
			deleteSavelistData(BatchNumber);
			listBatch.MsStock.remove(position);
			//重置spinner中的批次信息
			setBatchNumber();
		}
		//判断是否存在批次
		if(listBatch.MsStock.isEmpty()){
			BatchMemo.setText(null);
			BatchState.setText(null);
			StockTimeStart.setText(null);
			return;
		}

		//资产初始化
		if(flag==2){
			String saveStr = gson.toJson(listSave);
			editor.putString("check" + mobile, saveStr);
			editor.commit();
		}
	}

	//删除批次信息
	private void deleteBatch(String batchNumber) {
		//清除图片信息
		deleteSp("CheckPic"+batchNumber);
		//打开保存批次信息的数据提供者
		SharedPreferences sp = CheckChooseActivity.this.getSharedPreferences("checkdata", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		String strlist = sp.getString("check"+mobile, null);
		//		Log.i("下载时的批次信息", strlist.toString());

		listSaveBatch = new ArrayList<SaveCheckDataModel>();
		if( strlist != null ){
			listSaveBatch = gson.fromJson(strlist,new TypeToken<List<SaveCheckDataModel>>() {

			}.getType());
		}
		//删除指定批次信息
		for(int i=0;i<listSaveBatch.size();i++){
			if(listSaveBatch.get(i).MsStock.BatchNumer.equals(batchNumber)){
				listSaveBatch.remove(i);
				break;
			}
		}

		//重新保存批次信息
		String saveStr = gson.toJson(listSaveBatch);
		editor.putString("check" + mobile,saveStr);
		editor.commit();

	}

	//删除用来离线对比的批次编号
	private void deleteSavelistData(String batchNumber){
		BatchList = saveBatch.getDataList(spName+mobile);
		//		Log.i("删除前批次编号集合", BatchList.toString());
		for (int i = 0; i < BatchList.size(); i++) {
			if(BatchList.get(i).equals(batchNumber)){
				BatchList.remove(i);
				break;
			}
		}
		saveBatch.setDataList(spName+mobile, BatchList);
		//		Log.i("删除后批次编号集合", BatchList.toString());
	}

	//上传盘盈资产
	private void uploadOverage() {
		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {
				//				m_pDialog = new ProgressDialog(
				//						CheckChooseActivity.this);
				//				m_pDialog
				//				.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				//				m_pDialog.setMessage("正在上传盘盈信息...");
				//				m_pDialog.setCancelable(false);
				//				m_pDialog.show();
			}
		}, new Callable<String>() {

			public String call() throws Exception {
				CheckChooseActivity.this.Prepare(param);
				String string = gson.toJson(param);
				Log.i("盘盈上传的数据", string);
				
				param.put("MenuId", "104");
				param.put("batchid", batchid);
				param.put("MsPYInfo", overageAssets);
				param.put("StockRightCode",CheckMenuActivity.StockRightCode);
				return HttpHelper.getInstance(context).Post(
						ApiAddressHelper.uploadpyinfo(),
						param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {
				//				m_pDialog.hide();
				if (res.equals("")) {

					ToastUtil.showToast(context, "网络故障");
					return;
				}

				try {

					JSONObject respJson = new JSONObject(res);

					JSONObject response = respJson
							.getJSONObject("response");

					String ErrorCode = response
							.getString("ErrorCode");
					if (ErrorCode.equals("S00000") == false) {
						if (response.getString("ErrorMsg") != null) {
							ToastUtil.showToast(context, response
									.getString("ErrorMsg"));
						}
						return;
					} else {
						if (response.getString("ErrorMsg") != null) {
							overageAssets.clear();
							ToastUtil.showToast(context, response
									.getString("ErrorMsg"));
						}
					}

				} catch (JsonSyntaxException localJsonSyntaxException) {

				} catch (JSONException e) {

				}

			}

		});
	}

	//上传盘点资产
	private void uploadAsset(){
		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {
				String strflag = "盘点";
				if(StaticUtil.getSpinner().equals("个人核查")){
					strflag = "核查";
				}
				m_pDialog = new ProgressDialog(
						CheckChooseActivity.this);
				m_pDialog
				.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在上传"+strflag+"信息...");
				m_pDialog.setCancelable(false);
				m_pDialog.show();

			}
		}, new Callable<String>() {
			public String call() throws Exception {

				Map<String,Object> MsStock = new HashMap<String,Object>();
				CheckChooseActivity.this.Prepare(param);
				String string = gson.toJson(param);
				Log.i("盘点上传的数据", string);
				param.put("MenuId", "104");
				param.put("StockRightCode",CheckMenuActivity.StockRightCode);
				if(StaticUtil.getSpinner().equals("部门盘点")){
					MsStock.put("BatchId", batchid);
				}
				if(StaticUtil.getSpinner().equals("个人核查")){
					MsStock.put("BatchCode", peoplebatchNumble);
				}
				MsStock.put("MsAsset", UpLoadMsAsset);
				param.put("MsStock", MsStock);
				if(StaticUtil.getSpinner().equals("个人核查")){
					return HttpHelper.getInstance(context).Post(
							ApiAddressHelper.uppeopleloadBatchDetail(),
							param);
				}
				return HttpHelper.getInstance(context).Post(
						ApiAddressHelper.uploadBatchDetail(),
						param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {
				m_pDialog.hide();
				if (res.equals("")) {

					ToastUtil.showToast(context, "网络故障");
					return;
				}

				try {

					JSONObject respJson = new JSONObject(res);

					JSONObject response = respJson
							.getJSONObject("response");

					String ErrorCode = response
							.getString("ErrorCode");
					if (ErrorCode.equals("S00000") == false) {
						if (response.getString("ErrorMsg") != null) {
							ToastUtil.showToast(context, response.getString("ErrorMsg"));
						}

						return;
					} else {
						if (response.getString("ErrorMsg") != null) {

							UpLoadMsAsset.clear();
							//清除图片信息
							if(StaticUtil.getSpinner().equals("部门盘点")){
								deleteSp("CheckPic"+peoplebatchNumble);
							}
							if(StaticUtil.getSpinner().equals("个人核查")){
								deleteSp("PeoplePic"+peoplebatchNumble);
							}
							
							if(StaticUtil.getSpinner().equals("个人核查")){

								SharedPreferences sp = CheckChooseActivity.this.getSharedPreferences("peoplecheckdata",Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								List<SaveCheckDataModel> listSave;
								String strList = sp.getString("check"+ mobile, null);
								if (strList == null) {
									listSave = new ArrayList<SaveCheckDataModel>();
								} else {
									listSave = gson.fromJson(
											strList,
											new TypeToken<List<SaveCheckDataModel>>() {
											}.getType());
								}

								MsStockModel model = new MsStockModel();
								Log.i("个人核查批次编号", peoplebatchNumble);
								for (int i = 0; i < listSave.size(); i++) {
									model = listSave.get(i).MsStock;
									if(model.BatchNumer.equals(peoplebatchNumble)){
										Log.i("要被删除的批次编号", model.BatchNumer);
										listSave.remove(i);
									}
								}
								
								String saveStr = gson.toJson(listSave);
								Log.i("去除个人核查上传的批次信息", saveStr);
								editor.putString("check" + mobile,saveStr);
								editor.commit();

								DatabaseHelper helper = new DatabaseHelper(CheckChooseActivity.this, "MessageTwo.db");
								SQLiteDatabase database = helper.getReadableDatabase();
								database.delete("MessageTable", "zzbh=?", new String[]{""+peoplebatchNumble});

								openSQL();
								batchSqlData.delete("BatchTable", "batchnumble=?", new String[]{""+peoplebatchNumble});
								batchSqlData.close();
								setBatchNumber();
							}

							ToastUtil.showToast(context, response.getString("ErrorMsg"));
						}
					}

				} catch (JsonSyntaxException localJsonSyntaxException) {

				} catch (JSONException e) {

				}
			}
		});
	}

	//下载盘点批次
	private void downloadBatch(){
		mSpinner = (Spinner) findViewById(R.id.spinner_choose);

		CheckChooseActivity.this.Prepare(param);
		param.put("MenuId", "104");
		param.put("StockRightCode",
				CheckMenuActivity.StockRightCode);

		//判断是否有可下载批次
		if(listBatch.MsStock.isEmpty()){
			BatchMemo.setText(null);
			BatchState.setText(null);
			StockTimeStart.setText(null);
			ToastUtil.showToast(CheckChooseActivity.this, "没有可以下载的批次");
			return;
		}

		String string = gson.toJson(listBatch.MsStock);
		Log.i("能下载的批次信息", string);
		//判断个人核查下载批次是否为空
		//		MsStockModel model = listBatch.MsStock.get(position);
		MsStockModel model = null;
		for (int i = 0; i < listBatch.MsStock.size(); i++) {
			if (listBatch.MsStock.get(i).BatchNumer.equals(batchNumber)) {
				model = listBatch.MsStock.get(i);
			}
		}
		if(model==null ){
			ToastUtil.showToast(CheckChooseActivity.this, "没有可以下载的批次");
			return;
		}
		Map<String, String> item = new HashMap<String, String>();
		//		item.put("BatchCode", batchNumber);
		item.put("BatchId", model.BatchId);
		param.put("MsStock", item);

		Log.i("盘点下载的批次编号", model.BatchNumer);
		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {

				m_pDialog = new ProgressDialog(
						CheckChooseActivity.this);
				m_pDialog
				.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在下载盘点信息...");
				m_pDialog.setCancelable(false);
				m_pDialog.show();

			}
		}, new Callable<String>() {
			public String call() throws Exception {
				return HttpHelper.getInstance(context).Post(
						ApiAddressHelper.downloadBatchDetail(),
						param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {
				m_pDialog.hide();
				if (res.equals("")) {

					ToastUtil.showToast(context, "网络故障");
					return;
				}

				try {

					JSONObject respJson = new JSONObject(res);

					JSONObject response = respJson.getJSONObject("response");

					String ErrorCode = response.getString("ErrorCode");
					if (ErrorCode.equals("S00000") == false) {
						if (response.getString("ErrorMsg") != null) {
							ToastUtil.showToast(context, response
									.getString("ErrorMsg"));
						}

						return;
					} else {
						if (response.getString("ErrorMsg") != null) {
							ToastUtil.showToast(context, response
									.getString("ErrorMsg"));
						}
					}
					RequestRetModel<DetailCheckModel> model = gson
							.fromJson(
									res,
									new TypeToken<RequestRetModel<DetailCheckModel>>() {
									}.getType());

					if (model != null) {
						MsStockModel MsStock = new MsStockModel();
						for (int i = 0; i < listBatch.MsStock.size(); i++) {
							if (listBatch.MsStock.get(i).BatchNumer.equals(batchNumber)) {
								MsStock = listBatch.MsStock.get(i);
							}
						}

						String string = gson.toJson(model.rspcontent.MsAsset);

						//解析资产信息
						MsAssetDatas =  gson.fromJson(
								string,
								new TypeToken<List<HashMap<String, Object>>>() {
								}.getType());

						SaveCheckDataModel addobject = new SaveCheckDataModel();
						addobject.MsAsset = MsAssetDatas;
						addobject.MsStock = MsStock;

						SharedPreferences sp = CheckChooseActivity.this
								.getSharedPreferences(
										"checkdata",
										Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();

						List<SaveCheckDataModel> listSave;
						String strList = sp.getString("check"+ mobile, null);
						if (strList == null) {
							listSave = new ArrayList<SaveCheckDataModel>();
						} else {
							listSave = gson.fromJson(
									strList,
									new TypeToken<List<SaveCheckDataModel>>() {
									}.getType());
						}
						listSave.add(addobject);
						String saveStr = gson.toJson(listSave);

						editor.putString("check" + mobile,saveStr);
						editor.commit();

						String zzlb = StaticUtil.getSpinner();
						updatafordata(zzlb,batchNumber);

						setDataForList();

						//保存已下载的批次编号

						BatchList.add(batchNumber);
						if(StaticUtil.getSpinner().equals("部门盘点")){
							saveBatch.setDataList(spName+mobile, BatchList);
						}

						//去除下载成功的批次
						listBatch.MsStock.remove(MsStock);
						setBatchNumber();

						//判断是否有可下载批次
						if(listBatch.MsStock.isEmpty()){
							BatchMemo.setText(null);
							BatchState.setText(null);
							StockTimeStart.setText(null);
							return;
						}

					} else {

						ToastUtil.showToast(context, "网络异常");
					}
				} catch (JsonSyntaxException localJsonSyntaxException) {

				} catch (JSONException e) {

				}
			}
		});
	}

	//下载个人核查批次
	private void downloadPeople(){
		mSpinner = (Spinner) findViewById(R.id.spinner_choose);

		CheckChooseActivity.this.Prepare(param);
		param.put("MenuId", "104");
		param.put("StockRightCode",
				CheckMenuActivity.StockRightCode);

		//判断个人核查下载批次是否为空
		if(checkBatch.MsStock.isEmpty()){
			ToastUtil.showToast(CheckChooseActivity.this, "没有可以下载的批次");
			return;
		}
		Map<String, String> item = new HashMap<String, String>();
		item.put("BatchCode", peoplebatchNumble);
		param.put("MsStock", item);

		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {

				m_pDialog = new ProgressDialog(
						CheckChooseActivity.this);
				m_pDialog
				.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在下载个人核查信息...");
				m_pDialog.setCancelable(false);
				m_pDialog.show();

			}
		}, new Callable<String>() {
			public String call() throws Exception {
				return HttpHelper.getInstance(context).Post(
						ApiAddressHelper.downloadpeopleBatchDetail(),
						param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {
				m_pDialog.hide();
				if (res.equals("")) {

					ToastUtil.showToast(context, "网络故障");
					return;
				}

				try {
					JSONObject respJson = new JSONObject(res);

					JSONObject response = respJson.getJSONObject("response");

					String ErrorCode = response.getString("ErrorCode");
					if (ErrorCode.equals("S00000") == false) {
						if (response.getString("ErrorMsg") != null) {
							ToastUtil.showToast(context, response
									.getString("ErrorMsg"));
						}

						return;
					} else {
						if (response.getString("ErrorMsg") != null) {
							ToastUtil.showToast(context, response
									.getString("ErrorMsg"));
						}
					}
					RequestRetModel<DetailCheckModel> model = gson
							.fromJson(
									res,
									new TypeToken<RequestRetModel<DetailCheckModel>>() {
									}.getType());

					if (model != null) {
						MsStockModel MsStock = new MsStockModel();

						String string = gson.toJson(model.rspcontent.MsAsset);
						MsStock.BatchNumer = peoplebatchNumble; 
						//解析资产信息
						MsAssetDatas =  gson.fromJson(
								string,
								new TypeToken<List<HashMap<String, Object>>>() {
								}.getType());

						SaveCheckDataModel addobject = new SaveCheckDataModel();
						addobject.MsAsset = MsAssetDatas;
						addobject.MsStock = MsStock;

						//存储到个人核查信息
						openSQL();
						ContentValues values = new ContentValues();
						values.put("people", mobile);
						values.put("batchnumble", peoplebatchNumble);
						batchSqlData.insert("BatchTable", null, values);
						batchSqlData.close();
						SharedPreferences sp = CheckChooseActivity.this.getSharedPreferences("peoplecheckdata",Context.MODE_PRIVATE);

						SharedPreferences.Editor editor = sp.edit();

						List<SaveCheckDataModel> listSave;
						String strList = sp.getString("check"+ mobile, null);
						if (strList == null) {
							listSave = new ArrayList<SaveCheckDataModel>();
						} else {
							listSave = gson.fromJson(
									strList,
									new TypeToken<List<SaveCheckDataModel>>() {
									}.getType());
						}
						listSave.add(addobject);
						String saveStr = gson.toJson(listSave);

						editor.putString("check" + mobile,
								saveStr);
						editor.commit();

						String zzlb = StaticUtil.getSpinner();
						updatafordata(zzlb,peoplebatchNumble);

						setDataForList();
						
						//保存已经下载的批次
						SharedPreferences sp2 = CheckChooseActivity.this.getSharedPreferences(spPeopleName,Context.MODE_PRIVATE);
						SharedPreferences.Editor editor2 = sp2.edit();
						List<MsStockModel> MsStocks;
						String string2 = sp2.getString(spPeopleName+mobile, null);
						if(string2==null){
							MsStocks = new ArrayList<MsStockModel>();
						}else{
							MsStocks = gson.fromJson(
									string2,
									new TypeToken<List<MsStockModel>>() {
									}.getType());
						}
						if(checkBatch!=null && !checkBatch.MsStock.isEmpty()){
							for(MsStockModel model2 : checkBatch.MsStock){
								if(model2.BatchNumer.equals(peoplebatchNumble)){
									MsStocks.add(model2);
									break;
								}
							}
						}
						String string3 = gson.toJson(MsStocks);
						editor2.putString(spPeopleName+mobile, string3);
						editor2.commit();
						
						//去除已下载批次
						setBatchNumber();
						
//						if(checkBatch.MsStock.isEmpty()){
//							setDataforDown(null,null);
//						}
					} else {
						ToastUtil.showToast(context, "网络异常");
					}
				} catch (JsonSyntaxException localJsonSyntaxException) {

				} catch (JSONException e) {

				}
			}
		});
	}

	//对话框
	protected void dialog() {
		AlertDialog.Builder builder=new Builder(CheckChooseActivity.this);
		//2所有builder设置一些参数
		builder.setTitle("提示");
		builder.setMessage("是否删除该数据");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//获取批次下标
				int position = mSpinner.getSelectedItemPosition();
				int flag = 1;
				resetBatch(position,flag);
			}
		});
		builder.setNeutralButton("取消",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

	private void getCheckBatch(){
		CheckChooseActivity.this.Prepare(param);
		param.put("MenuId", "419");
		param.put("StockRightCode", CheckMenuActivity.StockRightCode);

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
					} 
				} catch (JsonSyntaxException localJsonSyntaxException) {
				}
			}
		});
	}

	//清除SharedPreferences数据
	private void deleteSp(String name){
		ListDataSave dataSave = new ListDataSave(this, name);
		Editor editor = dataSave.getEditor();
		editor.clear();
		editor.commit();
	}
}
