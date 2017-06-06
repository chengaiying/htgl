package com.moa.mgr.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.model.NewsModel;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.NewsService;

public class NewsManController extends Controller{
	private static final String TAG = "NewsManController";
	@Before(TokenInterceptor.class)
	public void addNews() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = NewsService.addNews(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("add news error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	@Before(TokenInterceptor.class)
	public void newsList() {
		ResultInfo<Page<NewsModel>> result = new ResultInfo<Page<NewsModel>>(
				ResultCodes.RET_UNKNOWN_ERROR);
		try {
			result = NewsService.newsList(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("query news error " + e.getMessage());
		}
		renderText(result.toJson());
	}
	@Before(TokenInterceptor.class)
	public void newsDetails() {
		ResultInfo<NewsModel> result = new ResultInfo<NewsModel>(
				ResultCodes.RET_UNKNOWN_ERROR);
		try {
			result = NewsService.newsDetails(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("query news error " + e.getMessage());
		}
		renderText(result.toJson());
	}
	@Before(TokenInterceptor.class)
	public void deleteNews() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = NewsService.deleteNews(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("delete news error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	@Before(TokenInterceptor.class)
	public void updateNews() {
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = NewsService.updateNews(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("update news error " + e.getMessage());
		}
		renderText(ret.toJson());
	}
	@Before(TokenInterceptor.class)
	public void queryNewsource(){
		ResultInfo<List<String>> ret=new ResultInfo<List<String>>(ResultCodes.RET_UNKNOWN_ERROR);
		try {
			ret = NewsService.queryNewsource(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(TAG).error("query  newsource  error " + e.getMessage());
		}
		renderText(ret.toJson());
	}

}
