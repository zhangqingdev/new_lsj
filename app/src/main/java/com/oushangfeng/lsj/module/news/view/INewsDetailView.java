package com.oushangfeng.lsj.module.news.view;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.bean.NeteastNewsDetail;

/**
 * ClassName: INewsDetailView<p>
 * Fuction: 新闻详情视图接口<p>
 * UpdateUser: <p>
 */
public interface INewsDetailView extends BaseView{

    void initNewsDetail(NeteastNewsDetail data);

}
