package com.oushangfeng.lsj.module.news.model;

import com.oushangfeng.lsj.base.BaseSubscriber;
import com.oushangfeng.lsj.bean.NeteastNewsDetail;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.http.HostType;
import com.oushangfeng.lsj.http.manager.RetrofitManager;

import java.util.Map;

import rx.Subscription;
import rx.functions.Func1;


public class INewsDetailInteractorImpl implements INewsDetailInteractor<NeteastNewsDetail> {

    @Override
    public Subscription requestNewsDetail(final RequestCallback<NeteastNewsDetail> callback, final String id) {
        return RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO).getNewsDetailObservable(id)
                .map(new Func1<Map<String, NeteastNewsDetail>, NeteastNewsDetail>() {
                    @Override
                    public NeteastNewsDetail call(Map<String, NeteastNewsDetail> map) {
                        return map.get(id);
                    }
                }).subscribe(new BaseSubscriber<>(callback));
    }

}
