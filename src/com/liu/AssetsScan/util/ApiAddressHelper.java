package com.liu.AssetsScan.util;



public class ApiAddressHelper
 {

	public static String SERVERHOST = "http://58.134.112.6";

	//private static final String SERVERHOST = "http://www.fuzhi99.com/";

	public static String getBaseDataUrl(){
		return urlWhitParam("/loginservice/getstockbasedatainfo");
	}
	public static String getLotterysUrl(){
		return urlWhitParam("user/getLotterys");
	}
	public static String getRecommendUrl(){
		return urlWhitParam("user/recommend");
	}
	
	public static String getLoginUrl(){
		
		String url = urlWhitParam("/loginservice/getlogindatainfo");
		return urlWhitParam("/loginservice/getloginforstockdatainfo");
	}
	
	public static String getLatestVersion(){
		return urlWhitParam("/loginservice/getsystemversion");
	}
	
	public static String changepwd(){
		return urlWhitParam("/loginservice/editpwd");
	}
	
	public static String getOnLineBatch()
	{
		return urlWhitParam("/stockservice/getstockbatch");
	}
	
	public static String getCheckBatch(){
		return urlWhitParam("/stockservice/getpersoncheckstockbatch");
	}
	
	public static String downloadBatchDetail()
	{
		return urlWhitParam("/stockservice/downloadbatchdetail");
	}
	
	public static String downloadpeopleBatchDetail()
	{
		return urlWhitParam("/stockservice/downloadpersonassetinfo");
	}
	
	public static String uploadBatchDetail()
	{
		return urlWhitParam("/stockservice/uploadbatchinfo");
	}
	
	public static String uploadpyinfo()
	{
		return urlWhitParam("/stockservice/uploadpyinfo");
	}
	
	public static String uppeopleloadBatchDetail()
	{
		return urlWhitParam("/stockservice/uploadpersonassetinfo");
	}
	
	
	public static String getStockRightCode()
	{
		return urlWhitParam("/stockservice/getstockrightcode");
	}
	
	public static String getStockAssetObj()
	{
		return urlWhitParam("/stockservice/getstockassetobj");
	}
	
	
	public static String getcanretassetinfo()
	{
		return urlWhitParam("/borrowservice/getcanretassetinfo");
	}
	
	
	public static String getSaveStockAssetObj()
	{
		return urlWhitParam("/stockservice/savestockassetobj");
	}
	
	
	
	public static String getSavePicUrl(String MenuId)
	{
//		if( MenuId.equals("19") || MenuId.equals("310") )
		{
			return urlWhitParam("/assetservice/saveattchimages");
		}
//		return null;
	}
	
	public static String SavePCPicUrl(String MenuId){
//		if(MenuId.equals("0")){
			return urlWhitParam("/loginservice/savesignimage");
//		}
//		return null;
	}
	
	public static String getSavePCPicUrl(String MenuId){
		return urlWhitParam("/loginservice/getsignimage");
	}
	
	public static String getSaveAssetUrl(String MenuId)
	{
		if( MenuId.equals("19") || MenuId.equals("310") )
		{
			return urlWhitParam("/assetservice/saveassetInfo");
		}
		else if( MenuId.equals("29") )
		{
			return urlWhitParam("/applyservice/saveapplyinfo");
		}
		else if( MenuId.equals("39") )
		{
			return urlWhitParam("/moveservice/savemoveinfo");
		}
		else if( MenuId.equals("34") )
		{
			return urlWhitParam("/borrowservice/saveborrowinfo");
		}
		else if( MenuId.equals("48") )
		{
			return urlWhitParam("/repairservice/saverepairinfo");
		}
		else if( MenuId.equals("43") )
		{
			return urlWhitParam("/scrapservice/savescrapinfo");
		}
		else if( MenuId.equals("31") )
		{
			return urlWhitParam("/revertservice/saverevertinfo");
		}
		else if( MenuId.equals("36") )
		{
			return urlWhitParam("/borrowservice/saveborrowretinfo");
		}		
		
		
		return null;
	}
	
	
	public static String finishrepair()
	{
		return urlWhitParam("/repairservice/finishrepair");
	}
	
	
	public static String getSearchUrl(String MenuId){
		
		if( MenuId.equals("19") || MenuId.equals("21") )
		{
			return urlWhitParam("/assetservice/getassetlistinfo");
		}
		else if( MenuId.equals("207") )
		{
			return urlWhitParam("/applyservice/getapplylistinfo");
		}
		else if( MenuId.equals("206") )
		{
			return urlWhitParam("/moveservice/getmovelistinfo");
		}
		else if( MenuId.equals("209") )
		{
			return urlWhitParam("/borrowservice/getborrowlistinfo");
		}
		else if( MenuId.equals("321") )
		{
			return urlWhitParam("/borrowservice/getborrowretlistinfo");
		}	
		else if( MenuId.equals("211") )
		{
			return urlWhitParam("/repairservice/getrepairlistinfo");
		}		
		else if( MenuId.equals("208") )
		{
			return urlWhitParam("/revertservice/getrevertlistinfo");
		}	
		else if( MenuId.equals("210") )
		{
			return urlWhitParam("/scrapservice/getscraplistinfo");
		}			

		return null;
	}
	
	public static String getStatisticsByStatus()
	{
		return urlWhitParam("/reportservice/statisticsbystatus");
	}
	
	public static String getStatisticsByDept()
	{
		return urlWhitParam("/reportservice/statisticsbydept");
	}
	
	public static String getStatisticsByCategory()
	{
		return urlWhitParam("/reportservice/statisticsbycategory");
	}
	
	public static String getStatisticsByStandard()
	{
		return urlWhitParam("/reportservice/statisticsbystandard");
	}
	
	
	
	public static String getStatisticsTrend()
	{
		return urlWhitParam("/reportservice/statisticstrend");
	}
	
	
	public static String getResultUrl(String MenuId){
		
		if( MenuId.equals("19") )
		{
			return urlWhitParam("/assetservice/getassetobjinfo");
		}
		else if( MenuId.equals("21") )
		{
			return urlWhitParam("/assetservice/getassethistoryinfo");
		}
		else if( MenuId.equals("207") )
		{
			return urlWhitParam("/applyservice/getapplyobjinfo");
		}
		else if( MenuId.equals("208") )
		{
			return urlWhitParam("/revertservice/getrevertobjinfo");
		}		
		else if( MenuId.equals("206") )
		{
			return urlWhitParam("/moveservice/getmoveobjinfo");
		}		
		else if( MenuId.equals("209") )
		{
			return urlWhitParam("/borrowservice/getborrowobjinfo");
		}		
		else if( MenuId.equals("210") )
		{
			return urlWhitParam("/scrapservice/getscrapobjinfo");
		}	
		else if( MenuId.equals("211") )
		{
			return urlWhitParam("/repairservice/getrepairobjinfo");
		}
		
		
		
		
		return null;
	}	
	
	public static String getAssetformodel()
	{
		return urlWhitParam("/assetservice/getassetformodel");
	}

	public static String getDelUrl(String MenuId)
	{
		if( MenuId.equals("19") )
		{
			return urlWhitParam("/assetservice/deleteassetinfo");
		}
		else if( MenuId.equals("207") )
		{
			return urlWhitParam("/applyservice/deleteapplyinfo");
		}
		else if( MenuId.equals("208") )
		{
			return urlWhitParam("/revertservice/deleterevertinfo");
		}		
		else if( MenuId.equals("206") )
		{
			return urlWhitParam("/moveservice/deletemoveinfo");
		}		
		else if( MenuId.equals("209") )
		{
			return urlWhitParam("/borrowservice/deleteborrowinfo");
		}		
		else if( MenuId.equals("210") )
		{
			return urlWhitParam("/scrapservice/deletescrapinfo");
		}	
		else if( MenuId.equals("211") )
		{
			return urlWhitParam("/repairservice/deleterepairinfo");
		}		

		
		
		return null;
		
	}
	
	public static String getStockAssetList()
	{
		return urlWhitParam("/stockservice/getstockassetlist");
	}
	
	
	
	
	
	
	
	public static String getSendSMSUrl(){
		return urlWhitParam("sms/send");
	}
	
	public static String getSendVerifyUrl(){
		return urlWhitParam("sms/verify");
	}
	public static String getodeExchangeUrl(){
		return urlWhitParam("user/codeExchange");
	}
	public static String getSettingPwd(){
		return urlWhitParam("user/setPassword");
	}
	public static String getChangePwd(){
		return urlWhitParam("user/changePassword");
	}	
	public static String getApplyCashUrl(){
		return urlWhitParam("user/applyCash");
	}
	private static String urlWhitParam(String paramString) {
		return SERVERHOST + paramString ;
	}

	public static String getConstructPackage(){
		return urlWhitParam("luckybag/pack");
	}
	
	public static String getReceivedPackageUrl(){
		return urlWhitParam("luckybag/recvbags");
	}
	
	public static String getSentPackageUrl(){
		return urlWhitParam("luckybag/sentbags");
	}
	
	public static String getSharePackageUrl(){
		return urlWhitParam("luckybag/url");
	}
	
	public static String saveDepartCheck(String MenuId){
		return urlWhitParam("scrapservice/uploadbatchinfo");
	}
}
