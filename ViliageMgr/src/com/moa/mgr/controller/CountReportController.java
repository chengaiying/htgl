package com.moa.mgr.controller;

import java.util.Map;

import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.service.CountReportService;

public class CountReportController extends Controller{
	private static final String TAG = "CountReportController";
	
	@Before(TokenInterceptor.class)
	public void provinceRegisterNumber(){
		ResultInfo ret = new ResultInfo(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = CountReportService.provinceRegisterNumber(this);
		} catch (Exception e) { 
			LoggerFactory.getLogger(TAG).error("provinceRegisterNumber error:" + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void RegisterType(){
		ResultInfo ret = new ResultInfo(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = CountReportService.RegisterType(this);
		} catch (Exception e) { 
			LoggerFactory.getLogger(TAG).error("RegisterType error:" + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void userAge(){
		ResultInfo ret = new ResultInfo(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = CountReportService.userAge(this);
		} catch (Exception e) { 
			LoggerFactory.getLogger(TAG).error("userAge error:" + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void trainCount(){
		ResultInfo<Map<String,Map<String,Integer>>> ret = new ResultInfo<Map<String,Map<String,Integer>>>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = CountReportService.trainCount(this);
		} catch (Exception e) { 
			LoggerFactory.getLogger(TAG).error("trainCount:" + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void educationCount(){
		ResultInfo<Map<String,String>> ret = new ResultInfo<Map<String,String>>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = CountReportService.educationCount(this);
		} catch (Exception e) { 
			LoggerFactory.getLogger(TAG).error("educationCount:" + e.getMessage());
		}
		renderText(ret.toJson());
	}
}
