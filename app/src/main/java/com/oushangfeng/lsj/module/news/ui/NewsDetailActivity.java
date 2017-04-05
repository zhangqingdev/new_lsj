package com.oushangfeng.lsj.module.news.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.base.BaseActivity;
import com.oushangfeng.lsj.bean.NeteastNewsDetail;
import com.oushangfeng.lsj.bean.PhotoModel;
import com.oushangfeng.lsj.module.news.presenter.INewsDetailPresenter;
import com.oushangfeng.lsj.module.news.view.INewsDetailView;
import com.oushangfeng.lsj.module.photo.ui.PhotoDetailActivity;
import com.oushangfeng.lsj.utils.MeasureUtil;
import com.oushangfeng.lsj.utils.Utils;
import com.oushangfeng.lsj.utils.ViewUtil;
import com.oushangfeng.lsj.widget.LockWebView;
import com.oushangfeng.lsj.widget.LockWebViewClient;
import com.oushangfeng.lsj.widget.ThreePointLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ClassName: NewsDetailActivity<p>
 * Fuction: 新闻详情界面<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_news_detail,
        menuId = R.menu.menu_settings,
        enableSlidr = false,
		toolbarTitle = R.string.news_detail)
public class NewsDetailActivity extends BaseActivity<INewsDetailPresenter> implements INewsDetailView {

    private ThreePointLoadingView mLoadingView;

	private LockWebView mWebView;

	private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // 设置全屏，并且不会Activity的布局让出状态栏的空间
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            ViewUtil.showStatusBar(this);
        }

        getWindow().setBackgroundDrawable(null);
		url = getIntent().getStringExtra("url");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // 4.4设置全屏并动态修改Toolbar的位置实现类5.0效果，确保布局延伸到状态栏的效果
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            mlp.topMargin = MeasureUtil.getStatusBarHeight(this);
        }


        mLoadingView = (ThreePointLoadingView) findViewById(R.id.tpl_view);
        mLoadingView.setOnClickListener(this);
		mWebView = (LockWebView)findViewById(R.id.webview);
		mWebView.setWebViewClient(new LockWebViewClient(this){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("lsj://gallery")){
					HashMap<String,String> params = Utils.getUrlParams(url);
					String data = URLDecoder.decode(params.get("data"));
					if(!Utils.isEmpty(data)){
						try {
							JSONObject jsonObject = new JSONObject(data);
							JSONArray jsonArray = jsonObject.getJSONArray("img");
							if(jsonArray != null && jsonArray.length() > 0){
								ArrayList<PhotoModel> photos = new ArrayList<>();
								for(int i = 0;i<jsonArray.length();i++){
									JSONObject item = jsonArray.getJSONObject(i);
									PhotoModel model = new PhotoModel();
									model.img = item.getString("url");
									photos.add(model);
								}
								Intent intent = new Intent(NewsDetailActivity.this, PhotoDetailActivity.class);
								intent.putExtra("data",photos);
								intent.putExtra("title","查看大图");
								intent.putExtra("index",jsonObject.optInt("index"));
								startActivity(intent);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mLoadingView.stop();
				mWebView.setVisibility(View.VISIBLE);
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
		String title = getIntent().getStringExtra("title");
		if(!Utils.isEmpty(title)){
			setToolbarTitle(title);
		}

    }

    @Override
    public void initNewsDetail(final NeteastNewsDetail data) {



    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
    }*/

    @Override
    public void showProgress() {
        mLoadingView.play();
    }

    @Override
    public void hideProgress() {
        mLoadingView.stop();
    }

    @Override
    public void onClick(View v) {

    }

}
