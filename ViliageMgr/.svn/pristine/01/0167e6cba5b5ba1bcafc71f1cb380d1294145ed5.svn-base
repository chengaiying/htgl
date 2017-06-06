package com.moa.mgr.service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.common.Utils;
import com.moa.mgr.db.Tables.TFormData;
import com.moa.mgr.db.Tables.TRegion;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.service.FormManService.FormDataListInfo;
import com.moa.mgr.service.form.FormDef;
import com.moa.mgr.service.form.FormDefLoader;
import com.moa.mgr.service.form.WordDef;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;

public class CountReportService{
	private static final String TAG = "FormManService";
	
	public static ResultInfo<Map<String,String>> provinceRegisterNumber(Controller controller) {
		//查询数据库目前存储的所有省份
		String sql = "select distinct province from alipay_user";
		Map<String,String> map = new HashMap<String,String>();
		List<Record> records = Db.find(sql);
		if(null == records){
			return new ResultInfo<>(ResultCodes.RET_DATA_NOT_EXIST, "省份数据不存在");
		}
		String[] province = new String[records.size()];
		for(int i=0;i<records.size();i++){
			String tmp = records.get(i).get("province");
			String tmpTrim = tmp.trim();
			if(Utils.isEmpty(tmpTrim)){
				continue;
			}
			province[i] = records.get(i).get("province");
		}
		//查询每个省份对应的农场主数量
		for(int i=0;i<province.length;i++){
			String provinceNumberSql = "select count(*) as count from alipay_user where province = ?";
			if(null != province[i]){
				int num = Db.findFirst(provinceNumberSql, province[i]).getLong("count").intValue();
				map.put(province[i].replaceAll("省", "").replaceAll("市", ""),num+"");
			}
		}
		return new ResultInfo<Map<String,String>>(ResultCodes.RET_SUCCESS, "ok",map);
	}
	
	public static ResultInfo<Map<String,String>> RegisterType(Controller controller){
		Map<String,String> map = new HashMap<String,String>();
		String sql = "select count(*) as count from alipay_user where is_hzs = ?";
		//家庭农场注册数量
		int user = Db.findFirst(sql, 0).getLong("count").intValue();
		//合作社注册数量
		int hzs = Db.findFirst(sql, 1).getLong("count").intValue();
		map.put("农场主注册数量", user+"");
		map.put("合作社注册数量", hzs+"");
		return new ResultInfo<Map<String,String>>(ResultCodes.RET_SUCCESS, "ok",map);
	}
	
	public static ResultInfo<Map<Integer,Integer>> userAge(Controller controller){
		Map<Integer,Integer> map = null;
		SimpleDateFormat sim = new SimpleDateFormat("yyyy");
		//得到当前年份
		String thisDate = sim.format(new Date());
		map = new TreeMap<Integer,Integer>();
		//设置起始年龄
		int init = 19;
		//设置目标年龄
		int end = 83;
		for(int i=init;i<=end;i++){
			map.put(i, 0);
		}
		String sql = "select id_num from alipay_user";
		List<Record> list = Db.find(sql);
		//根据身份证得到农场主年龄并加入统计
		for(Record r:list){
			String a = r.getStr("id_num");
			if(StringUtils.isEmpty(r.getStr("id_num")) || r.getStr("id_num").length() != 18){
				continue;
			}
			String str = r.getStr("id_num").substring(6,10);
			int result = 0;
			try{
				result = Integer.parseInt(str) - Integer.parseInt(thisDate);
			}catch(Exception e){
				LoggerFactory.getLogger(TAG).error("userAge error:" + e.getMessage());
				continue;
			}
			result = result - result - result;
			Set<Integer> keySet = map.keySet();
			int i = 0;
			for(Integer s:keySet){
				if(result == s){
					int value = map.get(s).intValue()+1;
					map.put(s,value);
					continue;
				}
			}
		}
		return new ResultInfo<Map<Integer,Integer>>(ResultCodes.RET_SUCCESS, "ok",map);
	}
	public static ResultInfo<Map<String,Map<String,Integer>>> trainCount(Controller controller){
		Map<String,Map<String,Integer>> map = new HashMap<String,Map<String,Integer>>();
		//查询所有课程
		List<Record> find = Db.find("select * from train_course");
		for(Record f:find){
			String trainProject = f.getStr("train_project");
			//查询课程报名总数
			List<Record> find2 = Db.find("select user_id from training where training_name = ?", trainProject);
			Map<String,Integer> values = new HashMap<String,Integer>();
			values.put("农场主总数量",find2.size());
			values.put("家庭农场总数量",0);
			values.put("合作社总数量",0);
			//区分家庭农场及合作社
			for(Record ff:find2){
				String userId = ff.getInt("user_id")+"";
				Record findById = Db.findById("alipay_user", userId);
				String isHzs = findById.getStr("is_hzs");
				if("0".equals(isHzs)){
					values.put("家庭农场总数量",values.get("家庭农场总数量").intValue()+1);
				}else{
					values.put("合作社总数量",values.get("合作社总数量").intValue()+1);
				}
			}
			map.put(trainProject, values);
		}
		return new ResultInfo<Map<String,Map<String,Integer>>>(ResultCodes.RET_SUCCESS, "ok",map);
	}
	
