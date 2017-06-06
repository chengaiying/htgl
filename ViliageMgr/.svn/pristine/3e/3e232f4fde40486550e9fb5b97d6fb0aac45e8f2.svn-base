package com.moa.mgr.service.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.common.Utils;
import com.moa.mgr.db.Tables.TManager;

public class ManagerCache {
	
	private Map<String, ManagerInfo> cache = new ConcurrentHashMap<String, ManagerInfo>();
	
	private static ManagerCache instance = new ManagerCache();
	
	private ManagerCache() {
		load();
	}
	
	public static ManagerCache getInstance() {
		return instance;
	}
	
	private void load() {
		List<Record> list = Db.find("select * from manager");
		if (list != null) {
			cache.clear();
			for (Record record : list) {
				String userName = record.getStr(TManager.COL_USER_NAME);
				ManagerInfo mgr = ManagerInfo.genFromMap(record.getColumns());
				if (mgr != null) {
					cache.put(userName, mgr);
					LoggerFactory.getLogger("ManagerCache").info("load manager info:" + mgr);
				} else {
					LoggerFactory.getLogger("ManagerCache").error("load manager error:" + userName);
				}
			}
		}
	}
	
	public void addManagerToCache(ManagerInfo mgr) {
		if (mgr != null) {
			LoggerFactory.getLogger("ManagerCache").debug("add manager to cache:" + mgr);
			cache.put(mgr.userName, mgr);
		}
	}
	
	public ManagerInfo findManager(String userName) {
		if (Utils.isEmpty(userName)) {
			return null;
		}
		if (! cache.containsKey(userName)) {
			Record userRecord = Db.findById(TManager.TABLE_NAME, TManager.COL_USER_NAME, userName, "*");
			if (userRecord != null) {
				ManagerInfo mgrInfo = new ManagerInfo();
				mgrInfo.userName = userName;
				mgrInfo.type = userRecord.getInt(TManager.COL_TYPE);
				mgrInfo.bankId = userRecord.getInt(TManager.COL_BANK_ID);
				mgrInfo.setPermissions(userRecord.getStr(TManager.COL_PERMISSIONS));
				cache.put(userName, mgrInfo);
				return mgrInfo;
			}
		} else {
			return cache.get(userName);
		}
		
		return null;
	}
	
	public ManagerInfo removeManager(String userName) {
		ManagerInfo mgr = cache.remove(userName);
		if (mgr != null) {
			LoggerFactory.getLogger("ManagerCache").debug("add manager to cache:" + mgr);
		}
		return mgr;
	}
	
	public List<ManagerInfo> getAllManagers() {
		List<ManagerInfo> mgrList = new ArrayList<ManagerInfo>();
		mgrList.addAll(cache.values());
		Collections.sort(mgrList);
		
		return mgrList;
	}
}
