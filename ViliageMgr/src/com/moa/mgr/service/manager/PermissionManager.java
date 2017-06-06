package com.moa.mgr.service.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.common.Utils;

public class PermissionManager {
	private final Map<Integer, Permission> permissionCache = new TreeMap<>();
	
	private static final String TAG = "PermissionManager";
	
	private static PermissionManager instance = new PermissionManager();
	
	public final List<Permission> permissionList = new ArrayList<Permission>();
	
	private PermissionManager() {}
	
	public static PermissionManager getInstance() {
		return instance;
	}
	
	public Permission findPermission(int id) {
		if (permissionCache.isEmpty()) {
			load();
		}
		
		return permissionCache.get(id);
	}
	
	public Collection<Permission> getPermissionList() {
		return null;
	}
	
	
	public void load() {
		permissionCache.clear();
		
		List<Record> list = Db.find("select * from permission order by id");
		if (list != null) {
			for (Record record : list) {
				int id = record.getInt("id");
				String name = record.getStr("name");
				
				Permission per = new Permission();
				per.id = id;
				per.name = name;
				
				permissionCache.put(id, per);
				permissionList.add(per);
			}
		}
	}
	
	
	/**
	 * 判断用户提交权限字符串是否合法
	 * @param mgrType
	 * @param permissionStr
	 * @return
	 */
	@Deprecated
	public static boolean checkPermissionParam(int mgrType, String permissionStr) {
		if (Utils.isEmpty(permissionStr)) {
			return false;
		}
		
		String[] pers = permissionStr.split(",");
		if (pers == null || pers.length == 0) {
			return false;
		}
		
		for (String str : pers) {
			try {
				int perId = Integer.parseInt(str);
				if (getInstance().findPermission(perId) == null) {
					return false;
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(TAG).error("权限id错误：" + str);
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean checkPermissionParam(int mgrType, String[] pers, TreeSet<String> outSet) {
		if (pers == null || pers.length == 0) {
			return false;
		}
		
		for (String str : pers) {
			try {
				int perId = Integer.parseInt(str);
				
				if (getInstance().findPermission(perId) == null) {
					return false;
				}
				
				outSet.add(str);
			} catch (Exception e) {
				LoggerFactory.getLogger(TAG).error("权限id错误：" + str);
				return false;
			}
		}
		
		
		return true;
	}
}
