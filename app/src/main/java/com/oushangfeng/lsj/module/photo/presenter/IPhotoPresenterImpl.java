package com.oushangfeng.lsj.module.photo.presenter;

import com.oushangfeng.lsj.base.BasePresenterImpl;
import com.oushangfeng.lsj.module.photo.view.IPhotoView;

/**
 * ClassName: IPhotoPresenterImpl<p>
 * Fuction: 图片代理接口实现<p>
 * UpdateDate: <p>
 */
public class IPhotoPresenterImpl extends BasePresenterImpl<IPhotoView, Void> implements IPhotoPresenter{

    public IPhotoPresenterImpl(IPhotoView view) {
        super(view);
        view.initViewPager();
    }

}
