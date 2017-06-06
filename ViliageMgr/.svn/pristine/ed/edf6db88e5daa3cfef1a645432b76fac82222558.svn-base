package com.moa.mgr.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.TrainService;

/**
 * 培训控制器
 * @author bjc-huran
 * 创建时间:2016-12-22
 */
public class TrainContorller extends Controller{
	private static final String TAG = "TrainContorller";
	
	@Before(TokenInterceptor.class)
	public void addTrainProject() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = TrainService.addTrainProject(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("add TrainProject error: " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void removeTrainProject() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {			
			ret = TrainService.removeTrainProject(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("remove TrainProject error: " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void updateTrainProject() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = TrainService.updateTrainProject(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("update TrainProject error: " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void findTrainProject() {
		ResultInfo<Record> ret = new ResultInfo<Record>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = TrainService.findTrainProject(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("findTrainProject error: " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void findCourseApply() {
		ResultInfo<List<Map<String, Object>>> ret = new ResultInfo<List<Map<String, Object>>>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = TrainService.findCourseApply(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("findCourseApply error: " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void pageBeanTrain() {
		ResultInfo<Record> ret = new ResultInfo<Record>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = TrainService.pageBeanTrain(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("pageBeanTrain error: " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void excelOut() {
		File file = null;
		try {
			file = TrainService.excelOut(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("pageBeanTrain error: " + e.getMessage());
		}
		renderFile(file);
	}
}