package com.oushangfeng.lsj.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.WeakReference;

public abstract class BaseFragmentActivity extends FragmentActivity {

	public MyHandler handler;

	
	public abstract void onInitView(); //构建界面
	
	public abstract void handleMsg(Message msg);


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		handler = new MyHandler(this);
		onInitView();
	}


	@Override
	protected void onDestroy() {
		handler.setDestoryed(true);
		super.onDestroy();

	}

	public static class MyHandler extends Handler {
		WeakReference<BaseFragmentActivity> reference;
		private boolean isDestoryed;

		public void setDestoryed(boolean destoryed) {
			isDestoryed = destoryed;
		}

		public MyHandler(BaseFragmentActivity activity){
			reference = new WeakReference<BaseFragmentActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			if(reference != null){
				BaseFragmentActivity activity = reference.get();
				if(activity != null ){
					activity.handleMsg(msg);
				}
			}
			super.handleMessage(msg);
		}

		@Override
		public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
			if(isDestoryed){
				return false;
			}else {
				return super.sendMessageAtTime(msg, uptimeMillis);
			}
		}
	}
	

}
