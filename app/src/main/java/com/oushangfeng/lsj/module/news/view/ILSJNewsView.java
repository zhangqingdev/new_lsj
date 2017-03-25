package com.oushangfeng.lsj.module.news.view;

import android.support.annotation.NonNull;

import com.oushangfeng.lsj.base.BaseView;
import com.oushangfeng.lsj.bean.IndexPageModel;
import com.oushangfeng.lsj.bean.NeteastNewsSummary;
import com.oushangfeng.lsj.common.DataLoadType;

import java.util.List;

/**
 * Created by zhangqing on 2017/3/25.
 */

public interface ILSJNewsView extends BaseView {
    void updateNewsList(IndexPageModel data, @NonNull String errorMsg, @DataLoadType.DataLoadTypeChecker int type);
}
