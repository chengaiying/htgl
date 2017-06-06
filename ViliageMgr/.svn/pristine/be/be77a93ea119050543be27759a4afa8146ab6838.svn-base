package com.moa.mgr.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.http.util.TextUtils;
import org.slf4j.LoggerFactory;
import com.alibaba.druid.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.WebConfig4Mgr;
import com.moa.mgr.common.Utils;
import com.moa.mgr.db.Tables.TFormData;
import com.moa.mgr.db.Tables.TFormDef;
import com.moa.mgr.db.Tables.TRegion;
import com.moa.mgr.model.AlipayUserModel;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.service.form.FormDef;
import com.moa.mgr.service.form.FormDefLoader;
import com.moa.mgr.service.form.WordDef;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;
import com.moa.mgr.service.manager.Permission;

public class FormManService {

	private static final String TAG = "FormManService";

	private static final String PARAM_FORM_ID = "form_id";

	private static final String PARAM_PAGE = "page";

	private static final int NUM_PER_PAGE = 50;

	// private static final String FARMER = "farmer";
	// private static final String FARM_NAME = "farmName";

	public static ResultInfo<List<FarmerInfo>> formInfo(Controller controller) {
		// String farmer = controller.getPara(FARMER);
		// String farmName = controller.getPara(FARM_NAME);
		int page = controller.getParaToInt(PARAM_PAGE);
		List<FarmerInfo> result = new ArrayList<>();
		// 直报表单类型
		List<Map<String, Object>> formList = FormDefLoader.getInstance().simpleFormList;
		for (Map<String, Object> map : formList) {
			int formId = (int) map.get("id");
			FormDef def = FormDefLoader.getInstance().findForm(formId);
			FormDataListInfo dataListInfo = getFormData(formId, page);
			FarmerInfo info = new FarmerInfo();
			info.setFormType(map);
			info.setDef(def);
			info.setListInfo(dataListInfo);
			result.add(info);
		}
		return new ResultInfo<List<FarmerInfo>>(ResultCodes.RET_SUCCESS, "ok",
				result);
	}

	public static ResultInfo formCount(Controller controller) {
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
		int formId = -1;
		try {
			formId = controller.getParaToInt(PARAM_FORM_ID);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("form id formate error");
		}
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
		if (recordList.size() > 0) {
			return new ResultInfo(1, "no", recordList.size());
		} else {
			return new ResultInfo(0, "ok");
		}
	}

