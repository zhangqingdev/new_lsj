package com.oushangfeng.lsj.module.photo.view;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.bean.SinaPhotoDetail;

/**
 * ClassName: IPhotoDetailView<p>
 * Fuction: 图片新闻详情视图接口<p>
 * UpdateDate: <p>
 */
public interface IPhotoDetailView extends BaseView {

    void initViewPager(SinaPhotoDetail photoList);

}
