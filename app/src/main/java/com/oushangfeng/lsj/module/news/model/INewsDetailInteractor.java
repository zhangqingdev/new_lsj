package com.oushangfeng.lsj.module.news.model;

import com.oushangfeng.lsj.callback.RequestCallback;

import rx.Subscription;

public interface INewsDetailInteractor<T> {

    Subscription requestNewsDetail(RequestCallback<T> callback, String id);

}
