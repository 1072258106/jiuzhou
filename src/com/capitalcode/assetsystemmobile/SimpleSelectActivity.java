package com.capitalcode.assetsystemmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liu.AssetsScan.adapter.ContentAdapter;
import com.liu.AssetsScan.adapter.SimpleSelectAdapter;
import com.liu.AssetsScan.model.SupplierModel;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SimpleSelectActivity extends BaseActivity {

	//private List<Map<String,String>> listCandidates = new ArrayList<Map<String,String>>();
	String[] allvalues;
	public static Map<String,Object> item;
	public static ContentAdapter updateadapter;
	String realvalue;
	
	SimpleSelectAdapter adapter;
	private List<Map<String,String>> list = new ArrayList<Map<String,String>>();
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
		EditText edit=(EditText)findViewById(R.id.value);
		
		String searchid = this.getIntent().getStringExtra("searchid");
		
		
		if( searchid.equals("AssetName") || searchid.equals("Standard") )
		{
			edit.setEnabled(true);
			edit.addTextChangedListener(new TextWatcher() {  
	            @Override  
	            public void onTextChanged(CharSequence s, int start, int before, int count) {  
	                  
	            }  
	              
	            @Override  
	            public void beforeTextChanged(CharSequence s, int start,   
	                    int count,int after) {  
	                  
	            }  
	             
	           @Override  
	            public void afterTextChanged(Editable s) {  
	               //将editText中改变的值设置的HashMap中  
					if (s != null && !"".equals(s.toString())) {
						list.clear();
						for (String str : allvalues) {
							if (str.contains(s)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("value", str);//

								list.add(map);
							}
						}

					} else {
						list.clear();
						for (String str : allvalues) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("value", str);//

							list.add(map);
						}

					}
					
					adapter.notifyDataSetChanged();
	        	   

	        	   
	            }  
	       });
		}
		else
		{
			edit.setEnabled(false);
		}
		
		
		
		
		if( searchid.equals("AssetName") )
		{
			String value = basedataModel.AssetName;
			
			allvalues = value.split("\\$");
			for( String str : allvalues)
			{
				Map<String, String> map = new HashMap<String, String>();
				map.put("value", str);//
				
				list.add(map);
			}
		}
		else if( searchid.equals("Standard") )
		{
			String value = basedataModel.Standard;
			
			allvalues = value.split("\\$");
			for( String str : allvalues)
			{
				Map<String, String> map = new HashMap<String, String>();
				map.put("value", str);//
				
				list.add(map);
			}
		}
		else if( searchid.equals("Supplier") || searchid.equals("EquipmentFactory") || searchid.equals("RepairFactory") )
		{
			List<SupplierModel> listSupplier = basedataModel.mapList.get(searchid);
			for( SupplierModel item : listSupplier )
			{
				Map<String, String> map = new HashMap<String, String>();
				map.put("value", item.Name);//
				map.put("realvalue", item.SupplierId);//
				
				list.add(map);
			}
			
		}		
		else 
		{
			Map<String,String> mapType = basedataModel.mapType.get(searchid);
			
			for (String key : mapType.keySet()) 
			{
				Map<String, String> map = new HashMap<String, String>();
				map.put("value", mapType.get(key));//
				map.put("realvalue", key);//
				
				list.add(map);
			}
			
			
		}
		
		
		
		
		adapter.notifyDataSetChanged();
		
		
		
		
		edit.setText(this.getIntent().getStringExtra("value"));		
 
		
		
		
	}

	@Override
	protected void Destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ViewInit() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_simple_select);
		
        TextView tv = (TextView)this.findViewById(R.id.tv_title_name);
        tv.setText(this.getIntent().getStringExtra("title"));
        
        Button btn = (Button)this.findViewById(R.id.btn_title_right);
		btn.setText("确认");
		
		btn.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				String search = SimpleSelectActivity.this.getIntent().getStringExtra("searchid");
				
				
				EditText edit=(EditText)findViewById(R.id.value);
				String value = edit.getText().toString();
					
				item.put("value", value);
				
				if( search.equals("AssetName") || search.equals("Standard") )
				{
					item.put("realvalue", value);
				}
				else
				{
					item.put("realvalue", realvalue);
				}

				finish();
				updateadapter.notifyDataSetChanged();
			}
			
		}
		);
		
		
	    ListView lv = (ListView)this.findViewById(R.id.chooselist);
		adapter = new SimpleSelectAdapter(this,list);
		lv.setAdapter(adapter);
		
		
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Map<String,String> map = list.get((int)arg3);
				String value = map.get("value");
				realvalue = map.get("realvalue");
				
				EditText edit=(EditText)findViewById(R.id.value);
				edit.setText(value);
			}
			
		}
				
				
		);
		
		

		

	    
		
		

		
		
	}

	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub
        LinearLayout ll = (LinearLayout)this.findViewById(R.id.tv_back);
        ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
