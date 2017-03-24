package com.oushangfeng.lsj.module.photo.view;

import android.support.annotation.NonNull;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.bean.IndexPhotoModel;
import com.oushangfeng.lsj.common.DataLoadType;

/**
 * Created by zhangqing on 2017/3/24.
 */

public interface ILSJPhotoListView extends BaseView {
    void getPhotoList(IndexPhotoModel data, @NonNull String errorMsg, @DataLoadType.DataLoadTypeChecker int type);
}
