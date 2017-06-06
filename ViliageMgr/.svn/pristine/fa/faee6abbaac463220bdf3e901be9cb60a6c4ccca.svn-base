package com.moa.mgr.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;

public class ReloaderService {
	
	private static final String RELOAD_URL = "http://127.0.0.1:88/NewViliage/api/loader/reload";
	
	private static final int DEFAULT_POOL_SIZE = 5;
	
	public static final int OPT_CODE_UPDATE_FORM_DEF = 1;
	
	public static final int OPT_CODE_UPDATE_BANK = 2;
	
	public static final int OPT_CODE_UPDATE_FI_PROD = 3;
	
	private static ExecutorService pools = Executors.newSingleThreadExecutor();
	
	public static void reload(int optCode) {
		if (pools == null || pools.isShutdown()) {
			pools = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
		}
		
		pools.execute(new ReloadTask(optCode));
	}
	
	private static class ReloadTask implements Runnable {
		
		private int code = -1;
		
		private ReloadTask(int code) {
			this.code = code;
		}
		
		public void run() {
			HttpClientBuilder builder = HttpClients.custom();
			builder.setConnectionTimeToLive(5, TimeUnit.SECONDS);
			HttpClient client = builder.build();
			HttpPost post = new HttpPost(RELOAD_URL + "?opt_code=" + code);
			try {
				LoggerFactory.getLogger("ReloaderService").info("start reload");
				client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
				LoggerFactory.getLogger("ReloaderService").error("reload error:" + e.getMessage());
			} 
		}
	}
}
