package com.moa.mgr.service.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.moa.mgr.common.Utils;
import com.moa.mgr.db.Tables.TManager;

public class ManagerInfo implements Serializable, Comparable<ManagerInfo> {

	private static final long serialVersionUID = -1793580153413984524L;
	
	/** 管理员类型： 超级管理员*/
	public static final int MGR_TYPE_ADMIN = 1;
	
	/** 管理员类型： 普通管理员*/
	public static final int MGR_TYPE_SYS = 2;
	
	/** 管理员类型： 机构管理员*/
	public static final int MGR_TYPE_BANK_MGR = 3;
	/** 管理员类型： 地区管理员*/
	public static final int MGR_TYPE_AREA_MGR = 4;
	/** 管理员类型： 保险公司管理员*/
	public static final int MGR_TYPE_INSURER_MGR = 5;
	
	
	public String userName;
	
	public String realName;
	
	public String phone;
	
	public String regionId;
	
	public int type;
	
	public int bankId;
	
	public int insurerId;
	
	public transient String sessionId;
	
	/** 
	 * 保存用户上传文件的临时路径
	 *  如果信息直报时提交的图片
	 **/
	private transient ConcurrentHashMap<String, String> tmpFileCache = new ConcurrentHashMap<String, String>();
	
	public List<Permission> permissions = new ArrayList<>();
	
	private transient String permissionStr = "";
	
	public void setPermission(TreeSet<String> perIds) {
		permissions.clear();
		permissionStr = "";
		for (String idStr : perIds) {
			if (! Utils.isEmpty(idStr)) {
				try {
					Integer id = Integer.parseInt(idStr);
					Permission per = PermissionManager.getInstance().findPermission(id);
					if (per != null) {
						permissions.add(per);
						permissionStr += (id + ",");
					}
				} catch (Exception e) {
					LoggerFactory.getLogger("ManagerInfo").error("管理员权限数据有错误:" + userName + ", " + e.getMessage());
				}
			}
		}
		
		if (permissionStr.length() > 2) {
			permissionStr = permissionStr.substring(0, permissionStr.length() - 2);
			System.out.println("permissionStr: " + permissionStr);
		}
	}
	
	public void setPermissions(String str) {
		if (Utils.isEmpty(str)) {
			return;
		}
		TreeSet<String> perIds = Utils.fromJson(str, new TypeToken<TreeSet<String>>(){}.getType());
		setPermission(perIds);
	}
	
	public static ManagerInfo genFromMap(Map<String, Object> map) {
		ManagerInfo mgrInfo = new ManagerInfo();
		try {
			if (map.get(TManager.COL_USER_NAME) != null) {
				mgrInfo.userName = map.get(TManager.COL_USER_NAME).toString();
			}
			
			if (map.get(TManager.COL_REAL_NAME) != null) {
				mgrInfo.realName = map.get(TManager.COL_REAL_NAME).toString();
			}
			
			if (map.get(TManager.COL_PHONE) != null) {
				mgrInfo.phone = map.get(TManager.COL_PHONE).toString();
			}
			if (map.get(TManager.COL_REGION_ID) != null) {
				mgrInfo.regionId = map.get(TManager.COL_REGION_ID).toString();
			}
			
			if (map.get(TManager.COL_TYPE) != null) {
				try {
					mgrInfo.type =  (int) map.get(TManager.COL_TYPE);
				} catch (Exception e) {
					LoggerFactory.getLogger("ManagerInfo").error("参数[type]错误");
				}
			}
			
			if (map.get(TManager.COL_BANK_ID) != null) {
				try {
					mgrInfo.bankId = (int) map.get(TManager.COL_BANK_ID);
				} catch (Exception e) {
					LoggerFactory.getLogger("ManagerInfo").error("参数[bank_id]错误");
				}
			}
			if (map.get(TManager.COL_INSURER_ID) != null) {
				try {
					mgrInfo.insurerId = (int) map.get(TManager.COL_INSURER_ID);
				} catch (Exception e) {
					LoggerFactory.getLogger("ManagerInfo").error("参数[insurer_id]错误");
				}
			}
			
			mgrInfo.setPermissions(map.get(TManager.COL_PERMISSIONS).toString());
		} catch (Exception e) {
			LoggerFactory.getLogger("ManagerInfo").error("管理员数据读取失败:" + e.getMessage());
		}
		
		return mgrInfo;
	}
	
	@Override
	public String toString() {
		String str = "mgrName:" + userName + ", realName:" + realName +", mgrType:" + type + "\n";
		for (Permission per : permissions) {
			str += (per + ", ");
		}
		
		return str;
	}
	
	/**
	 * 检测是否具有指定权限
	 * @param permissionId
	 * @return
	 */
	public boolean checkPermission(int permissionId) {
		synchronized (permissions) {
			for (Permission per : permissions) {
				if (per.id == permissionId) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getPermissionStr() {
		return permissionStr;
	}

	public void addTmpFile(String key, String file) {
		String oriName = tmpFileCache.get(key);
		if (! Utils.isEmpty(oriName)) {
			Utils.deleteFileByName(oriName);
		}
		
		tmpFileCache.put(key, file);
	}
	
	public String findTmpFileName(String key) {
		return tmpFileCache.get(key);
	}
	
	public void removeTmpFile(String key) {
		tmpFileCache.remove(key);
	}
	
	public void clearTmpFiles() {
		tmpFileCache.values();
		Collection<String> files = tmpFileCache.values();
		for (String tmp : files) {
			Utils.deleteFileByName(tmp);
		}
	}
	
	@Override
	public int compareTo(ManagerInfo another) {
		if (type == another.type) {
			return userName.compareTo(another.userName);
		}
		return type - another.type;
	}
}
