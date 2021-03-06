package com.moa.mgr.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.common.PasswordHash;
import com.moa.mgr.common.Utils;
import com.moa.mgr.db.Tables.TManager;
import com.moa.mgr.model.AlipayUserModel;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;
import com.moa.mgr.service.manager.Permission;
import com.moa.mgr.service.manager.PermissionManager;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class ManagerService {
	
	private static final String TAG = "ManagerService";
	
	
	/** 默认密码：888888 */
	private static final String DEFAULT_PSWD_HASH = "1000:40f9a5af550135dfca0d6052a157d475:d4063769c45c9f068df961986993ce28";
	
	/** 核心操作日志模板 */
	private static final String OPT_LOG_TMP = "%s, Operator:[%s], Ip:[%s]";
	
	/**
	 * 管理员登录接口
	 * @param controller
	 * @return
	 */
	public static ResultInfo<ManagerInfo> login(Controller controller) {
		if (controller.isParaBlank(TManager.COL_USER_NAME) || controller.isParaBlank(TManager.COL_PSWD)) {
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "user name or password error");
		}
		
		String userName = controller.getPara(TManager.COL_USER_NAME);
		String pswd = controller.getPara(TManager.COL_PSWD);
		
		Record userRecord = Db.findById(TManager.TABLE_NAME, TManager.COL_USER_NAME, userName, "pswd");
		if (userRecord == null) {
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "user name or password error");
		}
		
		String correctHash = userRecord.getStr(TManager.COL_PSWD);
		boolean bool = false;
		try {
			bool = PasswordHash.validatePassword(pswd, correctHash);
		} catch (Exception e) {
			StackTraceElement[] es =  e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			if (es != null) {
				for (StackTraceElement el : es) {
					sb.append(el.toString() + "\n");
				}
			} 
			LoggerFactory.getLogger(TAG).error("login error:" + es.toString());
			LoggerFactory.getLogger(TAG).error("login error ext,userName:" + userName + ", pswd:" + pswd);
		}
		if (bool) {
			ManagerInfo mgrInfo = ManagerCache.getInstance().findManager(userName);
			controller.getSession().setAttribute(TokenInterceptor.PARAM_MGR_ID, userName);
			mgrInfo.sessionId = controller.getSession().getId();
			LoggerFactory.getLogger(TAG).info(String.format(OPT_LOG_TMP, "Login", userName, controller.getRequest().getRemoteHost()));
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_SUCCESS, "ok", mgrInfo);
		} else {
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "user name or password error");
		}
	}
	
	/**
	 * 修改密码,只能修改自己的密码
	 * @param controller
	 */
	public static ResultInfo<EmptyResultObj> changePswd(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		String oriPswd = controller.getPara("ori_" + TManager.COL_PSWD);
		String pswd = controller.getPara("new_" + TManager.COL_PSWD);
		
		if (Utils.isEmpty(pswd) || pswd.length() < 6 || Utils.isEmpty(oriPswd) || oriPswd.length() < 6) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "密码长度不得小于6位");
		}
		
		if (pswd.equals(oriPswd)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "不能和之前密码一样");
		}
		
		Record userRecord = Db.findById(TManager.TABLE_NAME, TManager.COL_USER_NAME, optMgr.userName, "*");
		if (userRecord == null) {
			return new ResultInfo<>(ResultCodes.RET_DATA_NOT_EXIST, "用户不存在");
		}
		
		String correctHash = userRecord.getStr(TManager.COL_PSWD);
		try {
			if (PasswordHash.validatePassword(oriPswd, correctHash)) {
				String newPswdHash = PasswordHash.createHash(pswd);
				userRecord.set(TManager.COL_PSWD, newPswdHash);
				//manager中新增modify_time（修改时间），modify(修改者)字段
				userRecord.set(TManager.COL_MODIFY_TIME,Timestamp.valueOf(Utils.getCurrentTimeStr()));
				userRecord.set(TManager.COL_MODIFY,optMgr.userName);
				
				boolean bol = Db.update(TManager.TABLE_NAME, TManager.COL_USER_NAME, userRecord);
				if (bol) {
					return new ResultInfo<>(ResultCodes.RET_SUCCESS, "密码修改成功");
				}
				LoggerFactory.getLogger(TAG).info(String.format(OPT_LOG_TMP, "修改密码：成功", optMgr.userName, controller.getRequest().getRemoteHost()));
			} else {
				LoggerFactory.getLogger(TAG).info(String.format(OPT_LOG_TMP, "修改密码：原始密码错误", optMgr.userName, controller.getRequest().getRemoteHost()));
				return new ResultInfo<>(ResultCodes.RET_FAILED, "原始密码错误，请重试或联系客服");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("login error:" + e.getMessage());
		}
		
		try {
			String pswdHash = PasswordHash.createHash(pswd);
			//修改sql中新增修改时间，修改者参数
			int ct = Db.update("update manager set pswd=?  where user_name=? and modify_time=? and modify=?", pswdHash, optMgr.userName,Timestamp.valueOf(Utils.getCurrentTimeStr()),optMgr.userName);
			if (ct == 1) {
				return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("hash pash error");
		}
		
		return new ResultInfo<>(ResultCodes.RET_FAILED);
	}
	
	/**
	 * 超级管理员将其它管理员密码进行重置
	 * 默认为 888888
	 * @param controller
	 */
	public static ResultInfo<EmptyResultObj> resetPswd(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (optMgr.type != ManagerInfo.MGR_TYPE_ADMIN) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "非超级管理无权限进行密码重置");
		}
		
		String mgrName = controller.getPara(TManager.COL_USER_NAME);
		if (Utils.isEmpty(mgrName)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "mgr name cannot be empty");
		}
		
		ManagerInfo mgr = ManagerCache.getInstance().findManager(mgrName);
		if (mgr != null && optMgr.userName.equals(mgrName)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "不能对自己进行该操作");
		} 
		
		String pswd = controller.getPara(TManager.COL_PSWD);
		String pswdHash = DEFAULT_PSWD_HASH;
		
		if (! Utils.isEmpty(pswd)) {
			if (pswd.length() < 6) {
				return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "密码长度不得小于6位");
			}
			try {
				pswdHash = PasswordHash.createHash(pswd);
				//修改参数新增modify和modify_time字段
				int ct = Db.update("update manager set pswd=?  where user_name=? and modify_time=? and modify=?", pswdHash, mgrName,Timestamp.valueOf(Utils.getCurrentTimeStr()),optMgr.userName);
				if (ct == 1) {
					return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(TAG).error("hash pash error");
			}
		}
		
		return new ResultInfo<>(ResultCodes.RET_FAILED);
	}
	
	
	/**
	 * 新增管理员接口，需要相应权限
	 * @param controller
	 */
	public static ResultInfo<ManagerInfo> addMgr(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (! optMgr.checkPermission(Permission.PERMISSION_ADD_MGR)) {
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		
		String userName = controller.getPara(TManager.COL_USER_NAME);
		String realName = controller.getPara(TManager.COL_REAL_NAME);
		String pswd = controller.getPara(TManager.COL_PSWD);
		String phone = controller.getPara(TManager.COL_PHONE);
		int type = -1;
		
		if (Utils.isEmpty(userName)) {
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "用户名不得为空");
		}
		try {
			type = controller.getParaToInt(TManager.COL_TYPE);
		} catch (Exception e) {
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "管理员类型错误");
		}
		
		Record mgrRecord = new Record();
		mgrRecord.set(TManager.COL_USER_NAME, userName);
		mgrRecord.set(TManager.COL_REAL_NAME, realName);
		mgrRecord.set(TManager.COL_PHONE, phone);
		mgrRecord.set(TManager.COL_TYPE, type);
		
		if (type == ManagerInfo.MGR_TYPE_ADMIN) {
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_NO_PERMISSION, "不得添加超级管理员");
		}
		
		//机构管理员需要指定机构id
		if (type == ManagerInfo.MGR_TYPE_BANK_MGR) {
			try {
				mgrRecord.set(TManager.COL_BANK_ID, controller.getParaToInt(TManager.COL_BANK_ID));
			} catch (Exception e) {
				return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "机构id错误");
			}
		}
		if (type == ManagerInfo.MGR_TYPE_AREA_MGR) {
			String region_id=controller.getPara(TManager.COL_REGION_ID);
			if (Utils.isEmpty(region_id)) {
				return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "地区id不能为空");
			}
			try {
				mgrRecord.set(TManager.COL_REGION_ID, region_id);
			} catch (Exception e) {
				return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "地区id错误");
			}
		}
		if (type == ManagerInfo.MGR_TYPE_INSURER_MGR) {
			try {
				mgrRecord.set(TManager.COL_INSURER_ID, controller.getParaToInt(TManager.COL_INSURER_ID));
			} catch (Exception e) {
				return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "保险公司id错误");
			}
		}
		
		if (Utils.isEmpty(pswd)) {
			mgrRecord.set(TManager.COL_PSWD, DEFAULT_PSWD_HASH);
		} else {
			try {
				mgrRecord.set(TManager.COL_PSWD, PasswordHash.createHash(pswd));
			} catch (Exception e) {
				LoggerFactory.getLogger(TAG).error("hash pash error");
				return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "密码错误");
			}
		}
		
		TreeSet<String> perSet = new TreeSet<String>();
		String[] permArray = controller.getParaValues(TManager.COL_PERMISSIONS);
		if (! PermissionManager.checkPermissionParam(type, permArray, perSet)) {
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_PARAM_ERROR, "权限错误");
		}
		
		mgrRecord.set(TManager.COL_PERMISSIONS, Utils.toJson(perSet));
		
		try {
			boolean bool = Db.save(TManager.TABLE_NAME, TManager.COL_USER_NAME, mgrRecord);
			if(bool) {
				LoggerFactory.getLogger(TAG).info(String.format(OPT_LOG_TMP, "Add mgr:" + userName, optMgr.userName, controller.getRequest().getRemoteHost()));
				mgrRecord.set(TManager.COL_USER_NAME, userName);
				ManagerInfo mgrInfo = ManagerInfo.genFromMap(mgrRecord.getColumns());
				if (mgrInfo == null) {
					return new ResultInfo<ManagerInfo>(ResultCodes.RET_FAILED);
				}
				ManagerCache.getInstance().addManagerToCache(mgrInfo);
				return new ResultInfo<ManagerInfo>(ResultCodes.RET_SUCCESS, "ok", mgrInfo);
			}
		} catch (Exception e) {
			if (e != null && e.getCause() != null && e.getCause() instanceof MySQLIntegrityConstraintViolationException) {
				return new ResultInfo<ManagerInfo>(ResultCodes.RET_DATA_HAS_EXIST, "用户已经存在");
			}
			return new ResultInfo<ManagerInfo>(ResultCodes.RET_FAILED);
		}
		
		return new ResultInfo<ManagerInfo>(ResultCodes.RET_FAILED);
	}
	
	
	/**
	 * 删除管理员，需要相应权限
	 * @param controller
	 */
	public static ResultInfo<EmptyResultObj> delete(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (! optMgr.checkPermission(Permission.PERMISSION_DEL_MGR)) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		
		String delName = controller.getPara(TManager.COL_USER_NAME);
		
		if (Utils.isEmpty(delName)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "请指定待删除管理员用户名");
		}
		
		if (delName.equals(optMgr.userName)) {
			return new ResultInfo<>(ResultCodes.RET_FAILED, "不能删除自己");
		}
		
		//不能删除超级管理员
		int ct = Db.update("delete from manager where user_name = ? and type != ?" , delName, 1);
