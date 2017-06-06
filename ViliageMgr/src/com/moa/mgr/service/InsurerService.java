package com.moa.mgr.service;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.WebConfig4Mgr;
import com.moa.mgr.common.Utils;
import com.moa.mgr.db.Tables.INSApply;
import com.moa.mgr.db.Tables.INSProd;
import com.moa.mgr.db.Tables.TFormData;
import com.moa.mgr.db.Tables.TRegion;
import com.moa.mgr.db.Tables.Tinsurer;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.insurer.InsApplyResult;
import com.moa.mgr.service.insurer.Insurer;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;
import com.moa.mgr.service.manager.Permission;
import com.moa.mgr.service.form.FormDef;
import com.moa.mgr.service.form.FormDefLoader;
import com.moa.mgr.service.form.WordDef;


public class InsurerService {
	
	public static final int NUM_PER_PAGE = 50;
	private static final String PARAM_APPLY_ID = "apply_id";
	private static final String PARAM_INS_STATUS = "ins_status";
	
	private static final String TAG = "InsurerService";
	public static ResultInfo<Insurer> addInsurer(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (! optMgr.checkPermission(Permission.PERMISSION_ADD_INSURANCE)) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		
		String insurerName = controller.getPara(Tinsurer.COL_INSURER_NAME);
		if (Utils.isEmpty(insurerName)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "保险公司名字不能为空");
		}
		
