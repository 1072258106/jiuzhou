package com.zhy.bean;

import java.util.List;

import com.liu.AssetsScan.model.CategoryModel;
import com.liu.AssetsScan.model.DeptModel;
import com.liu.AssetsScan.model.UserInfoModel;

public class FileBean
{
//	@TreeNodeId
	public int id;
//	@TreeNodePid
	public int parentId;
//	@TreeNodeLabel
	public String name;
	
	public boolean isChild;
	
	public List<DeptModel> submodel;
	
	public List<UserInfoModel> info;
	
	public List<CategoryModel> subCategory;
	
	private long length;
	private String desc;

	public FileBean(int id, int parentId, String name,boolean isChild,List<DeptModel> submodel,List<UserInfoModel> info)
	{
		super();
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.isChild = isChild;
		this.submodel = submodel;
		this.info = info;
	}

	public FileBean(int id, int parentId, String name,boolean isChild,List<CategoryModel> subCategory)
	{
		super();
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.isChild = isChild;
		this.subCategory = subCategory;
	}
	
}
