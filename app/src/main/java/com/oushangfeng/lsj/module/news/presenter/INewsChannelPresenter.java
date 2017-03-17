package com.oushangfeng.lsj.module.news.presenter;

import com.oushangfeng.lsj.base.BasePresenter;

public interface INewsChannelPresenter extends BasePresenter {

    void onItemAddOrRemove(String channelName, boolean selectState);

    void onItemSwap(int fromPos, int toPos);

}
