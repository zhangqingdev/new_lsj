package com.oushangfeng.lsj.module.news.model;

import com.oushangfeng.lsj.callback.RequestCallback;

import retrofit2.http.Path;
import rx.Subscription;

/**
 * Created by zhangqing on 2017/3/24.
 */

public interface ILSJIIndexPhotoList<T> {
    public Subscription getIndexPhotos(RequestCallback<T> callback,String imei,String lastMaxId, String pageSize);
}
