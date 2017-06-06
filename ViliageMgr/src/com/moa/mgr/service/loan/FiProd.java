package com.moa.mgr.service.loan;

import java.io.Serializable;

import com.jfinal.plugin.activerecord.Record;
import com.moa.mgr.db.Tables.TFiProd;

/**
 * 金融产品定义
 * @author alps
 *
 */
public class FiProd implements Serializable {

	private static final long serialVersionUID = 3947822207435962979L;
	
	public String id = "";
	
	public int bankId;
	
	public String prodName = "";
	
	public String prodIcon = "";
	
	public String prodDesc = "";
	
	public String prodContent = "";
	
	public String rate = "";
	
//	public int maxAmount = 0;
//	
//	public int maxPeriod = 0;
	
	public String linkUrl = "";
	
	public String contact = "";
	
	public String createDate = "";
	
	public int status = 0;
	
	public static FiProd createProd(Record prodRecord) {
		FiProd prodInfo = new FiProd();
		prodInfo.id = prodRecord.getStr(TFiProd.COL_ID);
		prodInfo.bankId = prodRecord.getInt(TFiProd.COL_BANK_ID);
		prodInfo.prodName = prodRecord.getStr(TFiProd.COL_PROD_NAME);
		prodInfo.prodIcon = prodRecord.getStr(TFiProd.COL_PROD_ICON);
		prodInfo.prodDesc = prodRecord.getStr(TFiProd.COL_PROD_DESC);
		prodInfo.prodContent = prodRecord.getStr(TFiProd.COL_PROD_CONTENT);
		prodInfo.rate = prodRecord.getStr(TFiProd.COL_RATE);
		prodInfo.createDate = prodRecord.getStr(TFiProd.COL_CREATE_DATE);
		prodInfo.status = prodRecord.getInt(TFiProd.COL_STATUS);
		
//		try {
//			prodInfo.maxAmount = prodRecord.getInt(TFiProd.COL_MAX_AMOUNT);
//			prodInfo.maxPeriod = prodRecord.getInt(TFiProd.COL_MAX_PERIOD);
//		} catch (Exception e) {
//			LoggerFactory.getLogger("FiProd").error("load fiprod:" + prodInfo.id + " error " + e.getMessage());
//			return null;
//		}
		prodInfo.linkUrl = prodRecord.getStr(TFiProd.COL_LINK_URL);
		prodInfo.contact = prodRecord.getStr(TFiProd.COL_CONTACT);
		return prodInfo;
	}
	
	public String getProdContent() {
		return prodContent;
	}
	
	public void setProdContent(String content) {
		this.prodContent = content;
	}

	public String getContact() {
		return contact;
	}

	
	@Override
	public String toString() {
		return "[" + id + ", " + prodName + "]";
	}

}
