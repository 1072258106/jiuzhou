package com.capitalcode.assetsystemmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.adapter.ScrollAdapter;
import com.liu.AssetsScan.async.CallEarliest;
import com.liu.AssetsScan.async.Callable;
import com.liu.AssetsScan.async.Callback;
import com.liu.AssetsScan.model.DetailCheckModel;
import com.liu.AssetsScan.model.MsAssetModel;
import com.liu.AssetsScan.model.MsStockModel;
import com.liu.AssetsScan.model.OverageAssets;
import com.liu.AssetsScan.model.RequestRetModel;
import com.liu.AssetsScan.model.SaveCheckDataModel;
import com.liu.AssetsScan.util.ApiAddressHelper;
import com.liu.AssetsScan.util.Common;
import com.liu.AssetsScan.util.HttpHelper;
import com.liu.AssetsScan.util.StaticUtil;
import com.liu.AssetsScan.util.ToastUtil;
import com.wyy.twodimcode.CaptureActivity;


public class CheckDetailActivity extends BaseActivity implements View.OnClickListener{
	//	private List<MsAssetModel> AllFindMsAsset = new ArrayList<MsAssetModel>();
	private String BatchId;
	//	private List<MsAssetModel> MsAsset;
	private List<HashMap<String, Object>> MsAssetDatas;
	private ScrollAdapter adapter;
	int currentpage = 1;
	private List<Map<String, String>> datas = new ArrayList<Map<String,String>>();
	private List<SaveCheckDataModel> listSave;
	private Spinner mSpinner;
	private List<OverageAssets> overageAssets;
	int pagecount = 0;
	private String[] values;
	private String StockState;
	private Button pre,next;

	private EditText codevalue,snvalue;
	//扫描头监听
	private BroadcastReceiver mReceiver;
	private IntentFilter mFilter;
	private String BatchNumber;
    private MsStockModel MsStock;
	@Override
	protected void Init(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onPause();
		//注册广播来获取扫描结果
		this.registerReceiver(mReceiver, mFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//注销获取扫描结果的广播
		this.unregisterReceiver(mReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mReceiver = null;
		mFilter = null;
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
		setContentView(R.layout.activity_check_detail);

		BatchId = this.getIntent().getStringExtra("BatchId");
		BatchNumber = this.getIntent().getStringExtra("BatchNumber");

		codevalue = (EditText) findViewById(R.id.codevalue);
		snvalue = (EditText) findViewById(R.id.snvalue);

		next = (Button)this.findViewById(R.id.btn_next);
		pre = (Button)this.findViewById(R.id.btn_pre);
		
		Log.e("erwefefdfdgfdggytr",MenuId);
		if( MenuId.equals("offline_statistics") )
		{
			SetData();
		}


		Button btn = (Button)this.findViewById(R.id.btn_title_left);

		btn = (Button)this.findViewById(R.id.btn_title_right);
		
		if(StaticUtil.upload.equals("上传")){
			btn.setVisibility(View.GONE);
		}else{
			btn.setVisibility(View.VISIBLE);
			btn.setText("初始化");
			btn.setOnClickListener(this);
		}

		TextView tv = (TextView)this.findViewById(R.id.tv_title_name);
		tv.setText("批次明细");

		mSpinner = (Spinner) findViewById(R.id.spinner_state);

		String[] mItems = new String[4];
		mItems[0] = "全部";
		mItems[1] = "已盘";
		mItems[2] = "盘亏";
		mItems[3] = "盘盈";
		values = new String[4];
		values[0] = "全部";
		values[1] = "已盘";
		values[2] = "盘亏";
		values[3] = "盘盈";

		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);

		mSpinner.setAdapter(_Adapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) 
			{
				//				EditText edit = (EditText)CheckDetailActivity.this.findViewById(R.id.codevalue);
				codevalue.setText("");

				//				edit = (EditText)CheckDetailActivity.this.findViewById(R.id.snvalue);
				snvalue.setText("");

				StockState = null ;
				if( position > 0 )
				{
					StockState = values[position];
				}

				currentpage = 1;
				getList(null,null,StockState);

				// 设置选中的字体颜色 大小 位置
				TextView tvSelect = (TextView)view;
				tvSelect.setTextColor(Color.parseColor("#464d4c"));
				tvSelect.setTextSize(18.0f);    //设置大小
				tvSelect.setGravity(Gravity.CENTER_VERTICAL);   //设置居中
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		ListView mListView = (ListView) findViewById(R.id.scroll_list);
		adapter = new ScrollAdapter(this, datas, R.layout.item
				, new String[] { "data_0", "data_1", "data_2", "data_3","data_4", "data_5",
				"data_6", "data_7", "data_8", "data_9","data_10", "data_11",
				"data_12", "data_13", "data_14", "data_15"}
		, new int[] { R.id.item_data0 , R.id.item_data1, R.id.item_data2, R.id.item_data3
				, R.id.item_data4, R.id.item_data5, R.id.item_data6 , R.id.item_data7, 
				R.id.item_data8, R.id.item_data9, R.id.item_data10 , R.id.item_data11,
				R.id.item_data12, R.id.item_data13, R.id.item_data14 , R.id.item_data15,
		});
		mListView.setAdapter(adapter);

		//扫描头设置
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//此处获取扫描结果信息
				final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
				getList(scanResult, null, null);
				codevalue.setText(scanResult);
			}
		};
		mFilter = new IntentFilter("ACTION_BAR_SCAN");
	}

