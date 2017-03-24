package com.oushangfeng.lsj.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;

import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.base.BaseFragmentActivity;
import com.oushangfeng.lsj.utils.MainConstants;
import com.oushangfeng.lsj.utils.Utils;
import com.oushangfeng.lsj.widget.LoadingDialog;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ShareActivity extends BaseFragmentActivity implements IWeiboHandler.Response{


	public static final int SHARE_BITMAP = 1;
	private ShareRequest request;
	private BroadcastReceiver wxReceiver;
	private Bitmap shareImageThumb;
	private LoadingDialog loadingDialog;
	private IWeiboShareAPI weiboAPI;
	private SsoHandler ssoHandler;
	private IUiListener shareQQListener = new IUiListener() {

		@Override
		public void onError(UiError arg0) {
			postShareResponse(ShareResponse.Status.FAILED, arg0.errorCode, arg0.errorMessage);
		}

		@Override
		public void onComplete(Object arg0) {
			postShareResponse(ShareResponse.Status.SUCCEED);
		}

		@Override
		public void onCancel() {
			postShareResponse(ShareResponse.Status.CANCEL);
		}
	};

	@Override
	public void onInitView() {
		Intent intent = getIntent();
		request = intent.getParcelableExtra("request");
		loadingDialog = new LoadingDialog(this);
		weiboAPI = WeiboShareSDK.createWeiboAPI(this,
				MainConstants.WB_APPID);
		weiboAPI.registerApp();
		weiboAPI.handleWeiboResponse(getIntent(), this);
		if(request != null){
			String icon = request.getIcon();
			ShareManager.Platform platform = request.getPlatform();
			if(platform == ShareManager.Platform.WEIBO || platform == ShareManager.Platform.WXSCENESESSION || platform == ShareManager.Platform.WXTIMELINE){
				//需要预加载图片
				getShareImageThumb(icon);
			}else {
				handleRequest();
			}

		}else {
			finish();
		}
	}

	@Override
	public void handleMsg(Message msg) {
		switch (msg.what){
			case SHARE_BITMAP:
				loadingDialog.dismiss();
				shareImageThumb = (Bitmap) msg.obj;
				handleRequest();
				break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	private void getShareImageThumb(final String path){
		loadingDialog.show("加载中...");
		ThreadManager.getInstance().postRunnable(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = null;
				if (!Utils.isEmpty(path)) {
					try {
						URL url = new URL(path);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setRequestMethod("GET"); // 设置请求方法为GET
						conn.setReadTimeout(10 * 1000); // 设置请求过时时间为5秒
						if (conn.getResponseCode() == 200) {
							InputStream inputStream = conn.getInputStream();
							if (inputStream != null) {
								Bitmap sourceBmp = BitmapFactory
										.decodeStream(inputStream);
								if (sourceBmp != null) {
									bitmap = Utils.centerSquareScaleBitmap(
											sourceBmp, 200);
								}
							}
						}
					} catch (Exception e) {
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.mipmap.ic_launcher);
					}
				} else {
					bitmap = BitmapFactory.decodeResource(getResources(),
							R.mipmap.ic_launcher);
				}
				handler.sendMessage(handler.obtainMessage(SHARE_BITMAP, getThumbImage(bitmap)));
			}
		});
	}

	private void handleRequest(){
		String icon = request.getIcon();
		String title = request.getTitle();
		String content = request.getContent();
		String url = request.getUrl();
		ShareManager.Platform platform = request.getPlatform();

		if (Utils.isEmpty(title) || Utils.isEmpty(content)
				|| Utils.isEmpty(url)) {
			postShareResponse(ShareResponse.Status.FAILED, ShareResponse.ErrCode.ERR_INVALID_PARAMS,"无效的参数");
			return;
		}
		url.replaceAll(" ", "");
		switch (platform){
			case QQ:
				shareToQQ(title,content,url,icon);
				break;
			case QZONE:
				shareToQzone(title,content,url,icon);
				break;
			case WEIBO:
				shareToWeibo(title,content,url,shareImageThumb);
				break;
			case WXSCENESESSION:
				shareToWeiXin(title,content,url,shareImageThumb, ShareManager.Platform.WXSCENESESSION);
				break;
			case WXTIMELINE:
				shareToWeiXin(title,content,url,shareImageThumb, ShareManager.Platform.WXTIMELINE);
				break;
			case SMS:
				shareToSms(title,content,url);
				break;
			default:
				postShareResponse(ShareResponse.Status.FAILED, ShareResponse.ErrCode.ERR_INVALID_PARAMS,"无效的参数");
				break;
		}
	}

	//分享到QQ
	private void shareToQQ(String title, String content, String url, String icon) {
		Tencent tencent = Tencent.createInstance(MainConstants.QQ_APPID, this);
		Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, icon);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getResources()
				.getString(R.string.app_name));
		tencent.shareToQQ(this, params, shareQQListener);
	}

	//分享到Qzone
	private void shareToQzone(String title, String content, String url, String icon){
		Tencent tencent = Tencent.createInstance(MainConstants.QQ_APPID, this);
		Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
				QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 必填
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);// 选填
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);// 必填
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(icon);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		tencent.shareToQzone(this, params, shareQQListener);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (weiboAPI == null) {
			weiboAPI = WeiboShareSDK.createWeiboAPI(this,
					MainConstants.WB_APPID);
			weiboAPI.registerApp();
		}
		weiboAPI.handleWeiboResponse(intent, this);
		super.onNewIntent(intent);
	}

	//微博
	private void shareToWeibo(final String title, final String content,
							  final String url, Bitmap shareBmp){
		if(shareBmp == null || shareBmp.isRecycled()){
			shareBmp = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
		}
		if(weiboAPI == null){
			weiboAPI = WeiboShareSDK.createWeiboAPI(this,
					MainConstants.WB_APPID);
			weiboAPI.registerApp();
			weiboAPI.handleWeiboResponse(getIntent(), this);
		}
		WebpageObject mediaObject = new WebpageObject();
		mediaObject.identify = Utility.generateGUID();
		mediaObject.title = title;
		mediaObject.description = content;
		// 设置 Bitmap 类型的图片到视频对象里 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
		mediaObject.setThumbImage(shareBmp);
		mediaObject.actionUrl = url;
		mediaObject.defaultText = content;

		// 2. 初始化从第三方到微博的消息请求
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		weiboMessage.mediaObject = mediaObject;
		final SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		AuthInfo authInfo = new AuthInfo(this,
				MainConstants.WB_APPID, MainConstants.WB_REDIRECT_URL,
				MainConstants.WB_SCOPE);
		ssoHandler = new SsoHandler(this,authInfo);
		ssoHandler.authorize(new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle bundle) {
				weiboAPI.sendRequest(ShareActivity.this,request);
			}

			@Override
			public void onWeiboException(WeiboException e) {
				postShareResponse(ShareResponse.Status.FAILED,-1,e.getMessage());
			}

			@Override
			public void onCancel() {
				postShareResponse(ShareResponse.Status.CANCEL);
			}
		});

	}

	//短信分享
	private void shareToSms(String title, String content, String url) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		sendIntent.putExtra("sms_body", title + content + url);
		startActivity(sendIntent);
		postShareResponse(ShareResponse.Status.SUCCEED);
	}

	//微信
	private void shareToWeiXin(final String title, final String content,
							   final String url, Bitmap shareBmp, final ShareManager.Platform platform) {
		final IWXAPI api = WXAPIFactory.createWXAPI(this,
				MainConstants.WX_APPID, true);
		if (!api.isWXAppInstalled()) {
			postShareResponse(ShareResponse.Status.FAILED, ShareResponse.ErrCode.ERR_WX_NOT_INSTALLED,"未安装微信");
			return;
		}
		if (platform == ShareManager.Platform.WXTIMELINE) {
			if (api.getWXAppSupportAPI() < Build.TIMELINE_SUPPORTED_SDK_INT) {
				postShareResponse(ShareResponse.Status.FAILED, ShareResponse.ErrCode.ERR_TIMELINE_NOT_SUPPORTED, "您的微信版本过低，不支持分享到朋友圈");
				return;
			}
		}

		if(shareBmp == null || shareBmp.isRecycled()){
			shareBmp = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
		}

		wxReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (MainConstants.ACTION_WX_SHARE.equals(intent.getAction())) {
					int errCode = intent.getIntExtra("errCode",0);
					String errStr = intent.getStringExtra("errStr");
					switch (errCode){
						case BaseResp.ErrCode.ERR_OK:
							postShareResponse(ShareResponse.Status.SUCCEED);
							break;
						case BaseResp.ErrCode.ERR_USER_CANCEL:
							postShareResponse(ShareResponse.Status.CANCEL);
							break;
						default:
							postShareResponse(ShareResponse.Status.FAILED,errCode,errStr);
							break;
					}
				}
			}
		};
		IntentFilter wxfilter = new IntentFilter(MainConstants.ACTION_WX_SHARE);
		registerReceiver(wxReceiver, wxfilter);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = content;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		shareBmp.compress(Bitmap.CompressFormat.PNG,100,out);
		msg.thumbData = out.toByteArray();
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = platform == ShareManager.Platform.WXSCENESESSION ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}

	@Override
	protected void onDestroy() {
		if(wxReceiver != null){
			this.unregisterReceiver(wxReceiver);
		}
		if(loadingDialog != null && loadingDialog.isShowing()){
			loadingDialog.dismiss();
		}
		super.onDestroy();
	}


	private Bitmap getThumbImage(Bitmap src) {
		// 原图小于32K？
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		src.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] srcThumb = stream.toByteArray();
		if (srcThumb.length < 32 * 1024) {
			return src;
		}

		// 缩小一半大小
		ByteArrayOutputStream baos = null;
		int quality = 100;
		byte[] thumb = null;
		do {
			baos = new ByteArrayOutputStream();
			src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			thumb = baos.toByteArray();
			quality -= 10;
		} while (thumb.length > 32 * 1024);
		return BitmapFactory.decodeByteArray(thumb,0,thumb.length);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_QQ_SHARE
				|| requestCode == Constants.REQUEST_QZONE_SHARE) {
			Tencent.onActivityResultData(requestCode, resultCode, data,
					shareQQListener);
		}
		if(ssoHandler !=null){
			ssoHandler.authorizeCallBack(requestCode,resultCode,data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 返回分享结果
	 * @param status
	 * @param errCode
	 * @param errMsg
	 */
	private void postShareResponse(ShareResponse.Status status,int errCode,String errMsg){
		ShareResponse response = new ShareResponse();
		response.setSession(request.getSession());
		response.setPlatform(request.getPlatform());
		response.setStatus(status);
		response.setErrCode(errCode);
		response.setErrMessage(errMsg);
		ShareManager.getInstance().postResponse(response);
		finish();
	}

	private void postShareResponse(ShareResponse.Status status){
		postShareResponse(status,0,"");
	}


	@Override
	public void onResponse(BaseResponse baseResponse) {
		int errCode = baseResponse.errCode;
		switch (errCode){
			case WBConstants.ErrorCode.ERR_OK:
				postShareResponse(ShareResponse.Status.SUCCEED);
				break;
			case WBConstants.ErrorCode.ERR_CANCEL:
				postShareResponse(ShareResponse.Status.CANCEL);
				break;
			default:
				postShareResponse(ShareResponse.Status.FAILED,baseResponse.errCode,baseResponse.errMsg);
				break;
		}
	}
}
