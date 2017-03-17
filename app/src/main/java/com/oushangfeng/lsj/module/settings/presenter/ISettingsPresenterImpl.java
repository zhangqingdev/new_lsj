package com.oushangfeng.lsj.module.settings.presenter;

import com.oushangfeng.lsj.base.BasePresenterImpl;
import com.oushangfeng.lsj.module.settings.view.ISettingsView;

public class ISettingsPresenterImpl extends BasePresenterImpl<ISettingsView, Void> implements ISettingsPresenter{

    public ISettingsPresenterImpl(ISettingsView view) {
        super(view);
        mView.initItemState();
    }
}
