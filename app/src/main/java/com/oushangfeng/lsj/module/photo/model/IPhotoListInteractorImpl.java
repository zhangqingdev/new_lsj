package com.oushangfeng.lsj.module.photo.model;

import com.oushangfeng.lsj.base.BaseSubscriber;
import com.oushangfeng.lsj.bean.SinaPhotoList;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.http.HostType;
import com.oushangfeng.lsj.http.manager.RetrofitManager;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * ClassName: IPhotoListInteractorImpl<p>
 * Fuction: 图片列表Model层接口实现<p>
 * UpdateUser: <p>
 */
public class IPhotoListInteractorImpl
        implements IPhotoListInteractor<List<SinaPhotoList.DataEntity.PhotoListEntity>> {
    @Override
    public Subscription requestPhotoList(final RequestCallback<List<SinaPhotoList.DataEntity.PhotoListEntity>> callback, String id, int startPage) {
        return RetrofitManager.getInstance(HostType.SINA_NEWS_PHOTO)
                .getSinaPhotoListObservable(id, startPage)
                .flatMap(
                        new Func1<SinaPhotoList, Observable<SinaPhotoList.DataEntity.PhotoListEntity>>() {
                            @Override
                            public Observable<SinaPhotoList.DataEntity.PhotoListEntity> call(SinaPhotoList sinaPhotoList) {
                                return Observable.from(sinaPhotoList.data.list);
                            }
                        })
                .toSortedList(
                        new Func2<SinaPhotoList.DataEntity.PhotoListEntity, SinaPhotoList.DataEntity.PhotoListEntity, Integer>() {
                            @Override
                            public Integer call(SinaPhotoList.DataEntity.PhotoListEntity photoListEntity, SinaPhotoList.DataEntity.PhotoListEntity photoListEntity2) {
                                return photoListEntity2.pubDate > photoListEntity.pubDate ? 1 : photoListEntity.pubDate == photoListEntity2.pubDate ? 0 : -1;
                            }
                        })
                .subscribe(new BaseSubscriber<>(callback));
    }

}
