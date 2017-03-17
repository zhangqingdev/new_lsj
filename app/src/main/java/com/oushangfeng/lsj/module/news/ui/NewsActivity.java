package com.oushangfeng.lsj.module.news.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.app.AppManager;
import com.oushangfeng.lsj.base.BaseActivity;
import com.oushangfeng.lsj.base.BaseFragment;
import com.oushangfeng.lsj.base.BaseFragmentAdapter;
import com.oushangfeng.lsj.greendao.NewsChannelTable;
import com.oushangfeng.lsj.module.news.presenter.INewsPresenter;
import com.oushangfeng.lsj.module.news.presenter.INewsPresenterImpl;
import com.oushangfeng.lsj.module.news.view.INewsView;
import com.oushangfeng.lsj.utils.RxBus;
import com.oushangfeng.lsj.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * ClassName: NewsActivity<p>
 * Fuction: 新闻界面<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_news,
        menuId = R.menu.menu_news,
        hasNavigationView = true,
        toolbarTitle = R.string.news,
        toolbarIndicator = R.drawable.ic_list_white,
        menuDefaultCheckedItem = R.id.action_info)
public class NewsActivity extends BaseActivity<INewsPresenter> implements INewsView {

    private Observable<Boolean> mChannelObservable;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister("channelChange", mChannelObservable);
    }

    @Override
    protected void initView() {

        // 设了默认的windowBackground使得冷启动没那么突兀，这里再设置为空减少过度绘制
        getWindow().setBackgroundDrawable(null);
        ViewUtil.quitFullScreen(this);

        AppManager.getAppManager().orderNavActivity(getClass().getName(), false);
        mPresenter = new INewsPresenterImpl(this);

    }

    @Override
    public void initViewPager(List<NewsChannelTable> newsChannels) {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        List<BaseFragment> fragments = new ArrayList<>();
        final List<String> title = new ArrayList<>();

        if (newsChannels != null) {
            // 有除了固定的其他频道被选中，添加
            for (NewsChannelTable news : newsChannels) {
                final NewsListFragment fragment = NewsListFragment
                        .newInstance(news.getNewsChannelId(), news.getNewsChannelType(),
                                news.getNewsChannelIndex());

                fragments.add(fragment);
                title.add(news.getNewsChannelName());
            }

            if (viewPager.getAdapter() == null) {
                // 初始化ViewPager
                BaseFragmentAdapter adapter = new BaseFragmentAdapter(getSupportFragmentManager(),
                        fragments, title);
                viewPager.setAdapter(adapter);
            } else {
                final BaseFragmentAdapter adapter = (BaseFragmentAdapter) viewPager.getAdapter();
                adapter.updateFragments(fragments, title);
            }
            viewPager.setCurrentItem(0, false);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setScrollPosition(0, 0, true);
            // 根据Tab的长度动态设置TabLayout的模式
            ViewUtil.dynamicSetTabLayoutMode(tabLayout);

            setOnTabSelectEvent(viewPager, tabLayout);

        }
    }

    @Override
    public void initRxBusEvent() {
        mChannelObservable = RxBus.get().register("channelChange", Boolean.class);
        mChannelObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean change) {
                if (change) {
                    mPresenter.operateChannelDb();
                }
            }
        });
    }
}
