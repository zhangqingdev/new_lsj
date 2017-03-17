package com.oushangfeng.lsj.module.news.view;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.greendao.NewsChannelTable;

import java.util.List;

/**
 * ClassName: INewsView<p>
 * Fuction: 新闻视图接口<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
public interface INewsView extends BaseView {

    void initViewPager(List<NewsChannelTable> newsChannels);

    void initRxBusEvent();

}
