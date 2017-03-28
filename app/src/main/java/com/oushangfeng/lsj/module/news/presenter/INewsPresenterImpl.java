package com.oushangfeng.lsj.module.news.presenter;

import com.oushangfeng.lsj.base.BasePresenterImpl;
import com.oushangfeng.lsj.base.BaseSubscriber;
import com.oushangfeng.lsj.bean.InitModel;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.greendao.NewsChannelTable;
import com.oushangfeng.lsj.http.manager.LSJRetrofitManager;
import com.oushangfeng.lsj.module.news.model.INewsInteractor;
import com.oushangfeng.lsj.module.news.model.INewsInteractorImpl;
import com.oushangfeng.lsj.module.news.view.INewsView;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import rx.Subscription;

public class INewsPresenterImpl extends BasePresenterImpl<INewsView, List<NewsChannelTable>>
        implements INewsPresenter {

    private INewsInteractor<List<NewsChannelTable>> mNewsInteractor;

    public INewsPresenterImpl(INewsView newsView) {
        super(newsView);
        mNewsInteractor = new INewsInteractorImpl();
        mSubscription = mNewsInteractor.operateChannelDb(this);
        mView.initRxBusEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void requestSuccess(List<NewsChannelTable> data) {
        mView.initViewPager(data);
    }

    @Override
    public void operateChannelDb() {
        mSubscription = mNewsInteractor.operateChannelDb(this);
    }

	public Subscription init(RequestCallback<InitModel> callback, Map<String,String> map){
		return LSJRetrofitManager.getInstance(0).initEnv(map).subscribe(new BaseSubscriber<>(callback));

	}

	public Subscription downloadApk(RequestCallback<ResponseBody> callback,String url){
		return LSJRetrofitManager.getInstance(0).downloadApk(url).subscribe(new BaseSubscriber<>(callback));
	}
}
