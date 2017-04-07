package com.capitalcode.assetsystemmobile;

import android.net.Uri;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.async.CallEarliest;
import com.liu.AssetsScan.async.Callable;
import com.liu.AssetsScan.async.Callback;
import com.liu.AssetsScan.model.RequestRetModel;
import com.liu.AssetsScan.model.SearchVersionModel;
import com.liu.AssetsScan.util.ApiAddressHelper;
import com.liu.AssetsScan.util.Common;
import com.liu.AssetsScan.util.HttpHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PersonalCenterActivity extends BaseActivity {

	private String mUrl;

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

	String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void ViewInit() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_personalcenter);

		TextView tv = (TextView) this.findViewById(R.id.tv_title_name);
		tv.setText("个人中心");

		Button btn = (Button) this.findViewById(R.id.btn_title_left);
		btn.setVisibility(View.GONE);

		btn = (Button) this.findViewById(R.id.btn_title_right);
		btn.setVisibility(View.GONE);

		tv = (TextView) this.findViewById(R.id.tv_show);
		tv.setText(loginModel.LoginUser.UserName);
		Log.i("UserName", tv.toString());

		tv = (TextView) this.findViewById(R.id.tv_state);
		tv.setText(loginModel.LoginUser.PosName);
		Log.i("PosName", tv.toString());
		
		tv = (TextView) this.findViewById(R.id.tv_time);
		tv.setText(loginModel.LoginUser.DeptName);
		Log.i("DeptName", tv.toString());
		
		tv = (TextView) this.findViewById(R.id.tv_author);
		tv.setText(getVersion());

		btn = (Button) this.findViewById(R.id.btn_search_update);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				param.clear();

				doAsync(new CallEarliest<String>() {
					public void onCallEarliest() throws Exception {

						m_pDialog = new ProgressDialog(
								PersonalCenterActivity.this);
						m_pDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						m_pDialog.setMessage("正在获取最新版本号...");
						m_pDialog.setCancelable(false);
						m_pDialog.show();

					}
				}, new Callable<String>() {
					public String call() throws Exception {
						return HttpHelper.getInstance(context).Post(
								ApiAddressHelper.getLatestVersion(), param);
					}
				}, new Callback<String>() {
					public void onCallback(String res) {
						m_pDialog.hide();
						if (res.equals("")) {

							Common.ShowInfo(context, "网络故障");
							return;
						}
						try {
							RequestRetModel<SearchVersionModel> model = gson
									.fromJson(
											res,
											new TypeToken<RequestRetModel<SearchVersionModel>>() {
											}.getType());
							mUrl = model.rspcontent.MsVersion.Url;
							if (model != null) {

								String version = model.rspcontent.MsVersion.SystemVersion;

								if (getVersion().compareTo(version) <= 0) {
									new AlertDialog.Builder(
											PersonalCenterActivity.this)
											.setTitle("提示")
											// 设置对话框标题
											.setMessage(
													"有最新版本" + version
															+ ",是否下载？")
											// 设置显示的内容
											.setPositiveButton(
													"是",
													new DialogInterface.OnClickListener() {// 添加确定按钮
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {// 确定按钮的响应事件
															/*UpdateManager manager = new UpdateManager(PersonalCenterActivity.this,mUrl);
															// 检查软件更新
															manager.showDownloadDialog();*/
															Intent intent = new Intent();
															intent.setAction("android.intent.action.VIEW");
															Uri content_Uri = Uri.parse(mUrl);
															intent.setData(content_Uri);
															startActivity(intent);
															return;
														}
													})
											.setNegativeButton(
													"否",
													new DialogInterface.OnClickListener() {// 添加返回按钮
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {// 响应事件

														}
													}).show();// 在按键响应事件中显示此对话框
								} else {
									new AlertDialog.Builder(
											PersonalCenterActivity.this)
											.setTitle("提示")
											// 设置对话框标题
											.setMessage("当前已是最新版!")
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

								}
							} 
						} catch (JsonSyntaxException localJsonSyntaxException) {

							RequestRetModel<String> model = gson.fromJson(res,
									new TypeToken<RequestRetModel<String>>() {
									}.getType());
							if (model != null) {
								if (model.response.ErrorCode.equals("S00000") == false) {
									Common.ShowInfo(context,
											model.response.ErrorMsg);
								}
							}
						}
					}
				});

			}
		});

		btn = (Button) this.findViewById(R.id.btn_change_pwd);
		btn.setVisibility(View.GONE);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(PersonalCenterActivity.this,
						ChangePwdActivity.class);

				PersonalCenterActivity.this.startActivity(intent);

			}
		});

		btn = (Button) findViewById(R.id.btn_change_signature);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PersonalCenterActivity.this,
						ElectronicSignatureActivity.class);

				PersonalCenterActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub

	}
}
