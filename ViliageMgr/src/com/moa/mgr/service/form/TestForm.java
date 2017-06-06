package com.moa.mgr.service.form;

import java.util.ArrayList;

import com.moa.mgr.common.Utils;


public class TestForm {

	private static final String FAMILY_FORM_DESC = "点击进入填写并完善家庭信息，提升芝麻信用分。";

	private static final String LAND_FORM_DESC = "点击进入填写您的真实土地基本信息，如土地总面积等，为您贷款增信。";

	private static final String PLANT_FORM_DESC = "种植农户点击进入填写相关信息，如种植品种等，针对性的进行农业部信息推送。";

	private static final String BREED_FORM_DESC = "养植农户点击进入填写相关信息，如种植品种等，针对性的进行农业部信息推送。";

	private static final String FARM_TOOL_FORM_DESC = "点击进入填写农机具资产信息，为您贷款增信。";

	private static final String EMPLOYEE_FORM_DESC = "点击进入填写固定雇员和临时雇员信息，针对性为您推送合适的保险产品。";

	private static final String INSURANCE_FORM_DESC = "点击进入填写近两年投保情况和受灾情况，为后期农业补贴的发放提供真实信息";

	private static final String LOAN_FORM_DESC = "点击进入填写近年来的贷款数据、获贷情况，良好的贷款记录有助于增信。";

	private static final String SUB_FUNDS_FORM_DESC = "点击进入填写今年最新的补贴款信息，便于农业部了解真实的补贴款发放情况。";
	
	private static final String HZS_BASE_FORM_DESC = "农民专业合作社介绍";
	
	private static final String ZCFZ_FORM_DESC = "资产负债表单介绍";


	public static void main(String[] args) {
		createFamilyInfoForm();
		createLandInfoForm();
		createPlantProdInfoForm();
		createBreedProdInfoForm();
		createFarmToolInfoForm();
		createEmployeeInfoForm();
		createInsuranceInfoForm();
		createLoanInfoForm();
		createSubFundsInfoForm();
		createHZSBaseInfoForm();
		createAssetLiabilityRatio();
	}

	public static void load(int[] args) {
		for (int i : args) {
			switch (i) {
			case 1:
				createFamilyInfoForm();
				break;
			case 2:
				createLandInfoForm();
				break;
			case 3:
				createPlantProdInfoForm();
				break;
			case 4:
				createBreedProdInfoForm();
				break;
			case 5:
				createFarmToolInfoForm();
				break;
			case 6:
				createEmployeeInfoForm();
				break;
			case 7:
				createInsuranceInfoForm();
				break;
			case 8:
				createLoanInfoForm();
				break;
			case 9:
				createSubFundsInfoForm();
				break;
			}
		}
		createTestForm();
		FormDefLoader.getInstance().loadFormDefs();
	}



