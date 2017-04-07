package com.liu.AssetsScan.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsAssetModel {
	
	public List<Map<String, String>> ImgGuid;
	public String StockMemo = "";		//盘点说明
	public String addressName = "";		//存放地点
	public String adminDeptName = "";	//管理部门
	public String assetCode = "";	//资产编码
	public String assetId = "";		//资产id
	public String assetName = "";	//资产名称
	public String assetOldCode = "";	//旧资产编码
	public String assetStatusName = "";	//资产状态
	public String brand = "";	//品牌
	public String drawDT = "";	//购置日期
	public String inventoryStateName = "";	//盘点状态
	
	public HashMap<String, Object> map;
	public String measureUnit = "";	//计量单位
	public String modelno = "";	//规格型号
	public String onWorth = "";	//资产原值
	public String str9 = "";	//序列号
	public String useDeptName = "";	//使用部门
	public String useUserName = "";	//使用人员
}
