package com.moa.mgr.controller;

import java.util.List;

import com.jfinal.core.Controller;
import com.moa.mgr.model.RegionModel;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.service.RegionService2;

public class RegionController2 extends Controller{

	public void getProvnice() {
		List<RegionModel> province = RegionService2.getProvince(this);
		ResultInfo<List<RegionModel>> regionRet = new ResultInfo<>(
				ResultCodes.RET_SUCCESS, "ok", province);
		renderText(regionRet.toJson());
	}

	public void getCity() {
		List<RegionModel> city = RegionService2.getCity(this);
		ResultInfo<List<RegionModel>> regionRet = new ResultInfo<>(
				ResultCodes.RET_SUCCESS, "ok", city);
		renderText(regionRet.toJson());
	}

	public void getArea() {
		List<RegionModel> area = RegionService2.getArea(this);
		ResultInfo<List<RegionModel>> regionRet = new ResultInfo<>(
				ResultCodes.RET_SUCCESS, "ok", area);
		renderText(regionRet.toJson());
	}


}