//		boolean bool = Db.deleteById(TManager.TABLE_NAME, TManager.COL_USER_NAME, delName);
		if (ct > 0) {
			ManagerCache.getInstance().removeManager(delName);
			LoggerFactory.getLogger(TAG).info(String.format(OPT_LOG_TMP, "Delete mgr:" + delName, optMgr.userName, controller.getRequest().getRemoteHost()));
			return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
		}
		
		return new ResultInfo<>(ResultCodes.RET_FAILED);
	}
	
	  public static ResultInfo<Page<ManagerInfo>> queryUserByPage(
			Controller controller){
		  int type=controller.getParaToInt("type",0);  
		  String userName=controller.getPara("userName");
		  int pageNumber=controller.getParaToInt("page", 1);
		  String sql = getSql(type, userName);
		  List<Object> param = new ArrayList<>();
		  if (type!=0) {
				param.add(type);
			};
		 if (!StringUtils.isEmpty(userName)) {
			
				param.add("%" + userName + "%");
			};
			
		  List<ManagerInfo> managerlist=new ArrayList<>();
		  Page<Record> result = Db.paginate(pageNumber, 50, "select *",
					sql, param.toArray());
		  List<Record> list=result.getList();
		  for (Record record : list) {
			  
			  ManagerInfo mgr = ManagerInfo.genFromMap(record.getColumns());
			  managerlist.add(mgr);
			  Collections.sort(managerlist);
			  
		}
		  Page<ManagerInfo> pageList = new Page<ManagerInfo>(managerlist,
					result.getPageNumber(), result.getPageSize(),
					result.getTotalPage(), result.getTotalRow());
		  return new ResultInfo<Page<ManagerInfo>>(ResultCodes.RET_SUCCESS,
					"ok", pageList);
		 
	  } 
	 public static String getSql(int type,String userName){
		String sql=null;
		if (type!=0 && !StringUtils.isEmpty(userName)) {
			sql="from manager where type = ? and user_name like ?";
		}else if(type!=0 && StringUtils.isEmpty(userName)){
			sql="from manager where type like ? ";
		}else if(type==0 && !StringUtils.isEmpty(userName)){
			sql="from manager where user_name like ? ";
		}else if(type==0 && StringUtils.isEmpty(userName)){
			sql="from manager";
		}
		return sql;
	 }
}
