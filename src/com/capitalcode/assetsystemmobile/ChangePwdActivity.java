package com.capitalcode.assetsystemmobile;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.async.CallEarliest;
import com.liu.AssetsScan.async.Callable;
import com.liu.AssetsScan.async.Callback;
import com.liu.AssetsScan.model.RequestRetModel;
import com.liu.AssetsScan.util.ApiAddressHelper;
import com.liu.AssetsScan.util.Common;
import com.liu.AssetsScan.util.HttpHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePwdActivity extends BaseActivity {

	String changepwd;
	
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

	}

	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_changepwd);

		TextView tv = (TextView) this.findViewById(R.id.tv_title_name);
		tv.setText("修改密码");

		Button btn = (Button) this.findViewById(R.id.btn_title_left);
		btn.setVisibility(View.GONE);

		btn = (Button) this.findViewById(R.id.btn_title_right);
		btn.setVisibility(View.GONE);
		
		tv = (TextView) this.findViewById(R.id.tv_show);
		tv.setText(loginModel.LoginUser.UserLgName);
		
		
		btn = (Button) this.findViewById(R.id.btn_confirm);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				EditText et = (EditText)findViewById(R.id.codevalue);
				String oldpwd = et.getText().toString();
				
				et = (EditText)findViewById(R.id.snvalue);
				String newpwd = et.getText().toString();
				
				et = (EditText)findViewById(R.id.confirmvalue);
				String confirmpwd = et.getText().toString();	
				
				Map<String,String> map = new HashMap<String,String>();
				
				if( oldpwd.length()>0 && newpwd.length()>0 && confirmpwd.length()>0 && newpwd.equals(confirmpwd) )
				{
					changepwd = newpwd;
					
					map.put("LoginName", loginModel.LoginUser.UserLgName);
					map.put("Pwd", oldpwd);
					map.put("NewPwd", newpwd);
				}
				else
				{
					new AlertDialog.Builder(
							ChangePwdActivity.this)
							.setTitle("提示")
							// 设置对话框标题
							.setMessage("输入不合法!")
							// 设置显示的内容
							.setPositiveButton(
									"是",
									new DialogInterface.OnClickListener() {// 添加确定按钮
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {// 确定按钮的响应事件

										}
									}).show();// 在按键响应事件中显示此对话框
					
					return;
				}
				
				
				

				param.clear();
				param.put("LoginUser", map);
				
				

				doAsync(new CallEarliest<String>() {
					public void onCallEarliest() throws Exception {

						m_pDialog = new ProgressDialog(
								ChangePwdActivity.this);
						m_pDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						m_pDialog.setMessage("正在提交操作...");
						m_pDialog.setCancelable(false);
						m_pDialog.show();

					}
				}, new Callable<String>() {
					public String call() throws Exception {
						return HttpHelper.getInstance(context).Post(
								ApiAddressHelper.changepwd(), param);
					}
				}, new Callback<String>() {
					public void onCallback(String res) {
						m_pDialog.hide();
						if (res.equals("")) {

							Common.ShowInfo(context, "网络故障");
							return;
						}
						try {
							
							
							RequestRetModel<String> model = gson.fromJson(res,
									new TypeToken<RequestRetModel<String>>() {
									}.getType());
							if (model != null) {
								{
									Common.ShowInfo(context,
											model.response.ErrorMsg);
								}
								
								if( model.response.ErrorCode.equals("S00000")) 
								{
				                    SharedPreferences sp = ChangePwdActivity.this.getSharedPreferences("logindata", Context.MODE_PRIVATE);
				                    SharedPreferences.Editor editor = sp.edit();
				               
				                    
				                    editor.putString( "pwd", changepwd);
				                    pwd = changepwd;
				                    
				                    editor.commit();
								}
								
							}


							

						} catch (JsonSyntaxException localJsonSyntaxException) {
							Log.e("iws", "Login json转换错误 e:"
									+ localJsonSyntaxException);

						}
					}
				});

			}
		});

	}

}
