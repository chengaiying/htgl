package com.moa.mgr.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.LoggerFactory;

import com.moa.mgr.TokenInterceptor;
import com.moa.mgr.service.manager.ManagerCache;

public class SessionListener implements HttpSessionListener {
	
	private static final String TAG = "SessionListener";
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		event.getSession().setMaxInactiveInterval(1800);
		
		LoggerFactory.getLogger(TAG).info("session created:" + event.getSession().getId());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		if (event.getSession().getAttribute(TokenInterceptor.PARAM_MGR_ID) != null) {
			String mgrName = event.getSession().getAttribute(TokenInterceptor.PARAM_MGR_ID).toString();
			ManagerCache.getInstance().removeManager(mgrName);
		}
		LoggerFactory.getLogger(TAG).info("session destroyed:" + event.getSession().getId());
	}
}
