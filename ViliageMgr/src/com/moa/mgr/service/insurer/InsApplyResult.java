package com.moa.mgr.service.insurer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InsApplyResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557006948190336647L;
	
    public int page;
	
	public long totalPage;
	
	public List<Map<String, Object>> insApplies = new ArrayList<Map<String, Object>>();

}