	public static File outExcel(Controller controller) throws Exception {
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
		int formId = -1;
		try {
			formId = controller.getParaToInt(PARAM_FORM_ID);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("form id formate error");
		}
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
		List list = new ArrayList();
		List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
		int num = 0;
		String fileName = "新农直报_" + def.formName + ".xls";
		File excelFile = new File(fileName);
		// 文件格式
		WritableCellFormat format = new WritableCellFormat();
		format.setAlignment(Alignment.CENTRE);
		/*
		 * // x 靠左 format.setAlignment(Alignment.RIGHT); // y 靠顶
		 * format.setVerticalAlignment(VerticalAlignment.TOP);
		 */
		// 创建一个工作文件
		WritableWorkbook writableWorkbook = Workbook.createWorkbook(excelFile);
		// 创建一个工作簿
		WritableSheet sheet1 = writableWorkbook.createSheet("sheet1", 0);
		if (recordList.size() == 0) {
			return null;
		}
		for (Record record : recordList) {
			Map<String, String> map2 = new LinkedHashMap<String, String>();
			record.remove(TFormData.COL_ALIPAY_USER_ID, TFormData.COL_ID,
					TFormData.COL_IS_LAST, TFormData.COL_FORM_ID);
			HashMap<String, Object> map = new LinkedHashMap<>();
			String farmer = record.getStr("farmer");
			if (Utils.isEmpty(farmer)) {
				farmer = "未认证农户";
			}
			map2.put("序号", "" + (num + 1));
			map2.put("农场主", farmer);
			num++;
			for (int i = 0; i < formWords.length; i++) {
				if (formWords[i] != null) {
					String s = record.getStr(TFormData.COL_DATA_ARRAY[i]);
					if (s == null) {
						s = "";
					}
					if (formWords[i] == null) {
						continue;
					}
					formWords[i].value = s;
				}
			}
			List<WordDef> words = new ArrayList<WordDef>();
			for (int i = 0; i < formWords.length; i++) {
				if (formWords[i] != null) {
					WordDef word = new WordDef("", 0, "");
					word.display = formWords[i].display;
					word.value = formWords[i].value;
					words.add(word);
				}
			}
			list.add(words);
			list2.add(map2);
		}
		if (list.size() > 0) {
			List<WordDef> listWordDefs = (List<WordDef>) list.get(0);
			sheet1.setColumnView(0, 35);
			sheet1.setColumnView(1, 35);
			sheet1.addCell(new Label(0, 0, "序号", format));
			sheet1.addCell(new Label(1, 0, "农场主", format));
			int number = 2;
			for (int i = 0; i < listWordDefs.size(); i++) {
				sheet1.setColumnView(number, 35);
				sheet1.addCell(new Label(number, 0, listWordDefs.get(i).display
						.replaceAll("&nbsp", ""), format));
				number++;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			List<WordDef> listWordDefs = (List<WordDef>) list.get(i);
			sheet1.setColumnView(0, 35);
			sheet1.setColumnView(1, 35);
			sheet1.addCell(new Label(0, i + 1, list2.get(i).get("序号"), format));
			sheet1.addCell(new Label(1, i + 1, list2.get(i).get("农场主"), format));
			int number = 2;
			for (int b = 0; b < listWordDefs.size(); b++) {
				sheet1.setColumnView(number, 35);
				sheet1.addCell(new Label(number, i + 1,
						listWordDefs.get(b).value.replaceAll("&nbsp", ""),
						format));
				number++;
			}
		}
		writableWorkbook.write();
		writableWorkbook.close();
		return excelFile;
	}

	private static FormDataListInfo getFormData(int formId, int page) {
		FormDataListInfo info = new FormDataListInfo();
		try {
			info.page = page;
			if (info.page < 1) {
				info.page = 1;
			}
		} catch (Exception e) {
			info.page = 1;
		}

		String sql = "select farmer.farmer, form.* from(\n"
				+ "select * from form_data where form_id=? and is_last=? limit ?, ?\n"
				+ ") form left join alipay_user farmer on farmer.alipay_user_id=form.alipay_user_id";

		Record countRecord = Db
				.findFirst(
						"select count(1) count from form_data where form_id=? and is_last=?",
						formId, "1");
		long count = countRecord.getLong("count");
		info.totalPage = (int) (count / NUM_PER_PAGE + (count % NUM_PER_PAGE == 0 ? 0
				: 1));

		List<Record> recordList = Db.find(sql, formId, "1", (info.page - 1)
				* NUM_PER_PAGE, (NUM_PER_PAGE + 1));

		FormDef def = FormDefLoader.getInstance().findForm(formId);
		WordDef[] formWords = def.getWordDefs();

		info.hasMorePage = recordList.size() <= NUM_PER_PAGE ? 0 : 1;
		if (recordList.size() > NUM_PER_PAGE) {
			recordList.remove(NUM_PER_PAGE);
		}
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
		}
		return info;
	}

	public static class FarmerInfo {
		private Map<String, Object> formType;
		private FormDef def;
		private FormDataListInfo listInfo;

		public Map<String, Object> getFormType() {
			return formType;
		}

		public void setFormType(Map<String, Object> formType) {
			this.formType = formType;
		}

		public FormDef getDef() {
			return def;
		}

		public void setDef(FormDef def) {
			this.def = def;
		}

		public FormDataListInfo getListInfo() {
			return listInfo;
		}

		public void setListInfo(FormDataListInfo listInfo) {
			this.listInfo = listInfo;
		}
	}

	public static ResultInfo<FormDataListInfo> formData(Controller controller) {
		// 获取系统用户缓存
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		// 省-市-县对应的数据
		String regionId = optMgr.regionId;
		int type = optMgr.type;
		AlipayUserModel model = new AlipayUserModel();
		if (optMgr.type == 4) {
			// 获取省-市-县
			Utils.getAddress(regionId, model);
		}
		int formId = -1;
		try {
			formId = controller.getParaToInt(PARAM_FORM_ID);
		} catch (Exception e) {

			LoggerFactory.getLogger(TAG).error("form id formate error");
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR);
		}

