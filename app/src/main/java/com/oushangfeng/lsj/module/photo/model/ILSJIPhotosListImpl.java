package com.oushangfeng.lsj.module.photo.model;

import com.oushangfeng.lsj.base.BaseSubscriber;
import com.oushangfeng.lsj.bean.IndexPhotoModel;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.http.manager.LSJRetrofitManager;

import rx.Subscription;

/**
 * Created by zhangqing on 2017/3/24.
 */

public class ILSJIPhotosListImpl implements ILSJIIndexPhotoList<IndexPhotoModel> {
    @Override
    public Subscription getIndexPhotos(final RequestCallback<IndexPhotoModel> callback,String imei,String lastMaxId, String pageSize) {
        return LSJRetrofitManager.getInstance(0).getPhotoListObservable(imei,lastMaxId,pageSize).subscribe(new BaseSubscriber<>(callback));
    }
}
