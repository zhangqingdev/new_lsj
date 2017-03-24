package com.oushangfeng.lsj.share;

/**
 * Created by Admin on 2016/3/30.
 */
public interface ShareCallBack {

	public void onSucceed(ShareManager.Platform platform);

	public void onFailed(ShareResponse response);

	public void onCancel();
}
