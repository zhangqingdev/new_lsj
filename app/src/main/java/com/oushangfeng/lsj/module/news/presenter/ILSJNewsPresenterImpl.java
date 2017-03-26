package com.oushangfeng.lsj.module.news.presenter;

import com.oushangfeng.lsj.base.BasePresenterImpl;
import com.oushangfeng.lsj.base.BaseSubscriber;
import com.oushangfeng.lsj.bean.IndexPageBannerModel;
import com.oushangfeng.lsj.bean.IndexPageModel;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.common.DataLoadType;
import com.oushangfeng.lsj.http.manager.LSJRetrofitManager;
import com.oushangfeng.lsj.module.news.model.ILSJIndexListNews;
import com.oushangfeng.lsj.module.news.model.ILSJIndexNewsListImpl;
import com.oushangfeng.lsj.module.news.view.ILSJNewsView;

import java.util.List;

import rx.Subscription;

/**
 * Created by zhangqing on 2017/3/25.
 */

public class ILSJNewsPresenterImpl extends BasePresenterImpl<ILSJNewsView,IndexPageModel> implements ILSJNewsPresenter {

    private ILSJIndexListNews<IndexPageModel> mNewsListInteractor;
    private String imei;
    private int mNewsId;
    private int mStartPage=10;

    private boolean mIsRefresh = true;
    private boolean mHasInit;
    public ILSJNewsPresenterImpl(ILSJNewsView view,String imei,int id) {
        super(view);
        this.imei=imei;
         this.mNewsId=id;
        mNewsListInteractor=new ILSJIndexNewsListImpl();
        mSubscription=mNewsListInteractor.getLastNewsList(this,imei,"0","10");
    }

    @Override
    public void beforeRequest() {
        if (!mHasInit) {
            mHasInit = true;
            mView.showProgress();
        }
    }

    @Override
    public void requestError(String e) {
        super.requestError(e);
        mView.getIndexNewsList(null, e, mIsRefresh ? DataLoadType.TYPE_REFRESH_FAIL : DataLoadType.TYPE_LOAD_MORE_FAIL);
    }


    @Override
    public void refreshData() {
		mIsRefresh = true;
		mNewsId = 0;
        mSubscription=mNewsListInteractor.getLastNewsList(this,imei,mNewsId+"",mStartPage+"") ;
    }

    @Override
    public void loadMoreData() {
		mIsRefresh = false;
		if(mNewsId == -1){
			mView.getIndexNewsList(new IndexPageModel(),"",DataLoadType.TYPE_LOAD_MORE_SUCCESS);
		}else {
			mSubscription=mNewsListInteractor.getLastNewsList(this,imei,mNewsId+"",mStartPage+"") ;
		}

    }

    @Override
    public void requestSuccess(IndexPageModel data) {
		mNewsId = data.lastMaxId;
        mView.getIndexNewsList(data, "", mIsRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS : DataLoadType.TYPE_LOAD_MORE_SUCCESS);

    }

	public Subscription getIndexBannerList(RequestCallback<List<IndexPageBannerModel>> callback, String imei, String pageSize){
		return LSJRetrofitManager.getInstance(0).getIndexBannerList(imei,pageSize).subscribe(new BaseSubscriber<>(callback));

	}


}