		String tmpFileName = optMgr.findTmpFileName(Tinsurer.COL_INSURER_ICON);
	    if (Utils.isEmpty(tmpFileName)) {
	      return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "请重新上传保险公司图标");
	    }
	    File tmpFile = new File(tmpFileName);
	    if (! tmpFile.isFile()) {
	      return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "请重新上传保险公司图标");
	    }

	    String newFileName =  tmpFile.getName();
	    String newFilePath = WebConfig4Mgr.insurerDir + File.separator  + newFileName;
	    //WebConfig4Mgr.insurerDir为空，文件重命名错误
	    if (! tmpFile.renameTo(new File(newFilePath))) {
	      return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "请重新上传保险公司图标");
	    }

		Record newIdRecord = Db.findFirst("select IFNULL(max(id),0)+1 as newId from insurer");
		
		long newId=newIdRecord.getLong("newId");
		Record newInsurer = new Record();
		newInsurer.set(Tinsurer.COL_INSURER_NAME, insurerName);
		newInsurer.set(Tinsurer.COL_INSURER_ICON, "insurer/" + newFileName);
		newInsurer.set(Tinsurer.COL_ID, newId);
		newInsurer.set(Tinsurer.COL_INSURER_DESC, controller.getPara(Tinsurer.COL_INSURER_DESC));
		//创建人
		newInsurer.set(Tinsurer.COL_CREATEOR, optMgr.userName);		
		try {
			if (Db.save(Tinsurer.TABLE_NAME, newInsurer)) {
				optMgr.removeTmpFile("insurer_icon");
				Insurer insurerObj = new Insurer((int)newId,insurerName,"insurer/" + newFileName,controller.getPara(Tinsurer.COL_INSURER_DESC));
				return new ResultInfo<Insurer>(ResultCodes.RET_SUCCESS, "ok", insurerObj);
			} else {
				Utils.deleteFileByName(newFilePath);
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("add Insurer error:" + e.getMessage());
			Utils.deleteFileByName(newFilePath);
		}
		return new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR, "error");
	}
	public static ResultInfo<List<Map<String,Object>>> insurerList(Controller controller){

			List<Map<String,Object>> list=new ArrayList<>();
			List<Record> listrd=Db.find("SELECT * from insurer ORDER BY createor_time");
			for (Record record : listrd) {
				Map<String,Object> map=record.getColumns();
				list.add(map);
			}
		
			return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok", list);
		
	}
	public static ResultInfo<EmptyResultObj> deleteInsurer(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (! optMgr.checkPermission(Permission.PERMISSION_DEL_INSURANCE)) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		int insurerId = -1;
		try {
			insurerId = controller.getParaToInt(Tinsurer.COL_ID);
		} catch (Exception e) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "保险公司id填写错误");
		}
		Record newIdRecord =Db.findFirst("select insurer_icon from insurer where id=?", insurerId);
		String insurer_icon=newIdRecord.getStr("insurer_icon");
		int ct = Db.update("delete from insurer where id=?", insurerId);
		if (ct == 1) {
			if (!Utils.isEmpty(insurer_icon)) {
				Insurer.deleteIcon(insurer_icon);
			}
			List<Record> list=Db.find("select * from ins_prod WHERE ins_id=?",insurerId);
			for (Record record : list) {
				if (Utils.isEmpty(record.getStr(INSProd.COL_INSPROD_ICON))) {
					continue;
				}
				Utils.deleteFileByName(WebConfig4Mgr.rootDir + File.separator + record.getStr(INSProd.COL_INSPROD_ICON));
			}
			return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
		}
		
		return new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR, "error");
	}
	
	public static ResultInfo<List<Map<String,Object>>> insprodList(Controller controller){
		List<Map<String,Object>> list=new ArrayList<>();
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (optMgr.type == ManagerInfo.MGR_TYPE_INSURER_MGR) {
			List<Record> inslist=Db.find("SELECT * from ins_prod where  ins_id=? ORDER BY create_date DESC",optMgr.insurerId);
			for (Record record : inslist) {
				list.add(record.getColumns());
			}
			
		} else {  //如果是系统管理员，可以查看所有贷款产品
			List<Record> listrd=Db.find("SELECT * from ins_prod ORDER BY create_date DESC");
			for (Record record : listrd) {
				Map<String,Object> map=record.getColumns();
			    
				list.add(map);
			}
			
		}
	
		return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok", list);
	
}
	
	public static ResultInfo<EmptyResultObj> updateInsprod(Controller controller) {
		// 获取系统用户缓存
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(
				controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (! optMgr.checkPermission(Permission.PERMISSION_DEL_INSPROD) || optMgr.insurerId == 0) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		String id = controller.getPara("id");
		String insName = controller.getPara("insName");
		String insFeatures = controller.getPara("insFeatures");
		String fitPeople = controller.getPara("fitPeople");
		String price = controller.getPara("price");
		String claimAmount = controller.getPara("claimAmount");
		String insTerm = controller.getPara("insTerm");
		String telphone = controller.getPara("telphone");
		String email = controller.getPara("email");
		String userName = optMgr.userName;
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		if(Utils.isEmpty(id)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "id错误");
		}
		if(Utils.isEmpty(insName)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "保险产品名称填写错误");
		}
		if(Utils.isEmpty(price)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "产品价格填写错误");
		}
		if(Utils.isEmpty(claimAmount)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "保障金额填写错误");
		}
		if(Utils.isEmpty(insTerm)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "保险期限填写错误");
		}
		if(Utils.isEmpty(telphone)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "联系电话填写错误");
		}
		if(Utils.isEmpty(email)){
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "邮箱地址填写错误");
		}
		Record record = new Record();
		record.set(INSProd.COL_ID, id);
		record.set(INSProd.COL_INSPROD_NAME, insName);
		record.set(INSProd.COL_MODIFY, userName);
		record.set(INSProd.COL_MODIFY_TIME, date);
		//新增字段
		record.set(INSProd.COL_INSPROD_DESC, insFeatures);
		record.set(INSProd.COL_FITPEOPLE, fitPeople);
		record.set(INSProd.COL_INSPROD_PRICE, price);
		record.set(INSProd.COL_INSPROD_CLAIMAMOUNT, claimAmount);
		record.set(INSProd.COL_INSPROD_INSTERM, insTerm);
		record.set(INSProd.COL_TELPHONE, telphone);
		record.set(INSProd.COL_INSPROD_EMAIL, email);
		if (Db.update(INSProd.TABLE_NAME,record)) {
			return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
		}
		return new ResultInfo<>(ResultCodes.RET_UNKNOWN_ERROR, "failed");
	}
	
	public static ResultInfo<Map<String, Object>> addInsprod(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (! optMgr.checkPermission(Permission.PERMISSION_ADD_INSPROD) || optMgr.insurerId == 0) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		
		//产品名称和产品联系邮箱不能为空
		if (controller.isParaBlank(INSProd.COL_INSPROD_NAME)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "参数错误");
		}
		//查找上传过的产品图标
		String prodIconFilePath = optMgr.findTmpFileName(INSProd.COL_INSPROD_ICON);
		if (Utils.isEmpty(prodIconFilePath)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "请上传保险产品图标");
		}
		
		File tmpFile = new File(prodIconFilePath);
		if (! tmpFile.isFile()) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "请上传保险产品图标");
		}
		
		if (! tmpFile.renameTo(new File(WebConfig4Mgr.insprodDir + File.separator + tmpFile.getName()))) {
			tmpFile.delete();
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "请上传保险传产品图标");
		}
		
		Record record = new Record();
		String id = optMgr.insurerId + ":" + Utils.createUUID();
		record.set(INSProd.COL_ID, id);
		record.set(INSProd.COL_INS_ID, optMgr.insurerId);
		record.set(INSProd.COL_INSPROD_NAME, controller.getPara(INSProd.COL_INSPROD_NAME));
		record.set(INSProd.COL_INSPROD_DESC, controller.getPara(INSProd.COL_INSPROD_DESC));
		record.set(INSProd.COL_INSPROD_EMAIL, controller.getPara(INSProd.COL_INSPROD_EMAIL));
		record.set(INSProd.COL_CREATE_DATE, Utils.getCurrentTimeStr());
		record.set(INSProd.COL_INSPROD_ICON, "insprod/" + tmpFile.getName());
		record.set(INSProd.COL_STATUS, 0);
		record.set(INSProd.COL_INSPROD_PRICE, controller.getPara(INSProd.COL_INSPROD_PRICE));
		record.set(INSProd.COL_INSPROD_PRICEUNIT, controller.getPara(INSProd.COL_INSPROD_PRICEUNIT));
		record.set(INSProd.COL_INSPROD_CLAIMAMOUNT, controller.getPara(INSProd.COL_INSPROD_CLAIMAMOUNT));
		record.set(INSProd.COL_INSPROD_INSTERM, controller.getPara(INSProd.COL_INSPROD_INSTERM));
		record.set(INSProd.COL_FITPEOPLE, controller.getPara(INSProd.COL_FITPEOPLE));
		record.set(INSProd.COL_TELPHONE, controller.getPara(INSProd.COL_TELPHONE));
		record.set(INSProd.COL_INSPROD_CONTENT, controller.getPara(INSProd.COL_INSPROD_CONTENT));
		
		//创建人
		record.set(INSProd.COL_CREATEOR, optMgr.userName);
		record.set(INSProd.COL_PROVINCE, controller.getPara(INSProd.COL_PROVINCE));
		record.set(INSProd.COL_CITY, controller.getPara(INSProd.COL_CITY));
		record.set(INSProd.COL_DISTRICT, controller.getPara(INSProd.COL_DISTRICT));
		
		
		if (Db.save(INSProd.TABLE_NAME, record)) {
			record.set(INSProd.COL_ID, id);
			
			return new ResultInfo<Map<String,Object>>(ResultCodes.RET_SUCCESS, "ok", record.getColumns()); 
		} 
		
		return new ResultInfo<>(ResultCodes.RET_FAILED); 
	}
	
	public static ResultInfo<EmptyResultObj> delteInsprod(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (! optMgr.checkPermission(Permission.PERMISSION_DEL_INSPROD) || optMgr.insurerId == 0) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		
		String prodId = controller.getPara(INSProd.COL_ID);
		if (Utils.isEmpty(prodId)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "产品id不能为空");
		}
		Record newIdRecord =Db.findFirst("select ins_icon from ins_prod where id=?", prodId);
		String ins_icon=newIdRecord.getStr("ins_icon");
		int ct = Db.update("delete from ins_prod where id=? and ins_id=?", prodId, optMgr.insurerId);
		if (ct < 1) {
			return new ResultInfo<>(ResultCodes.RET_FAILED, "产品删除失败");
		}
	
		
			if (Utils.isEmpty(ins_icon)) {
				Utils.deleteFileByName(WebConfig4Mgr.rootDir + File.separator + ins_icon);
			}
	
		return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
	} 
	
	public static ResultInfo<EmptyResultObj> setInsprodStatus(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		//需要新增商品或删除商品权限，且必须为该商品所属机构管理员
		if (! optMgr.checkPermission(Permission.PERMISSION_UPDATE_INSPROD) || optMgr.insurerId == 0) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		
		int status = controller.getParaToInt(INSProd.COL_STATUS);
		if (status != 0 && status != -1) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "产品状态错误");
		}
		
		String prodId = controller.getPara(INSProd.COL_ID);
		if (Utils.isEmpty(prodId)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "产品id不能为空");
		}
		//修改参数新增modify和modify_time字段
		int ct = Db.update("update ins_prod set status=?,modify_time=?,modify=? where id=? and ins_id=? and status != ? ", status,Timestamp.valueOf(Utils.getCurrentTimeStr()),optMgr.userName, prodId, optMgr.insurerId, status);
		if (ct < 1) {
			return new ResultInfo<>(ResultCodes.RET_FAILED, "操作失败");
		}
		return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
	}
	
	public static ResultInfo<InsApplyResult> insApplyList(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		
		boolean scanAllInsAppliedPer = optMgr.checkPermission(Permission.PERMISSION_SCAN_ALL_LOAN_INSLIES);
		if (! scanAllInsAppliedPer){
			if (! optMgr.checkPermission(Permission.PERMISSION_SCAN_LOAN_INSLIES) || optMgr.insurerId == 0) {
				return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
			}
		}
		int page = 1;
		try {
			page = controller.getParaToInt("page");
		} catch (Exception e) {
			
		}
		InsApplyResult result = new InsApplyResult();
		result.page = page;
		int startIndex = (page - 1) * NUM_PER_PAGE;
		List<Record> recordList = null;
		String ins_name=controller.getPara(INSProd.COL_INSPROD_NAME);
		String bdate=controller.getPara("bdate");
		String edate=controller.getPara("edate");
		Record ctRecord = null;
		List<Object> paramCount = new ArrayList<>();
		List<Object> paramList = new ArrayList<>();
		if (scanAllInsAppliedPer) {
			//具有500权限的用户查看贷款列表
			//地区管理员类型
			if(optMgr.type==4){
				//具有500权限的用户查看贷款列表,地区管理员需要根据区域控制他查看贷款列表的范围				
				Record regionRecord = Db.findFirst("select * from region where region_id =?", optMgr.regionId);
				// 判断regionRecord是省-市-县（区）
				if (!regionRecord.get(TRegion.COL_PARENT_ID).toString().trim()
						.equals("0")) {
					Record cityRecord = Db.findFirst(
							"select * from region where region_code =?",
							regionRecord.get(TRegion.COL_PARENT_ID));
					if (!cityRecord.get(TRegion.COL_PARENT_ID).toString().trim()
							.equals("0")) {
						// 县级管理员权限
						String district = regionRecord.get(TRegion.COL_REGION_NAME).toString();
						String sqlCount=getSql("select count(1) count from v_ins_apply where  district=? \n", ins_name, bdate, edate, "");
						
						if (!StringUtils.isEmpty(district)) {
							paramCount.add(district);
							paramList.add(district);
						}
						if (!StringUtils.isEmpty(ins_name)) {
							paramCount.add("%" + ins_name + "%");
							paramList.add("%" + ins_name + "%");
						}
						if (!StringUtils.isEmpty(bdate)) {
							paramCount.add(bdate );
							paramList.add(bdate );
						}
						if (!StringUtils.isEmpty(edate)) {
							paramCount.add(edate);
							paramList.add(edate );
						}
						ctRecord = Db.findFirst(sqlCount,paramCount.toArray());
						
						String sqlList=getSql("select * from v_ins_apply where province=? \n",ins_name, bdate, edate,"limit ?, ? ");
						paramList.add(startIndex);
						paramList.add(NUM_PER_PAGE);
						recordList = Db.find(sqlList, paramList.toArray());
					} else {
						//市级管理员权限
					String	city = regionRecord.get(TRegion.COL_REGION_NAME).toString();
					String sqlCount=getSql("select count(1) count from v_ins_apply where  city=? \n", ins_name, bdate, edate, "");
					
					if (!StringUtils.isEmpty(city)) {
						paramCount.add(city);
						paramList.add(city);
					}
					if (!StringUtils.isEmpty(ins_name)) {
						paramCount.add("%" + ins_name + "%");
						paramList.add("%" + ins_name + "%");
					}
					if (!StringUtils.isEmpty(bdate)) {
						paramCount.add(bdate );
						paramList.add(bdate );
					}
					if (!StringUtils.isEmpty(edate)) {
						paramCount.add(edate);
						paramList.add(edate );
					}
					ctRecord = Db.findFirst(sqlCount,paramCount.toArray());
					
					String sqlList=getSql("select * from v_ins_apply where city=? \n",ins_name, bdate, edate,"limit ?, ? ");
					paramList.add(startIndex);
					paramList.add(NUM_PER_PAGE);
					recordList = Db.find(sqlList, paramList.toArray());
					
					}
				} else {
					//省级管理员权限
					String province = regionRecord.get(TRegion.COL_REGION_NAME).toString();
					String sqlCount=getSql("select count(1) count from v_ins_apply where  province=? \n", ins_name, bdate, edate, "");
				
					if (!StringUtils.isEmpty(province)) {
						paramCount.add(province);
						paramList.add(province);
					}
					if (!StringUtils.isEmpty(ins_name)) {
						paramCount.add("%" + ins_name + "%");
						paramList.add("%" + ins_name + "%");
					}
					if (!StringUtils.isEmpty(bdate)) {
						paramCount.add(bdate );
						paramList.add(bdate );
					}
					if (!StringUtils.isEmpty(edate)) {
						paramCount.add(edate);
						paramList.add(edate );
					}
					ctRecord = Db.findFirst(sqlCount,paramCount.toArray());
					
					String sqlList=getSql("select * from v_ins_apply where province=? \n",ins_name, bdate, edate,"limit ?, ? ");
					paramList.add(startIndex);
					paramList.add(NUM_PER_PAGE);
					recordList = Db.find(sqlList, paramList.toArray());
				}
			}else{
				//具有500权限的用户查看贷款列表，超级管理员或系统管理员
				String sqlCount=getSql("select count(1) count from v_ins_apply where  1=1 \n", ins_name, bdate, edate, "");

				if (!StringUtils.isEmpty(ins_name)) {
					paramCount.add("%" + ins_name + "%");
					paramList.add("%" + ins_name + "%");
				}
				if (!StringUtils.isEmpty(bdate)) {
					paramCount.add(bdate );
					paramList.add(bdate );
				}
				if (!StringUtils.isEmpty(edate)) {
					paramCount.add(edate);
					paramList.add(edate );
				}
				ctRecord = Db.findFirst(sqlCount,paramCount.toArray());
				
				String sqlList=getSql("select * from v_ins_apply where 1=1 \n",ins_name, bdate, edate,"limit ?, ? ");
				paramList.add(startIndex);
				paramList.add(NUM_PER_PAGE);
				recordList = Db.find(sqlList, paramList.toArray());
			}
		} else {
			//保险公司管理员
			if(optMgr.type==5){
				String sqlCount=getSql("select count(1) count from v_ins_apply where  insurer_id=? \n", ins_name, bdate, edate, "");
				paramCount.add(optMgr.insurerId );
				paramList.add(optMgr.insurerId );
				if (!StringUtils.isEmpty(ins_name)) {
					paramCount.add("%" + ins_name + "%");
					paramList.add("%" + ins_name + "%");
				}
				if (!StringUtils.isEmpty(bdate)) {
					paramCount.add(bdate );
					paramList.add(bdate );
				}
				if (!StringUtils.isEmpty(edate)) {
					paramCount.add(edate);
					paramList.add(edate );
				}
				ctRecord = Db.findFirst(sqlCount,paramCount.toArray());
				
				String sqlList=getSql("select * from v_ins_apply where insurer_id=? \n",ins_name, bdate, edate,"limit ?, ? ");
				paramList.add(startIndex);
				paramList.add(NUM_PER_PAGE);
				recordList = Db.find(sqlList, paramList.toArray());
			}
		}
		long ct = ctRecord.getLong("count");
		result.totalPage = ct / NUM_PER_PAGE + (long)((ct % NUM_PER_PAGE) == 0 ? 0 : 1);
		
		if (recordList != null) {
			for (Record tmp : recordList) {
				result.insApplies.add(tmp.getColumns());
			}
		}
		
		return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok", result);
	}
	
	public static ResultInfo<List<Map<String, Object>>> getUserDetail(Controller controller) {
		String id_num = controller.getPara("id_num");
		if (!StringUtils.isEmpty(id_num)) {
			Record user = Db.findFirst("select * from alipay_user where id_num =?", id_num);
			if (user != null) {
				String isHzsObj = user.get(com.moa.mgr.db.Tables.TAlipayUser.COL_IS_HZS).toString();
				String userId = user.get(com.moa.mgr.db.Tables.TAlipayUser.COL_IID).toString();
				
				String sql = "select def.id  as formId, def.form_name as formName, def.end_date as endDate, def.form_desc as formDesc, def.icon, def.fit_farmer as fitFarmer, def.form_type as formType," +
						" if (g.count > 0, g.count, 0) as count, g.lastDate from form_def def \n" +
						"left join(\n" +
						"select form_id, count(form_id) as count, max(date) as lastDate from form_data where user_id=? GROUP BY form_id \n" +
						") g on def.id=g.form_id where 1=1 ";
				
				List<Object> sqlParams = new ArrayList<Object>();
				sqlParams.add(userId);
				if (isHzsObj != null) {
					if ("0".equals(isHzsObj) || "1".equals(isHzsObj)) {
						String flag = "1".equals(isHzsObj.toString()) ? "2" : "1";
						sql += "and (def.flag=4 or def.flag=0 or def.flag=?) ";
						sqlParams.add(flag);
					} else if ("2".equals(isHzsObj)) {
						sql += "and (def.flag=4 or def.flag=3) ";
					}
				}		
				sql += " order by def.seq";
				
				Object[] paramArray = new Object[sqlParams.size()];
				paramArray = sqlParams.toArray(paramArray);
				List<Record> list = Db.find(sql, paramArray);
				if (list == null) {
					return new ResultInfo<List<Map<String, Object>>>(ResultCodes.RET_FAILED, "failed"); 
				}
				
				List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
				for (Record record : list) {
					if(!"雇工信息".equals(record.getColumns().get("formName"))){
						int formId = Integer.parseInt(String.valueOf(record.getColumns().get("formId")));
						Map<String, Object> map = new HashMap<String, Object>();
						List<WordDef> lst = new ArrayList<WordDef>();
						ResultInfo<FormDef> info = formDetail(formId, userId);
						if (info != null && info.result != null) {
							WordDef[] words = info.result.getWordDefs();
							for (int i = 0; i < words.length; i++) {
								WordDef wordDef = words[i];
								if (wordDef != null && !StringUtils.isEmpty(wordDef.value) && !"null".equals(wordDef.value.toLowerCase())) {
									lst.add(wordDef);
								}
							}
							map.put(info.result.formName, lst);
							retList.add(map);
						}
					}
				}
				return new ResultInfo<List<Map<String, Object>>>(ResultCodes.RET_SUCCESS, "ok", retList);
			} else {
				return new ResultInfo<List<Map<String, Object>>>(ResultCodes.RET_FAILED, "failed");
			}
		} else {
			return new ResultInfo<List<Map<String, Object>>>(ResultCodes.RET_FAILED, "failed");
		}
	}
	
	public static ResultInfo<FormDef> formDetail(int formId, String userId) {
		try {
			FormDef formDef = FormDefLoader.getInstance().findForm(formId);
			if (formDef == null) {
				return new ResultInfo<FormDef>(ResultCodes.RET_DATA_NOT_EXIST);
			}
			Record record = Db.findFirst("select * from form_data where form_id=? and user_id=? order by date desc limit 0, 1", formId, userId);
			if (record != null) {
				formDef.submitDate = record.getStr(TFormData.COL_DATE);
				WordDef[] words = formDef.getWordDefs();
				for (int i = 0; i < words.length; i++) {
					if (words[i] == null) {
						continue;
					}
					words[i].value = record.getStr(TFormData.COL_DATA_ARRAY[i]);
				}
				return new ResultInfo<FormDef>(ResultCodes.RET_SUCCESS, "success", formDef);
			} else {
				return new ResultInfo<FormDef>(ResultCodes.RET_DATA_NOT_EXIST);
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("query FormDetail error:" + e.getMessage());
			return new ResultInfo<FormDef>(ResultCodes.RET_PARAM_ERROR, "form id must be a num");
		}
	}
	
	private static String getSql(String sql1,String prod_name,String bdate,String edate,String sql2) {
		StringBuilder sql=new StringBuilder();
		sql.append(sql1);
			if(!StringUtils.isEmpty(prod_name)){
			sql.append("and ins_name like ?  ");
			}
			if(!StringUtils.isEmpty(bdate)){
				sql.append("and apply_date >= ?  ");
				}
			if(!StringUtils.isEmpty(edate)){
				sql.append("and apply_date <= ?  ");
			}
			sql.append(sql2);
		

		return sql.toString();
	}
	
	public static ResultInfo<EmptyResultObj> insApproval(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		if (! optMgr.checkPermission(Permission.PERMISSION_APPINS_LOAN_APPLY) || optMgr.insurerId == 0) {
			return new ResultInfo<>(ResultCodes.RET_NO_PERMISSION, "无权限");
		}
		
		String applyId = controller.getPara(PARAM_APPLY_ID);
		if (Utils.isEmpty(applyId)) {
			return new ResultInfo<>(ResultCodes.RET_DATA_NOT_EXIST, "数据不存在");
		}
		int ins_status = -1;
		try {
			ins_status = controller.getParaToInt(PARAM_INS_STATUS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Record applyRecord = Db.findFirst("select * from ins_apply where id=?", applyId);
		if (applyRecord == null) {
			return new ResultInfo<>(ResultCodes.RET_DATA_NOT_EXIST, "申请记录不存在");
		}
		/*	int status = applyRecord.getInt(TLoanApply.COL_STATUS);
		if (! checkOpt(status, optCode)) {
			return new ResultInfo<>(ResultCodes.RET_PARAM_ERROR, "不支持该操作");
		}*/
		String deny_reason=controller.getPara("refusalReason");
		if (!Utils.isEmpty(applyId)) {
			applyRecord.set(INSApply.COL_DENYREASON, deny_reason);
		}
		applyRecord.set(INSApply.COL_INS_STATUS, ins_status);
		applyRecord.set(INSApply.COL_MODIFY_DATE, Utils.getCurrentTimeStr());
		//applyRecord中新增modify(修改者)字段
		applyRecord.set(INSApply.COL_MODIFY,optMgr.userName);
		
		try {
			if (Db.update(INSApply.TABLE_NAME, INSApply.COL_ID, applyRecord)) {
				return new ResultInfo<>(ResultCodes.RET_SUCCESS, "ok");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("保险审批错误 id:" + applyId + ", ins_status:" + ins_status + ", " + e.getMessage());
		}
		return new ResultInfo<>(ResultCodes.RET_FAILED, "审批失败");	
	}
}
