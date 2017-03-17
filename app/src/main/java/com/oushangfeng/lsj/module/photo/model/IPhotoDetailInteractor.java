package com.oushangfeng.lsj.module.photo.model;

import com.oushangfeng.lsj.callback.RequestCallback;

import rx.Subscription;

/**
 * ClassName: IPhotoDetailInteractor<p>
 * Fuction: 图片详情的Model层接口<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
public interface IPhotoDetailInteractor<T> {

    Subscription requestPhotoDetail(RequestCallback<T> callback, String id);

}
