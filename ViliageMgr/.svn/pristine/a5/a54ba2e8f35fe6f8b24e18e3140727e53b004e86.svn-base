package com.moa.mgr.service.loan;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.db.Tables.TRegion;
import com.moa.mgr.model.RegionModel;

/**
 * 启动时加载区域数据
 * 
 * @author lee
 * 
 */
public class RegionLoader {
	private static String ALL_PRIOVINCE_SQL = "select * from region where parent_id=0";
	private static String SEL_BY_PARENT = "select * from region where parent_id=?";
	private static RegionLoader instance = new RegionLoader();
	private List<RegionModel> regionList = new ArrayList<>();

	private RegionLoader() {
	}

	public static RegionLoader getInstance() {
		return instance;
	}

	public void loadRegion() {
		List<Record> provinceRecords = Db.find(ALL_PRIOVINCE_SQL);
		for (Record province : provinceRecords) {
			int proId = province.getInt(TRegion.COL_REGION_ID);
			String proName = province.getStr(TRegion.COL_REGION_NAME);
			int proCode = province.getInt(TRegion.COL_REGION_CODE);
			RegionModel proModel = new RegionModel();
			proModel.setRegionId(proId);
			proModel.setRegionName(proName);
			proModel.setRegionCode(proCode);
			List<RegionModel> cityList = new ArrayList<>();
			List<Record> cityRecords = Db.find(SEL_BY_PARENT, proCode);
			for (Record city : cityRecords) {
				int cityId = city.getInt(TRegion.COL_REGION_ID);
				String cityName = city.getStr(TRegion.COL_REGION_NAME);
				int cityCode = city.getInt(TRegion.COL_REGION_CODE);
				RegionModel cityModel = new RegionModel();
				cityModel.setRegionId(cityId);
				cityModel.setRegionName(cityName);
				cityModel.setRegionCode(cityCode);
				cityList.add(cityModel);
				List<RegionModel> countryList = new ArrayList<>();
				List<Record> contryRecords = Db.find(SEL_BY_PARENT, cityCode);
				for (Record country : contryRecords) {
					int countryId = country.getInt(TRegion.COL_REGION_ID);
					String countryName = country
							.getStr(TRegion.COL_REGION_NAME);
					int countryCode = country.getInt(TRegion.COL_REGION_CODE);
					RegionModel countryModel = new RegionModel();
					countryModel.setRegionId(countryId);
					countryModel.setRegionName(countryName);
					countryModel.setRegionCode(countryCode);
					countryList.add(countryModel);
				}
				cityModel.setChildren(countryList);
			}
			proModel.setChildren(cityList);
			regionList.add(proModel);
		}
	}
	
	public List<RegionModel> getRegionList() {
		return regionList;
	}
}
