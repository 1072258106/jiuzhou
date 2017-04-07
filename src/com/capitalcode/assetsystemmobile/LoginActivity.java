package com.capitalcode.assetsystemmobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.liu.AssetsScan.async.CallEarliest;
import com.liu.AssetsScan.async.Callable;
import com.liu.AssetsScan.async.Callback;
import com.liu.AssetsScan.model.LoginDataModel;
import com.liu.AssetsScan.model.MenuModel;
import com.liu.AssetsScan.model.RequestRetModel;
import com.liu.AssetsScan.util.ApiAddressHelper;
import com.liu.AssetsScan.util.HttpHelper;
import com.liu.AssetsScan.util.StaticUtil;
import com.liu.AssetsScan.util.ToastUtil;
import com.wyy.twodimcode.CaptureActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
	private RelativeLayout pingheng;
	private RelativeLayout re;
	private EditText api;
	private String loginname;

	@Override
	public void onClick(View arg0) {

		String apistring;
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
		case R.id.btn_reset:
		{
			EditText etname = (EditText)this.findViewById(R.id.et_name);
			EditText etpwd = (EditText)this.findViewById(R.id.et_pwd);

			String name = etname.toString();

			if( name.length()>0 /*&& password.length()>0*/ )
			{
				SharedPreferences sp = LoginActivity.this.getSharedPreferences("logindata", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();

				editor.putString( "mobile", null);
				editor.putString( "pwd", null);
				editor.putString( "logindata"+name, null);
				editor.putString("flagLogin", "No");

				editor.commit();

				sp = this.getSharedPreferences("basedata"
						, Context.MODE_PRIVATE);

				editor = sp.edit();
				editor.putString( "basedata"+name, null);
				editor.commit();
			}
			etname.setText("");
			etpwd.setText("");
		}

		break;
		case R.id.btn_title_right:
		{
			new AlertDialog.Builder(LoginActivity.this).setTitle("提示")//设置对话框标题  		  
			.setMessage("确认要退出吗？")//设置显示的内容  
			.setPositiveButton("是",new DialogInterface.OnClickListener() {//添加确定按钮  
				@Override  
				public void onClick(DialogInterface dialog, int which) 
				{//确定按钮的响应事件  
					android.os.Process.killProcess(android.os.Process.myPid());
					return;
				}  
			}).setNegativeButton("否",new DialogInterface.OnClickListener() 
			{//添加返回按钮  
				@Override  
				public void onClick(DialogInterface dialog, int which) 
				{//响应事件  
				}  
			}).show();//在按键响应事件中显示此对话框
		}
		break;
		case R.id.btn_login:
		{
			EditText etname = (EditText)this.findViewById(R.id.et_name);
			EditText etpwd = (EditText)this.findViewById(R.id.et_pwd);
			try {
				mobile = etname.getText().toString().trim();
				pwd = etpwd.getText().toString().trim();
				if(StringUtils.isEmpty(mobile)){
					ToastUtil.showToast(this.context, "账号不能为空");
					return;
				}
				if(StringUtils.isEmpty(pwd)){
					ToastUtil.showToast(this.context, "密码不能为空");
					return;
				}
			} catch (Exception localException) {
				Log.e("iws", "Login e" + localException);
				return;
			}
			this.param.clear();
			this.param.put("loginname", mobile);
			this.param.put("pwd", pwd);
			this.param.put("maccode", deviceId);//352315052327497
			doAsync(new CallEarliest<String>() {
				public void onCallEarliest() throws Exception {

					m_pDialog = new ProgressDialog(LoginActivity.this);
					m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					m_pDialog.setMessage("正在获取登录数据...");
					m_pDialog.setCancelable(false);
					m_pDialog.show();

				}
			}, new Callable<String>() {
				public String call() throws Exception {
					return HttpHelper.getInstance(context).Post(
							ApiAddressHelper.getLoginUrl(), param);
				}
			}, new Callback<String>() {
				public void onCallback(String res) {

					if (res.equals("")) {
						m_pDialog.hide();
						ToastUtil.showToast(
								context,"网络故障");
						return;
					}
					try {
						loginModel = gson.fromJson(res,
								new TypeToken<LoginDataModel>() {
						}.getType());
						if (loginModel != null && loginModel.LoginUser != null ) {

							SharedPreferences sp = LoginActivity.this.getSharedPreferences("logindata", Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = sp.edit();
							//初始化个推
							PushManager pushManager = PushManager.getInstance();
							pushManager.initialize(LoginActivity.this, DemoPushService.class);
							pushManager.registerPushIntentService(LoginActivity.this, DemoIntentService.class);

							//解绑之后不能立即绑定，要等待起码5秒的时间
							pushManager.bindAlias(LoginActivity.this, mobile);
							Log.i("绑定的名字", mobile);
							
							//做判断，如果记录的用户名与登陆名不一致，解绑记录用户名，绑定新用户名
							loginname = sp.getString("mobile", null);
							if(loginname!=null && !loginname.equals(mobile)){
								StaticUtil.LoginName = loginname;
								//设置一个定时任务，10秒后解绑之前绑定的名字
								AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
								long triggerAtTime = SystemClock.elapsedRealtime()+10*1000;
								Intent intent = new Intent(LoginActivity.this,MyAlarmReceiver.class);
								PendingIntent pi = PendingIntent.getBroadcast(LoginActivity.this, 0, intent, 0);
								manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
							}
							editor.putString( "mobile", mobile);
							editor.putString( "pwd", pwd);
							editor.putString( "logindata"+mobile, res);
							editor.putString("flagLogin", "Yes");
							editor.commit();
							m_pDialog.hide();
							
							CheckMenuActivity.listmenu.clear();

							MenuModel model = new MenuModel();
							model.MenuId = "offline_download";
							model.MenuName = "盘点下载";
							CheckMenuActivity.listmenu.add(model);

							model = new MenuModel();
							model.MenuId = "offline_check";
							model.MenuName = "离线盘点";
							CheckMenuActivity.listmenu.add(model);

							model = new MenuModel();
							model.MenuId = "offline_statistics";
							model.MenuName = "盘点统计";
							CheckMenuActivity.listmenu.add(model);

							model = new MenuModel();
							model.MenuId = "offline_upload";
							model.MenuName = "盘点上传";
							CheckMenuActivity.listmenu.add(model);

							Intent intent = new Intent(LoginActivity.this, CheckMenuActivity.class);
							LoginActivity.this.startActivity(intent);
							finish();
						} 
						else 
						{
							m_pDialog.hide();
							RequestRetModel<String> model = gson.fromJson(res,
									new TypeToken<RequestRetModel<String>>() {
							}.getType());
							if( model != null )
							{
								if( model.response.ErrorCode.equals("S00000") == false )
								{
									ToastUtil.showToast(context, model.response.ErrorMsg);
								}
							}
						}
					} catch (JsonSyntaxException localJsonSyntaxException) {
						m_pDialog.hide();

						RequestRetModel<String> model = gson.fromJson(res,
								new TypeToken<RequestRetModel<String>>() {
						}.getType());
						if( model != null )
						{
							if( model.response.ErrorCode.equals("S00000") == false )
							{
								ToastUtil.showToast(context, model.response.ErrorMsg);
							}
						}

						Log.e("iws", "Login json转换错误 e:" + localJsonSyntaxException);
					}
				}
			});
		}
		break;

		case R.id.login_api_save:
			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				File sdcardDir =Environment.getExternalStorageDirectory();
				//得到一个路径，内容是sdcard的文件夹路径和名字
				String path=sdcardDir.getPath()+"/webapi/webapi.txt";

				//写入文件
				apistring = api.getText().toString();
				Log.i("apistring", "apistring="+apistring);

				if(apistring.equals("")){
					ToastUtil.showToast(context, "输入不能为空");
				}else {
					writeFileSdcard(path, apistring.trim());
					ToastUtil.showToast(context, "保存成功");
				}
			}
			break;

		case R.id.login_api_scan:
			Intent it = new Intent(LoginActivity.this, CaptureActivity.class);
			startActivityForResult(it, 1);
			break;
		}
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

		setContentView(R.layout.activity_login);

		LinearLayout  ll = (LinearLayout)this.findViewById(R.id.tv_back);
		ll.setVisibility(View.GONE);

		re = (RelativeLayout) this.findViewById(R.id.login_api_set);
		re.setVisibility(View.GONE);

		pingheng = (RelativeLayout) this.findViewById(R.id.login_pingheng);


		Button btn = (Button)this.findViewById(R.id.btn_title_left);
		btn.setVisibility(View.GONE);

		btn = (Button)this.findViewById(R.id.btn_title_right);
		btn.setText("退出");

		btn = (Button)this.findViewById(R.id.btn_reset);
		btn.setOnClickListener(this);

		Button btnLogin = (Button)this.findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);

		btn = (Button) this.findViewById(R.id.login_api_save);
		btn.setOnClickListener(this);

		btn = (Button) this.findViewById(R.id.login_api_scan);
		btn.setOnClickListener(this);

		api = (EditText) this.findViewById(R.id.login_et_api);
		EditText etname = (EditText)this.findViewById(R.id.et_name);
		EditText etpwd = (EditText)this.findViewById(R.id.et_pwd);

		SharedPreferences sp = this.getSharedPreferences("logindata", Context.MODE_PRIVATE);
		mobile = sp.getString("mobile", "");
		pwd = sp.getString("pwd", "");
		etname.setText(mobile);
		etpwd.setText(pwd);
		
		SetNotification();
		
	}

	float y1 = 0;
	float y2 = 0;

	//滑动监听
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//继承了Activity的onTouchEvent方法，直接监听点击事件

		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			//当手指按下的时候
			y1 = event.getY();
		}
		if(event.getAction() == MotionEvent.ACTION_UP) {
			//当手指离开的时候
			y2 = event.getY();
			if(y1 - y2 > 50 && re.getVisibility()==View.VISIBLE) {
				//上滑
				Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.out_to_up);
				re.startAnimation(animation);

				Animation animation1 = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_to_out);
				pingheng.startAnimation(animation1);
				re.setVisibility(View.GONE);

			} else if(y2 - y1 > 50 && re.getVisibility()==View.GONE) {
				//下滑
				createSDCardDir();

				if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
					File sdcardDir =Environment.getExternalStorageDirectory();
					//得到一个路径，内容是sdcard的文件夹路径和名字
					String path=sdcardDir.getPath()+"/webapi/webapi.txt";
					//读取文件
					String apitext = ReadTxtFile(path);

					if(apitext!=null){
						api.setText(apitext);
					}
				}

				re.setVisibility(View.VISIBLE);

				Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.out_to_in);
				re.startAnimation(animation);

			} 
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub
		Button btnExit = (Button)this.findViewById(R.id.btn_title_right);
		btnExit.setOnClickListener(this);
	}

	@Override
	protected void Init(Bundle paramBundle) {
		// TODO Auto-generated method stub

	}

	//创建文件夹与文件
	public void createSDCardDir(){
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir =Environment.getExternalStorageDirectory();
			//得到一个路径，内容是sdcard的文件夹路径和名字
			String path=sdcardDir.getPath()+"/webapi";
			String pathname = path + "/webapi"+".txt";
			//创建文件夹
			File path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();  
			}
			//创建文件
			File dir = new File(pathname);
			if (!dir.exists()) {  
				try {  
					//在指定的文件夹中创建文件  
					dir.createNewFile();  
				} catch (Exception e) {  
				}  
			}  
		}
	}

	//读取文本文件中的内容
	public static String ReadTxtFile(String strFilePath)
	{
		String path = strFilePath;
		String content = ""; //文件内容字符串
		//打开文件
		File file = new File(path);
		//如果path是传递过来的参数，可以做一个非目录的判断
		if (file.isDirectory()){
			Log.d("TestFile", "The File doesn't not exist.");
		}
		else{
			try {
				InputStream instream = new FileInputStream(file); 
				if (instream != null){
					InputStreamReader inputreader = new InputStreamReader(instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					content = buffreader.readLine();
					instream.close();
				}
			}
			catch (IOException e){
				Log.d("TestFile", e.getMessage());
			}
		}
		return content;
	}

	/** 
	 * 写， 读sdcard目录上的文件，要用FileOutputStream， 不能用openFileOutput 
	 * 不同点：openFileOutput是在raw里编译过的，FileOutputStream是任何文件都可以 
	 * @param fileName 
	 * @param message 
	 */  
	// 写在/mnt/sdcard/目录下面的文件  
	public void writeFileSdcard(String fileName, String message) {  
		try {  
			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);  
			FileOutputStream fout = new FileOutputStream(fileName);  
			byte[] bytes = message.getBytes();  
			fout.write(bytes);  
			fout.close();  
		}  
		catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case 1:
			if (data != null) {
				String result = data.getStringExtra("result");
				if(result !=null){
					api.setText(result);
				}
			}
			break;
		}
	}

	@Override
	protected void AppInit() {
		// TODO Auto-generated method stub
		
	}
	
	private void SetNotification(){
		//判断是否打开了监听权限
		String string = Settings.Secure.getString(getContentResolver(),
				"enabled_notification_listeners");
		SharedPreferences sp = LoginActivity.this.getSharedPreferences("logindata", Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = sp.edit();
		if (!string.contains(NotificationMonitorService.class.getName())) {
			String flagPower = sp.getString("flagPower", null);
			if(flagPower!=null){
				return;
			}
			
//			弹窗询问是否需要设置，是则跳转界面，否则提示失败
			AlertDialog.Builder builder=new Builder(LoginActivity.this);
			//2所有builder设置一些参数
			builder.setTitle("提示");
			builder.setMessage("是否配置监听通知栏消息权限");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					editor.putString("flagPower", "Yes");
					editor.commit();
					startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
				}
			});
			builder.setNeutralButton("取消",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					editor.putString("flagPower", "No");
					editor.commit();
				}
			});
			builder.create().show();
			
		}
	}
	
}