	/** 1.家庭基本信息表单结构 */
	private static void createFamilyInfoForm() {
		WordDef word0= new WordDef("家庭农场名称", WordDef.DATA_TYPE_TEXT, "farm_name", true);
		WordDef word1= new WordDef("家庭总人口数", WordDef.DATA_TYPE_NUM, "member_num", true);
		WordDef word2 = new WordDef("劳动力人数", WordDef.DATA_TYPE_NUM, "worker_num", true);
		WordDef word3 = new WordDef("家庭年收入", WordDef.DATA_TYPE_NUM, "sales");
		word3.unit = "元";
		word3.dotNum = "2";

		WordDef word4 = new WordDef("婚姻状态", WordDef.DATA_TYPE_OPTION, "married", true);
		word4.options = new String[]{"已婚", "未婚"};

		WordDef word5 = new WordDef("学历", WordDef.DATA_TYPE_OPTION, "study", true);
		word5.options = new String[]{"小学", "初中", "高中", "本科", "研究生", "其它",};

		WordDef word6 = new WordDef("自建房面积", WordDef.DATA_TYPE_NUM, "homeArea", true);
		word6.unit = "平方";

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word0);
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);
		list.add(word5);
		list.add(word6);

		FormDef formDef =  new FormDef(1, "家庭基本信息", "form/icon/1.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/family.jpg", FAMILY_FORM_DESC);
		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}

	/** 2.创建土地信息表单结构 */
	private static void createLandInfoForm() {
		WordDef areaWord = new WordDef("土地总面积", WordDef.DATA_TYPE_NUM, "area", true);
		areaWord.dotNum = "2";
		areaWord.unit = "亩";
		
		WordDef selfAreaWord = new WordDef("自有土地面积", WordDef.DATA_TYPE_NUM, "self_area", true);
		selfAreaWord.dotNum = "2";
		selfAreaWord.unit = "亩";
		
		WordDef transAreaWord = new WordDef("流转土地面积", WordDef.DATA_TYPE_NUM, "trans_area", true);
		transAreaWord.dotNum = "2";
		transAreaWord.unit = "亩";
		
		WordDef rentPrice = new WordDef("土地流转平均租金", WordDef.DATA_TYPE_NUM, "trans_amount_per_unit", true);
		rentPrice.dotNum = "2";
		rentPrice.unit = "元/亩";
		
		WordDef fileWord = new WordDef("土地合同照片", WordDef.DATA_TYPE_FILE, "agreement_img", true);

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(areaWord);
		list.add(selfAreaWord);
		list.add(transAreaWord);
		list.add(rentPrice);
		list.add(fileWord);

		FormDef formDef =  new FormDef(2, "土地信息", "form/icon/2.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/land.jpg", LAND_FORM_DESC);

		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}

	/** 3.创建种植户生产经营信息表单结构 */
	private static void createPlantProdInfoForm() {
		WordDef  word1= new WordDef("种植品种一", WordDef.DATA_TYPE_OPTION, "plant_type1", true);
		word1.options = new String[]{"水稻", "小麦", "玉米", "大豆", "油菜", "棉花", "马铃薯", "水果类", "蔬菜类", "其他"};

		WordDef word2 =  new WordDef("种植面积", WordDef.DATA_TYPE_NUM, "area1", true);
		word2.unit = "亩";
		word2.dotNum = "1";

		WordDef word3 = new WordDef("上年度总销售额", WordDef.DATA_TYPE_NUM, "sales1", true);
		word3.unit = "元";
		word3.dotNum = "2";

		WordDef word4 = new WordDef("上年度农资采购额", WordDef.DATA_TYPE_NUM, "cost1", true);
		word4.unit = "元";
		word4.dotNum = "2";
		word4.div = "1";

		WordDef word5= new WordDef("种植品种二", WordDef.DATA_TYPE_OPTION, "plant_type2", true);
		word5.options = new String[]{"水稻", "小麦", "玉米", "大豆", "油菜", "棉花", "马铃薯", "水果类", "蔬菜类", "其他"};

		WordDef word6 =  new WordDef("种植面积", WordDef.DATA_TYPE_NUM, "area2", true);
		word6.unit = "亩";
		word6.dotNum = "1";

		WordDef word7 = new WordDef("上年度总销售额", WordDef.DATA_TYPE_NUM, "sales2", true);
		word7.unit = "元";
		word7.dotNum = "2";

		WordDef word8 = new WordDef("上年度农资采购额", WordDef.DATA_TYPE_NUM, "cost2", true);
		word8.unit = "元";
		word8.dotNum = "2";
		word8.div = "1";

//		WordDef word9 = new WordDef("近一年总销售额", WordDef.DATA_TYPE_NUM, "total_sales", true);
//		word9.unit = "元";
//		word9.dotNum = "2";
//
//		WordDef word10 = new WordDef("近一年农资采购额", WordDef.DATA_TYPE_NUM, "nzcg", true);
//		word10.unit = "元";
//		word10.dotNum = "2";

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);
		list.add(word5);
		list.add(word6);
		list.add(word7);
		list.add(word8);
