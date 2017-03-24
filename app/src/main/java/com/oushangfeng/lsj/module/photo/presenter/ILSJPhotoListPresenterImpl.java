package com.oushangfeng.lsj.module.photo.presenter;

import com.oushangfeng.lsj.base.BasePresenterImpl;
import com.oushangfeng.lsj.bean.IndexPhotoModel;
import com.oushangfeng.lsj.module.photo.model.ILSJIIndexPhotoList;
import com.oushangfeng.lsj.module.photo.model.ILSJIPhotosListImpl;
import com.oushangfeng.lsj.module.photo.view.ILSJPhotoListView;

/**
 * Created by zhangqing on 2017/3/24.
 */

public class ILSJPhotoListPresenterImpl extends BasePresenterImpl<ILSJPhotoListView,IndexPhotoModel> implements ILSJPhotoListPresenter {

    ILSJIIndexPhotoList<IndexPhotoModel> mPhotoListInteractor;
    private int mStartPage;
    private String mPhotoId;
    private boolean mIsRefresh = true;
    private boolean mHasInit;
    private String imei;
    public ILSJPhotoListPresenterImpl(ILSJPhotoListView view,String imei,String photoId,int startPage) {
        super(view);
        mStartPage = startPage;
        mPhotoId=photoId;
        imei=imei;
        mPhotoListInteractor=new ILSJIPhotosListImpl();
        mSubscription=mPhotoListInteractor.getIndexPhotos(this,imei,mPhotoId,"10");
    }


    @Override
    public void beforeRequest() {
        if (!mHasInit) {
            mHasInit = true;
            mView.showProgress();
        }
    }

    @Override
    public void requestError(String e) {
        super.requestError(e);
        mView.getPhotoList(null);
    }

    @Override
    public void loadPhotoData() {
        mIsRefresh = false;
        mSubscription=mPhotoListInteractor.getIndexPhotos(this,imei,mPhotoId,"10");
    }

    @Override
    public void requestSuccess(IndexPhotoModel data) {
      //  if (data != null && data.size() > 0) {
       //     mStartPage++;
      //  }
      //  mView.updatePhotoList(data, "", mIsRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS : DataLoadType.TYPE_LOAD_MORE_SUCCESS);
     mView.getPhotoList(data);
    }
}
