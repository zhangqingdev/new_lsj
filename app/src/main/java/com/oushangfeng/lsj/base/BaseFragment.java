package com.oushangfeng.lsj.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.app.App;
import com.oushangfeng.lsj.module.news.ui.NewsDetailActivity;
import com.oushangfeng.lsj.utils.RxBus;
import com.oushangfeng.lsj.widget.refresh.RefreshLayout;
import com.squareup.leakcanary.RefWatcher;

import rx.Observable;
import rx.functions.Action1;


public abstract class BaseFragment<T extends BasePresenter> extends Fragment
        implements BaseView, View.OnClickListener {

    // 一般作为ViewPager承载的Fragment都会把位置索引传过来，这里放到基类，
    // 方便下面的initRefreshLayoutOrRecyclerViewEvent()方法处理订阅事件
    protected int mPosition;

    // 将代理类通用行为抽出来
    protected T mPresenter;

    protected View mFragmentRootView;
    protected int mContentViewId;

    /**
     * 监听AppbarLayout偏移量
     */
    private Observable<Object> mAppbarOffsetObservable;
    private RefreshLayout mRefreshLayout;

    // 是否处理RefreshLayout与AppbarLayout的冲突
    private boolean mHandleRefreshLayout;

    // 标示当前Fragment所在的Activity是否可见
    private boolean mIsStop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == mFragmentRootView) {
            if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
                ActivityFragmentInject annotation = getClass()
                        .getAnnotation(ActivityFragmentInject.class);
                mContentViewId = annotation.contentViewId();
                mHandleRefreshLayout = annotation.handleRefreshLayout();
            } else {
                throw new RuntimeException(
                        "Class must add annotations of ActivityFragmentInitParams.class");
            }
            mFragmentRootView = inflater.inflate(mContentViewId, container, false);

            if (mHandleRefreshLayout) {
                initRefreshLayoutOrRecyclerViewEvent();
            }

            initView(mFragmentRootView);
        }

        return mFragmentRootView;
    }

    /**
     * 订阅事件处理RefreshLayout
     */
    private void initRefreshLayoutOrRecyclerViewEvent() {
        mRefreshLayout = (RefreshLayout) mFragmentRootView.findViewById(R.id.refresh_layout);
        if (mRefreshLayout != null) {

            final RecyclerView recyclerView = (RecyclerView) mFragmentRootView
                    .findViewById(R.id.recycler_view);

            mAppbarOffsetObservable = RxBus.get()
                    .register("enableRefreshLayoutOrScrollRecyclerView", Object.class);
            mAppbarOffsetObservable.subscribe(new Action1<Object>() {
                @Override
                public void call(Object obj) {
                    if (obj instanceof Integer) {
                        if (!mIsStop && recyclerView != null && (Integer) obj == mPosition) {
                            // 当前Fragment所在的Activity可见并且是选中的Fragment才处理事件
                            recyclerView.smoothScrollToPosition(0);
                        }
                    } else if (obj instanceof Boolean) {
                        mRefreshLayout.setRefreshable((Boolean) obj);
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsStop = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsStop = false;
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) mFragmentRootView.getParent();
        if (null != parent) {
            parent.removeView(mFragmentRootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        if (mHandleRefreshLayout && mRefreshLayout != null && mAppbarOffsetObservable != null) {
            RxBus.get().unregister("enableRefreshLayoutOrScrollRecyclerView", mAppbarOffsetObservable);
        }
        // 使用 RefWatcher 监控 Fragment
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    public BaseFragment() {
    }

    protected abstract void initView(View fragmentRootView);

    /**
     * 当通过changeFragment()显示时会被调用(类似于onResume)
     */
    public void onChange() {
    }

    protected void showSnackbar(String msg) {
        Snackbar.make(mFragmentRootView, msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showSnackbar(int id) {
        Snackbar.make(mFragmentRootView, id, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 继承BaseView抽出显示信息通用行为
     *
     * @param msg
     */
    @Override
    public void toast(String msg) {
        showSnackbar(msg);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onClick(View v) {

    }

	public void startUrl(String url){
		if(TextUtils.isEmpty(url)){
			toast("这篇新闻浏览不了");
		}else {
			Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
			intent.putExtra("url",url);
			startActivity(intent);
		}
	}
}
