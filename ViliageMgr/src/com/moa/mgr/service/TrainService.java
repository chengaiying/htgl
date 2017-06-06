package com.moa.mgr.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.common.Utils;
import com.moa.mgr.db.Tables.TrainCourse;
import com.moa.mgr.model.AlipayUserModel;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;
import com.alibaba.druid.util.StringUtils;

public class TrainService {
	private static final String TAG = "TrainService";
	
	public static ResultInfo<EmptyResultObj> addTrainProject(Controller controller) {
		// 获取系统用户缓存
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		String regionId = optMgr.regionId;
		String trainName = controller.getPara("trainName").replaceAll(" ", "");
		String trainAddress = controller.getPara("trainAddress");
		String trainDate = controller.getPara("trainDate");
		String trainContent = controller.getPara("trainContent");
		String trainMessage = controller.getPara("trainMessage");
		String userName = optMgr.userName;
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		if(StringUtils.isEmpty(regionId)){
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "非区域管理员");
		}
		if(Utils.isEmpty(trainName)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目名称填写错误");
		}
		if(Utils.isEmpty(trainAddress)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目地点填写错误");
		}
		if(Utils.isEmpty(trainDate)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目时间填写错误");
		}
		if(Utils.isEmpty(trainContent)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目内容填写错误");
		}
		Record record = new Record();
		record.set(TrainCourse.COL_TRAIN_PROJECT, trainName);
		record.set(TrainCourse.COL_REGION_ID, regionId);
		record.set(TrainCourse.COL_CREATEOR, userName);
		record.set(TrainCourse.COL_CREATEOR_TIME, date);
		record.set(TrainCourse.COL_PHONE, optMgr.phone);
		//新增字段
		record.set(TrainCourse.COL_DESCRIBE, trainMessage);
		record.set(TrainCourse.COL_ADDRESS, trainAddress);
		record.set(TrainCourse.COL_CONTENT, trainContent);
		record.set(TrainCourse.COL_RELEASE_DATE, trainDate);
		if (Db.save(TrainCourse.TABLE_NAME, record)) {
			return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
		}
		return new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR, "failed");
	}
	
	public static ResultInfo<EmptyResultObj> removeTrainProject(Controller controller) {
		// 获取系统用户缓存
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		String regionId = optMgr.regionId;
		String id = controller.getPara("id");
		if(StringUtils.isEmpty(regionId)){
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "非区域管理员");
		}
		if(Utils.isEmpty(id)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "id错误");
		}
		if(Db.deleteById(TrainCourse.TABLE_NAME, id)){
			return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
		}
		return new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR, "failed");
	}
	
	public static ResultInfo<EmptyResultObj> updateTrainProject(Controller controller) {
		// 获取系统用户缓存
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		String regionId = optMgr.regionId;
		if(StringUtils.isEmpty(regionId)){
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "非区域管理员");
		}
		String id = controller.getPara("id");
		String trainName = controller.getPara("trainName");
		String trainAddress = controller.getPara("trainAddress");
		String trainDate = controller.getPara("trainDate");
		String trainContent = controller.getPara("trainContent");
		String trainMessage = controller.getPara("trainMessage");
		String userName = optMgr.userName;
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		if(Utils.isEmpty(id)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "id错误");
		}
		if(Utils.isEmpty(trainName)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目名称填写错误");
		}
		if(Utils.isEmpty(trainAddress)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目地点填写错误");
		}
		if(Utils.isEmpty(trainDate)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目起始时间填写错误");
		}
		if(Utils.isEmpty(trainContent)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目内容填写错误");
		}
		/*if(Utils.isEmpty(trainMessage)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "培训项目备注填写错误");
		}*/
		Record record = new Record();
		record.set(TrainCourse.COL_ID, id);
		record.set(TrainCourse.COL_TRAIN_PROJECT, trainName);
		record.set(TrainCourse.COL_REGION_ID, regionId);
		record.set(TrainCourse.COL_MODIFY, userName);
		record.set(TrainCourse.COL_MODIFY_TIME, date);
		//新增字段
		record.set(TrainCourse.COL_DESCRIBE, trainMessage);
		record.set(TrainCourse.COL_ADDRESS, trainAddress);
		record.set(TrainCourse.COL_CONTENT, trainContent);
		record.set(TrainCourse.COL_RELEASE_DATE, trainDate);
		if (Db.update(TrainCourse.TABLE_NAME,record)) {
			return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
		}
		return new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR, "failed");
	}
	
	public static ResultInfo<Record> findTrainProject(Controller controller) {
		String id = controller.getPara("id");
		if(Utils.isEmpty(id)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "id错误");
		}
		Record record = Db.findById(TrainCourse.TABLE_NAME, id);
		if(null == record){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "未查询到此记录");
		}
		if(null != record.getTimestamp("release_date")){
			Timestamp timestamp = record.getTimestamp("release_date");
			record.set("release_date", timestamp.toString().substring(0, 10));
		}
		 return new ResultInfo<Record>(ResultCodes.RET_SUCCESS, "ok",record);
	}
	
	public static ResultInfo<List<Map<String, Object>>> findCourseApply(Controller controller) {
		String id = controller.getPara("id");
		if(Utils.isEmpty(id)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "id错误");
		}
		Record record = Db.findById(TrainCourse.TABLE_NAME, id);
		if(null == record){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "未查询到此记录");
		}
		String str = record.getStr("train_project");
		String sql = "select * from training where training_name = ?";
		List<Record> find = Db.find(sql, str);
		if(find.size() == 0){
			return new ResultInfo<>(ResultCodes.RET_DATA_NOT_EXIST, "暂无人员报名此课程");
		}
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		for(Record f:find){
			String userId = f.getInt("user_id")+"";
			Record findById = Db.findById("alipay_user", userId);
			Map<String, Object> columns = findById.getColumns();
			f.set("phone", findById.getStr("contact"));
			f.set("farmer", findById.getStr("farmer"));
			f.set("farmName", findById.getStr("farm_name"));
			f.set("idNum", findById.getStr("id_num"));
			String province = findById.getStr("province")==null?"":findById.getStr("province");
			String city = findById.getStr("city")==null?"":findById.getStr("city").equals(province)?"":findById.getStr("city");
			String district = findById.getStr("district")==null?"":findById.getStr("district");
			String addr = findById.getStr("addr")==null?"":findById.getStr("addr");
			f.set("address",province+city+district+addr);
			records.add(f.getColumns());
		}
		 return new ResultInfo<List<Map<String, Object>>>(ResultCodes.RET_SUCCESS, "ok",records);
	}
	
	public static ResultInfo<Record> pageBeanTrain(Controller controller){
		String isExcel = (String)controller.getAttr("isExcel");
		//当前页码
		if(null == controller.getPara("pageCode")){
			List<Record> rs = Db.find("select * from train_course");
			Record rd = new Record();
			rd.set("beanList", rs);
			return new ResultInfo<Record>(ResultCodes.RET_SUCCESS, "success",rd);
		}
		int pageCode = controller.getParaToInt("pageCode");
		if(pageCode==0){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "页码错误");
		}
		//总记录数
		Record count = Db.findFirst("select count(*) as count from train_course");
		int sumCount = count.getLong("count").intValue();
		//每页记录数
		int pageCodes = 20;
		//总页数
		int pages = sumCount%pageCodes==0?sumCount/pageCodes:sumCount/pageCodes+1;
		//当前页的所有bean
		List<Record> list = Db.find("select * from train_course limit ?,?",(pageCode-1)*pageCodes,pageCodes);
		int num2 = 0;
		int[] index = new int[list.size()];
		if(null != list || list.size()>0){
			for(int i=0;i<list.size();i++){
				if(null != list.get(i).getTimestamp("release_date")){
					Timestamp releaseDate = list.get(i).getTimestamp("release_date");
					list.get(i).set("release_date", releaseDate.toString().substring(0, 10));
				}
				num2 = (pageCode-1)*pageCodes+(i+1);
				index[i] = num2;
			}
		}
		if(null == list){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "未查询到此记录");
		}
		String fuzzyQuery = controller.getPara("fuzzyQuery");
		try {
			if(null != fuzzyQuery){
				fuzzyQuery = new String(controller.getPara("fuzzyQuery").getBytes("iso8859-1"),"utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			LoggerFactory.getLogger(TAG).error("remove TrainProject error: " + e.getMessage());
		}
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> copyListMap = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> excelListMap = new ArrayList<Map<String, Object>>();
		if(null != fuzzyQuery){
			list.clear();
			list = Db.find("select * from train_course");
			List ls = new ArrayList();
			for(Record r:list){
				ls.add(r.getStr("region_id"));
			}
			String sql = "select * from train_course where train_project like ?";
			List<Record> find = Db.find(sql, "%"+fuzzyQuery+"%");
			List<String> fds = new ArrayList<String>();
			for(Record r:find){
				if(ls.contains(r.getStr("region_id"))){
					fds.add(r.getStr("train_project"));
				}
			}
			String userTrainingSql = "select * from training where training_name = ?";
			for(String s:fds){
				List<Record> find2 = Db.find(userTrainingSql,s);
				for(Record f:find2){
					String userId = f.getInt("user_id")+"";
					Record findById = Db.findById("alipay_user", userId);
					Map<String, Object> columns = findById.getColumns();
					f.set("phone", findById.getStr("contact"));
					f.set("farmer", findById.getStr("farmer"));
					f.set("farmName", findById.getStr("farm_name"));
					f.set("idNum", findById.getStr("id_num"));
					String province = findById.getStr("province")==null?"":findById.getStr("province");
					String city = findById.getStr("city")==null?"":findById.getStr("city").equals(province)?"":findById.getStr("city");
					String district = findById.getStr("district")==null?"":findById.getStr("district");
					String addr = findById.getStr("addr")==null?"":findById.getStr("addr");
					f.set("address",province+city+district+addr);
					listMap.add(f.getColumns());
				}
			}
			for(int i=0;i<listMap.size();i++){
				excelListMap.add(listMap.get(i));
			}
			sumCount = listMap.size();
			pages = sumCount%pageCodes==0?sumCount/pageCodes:sumCount/pageCodes+1;
			int init = (pageCode-1)*pageCodes;
			for(int i=init;i<(init+pageCodes);i++){
				try{
					copyListMap.add(listMap.get(i));
				}catch(Exception e){
					break;
				}
			}
			listMap = copyListMap;
		}
		Record record = new Record();
		record.set("beanList", list);
		if(null != fuzzyQuery){
			record.set("beanList",listMap);
			if(!StringUtils.isEmpty(isExcel)){
				record.set("beanList",excelListMap);
			}
		}
		record.set("pages", pages);
		record.set("pageCodes", pageCodes);
		record.set("sumCount", sumCount);
		record.set("pageCode", pageCode);
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if(optMgr.type != 4){
			return new ResultInfo<Record>(ResultCodes.RET_SUCCESS, "ok",record);
		}
		String regionId = optMgr.regionId;
		if(StringUtils.isEmpty(regionId)){
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "非区域管理员");
		}
		AlipayUserModel alipayUser = new AlipayUserModel();
		Utils.getAddress(regionId, alipayUser);
		int num = 0;
		if(!StringUtils.isEmpty(alipayUser.getProvince())){
			num = 1;
		}
		if(!StringUtils.isEmpty(alipayUser.getCity())){
			num = 2;
		}
		if(!StringUtils.isEmpty(alipayUser.getDistrict())){
			num = 3;
		}
		if(num == 1){
			List list2 = new ArrayList();
			List<Record> list3 = new ArrayList<Record>();
			String sql = "select * from region where region_id = ?";
			Record findFirst = Db.findFirst(sql, regionId);
			String code = findFirst.getInt("region_code")+"";
			String sql2 = "select * from region where parent_id = ?";
			List<Record> find = Db.find(sql2, code);
			list2.add(regionId);
			for(Record r:find){
				String sql3 = "select * from region where parent_id = ?";
				List<Record> find2 = Db.find(sql3,r.getInt("region_code")+"");
				list2.add(r.getInt("region_id")+"");
				if(find2.size() == 0){
					continue;
				}
				for(Record r2:find2){
					list2.add(r2.getInt("region_id")+"");
				}
			}
			String listSql = "select * from train_course where region_id = ?";
			for(int i=0;i<list2.size();i++){
				List<Record> findFirst2 = Db.find(listSql, list2.get(i));
				list3.addAll(findFirst2);
			}
			for(Record r:list3){
				if(null != r.getTimestamp("release_date")){
					Timestamp releaseDate = r.getTimestamp("release_date");
					r.set("release_date", releaseDate.toString().substring(0, 10));
				}
			}
			int sumCount2 = list3.size();
			int pages2 = sumCount2%pageCodes==0?sumCount2/pageCodes:sumCount2/pageCodes+1;
			if(null != fuzzyQuery){
				listMap.clear();
				copyListMap = new ArrayList<Map<String, Object>>();
				excelListMap = new ArrayList<Map<String, Object>>();
				List ls = new ArrayList();
				for(Record r:list3){
					ls.add(r.getStr("region_id"));
				}
				String fuzzyQuerySql = "select * from train_course where train_project like ?";
				List<Record> fuzzyQuerys = Db.find(fuzzyQuerySql, "%"+fuzzyQuery+"%");
				List<String> fds = new ArrayList<String>();
				for(Record r:fuzzyQuerys){
					if(ls.contains(r.getStr("region_id"))){
						fds.add(r.getStr("train_project"));
					}
				}
				String userTrainingSql = "select * from training where training_name = ?";
				for(String s:fds){
					List<Record> find2 = Db.find(userTrainingSql,s);
					for(Record f:find2){
						String userId = f.getInt("user_id")+"";
						Record findById = Db.findById("alipay_user", userId);
						Map<String, Object> columns = findById.getColumns();
						f.set("phone", findById.getStr("contact"));
						f.set("farmer", findById.getStr("farmer"));
						f.set("farmName", findById.getStr("farm_name"));
						f.set("idNum", findById.getStr("id_num"));
						String province = findById.getStr("province")==null?"":findById.getStr("province");
						String city = findById.getStr("city")==null?"":findById.getStr("city").equals(province)?"":findById.getStr("city");
						String district = findById.getStr("district")==null?"":findById.getStr("district");
						String addr = findById.getStr("addr")==null?"":findById.getStr("addr");
						f.set("address",province+city+district+addr);					
						listMap.add(f.getColumns());
					}
				}
				for(int i=0;i<listMap.size();i++){
					excelListMap.add(listMap.get(i));
				}
				sumCount2 = listMap.size();
				pages2 = sumCount2%pageCodes==0?sumCount2/pageCodes:sumCount2/pageCodes+1;
				int init = (pageCode-1)*pageCodes;
				for(int i=init;i<(init+pageCodes);i++){
					try{
						copyListMap.add(listMap.get(i));
					}catch(Exception e){
						break;
					}
				}
				listMap = copyListMap;
			}
			record.set("pages", pages2);
			record.set("sumCount", sumCount2);
			record.set("beanList", list3);
			if(null != fuzzyQuery){
				record.set("beanList",listMap);
				if(!StringUtils.isEmpty(isExcel)){
					record.set("beanList",excelListMap);
				}
			}
		}
		if(num == 2){
			List list2 = new ArrayList();
			List<Record> list3 = new ArrayList<Record>();
			String sql = "select * from region where region_id = ?";
			Record findFirst = Db.findFirst(sql, regionId);
			String code = findFirst.getInt("region_code")+"";
			String sql2 = "select * from region where parent_id = ?";
			List<Record> find = Db.find(sql2, code);
			list2.add(regionId);
			for(Record r:find){
				String sql3 = "select * from region where parent_id = ?";
				List<Record> find2 = Db.find(sql3,r.getInt("region_code")+"");
				list2.add(r.getInt("region_id")+"");
			}
			String listSql = "select * from train_course where region_id = ?";
			for(int i=0;i<list2.size();i++){
				List<Record> findFirst2 = Db.find(listSql, list2.get(i));
				list3.addAll(findFirst2);
			}
			for(Record r:list3){
				if(null != r.getTimestamp("release_date")){
					Timestamp releaseDate = r.getTimestamp("release_date");
					r.set("release_date", releaseDate.toString().substring(0, 10));
				}
			}
			int sumCount2 = list3.size();
			int pages2 = sumCount2%pageCodes==0?sumCount2/pageCodes:sumCount2/pageCodes+1;
			if(null != fuzzyQuery){
				listMap.clear();
				copyListMap = new ArrayList<Map<String, Object>>();
				excelListMap = new ArrayList<Map<String, Object>>();
				List ls = new ArrayList();
				for(Record r:list3){
					ls.add(r.getStr("region_id"));
				}
				String fuzzyQuerySql = "select * from train_course where train_project like ?";
				List<Record> fuzzyQuerys = Db.find(fuzzyQuerySql, "%"+fuzzyQuery+"%");
				List<String> fds = new ArrayList<String>();
				for(Record r:fuzzyQuerys){
					if(ls.contains(r.getStr("region_id"))){
						fds.add(r.getStr("train_project"));
					}
				}
				String userTrainingSql = "select * from training where training_name = ?";
				for(String s:fds){
					List<Record> find2 = Db.find(userTrainingSql,s);
					for(Record f:find2){
						String userId = f.getInt("user_id")+"";
						Record findById = Db.findById("alipay_user", userId);
						Map<String, Object> columns = findById.getColumns();
						f.set("phone", findById.getStr("contact"));
						f.set("farmer", findById.getStr("farmer"));
						f.set("farmName", findById.getStr("farm_name"));
						f.set("idNum", findById.getStr("id_num"));
						String province = findById.getStr("province")==null?"":findById.getStr("province");
						String city = findById.getStr("city")==null?"":findById.getStr("city").equals(province)?"":findById.getStr("city");
						String district = findById.getStr("district")==null?"":findById.getStr("district");
						String addr = findById.getStr("addr")==null?"":findById.getStr("addr");
						f.set("address",province+city+district+addr);					
						listMap.add(f.getColumns());
					}
				}
				for(int i=0;i<listMap.size();i++){
					excelListMap.add(listMap.get(i));
				}
				sumCount2 = listMap.size();
				pages2 = sumCount2%pageCodes==0?sumCount2/pageCodes:sumCount2/pageCodes+1;
				int init = (pageCode-1)*pageCodes;
				for(int i=init;i<(init+pageCodes);i++){
					try{
						copyListMap.add(listMap.get(i));
					}catch(Exception e){
						break;
					}
				}
				listMap = copyListMap;
			}
			record.set("pages", pages2);
			record.set("sumCount", sumCount2);
			record.set("beanList", list3);
			if(null != fuzzyQuery){
				record.set("beanList",listMap);
				if(!StringUtils.isEmpty(isExcel)){
					record.set("beanList",excelListMap);
				}
			}
		}
		if(num == 3){
			String listSql = "select * from train_course where region_id = ?";
			List<Record> findFirst2 = Db.find(listSql,regionId);
			for(Record r:findFirst2){
				if(null != r.getTimestamp("release_date")){
					Timestamp releaseDate = r.getTimestamp("release_date");
					r.set("release_date", releaseDate.toString().substring(0, 10));
				}
			}
			int sumCount2 = findFirst2.size();
			int pages2 = sumCount2%pageCodes==0?sumCount2/pageCodes:sumCount2/pageCodes+1;
			if(null != fuzzyQuery){
				listMap.clear();
				copyListMap = new ArrayList<Map<String, Object>>();
				excelListMap = new ArrayList<Map<String, Object>>();
				List ls = new ArrayList();
				for(Record r:findFirst2){
					ls.add(r.getStr("region_id"));
				}
				String fuzzyQuerySql = "select * from train_course where train_project like ?";
				List<Record> fuzzyQuerys = Db.find(fuzzyQuerySql, "%"+fuzzyQuery+"%");
				List<String> fds = new ArrayList<String>();
				for(Record r:fuzzyQuerys){
					if(ls.contains(r.getStr("region_id"))){
						fds.add(r.getStr("train_project"));
					}
				}
				String userTrainingSql = "select * from training where training_name = ?";
				for(String s:fds){
					List<Record> find2 = Db.find(userTrainingSql,s);
					for(Record f:find2){
						String userId = f.getInt("user_id")+"";
						Record findById = Db.findById("alipay_user", userId);
						Map<String, Object> columns = findById.getColumns();
						f.set("phone", findById.getStr("contact"));
						f.set("farmer", findById.getStr("farmer"));
						f.set("farmName", findById.getStr("farm_name"));
						f.set("idNum", findById.getStr("id_num"));
						String province = findById.getStr("province")==null?"":findById.getStr("province");
						String city = findById.getStr("city")==null?"":findById.getStr("city").equals(province)?"":findById.getStr("city");
						String district = findById.getStr("district")==null?"":findById.getStr("district");
						String addr = findById.getStr("addr")==null?"":findById.getStr("addr");
						f.set("address",province+city+district+addr);					
						listMap.add(f.getColumns());
					}
				}
				for(int i=0;i<listMap.size();i++){
					excelListMap.add(listMap.get(i));
				}
				sumCount2 = listMap.size();
				pages2 = sumCount2%pageCodes==0?sumCount2/pageCodes:sumCount2/pageCodes+1;
				int init = (pageCode-1)*pageCodes;
				for(int i=init;i<(init+pageCodes);i++){
					try{
						copyListMap.add(listMap.get(i));
					}catch(Exception e){
						break;
					}
				}
				listMap = copyListMap;
			}
			record.set("beanList", findFirst2);
			record.set("pages", pages2);
			record.set("sumCount", sumCount2);
			if(null != fuzzyQuery){
				record.set("beanList",listMap);
				if(!StringUtils.isEmpty(isExcel)){
					record.set("beanList",excelListMap);
				}
			}
		}
		return new ResultInfo<Record>(ResultCodes.RET_SUCCESS, "ok",record);
	}
	public static File excelOut(Controller controller) throws Exception{
		controller.setAttr("isExcel", "1");
		ResultInfo<Record> pageBeanTrain = pageBeanTrain(controller);
		Record result = pageBeanTrain.result;
		List<Map<String, Object>> excelListMap = result.get("beanList");
		for(int i=0;i<excelListMap.size();i++){
			excelListMap.get(i).put("index", i+1);
		}
		String fileName = "我要培训留言列表"+".xls";
		File excelFile = new File(fileName);
		// 文件格式
		WritableCellFormat format = new WritableCellFormat();
		format.setAlignment(Alignment.CENTRE);
		// 创建一个工作文件
		WritableWorkbook writableWorkbook = Workbook.createWorkbook(excelFile);
		// 创建一个工作簿
		WritableSheet sheet1 = writableWorkbook.createSheet("sheet1", 0);
		sheet1.setColumnView(0, 35);
		sheet1.setColumnView(1, 35);
		sheet1.setColumnView(2, 35);
		sheet1.setColumnView(3, 35);
		sheet1.setColumnView(4, 35);
		sheet1.setColumnView(5, 35);
		sheet1.setColumnView(6, 35);
		sheet1.addCell(new Label(0, 0, "序号", format));
		sheet1.addCell(new Label(1, 0, "课程名称", format));
		sheet1.addCell(new Label(2, 0, "姓名", format));
		sheet1.addCell(new Label(3, 0, "身份证号码", format));
		sheet1.addCell(new Label(4, 0, "经营主体名称", format));
		sheet1.addCell(new Label(5, 0, "地址", format));
		sheet1.addCell(new Label(6, 0, "电话号码", format));
		if(excelListMap.size() != 0){
			for(int i=0;i<excelListMap.size();i++){
				int column = 0;
				Map<String, Object> map = excelListMap.get(i);
				sheet1.setColumnView(column, 35);
				sheet1.addCell(new Label(column,i+1,map.get("index").toString(), format));
				sheet1.setColumnView(column+=1, 35);
				sheet1.addCell(new Label(column,i+1,map.get("training_name").toString(), format));
				sheet1.setColumnView(column+=1, 35);
				sheet1.addCell(new Label(column,i+1,map.get("farmer").toString(), format));
				sheet1.setColumnView(column+=1, 35);
				sheet1.addCell(new Label(column,i+1,map.get("idNum").toString(), format));
				sheet1.setColumnView(column+=1, 35);
				sheet1.addCell(new Label(column,i+1,map.get("farmName").toString(), format));
				sheet1.setColumnView(column+=1, 35);
				sheet1.addCell(new Label(column,i+1,map.get("address").toString(), format));
				sheet1.setColumnView(column+=1, 35);
				sheet1.addCell(new Label(column,i+1,map.get("phone").toString(), format));
			}
		}
		writableWorkbook.write();
		writableWorkbook.close();
		return excelFile;
	}
}