package com.oushangfeng.lsj.module.photo.presenter;

import com.oushangfeng.lsj.base.BasePresenterImpl;
import com.oushangfeng.lsj.bean.IndexPhotoModel;
import com.oushangfeng.lsj.common.DataLoadType;
import com.oushangfeng.lsj.module.photo.model.ILSJIIndexPhotoList;
import com.oushangfeng.lsj.module.photo.model.ILSJIPhotosListImpl;
import com.oushangfeng.lsj.module.photo.view.ILSJPhotoListView;

/**
 * Created by zhangqing on 2017/3/24.
 */

public class ILSJPhotoListPresenterImpl extends BasePresenterImpl<ILSJPhotoListView,IndexPhotoModel> implements ILSJPhotoListPresenter {

    ILSJIIndexPhotoList<IndexPhotoModel> mPhotoListInteractor;
    private int mStartPage;
    private int mPhotoId;
    private boolean mIsRefresh = true;
    private boolean mHasInit;
    private String imei;
    public ILSJPhotoListPresenterImpl(ILSJPhotoListView view,String imei,int photoId,int startPage) {
        super(view);
        mStartPage = startPage;
        mPhotoId=photoId;
        this.imei=imei;
        mPhotoListInteractor=new ILSJIPhotosListImpl();
        mSubscription=mPhotoListInteractor.getIndexPhotos(this,imei,mPhotoId+"","10");
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
        mView.getPhotoList(null, e, mIsRefresh ? DataLoadType.TYPE_REFRESH_FAIL : DataLoadType.TYPE_LOAD_MORE_FAIL);

    }

    @Override
    public void refreshData() {
        mStartPage = 1;
        mIsRefresh = true;
		mPhotoId  = 0;
        mSubscription=mPhotoListInteractor.getIndexPhotos(this,imei,mPhotoId+"","10");
    }

    @Override
    public void loadPhotoData() {
        mIsRefresh = false;
		if(mPhotoId == -1){
			mView.getPhotoList(new IndexPhotoModel(),"",DataLoadType.TYPE_LOAD_MORE_SUCCESS);
		}else {
			mSubscription=mPhotoListInteractor.getIndexPhotos(this,imei,mPhotoId+"","10");
		}
    }

    @Override
    public void requestSuccess(IndexPhotoModel data) {
      //  if (data != null && data.size() > 0) {
       //     mStartPage++;
      //  }
		mPhotoId = data.lastMaxId;
        mView.getPhotoList(data, "", mIsRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS : DataLoadType.TYPE_LOAD_MORE_SUCCESS);
    }
}
