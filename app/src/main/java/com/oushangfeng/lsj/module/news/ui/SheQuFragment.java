package com.oushangfeng.lsj.module.news.ui;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.base.BaseFragment;
import com.oushangfeng.lsj.widget.LockWebView;
import com.oushangfeng.lsj.widget.LockWebViewClient;
import com.oushangfeng.lsj.widget.ThreePointLoadingView;
import com.oushangfeng.lsj.widget.refresh.RefreshLayout;

/**
 * Created by wudi on 17/4/25.
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_shequ,
		handleRefreshLayout = true)
public class SheQuFragment extends BaseFragment {
	ThreePointLoadingView mLoadingView;
	private LockWebView mWebView;
//	private String url = "https://chaojilaosiji.kuaizhan.com";
	private String url = "http://www.kuaizhan.com/club/apiv1/forums/WPDLi-RQajc0yMMw/jump-to";
	private RefreshLayout mRefreshLayout;

	@Override
	protected void initView(View fragmentRootView) {
		mLoadingView = (ThreePointLoadingView) fragmentRootView.findViewById(R.id.tpl_view);
		mLoadingView.setOnClickListener(this);
		mRefreshLayout = (RefreshLayout) fragmentRootView.findViewById(R.id.refresh_layout);
		mRefreshLayout.setRefreshListener(new RefreshLayout.OnRefreshListener() {
			@Override
			public void onRefreshing() {
				mWebView.loadUrl(mWebView.getUrl());
			}
		});
		mWebView = (LockWebView)fragmentRootView.findViewById(R.id.webview);
		mWebView.setWebViewClient(new LockWebViewClient(getActivity()){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mLoadingView.stop();
				mWebView.setVisibility(View.VISIBLE);
				mRefreshLayout.refreshFinish();
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mLoadingView.play();
				mWebView.setVisibility(View.INVISIBLE);
				super.onPageStarted(view, url, favicon);
			}
		});



//        mPresenter = new INewsDetailPresenterImpl(this, getIntent().getStringExtra("postid"));

		mWebView.loadUrl(url);
		mWebView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK){
					if(mWebView.canGoBack()){
						mWebView.goBack();
						return true;
					}
				}
				return false;
			}
		});
	}
}
