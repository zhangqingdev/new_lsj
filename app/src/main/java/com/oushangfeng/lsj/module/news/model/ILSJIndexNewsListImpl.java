package com.oushangfeng.lsj.module.news.model;

import com.oushangfeng.lsj.base.BaseSubscriber;
import com.oushangfeng.lsj.bean.IndexPageBannerModel;
import com.oushangfeng.lsj.bean.IndexPageModel;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.http.manager.LSJRetrofitManager;

import java.util.List;

import rx.Subscription;

/**
 * Created by zhangqing on 2017/3/25.
 */

public class ILSJIndexNewsListImpl implements ILSJIndexListNews<IndexPageModel> {

    public Subscription getIndexBannerList(RequestCallback<List<IndexPageBannerModel>> callback, String imei, String pageSize){
        return LSJRetrofitManager.getInstance(0).getIndexBannerList(imei,pageSize).subscribe(new BaseSubscriber<>(callback));

    }

    @Override
    public Subscription getNewsListObservable(RequestCallback<IndexPageModel> callback, String imei) {
        return LSJRetrofitManager.getInstance(0).getNewsListObservable(imei).subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription getLastNewsList(RequestCallback<IndexPageModel> callback, String imei, String lastMaxId, String pageSize) {
        return LSJRetrofitManager.getInstance(0).getgetLastNewsListObservable(imei,lastMaxId,pageSize).subscribe(new BaseSubscriber<>(callback));
    }
}
