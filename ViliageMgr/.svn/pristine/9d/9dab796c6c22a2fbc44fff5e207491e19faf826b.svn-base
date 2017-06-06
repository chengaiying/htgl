package com.moa.mgr.controller;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.ManagerService;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;
import com.moa.mgr.service.manager.Permission;
import com.moa.mgr.service.manager.PermissionManager;

public class ManagerController extends Controller {
	
	private static final String TAG = "ManagerController";
	
	public void login() {
		ResultInfo<ManagerInfo> ret = new ResultInfo<ManagerInfo>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = ManagerService.login(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("login error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void addMgr() {
		ResultInfo<ManagerInfo> ret = new ResultInfo<ManagerInfo>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = ManagerService.addMgr(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("add mgr error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	public void permissionList() {
		ResultInfo<List<Permission>> ret = new ResultInfo<List<Permission>>(ResultCodes.RET_SUCCESS, "ok", PermissionManager.getInstance().permissionList);
		
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void delMgr() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = ManagerService.delete(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("delete mgr error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	@Before(TokenInterceptor.class)
	public void mgrList() {
		ResultInfo<Page<ManagerInfo>> result = new ResultInfo<Page<ManagerInfo>>(
				ResultCodes.RET_NO_PERMISSION, "无权限");
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (!optMgr.checkPermission(Permission.PERMISSION_SCAN_FARMER_DIRECTORY)) {
			result = new ResultInfo<Page<ManagerInfo>>(
					ResultCodes.RET_NO_PERMISSION, "无权限");
		} else {
			result = ManagerService.queryUserByPage(this);
		}
		renderText(result.toJson());
	}
	@Before(TokenInterceptor.class)
	public void resetPswd() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = ManagerService.resetPswd(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("reset mgr error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void changePswd() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = ManagerService.changePswd(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("change mgr error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void checkLoginStatus() {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		ResultInfo<ManagerInfo> ret = new ResultInfo<ManagerInfo>(ResultCodes.RET_SUCCESS, "", optMgr);
		
		renderText(ret.toJson());
	}
	
	@Before(TokenInterceptor.class)
	public void logout() {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		optMgr.sessionId = "";
		ResultInfo<EmptyResultObj> ret = new ResultInfo<>(ResultCodes.RET_SUCCESS);
		renderText(ret.toJson());
	}
}
