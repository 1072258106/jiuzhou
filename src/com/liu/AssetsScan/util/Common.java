package com.liu.AssetsScan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
/*
import com.fz.ilucky.InputMdnActivity;
import com.fz.ilucky.LuckyApplication;
import com.fz.ilucky.SmsActivity;
*/

public class Common {
	public static boolean mobileMumVerify(String phoneNum) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(170)|(18[0,5-9])|(14[5,7]))\\d{8}$");
		return p.matcher(phoneNum).matches();

	}

	public static boolean mailAddressVerify(String mailAddress) {
		String emailExp = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(emailExp);
		return p.matcher(mailAddress).matches();
	}
	public static String GetCache(Context context, String strKey) {
		if (context != null && !strKey.equals("")) {
			try {
				return context.getSharedPreferences("fz99", 0).getString(
						strKey, "");
			} catch (Exception localException) {
				return "";
			}
		}
		return "";
	}


	public static int GetNetworkInfo(Context context) {
		int i = 0;
		ConnectivityManager localConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo localNetworkInfo;

		if (localConnectivityManager != null) {
			localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
			if (localNetworkInfo != null) {
				String str1 = localNetworkInfo.getTypeName();
				if (str1.toUpperCase().equals("MOBILE")) {
					String str2 = localNetworkInfo.getExtraInfo();
					if (str2.toUpperCase().equals("CMWAP")) {
						i = 1;
					} else {
						i = 2;
					}
				} else if (str1.toUpperCase().equals("WIFI")) {
					i = 3;
				}

				if (localNetworkInfo.getState() != NetworkInfo.State.CONNECTED) {
					NetworkInfo.State localState1 = localNetworkInfo.getState();
					NetworkInfo.State localState2 = NetworkInfo.State.CONNECTING;
					if ((localState1 == localState2) && (i != 1))
						i = 0;
				}
			}
		}
		//LuckyApplication.NetworkMode = i;
		return i;
	}


	public static boolean RemoveCache(Context context, String strKey) {
		if (context != null && !strKey.equals("")) {
			try {
				SharedPreferences.Editor editor = context.getSharedPreferences(
						"fz99", 0).edit();
				editor.remove(strKey);
				editor.commit();
				return true;
			} catch (Exception localException) {
			}
		}
		return false;
	}

	public static boolean SetCache(Context context, String strKey,
			String strValue) {
		if (context != null && !strKey.equals("")) {
			try {
				SharedPreferences.Editor editor = context.getSharedPreferences(
						"fz99", 0).edit();
				editor.putString(strKey, strValue);
				editor.commit();
				return true;
			} catch (Exception localException) {
			}
		}
		return false;
	}


	public static void ShowInfo(Context context, String showmsg) {
		ShowInfo(context, showmsg, "0");
	}

	public static void ShowInfo(Context context, String showmsg, String type) {
		if (type.equals("1")) {
			Toast.makeText(context, showmsg, 1).show();
			return;
		}
		Toast.makeText(context, showmsg, 0).show();
	}
	
	

	private static Bundle getBundleWithParma(String url) {
		Bundle localBundle = new Bundle();
		try {
			String[] arrayOfString = url.split("\\?")[1].split("=");
			localBundle.putInt(arrayOfString[0],
					Integer.parseInt(arrayOfString[1]));
			return localBundle;
		} catch (Exception localException) {
			Log.e("iws", "getBundleWithParma e:" + localException);
		}
		return localBundle;
	}

	

	public static Matcher getMatcher(String str1, String str2) {
		return Pattern.compile(str2).matcher(str1);
	}

	public static void toActivity(Context context, Class<?> cls, Bundle bundle) {
		Intent localIntent = new Intent(context, cls);
		if (bundle != null)
			localIntent.putExtras(bundle);
		context.startActivity(localIntent);
	}

	public static int dip2px(Context context, float dpValue) {
		return (int) (0.5F + dpValue
				* context.getResources().getDisplayMetrics().density);
	}
	
	public static boolean isMDN(String mobiles) {
	    String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。  
	    if (TextUtils.isEmpty(mobiles)) return false;  
	    else return mobiles.matches(telRegex);  
	    
//	    Pattern p = Pattern.compile("^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$");
//	    Matcher m = p.matcher(mobiles);
//	    return m.matches();
	    
	    
	}
	
}
