package com.oushangfeng.lsj.module.news.presenter;

import com.oushangfeng.lsj.base.BasePresenter;

/**
 * Created by zhangqing on 2017/3/25.
 */

public interface ILSJNewsPresenter extends BasePresenter {

    void refreshData();

    void loadMoreData();
}