//		list.add(word9);
//		list.add(word10);

		FormDef formDef =  new FormDef(3, "生产经营信息（种植户填写）", "form/icon/3.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "1", "form/boot_img/plant.jpg", PLANT_FORM_DESC);
		formDef.flag = "0";
		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}

	/** 4.创建养殖户生产经营信息表单结构 */
	private static void createBreedProdInfoForm() {
		WordDef  word1= new WordDef("养殖品种一", WordDef.DATA_TYPE_OPTION, "breed_type1", true);
		word1.options = new String[]{"猪", "牛", "羊", "鸡", "鸭", "鹅", "桑蚕", "蛇", "蜜蜂", "鱼", "虾", "螃蟹", "其他"};
		
		WordDef word2 = new WordDef("养殖存栏", WordDef.DATA_TYPE_NUM, "breed_num1", true);
		word2.unit = "头";
		
		WordDef word3 = new WordDef("一头的成本", WordDef.DATA_TYPE_NUM, "single_cost1", true);
		word3.unit = "元";
		word3.dotNum = "2";
		
		WordDef word4 = new WordDef("上年度总销售额", WordDef.DATA_TYPE_NUM, "sale_amount1", true);
		word4.unit = "元";
		word4.dotNum = "2";
		
		WordDef word5 = new WordDef("上年度饲料等采购额", WordDef.DATA_TYPE_NUM, "cost1", true);
		word5.unit = "元";
		word5.dotNum = "2";
		word5.div = "1";

		WordDef  word6= new WordDef("养殖品种二", WordDef.DATA_TYPE_OPTION, "breed_type2", true);
		word6.options = new String[]{"猪", "牛", "羊", "鸡", "鸭", "鹅", "桑蚕", "蛇", "蜜蜂", "鱼", "虾", "螃蟹", "其他"};
		
		
		WordDef word7 = new WordDef("养殖存栏", WordDef.DATA_TYPE_NUM, "breed_num2", true);
		word7.unit = "头";
		
		WordDef word8 = new WordDef("一头的成本", WordDef.DATA_TYPE_NUM, "single_cost2", true);
		word8.unit = "元";
		word8.dotNum = "2";
		
		WordDef word9 = new WordDef("上年度总销售额", WordDef.DATA_TYPE_NUM, "sale_amount2", true);
		word9.unit = "元";
		word9.dotNum = "2";
		WordDef word10 = new WordDef("上年度饲料等采购额", WordDef.DATA_TYPE_NUM, "cost2", true);
		word10.unit = "元";
		word10.dotNum = "2";
		word10.div = "1";

//		WordDef word11 = new WordDef("近1年总销售额", WordDef.DATA_TYPE_NUM, "totle_sale_amount", true);
//		word11.unit = "元";
//		word11.dotNum = "2";
//		WordDef word12 = new WordDef("近1年饲料等采购额", WordDef.DATA_TYPE_NUM, "cost_for_feed", true);
//		word12.unit = "元";
//		word11.dotNum = "2";

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);
		list.add(word5);
		list.add(word6);
		list.add(word7);
		list.add(word8);
		list.add(word9);
		list.add(word10);
