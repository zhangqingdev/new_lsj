package com.oushangfeng.lsj.module.news.model;

import com.oushangfeng.lsj.callback.RequestCallback;

import rx.Subscription;

public interface INewsInteractor<T> {

    Subscription operateChannelDb(RequestCallback<T> callback);

}
