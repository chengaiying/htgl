package com.moa.mgr.controller;

import org.slf4j.LoggerFactory;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.UserService;


public class UserController extends Controller {
	
	private static final String TAG = "UserController";
	
	@Before(TokenInterceptor.class)
	public void addUser() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = UserService.addUser(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("add user error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	//初始化用户参数
	@Before(TokenInterceptor.class)
	public void InitAlipayUser(){
		ResultInfo<EmptyResultObj> ret = new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = UserService.saveAlipayUser(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("add user error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	//xf updated by 20170414
	@Before(TokenInterceptor.class)
	public void addFarm(){
		ResultInfo<EmptyResultObj> ret = new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = UserService.addFarm(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("add user error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
}