//		list.add(word11);
//		list.add(word12);


		FormDef formDef =  new FormDef(4, "生产经营信息（养殖户填写）", "form/icon/4.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "2", "form/boot_img/breed.jpg", BREED_FORM_DESC);
		formDef.flag = "0";
		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}


	/**
	 * 5.创建农具信息表单结构
	 */
	private static void createFarmToolInfoForm() {
		
		WordDef word1 = new WordDef("农机品种一", WordDef.DATA_TYPE_OPTION, "tool_type1", true);
		word1.options = new String[]{"农机1", "农机2"};
		
		WordDef word2 = new WordDef("农机数量", WordDef.DATA_TYPE_NUM, "tool_num1", true);
//		word1.unit = "辆";

		WordDef word3 = new WordDef("农机具单价", WordDef.DATA_TYPE_NUM, "tool_price1", true);
		word3.unit = "元";
		word3.dotNum = "2";
		
		WordDef word4 = new WordDef("该品种享受补贴总金额", WordDef.DATA_TYPE_NUM, "total_sub_amount1", true);
		word4.unit = "元";
		word4.dotNum = "2";
		
		WordDef word5 = new WordDef("农机具照片", WordDef.DATA_TYPE_FILE, "tool_img1", true);
		word5.div = "1";
		
		
		WordDef word6 = new WordDef("农机品种二", WordDef.DATA_TYPE_OPTION, "tool_type2", true);
		word6.options = new String[]{"农机1", "农机2"};
		
		WordDef word7 = new WordDef("农机数量", WordDef.DATA_TYPE_NUM, "tool_num2", true);

		WordDef word8 = new WordDef("农机具单价", WordDef.DATA_TYPE_NUM, "tool_price2", true);
		word8.unit = "元";
		word8.dotNum = "2";
		
		WordDef word9 = new WordDef("该品种享受补贴总金额", WordDef.DATA_TYPE_NUM, "total_sub_amount2", true);
		word9.unit = "元";
		word9.dotNum = "2";

		WordDef word10 = new WordDef("农机具照片", WordDef.DATA_TYPE_FILE, "tool_img2", true);

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);
		list.add(word5);
		list.add(word6);
		list.add(word7);
		list.add(word8);
		list.add(word9);
		list.add(word10);

		FormDef formDef =  new FormDef(5, "农机信息", "form/icon/5.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/tools.jpg", FARM_TOOL_FORM_DESC);
		formDef.flag = "0";
		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}

	/**
	 * 6.创建雇员信息表单结构
	 */
	private static void createEmployeeInfoForm() {
		WordDef  word1 = new WordDef("常年雇工人数", WordDef.DATA_TYPE_NUM, "employee_num", true);
		WordDef  word2 = new WordDef("临时雇工人数", WordDef.DATA_TYPE_NUM, "tmp_employee_num", true);
		WordDef word3 = new WordDef("常年雇工人均年工资", WordDef.DATA_TYPE_NUM, "employee_yearly_salary", true);
		word3.unit = "元/年";
		word3.dotNum = "2";
		WordDef word4 = new WordDef("常年雇工总工资", WordDef.DATA_TYPE_NUM, "total_yearly_salary", true);
		word4.unit = "元/年";
		word4.dotNum = "2";

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);

		FormDef formDef =  new FormDef(6, "雇员信息", "form/icon/6.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/employee.jpg", EMPLOYEE_FORM_DESC);
		formDef.flag = "0";
		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}

	/**
	 * 7.创建保险信息表单结构
	 */
	private static void createInsuranceInfoForm() {
		WordDef word1 = new WordDef("当年是否投保", WordDef.DATA_TYPE_OPTION, "status", true);
		word1.options =  new String[]{"是", "否"};

		WordDef word2 = new WordDef("2016年投保金额", WordDef.DATA_TYPE_NUM, "2016_amount", true);
		word2.dotNum = "2";
		word2.unit = "元";
		
		WordDef word3 = new WordDef("2016年保险险种", WordDef.DATA_TYPE_TEXT, "2016_insurance_type", true);

		WordDef word4 = new WordDef("当年受灾面积", WordDef.DATA_TYPE_NUM, "disaster_area");
		word4.unit = "亩";
		
		WordDef word5 = new WordDef("获赔金额", WordDef.DATA_TYPE_NUM, "compensation");
		word5.dotNum = "2";
		word5.unit = "元";
		
		WordDef word6 = new WordDef("2015年获得政府补助金额", WordDef.DATA_TYPE_NUM, "2015_subsidy");	
		word6.dotNum = "2";
		word6.unit = "元";

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);
		list.add(word5);
		list.add(word6);

		FormDef formDef =  new FormDef(7, "保险信息", "form/icon/7.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/insurance.jpg", INSURANCE_FORM_DESC);
		formDef.flag = "0";
		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}


	/**
	 * 8.创建贷款表单
	 */
	private static void createLoanInfoForm() {
		WordDef word1 = new WordDef("贷款来源", WordDef.DATA_TYPE_OPTION, "loan_from", true);
		word1.options = new String[]{"银行", "担保公司", "P2P", "民间借贷", "其他"};
		WordDef word2 = new WordDef("贷款用途", WordDef.DATA_TYPE_OPTION, "loan_for", true);
		word2.options = new String[]{"购买农具", "扩大经营", "购买农资", "土地租金", "短期垫资", "基础建设", "其他"};
		WordDef word3 = new WordDef("获贷金额", WordDef.DATA_TYPE_NUM, "loan_amount", true);
		word3.unit = "元";
		word3.dotNum = "2";
		WordDef word4 = new WordDef("贷款月利率", WordDef.DATA_TYPE_NUM, "loan_rate", true);
		word4.dotNum = "2";
		word4.unit = "厘";
//		WordDef word5 = new WordDef("2016年贷款余额", WordDef.DATA_TYPE_NUM, "2016_loan_rest", true);
//		word5.unit = "元";
//		word5.dotNum = "2";
//		WordDef word6 = new WordDef("是否有扩大贷款需求", WordDef.DATA_TYPE_OPTION, "need_more", true);
//		word6.options =  new String[]{"是", "否"};

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);
//		list.add(word5);
//		list.add(word6);
		FormDef formDef =  new FormDef(8, "贷款信息", "form/icon/8.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/loan.png", LOAN_FORM_DESC);
		formDef.flag = "0";
		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}

	/**
	 * 9.创建补贴款表单
	 */
	private static void createSubFundsInfoForm() {
		WordDef word1 = new WordDef("2016年已收到哪些补贴", WordDef.DATA_TYPE_CHECKBOX, "2016_sub_funds_items", true);
		word1.options =  new String[]{"耕地地力补贴", "大棚/饲舍及配套设施/场库棚等补贴", "种苗/种畜/种草补贴", 
				"农机购置补贴", "贷款贴息", "土地租金补贴", "家庭农场专项补贴(含当地规模化经营补贴)", "其他补贴", "未获得补贴"};
		WordDef word2 = new WordDef("补贴金额", WordDef.DATA_TYPE_NUM, "sub_funds_amount", true);
		word2.unit = "元";
		word2.dotNum = "2";

		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		FormDef formDef =  new FormDef(9, "补贴款信息", "form/icon/9.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/sub_funds.jpg", SUB_FUNDS_FORM_DESC);
		System.out.println(Utils.toJson(formDef));
		save(formDef);
	}

	private static void createTestForm() {
//		WordDef word1 = new WordDef("金钱,2位小数", WordDef.DATA_TYPE_NUM, "money", true);
//		word1.dotNum = "2";
//		word1.unit="美元";
//		
//		WordDef word2 = new WordDef("文本，6-20长度限制", WordDef.DATA_TYPE_TEXT, "text", true);
//		word2.lenMinLimit = "6";
//		word2.lenMaxLimit = "20";
//		
//		WordDef word3 = new WordDef("单选必填项", WordDef.DATA_TYPE_OPTION, "selection_item", true);
//		word3.options =  new String[]{"选项一", "选项二", "选项三"};
//		
//		WordDef word4 = new WordDef("你喜欢什么手机", WordDef.DATA_TYPE_CHECKBOX, "love_phone", true);
//		word4.options =  new String[]{"苹果", "小米", "三星", "华为", "魅族", "联想", "诺基亚", "摩托罗拉"};
//		
//		WordDef word5 = new WordDef("你出生的日期", WordDef.DATA_TYPE_DATE, "born_date", false);
//		
//		WordDef word6 = new WordDef("照片一", WordDef.DATA_TYPE_FILE, "img1", true);
//		
//		WordDef word7 = new WordDef("照片二", WordDef.DATA_TYPE_FILE, "img2", true);
//		
//
//		ArrayList<WordDef> list = new ArrayList<WordDef>();
//		list.add(word1);
//		list.add(word2);
//		list.add(word3);
//		list.add(word4);
//		list.add(word5);
//		list.add(word6);
//		list.add(word7);
//		FormDef formDef =  new FormDef(10, "测试表单", "form/icon/8.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/loan.png", LOAN_FORM_DESC);
//		System.out.println(formDef);
//		save(formDef);
	}
	
	
	/** 10.创建合作社基本情况表单 */
	public static void createHZSBaseInfoForm() {
		WordDef word1 = new WordDef("农民专业合作社名称", WordDef.DATA_TYPE_TEXT, "hzs_name", true);
		word1.lenMaxLimit = "40";
		
		WordDef word2 = new WordDef("文化程度", WordDef.DATA_TYPE_OPTION, "edu_level", true);
		word2.options = new String[]{"小学", "初中", "高中", "本科", "研究生", "其它",};
		
		WordDef word3 = new WordDef("社会兼职", WordDef.DATA_TYPE_TEXT, "shjz", true);
		
		WordDef word4 = new WordDef("注册登记时间", WordDef.DATA_TYPE_DATE, "reg_date", true);
		
		WordDef word5 = new WordDef("实有成员总数", WordDef.DATA_TYPE_NUM, "staff_num", true);
		
		WordDef word6 = new WordDef("其中农民成员数", WordDef.DATA_TYPE_NUM, "farmer_num", true);
		
		WordDef word7 = new WordDef("成员出资总额", WordDef.DATA_TYPE_NUM, "contribution", true);
		word7.unit = "万元";
		word7.dotNum = "2";
		
		WordDef word8 = new WordDef("内部信用合作资金规模", WordDef.DATA_TYPE_NUM, "fund", true);
		word8.unit = "万元";
		word8.dotNum = "2";
		
		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);
		list.add(word5);
		list.add(word6);
		list.add(word7);
		list.add(word8);
		FormDef formDef = new FormDef(10, "基本情况", "form/icon/10.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/hzs_base.png", HZS_BASE_FORM_DESC);
		formDef.flag = "2";
		
		System.out.println(Utils.toJson(formDef));
	}
	
	
	/** 11.创建资产负债及收益  表单 */
	private static void createAssetLiabilityRatio() {
		WordDef word1 = new WordDef("期末贷款余额", WordDef.DATA_TYPE_NUM, "loan_rest", true);
		word1.unit = "万元";
		word1.dotNum = "2";
		
		WordDef word2 = new WordDef("盈余返还总额", WordDef.DATA_TYPE_NUM, "return_amt", true);
		word2.unit = "万元";
		word2.dotNum = "2";
		
		WordDef word3 = new WordDef("可分配盈余按成员与本社交易两（额）返还比例", WordDef.DATA_TYPE_NUM, "return_ratio", true);
		word3.dotNum = "2";
		
		WordDef word4 = new WordDef("固定资产净值", WordDef.DATA_TYPE_NUM, "asset_value", true);
		word4.unit = "万元";
		word4.dotNum = "2";
		
		WordDef word5 = new WordDef("年经营收入", WordDef.DATA_TYPE_NUM, "operating_income", true);
		word5.unit = "万元";
		word5.dotNum = "2";
		
		WordDef word6 = new WordDef("获得财政扶持资金总额", WordDef.DATA_TYPE_NUM, "support_amt", true);
		word6.unit = "万元";
		word6.dotNum = "2";
		
		WordDef word7 = new WordDef("成员社内年均所得收入", WordDef.DATA_TYPE_NUM, "avg_income", true);
		word7.unit = "元";
		word7.dotNum = "2";
		
		ArrayList<WordDef> list = new ArrayList<WordDef>();
		list.add(word1);
		list.add(word2);
		list.add(word3);
		list.add(word4);
		list.add(word5);
		list.add(word6);
		list.add(word7);
		FormDef formDef = new FormDef(11, "资产负债及收益情况", "form/icon/11.png", list, "2016-07-01 00:00:00", "2017-01-01 00:00:00", "0", "form/boot_img/zcfz.png", ZCFZ_FORM_DESC);
		formDef.flag = "2";
		
		System.out.println(Utils.toJson(formDef));
	}
	
	

	private static void save(FormDef def) {
//		Db.deleteById(TFormDef.TABLE_NAME, TFormDef.COL_ID, def.id);
//
//		Record record = new Record();
//		record.set(TFormDef.COL_ID, def.id);
//		record.set(TFormDef.COL_FORM_NAME, def.formName);
//		record.set(TFormDef.COL_ICON, def.icon);
//		record.set(TFormDef.COL_BOOT_IMG, def.bootImg);
//		record.set(TFormDef.COL_FIT_FARMER, def.fitFarmer);
//		record.set(TFormDef.COL_START_DATE, def.startDate);
//		record.set(TFormDef.COL_END_DATE, def.endDate);
//		record.set(TFormDef.COL_FORM_DESC, def.formDesc);
//		record.set(TFormDef.COL_WORDS_DEF, Utils.toJson(def.getWordDefs()));
//
//		Db.save(TFormDef.TABLE_NAME, record);
	}

}
