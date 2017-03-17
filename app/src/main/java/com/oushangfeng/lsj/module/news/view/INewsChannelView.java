package com.oushangfeng.lsj.module.news.view;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.greendao.NewsChannelTable;

import java.util.List;

/**
 * ClassName: INewsChannelView<p>
 * Fuction: 新闻频道管理视图接口<p>
 * UpdateUser: <p>
 */
public interface INewsChannelView extends BaseView {

    void initTwoRecyclerView(List<NewsChannelTable> selectChannels, List<NewsChannelTable> unSelectChannels);

}
