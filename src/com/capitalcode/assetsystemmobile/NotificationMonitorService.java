package com.capitalcode.assetsystemmobile;

import android.app.Notification;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * 监听通知栏服务，请合理使用
 * @author liu
 *
 */
public class NotificationMonitorService extends NotificationListenerService {

    // 在收到消息时触发
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
    	Notification notification = sbn.getNotification();
    	String data = (String) notification.tickerText;
    	if(data!=null){
    		Message msg = Message.obtain();
    		msg.what = 1;
    		msg.obj = data;
    		DemoApplication.sendMessage(msg);
    	}
    }

    //在删除消息是触发
	@Override
	public void onNotificationRemoved(StatusBarNotification arg0) {
		// TODO Auto-generated method stub
		
	}
}
