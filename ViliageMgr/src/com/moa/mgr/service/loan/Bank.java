package com.moa.mgr.service.loan;

import java.io.File;
import java.io.Serializable;

import com.moa.mgr.WebConfig4Mgr;
import com.moa.mgr.common.Utils;

/**
 * 金融机构实体	
 * @author alps
 */
public class Bank implements Serializable {
	private static final long serialVersionUID = -8778345769570640334L;
	
	public final int bankId;
	
	public String bankName = "";
	
	public String bankIcon = "";
	
	public String bankDesc = "";
	
	public final transient int seq;
	
	/**
	 * 1：银行
	 * 2：担保公司
	 */
	public int type = 1;
	
	public Bank(int id, String name, String icon, String desc, int seq) {
		bankId = id;
		bankName = name;
		bankIcon = icon;
		bankDesc = desc;
		this.seq = seq;
	}
	
	public Bank(int id, String name, int type, String icon, String desc, int seq) {
		this(id, name, icon, desc, seq);
		this.type = type;
	}
	
	public void deleteIcon() {
		if (Utils.isEmpty(bankIcon)) {
			return;
		}
		Utils.deleteFileByName(WebConfig4Mgr.rootDir + File.separator + bankIcon);
	}
	
	@Override
	public String toString() {
		return "[" + bankId + ", " + bankName + "]";
	}
}
