package com.oushangfeng.lsj.module.photo.model;

import com.oushangfeng.lsj.callback.RequestCallback;

import rx.Subscription;

/**
 * ClassName: IPhotoListInteractor<p>
 * Fuction: 图片列表Model层接口<p>
 * UpdateUser: <p>
 */
public interface IPhotoListInteractor<T> {

    Subscription requestPhotoList(RequestCallback<T> callback, String id, int startPage);

}
