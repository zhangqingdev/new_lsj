package com.oushangfeng.lsj.module.photo.view;

import android.support.annotation.NonNull;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.bean.SinaPhotoList;
import com.oushangfeng.lsj.common.DataLoadType;

import java.util.List;

/**
 * ClassName: IPhotoListView<p>
 * Fuction: 图片新闻列表接口<p>
 * UpdateDate: <p>
 */
public interface IPhotoListView extends BaseView {

    void updatePhotoList(List<SinaPhotoList.DataEntity.PhotoListEntity> data, @NonNull String errorMsg, @DataLoadType.DataLoadTypeChecker int type);

}
