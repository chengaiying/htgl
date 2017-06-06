package com.moa.mgr.service.form;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.moa.mgr.common.Utils;

/**
 * 定义单个表单的结构
 * @author zf21100
 *
 */
public class FormDef {
	
	private static final String TAG = "FormDef";
	
	public int id;
	
	public String formName = "";
	
	public String icon;
	
	/** 表单标识0: 适用所有用户   1：农场主  2:合作社 */
	public String flag = "1";
	
	/** 适合的农户 0：所有农户   1:种植户  2:养殖户 */
	public String fitFarmer = "0";
	
	public String bootImg;
	
	public String startDate = "2016-07-01 00:00:00";
	
	public String endDate = "2099-07-01 00:00:00";
	
	public String submitDate;
	
	public String formDesc;
	
	public int seq = 999;
	
	public static final int MAX_WORD_NUM = 20;
	
	private static Type type = new TypeToken<WordDef[]>(){}.getType();
	
	/** 当前表单所有字段的集合  */
	private WordDef[] formWords;
	
	/**
	 * 用于初次构建表单结构
	 * @param id
	 * @param name
	 * @param wordsDefJson
	 */
	public FormDef(int id, String name, String icon, String wordsDefJson, String startDate, String endDate, String fitFarmer, String bootImg, String desc) {
		this.id = id;
		this.formName = name;
		this.icon = icon;
		try {
			formWords = Utils.fromJson(wordsDefJson, type);
			LoggerFactory.getLogger(TAG).info("load FormDef success, formId:" + id + ", formName:" + name);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("init FormDef error");
		}
		this.startDate = startDate;
		this.endDate = endDate;
		this.fitFarmer = fitFarmer;
		this.bootImg = bootImg;
		this.formDesc = desc;
	}
	

	
	
	/**
	 * 用于初次构建表单结构
	 * @param id
	 * @param name
	 * @param words
	 */
	public FormDef(int id, String name, String icon, ArrayList<WordDef> words, String startDate, String endDate, String fitFarmer, String bootImg, String desc) {
		this.id = id;
		this.formName = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.fitFarmer = fitFarmer;
		this.bootImg = bootImg;
		this.icon = icon;
		this.formDesc = desc;
		formWords = new WordDef[MAX_WORD_NUM];
		try {
			if (words == null || words.size() > MAX_WORD_NUM) {
				throw new Exception();
			}
			int size = words.size();
			for (int i = 0; i < size; i++) {
				formWords[i] = words.get(i);
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("init FormDef error");
		}
	}
	
	public WordDef[] getWordDefs() {
		return formWords;
	}
	
	public FormDef copy() throws CloneNotSupportedException {
		ArrayList<WordDef> words = new ArrayList<WordDef>();
		for (WordDef word : formWords) {
			words.add(word == null ? word : word.copy());
		}
		FormDef fd = new FormDef(id, formName, icon, words, startDate, endDate, fitFarmer, bootImg, formDesc);
		
		return fd;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: " + id + ", name: " + formName);
		sb.append(", startDate: " + startDate + ", endDate:" + endDate + ", fitFarmer: " + fitFarmer);
		sb.append(", bootImg: " + bootImg + "\n");
		sb.append(Utils.toJson(formWords));
		return  sb.toString();
	}
	
}
