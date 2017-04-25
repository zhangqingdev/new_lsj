package com.oushangfeng.lsj.module.news.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.app.AppManager;
import com.oushangfeng.lsj.base.BaseActivity;
import com.oushangfeng.lsj.base.BaseFragment;
import com.oushangfeng.lsj.base.BaseFragmentAdapter;
import com.oushangfeng.lsj.bean.InitModel;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.module.news.presenter.INewsPresenter;
import com.oushangfeng.lsj.module.news.presenter.INewsPresenterImpl;
import com.oushangfeng.lsj.module.news.view.INewsView;
import com.oushangfeng.lsj.module.photo.ui.PhotoListFragment;
import com.oushangfeng.lsj.utils.RxBus;
import com.oushangfeng.lsj.utils.Utils;
import com.oushangfeng.lsj.utils.ViewUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Action1;

import static com.oushangfeng.lsj.R.string.news;

/**
 * ClassName: NewsActivity<p>
 * Fuction: 新闻界面<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_news,
        menuId = R.menu.menu_news,
        hasNavigationView = true,
        toolbarTitle = news,
        toolbarIndicator = R.drawable.ic_toolbar_left
		)
public class NewsActivity extends BaseActivity<INewsPresenter> implements INewsView {

    private Observable<Boolean> mChannelObservable;

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        RxBus.get().unregister("channelChange", mChannelObservable);
    }

    @Override
    protected void initView() {

        // 设了默认的windowBackground使得冷启动没那么突兀，这里再设置为空减少过度绘制
        getWindow().setBackgroundDrawable(null);
        ViewUtil.quitFullScreen(this);

		//Umeng

		MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this.getApplicationContext(),"58d481eb5312dd0fc90000a4",Utils.getChannel(this), MobclickAgent.EScenarioType.E_UM_NORMAL,false));
		MobclickAgent.onEvent(this.getApplicationContext(),"open_home");

        AppManager.getAppManager().orderNavActivity(getClass().getName(), false);
        mPresenter = new INewsPresenterImpl(this);
		((INewsPresenterImpl)mPresenter).init(new RequestCallback<InitModel>() {
			@Override
			public void beforeRequest() {

			}

			@Override
			public void requestError(String msg) {

			}

			@Override
			public void requestComplete() {

			}

			@Override
			public void requestSuccess(InitModel data) {
				if(data != null && Utils.isEmpty(data.errorCode)){
					//test
//					data.client.update = true;
//					data.client.download = "http://7xtfm0.com1.z0.glb.clouddn.com/lsj-3.0-debug-.apk";
					//请求成功
					Utils.setPreferenceStr(NewsActivity.this,"feedback",data.feedback);
					Utils.setPreferenceStr(NewsActivity.this,"share_url",data.shareAppLink);
					if(data.client.update){
						File file = new File(getExternalFilesDir(null)+ File.separator+getPackageName()+".apk");
						if(file.exists()){
							//check and show
						}else {
							downloadApk(data.client.download);
						}
					}
					setUid("ID:"+data.uuid);
				}

			}
		}, Utils.getDeviceInfo(this));

		if(Utils.isNewVersionApk(this)){
			//版本升级
			showUpdateDialog();
		}
    }

	private void downloadApk(final String url){
		new Thread(){

			@Override
			public void run() {
				((INewsPresenterImpl) mPresenter).downloadApk(new RequestCallback<ResponseBody>() {
					@Override
					public void beforeRequest() {

					}

					@Override
					public void requestError(String msg) {

					}

					@Override
					public void requestComplete() {

					}

					@Override
					public void requestSuccess(ResponseBody data) {
						String fileName = getPackageName()+".apk";
						writeResponseBodyToDisk(data,fileName);
					}
				},url);
			}
		}.start();

	}


	private boolean writeResponseBodyToDisk(ResponseBody body,String fileName) {
		try {
			File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + fileName);

			InputStream inputStream = null;
			OutputStream outputStream = null;

			try {
				byte[] fileReader = new byte[4096];

				long fileSizeDownloaded = 0;

				inputStream = body.byteStream();
				outputStream = new FileOutputStream(futureStudioIconFile);

				while (true) {
					int read = inputStream.read(fileReader);

					if (read == -1) {
						break;
					}

					outputStream.write(fileReader, 0, read);

					fileSizeDownloaded += read;

				}

				outputStream.flush();

				return true;
			} catch (IOException e) {
				return false;
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}

				if (outputStream != null) {
					outputStream.close();
				}
			}
		} catch (IOException e) {
			return false;
		}
	}



    @Override
    public void initViewPager(List<String> newsChannels) {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        List<BaseFragment> fragments = new ArrayList<>();
        final List<String> title = new ArrayList<>();

        if (newsChannels != null) {
            // 有除了固定的其他频道被选中，添加
            for (String news : newsChannels) {
				if(news.equals("看图")){
					PhotoListFragment photoListFragment = PhotoListFragment.newInstance();
					fragments.add(photoListFragment);
					title.add(news);

				}else if(news.equals("社区")){
					SheQuFragment sheQuFragment = new SheQuFragment();
					fragments.add(sheQuFragment);
					title.add(news);
				}else {
					final NewsListFragment fragment = NewsListFragment
							.newInstance();
					fragments.add(fragment);
					title.add(news);
				}
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
