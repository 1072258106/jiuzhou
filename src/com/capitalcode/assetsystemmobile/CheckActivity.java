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
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.adapter.ContentAdapter;
import com.liu.AssetsScan.async.CallEarliest;
import com.liu.AssetsScan.async.Callable;
import com.liu.AssetsScan.async.Callback;
import com.liu.AssetsScan.model.CheckBaseData;
import com.liu.AssetsScan.model.CheckModel;
import com.liu.AssetsScan.model.OverageAssets;
import com.liu.AssetsScan.model.RequestRetModel;
import com.liu.AssetsScan.model.SaveCheckDataModel;
import com.liu.AssetsScan.model.SaveCheckModel;
import com.liu.AssetsScan.util.ApiAddressHelper;
import com.liu.AssetsScan.util.Common;
import com.liu.AssetsScan.util.HttpHelper;
import com.liu.AssetsScan.util.ListDataSave;
import com.liu.AssetsScan.util.ToastUtil;
import com.wyy.twodimcode.CaptureActivity;

//盘点界面
public class CheckActivity extends BaseActivity implements View.OnClickListener{

	private List<Map<String, Object>> listCheck = new ArrayList<Map<String, Object>>();
	ContentAdapter checkAdapter;
	String BatchId;
	String assetId;
	
	int HaveStock = 0;
	int NoStock = 0;
	int OverStock = 0;

	private Button button;

	List<SaveCheckDataModel> listSave;

	private List<HashMap<String, Object>> MsAssetDatas;
	String assetCode;
	private List<OverageAssets> overageAssets;
	
	private EditText codevalue,snvalue,remarkvalue;
	//扫描头监听
	private BroadcastReceiver mReceiver;
	private IntentFilter mFilter;
	
	//存储图片数据的偏好设置
	private ListDataSave Check;
	private String spName = "CheckPic";
	private String BatchNumber; //用于保存的批次编号
	private String AssetsNumber; //用于保存的资产编号
	
	@Override
	protected void Init(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void AppInit() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DataInit();
		//注册广播来获取扫描结果
		this.registerReceiver(mReceiver, mFilter);
	}

	@Override
	protected void onPause() {
		//注销获取扫描结果的广播
		this.unregisterReceiver(mReceiver);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mReceiver = null;
		mFilter = null;
		super.onDestroy();
	}
	
	
	@Override
	protected void DataInit() {
		// TODO Auto-generated method stub
		
		if( MenuId.equals("offline_check"))
		{
			SharedPreferences sp = CheckActivity.this.getSharedPreferences("checkdata", Context.MODE_PRIVATE);
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
					if(model.MsStock.BatchId.equals(BatchId))
					{
						//						MsAsset = model.MsAsset;
						BatchNumber = model.MsStock.BatchNumer;
						Check = new ListDataSave(this, spName + BatchNumber);
						MsAssetDatas = model.MsAsset;
						overageAssets = model.MsPYInfo;
						break;
					}
				}
				HaveStock = 0;
				NoStock = 0;
				OverStock = 0;

				for (HashMap<String, Object> map : MsAssetDatas) {
					if(map.get("inventoryStateName").equals("已盘")){
						HaveStock++;
					}
					if(map.get("inventoryStateName").equals("盘亏")){
						NoStock++;
					}
				}
				if(overageAssets!=null){
					for (int i = 0; i < overageAssets.size(); i++) {
						OverStock++;
					}
				}
				String show ="已盘:"+HaveStock+" 盘亏:"+NoStock+" 盘盈:"+OverStock;
				TextView tv = (TextView)CheckActivity.this.findViewById(R.id.tvstate);
				tv.setText(show);
				tv.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void Destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ViewInit() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_check);
		button = (Button) this.findViewById(R.id.btn_overage);
		button.setOnClickListener(this);

		Button btn = (Button)this.findViewById(R.id.btn_title_left);

		btn = (Button)this.findViewById(R.id.btn_title_right);
		btn.setVisibility(View.GONE);

