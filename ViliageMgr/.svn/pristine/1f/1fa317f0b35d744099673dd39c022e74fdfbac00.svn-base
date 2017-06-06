package com.moa.mgr.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.db.Tables.TRegion;
import com.moa.mgr.model.RegionModel;


public class RegionService2 {
	public static List<RegionModel> getProvince(Controller controller) {
		List<Record> list=Db.find("select * from region WHERE parent_id=0");
		List<RegionModel> regionList =new ArrayList<>();
		for (Record record : list) {
			RegionModel rm=new RegionModel();
			rm.setRegionId(record.getInt(TRegion.COL_REGION_ID));
			rm.setRegionCode(record.getInt(TRegion.COL_REGION_CODE));
			rm.setRegionName(record.getStr(TRegion.COL_REGION_NAME));
			regionList.add(rm);
		}
		return regionList;
	}

	public static List<RegionModel> getCity(Controller controller) {
		List<RegionModel> regionList =new ArrayList<>();
		int provinceId = controller.getParaToInt("regionId");
		int region_code = controller.getParaToInt("regionCode");
		if (provinceId==1||provinceId==19||provinceId==818||provinceId==1900) {
			
			Record record=Db.findById(TRegion.TABLE_NAME, "region_Id",provinceId);
			RegionModel rm=new RegionModel();
			rm.setRegionId(record.getInt(TRegion.COL_REGION_ID));
			rm.setRegionCode(record.getInt(TRegion.COL_REGION_CODE));
			rm.setRegionName(record.getStr(TRegion.COL_REGION_NAME));
			regionList.add(rm);
		} else{
			List<Record> list=Db.find("select * from region WHERE parent_id=?",region_code);
			for (Record record : list) {
				RegionModel rm=new RegionModel();
				rm.setRegionId(record.getInt(TRegion.COL_REGION_ID));
				rm.setRegionCode(record.getInt(TRegion.COL_REGION_CODE));
				rm.setRegionName(record.getStr(TRegion.COL_REGION_NAME));
				regionList.add(rm);
			}
		}
		
		return regionList;
	}

	public static List<RegionModel> getArea(Controller controller) {
		int region_code = controller.getParaToInt("regionCode");
		List<Record> list=Db.find("select * from region WHERE parent_id=?",region_code);
		List<RegionModel> regionList =new ArrayList<>();
		for (Record record : list) {
			RegionModel rm=new RegionModel();
			rm.setRegionId(record.getInt(TRegion.COL_REGION_ID));
			rm.setRegionCode(record.getInt(TRegion.COL_REGION_CODE));
			rm.setRegionName(record.getStr(TRegion.COL_REGION_NAME));
			regionList.add(rm);
		}
		return regionList;
	
	}


}
