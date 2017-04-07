package com.liu.AssetsScan.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SerializableList implements Serializable{
	
    private List<Object> list;
    
    public List<Object> getList() {
        return list;
    }
 
    public void setList(List<Object> list) {
        this.list = list;
    }

}
