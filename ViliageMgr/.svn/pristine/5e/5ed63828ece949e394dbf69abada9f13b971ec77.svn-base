package com.moa.mgr.service;

import java.util.List;

import com.jfinal.core.Controller;
import com.moa.mgr.model.RegionModel;
import com.moa.mgr.service.loan.RegionLoader;

public class RegionService {
	public static List<RegionModel> getProvince(Controller controller) {
		List<RegionModel> regionList = RegionLoader.getInstance()
				.getRegionList();
		return regionList;
	}

	public static List<RegionModel> getCity(Controller controller) {
		List<RegionModel> regionList = RegionLoader.getInstance()
				.getRegionList();
		int provinceId = controller.getParaToInt("provinceId");
		for (RegionModel regionModel : regionList) {
			if (regionModel.getRegionId() == provinceId) {
				return regionModel.getChildren();
			}
		}
		return null;
	}

	public static List<RegionModel> getArea(Controller controller) {
		List<RegionModel> regionList = RegionLoader.getInstance()
				.getRegionList();
		int provinceId = controller.getParaToInt("provinceId");
		int cityId = controller.getParaToInt("cityId");
		for (RegionModel regionModel : regionList) {
			if (regionModel.getRegionId() == provinceId) {
				List<RegionModel> cityList = regionModel.getChildren();
				for (RegionModel city : cityList) {
					if (city.getRegionId() == cityId) {
						return city.getChildren();
					}
				}
			}
		}
		return null;
	}
}