	void setupData(DetailCheckModel model)
	{
		datas.clear();
		pagecount = Integer.valueOf(model.PageCount)/10;

		int other = Integer.valueOf(model.PageCount)%10;
		if( other != 0 )
		{
			pagecount++;
		}

		next = (Button)this.findViewById(R.id.btn_next);
		if( this.currentpage == this.pagecount )
		{
			next.setEnabled(false);
		}


		Map<String, String> data = null;
		String string = gson.toJson(model.MsAsset);
		Log.i("该解析的数据", string);
		List<MsAssetModel> assetModels = new ArrayList<MsAssetModel>();
		assetModels = gson
				.fromJson(
						string,
						new TypeToken<List<MsAssetModel>>() {
						}.getType());
		for( MsAssetModel item : assetModels ) {
			data = new HashMap<String, String>();

			data.put("data_" + 0, item.assetCode );
			data.put("data_" + 1, item.assetName );
			data.put("data_" + 2, item.str9 );
			data.put("data_" + 3, item.useUserName );
			data.put("data_" + 4, item.useDeptName );
			data.put("data_" + 5, item.inventoryStateName );
			data.put("data_" + 6, item.addressName );
			data.put("data_" + 7, item.adminDeptName );
			data.put("data_" + 8, item.assetOldCode );
			data.put("data_" + 9, item.brand );
			data.put("data_" + 10, item.drawDT);
			data.put("data_" + 11, item.measureUnit );
			data.put("data_" + 12, item.modelno );
			data.put("data_" + 13, item.onWorth );
			data.put("data_" + 14, item.assetStatusName );
			data.put("data_" + 15, item.StockMemo );
			datas.add(data);
		}

		List<OverageAssets> overageAssets = model.overageAssets;
		if(overageAssets!=null){
			for(OverageAssets assets : overageAssets){
				String setdata = "";
				if(assets.count!=null && assets.count.length()>0){
					setdata = "数量："+assets.count;
				}
				if(assets.pyreason!=null && assets.pyreason.length()>0){
					setdata = setdata +";盘盈原因："+assets.pyreason;
				}
				if(assets.pyexplan!=null && assets.pyexplan.length()>0){
					setdata = setdata+";盘盈说明："+assets.pyexplan;
				}
				data = new HashMap<String, String>();
				data.put("data_" + 0, assets.pycode );
				data.put("data_" + 1, assets.pyname );
				data.put("data_" + 2, "" );
				data.put("data_" + 3, assets.pyuserName );
				data.put("data_" + 4, assets.pydeptName );
				data.put("data_" + 5, assets.inventoryStateName );
				data.put("data_" + 6, assets.saveplace );
				data.put("data_" + 7, "" );
				data.put("data_" + 8, "" );
				data.put("data_" + 9, "" );
				data.put("data_" + 10, assets.pytime );
				data.put("data_" + 11, "" );
				data.put("data_" + 12, assets.models );
				data.put("data_" + 13, "" );
				data.put("data_" + 14, "" );
				data.put("data_" + 15, setdata );
				datas.add(data);
			}
		}
	}


