package com.liu.AssetsScan.util;

import java.text.SimpleDateFormat;
import java.util.Date;

//获取当前时间
public class CurrentTime {

	public String getDate(){
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd"); 
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间 
		String str = formatter.format(curDate);
		return str;
	}
}