		FormDataListInfo info = new FormDataListInfo();
		try {
			info.page = controller.getParaToInt(PARAM_PAGE);
			if (info.page < 1) {
				info.page = 1;
			}
		} catch (Exception e) {
			info.page = 1;
		}
		List<Record> recordList = null;
		if (type == 4) {
			// 1-count(*)总条数查询
			List<Object> param = new ArrayList<>();
			param.add(formId);
			if (!StringUtils.isEmpty(model.getDistrict())) {
				param.add(model.getDistrict());
			}
			if (!StringUtils.isEmpty(model.getCity())) {
				param.add(model.getCity());
			}
			if (!StringUtils.isEmpty(model.getProvince())) {
				param.add(model.getProvince());
			}
			param.add("1");
			Record countRecord = Db.findFirst(getSql(model, type, 1),
					param.toArray());
			long count = countRecord.getLong("count");
			info.totalPage = (int) (count / NUM_PER_PAGE + (count
					% NUM_PER_PAGE == 0 ? 0 : 1));
			String sql = getSql(model, type, 0);
			param.add((info.page - 1) * NUM_PER_PAGE);
			param.add(NUM_PER_PAGE + 1);
			recordList = Db.find(sql, param.toArray());

		} else {

			String sql = getSql(null, type, 0);
			// 1-count(*)总条数查询
			Record countRecord = Db.findFirst(getSql(null, type, 1), formId,
					"1");
			long count = countRecord.getLong("count");
			info.totalPage = (int) (count / NUM_PER_PAGE + (count
					% NUM_PER_PAGE == 0 ? 0 : 1));

			recordList = Db.find(sql, formId, "1", (info.page - 1)
					* NUM_PER_PAGE, (NUM_PER_PAGE + 1));
		}
		if (recordList == null || recordList.size() == 0) {
			return new ResultInfo<>(ResultCodes.RET_DATA_NOT_EXIST,
					"has no data");
		}

		FormDef def = FormDefLoader.getInstance().findForm(formId);
		if (def == null) {
			return new ResultInfo<>(ResultCodes.RET_DATA_NOT_EXIST,
					"has no data");
		}
		WordDef[] formWords = def.getWordDefs();

