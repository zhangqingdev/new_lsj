package com.oushangfeng.lsj.module.photo.view;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.bean.IndexPhotoModel;

/**
 * Created by zhangqing on 2017/3/24.
 */

public interface ILSJPhotoListView extends BaseView {
    void getPhotoList(IndexPhotoModel data);
}
