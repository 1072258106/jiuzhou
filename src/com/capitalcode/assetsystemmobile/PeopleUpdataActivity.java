package com.capitalcode.assetsystemmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.capitalcode.assetsystemmobile.R.layout;
import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.adapter.ContentAdapter;
import com.liu.AssetsScan.model.CheckBaseData;
import com.liu.AssetsScan.model.SaveCheckDataModel;
import com.liu.AssetsScan.util.ListDataSave;
import com.liu.AssetsScan.util.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PeopleUpdataActivity extends BaseActivity implements OnClickListener {


	private List<Map<String, Object>> listCheck = new ArrayList<Map<String, Object>>();
	ContentAdapter checkAdapter;
	String BatchNumber;
	String assetId;

	int HaveStock = 0;
	int NoStock = 0;
	int OverStock = 0;


	List<SaveCheckDataModel> listSave;
	//	List<MsAssetModel> MsAsset;

	private List<HashMap<String, Object>> MsAssetDatas;
	String assetCode;

	//存储图片数据的偏好设置
	private ListDataSave PeopleCheck;
	private String spName = "PeoplePic";
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
	protected void DataInit() {
		// TODO Auto-generated method stub

		Intent intent = getIntent();
		String result = intent.getStringExtra("assetscode");
		BatchNumber = intent.getStringExtra("BatchNumber");

		SharedPreferences sp = PeopleUpdataActivity.this.getSharedPreferences("peoplecheckdata", Context.MODE_PRIVATE);
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
					PeopleCheck = new ListDataSave(this, spName+BatchNumber);
					break;
				}
			}
		}
		getStockAssetObj("AssetCode", result);	
	}

	@Override
	protected void Destroy() {

	}

	@Override
	protected void ViewInit() {
		// TODO Auto-generated method stub
		setContentView(layout.activity_people_updata);

		Button btn = (Button)this.findViewById(R.id.btn_title_left);

		btn = (Button)this.findViewById(R.id.btn_title_right);
		btn.setVisibility(View.GONE);

		TextView tv = (TextView)this.findViewById(R.id.tv_title_name);
		tv.setText("个人核查");

		setuplist(new HashMap<String, Object>());
		ListView lv = (ListView) this.findViewById(R.id.people_listinfo);
		checkAdapter = new ContentAdapter(this, listCheck,
				this.basedataModel.StockLabel);
		lv.setAdapter(checkAdapter);

		btn = (Button)this.findViewById(R.id.people_btn_clear);
		btn.setOnClickListener(this);

		btn = (Button)this.findViewById(R.id.people_btn_check);
		btn.setOnClickListener(this);
		btn.setEnabled(false);

		BatchNumber = this.getIntent().getStringExtra("BatchNumber");
	}

	@Override
	protected void ViewListen() {

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{

		case R.id.people_btn_clear:
		{
			EditText edit = (EditText)this.findViewById(R.id.people_remarkvalue);
			edit.setText("");

			setuplist(new HashMap<String, Object>());
			checkAdapter.notifyDataSetChanged();

			TextView tv = (TextView)PeopleUpdataActivity.this.findViewById(R.id.people_tvresult);
			tv.setText("");
			tv.setVisibility(View.GONE);
		}
		break;

		//盘点资产
		case R.id.people_btn_check:
		{
			//				saveImage();
			
			
			Map<String,Object> map = listCheck.get(0);
			String AssetCode = (String)map.get("value");
			
			if(AssetCode==null || AssetCode ==""){
				ToastUtil.showToast(this, "资产编号不能为空");
				break;
			}

			map = listCheck.get(listCheck.size()-1);
			String SerialNumber = (String)map.get("value");

			if( AssetCode.length()>0 || SerialNumber.length() > 0 )
			{
//				EditText edit = (EditText)this.findViewById(R.id.people_remarkvalue);
//				String StockMemo = edit.getText().toString();
				if( assetId != null )
				{
					for( HashMap<String, Object> model : MsAssetDatas )
					{
						if( model.get("assetId").equals(assetId))
						{
//							if( model.get("inventoryStateName").equals("未核查"))
//							{
//								//<---->
//								//获取图片数据
//								setDataForUpdata(model);
//							}
//							else
//							{
								setDataForUpdata(model);
//								new AlertDialog.Builder(PeopleUpdataActivity.this).setTitle("提示")//设置对话框标题  		  
//								.setMessage("该资产已盘点!")//设置显示的内容  
//								.setPositiveButton("是",new DialogInterface.OnClickListener() {//添加确定按钮  
//									@Override  
//									public void onClick(DialogInterface dialog, int which) 
//									{//确定按钮的响应事件  
//
//									}  
//								}).show();//在按键响应事件中显示此对话框
//							}
						}
					}
				}
			}
		}

		Button btn = (Button)PeopleUpdataActivity.this.findViewById(R.id.people_btn_check);
		btn.setEnabled(false);

		break;
		}
	}

	void setuplist(HashMap<String, Object> model) {
		listCheck.clear();
		Log.e("enterenter","8888888888888888888888888888888888888");
		setView(model);
	}


	void getStockAssetObj(String type,String result)
	{
		assetId = null;
		if( "AssetCode".equals(type) )
		{
			for( HashMap<String, Object> model : MsAssetDatas )
			{
				if( model.get("assetCode").equals(result))
				{
					assetId = (String) model.get("assetId");
					setuplist(model);
					checkAdapter.notifyDataSetChanged();
					AssetsNumber = (String) model.get("assetCode");
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
					assetId =(String) model.get("str9");
					setuplist(model);
					checkAdapter.notifyDataSetChanged();
					AssetsNumber = (String) model.get("assetCode");
					if(AssetsNumber!=null){
						setImage();
					}
					break;
				}
			}
		}
		Button btn = (Button)PeopleUpdataActivity.this.findViewById(R.id.people_btn_check);
		btn.setEnabled(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		switch (requestCode)
		{
		case 3: {
			Log.e("camera", "recv");

			Bitmap bmp = getBitmap();
			String base64 = getBase64();
			boolean b = compareBase(base64);
			if(b!=false){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("type", "pic");
				map.put("image", bmp);
				map.put("data", base64);
				listCheck.add(map);

				checkAdapter.notifyDataSetChanged();

				ListView lv = (ListView) this.findViewById(R.id.people_listinfo);

				lv.post(new Runnable() {
					@Override
					public void run() {
						// Select the last row so it will scroll into view...
						ListView lv = (ListView) PeopleUpdataActivity.this.findViewById(R.id.people_listinfo);
						lv.setSelection(checkAdapter.getCount() - 1);
					}
				});	
			}
		}
		break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	//画界面
	private void setView(HashMap<String, Object> msAsset){
		//获取基础数据
		SharedPreferences sp = this.getSharedPreferences("BaseDatas", Context.MODE_PRIVATE);
		String string = sp.getString("BaseDatas", null);
		List<CheckBaseData> baseDatas = new ArrayList<CheckBaseData>();
		if(string!=null){
			baseDatas = gson.fromJson(string,new TypeToken<List<CheckBaseData>>() {
			}.getType());
		}	

		if(baseDatas!=null){
			Map<String, Object> map;
			for (int i = 0; i < baseDatas.size(); i++) {
				String key =  baseDatas.get(i).getKey();
				String name = baseDatas.get(i).getValue();
				if(key.equals("assetCode")){
					map = new HashMap<String, Object>();
					map.put("type", "choose");
					map.put("id", key);
					map.put("value", msAsset.get("assetCode"));
					map.put("readonly", "true");
					map.put("name", name);
					listCheck.add(map);
				}
				if(key.equals("assetName")){
					map = new HashMap<String, Object>();
					map.put("type", "choose");
					map.put("id", key);
					map.put("value", msAsset.get("assetName"));
					map.put("readonly", "true");
					map.put("name", name);
					listCheck.add(map);
				}
				if(key.equals("useDeptName")){
					map = new HashMap<String, Object>();
					map.put("type", "choose");
					map.put("id", key);
					map.put("value", msAsset.get("useDeptName"));
					map.put("readonly", "true");
					map.put("name",name);
					listCheck.add(map);
				}
				if(key.equals("useUserName")){
					map = new HashMap<String, Object>();
					map.put("type", "choose");
					map.put("id", key);
					map.put("value", msAsset.get("useUserName"));
					map.put("readonly", "true");
					map.put("name", name);
					listCheck.add(map);
				}
				if(key.equals("drawDT")){
					map = new HashMap<String, Object>();
					map.put("type", "choose");
					map.put("id", key);
					map.put("value", msAsset.get("drawDT"));
					map.put("readonly", "true");
					map.put("name", name);
					listCheck.add(map);
				}

			}
			for (int i = 0; i < baseDatas.size(); i++) {
				String key =  baseDatas.get(i).getKey();
				String name = baseDatas.get(i).getValue();
				if(key.equals("assetCode")){
					break;
				}
				if(key.equals("assetName")){
					break;
				}
				if(key.equals("useDeptName")){
					break;
				}
				if(key.equals("useUserName")){
					break;
				}
				if(key.equals("drawDT")){
					break;
				}
				map = new HashMap<String, Object>();
				map.put("type", "choose");
				map.put("id", key);
				map.put("value", msAsset.get(key));
				map.put("readonly", "true");
				map.put("name", name);
				listCheck.add(map);
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "addpic");
		map.put("id", "imgGuid");
		map.put("value", "");
		map.put("name", "资产图片");
		listCheck.add(map);
		
		String StockMemo = (String) msAsset.get("StockMemo");
		if(StockMemo!=null){
			EditText editText = (EditText) findViewById(R.id.people_remarkvalue);
			editText.setText(StockMemo);
		}
	}
	
	private boolean compareBase(String base64) {
		String image;
		SharedPreferences sp = PeopleUpdataActivity.this.getSharedPreferences("ImageData", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		image = sp.getString("image", null);
		if(image!=null && image.equals(base64)){
			return false;
		}
		editor.putString("image", base64);
		editor.commit();
		return true;
	}
	
	private void setDataForUpdata(HashMap<String, Object> model){
		EditText edit = (EditText)this.findViewById(R.id.people_remarkvalue);
		String StockMemo = edit.getText().toString();
		//获取图片数据
		List<Map<String, String>> listImgGuid = new ArrayList<Map<String, String>>();
		List<String> list = new ArrayList<String>();
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
		
		model.put("ImgGuid", listImgGuid);
		model.put("inventoryStateName", "已核查");
		model.put("StockMemo", StockMemo);
		
		PeopleCheck.setDataList(AssetsNumber+mobile, list);
		
		String saveStr = gson.toJson(listSave);

		SharedPreferences sp = PeopleUpdataActivity.this.getSharedPreferences("peoplecheckdata", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		Log.e("gson2",saveStr);

		editor.putString("check"+mobile, saveStr);

		editor.commit();

		ToastUtil.showToast(context, "盘点成功!");
	}
	
	private void setImage(){
		List<String> list = PeopleCheck.getDataList(AssetsNumber+mobile);
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
			
			ListView lv = (ListView) this.findViewById(R.id.people_listinfo);

			lv.post(new Runnable() {
				@Override
				public void run() {
					ListView lv = (ListView) PeopleUpdataActivity.this.findViewById(R.id.people_listinfo);
					lv.setSelection(checkAdapter.getCount() - 1);
				}
			});
		}
	}
}
