package com.moa.mgr;

import javax.servlet.http.HttpSession;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.moa.mgr.result.ResultCodes;
import com.moa.mgr.result.ResultInfo;
import com.moa.mgr.result.obj.EmptyResultObj;
import com.moa.mgr.service.manager.ManagerCache;
import com.moa.mgr.service.manager.ManagerInfo;


/**
 * 用于检查用户是否登录
 * @author zf21100
 *
 */
public class TokenInterceptor implements Interceptor {

	public static final String PARAM_MGR_ID = "mgr_id";
	
	@Override
	public void intercept(ActionInvocation ai) {
		try {
			HttpSession session = ai.getController().getSession();
			Object mgrIdObj = session.getAttribute(PARAM_MGR_ID);
			if (mgrIdObj != null) {
				String mgrId = mgrIdObj.toString();
				ManagerInfo mgrInfo = ManagerCache.getInstance().findManager(mgrId);
				if (mgrInfo != null && session.getId().equals(mgrInfo.sessionId)) {
					ai.getController().setAttr(PARAM_MGR_ID, mgrId);
					ai.invoke();
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResultInfo<EmptyResultObj> ret = new ResultInfo<EmptyResultObj>(ResultCodes.RET_TOKEN_INVALIDATE, "need login");
		ai.getController().renderText(ret.toJson());
	}
}
