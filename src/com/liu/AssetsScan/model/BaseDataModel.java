package com.liu.AssetsScan.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDataModel{
	  public  Map<String,String> PageLabel = new HashMap<String,String>();
	  public  String AssetName;
	  public  String Standard;
	  public  List<CategoryModel> Category;
	  
	  
	  public  Map<String,Map<String,String>> mapType = new HashMap<String,Map<String,String>>();
	  
	  public  Map<String,List<SupplierModel>> mapList = new HashMap<String,List<SupplierModel>>();
	  
	  public  Map<String,String>  StockLabel = new HashMap<String,String>();
	  
	  public  Map<String,String> AssetStockState = new HashMap<String,String>();
	  
	  public  Map<String,String> StockState = new HashMap<String,String>();
	  
	  public  Map<String,String> TimeType = new HashMap<String,String>();
	  
}
