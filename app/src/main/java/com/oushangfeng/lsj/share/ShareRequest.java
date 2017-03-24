package com.oushangfeng.lsj.share;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Admin on 2016/3/30.
 */
public class ShareRequest implements Parcelable {
	public static final Creator<ShareRequest> CREATOR = new Creator<ShareRequest>() {
		@Override
		public ShareRequest createFromParcel(Parcel source) {
			return new ShareRequest(source);
		}

		@Override
		public ShareRequest[] newArray(int size) {
			return new ShareRequest[size];
		}
	};
	/** 分享图片地址 */
	private String icon;
	/**  分享标题*/
	private String title;
	/** 分享链接*/
	private String url;
	/** 分享内容*/
	private String content;
	/** 分享平台*/
	private ShareManager.Platform platform;
	/** session */
	private String session;

	private ShareRequest() {
	}

	protected ShareRequest(Parcel in) {
		this.icon = in.readString();
		this.title = in.readString();
		this.url = in.readString();
		this.content = in.readString();
		int tmpPlatform = in.readInt();
		this.platform = tmpPlatform == -1 ? null : ShareManager.Platform.values()[tmpPlatform];
		this.session = in.readString();
	}

	public String getSession() {
		return session;
	}

	private void setSession(String session) {
		this.session = session;
	}

	public ShareManager.Platform getPlatform() {
		return platform;
	}

	public void setPlatform(ShareManager.Platform platform) {
		this.platform = platform;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.icon);
		dest.writeString(this.title);
		dest.writeString(this.url);
		dest.writeString(this.content);
		dest.writeInt(this.platform == null ? -1 : this.platform.ordinal());
		dest.writeString(this.session);
	}

	/**
	 * 构造器
	 */
	public static class Builder {
		private String icon,title,url,content;
		private ShareManager.Platform platform;

		public Builder setIcon(String icon) {
			this.icon = icon;
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder setContent(String content) {
			this.content = content;
			return this;
		}

		public Builder setPlatform(ShareManager.Platform platform) {
			this.platform = platform;
			return this;
		}

		public ShareRequest create(){
			ShareRequest request = new ShareRequest();
			request.setIcon(icon);
			request.setContent(content);
			request.setTitle(title);
			request.setUrl(url);
			request.setPlatform(platform);
			request.setSession(UUID.randomUUID().toString());
			return request;
		}
	}
}
