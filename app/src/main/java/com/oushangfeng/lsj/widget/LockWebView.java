package com.oushangfeng.lsj.widget;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.oushangfeng.lsj.utils.Utils;

public class LockWebView extends WebView {
	
	private Context mContext;
	private static final String USER_AGENT = "lsj";

	public LockWebView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public LockWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init() {
		WebSettings webSettings = this.getSettings();
		webSettings.setJavaScriptEnabled(true);
		String ua = webSettings.getUserAgentString()+"/"+USER_AGENT;
		webSettings.setUserAgentString(ua);
		// 防止JS注入
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			this.removeJavascriptInterface("searchBoxJavaBredge_");
		}
		// 开启缓存
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setAllowFileAccess(true);
		webSettings.setDatabaseEnabled(true);
		String dir = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setDatabasePath(dir);
		webSettings.setDomStorageEnabled(true);
		webSettings.setGeolocationEnabled(true);
		webSettings.setSupportMultipleWindows(true);
		webSettings.setUseWideViewPort(true);  
		webSettings.setSupportZoom(true);
		this.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				return super.onConsoleMessage(consoleMessage);
			}
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
									 JsResult result) {

				return super.onJsAlert(view, url, message, result);
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
									  String defaultValue, JsPromptResult result) {
				return super.onJsPrompt(view, url, message, defaultValue,
						result);
			}
		});
		
		this.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
										String contentDisposition, String mimetype,
										long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				if(Utils.isIntentAvailable(mContext, intent)){
					mContext.startActivity(intent);
				}else {
					Toast.makeText(mContext, "未检测到可用的浏览器，无法打开", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void setWebViewClient(WebViewClient client) {
		if(client instanceof LockWebViewClient == false){
			throw new IllegalArgumentException("u must use LockWebViewClient in LockWebView");
		}
		super.setWebViewClient(client);
	}
	
	

}
