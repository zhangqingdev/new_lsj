package com.oushangfeng.lsj.module.news.model;

import com.oushangfeng.lsj.base.BaseSubscriber;
import com.oushangfeng.lsj.bean.NeteastNewsSummary;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.http.Api;
import com.oushangfeng.lsj.http.HostType;
import com.oushangfeng.lsj.http.manager.RetrofitManager;
import com.socks.library.KLog;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;

public class INewsListInteractorImpl implements INewsListInteractor<List<NeteastNewsSummary>> {

    @Override
    public Subscription requestNewsList(final RequestCallback<List<NeteastNewsSummary>> callback, String type, final String id, int startPage) {
        KLog.e("新闻列表：" + type + ";" + id);
        return RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO)
                .getNewsListObservable(type, id, startPage)
                .flatMap(
                        new Func1<Map<String, List<NeteastNewsSummary>>, Observable<NeteastNewsSummary>>() {
                            // map得到list转换成item
                            @Override
                            public Observable<NeteastNewsSummary> call(Map<String, List<NeteastNewsSummary>> map) {
                                if (id.equals(Api.HOUSE_ID)) {
                                    // 房产实际上针对地区的它的id与返回key不同
                                    return Observable.from(map.get("北京"));
                                }
                                return Observable.from(map.get(id));
                            }
                        })
                .toSortedList(new Func2<NeteastNewsSummary, NeteastNewsSummary, Integer>() {
                    // 按时间先后排序
                    @Override
                    public Integer call(NeteastNewsSummary neteastNewsSummary, NeteastNewsSummary neteastNewsSummary2) {
                        return neteastNewsSummary2.ptime.compareTo(neteastNewsSummary.ptime);
                    }
                }).subscribe(new BaseSubscriber<>(callback));
    }
}
