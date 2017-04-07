package com.capitalcode.assetsystemmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.adapter.ContentAdapter;
import com.liu.AssetsScan.model.OverageAssets;
import com.liu.AssetsScan.model.SaveCheckDataModel;
import com.liu.AssetsScan.util.CurrentTime;
import com.liu.AssetsScan.util.ToastUtil;
import com.wyy.twodimcode.CaptureActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
/**
 * 盘盈界面
 * 
 */
public class OverageActivity extends BaseActivity implements OnClickListener{

	private String BatchId;
	private String Reason;
	private String[] ReasonValues;
	private Spinner SpReason;
	private ContentAdapter adapter;
	private List<Map<String, Object>> listBase = new ArrayList<Map<String,Object>>();
	private List<SaveCheckDataModel> listSave;
	private ListView listView;
	private List<OverageAssets> overageAssets;
	private Button save;
	private Button scan;
	private String flag = "read";
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
		setContentView(R.layout.activity_overage);
		Intent intent = getIntent();
		String assetsCode = intent.getStringExtra("assetCode");
		BatchId = intent.getStringExtra("BatchId");

		OverageAssets assets = new OverageAssets();
		if(assetsCode!=null){
			assets.pycode = assetsCode;
		}
		setDataForView(assets);

		listView = (ListView) this.findViewById(R.id.overage_lv);
		adapter = new ContentAdapter(this, listBase);
		listView.setAdapter(adapter);

		Button btn = (Button)this.findViewById(R.id.btn_title_left);

		btn = (Button)this.findViewById(R.id.btn_title_right);
		btn.setVisibility(View.GONE);

		TextView tv = (TextView)this.findViewById(R.id.tv_title_name);
		tv.setText("盘盈资产");

		SpReason = (Spinner)findViewById(R.id.reason_overage_sp);
		setDataForReason();

		//获取已经保存的盘盈资产信息
		saveAssets();

