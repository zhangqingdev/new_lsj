package com.oushangfeng.lsj.module.photo.presenter;

import com.oushangfeng.lsj.base.BasePresenter;

/**
 * Created by zhangqing on 2017/3/24.
 */

public interface ILSJPhotoListPresenter extends BasePresenter {

    void refreshData();

    void loadPhotoData();
}
