package com.moa.mgr.service.form;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.WebConfig4Mgr;
import com.moa.mgr.db.Tables.TFormDef;

/**
 * 表单资源加载器，服务启动或配置改变时启动
 * @author zf21100
 */
public class FormDefLoader {
	
	private static final String SQL_QUERY_ALL_FORM_DEFS = "select * from " + TFormDef.TABLE_NAME + " order by seq";
	
	public Map<Integer, FormDef> formDefMap = new LinkedHashMap<Integer, FormDef>();
	
	public List<Map<String, Object>> simpleFormList = new ArrayList<Map<String, Object>>();
	
	private static FormDefLoader instance = new FormDefLoader();
	
	private FormDefLoader(){}
	
	public static FormDefLoader getInstance() {
		return instance;
	}
	
	/** 加载表单定义数据  */
	public void loadFormDefs() {
		List<Record> formDefRecords = Db.find(SQL_QUERY_ALL_FORM_DEFS);
		if (formDefRecords == null) {
			return;
		}
		formDefMap.clear();
		simpleFormList.clear();
		
		for (Record record : formDefRecords) {
			int formDefId = record.getInt(TFormDef.COL_ID);
			String formName = record.getStr(TFormDef.COL_FORM_NAME);
			
			String wordsDefJson = record.getStr(TFormDef.COL_WORDS_DEF);
			String startDate = record.getStr(TFormDef.COL_START_DATE);
			String endDate = record.getStr(TFormDef.COL_END_DATE);
			String fitFarmer = record.getStr(TFormDef.COL_FIT_FARMER);
			String bootImg = record.getStr(TFormDef.COL_BOOT_IMG);
			String icon = record.getStr(TFormDef.COL_ICON); 
			String formDesc = record.getStr(TFormDef.COL_FORM_DESC);
			String flag = record.getStr(TFormDef.COL_FLAG);
			
			
			FormDef formDef = new FormDef(formDefId, formName, icon, wordsDefJson, startDate, endDate, fitFarmer, bootImg, formDesc);
			formDef.flag = flag;
			try {
				formDef.seq = record.getInt(TFormDef.COL_SEQ);
			} catch (Exception e) {}
			
			File dir = new File(WebConfig4Mgr.formDir + File.separator + formDefId);
			if (! dir.exists()) {
				dir.mkdir();
			}
			
			formDefMap.put(formDefId, formDef);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", formDefId);
			map.put("formName", formName);
			map.put("flag", flag);
			map.put("icon", icon);
			map.put("formDesc", formDesc);
			map.put("bootImg", bootImg);
			map.put("seq", formDef.seq);
			
			simpleFormList.add(map);
		}
	}
	
	public void addForm(FormDef def) {
		if (def != null) {
			File dir = new File(WebConfig4Mgr.formDir + File.separator + def.id);
			if (! dir.exists()) {
				dir.mkdir();
			}
			synchronized (formDefMap) {
				formDefMap.put(def.id, def);
				simpleFormList.clear();
				Iterator<FormDef> iterator = formDefMap.values().iterator();
				while (iterator.hasNext()) {
					FormDef next = iterator.next();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", next.id);
					map.put("formName", next.formName);
					map.put("flag", next.flag);
					map.put("icon", next.icon);
					map.put("formDesc", next.formDesc);
					map.put("bootImg", next.bootImg);
					map.put("seq", next.seq);
					
					simpleFormList.add(map);
				}
			}
		}
	}
	
	public FormDef findForm(int id) {
		return formDefMap.get(id);
	}
	
}
