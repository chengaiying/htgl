package com.moa.mgr.service.insurer;

import java.io.File;
import java.io.Serializable;

import com.moa.mgr.WebConfig4Mgr;
import com.moa.mgr.common.Utils;
/*
 * 保险公司实体类
 */
public class Insurer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    public final int insurerId;
	
	public String insurerName = "";
	
	public String insurerIcon = "";
	
	public String insurerDesc = "";
	
	
	public Insurer(int id, String name, String icon, String desc) {
		insurerId = id;
		insurerName = name;
		insurerIcon = icon;
		insurerDesc = desc;
	}
	
	
	public static void deleteIcon(String insurer_Icon) {
		
		Utils.deleteFileByName(WebConfig4Mgr.rootDir + File.separator + insurer_Icon);
	}
	
	@Override
	public String toString() {
		return "[" + insurerId + ", " + insurerName + "]";
	}

}
