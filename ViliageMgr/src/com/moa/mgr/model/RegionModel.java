package com.moa.mgr.model;

import java.util.List;

/**
 * 区域实体
 * 
 * @author lee
 * 
 */
public class RegionModel {
	private int regionId;
	private String regionName;
	private int regionCode;
	private List<RegionModel> children;

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public int getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(int regionCode) {
		this.regionCode = regionCode;
	}

	public List<RegionModel> getChildren() {
		return children;
	}

	public void setChildren(List<RegionModel> children) {
		this.children = children;
	}
}
