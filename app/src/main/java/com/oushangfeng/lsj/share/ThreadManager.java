package com.oushangfeng.lsj.share;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadManager {
	
	private static ThreadManager instance = null;
	private Executor pool = Executors.newFixedThreadPool(5);
	
	private ThreadManager(){
		
	}
	
	public static synchronized ThreadManager getInstance(){
		if(instance == null){
			instance = new ThreadManager();
		}
		return instance;
	}
	
	public void postRunnable(Runnable r){
		pool.execute(r);
	}

}
