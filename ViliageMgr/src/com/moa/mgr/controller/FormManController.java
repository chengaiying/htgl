package com.moa.mgr.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.service.FormManService;
import com.moa.mgr.service.FormManService.FarmerInfo;
import com.moa.mgr.service.FormManService.FormDataListInfo;
import com.moa.mgr.service.form.FormDef;
import com.moa.mgr.service.form.FormDefLoader;

public class FormManController extends Controller {
	
	private static final String TAG = "FormManController";
	
	@Before(TokenInterceptor.class)
	public void outExcel() {	
		File file = null;
		try {
			file = FormManService.outExcel(this);
		} catch (Exception e) {
			LoggerFactory.getLogger("FormManController").error("outExcel error " + e.getMessage());
		}
		renderFile(file);
	}
	
	@Before(TokenInterceptor.class)
	public void formCount() {	
		ResultInfo ret = new ResultInfo(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = FormManService.formCount(this);
		} catch (Exception e) {
			LoggerFactory.getLogger("FormManController").error("outExcel error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	public void farmerInfo(){
		ResultInfo<List<FarmerInfo>> result = FormManService.formInfo(this);
		renderText(result.toJson());
	}
	
	/** 列出所有表单 */
	public void formList() {
		ResultInfo<List<Map<String, Object>>> ret = new ResultInfo<List<Map<String, Object>>>(ResultCodes.RET_SUCCESS, "success", FormDefLoader.getInstance().simpleFormList);
		renderText(ret.toJson());
	}
	
	/**
	 * 查看表单结构信息
	 */
	public void formDefDetail() {
		ResultInfo<FormDef> ret = new ResultInfo<FormDef>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = FormManService.formDefDetail(this);
		} catch (Exception e) { 
			LoggerFactory.getLogger(TAG).error("formDetail error:" + e.getMessage());
		}
		renderText(ret.toJson());
	}
	@Before(TokenInterceptor.class)
	public void formData() {
		ResultInfo<FormDataListInfo> ret = new ResultInfo<FormDataListInfo>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = FormManService.formData(this);
		} catch (Exception e) {
			LoggerFactory.getLogger("FormManController").error("formData error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void updateFormDef() {
		ResultInfo<Integer> ret = new ResultInfo<Integer>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = FormManService.updateFormDef(this);
		} catch (Exception e) {
			LoggerFactory.getLogger("FormManController").error("update form def error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void addFormDef() {
		ResultInfo<Integer> ret = new ResultInfo<Integer>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = FormManService.addFormDef(this);
		} catch (Exception e) {
			LoggerFactory.getLogger("FormManController").error("add form def error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void removeFormDef() {
		ResultInfo<Integer> ret = new ResultInfo<Integer>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = FormManService.removeFormDef(this);
		} catch (Exception e) {
			LoggerFactory.getLogger("FormManController").error("remove form def error " + e.getMessage());
		}
		System.out.println("ret.code:"+ret.code+","+"ret.desc:"+ret.desc);
		FormDefLoader.getInstance().loadFormDefs();
		renderText(ret.toJson());
		//render("/man/html/form_list.html");
	}
}
