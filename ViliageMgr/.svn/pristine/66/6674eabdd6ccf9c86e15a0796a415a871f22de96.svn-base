package com.moa.mgr.controller;

import java.util.Collection;

import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.service.ReportService;
import com.moa.mgr.service.report.SummaryReportData;

/**
 * 报表控制模块
 * @author zf21100
 *
 */
public class ReportController extends Controller {
	
	private static final String TAG = "ReportController";
	@Before(TokenInterceptor.class)
	public void summaryReport() {
		ResultInfo<Collection<SummaryReportData>> ret = new ResultInfo<Collection<SummaryReportData>> (ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = ReportService.summaryReport(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("formSumaryReport " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
}
