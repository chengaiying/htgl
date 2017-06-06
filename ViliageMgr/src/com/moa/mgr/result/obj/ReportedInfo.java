package com.moa.mgr.result.obj;

public class ReportedInfo {
	public String formId;
	
	public String formName;
	
	public String lastDate;
	
	public int count;
	
	public ReportedInfo(String id, String name, int count, String date) {
		this.formId = id;
		this.formName = name;
		this.count = count;
		this.lastDate = date;
	}
	
}
