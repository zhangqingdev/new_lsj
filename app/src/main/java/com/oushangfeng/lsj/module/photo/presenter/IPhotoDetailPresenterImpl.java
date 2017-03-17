package com.oushangfeng.lsj.module.photo.presenter;

import com.oushangfeng.lsj.base.BasePresenterImpl;
import com.oushangfeng.lsj.bean.SinaPhotoDetail;
import com.oushangfeng.lsj.module.photo.model.IPhotoDetailInteractor;
import com.oushangfeng.lsj.module.photo.model.IPhotoDetailInteractorImpl;
import com.oushangfeng.lsj.module.photo.view.IPhotoDetailView;

/**
 * ClassName: IPhotoDetailPresenterImpl<p>
 * Fuction: 图片详情代理接口实现<p>
 * UpdateDate: <p>
 */
public class IPhotoDetailPresenterImpl extends BasePresenterImpl<IPhotoDetailView, SinaPhotoDetail>
        implements IPhotoDetailPresenter {

    private IPhotoDetailInteractor<SinaPhotoDetail> mDetailInteractor;

    public IPhotoDetailPresenterImpl(IPhotoDetailView view, String id, SinaPhotoDetail data) {
        super(view);
        mDetailInteractor = new IPhotoDetailInteractorImpl();
        if (data != null) {
            mView.initViewPager(data);
        } else {
            mSubscription = mDetailInteractor.requestPhotoDetail(this, id);
        }
    }

    @Override
    public void requestSuccess(SinaPhotoDetail data) {
        mView.initViewPager(data);
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
        mView.toast(msg);
    }
}
