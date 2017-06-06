package com.moa.mgr.service;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.WebConfig4Mgr;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;
import com.moa.mgr.service.report.SummaryReportData;

public class ReportService {

	private static final String TAG = "ReportService";
	
	public static final String TAG_TOTAL = "汇总";
	
	public static final String TAG_OTHER = "其它";

//	private static final String[] PROVINCES = new String[]{
//		"北京市","天津市","河北省","山西省","内蒙古",
//		"辽宁省","吉林省","黑龙江省","上海市","江苏省",
//		"浙江省","安徽省","福建省","江西省","山东省",
//		"河南省","湖北省","湖南省","广东省","广西壮族",
//		"海南省","重庆","四川省","贵州省","云南省","西藏",
//		"陕西省","甘肃省","青海省","宁夏","新疆","台湾省",
//		"香港","澳门","海外"};
	
	
	private static final String[] PROVINCES = new String[]{
		"湖北省", TAG_TOTAL};

	
	public static ResultInfo<Collection<SummaryReportData>> summaryReport(Controller controller) {
		//用户权限控制
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		Map<String, SummaryReportData> cache = new LinkedHashMap<String, SummaryReportData>();
		for (String province : PROVINCES) {
			cache.put(province, new SummaryReportData(province));
		}
		
		SummaryReportData totalData = cache.get(TAG_TOTAL);
		//省份，农场总数，认证农场数，未认证农场数，并根据以上数值计算出认证比例
		List<Record> authRecordList = Db.find("select province, sum(is_authed) as authed, count(1) as total from alipay_user GROUP BY province order by province");
		for (Record record : authRecordList) {
			String province = record.getStr("province");
			SummaryReportData data = cache.get(province);
			if (data == null) {
				data = new SummaryReportData(province);
				cache.put(province, data);
			} 
			if (record.get("authed") == null) {
				data.authNum = 0;
			} else {
				data.authNum = record.getDouble("authed").longValue();
			}
			
			data.updateAuthTotalAndAuthRate(record.getLong("total"));
			
			totalData.authNum += data.authNum;
			totalData.subTotal += data.subTotal;
		}
		
		String sql2 = "select count(distinct form.alipay_user_id) count, IFNULL(farmer.province,'其它') as province from form_data form" +
				" left join alipay_user farmer on farmer.alipay_user_id = form.alipay_user_id " +
				" group by farmer.province";
		
		List<Record> submitTotalRecordList = Db.find(sql2);
		for (Record record : submitTotalRecordList) {
			String province = record.getStr("province");
			SummaryReportData data = cache.get(province);
			if (data == null) {
				data = new SummaryReportData(province);
				cache.put(province, data);
			}
			data.submitCount = record.getLong("count");
			if (TAG_OTHER.equals(province)) {
				data.updateAuthTotalAndAuthRate(data.subTotal + data.submitCount);
				totalData.updateAuthTotalAndAuthRate(totalData.subTotal + data.submitCount);
			}
			data.updateSubmitCountAndSubmitRate(data.submitCount);
			totalData.submitCount += data.submitCount;
		}
		totalData.updateAuthTotalAndAuthRate(totalData.subTotal);
		totalData.updateSubmitCountAndSubmitRate(totalData.submitCount);
		
		//统计 各省份 直报信息 提交总人数 
		//如张三为湖北人，提交了表单一3次，表单二5次，表单三3次，则计为 湖北省表单提交总人数为1
		List<Record> eachFormCountRecordList = Db.find("select count(form_id) count, form_id, province from v_form_data2 group by province, form_id order by province");
		for (Record record : eachFormCountRecordList) {
			String province = record.getStr("province");
			int formId = record.getInt("form_id");
			long count = record.getLong("count");
			SummaryReportData data = cache.get(province);
			if (data == null) {
				data = new SummaryReportData(province);
				cache.put(province, data);
			}
			data.addCountByFormId(formId, count);
			totalData.addCountByFormId(formId, count);
		}
		
		SummaryReportData otherData = cache.remove(TAG_OTHER);
		if (otherData != null) {
			cache.put(TAG_OTHER, otherData);
		}
		cache.remove(TAG_TOTAL);
		cache.put(TAG_TOTAL, totalData);
		if (WebConfig4Mgr.isDebug) {
			Iterator iterator = cache.values().iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next() + "\n\n");
			}
		}
		//判断是否是区域管理员，对区域管理员用户进行区域过滤
		if(optMgr.type==4){
			Record provinceRecord=Db.findFirst("select region_name as province from region where region_id =?", optMgr.regionId);
			String province=provinceRecord.get("province");
			SummaryReportData data = cache.get(province);
			cache.clear();
			cache.put(province, data);

			return new ResultInfo<Collection<SummaryReportData>>(ResultCodes.RET_SUCCESS, "ok",cache.values()); 

		}
		
		return new ResultInfo<Collection<SummaryReportData>>(ResultCodes.RET_SUCCESS, "ok", cache.values());
	}
	
	
	private static void saveAsExl(Collection<SummaryReportData> datas) throws Exception {
		WritableWorkbook workbook = Workbook.createWorkbook(new File("d:" + File.separator + "test.xls")) ;
		WritableSheet  sheet = workbook.createSheet("报表", 0);
		sheet.addCell(new Label(0, 0, "省份"));
		sheet.addCell(new Label(1, 0, "总家庭农场数量"));
		sheet.addCell(new Label(2, 0, "家庭农场认证总人数"));
		sheet.addCell(new Label(3, 0, "认证比例"));
		sheet.addCell(new Label(4, 0, "直报系统信息采集总人数"));
		sheet.addCell(new Label(5, 0, "采集比例"));
		sheet.addCell(new Label(6, 0, "家庭基本信息采集总人数"));
		sheet.addCell(new Label(7, 0, "土地信息采集总人数"));
		sheet.addCell(new Label(8, 0, "生产经营信息（种植户填写）采集总人数"));
		sheet.addCell(new Label(9, 0, "生产经营信息（养植户填写）采集总人数"));
		sheet.addCell(new Label(10, 0, "农具信息采集总人数"));
		sheet.addCell(new Label(11, 0, "雇员信息采集总人数"));
		sheet.addCell(new Label(12, 0, "保险信息采集总人数"));
		sheet.addCell(new Label(13, 0, "贷款信息采集总人数"));
		sheet.addCell(new Label(14, 0, "补贴款信息采集总人数"));
		sheet.addCell(new Label(15, 0, "培训信息采集总人数"));
		
		int rowIndex = 1;
		for (SummaryReportData data : datas) {
			sheet.addCell(new Label(0, rowIndex, data.province));
			sheet.addCell(new Number(1, rowIndex, data.subTotal));
			sheet.addCell(new Number(2, rowIndex, data.authNum));
			sheet.addCell(new Number(3, rowIndex, data.authRate));
			
			sheet.addCell(new Number(4, rowIndex, data.submitCount));
			sheet.addCell(new Number(5, rowIndex, data.submitRate));
			
			for (int i = 1; i < 10; i++) {
				Long c = data.eachCounts.get(i);
				if (c != null) {
					sheet.addCell(new Number(i + 5, rowIndex, c));
				} else {
					sheet.addCell(new Number(i + 5, rowIndex, 0));
				}
			}
			
			rowIndex++;
		}
		
		workbook.write();
		workbook.close();
	}
}
