package com.moa.mgr.service.loan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.db.Tables.TFiProd;

/**
 * 金融产品加载器
 * 系统启动时，将所有产品加载到内存
 * @author zf21100
 *
 */
public class FiProdLoader {
	private static FiProdLoader instance = new FiProdLoader();
	
	private Map<String, FiProd> fiProdCache = new LinkedHashMap<String, FiProd>();
	
	private FiProdLoader() {}
	
	public static FiProdLoader getInstance() {
		return instance;
	}
	
	public void loadFiProds() {
		fiProdCache.clear();
		List<Record> fiProdRecords = Db.find("select * from " + TFiProd.TABLE_NAME + " order by bank_id, id");
		
		if (fiProdRecords != null) {
			for (Record record : fiProdRecords) {
				FiProd fiProd = FiProd.createProd(record);
				if (fiProd != null) {
					fiProdCache.put(fiProd.id, fiProd);
					LoggerFactory.getLogger("FiProdLoader").info("load fiProd success:" + fiProd);
				}
			}
		}
	}
	
	public boolean contains(String prodId) {
		return fiProdCache.containsKey(prodId);
	}
	
	public Collection<FiProd> getFiProdList() {
		return fiProdCache.values();
	}
	
	public FiProd findProd(String prodId) {
		return fiProdCache.get(prodId);
	}
	
	public void addProd(FiProd prod) {
		if (prod != null) {
			fiProdCache.put(prod.id, prod);
		}
	}
	
	public FiProd removeProd(String prodId) {
		return fiProdCache.remove(prodId);
	}
	
	public List<FiProd> getProdsByBankId(int bankId) {
		List<FiProd> list = new ArrayList<FiProd>();
		for (FiProd prod : fiProdCache.values()) {
			if (prod.bankId == bankId) {
				list.add(prod);
			}
		}
		return list;
	} 
	
	public List<FiProd> removeProdsByBankId(int bankId) {
		List<FiProd> list = new ArrayList<FiProd>();
		for (FiProd prod : fiProdCache.values()) {
			if (prod.bankId == bankId) {
				list.add(prod);
			}
		}
		
		for (FiProd prod : list) {
			fiProdCache.remove(prod.id);
		}
		
		return list;
	}
}
