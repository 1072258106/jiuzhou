package com.capitalcode.assetsystemmobile;

import com.igexin.sdk.PushManager;
import com.liu.AssetsScan.util.StaticUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//是否只对当前 cid 有效，如果是 true，只对当前cid做解绑；如果是 false，对所有绑定该别名的cid列表做解绑
		PushManager.getInstance().unBindAlias(context, StaticUtil.LoginName, true);
		Log.i("解绑的名字",  StaticUtil.LoginName);
	}

}
