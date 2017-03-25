package com.oushangfeng.lsj.widget;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.oushangfeng.lsj.utils.MainConstants;

import java.net.URISyntaxException;

public class LockWebViewClient extends WebViewClient {
	
	private Context mContext;

	public LockWebViewClient(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if(!url.startsWith("http")&&!url.startsWith(MainConstants.SCHEME)){
			try {
				Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
				intent.addCategory("android.intent.category.BROWSABLE");  
				if(intent.resolveActivity(mContext.getPackageManager()) != null){
					mContext.startActivity(intent);
				}
				return true;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}  
		}
		return super.shouldOverrideUrlLoading(view, url);
	}
	
}