	void getList()
	{
		if( MenuId.equals("online_statistics") )
		{

			param.put("PageIndex", currentpage+"");
			doAsync(new CallEarliest<String>() {
				public void onCallEarliest() throws Exception {

					m_pDialog = new ProgressDialog(CheckDetailActivity.this);
					m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					m_pDialog.setMessage("正在获取数据...");
					m_pDialog.setCancelable(false);
					m_pDialog.show();

				}
			}, new Callable<String>() {
				public String call() throws Exception {
					return HttpHelper.getInstance(context).Post(
							ApiAddressHelper.getStockAssetList(), param);
				}
			}, new Callback<String>() {
				public void onCallback(String res) {
					m_pDialog.hide();
					if (res.equals("")) {

						Common.ShowInfo(
								context,"网络故障");
						return;
					}

					try 
					{				
						JSONObject respJson = new JSONObject(res);

						JSONObject response = respJson
								.getJSONObject("response");

						String ErrorCode = response.getString("ErrorCode");
						if( ErrorCode.equals("S00000") == false )
						{

							String ErrorMsg = response.getString("ErrorMsg");
							Common.ShowInfo(
									context,ErrorMsg);
							return;
						}


						RequestRetModel<DetailCheckModel> model = gson
								.fromJson(
										res,
										new TypeToken<RequestRetModel<DetailCheckModel>>() {
										}.getType());



						if (model != null) 
						{
							setupData(model.rspcontent);
							adapter.notifyDataSetChanged();

							TextView tv = (TextView)CheckDetailActivity.this.findViewById(R.id.page);
							tv.setText("第"+currentpage+"页/共"+pagecount+"页" );
						} 
						else 
						{

							Common.ShowInfo(context, "网络异常");
							Log.e("fail",
									"failfailfailfailfailfailfailfailfail");
						}

					}  
					catch (JsonSyntaxException localJsonSyntaxException) {

						Log.e("fail",
								localJsonSyntaxException.getMessage());
					}
					catch (JSONException localJsonSyntaxException) {

					}
				}
			});

		}
		else
		{
			SetDataForAdapter();
		}
	}

