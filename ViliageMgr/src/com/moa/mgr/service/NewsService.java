package com.moa.mgr.service;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.common.Utils;
import com.moa.mgr.db.Tables.News;
import com.moa.mgr.model.AlipayUserModel;
import com.moa.mgr.model.NewsModel;
import com.moa.mgr.model.NewsType;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;


public class NewsService {
	private static final int DEFAULT_PAGE_SIZE = 50;
	private static final String PAGE_NUMBER = "page";
	private static final String TITLE = "title";
	/**
	 * 新增新闻接口
	 * @param controller
	 */
	public static ResultInfo<EmptyResultObj> addNews(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		String createor=optMgr.userName;
		String title=controller.getPara(News.COL_TITLE);
		String url=controller.getPara(News.COL_URL);
		String type=controller.getPara(News.COL_TYPE);
		String addtype=controller.getPara(News.COL_ADDTYPE);
		String newscontent=controller.getPara(News.COL_CONTENT);
		String newsource=controller.getPara(News.COL_NEWSOURCE);
		if (Utils.isEmpty(title)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻标题不得为空");
		}
		if (Utils.isEmpty(addtype)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻添加方式不能为空");
		}
		if (Utils.isEmpty(type)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻类型不得为空");
		}
		if (Utils.isEmpty(newsource)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻来源不得为空");
		}
		Record record=new Record();
		record.set(News.COL_TITLE, title);
		record.set(News.COL_URL, url);
		record.set(News.COL_CREATEOR, createor);
		record.set(News.COL_ADDTYPE, addtype);
		record.set(News.COL_CONTENT, newscontent);
		record.set(News.COL_NEWSOURCE, newsource);
		switch (type) {
		case "1":
			record.set(News.COL_TYPE, NewsType.TYPE1.getKey());
			break;
		case "2":
			record.set(News.COL_TYPE, NewsType.TYPE2.getKey());
			break;
		case "3":
			record.set(News.COL_TYPE, NewsType.TYPE3.getKey());
			break;

		default:
			break;
		}
		try {
			boolean flag=Db.save(News.TABLE_NAME, News.COL_ID, record);
			if (flag) {
				return new ResultInfo<EmptyResultObj>(ResultCodes.RET_SUCCESS, "新增成功");
			} 
		} catch (Exception e) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_FAILED,"新增失败");
		}
		return new ResultInfo<EmptyResultObj>(ResultCodes.RET_FAILED);
	}
	/*
	 * 新闻详情接口
	 * 
	 */
	public static ResultInfo<NewsModel> newsDetails(Controller controller) {
		String id=controller.getPara(News.COL_ID);
		Record record=Db.findById(News.TABLE_NAME, id);
		NewsModel news=new NewsModel();
		news.setId(record.getInt(News.COL_ID).toString());
		news.setTitle(record.getStr(News.COL_TITLE));
		news.setNews_url(record.getStr(News.COL_URL));
		news.setType(record.getInt(News.COL_TYPE).toString());
		news.setAddtype(record.getInt(News.COL_ADDTYPE).toString());
		news.setNews_content(record.getStr(News.COL_CONTENT));
		news.setNewsource(record.getStr(News.COL_NEWSOURCE));
		return new ResultInfo<NewsModel>(ResultCodes.RET_SUCCESS,
				"ok", news);
		
	}
	/**
	 * 新闻列表接口
	 * @param controller
	 */
	public static ResultInfo<Page<NewsModel>> newsList(Controller controller) {
		int page=controller.getParaToInt(PAGE_NUMBER, 0);
		String title=controller.getPara(TITLE);
		String sql = getSql(title);
		List<Object> param = new ArrayList<>();
		if (!StringUtils.isEmpty(title)) {
			param.add("%" + title + "%");
		}
		List<NewsModel> newsList=new ArrayList<>();
		Page<Record> result= Db.paginate(page, DEFAULT_PAGE_SIZE, "select *", sql, param.toArray());
		List<Record> list= result.getList();
		for (Record record : list) {
			NewsModel news=new NewsModel();
			news.setId(record.getInt(News.COL_ID).toString());
			news.setTitle(record.getStr(News.COL_TITLE));
			news.setNews_url(record.getStr(News.COL_URL));
			news.setType(record.getInt(News.COL_TYPE).toString());
			news.setAddtype(record.getInt(News.COL_ADDTYPE).toString());
			news.setNews_content(record.getStr(News.COL_CONTENT));
			news.setNews_content(record.getStr(News.COL_NEWSOURCE));
			/*switch (record.getInt(News.COL_TYPE)) {
			case 1:
				news.setType(NewsType.TYPE1.getKey());
				break;
			case 2:
				news.setType(NewsType.TYPE2.getKey());
				break;
			case 3:
				news.setType(NewsType.TYPE3.getKey());
				break;
			default:
				break;
			}*/
			newsList.add(news);
		}
		Page<NewsModel> pageList=new Page<NewsModel>(newsList, result.getPageNumber(), result.getPageSize(), result.getTotalPage()
				,result.getTotalRow());
		return new ResultInfo<Page<NewsModel>>(ResultCodes.RET_SUCCESS,
				"ok", pageList);
		
	}
	/**
	 * 删除新闻接口
	 * @param controller
	 */
	public static ResultInfo<EmptyResultObj> deleteNews(Controller controller) {
		String id=controller.getPara(News.COL_ID);
		if (Utils.isEmpty(id)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻id不得为空");
		}
		try {
			boolean flag=Db.deleteById(News.TABLE_NAME, id);
			if (flag) {
				return new ResultInfo<EmptyResultObj>(ResultCodes.RET_SUCCESS, "删除成功");
			} 
		} catch (Exception e) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_FAILED,"删除失败");
		}
		return new ResultInfo<EmptyResultObj>(ResultCodes.RET_FAILED);
	}
	/**
	 * 修改新闻接口
	 * @param controller
	 */
	public static ResultInfo<EmptyResultObj> updateNews(Controller controller) {
		ManagerInfo optMgr = ManagerCache.getInstance().findManager(controller.getAttrForStr(TokenInterceptor.PARAM_MGR_ID));
		String modify=optMgr.userName;
		String id=controller.getPara(News.COL_ID);
		String title=controller.getPara(News.COL_TITLE);
		String url=controller.getPara(News.COL_URL);
		String type=controller.getPara(News.COL_TYPE);
		String addtype=controller.getPara(News.COL_ADDTYPE);
		String newscontent=controller.getPara(News.COL_CONTENT);
		String newsource=controller.getPara(News.COL_NEWSOURCE);
		if (Utils.isEmpty(title)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻标题不得为空");
		}
		if (Utils.isEmpty(addtype)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻添加方式不能为空");
		}
		if (Utils.isEmpty(type)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻类型不得为空");
		}
		if (Utils.isEmpty(newsource)) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻来源不得为空");
		}
		switch (type) {
		case "1":
			type=NewsType.TYPE1.getKey();
			break;
		case "2":
			type=NewsType.TYPE2.getKey();
			break;
		case "3":
			type=NewsType.TYPE3.getKey();
			break;
		default:
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_PARAM_ERROR, "新闻类型错误");
		}
		try {
			int ct=Db.update("update news set title=?,type=?,news_url=?,modify=?,addtype=?,news_content=?,newsource=? where id=?", title,type,url,modify,addtype,newscontent,newsource,id);
			if (ct==1) {
				return new ResultInfo<EmptyResultObj>(ResultCodes.RET_SUCCESS, "修改成功");
			} 
		} catch (Exception e) {
			return new ResultInfo<EmptyResultObj>(ResultCodes.RET_FAILED,"修改失败");
		}
		return new ResultInfo<EmptyResultObj>(ResultCodes.RET_FAILED);
	}
	private static String getSql(String title) {
		String sql=null;
		if (StringUtils.isEmpty(title)) {
			sql=" from news order by modify_time desc";
		} else {
			sql=" from news where title like ? order by modify_time desc";
		}
		return sql;
	}
	
	/**
	 * 查询新闻来源
	 * @param controller
	 */
	public static ResultInfo<List<String>> queryNewsource(Controller controller) {
		List<String> list=new ArrayList<>();
		List<Record> querylist=Db.find("select DISTINCT(newsource) as newsource from news");
		for (Record record : querylist) {
			list.add(record.getStr(News.COL_NEWSOURCE));
		}
		
		return new ResultInfo<List<String>>(ResultCodes.RET_SUCCESS,
				"ok", list);
	}
}
