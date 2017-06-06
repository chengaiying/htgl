package com.moa.mgr.controller;

import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.model.AlipayUserModel;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.AlipayUserService;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;
import com.moa.mgr.service.manager.Permission;

public class AlipayUserController extends Controller {
	private static final String TAG = "AlipayUserController";
	@Before(TokenInterceptor.class)
	public void getUser() {
		ResultInfo<Page<AlipayUserModel>> result = new ResultInfo<Page<AlipayUserModel>>(
				ResultCodes.RET_NO_PERMISSION, "无权限");
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (!optMgr.checkPermission(Permission.PERMISSION_SCAN_FARMER_DIRECTORY)) {
			result = new ResultInfo<Page<AlipayUserModel>>(
					ResultCodes.RET_NO_PERMISSION, "无权限");
		} else {
			result = AlipayUserService.queryUserByPage(this);
		}
		renderText(result.toJson());
	}
	@Before(TokenInterceptor.class)
	public void deleteUser(){
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {			
			ret = AlipayUserService.deleteAlipayUser(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("delete alipayUser error: " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	@Before(TokenInterceptor.class)
    public void updateUser(){

		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = AlipayUserService.updateAlipayUser(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("update alipayUser error: " + e.getMessage());
		}
		renderText(ret.toJson());
	
	}
}
