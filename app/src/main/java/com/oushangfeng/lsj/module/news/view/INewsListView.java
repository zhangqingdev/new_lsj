package com.oushangfeng.lsj.module.news.view;

import android.support.annotation.NonNull;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.bean.NeteastNewsSummary;
import com.oushangfeng.lsj.common.DataLoadType;

import java.util.List;

/**
 * ClassName: INewsListView<p>
 * Fuction: 新闻列表视图接口<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
public interface INewsListView extends BaseView {

    void updateNewsList(List<NeteastNewsSummary> data, @NonNull String errorMsg, @DataLoadType.DataLoadTypeChecker int type);

}
