package com.oushangfeng.lsj.module.news.model;

import com.oushangfeng.lsj.callback.RequestCallback;

import rx.Subscription;

/**
 * Created by zhangqing on 2017/3/25.
 */

public interface ILSJIndexListNews<T> {
    public Subscription getNewsListObservable(RequestCallback<T> callback, String imei);

}
