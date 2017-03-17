package com.oushangfeng.lsj.module.news.model;

import com.oushangfeng.lsj.callback.RequestCallback;

import rx.Subscription;

public interface INewsListInteractor<T> {

    Subscription requestNewsList(RequestCallback<T> callback, String type, String id, int startPage);

}
