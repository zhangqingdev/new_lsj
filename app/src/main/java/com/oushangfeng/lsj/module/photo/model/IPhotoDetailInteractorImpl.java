package com.oushangfeng.lsj.module.photo.model;

import com.oushangfeng.lsj.base.BaseSubscriber;
import com.oushangfeng.lsj.bean.SinaPhotoDetail;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.http.HostType;
import com.oushangfeng.lsj.http.manager.RetrofitManager;

import rx.Subscription;

/**
 * ClassName: IPhotoDetailInteractorImpl<p>
 * Fuction: 图片详情的Model层接口实现<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
public class IPhotoDetailInteractorImpl implements IPhotoDetailInteractor<SinaPhotoDetail> {
    @Override
    public Subscription requestPhotoDetail(final RequestCallback<SinaPhotoDetail> callback, String id) {
        return RetrofitManager.getInstance(HostType.SINA_NEWS_PHOTO).getSinaPhotoDetailObservable(id)
                .subscribe(new BaseSubscriber<>(callback));
    }
}
