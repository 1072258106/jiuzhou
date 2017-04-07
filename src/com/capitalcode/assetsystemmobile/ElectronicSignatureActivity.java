package com.capitalcode.assetsystemmobile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.liu.AssetsScan.async.CallEarliest;
import com.liu.AssetsScan.async.Callable;
import com.liu.AssetsScan.async.Callback;
import com.liu.AssetsScan.model.AssetSaveRetModel;
import com.liu.AssetsScan.model.RequestRetModel;
import com.liu.AssetsScan.util.ApiAddressHelper;
import com.liu.AssetsScan.util.Common;
import com.liu.AssetsScan.util.HttpHelper;
import com.write.WriteDialogListener;
import com.write.WritePadDialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//电子签名
public class ElectronicSignatureActivity extends BaseActivity {
	private static final int NONE = 1000;
	private static final int PHOTO_GRAPH = 1001; // 拍照
	private static final int PHOTO_ZOOM = 1002; // 缩放
	private static final int PHOTO_RESOULT = 1003; // 结果
	private static final String IMAGE_UNSPECIFIED = "image/*";


	private ImageView mIVSign;
	private Button mBTSign;
	private Button mPic;
	private Button save;
	private Bitmap mSignBitmap;

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
		setContentView(R.layout.activity_electronic_signature);

		TextView tv = (TextView) this.findViewById(R.id.tv_title_name);
		tv.setText("电子签名");

		Button btn = (Button) this.findViewById(R.id.btn_title_left);
		btn.setVisibility(View.GONE);

		btn = (Button) this.findViewById(R.id.btn_title_right);
		btn.setVisibility(View.GONE);

		mIVSign = (ImageView) findViewById(R.id.iv_autograph);
		mBTSign = (Button) findViewById(R.id.btn_search_write);

		//手写签名
		mBTSign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				WritePadDialog mWritePadDialog = new WritePadDialog(
						ElectronicSignatureActivity.this, new WriteDialogListener() {

							public void onPaintDone(Object object) {
								mSignBitmap = (Bitmap) object;
								createSignFile();
								mIVSign.setImageBitmap(mSignBitmap);
							}
						});