	//搜索后的列表变更
	void getList(String AssetCode,String SerialNumber,String StockState)
	{

		if( AssetCode != null && AssetCode.length() > 0 )
		{
			snvalue.setText("");
			codevalue.selectAll();
		}
		else if( SerialNumber != null && SerialNumber.length() > 0 )
		{
			codevalue.setText("");			
			snvalue.selectAll();
		}

		if( MenuId.equals("online_statistics") )
		{


			Prepare(param);
			param.put("MenuId", "419");

			Map<String,String> MsStock = new HashMap<String,String>();
			MsStock.put("BatchId",BatchId);
			MsStock.put("AssetCode", AssetCode);
			MsStock.put("StockState", StockState);
			MsStock.put("SerialNumber", SerialNumber);

			param.put("MsStock", MsStock);
			param.put("PageSize", "10");
			param.put("PageIndex", currentpage+"");

			param.put("StockRightCode", CheckMenuActivity.StockRightCode);

			doAsync(new CallEarliest<String>() {
				public void onCallEarliest() throws Exception {

					m_pDialog = new ProgressDialog(CheckDetailActivity.this);
					m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					m_pDialog.setMessage("正在获取数据...");
					m_pDialog.setCancelable(false);
					m_pDialog.show();

				}
			}, new Callable<String>() {
				public String call() throws Exception {
					return HttpHelper.getInstance(context).Post(
							ApiAddressHelper.getStockAssetList(), param);
				}
			}, new Callback<String>() {
				public void onCallback(String res) {
					m_pDialog.hide();
					if (res.equals("")) {

						Common.ShowInfo(
								context,"网络故障");
						return;
					}

					try 
					{				
						JSONObject respJson = new JSONObject(res);

						JSONObject response = respJson
								.getJSONObject("response");

						String ErrorCode = response.getString("ErrorCode");
						if( ErrorCode.equals("S00000") == false )
						{

							String ErrorMsg = response.getString("ErrorMsg");
							Common.ShowInfo(
									context,ErrorMsg);

							datas.clear();
							adapter.notifyDataSetChanged();

							currentpage = 1;
							pagecount = 1;
							TextView tv = (TextView)CheckDetailActivity.this.findViewById(R.id.page);
							tv.setText("第"+currentpage+"页/共"+pagecount+"页" );

							return;
						}


						RequestRetModel<DetailCheckModel> model = gson
								.fromJson(
										res,
										new TypeToken<RequestRetModel<DetailCheckModel>>() {
										}.getType());



						if (model != null) 
						{
							setupData(model.rspcontent);
							adapter.notifyDataSetChanged();

							TextView tv = (TextView)CheckDetailActivity.this.findViewById(R.id.page);
							tv.setText("第"+currentpage+"页/共"+pagecount+"页" );
						} 
						else 
						{

							Common.ShowInfo(context, "网络异常");
							Log.e("fail",
									"failfailfailfailfailfailfailfailfail");
						}




					}  
					catch (JsonSyntaxException localJsonSyntaxException) {

						Log.e("fail",
								localJsonSyntaxException.getMessage());
					}
					catch (JSONException localJsonSyntaxException) {




					}
				}
			});

		}
		else
		{
			List<HashMap<String, Object>> maps = new ArrayList<HashMap<String,Object>>();
			for( HashMap<String, Object> model: MsAssetDatas )
			{

				if( (model.get("assetCode").equals(AssetCode) && AssetCode.length()>0) || (model.get("str9").equals(SerialNumber) && SerialNumber.length()>0) )
				{
					maps.add(model);
					break;
				}
				else if( (StockState == null || model.get("inventoryStateName").equals(StockState)) && ((AssetCode!=null && SerialNumber!=null &&
						AssetCode.length()==0 && SerialNumber.length()==0 ) || (AssetCode==null && SerialNumber==null)) )
				{
					maps.add(model);
				}
			}
			List<OverageAssets> assets = new ArrayList<OverageAssets>();
			if(overageAssets!=null){
				for( OverageAssets model: overageAssets )
				{

					if( (model.pycode.equals(AssetCode) && AssetCode.length()>0) )
					{
						assets.add(model);
						break;
					}
					else if( (StockState == null || model.inventoryStateName.equals(StockState)) && ((AssetCode!=null && SerialNumber!=null &&
							AssetCode.length()==0 && SerialNumber.length()==0 ) || (AssetCode==null && SerialNumber==null)) )
					{
						assets.add(model);
					}
				}
			}
			DetailCheckModel detailmodel = new DetailCheckModel();
			if( maps.size()<=10 )
			{
				detailmodel.MsAsset = maps;
			}
			else
			{
				detailmodel.MsAsset = maps.subList(0, 9);
			}
			if (assets!=null) {
				if( assets.size()<=10 )
				{
					detailmodel.overageAssets = assets;
				}
				else
				{
					detailmodel.overageAssets = assets.subList(0, 9);
				}
				detailmodel.PageCount = String.valueOf(maps.size()+assets.size());
			}else {
				detailmodel.PageCount = String.valueOf(maps.size());
			}

			setupData(detailmodel);
			adapter.notifyDataSetChanged();

			TextView tv = (TextView)CheckDetailActivity.this.findViewById(R.id.page);
			tv.setText("第"+currentpage+"页/共"+pagecount+"页" );

		}
	}


	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub
		Button btn = (Button)this.findViewById(R.id.btn_scan_code);
		btn.setOnClickListener(this);