		scan = (Button)findViewById(R.id.scan_btn);
		save = (Button)findViewById(R.id.ok_btn);
		scan.setOnClickListener(this);
		save.setOnClickListener(this);

	}

	@Override
	protected void ViewListen() {

	}

	//初始化盘盈界面的值
	private void setDataForView(OverageAssets paramOverageAssets){
		paramOverageAssets.inventoryStateName = "盘盈";
		setData(paramOverageAssets);
	}

	//为盘盈原因赋值
	private void setDataForReason()
	{
		ReasonValues = new String[6];
		ReasonValues[0] = "";
		ReasonValues[1] = "借用盘盈";
		ReasonValues[2] = "赠送盘盈";
		ReasonValues[3] = "无账盘盈";
		ReasonValues[4] = "报废盘盈";
		ReasonValues[5] = "其他盘盈";
		setDataForSpinner(ReasonValues);
	}

	private void setDataForSpinner(String[] Items){
		ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(OverageActivity.this,
				android.R.layout.simple_spinner_item, Items);
		SpReason.setAdapter(_Adapter);
		SpReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//获取选中的盘盈原因
				String str = parent.getItemAtPosition(position).toString();
				Reason = str;
			}
			public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView){

			}
		});
	}

	//画界面
	private void setData(OverageAssets assets){
		listBase.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		map = new HashMap<String, Object>();
		map.put("type", "edit");
		map.put("id", "pycode");
		map.put("value", assets.pycode);
		map.put("name", "资产条码");
		listBase.add(map);

		map = new HashMap<String, Object>();
		map.put("type", "edit");
		map.put("id", "pyname");
		map.put("value", assets.pyname);
		map.put("name", "资产名称");
		listBase.add(map);

		map = new HashMap<String, Object>();
		map.put("type", "edit");
		map.put("id", "inventoryStateName");
		map.put("value", assets.inventoryStateName);
		map.put("readonly", "true");
		map.put("name", "资产状态");
		listBase.add(map);

		map = new HashMap<String, Object>();
		map.put("type", "edit");
		map.put("id", "pyexplan");
		map.put("value", assets.pyexplan);
		map.put("name", "盘盈说明");
		listBase.add(map);

//		map = new HashMap<String, Object>();
//		map.put("type", "edit");
//		map.put("id", "pydept");
//		map.put("value", assets.pydept);
//		map.put("name", "盘盈部门");
//		map.put("readonly", "true");
//		listBase.add(map);

//		map = new HashMap<String, Object>();
//		map.put("type", "edit");
//		map.put("id", "pyuser");
//		map.put("value", assets.pyuser);
//		map.put("name", "盘盈人");
//		map.put("readonly", "true");
//		listBase.add(map);

		map = new HashMap<String, Object>();
		map.put("type", "edit");
		map.put("id", "models");
		map.put("value", assets.models);
		map.put("name", "规格型号");
		listBase.add(map);

		map = new HashMap<String, Object>();
		map.put("type", "edit");
		map.put("id", "count");
		map.put("value", assets.count);
		map.put("inputtype", String.valueOf(InputType.TYPE_CLASS_NUMBER));
		map.put("name", "数量");
		listBase.add(map);

		map = new HashMap<String, Object>();
		map.put("type", "edit");
		map.put("id", "saveplace");
		map.put("value", assets.saveplace);
		map.put("name", "存放位置");
		listBase.add(map);

//		map = new HashMap<String, Object>();
//		map.put("type", "edit");
//		map.put("id", "pytime");
//		map.put("value", assets.pytime);
//		map.put("name", "盘盈时间");
//		listBase.add(map);

		map = new HashMap<String, Object>();
		map.put("type", "addpic");
		map.put("id", "imgGuid");
		map.put("value", "");
		map.put("name", "资产图片");
		listBase.add(map);
	}


	//扫描回调
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
		case 1:
			if (data != null)
			{
				String result = data.getStringExtra("result");
				OverageAssets assets = new OverageAssets();
				assets.pycode = result;
				setDataForView(assets);
				adapter.notifyDataSetChanged();
			}
			break;
		case 3: 
			Log.e("camera", "recv");
			// Bitmap bmp = (Bitmap) data.getExtras().get("data");
			Bitmap bmp = getBitmap();
			String base64 = getBase64();

			//把string存起来用于比较，如果相同则不添加图片
			boolean b = compareBase(base64);
			if(b!=false){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", "img");
				map.put("type", "pic");
				map.put("image", bmp);
				map.put("value", base64);
				listBase.add(map);

				adapter.notifyDataSetChanged();
				listView.post(new Runnable() {
					@Override
					public void run() {
						listView.setSelection(adapter.getCount() - 1);
					}
				});
				break;
			}
		}
	}
	
	private boolean compareBase(String base64) {
		String image;
		SharedPreferences sp = OverageActivity.this.getSharedPreferences("OverageImageData", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		image = sp.getString("image"+mobile, null);
		if(image!=null && image.equals(base64)){
			return false;
		}
		editor.putString("image"+mobile, base64);
		editor.commit();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scan_btn:
			Intent it = new Intent(this, CaptureActivity.class);
			startActivityForResult(it, 1);
			break;

		case R.id.ok_btn:
			flag = "write";
			setDataAssets(new CurrentTime().getDate());
			break;
		}

	}

	//为盘盈资产赋值
	private void setDataAssets(String date) {
		OverageAssets assets = new OverageAssets();
		for (Map<String, Object> map :listBase) {
			String key = (String) map.get("id");
			String value = (String) map.get("realvalue");
			Log.i("输出的key", key);
			if(value!=null){
				Log.i("获取的value",value);
			}
			if(key.equals("pycode")){
				assets.pycode = value;
			}
			if(key.equals("pyname")){
				assets.pyname = value;
			}
			if(key.equals("inventoryStateName")){
				assets.inventoryStateName = value;
			}
			if(key.equals("pyexplan")){
				assets.pyexplan = value;
			}
			if(key.equals("models")){
				assets.models = value;
			}
			if(key.equals("saveplace")){
				assets.saveplace = value;
			}
			if(key.equals("count")){
				assets.count = value;
			}
			if(assets.inventoryStateName==""||assets.inventoryStateName==null){
				assets.inventoryStateName = "盘盈";
			}
			if(assets.count==null||assets.count.length()<=0){
				assets.count = ""+1;
			}
			
			//获取图片数据
			List<Map<String, String>> listImgGuid = new ArrayList<Map<String, String>>();
			for (Map<String, Object> map2 : listBase) {
				String type = (String) map2.get("type");

				if (type.equals("pic")) {

					Bitmap bmp = (Bitmap) map2.get("image");
					if (bmp != null) {
						String base64 = bitmapToBase64(bmp, 70);
						Map<String, String> item = new HashMap<String, String>();
						item.put("ImageData", base64);
						listImgGuid.add(item);
					}
				}
			}
			
			assets.pyuserName = loginModel.LoginUser.UserName;
			assets.pydeptName = loginModel.LoginUser.DeptName;
			assets.pyuser = loginModel.LoginUser.UserId;
			assets.pydept = loginModel.LoginUser.DeptId;
			assets.pyreason = Reason;
			assets.pytime = date;
//			assets.ImgGuid = listImgGuid;
			for (int i = 0; i < listImgGuid.size(); i++) {
				String string = listImgGuid.get(i).get("ImageData");
				if(i==0){
					assets.img1 = string;
				}
				if(i==1){
					assets.img2 = string;
				}
				if(i==2){
					assets.img3 = string;
				}
				if(i==3){
					assets.img4 = string;
				}
				if(i==4){
					assets.img5 = string;
				}
			}
		}
		if(assets.pyname==null||assets.pyname.length()<=0){
			ToastUtil.showToast(this, "资产名称不能为空");
			return;
		}
		if(assets!=null){
			overageAssets.add(assets);
			saveAssets();
		}

	}

	//读取or保存盘盈资产信息
	private void saveAssets(){
		SharedPreferences sp = OverageActivity.this.getSharedPreferences("checkdata", Context.MODE_PRIVATE);
		String strlist = sp.getString("check"+mobile, null);
		Editor editor = sp.edit();
		if( strlist != null && strlist!="null")
		{
			listSave = gson
					.fromJson(
							strlist,
							new TypeToken<List<SaveCheckDataModel>>() {
							}.getType());
			Log.i("盘盈界面的解析数据", strlist);
			for( SaveCheckDataModel model :  listSave)
			{
				if(model.MsStock.BatchId.equals(BatchId))
				{
					if(flag == "read"){
						overageAssets = new ArrayList<OverageAssets>();
						overageAssets = model.MsPYInfo;
					}
					if(flag == "write"){
						model.MsPYInfo = overageAssets;
						flag = "read";
						ToastUtil.showToast(this, "盘盈成功");
					}
					break;
				}
			}
		}
		if(overageAssets == null){
			overageAssets = new ArrayList<OverageAssets>();
		}
		if(overageAssets!=null){
			String string = gson.toJson(listSave);
			Log.i("保存的信息", string);
			editor.putString("check"+mobile, string);
			editor.commit();
		}
	}
}