	public static ResultInfo<Map<String,String>> educationCount(Controller controller){
		// 获取系统用户缓存
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		// 省-市-县对应的数据
		String regionId = optMgr.regionId;
		int type = optMgr.type;
		String provice = "";
		String county = "";
		String city = "";
		if (optMgr.type == 4) {
			Record regionRecord = Db.findFirst(
					"select * from region where region_id =?", optMgr.regionId);
			// 判断regionName是省-市-县（区）
			if (!regionRecord.get(TRegion.COL_PARENT_ID).toString().trim()
					.equals("0")) {
				// 获取市
				Record cityRecord = Db.findFirst(
						"select * from region where region_code =?",
						regionRecord.get(TRegion.COL_PARENT_ID));
				if (!cityRecord.get(TRegion.COL_PARENT_ID).toString().trim()
						.equals("0")) {
					// 获取区或者县
					Record countyRecord = Db.findFirst(
							"select * from region where region_code =?",
							cityRecord.get(TRegion.COL_PARENT_ID));
					provice = countyRecord.get(TRegion.COL_REGION_NAME)
							.toString();
					city = cityRecord.get(TRegion.COL_REGION_NAME).toString();
					county = regionRecord.get(TRegion.COL_REGION_NAME)
							.toString();
				} else {
					city = regionRecord.get(TRegion.COL_REGION_NAME).toString();
					provice = cityRecord.get(TRegion.COL_REGION_NAME)
							.toString();
				}
			} else {
				provice = regionRecord.get(TRegion.COL_REGION_NAME).toString();
			}
		}
		int formId = 1;
		FormDataListInfo info = new FormDataListInfo();
		List<Record> recordList = null;
		if (type == 4) {
			List<Object> param = new ArrayList<>();
			param.add(formId);
			if (!StringUtils.isEmpty(county)) {
				param.add(county);
			}
			if (!StringUtils.isEmpty(city)) {
				param.add(city);
			}
			if (!StringUtils.isEmpty(provice)) {
				param.add(provice);
			}
			param.add("1");
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select farmer.farmer, form.* from(\n");
			if (type == 4) {
				sqlBuilder.append("select * from form_data where form_id=?\n");
				sqlBuilder
						.append("and user_id in (select id from alipay_user where \n 1=1");
				if (!StringUtils.isEmpty(county)) {
					sqlBuilder.append("\nand  district=?\n");
				}
				if (!StringUtils.isEmpty(city)) {
					sqlBuilder.append("\nand city=?\n");
				}
				if (!StringUtils.isEmpty(provice)) {
					sqlBuilder.append("\nand province=?\n");
				}
				sqlBuilder.append(") and is_last=? \n");
			}
			sqlBuilder
					.append(") form left join alipay_user farmer on farmer.alipay_user_id=form.alipay_user_id");
			String sql = sqlBuilder.toString();
			recordList = Db.find(sql, param.toArray());
		} else {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select farmer.farmer, form.* from(\n");
			sqlBuilder
					.append("select * from form_data where form_id=? and is_last=? \n");
			sqlBuilder
					.append(") form left join alipay_user farmer on farmer.alipay_user_id=form.alipay_user_id");
			String sql = sqlBuilder.toString();
			recordList = Db.find(sql, formId, "1");
		}
		FormDef def = FormDefLoader.getInstance().findForm(formId);
		WordDef[] formWords = def.getWordDefs();
		if (recordList.size() == 0) {
			return null;
		}
		//设置学历各区间段信息
		Map<String,Integer> m = new HashMap<String,Integer>();
		m.put("初中及以下", 0);
		m.put("高中中专", 0);
		m.put("大专本科", 0);
		m.put("研究生及以上", 0);
		for (Record record : recordList) {
			List<String> strList = new ArrayList<String>();
			record.remove(TFormData.COL_ALIPAY_USER_ID, TFormData.COL_ID,
					TFormData.COL_IS_LAST, TFormData.COL_FORM_ID);
			HashMap<String, Object> map = new LinkedHashMap<>();
			String farmer = record.getStr("farmer");
			if (Utils.isEmpty(farmer)) {
				farmer = "未认证农户";
			}
			map.put("farmer", farmer);
			map.put("date", record.getStr("date"));
			for (int i = 0; i < formWords.length; i++) {
				if (formWords[i] != null) {
					String s = record.getStr(TFormData.COL_DATA_ARRAY[i]);
					if (s == null) {
						s = "";
					}
					strList.add(s);
				}
			}
			map.put("coreData", strList);
			info.datas.add(map);
			Set<String> keySet = m.keySet();
			for(String s:keySet){
				if(s.equals(strList.get(6).replaceAll("/", ""))){
					m.put(s, m.get(s)+1);
				}
			}
		}
		NumberFormat format = NumberFormat.getPercentInstance();
	    format.setMinimumFractionDigits(2);
		Integer sumInteger = m.get("初中及以下")+m.get("高中中专")+m.get("大专本科")+m.get("研究生及以上");
		int sum = sumInteger.intValue();
		String sql = "select count(*) as count from alipay_user";
		int count = Db.findFirst(sql).getLong("count").intValue();
		int noEducationCount = count - sum;
		m.put("无教育经历", noEducationCount);
		//统计各学历区间段人数及比例信息
		Map<String,String> map = new HashMap<String,String>();
		map.put("初中及以下", m.get("初中及以下")+"");
		map.put("高中中专", m.get("高中中专")+"");
		map.put("大专本科", m.get("大专本科")+"");
		map.put("研究生及以上", m.get("研究生及以上")+"");
		map.put("无教育经历", m.get("无教育经历")+"");
		return new ResultInfo<Map<String,String>>(ResultCodes.RET_SUCCESS, "ok",map);
	}
}