		btn = (Button)this.findViewById(R.id.btn_scan_sn);
		btn.setOnClickListener(this);

		//		EditText edit = (EditText)this.findViewById(R.id.codevalue);
		codevalue.setOnEditorActionListener(new TextView.OnEditorActionListener() {  

			@Override  
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
				/*判断是否是“GO”键*/  
				if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
				{  
					String result = codevalue.getText().toString();

					if( result.length() > 0 )
					{
						getList(result, null, null);
					}
					if(result==null||result.length()<=0){
						getList(null, null, null);
					}

					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
					imm.hideSoftInputFromWindow(codevalue.getWindowToken(),0);   


					return true;  
				}  
				return false;  
			}


		});

		snvalue.setOnEditorActionListener(new TextView.OnEditorActionListener() {  

			@Override  
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
				/*判断是否是“GO”键*/  
				if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
				{  
					//					EditText edit = (EditText)CheckDetailActivity.this.findViewById(R.id.snvalue);
					String result = snvalue.getText().toString();

					if( result.length() > 0 )
					{
						getList(null, result, null);
					} 
					if(result==null||result.length()<=0){
						getList(null, null, null);
					}

					//edit.clearFocus(); 
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
					imm.hideSoftInputFromWindow(snvalue.getWindowToken(),0); 
					return true;  
				}  
				return false;  
			}
		});        


//		btn = (Button)this.findViewById(R.id.btn_pre);
		pre.setEnabled(false);
		pre.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub

				Button btn = (Button)arg0;
				if( CheckDetailActivity.this.currentpage > 1 )
				{
					CheckDetailActivity.this.currentpage--;
				}

				if( CheckDetailActivity.this.currentpage == 1 )
				{
					btn.setEnabled(false);
				}

				if( CheckDetailActivity.this.currentpage < CheckDetailActivity.this.pagecount )
				{
//					Button btn_next = (Button)CheckDetailActivity.this.findViewById(R.id.btn_next);
					next.setEnabled(true);
				}
				getList();
			}

		});

