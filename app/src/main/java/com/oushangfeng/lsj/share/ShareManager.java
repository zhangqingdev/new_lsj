package com.oushangfeng.lsj.share;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Woody on 2016/3/30.
 */
public class ShareManager {

	public static ShareManager instance = null;
	private ShareRequest shareRequest;
	private ShareCallBack callBack;
	private ShareManager(){

	}

	public static synchronized ShareManager getInstance(){
		if(instance == null){
			instance = new ShareManager();
		}
		return instance;
	}

	public void share(Context context, ShareRequest request, ShareCallBack callBack){
		if(request != null){
			this.shareRequest = request;
			this.callBack = callBack;
			Intent intent = new Intent(context,ShareActivity.class);
			intent.putExtra("request",request);
			context.startActivity(intent);
		}
	}

	protected void postResponse(ShareResponse response){
		if(response !=null && shareRequest != null && callBack != null){
			if(shareRequest.getSession().equals(response.getSession())){
				ShareResponse.Status status = response.getStatus();
				switch (status){
					case SUCCEED:
						callBack.onSucceed(response.getPlatform());
						break;
					case CANCEL:
						callBack.onCancel();
						break;
					case FAILED:
						callBack.onFailed(response);
						break;
				}
			}
		}
	}

	/** 分享平台*/
	public enum Platform{
		QQ,
		QZONE,
		WEIBO,
		WXSCENESESSION,
		WXTIMELINE,
		SMS
	}
}
