package com.moa.mgr.controller;

import java.util.List;

import com.jfinal.core.Controller;
import com.moa.mgr.model.RegionModel;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.service.RegionService;
import com.moa.mgr.service.loan.RegionLoader;

/**
 * 地区controller
 * 
 * @author lee
 * 
 */
public class RegionController extends Controller {
	public void getRegionList() {
		ResultInfo<List<RegionModel>> regionRet = new ResultInfo<>(
				ResultCodes.RET_SUCCESS, "ok", RegionLoader.getInstance()
						.getRegionList());
		renderText(regionRet.toJson());
	}

	public void getProvnice() {
		List<RegionModel> province = RegionService.getProvince(this);
		ResultInfo<List<RegionModel>> regionRet = new ResultInfo<>(
				ResultCodes.RET_SUCCESS, "ok", province);
		renderText(regionRet.toJson());
	}

	public void getCity() {
		List<RegionModel> city = RegionService.getCity(this);
		ResultInfo<List<RegionModel>> regionRet = new ResultInfo<>(
				ResultCodes.RET_SUCCESS, "ok", city);
		renderText(regionRet.toJson());
	}

	public void getArea() {
		List<RegionModel> area = RegionService.getArea(this);
		ResultInfo<List<RegionModel>> regionRet = new ResultInfo<>(
				ResultCodes.RET_SUCCESS, "ok", area);
		renderText(regionRet.toJson());
	}
}