				mWritePadDialog.show();
			}
		});

		mPic = (Button) findViewById(R.id.btn_search_pai);
		mPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(),"photo.jpg")));
				startActivityForResult(intent, PHOTO_GRAPH);
			}
		});

		save = (Button) findViewById(R.id.btn_search_save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mSignBitmap==null){
					saveImage();
				}else {
					dialog();
				}
			}
		});

		ElectronicSignatureActivity.this.getImage();

	}

	//对话框
	protected void dialog() {
		AlertDialog.Builder builder=new Builder(ElectronicSignatureActivity.this);
		//2所有builder设置一些参数
		builder.setTitle("提示");
		builder.setMessage("签名已存在,是否覆盖该数据");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				saveImage();
			}
		});
		builder.setNeutralButton("取消",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}


	//保存签名图片
	private void saveImage(){
		Map<String, String> listImgGuid = new HashMap<String, String>();
		//将图片转化为base64
		if (mSignBitmap != null) {
			String base64 = bitmapToBase64(mSignBitmap, 70);
			listImgGuid.put("ImageData",base64);
		}
		//初始化param
		ElectronicSignatureActivity.this.Prepare(param);
		param.put("MenuId", ""+0);
		if (listImgGuid.size() > 0) {
			param.put("ImgGuid", listImgGuid);
		}
		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {

				m_pDialog = new ProgressDialog(ElectronicSignatureActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在保存图片数据...");
				m_pDialog.setCancelable(false);
				m_pDialog.show();

			}
		}, new Callable<String>() {
			public String call() throws Exception {
				return HttpHelper.getInstance(context).Post(ApiAddressHelper.SavePCPicUrl(MenuId),
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
					RequestRetModel<AssetSaveRetModel> model = gson.fromJson(res,
							new TypeToken<RequestRetModel<AssetSaveRetModel>>() {
					}.getType());
					if (model != null) {
						m_pDialog.hide();

						String ErrorMsg = model.response.ErrorMsg;
						if (ErrorMsg != null && ErrorMsg.length() > 0) {
							Common.ShowInfo(context, ErrorMsg);
						}

					} else {
						m_pDialog.hide();
						Common.ShowInfo(context, "网络异常");
					}
				} catch (JsonSyntaxException localJsonSyntaxException) {
					m_pDialog.hide();

				}
			}
		});
	}


	//创建签名文件
	private void createSignFile() {
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;
		String path = null;  
		File file = null;
		try {  
			path = Environment.getExternalStorageDirectory()
					+ "/write.jpg"; 
			file = new File(path);
			fos = new FileOutputStream(file);
			baos = new ByteArrayOutputStream();
			//如果设置成Bitmap.compress(CompressFormat.JPEG, 100, fos) 图片的背景都是黑色的

			mSignBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);  
			byte[] b = baos.toByteArray();  
			if (b != null) {  
				fos.write(b); 
			}
			//关闭流
			fos.close();
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {  
			try {  
				if (fos != null) {
					fos.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}  
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTO_GRAPH) {
			// 设置文件保存路径
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/photo.jpg");
			startPhotoZoom(Uri.fromFile(picture));
		}

		if (data == null)
			return;

		// 读取相册缩放图片
		if (requestCode == PHOTO_ZOOM) {
			startPhotoZoom(data.getData());
		}
		// 处理结果
		if (requestCode == PHOTO_RESOULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				mSignBitmap = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
				//此处可以把Bitmap保存到sd卡中，具体请看：http://www.cnblogs.com/linjiqin/archive/2011/12/28/2304940.html
				mIVSign.setImageBitmap(mSignBitmap); //把图片显示在ImageView控件上
			}

		}

//		super.onActivityResult(requestCode, resultCode, data);
	}


	/**
	 * 收缩图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_RESOULT);
	}

	//首次进入个人中心界面时，获取签名图片，存在则显示，不存在则不显示

	//获取已经被保存的签名图片
	private void getImage(){
		Prepare(param);
		param.put("MenuId", 0);

		doAsync(new CallEarliest<String>() {
			public void onCallEarliest() throws Exception {

				m_pDialog = new ProgressDialog(ElectronicSignatureActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				m_pDialog.setMessage("正在获取数据...");
				m_pDialog.setCancelable(false);
				m_pDialog.show();

			}
		}, new Callable<String>() {
			public String call() throws Exception {
				return HttpHelper.getInstance(context).Post(ApiAddressHelper.getSavePCPicUrl(MenuId), param);
			}
		}, new Callback<String>() {
			public void onCallback(String res) {

				if (res.equals("")) {
					m_pDialog.hide();
					Common.ShowInfo(context, "网络故障");
					return;
				}

				try {
					JSONObject respJson = new JSONObject(res);

					JSONObject response = respJson.getJSONObject("response");

					String ErrorCode = response.getString("ErrorCode");
					if (ErrorCode.equals("S00000") == false) {
						m_pDialog.hide();
						//						String ErrorMsg = response.getString("ErrorMsg");
						//							Common.ShowInfo(context, ErrorMsg);
						return;
					}

					JSONObject rspcontent = respJson.getJSONObject("rspcontent");

					String base64 = rspcontent.get("ImageData").toString();

					if(base64!=null){
						mSignBitmap=null;
						try {
							byte[]bitmapArray;
							bitmapArray=Base64.decode(base64, Base64.DEFAULT);
							mSignBitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
						} catch (Exception e) {
							e.printStackTrace();
						}
						mIVSign.setImageBitmap(mSignBitmap);
					}

					m_pDialog.hide();

				} catch (JSONException localJsonSyntaxException) {
					m_pDialog.hide();

				}
			}
		});

	}


	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub

	}

}