//		btn = (Button)this.findViewById(R.id.btn_next);
		if( this.currentpage == this.pagecount )
		{
			next.setEnabled(false);
		}
		next.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub

				Button btn = (Button)arg0;
				if( CheckDetailActivity.this.currentpage < CheckDetailActivity.this.pagecount )
				{
					CheckDetailActivity.this.currentpage++;
				}

				if( CheckDetailActivity.this.currentpage == CheckDetailActivity.this.pagecount )
				{
					btn.setEnabled(false);
				}


				if( CheckDetailActivity.this.currentpage > 1 )
				{
//					Button btn_pre = (Button)CheckDetailActivity.this.findViewById(R.id.btn_pre);
					pre.setEnabled(true);
				}
				getList();

			}

		}

				);	
		btn = (Button)this.findViewById(R.id.btn_jump);
		btn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				EditText et = (EditText)findViewById(R.id.edit_jump);
				String strNum = et.getText().toString();
				if( strNum.length()>0 )
				{
					int index = Integer.valueOf(strNum);

					if( index >= 1 && index <= pagecount && index != currentpage )
					{
						currentpage = index;

						if( currentpage == 1 )
						{
//							Button btn_pre = (Button)CheckDetailActivity.this.findViewById(R.id.btn_pre);
							pre.setEnabled(false);
						}

						if( currentpage > 1 )
						{
//							Button btn_pre = (Button)CheckDetailActivity.this.findViewById(R.id.btn_pre);
							pre.setEnabled(true);
						}

						if( currentpage < pagecount )
						{
//							Button btn_next = (Button)CheckDetailActivity.this.findViewById(R.id.btn_next);
							next.setEnabled(true);
						}

						if( currentpage == pagecount )
						{
//							Button btn_next = (Button)CheckDetailActivity.this.findViewById(R.id.btn_next);
							next.setEnabled(false);
						}
						getList();
					}
				}
				et.setText("");
			}
		});       
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
		case R.id.btn_scan_code:
		{
			Intent it = new Intent(this, CaptureActivity.class);
			startActivityForResult(it, 1);

		}
		break;

		case R.id.btn_scan_sn:
		{
			Intent it = new Intent(this, CaptureActivity.class);
			startActivityForResult(it, 2);
		}
		break;

		case R.id.btn_title_right:
			resetBatch();
			break;
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		switch (requestCode)
		{
		case 1:
			if (data != null)
			{
				String result = data.getStringExtra("result");
				codevalue.setText(result);
				getList(result, null, null);
			}
			break;

		case 2:
			if (data != null)
			{
				String result = data.getStringExtra("result");
				snvalue.setText(result);
				getList(null, result, null);
			}
			break;    

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	//批次信息初始化与删除

	private void resetBatch(){

		//删除批次下资产信息
		Log.i("批次编号", BatchNumber);
		MsStock = new MsStockModel();
		SharedPreferences sp = CheckDetailActivity.this.getSharedPreferences("checkdata",Context.MODE_PRIVATE);
		String strlist = sp.getString("check" + mobile, null);
		SharedPreferences.Editor editor = sp.edit();

		List<SaveCheckDataModel> listSave = gson.fromJson(
				strlist,
				new TypeToken<List<SaveCheckDataModel>>() {
				}.getType());

		for (SaveCheckDataModel item : listSave) {
			if (item.MsStock.BatchNumer.equals(BatchNumber)) {
				Log.i("批次编号集合", item.MsStock.BatchNumer);
				MsStock = item.MsStock;
				listSave.remove(item);
//				List<HashMap<String, Object>> delitems = new ArrayList<HashMap<String,Object>>();
//				for (HashMap<String, Object> t : item.MsAsset) {
//					//资产初始化
//					if (t.get("inventoryStateName").equals("已盘")) {
//						t.put("inventoryStateName", "盘亏");
//					}  
//					if (t.get("inventoryStateName").equals("盘盈")) {
//						delitems.add(t);
//					}
//				}
//				if(item.MsPYInfo!=null){
//					item.MsPYInfo.clear();
//				}
//				item.MsAsset.removeAll(delitems);
				break;
			}
		}
		//资产初始化
		String saveStr = gson.toJson(listSave);
		Log.i("初始化后的资产信息", saveStr);
		editor.putString("check" + mobile, saveStr);
		editor.commit();
		
//		重新下载
//		if(BatchNumber!=null){
//			Thread thread = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
					download();	
//				}
//			});
//			thread.start();
//			//销毁线程
//			if(thread!=null){
//				thread = null;
//			}
//		}
		
	}

	//界面数据控制
	private void SetData(){
		SharedPreferences sp = CheckDetailActivity.this.getSharedPreferences("checkdata", Context.MODE_PRIVATE);
		String strlist = sp.getString("check"+mobile, null);
		if( strlist != null )
		{
			listSave = gson
					.fromJson(
							strlist,
							new TypeToken<List<SaveCheckDataModel>>() {
							}.getType());


			for( SaveCheckDataModel model :  listSave )
			{
				if(model.MsStock.BatchNumer.equals(BatchNumber))
				{
					MsAssetDatas = model.MsAsset;
					overageAssets = model.MsPYInfo;
					Log.i("明细资产信息", strlist);
					break;
				}
			}

			//统计
			int HaveStock = 0;
			int NoStock = 0;
			int OverStock = 0;

			for (HashMap<String, Object> map : MsAssetDatas) {
				if(map.get("inventoryStateName").equals("已盘")){
					HaveStock++;
				}
				if(map.get("inventoryStateName").equals("盘亏")){
					NoStock++;
				}
				if(map.get("inventoryStateName").equals("盘盈")){
					OverStock++;
				}
			}
			if(overageAssets!=null){
				for (int i = 0; i < overageAssets.size(); i++) {
					OverStock++;
				}
			}
			String show ="已盘:"+HaveStock+" 盘亏:"+NoStock+" 盘盈:"+OverStock;
			TextView tv = (TextView)CheckDetailActivity.this.findViewById(R.id.check_detail_state);
			tv.setText(show);
			tv.setVisibility(View.VISIBLE);
		}
	}
	
	//翻页控制
	private void SetDataForAdapter(){
		DetailCheckModel detailmodel = new DetailCheckModel();
		int tail = 10*(currentpage-1)+9;
		Log.i("数据大小", ""+MsAssetDatas.size());
		if( tail >= MsAssetDatas.size() )
		{
			tail = MsAssetDatas.size();
		}
		detailmodel.MsAsset = MsAssetDatas.subList(10*(currentpage-1), tail);
		detailmodel.PageCount = String.valueOf(MsAssetDatas.size());

		String string3 = gson.toJson(detailmodel);
		Log.i("要被传值的3", string3);


		setupData(detailmodel);
		adapter.notifyDataSetChanged();

		TextView tv = (TextView)CheckDetailActivity.this.findViewById(R.id.page);
		tv.setText("第"+currentpage+"页/共"+pagecount+"页" );
	}
	
	//初始化下载数据
	private void download(){
		CheckDetailActivity.this.Prepare(param);
		param.put("MenuId", "104");
		param.put("StockRightCode",
				CheckMenuActivity.StockRightCode);
		
		Map<String, String> item = new HashMap<String, String>();
		item.put("BatchId", BatchId);
		param.put("MsStock", item);

		Log.i("初始化的批次编号", BatchNumber);
		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {

				m_pDialog = new ProgressDialog(
						CheckDetailActivity.this);
				m_pDialog
				.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在初始化盘点信息...");
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
					ToastUtil.showToast(context, "初始化失败");
					return;
				}
				try {

					JSONObject respJson = new JSONObject(res);

					JSONObject response = respJson.getJSONObject("response");

					String ErrorCode = response.getString("ErrorCode");
					if (ErrorCode.equals("S00000") == false) {
						if (response.getString("ErrorMsg") != null) {
							ToastUtil.showToast(context, "初始化失败");
						}

						return;
					} else {
						if (response.getString("ErrorMsg") != null) {
							ToastUtil.showToast(context, "初始化成功");
						}
					}
//					Log.i("下载时的数据", res);
					RequestRetModel<DetailCheckModel> model = gson
							.fromJson(
									res,
									new TypeToken<RequestRetModel<DetailCheckModel>>() {
									}.getType());
					
					if (model != null) {
						
						String string = gson.toJson(model.rspcontent.MsAsset);
						
						//解析资产信息
						MsAssetDatas =  gson.fromJson(
								string,
								new TypeToken<List<HashMap<String, Object>>>() {
								}.getType());

						SaveCheckDataModel addobject = new SaveCheckDataModel();
						addobject.MsAsset = MsAssetDatas;
						addobject.MsStock = MsStock;
						
						SharedPreferences sp = CheckDetailActivity.this
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

						editor.putString("check" + mobile,
								saveStr);
						editor.commit();
						
						currentpage = 1;
						SetData();
						SetDataForAdapter();
						pre.setEnabled(false);
						next.setEnabled(true);

					} else {

						ToastUtil.showToast(context, "初始化失败");
					}
				} catch (JsonSyntaxException localJsonSyntaxException) {

				} catch (JSONException e) {

				}
			}
		});
	}
}








