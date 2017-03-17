package com.oushangfeng.lsj.module.photo.presenter;

import com.oushangfeng.lsj.base.BasePresenter;

/**
 * ClassName: IPhotoListPresenter<p>
 * Fuction: 图片列表代理接口<p>
 * UpdateDate: <p>
 */
public interface IPhotoListPresenter extends BasePresenter {

    void refreshData();

    void loadMoreData();

}
