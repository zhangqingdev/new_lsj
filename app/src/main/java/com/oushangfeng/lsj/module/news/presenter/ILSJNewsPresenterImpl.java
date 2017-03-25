package com.oushangfeng.lsj.module.news.presenter;

import com.oushangfeng.lsj.base.BasePresenterImpl;
import com.oushangfeng.lsj.bean.IndexPageModel;
import com.oushangfeng.lsj.common.DataLoadType;
import com.oushangfeng.lsj.module.news.model.ILSJIndexListNews;
import com.oushangfeng.lsj.module.news.model.ILSJIndexNewsListImpl;
import com.oushangfeng.lsj.module.news.view.ILSJNewsView;

import java.util.List;

/**
 * Created by zhangqing on 2017/3/25.
 */

public class ILSJNewsPresenterImpl extends BasePresenterImpl<ILSJNewsView,IndexPageModel> implements ILSJNewsPresenter {

    private ILSJIndexListNews<IndexPageModel> mNewsListInteractor;
    private String imei;
    private String mNewsId;
    private int mStartPage;

    private boolean mIsRefresh = true;
    private boolean mHasInit;
    public ILSJNewsPresenterImpl(ILSJNewsView view,String imei) {
        super(view);
        this.imei=imei;
        mNewsListInteractor=new ILSJIndexNewsListImpl();
        mSubscription=mNewsListInteractor.getNewsListObservable(this,imei) ;

    }

    @Override
    public void refreshData() {

    }

    @Override
    public void loadMoreData() {

    }

    @Override
    public void requestSuccess(IndexPageModel data) {
        mView.updateNewsList(data, "", mIsRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS : DataLoadType.TYPE_LOAD_MORE_SUCCESS);

    }
}
