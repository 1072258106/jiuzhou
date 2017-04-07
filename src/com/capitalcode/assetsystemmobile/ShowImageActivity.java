package com.capitalcode.assetsystemmobile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowImageActivity extends BaseActivity {

	static public String base64;
	
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

	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	@Override
	protected void ViewInit() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_showimage);
		
        Button btn = (Button)this.findViewById(R.id.btn_title_left);
        btn.setVisibility(View.GONE);
        
        btn = (Button)this.findViewById(R.id.btn_title_right);
        btn.setVisibility(View.GONE);
        
        TextView tv = (TextView)this.findViewById(R.id.tv_title_name);
        tv.setText("图片浏览");
        
        ImageView iv = (ImageView)this.findViewById(R.id.image);
        
		Bitmap bitmap = null;

		byte[] imgByte = null;
		InputStream input = null;
		try
		{
			   imgByte = Base64.decode(base64, Base64.DEFAULT);
			   input = new ByteArrayInputStream(imgByte);
			   
			   
			   BitmapFactory.Options options = new BitmapFactory.Options();
			   options.inJustDecodeBounds = true;
			   BitmapFactory.decodeStream(input, null, options);
			 
			    // 计算 inSampleSize 的值
			   Log.e("width,height",getWindowManager().getDefaultDisplay().getWidth()+"   "+getWindowManager().getDefaultDisplay().getHeight());
			   
			   options.inSampleSize = calculateInSampleSize(options, getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());
			   Log.e("inSampleSizeinSampleSizeinSampleSizeinSampleSize",options.inSampleSize+"");
			   options.inJustDecodeBounds = false;
		       SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(new ByteArrayInputStream(imgByte), null, options));
		       bitmap = (Bitmap)softRef.get();
    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(imgByte!=null)
			{
				imgByte = null;
			}

			if(input!=null)
			{
				try 
				{
					input.close();
				} 
			
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		iv.setImageBitmap(bitmap);        
        
        
	}

	@Override
	protected void ViewListen() {
		// TODO Auto-generated method stub

	}

}
