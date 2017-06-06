package com.moa.mgr.service.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.moa.mgr.service.form.FormDefLoader;

public class SummaryReportData implements Serializable {

	private static final long serialVersionUID = -5921072296668071929L;
	
	public String province = "";
	
	public long authNum = 0;
	
	/** 当前省份总农场数量 */
	public long subTotal;
	/** 当前省份认证占比 */
//	public String authRate = "0";
	
	public double authRate = 0;
	
	/** 当前省份信息采集农场数量 */
	public long submitCount = 0;
	
	public double submitRate = 0.0;
	
	/**
	 * 记录每个表单提交的人次
	 */
	public Map<Integer, Long> eachCounts = new LinkedHashMap<Integer, Long>();
	
	
	
	public SummaryReportData(String province) {
		this.province = province;
		for (Map<String, Object> desc : FormDefLoader.getInstance().simpleFormList) {
			eachCounts.put((Integer) desc.get("id"), 0L);
		}
	}
	
	public void updateAuthTotalAndAuthRate(long total) {
		this.subTotal = total;
		if (subTotal != 0) {
			double tmpRate = (double) authNum / (double) subTotal;
			authRate = new BigDecimal(tmpRate * 100).setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}
	
	public void updateSubmitCountAndSubmitRate(long count) {
		submitCount = count;
		if (subTotal != 0) {
			double tmp = (double)submitCount / (double)subTotal;
			submitRate = new BigDecimal(tmp * 100).setScale(2,  BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}
	
	public void putCountData(int formId, long count) {
		eachCounts.put(formId, count);
	}
	
	public void addCountByFormId(int formId, long count) {
		long oriCount = eachCounts.get(formId) == null ? 0 : eachCounts.get(formId);
		eachCounts.put(formId, oriCount + count);
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("*****************" + province + "**********************\n");
		sb.append("农场总量:" + subTotal + ", 认证总数:" + authNum + ", 认证比例: " + authRate + "%,  直报信息采集总人数:" + submitCount + ", 采集比例:" + submitRate + "%\n");
		
		Iterator<Entry<Integer, Long>> iterator = eachCounts.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, Long> entry = iterator.next();
			sb.append("  form" + entry.getKey() + ": " + entry.getValue() + " # ");
		}
		
		return sb.toString();
	}

}
