package com.moa.mgr.service.manager;

import java.io.Serializable;

public class Permission implements Serializable {

	private static final long serialVersionUID = -1560673633177022334L;
	
	/** 权限: 查看管理员*/
	public static final int PERMISSION_SCAN_MGR = 100;
	/** 权限: 新增管理员*/
	public static final int PERMISSION_ADD_MGR = 101;
	/** 权限: 删除管理员*/
	public static final int PERMISSION_DEL_MGR = 102;
	/** 权限: 更新管理员*/
	public static final int PERMISSION_UPDATE_MGR = 103;
	
	/** 权限: 新增金融机构*/
	public static final int PERMISSION_ADD_BANK = 104;
	
	/** 权限: 删除金融机构*/
	public static final int PERMISSION_DEL_BANK = 105;
	
	/** 权限: 新增保险公司*/
	public static final int PERMISSION_ADD_INSURANCE = 106;
	
	/** 权限: 删除保险公司*/
	public static final int PERMISSION_DEL_INSURANCE = 107;
	
	/** 权限: 查看机构管理员*/
	public static final int PERMISSION_SCAN_BANK_MGR = 200;
	/** 权限: 新增机构管理员*/
	public static final int PERMISSION_ADD_BANK_MGR = 201;
	/** 权限: 删除机构管理员*/
	public static final int PERMISSION_DEL_BANK_MGR = 202;
	/** 权限: 更新机构管理员*/
	public static final int PERMISSION_UPDATE_BANK_MGR = 203;
	
	/** 权限: 新增直报表单*/
	public static final int PERMISSION_ADD_FORM_DEF = 301;
	/** 权限: 删除直报表单*/
	public static final int PERMISSION_DEL_FORM_DEF = 302;
	/** 权限: 更新直报表单*/
	public static final int PERMISSION_UPDATE_FORM_DEF = 303;
	
	/** 权限: 新增贷款产品*/
	public static final int PERMISSION_ADD_FIPROD = 401;
	/** 权限: 删除贷款产品*/
	public static final int PERMISSION_DEL_FIPROD = 402;
	/** 权限: 修改贷款产品*/
	public static final int PERMISSION_UPDATE_FIPROD = 403;
	
	/** 权限: 查看所有贷款申请*/
	public static final int PERMISSION_SCAN_ALL_LOAN_APPLIES = 500;
	/** 权限: 查看个人所属机构的贷款申请*/
	public static final int PERMISSION_SCAN_LOAN_APPLIES = 501;
	/** 权限: 贷款申请审批*/
	public static final int PERMISSION_APPROVAL_LOAN_APPLY = 502;
	
	/** 权限:查看农户名录 */
	public static final int PERMISSION_SCAN_FARMER_DIRECTORY = 600;
	/** 权限:添加农户名录 */
	public static final int PERMISSION_ADD_FARMER_DIRECTORY = 601;
	/** 权限:删除农户名录 */
	public static final int PERMISSION_DEL_FARMER_DIRECTORY = 602;
	/** 权限:修改农户名录 */
	public static final int PERMISSION_UPDATE_FARMER_DIRECTORY = 603;
	
	
	/** 权限: 保险产品查看*/
	public static final int PERMISSION_LOAN_INSPROD = 800;
	/** 权限: 新增保险产品*/
	public static final int PERMISSION_ADD_INSPROD = 801;
	/** 权限: 删除保险产品*/
	public static final int PERMISSION_DEL_INSPROD = 802;
	/** 权限: 修改保险产品*/
	public static final int PERMISSION_UPDATE_INSPROD = 803;
	
	/** 权限: 全部保险信息查看*/
	public static final int PERMISSION_SCAN_ALL_LOAN_INSLIES = 804;
	/** 权限: 查看个人所属保险公司的保险申请*/
	public static final int PERMISSION_SCAN_LOAN_INSLIES =805;
	/** 权限: 保险审批操作*/
	public static final int PERMISSION_APPINS_LOAN_APPLY = 806;

	
	
	public int id;
	
	public String name;
	
	@Override
	public String toString() {
		return "[" + id + ", " + name + "]";
	}
}