		TextView tv = (TextView)this.findViewById(R.id.tv_title_name);
		tv.setText("资产盘点");

		setuplist(new HashMap<String, Object>());
		ListView lv = (ListView) this.findViewById(R.id.listinfo);
		checkAdapter = new ContentAdapter(this, listCheck,
				this.basedataModel.StockLabel);
		lv.setAdapter(checkAdapter);

		btn = (Button)this.findViewById(R.id.btn_scan_code);
		btn.setOnClickListener(this);

		btn = (Button)this.findViewById(R.id.btn_scan_sn);
		btn.setOnClickListener(this);

		btn = (Button)this.findViewById(R.id.btn_clear);
		btn.setOnClickListener(this);

		btn = (Button)this.findViewById(R.id.btn_check);
		btn.setOnClickListener(this);
		btn.setEnabled(false);
		
		codevalue = (EditText) findViewById(R.id.codevalue);
		snvalue = (EditText) findViewById(R.id.snvalue);
		remarkvalue = (EditText) findViewById(R.id.remarkvalue);
		
		BatchId = this.getIntent().getStringExtra("BatchId");
		
		//扫描头设置
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//此处获取扫描结果信息
				final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
				getStockAssetObj("AssetCode", scanResult);
			}
		};
		mFilter = new IntentFilter("ACTION_BAR_SCAN");
	}

	@Override
	protected void ViewListen() {
		codevalue.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
			@Override  
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
				/*判断是否是“GO”键*/  
				if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED )
				{  
					String result = codevalue.getText().toString();

					if( result.length() > 0 )
					{
						getStockAssetObj("AssetCode", result);
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
				if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED )
				{  
					String result = snvalue.getText().toString();

					if( result.length() > 0 )
					{
						getStockAssetObj("SerialNumber", result);

					} 
					//edit.clearFocus(); 
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
					imm.hideSoftInputFromWindow(snvalue.getWindowToken(),0); 
					return true;  
				}  
				return false;  
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

		case R.id.btn_overage:
			
			Intent it = new Intent(this, OverageActivity.class);
			it.putExtra("BatchId", BatchId);
			startActivity(it);
			break;

		case R.id.btn_clear:
		{
			snvalue.setText("");
			snvalue.setText("");
			remarkvalue.setText("");
			setuplist(new HashMap<String, Object>());
			checkAdapter.notifyDataSetChanged();

			TextView tv = (TextView)CheckActivity.this.findViewById(R.id.tvresult);
			tv.setText("");
			tv.setVisibility(View.GONE);

			if( MenuId.equals("online_check"))
			{
				tv = (TextView)CheckActivity.this.findViewById(R.id.tvstate);
				tv.setText("");
				tv.setVisibility(View.GONE);
			}

			Button btn = (Button)CheckActivity.this.findViewById(R.id.btn_check);
			btn.setEnabled(false);
		}
		break;

		//盘点资产
		case R.id.btn_check:
		{
			//			saveImage();
			Map<String,Object> map = listCheck.get(0);
			String AssetCode = (String)map.get("value");

			map = listCheck.get(listCheck.size()-1);
			String SerialNumber = (String)map.get("value");

			if( AssetCode.length()>0 || SerialNumber.length() > 0 )
			{
//				EditText edit = (EditText)this.findViewById(R.id.remarkvalue);
				String StockMemo = remarkvalue.getText().toString();

				if( MenuId.equals("online_check"))
				{

					CheckActivity.this.Prepare(param);
					if( MenuId.equals("online_check")  )
					{
						param.put("MenuId", "419");
					}
					else
					{
						param.put("MenuId", "104");
					}

					Map<String,String> item = new HashMap<String,String>();
					item.put("BatchId", BatchId);
					item.put("AssetCode", AssetCode);
					item.put("SerialNumber", SerialNumber);
					item.put("StockMemo", StockMemo);				

					param.put("MsStock", item);
					param.put("StockRightCode", CheckMenuActivity.StockRightCode);


					doAsync(new CallEarliest<String>() {
						public void onCallEarliest() throws Exception {

							m_pDialog = new ProgressDialog(CheckActivity.this);
							m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							m_pDialog.setMessage("正在提交数据...");
							m_pDialog.setCancelable(false);
							m_pDialog.show();

						}
					}, new Callable<String>() {
						public String call() throws Exception {
							return HttpHelper.getInstance(context).Post(
									ApiAddressHelper.getSaveStockAssetObj(), param);
						}
					}, new Callback<String>() {
						public void onCallback(String res) {
							m_pDialog.hide();
							if (res.equals("")) {

								Common.ShowInfo(context, "网络故障");
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
									if( response.getString("ErrorMsg") != null )
									{
										Common.ShowInfo(context, response.getString("ErrorMsg"));
									}

									return;
								}

								RequestRetModel<SaveCheckModel> model = gson
										.fromJson(
												res,
												new TypeToken<RequestRetModel<SaveCheckModel>>() {
												}.getType());

								if( model.response.ErrorMsg != null )
								{
									Common.ShowInfo(context, model.response.ErrorMsg);
								}
								else if( model.response.ErrorCode.equals("S00000") )
								{
									Common.ShowInfo(context, "方法执行成功");
								}

								if (model != null) 
								{
									TextView tvresult = (TextView)CheckActivity.this.findViewById(R.id.tvresult);
									tvresult.setText(model.rspcontent.StockResult);
									tvresult.setVisibility(View.VISIBLE);

									if( model.rspcontent.MsStock != null  )
									{
										String show ="已盘:"+model.rspcontent.MsStock.HaveStock+" 盘亏:"+model.rspcontent.MsStock.NoStock+" 盘盈:"+model.rspcontent.MsStock.OverStock;
										TextView tv = (TextView)CheckActivity.this.findViewById(R.id.tvstate);
										tv.setText(show);
										tv.setVisibility(View.VISIBLE);
									}

								} 
							} catch (JsonSyntaxException localJsonSyntaxException) {

								Log.e("fail",
										localJsonSyntaxException.getMessage());
							}
							catch (JSONException localJsonSyntaxException) {

								Log.e("fail",
										localJsonSyntaxException.getMessage());
							}
						}
					});

				}
				else if( MenuId.equals("offline_check"))
				{
					if( assetCode != null )
					{
						for (HashMap<String, Object> hashMap : MsAssetDatas) {

							if( hashMap.get("assetCode").equals(assetCode))
							{
								if( hashMap.get("inventoryStateName").equals("盘亏"))
								{
									//<---->
									setDataforCheck(hashMap);

									HaveStock++;
									NoStock--;

									//盘点结束，清空界面
									snvalue.setText("");
									snvalue.setText("");
									remarkvalue.setText("");
									setuplist(new HashMap<String, Object>());
									checkAdapter.notifyDataSetChanged();
									
									String show ="已盘:"+HaveStock+" 盘亏:"+NoStock+" 盘盈:"+OverStock;
									TextView tv = (TextView)CheckActivity.this.findViewById(R.id.tvstate);
									tv.setText(show);
									tv.setVisibility(View.VISIBLE);	
								}
								else
								{
									setDataforCheck(hashMap);

//									new AlertDialog.Builder(CheckActivity.this).setTitle("提示")//设置对话框标题  		  
//									.setMessage("该资产已盘点!")//设置显示的内容  
//									.setPositiveButton("是",new DialogInterface.OnClickListener() {//添加确定按钮  
//										@Override  
//										public void onClick(DialogInterface dialog, int which) 
//										{//确定按钮的响应事件  
//
//										}  
//									}).show();//在按键响应事件中显示此对话框
								}

							}
						}
						//盘点结束，清空界面
						snvalue.setText("");
						snvalue.setText("");
						remarkvalue.setText("");
						setuplist(new HashMap<String, Object>());
						checkAdapter.notifyDataSetChanged();
					}
				}
			}
			Button btn = (Button)CheckActivity.this.findViewById(R.id.btn_check);
			btn.setEnabled(false);
		}
		break;
		}

	}

	private void setuplist(HashMap<String, Object> msAsset) {
		listCheck.clear();
		setView(msAsset);
	}


	void getStockAssetObj(String type,String result)
	{
		TextView tvresult = (TextView)CheckActivity.this.findViewById(R.id.tvresult);
		tvresult.setText("");
		tvresult.setVisibility(View.GONE);

		if( type.equals("AssetCode"))
		{
			snvalue.setText("");
			codevalue.setText(result);
			codevalue.selectAll();
		}
		else
		{
			codevalue.setText("");
			snvalue.setText(result);
			snvalue.selectAll();
		}

		if( MenuId.equals("online_check") )
		{

			CheckActivity.this.Prepare(param);
			if( MenuId.equals("online_check")  )
			{
				param.put("MenuId", "419");
			}
			else
			{
				param.put("MenuId", "104");
			}

			Map<String,String> map = new HashMap<String,String>();
			map.put("BatchId", BatchId);
			map.put(type, result);

			param.put("MsStock", map);
			param.put("StockRightCode", CheckMenuActivity.StockRightCode);


			doAsync(new CallEarliest<String>() {
				public void onCallEarliest() throws Exception {

					m_pDialog = new ProgressDialog(CheckActivity.this);
					m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					m_pDialog.setMessage("正在获取数据...");
					m_pDialog.setCancelable(false);
					m_pDialog.show();

				}
			}, new Callable<String>() {
				public String call() throws Exception {
					return HttpHelper.getInstance(context).Post(
							ApiAddressHelper.getStockAssetObj(), param);
				}
			}, new Callback<String>() {
				public void onCallback(String res) {
					m_pDialog.hide();
					if (res.equals("")) {

						Common.ShowInfo(context, "网络故障");
						return;
					}

					try {
						RequestRetModel<CheckModel> model = gson.fromJson(res,
								new TypeToken<RequestRetModel<CheckModel>>() {
						}.getType());
						if (model != null) 
						{
							if( model.rspcontent.MsAsset != null && model.rspcontent.MsAsset.get("assetId") != null && 
									model.rspcontent.MsAsset.get("assetId").equals("")==false )
							{	
								assetId = (String) model.rspcontent.MsAsset.get("assetId");
								setuplist(model.rspcontent.MsAsset);
								checkAdapter.notifyDataSetChanged();
							}
							else
							{
								assetId = null;
								//在线未查到资产 不管了
							}

							if( model.rspcontent.MsStock != null  )
							{
								String show ="已盘:"+model.rspcontent.MsStock.HaveStock+" 盘亏:"+model.rspcontent.MsStock.NoStock+" 盘盈:"+model.rspcontent.MsStock.OverStock;
								TextView tv = (TextView)CheckActivity.this.findViewById(R.id.tvstate);
								tv.setText(show);
								tv.setVisibility(View.VISIBLE);
							}
						} 
						else 
						{

							Common.ShowInfo(context, "网络异常");
							Log.e("fail",
									"failfailfailfailfailfailfailfailfail");
						}
					} catch (JsonSyntaxException localJsonSyntaxException) {

						Log.e("fail",
								localJsonSyntaxException.getMessage());
					}
				}
			});

		}
		else
		{
			int flag = 1;
			assetId = null;
			if( "AssetCode".equals(type) )
			{

				for( HashMap<String, Object> model : MsAssetDatas )
				{
					if( model.get("assetCode").equals(result))
					{
						flag = 2;
						assetId = (String) model.get("assetId");
						AssetsNumber = (String) model.get("assetCode");
						setuplist(model);
						checkAdapter.notifyDataSetChanged();
						if(AssetsNumber!=null){
							setImage();
						}
						
						break;
					}
				}
			}
			else
			{

				for( HashMap<String, Object> model : MsAssetDatas )
				{
					if( model.get("str9").equals(result))
					{
						flag = 2;
						assetId = (String) model.get("assetId");
						AssetsNumber = (String) model.get("assetCode");
						setuplist(model);
						checkAdapter.notifyDataSetChanged();
						if(AssetsNumber!=null){
							setImage();
						}
						break;
					}
				}

			}
			if(flag == 1)
			{
				//未查到资产，跳转盘盈界面
				String assetCode = codevalue.getText().toString().trim();
				
				Intent intent = new Intent(this,OverageActivity.class);
				intent.putExtra("BatchId", BatchId);
				intent.putExtra("assetCode", assetCode);
				startActivity(intent);
			}
			
		}

		Button btn = (Button)CheckActivity.this.findViewById(R.id.btn_check);
		btn.setEnabled(true);
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
				getStockAssetObj("AssetCode", result);
			}
			break;

		case 2:
			if (data != null)
			{
				String result = data.getStringExtra("result");
				snvalue.setText(result);
				getStockAssetObj("SerialNumber", result);
			}
			break;

		case 3: {
			Log.e("camera", "recv");
			// Bitmap bmp = (Bitmap) data.getExtras().get("data");
			Bitmap bmp = getBitmap();
			String base64 = getBase64();
			
			//把string存起来用于比较，如果相同则不添加图片
			boolean b = compareBase(base64);
			if(b!=false){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("type", "pic");
				map.put("image", bmp);
				map.put("data", base64);
				listCheck.add(map);

				checkAdapter.notifyDataSetChanged();
				
				ListView lv = (ListView) this.findViewById(R.id.listinfo);

				lv.post(new Runnable() {
					@Override
					public void run() {
						ListView lv = (ListView) CheckActivity.this.findViewById(R.id.listinfo);
						lv.setSelection(checkAdapter.getCount() - 1);
					}
				});
			}
		}
		break;
		}
	}

	private boolean compareBase(String base64) {
		String image;
		SharedPreferences sp = CheckActivity.this.getSharedPreferences("ImageData", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		image = sp.getString("image", null);
		if(image!=null && image.equals(base64)){
			return false;
		}
		editor.putString("image", base64);
		editor.commit();
		return true;
	}

	//画界面
	private void setView(HashMap<String, Object> msAsset){
		//获取基础数据
		SharedPreferences sp = this.getSharedPreferences("BaseDatas", Context.MODE_PRIVATE);
		String string = sp.getString("BaseDatas", null);
		Log.i("获取的基础数据",string);
		List<CheckBaseData> baseDatas = new ArrayList<CheckBaseData>();
		if(string!=null){
			baseDatas = gson.fromJson(string,new TypeToken<List<CheckBaseData>>() {
			}.getType());
		}	

		assetCode = (String) msAsset.get("assetCode");
		String assetCode = null;
		String assetName = null;
		String useDeptName = null;
		String useUserName = null;
		String inventoryStateName = null;
		if(baseDatas!=null){
			Map<String, Object> map;
			for (int i = 0; i < baseDatas.size(); i++) {
				String key =  baseDatas.get(i).getKey();
				Log.i("key值", key);
				String name = baseDatas.get(i).getValue();
				if(key.equals("assetCode")){
					assetCode = name;
				}
				if(key.equals("assetName")){
					assetName = name;
				}
				if(key.equals("useDeptName")){
					useDeptName = name;
				}
				if(key.equals("useUserName")){
					useUserName = name;
				}
				if(key.equals("inventoryStateName")){
					inventoryStateName = name;
				}

			}
			map = new HashMap<String, Object>();
			map.put("type", "choose");
			map.put("id", "assetCode");
			map.put("value", msAsset.get("assetCode"));
			map.put("readonly", "true");
			map.put("name", assetCode);
			listCheck.add(map);
			
			map = new HashMap<String, Object>();
			map.put("type", "choose");
			map.put("id", "assetName");
			map.put("value", msAsset.get("assetName"));
			map.put("readonly", "true");
			map.put("name", assetName);
			listCheck.add(map);
			
			map = new HashMap<String, Object>();
			map.put("type", "choose");
			map.put("id", "useDeptName");
			map.put("value", msAsset.get("useDeptName"));
			map.put("readonly", "true");
			map.put("name",useDeptName);
			listCheck.add(map);
			
			map = new HashMap<String, Object>();
			map.put("type", "choose");
			map.put("id", "useUserName");
			map.put("value", msAsset.get("useUserName"));
			map.put("readonly", "true");
			map.put("name", useUserName);
			listCheck.add(map);
			
			map = new HashMap<String, Object>();
			map.put("type", "choose");
			map.put("id", "inventoryStateName");
			map.put("value", msAsset.get("inventoryStateName"));
			map.put("readonly", "true");
			map.put("name", inventoryStateName);
			listCheck.add(map);
			
			for (int i = 0; i < baseDatas.size(); i++) {
				String key =  baseDatas.get(i).getKey();
				String name = baseDatas.get(i).getValue();
				if(key.equals("assetCode")){
					continue;
				}
				if(key.equals("assetName")){
					continue;
				}
				if(key.equals("useDeptName")){
					continue;
				}
				if(key.equals("useUserName")){
					continue;
				}
				if(key.equals("inventoryStateName")){
					continue;
				}
				map = new HashMap<String, Object>();
				map.put("type", "choose");
				map.put("id", key);
				map.put("value", msAsset.get(key));
				map.put("readonly", "true");
				map.put("name", name);
				listCheck.add(map);
			}
			
			map = new HashMap<String, Object>();
			map.put("type", "addpic");
			map.put("id", "imgGuid");
//			map.put("value", "");//原
			map.put("value", msAsset.get("imgGuid"));
			map.put("name", "资产图片");
			listCheck.add(map);
		}
		
		String StockMemo = (String) msAsset.get("StockMemo");
		if(StockMemo!=null){
			remarkvalue.setText(StockMemo);
		}
		
	}
	
	private void setDataforCheck(HashMap<String, Object> hashMap){
		
		String StockMemo = remarkvalue.getText().toString();
		List<String> list = new ArrayList<String>(); //存储图片数据
		//获取图片数据
		List<Map<String, String>> listImgGuid = new ArrayList<Map<String, String>>();
		for (Map<String, Object> map2 : listCheck) {
			String type = (String) map2.get("type");

			if (type.equals("pic")) {

				Bitmap bmp = (Bitmap) map2.get("image");
				if (bmp != null) {
					String base64 = bitmapToBase64(bmp, 70);
					list.add(base64);
					Map<String, String> item = new HashMap<String, String>();
					item.put("ImageData", base64);
					listImgGuid.add(item);
				}
			}
		}
		Log.i("sss", ""+listImgGuid.size());

		hashMap.put("ImgGuid", listImgGuid);
		hashMap.put("inventoryStateName", "已盘");
		hashMap.put("StockMemo", StockMemo);
		
		Check.setDataList(AssetsNumber+mobile, list);
		String saveStr = gson.toJson(listSave);
		Log.i("保存时的数据", saveStr);

		SharedPreferences sp = CheckActivity.this.getSharedPreferences("checkdata", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("check"+mobile, saveStr);
		editor.commit();
		
		ToastUtil.showToast(context, "盘点成功!");
	}
	
	private void setImage(){
		List<String> list = Check.getDataList(AssetsNumber+mobile);
		if(list!=null && list.size()>0){
			for (String base64:list) {
				Map<String, Object> map = new HashMap<String, Object>();
				Bitmap bmp = decodeImg(base64);
				map.put("type", "pic");
				map.put("image", bmp);
				map.put("data", base64);
				listCheck.add(map);
			}
			
			checkAdapter.notifyDataSetChanged();
			
			ListView lv = (ListView) this.findViewById(R.id.listinfo);

			lv.post(new Runnable() {
				@Override
				public void run() {
					ListView lv = (ListView) CheckActivity.this.findViewById(R.id.listinfo);
					lv.setSelection(checkAdapter.getCount() - 1);
				}
			});
		}
	}
}
