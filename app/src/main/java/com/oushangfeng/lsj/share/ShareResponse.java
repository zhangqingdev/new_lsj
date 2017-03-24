package com.oushangfeng.lsj.share;

/**
 * Created by Admin on 2016/3/30.
 */
public class ShareResponse {
	private ShareManager.Platform platform;
	private String session;
	private int errCode;
	private String errMessage;

	private Status status; //返回的状态

	public ShareManager.Platform getPlatform() {
		return platform;
	}

	public void setPlatform(ShareManager.Platform platform) {
		this.platform = platform;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public enum Status{
		/**成功*/
		SUCCEED,
		/**失败*/
		FAILED,
		/**取消*/
		CANCEL
	}

	/**自定义错误码*/
	public static class ErrCode{
		/** 无效参数，title,content,url可能为空*/
		public static final int ERR_INVALID_PARAMS = -1;
		/** 未安装对微信的客户端*/
		public static final int ERR_WX_NOT_INSTALLED = -2;
		/**微信版本不支持朋友圈*/
		public static final int ERR_TIMELINE_NOT_SUPPORTED= -3;

	}
}
