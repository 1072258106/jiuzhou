package com.liu.AssetsScan.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;

//SharedPreferences存储list
public class ListDataSave {  
    private SharedPreferences preferences;  
    private SharedPreferences.Editor editor;  
  
    public SharedPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public SharedPreferences.Editor getEditor() {
		return editor;
	}

	public void setEditor(SharedPreferences.Editor editor) {
		this.editor = editor;
	}

	public ListDataSave(Context mContext, String preferenceName) {  
        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);  
        editor = preferences.edit();  
    }  
  
    /** 
     * 保存List 
     * @param tag 
     * @param datalist 
     */  
    public <T> void setDataList(String tag, List<T> datalist) { 
    	//本资产管理系统不需要此处判空
//        if (null == datalist || datalist.size() <= 0)  
//            return;  
  
        Gson gson = new Gson();  
        //转换成json数据，再保存  
        String strJson = gson.toJson(datalist);  
        editor.clear();  
        editor.putString(tag, strJson);  
        editor.commit();  
  
    }  
  
    /** 
     * 获取List 
     * @param tag 
     * @return 
     */  
    public <T> List<T> getDataList(String tag) {  
        List<T> datalist=new ArrayList<T>();  
        String strJson = preferences.getString(tag, null);  
        if (null == strJson) {  
            return datalist;  
        }  
        Gson gson = new Gson();  
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {  
        }.getType());  
        return datalist;  
  
    } 
    
}  
