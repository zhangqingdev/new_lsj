package com.oushangfeng.lsj.module.news.presenter;

import com.oushangfeng.lsj.base.BasePresenter;

public interface INewsListPresenter extends BasePresenter{

    void refreshData();

    void loadMoreData();

}