		info.hasMorePage = recordList.size() <= NUM_PER_PAGE ? 0 : 1;
		if (recordList.size() > NUM_PER_PAGE) {
			recordList.remove(NUM_PER_PAGE);
		}
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
		}

		return new ResultInfo<FormDataListInfo>(ResultCodes.RET_SUCCESS, "ok",
				info);
	}

	public static ResultInfo<FormDef> formDefDetail(Controller controller) {
		try {
			int formId = controller.getParaToInt(PARAM_FORM_ID);
			FormDef formDef = FormDefLoader.getInstance().findForm(formId);
			if (formDef == null) {
				return new ResultInfo<FormDef>(ResultCodes.RET_DATA_NOT_EXIST);
			}
			return new ResultInfo<FormDef>(ResultCodes.RET_SUCCESS, "success",
					formDef);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error(
					"query FormDefDetail error:" + e.getMessage());
			return new ResultInfo<FormDef>(ResultCodes.RET_PARAM_ERROR,
					"form id must be a num");
		}
	}

	public static class FormDataListInfo {
		public int hasMorePage = 0;
		public int page = 1;
		public int totalPage = 0;

		public List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	}

	/**
	 * 更新表单结构，需要相应权限 该操作需要慎重，可能会造成之前数据
	 * 
	 * @param controller
	 * @return
	 */
	public static ResultInfo<Integer> updateFormDef(Controller controller) {
		ManagerInfo mgrInfo = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (!mgrInfo.checkPermission(Permission.PERMISSION_UPDATE_FORM_DEF)) {
			return new ResultInfo<Integer>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}

		LoggerFactory.getLogger(TAG).info(
				"user:" + mgrInfo.userName + " try update form def");
		String content = controller.getPara("content");
		if (TextUtils.isEmpty(content)) {
			return new ResultInfo<Integer>(ResultCodes.RET_PARAM_ERROR,
					"content cannot be empty");
		}

		FormDef def = null;
		try {
			def = Utils.fromJson(content, FormDef.class);
		} catch (Exception e) {
			return new ResultInfo<Integer>(ResultCodes.RET_PARAM_ERROR,
					"表单结构错误");
		}
		if (def == null) {
			return new ResultInfo<Integer>(ResultCodes.RET_FAILED, "表单结构错误");
		}
		if (save(def, mgrInfo)) {
			return new ResultInfo<Integer>(ResultCodes.RET_SUCCESS, "ok",
					def.id);
		} else {
			return new ResultInfo<Integer>(ResultCodes.RET_FAILED);
		}
	}

	public static ResultInfo<Integer> addFormDef(Controller controller) {
		ManagerInfo mgrInfo = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (!mgrInfo.checkPermission(Permission.PERMISSION_ADD_FORM_DEF)) {
			return new ResultInfo<Integer>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		LoggerFactory.getLogger(TAG).info(
				"user:" + mgrInfo.userName + " try add form def");
		String content = controller.getPara("content");
		if (TextUtils.isEmpty(content)) {
			return new ResultInfo<Integer>(ResultCodes.RET_PARAM_ERROR,
					"content cannot be empty");
		}

		FormDef def = null;
		try {
			def = Utils.fromJson(content, FormDef.class);
		} catch (Exception e) {
			return new ResultInfo<Integer>(ResultCodes.RET_PARAM_ERROR,
					"表单结构错误");
		}
		if (def == null) {
			return new ResultInfo<Integer>(ResultCodes.RET_FAILED, "表单结构错误");
		}

		Record newIdRec = Db
				.findFirst("select max(id)+1 as newId from form_def");
		long newId = newIdRec.getLong("newId");
		def.id = (int) newId;
		def.startDate = "2016-07-01 00:00:00";
		def.endDate = "2099-07-01 00:00:00";

		String tmpImgPath = mgrInfo.findTmpFileName("form_boot_img");
		String newPath = "";
		if (!Utils.isEmpty(tmpImgPath)) {
			File tmpBootImg = new File(tmpImgPath);
			if (tmpBootImg.exists() && tmpBootImg.isFile()) {
				String shortName = tmpBootImg.getName();
				newPath = WebConfig4Mgr.formDir + File.separator + "boot_img"
						+ File.separator + shortName;
				if (tmpBootImg.renameTo(new File(newPath))) {
					def.bootImg = "form/boot_img/" + shortName;
				} else {
					tmpBootImg.delete();
				}
			}
		}
		try {
			boolean bool = save(def, mgrInfo);
			if (bool) {
				ReloaderService
						.reload(ReloaderService.OPT_CODE_UPDATE_FORM_DEF);
				return new ResultInfo<Integer>(ResultCodes.RET_SUCCESS, "ok",
						def.id);
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error(
					"add form def error:" + e.getMessage());
		}
		Utils.deleteFileByName(newPath);
		return new ResultInfo<Integer>(ResultCodes.RET_FAILED);
	}

	private static boolean save(FormDef def, ManagerInfo mgr) {
		Record record = new Record();
		record.set(TFormDef.COL_ID, def.id);
		record.set(TFormDef.COL_FORM_NAME, def.formName);
		record.set(TFormDef.COL_ICON, def.icon);
		record.set(TFormDef.COL_BOOT_IMG, def.bootImg);
		record.set(TFormDef.COL_FIT_FARMER, def.fitFarmer);
		record.set(TFormDef.COL_START_DATE, def.startDate);
		record.set(TFormDef.COL_END_DATE, def.endDate);
		record.set(TFormDef.COL_FORM_DESC, def.formDesc);
		record.set(TFormDef.COL_WORDS_DEF, Utils.toJson(def.getWordDefs()));
		record.set(TFormDef.COL_FLAG, def.flag);
		record.set(TFormDef.COL_SEQ, def.seq);
		// form_def中新增modify_time（修改时间），modify(修改人)字段
		record.set(TFormDef.COL_MODIFY_TIME,
				Timestamp.valueOf(Utils.getCurrentTimeStr()));
		record.set(TFormDef.COL_MODIFY, mgr.userName);
		try {
			Db.deleteById(TFormDef.TABLE_NAME, TFormDef.COL_ID, def.id);
			Db.save(TFormDef.TABLE_NAME, record);
			FormDefLoader.getInstance().loadFormDefs(); // 为了排序，要重新加载一次数据，败笔
			return true;
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error(
					"save form def error:" + e.getMessage());
			return false;
		}
	}

	public static ResultInfo<Integer> removeFormDef(Controller controller) {
		ManagerInfo mgrInfo = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (!mgrInfo.checkPermission(Permission.PERMISSION_ADD_FORM_DEF)) {
			return new ResultInfo<Integer>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		LoggerFactory.getLogger(TAG).info(
				"user:" + mgrInfo.userName + " try remove form def");
		Db.deleteById("form_def", controller.getPara("form_id"));
		return new ResultInfo<Integer>(ResultCodes.RET_SUCCESS, "ok");
	}

	private static String getSql(AlipayUserModel model, int type, int count) {
		StringBuilder sql = new StringBuilder();
		if (count == 1) {
			sql.append("select count(*) as count from(\n");
		} else {

			sql.append("select farmer.farmer, form.* from(\n");
		}
		if (type == 4) {
			sql.append("select * from form_data where form_id=?\n");
			sql.append("and user_id in (select id from alipay_user where \n 1=1");
			if (!StringUtils.isEmpty(model.getDistrict())) {
				sql.append("\nand  district=?\n");
			}
			if (!StringUtils.isEmpty(model.getCity())) {
				sql.append("\nand city=?\n");
			}
			if (!StringUtils.isEmpty(model.getProvince())) {
				sql.append("\nand province=?\n");
			}
			if (count == 1) {
				sql.append(") and is_last=?\n");
			} else {
				sql.append(") and is_last=? limit ?, ?\n");
			}
		} else {
			if (count == 1) {
				sql.append("select * from form_data where form_id=? and is_last=?\n");
			} else {
				sql.append("select * from form_data where form_id=? and is_last=? limit ?, ?\n");
			}
		}
		sql.append(") form left join alipay_user farmer on farmer.alipay_user_id=form.alipay_user_id");
		return sql.toString();
	}
}
