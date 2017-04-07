package com.capitalcode.assetsystemmobile;

import org.apache.commons.lang3.StringUtils;

import com.liu.AssetsScan.util.DatabaseHelper;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DemoApplication extends Application {

    private static final String TAG = "GetuiSdkDemo";

    private static DemoHandler handler;
    
    public static GetuiActivity getuiActivity;
    private static Context context;
    
    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "DemoApplication onCreate");

        context = this;
        
        if (handler == null) {
            handler = new DemoHandler();
        }
    }

    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public static class DemoHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	payloadData.append((String) msg.obj);
    		payloadData.append("\n");
    		String data = (String) msg.obj;
        	switch (msg.what) {
        	case 0:
        		if(data!=null){
        			setData(data);
        			Intent intent = new Intent();
        			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			intent.setClass(context,GetuiActivity.class);
        			context.startActivity(intent);
        		}
        		break;
        	}
        }
        
        private void setData(String data) {
    		// TODO Auto-generated method stub
        	Log.i("测试用", "setData(String "+data+")");
    		String zzlb = StringUtils.substringBetween(data, "，", "：").trim();
    		String zzbh = StringUtils.substringAfterLast(data, "：").trim();
    		String user = StringUtils.substringBefore(data, "您").trim();
    		if(zzlb!=null){
    			if(zzlb.equals("部门盘点")||zzlb.equals("个人核查")){
    				ContentValues values = new ContentValues();
    				values.put("zzlb", zzlb);
    				values.put("zzbh", zzbh);
    				values.put("message", data);
    				values.put("zzflag", "未读");
    				values.put("user", user);
    				DatabaseHelper helper = new DatabaseHelper(context, "MessageTwo.db");
    				SQLiteDatabase database = helper.getWritableDatabase();
    				database.insert("MessageTable", null, values);
    			}else{
//    				ToastUtil.showToast(context, "传递的通知格式不正确");
    			}
    		}
    	}
    }
    
}
