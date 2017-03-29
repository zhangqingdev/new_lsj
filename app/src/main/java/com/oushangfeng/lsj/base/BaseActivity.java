package com.oushangfeng.lsj.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oubowu.slideback.SlideBackHelper;
import com.oubowu.slideback.SlideConfig;
import com.oubowu.slideback.widget.SlideBackLayout;
import com.oushangfeng.lsj.BuildConfig;
import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.app.App;
import com.oushangfeng.lsj.app.AppManager;
import com.oushangfeng.lsj.bean.IndexPageBannerModel;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.http.manager.LSJRetrofitManager;
import com.oushangfeng.lsj.module.news.ui.NewsActivity;
import com.oushangfeng.lsj.module.news.ui.NewsDetailActivity;
import com.oushangfeng.lsj.module.settings.ui.SettingsActivity;
import com.oushangfeng.lsj.utils.GlideCircleTransform;
import com.oushangfeng.lsj.utils.GlideUtils;
import com.oushangfeng.lsj.utils.MainConstants;
import com.oushangfeng.lsj.utils.MeasureUtil;
import com.oushangfeng.lsj.utils.RxBus;
import com.oushangfeng.lsj.utils.SpUtil;
import com.oushangfeng.lsj.utils.ThemeUtil;
import com.oushangfeng.lsj.utils.Utils;
import com.oushangfeng.lsj.utils.ViewUtil;
import com.oushangfeng.lsj.widget.LoadingDialog;
import com.socks.library.KLog;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.changeskin.SkinManager;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements View.OnClickListener, BaseView {

    /**
     * 将代理类通用行为抽出来
     */
    protected T mPresenter;

    /**
     * 标示该activity是否可滑动退出,默认false
     */
    protected boolean mEnableSlidr;

    /**
     * 布局的id
     */
    protected int mContentViewId;

    /**
     * 是否存在NavigationView
     */
    protected boolean mHasNavigationView;

    /**
     * 滑动布局
     */
    protected DrawerLayout mDrawerLayout;

    /**
     * 侧滑导航布局
     */
    protected NavigationView mNavigationView;

    /**
     * 菜单的id
     */
    private int mMenuId;

    /**
     * Toolbar标题
     */
    private int mToolbarTitle;

    /**
     * 默认选中的菜单项
     */
    private int mMenuDefaultCheckedItem;

    /**
     * Toolbar左侧按钮的样式
     */
    private int mToolbarIndicator;

    /**
     * 控制滑动与否的接口
     */
    //    protected SlidrInterface mSlidrInterface;
    protected SlideBackLayout mSlideBackLayout;

    /**
     * 结束Activity的可观测对象
     */
    private Observable<Boolean> mFinishObservable;

    /**
     * 跳转的类
     */
    private Class mClass;

	private Tencent mTencent;

	public LoadingDialog loadingDialog;

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					loadingDialog.dismiss();
					Toast.makeText(BaseActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
			}
			super.handleMessage(msg);
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		loadingDialog = new LoadingDialog(this);
        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
            mContentViewId = annotation.contentViewId();
            mEnableSlidr = annotation.enableSlidr();
            mHasNavigationView = annotation.hasNavigationView();
            mMenuId = annotation.menuId();
            mToolbarTitle = annotation.toolbarTitle();
            mToolbarIndicator = annotation.toolbarIndicator();
            mMenuDefaultCheckedItem = annotation.menuDefaultCheckedItem();
        } else {
            throw new RuntimeException("Class must add annotations of ActivityFragmentInitParams.class");
        }
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        if (this instanceof SettingsActivity) {
            SkinManager.getInstance().register(this);
        }

        initTheme();

        setContentView(mContentViewId);

        if (mEnableSlidr && !SpUtil.readBoolean("disableSlide")) {
            // 默认开启侧滑，默认是整个页码侧滑
            mSlideBackLayout = SlideBackHelper.attach(this, App.getActivityHelper(), new SlideConfig.Builder()
                    // 是否侧滑
                    .edgeOnly(SpUtil.readBoolean("enableSlideEdge"))
                    // 是否会屏幕旋转
                    .rotateScreen(false)
                    // 是否禁止侧滑
                    .lock(false)
                    // 侧滑的响应阈值，0~1，对应屏幕宽度*percent
                    .edgePercent(0.1f)
                    // 关闭页面的阈值，0~1，对应屏幕宽度*percent
                    .slideOutPercent(0.35f).create(), null);


        }

        if (mHasNavigationView) {
            initNavigationView();
            initFinishRxBus();
        }

        initToolbar();

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
//        LSJRetrofitManager.getInstance(0).getNewsListObservable("123").subscribe(new Subscriber<IndexPageModel>() {
//            @Override
//            public void onCompleted() {
//                Log.i("test","DAS");
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("test","DAS");
//
//            }
//
//            @Override
//            public void onNext(IndexPageModel indexPageModel) {
//               Log.i("test","DAS");
//            }
//        });


//        LSJRetrofitManager.getInstance(0).getgetLastNewsListObservable("123","3","10").subscribe(new Subscriber<IndexPageModel>() {
//            @Override
//            public void onCompleted() {
//                Log.i("test","DAS");
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("test","DAS");
//
//            }
//
//            @Override
//            public void onNext(IndexPageModel indexPageModel) {
//                Log.i("test","DAS");
//            }
//        });

//        LSJRetrofitManager.getInstance(0).getIndexBannerList("121","1").subscribe(new Subscriber<List<IndexPageBannerModel>>() {
//            @Override
//            public void onCompleted() {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void onNext(List<IndexPageBannerModel> indexPageBannerModels) {
//                Log.i("test","DAS");
//            }
//        });

//        LSJRetrofitManager.getInstance(0).getPhotoListObservable("1313","0","11").subscribe(new Subscriber<IndexPhotoModel>() {
//            @Override
//            public void onCompleted() {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void onNext(IndexPhotoModel indexPhotoModel) {
//                Log.i("test","DAS");
//            }
//        });

//        getIndexBannerList(new RequestCallback<List<IndexPageBannerModel>>() {
//            @Override
//            public void beforeRequest() {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void requestError(String msg) {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void requestComplete() {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void requestSuccess(List<IndexPageBannerModel> data) {
//                Log.i("test","DAS");
//            }
//        },"0","0");


//        LSJRetrofitManager.getInstance(0).initEnv(Utils.getDeviceInfo(this)).subscribe(new Subscriber<InitModel>() {
//            @Override
//            public void onCompleted() {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("test","DAS");
//            }
//
//            @Override
//            public void onNext(InitModel initModel) {
//                Log.i("test","DAS");
//            }
//        });
    }

    public Subscription getIndexBannerList(RequestCallback<List<IndexPageBannerModel>> callback, String imei, String pageSize){
        return LSJRetrofitManager.getInstance(0).getIndexBannerList(imei,pageSize).subscribe(new BaseSubscriber<>(callback));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (this instanceof SettingsActivity) {
            SkinManager.getInstance().unregister(this);
        }

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        if (mFinishObservable != null) {
            RxBus.get().unregister("finish", mFinishObservable);
        }

        ViewUtil.fixInputMethodManagerLeak(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (mHasNavigationView) {
            KLog.e("FLAG_ACTIVITY_REORDER_TO_FRONT标志位的重新排序去除跳转动画");
            overridePendingTransition(0, 0);
        }
    }

    /**
     * 初始化主题
     */
    private void initTheme() {
        if (this instanceof NewsActivity) {
            setTheme(SpUtil.readBoolean("enableNightMode") ? R.style.BaseAppThemeNight_LauncherAppTheme : R.style.BaseAppTheme_LauncherAppTheme);
        } else if (!mEnableSlidr && mHasNavigationView) {
            setTheme(SpUtil.readBoolean("enableNightMode") ? R.style.BaseAppThemeNight_AppTheme : R.style.BaseAppTheme_AppTheme);
        } else {
            setTheme(SpUtil.readBoolean("enableNightMode") ? R.style.BaseAppThemeNight_SlidrTheme : R.style.BaseAppTheme_SlidrTheme);
        }
    }

    private void initToolbar() {

        // 针对父布局非DrawerLayout的状态栏处理方式
        // 设置toolbar上面的View实现类状态栏效果，这里是因为状态栏设置为透明的了，而默认背景是白色的，不设的话状态栏处就是白色
        final View statusView = findViewById(R.id.status_view);
        if (statusView != null) {
            statusView.getLayoutParams().height = MeasureUtil.getStatusBarHeight(this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            // 24.0.0版本后导航图标会有默认的与标题的距离，这里设置去掉
			if(Build.VERSION.SDK_INT < 20){
				ViewGroup.LayoutParams params = toolbar.getLayoutParams();
				if(params instanceof RelativeLayout.LayoutParams){
					((RelativeLayout.LayoutParams)params).addRule(RelativeLayout.BELOW);
				}
			}
            toolbar.setContentInsetStartWithNavigation(0);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            if (mToolbarTitle != -1) {
                setToolbarTitle(mToolbarTitle);
            }
            if (mToolbarIndicator != -1) {
                setToolbarIndicator(mToolbarIndicator);
            } else {
                setToolbarIndicator(R.drawable.ic_menu_back);
            }
        }
    }

    protected void setToolbarIndicator(int resId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(resId);
        }
    }

    protected void setToolbarTitle(String str) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(str);
        }
    }

    protected void setToolbarTitle(int strId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(strId);
        }
    }

    protected ActionBar getToolbar() {
        return getSupportActionBar();
    }

    protected View getDecorView() {
        return getWindow().getDecorView();
    }

    protected abstract void initView();

    private void initNavigationView() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        handleStatusView();

        handleAppBarLayoutOffset();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
		mNavigationView.setItemIconTintList(null);
//        if (mMenuDefaultCheckedItem != -1 && mNavigationView != null) {
//            mNavigationView.setCheckedItem(mMenuDefaultCheckedItem);
//        }
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
//                if (item.isChecked()) {
//                    return true;
//                }
                switch (item.getItemId()) {
                    case R.id.action_invite_friend:
                        //邀请好友
                        //mClass = VideoActivity.class;
                        break;
                    case R.id.action_feed_back:
                         //意见反馈
						String url = Utils.getPreferenceStr(BaseActivity.this,"feedback","");
						if(!Utils.isEmpty(url)){
							Intent intent = new Intent(BaseActivity.this, NewsDetailActivity.class);
							intent.putExtra("url",url);
							intent.putExtra("title","意见反馈");
							startActivity(intent);
						}
                        break;
                    case R.id.action_update:
                        //检查更新
						if(Utils.isNewVersionApk(BaseActivity.this)){
							showUpdateDialog();
						}else {
							toast("当前已是最新版本");
						}
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        mNavigationView.post(new Runnable() {
            @Override
            public void run() {
                final ImageView imageView = (ImageView) BaseActivity.this.findViewById(R.id.avatar);
                if (imageView != null) {
                    GlideUtils.loadDefaultTransformation(R.drawable.ic_header, imageView, false, null, new GlideCircleTransform(imageView.getContext()), null);
                    //                    Glide.with(mNavigationView.getContext()).load(R.drawable.ic_header).animate(R.anim.image_load).transform(new GlideCircleTransform(mNavigationView.getContext()))
                    //                            .into(imageView);
                }
            }
        });
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mClass != null) {
                    showActivityReorderToFront(BaseActivity.this, mClass, false);
                    mClass = null;
                }
            }
        });

		mNavigationView.getHeaderView(0).findViewById(R.id.tv_qq).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				qqAuth();
			}
		});

    }

	IUiListener loginListener = new IUiListener() {

		@Override
		public void onError(UiError arg0) {
			handler.sendMessage(handler.obtainMessage(1, Utils.isEmpty(arg0.errorMessage) ? "QQ登录失败" : arg0.errorMessage));
		}

		@Override
		public void onComplete(Object arg0) {
			try {
				JSONObject data = (JSONObject) arg0;
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("rs", "qq");
				params.put("access_token", data.getString("access_token"));
				params.put("openid", data.getString("openid"));
				Log.e("info","qq:"+ params.toString());
			} catch (Exception e) {
				handler.sendMessage(handler.obtainMessage(1, "网络异常"));
			}
		}

		@Override
		public void onCancel() {
			loadingDialog.dismiss();
		}
	};

	protected void showUpdateDialog(){
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("版本升级").setMessage("发现新版本，请立即升级").setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				File file = new File(getExternalFilesDir(null)+ File.separator+getPackageName()+".apk");
				Utils.installApkFile(BaseActivity.this,file);
				dialogInterface.dismiss();
			}
		}).setCancelable(false).create();
		dialog.show();
	}

	public void  qqAuth() {
		loadingDialog.show("登录中...");
		if (mTencent == null) {
			mTencent = Tencent.createInstance(MainConstants.QQ_APPID, this);
		}
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_LOGIN) {
			Tencent.onActivityResultData(requestCode, resultCode, data,
					loginListener);
		} else {

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
     * 处理与AppBarLayout偏移量相关事件处理
     */
    private void handleAppBarLayoutOffset() {
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (appBarLayout != null && toolbar != null) {
            final int toolbarHeight = MeasureUtil.getToolbarHeight(this);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT || BaseActivity.this instanceof SettingsActivity) {
                        // appBarLayout偏移量为Toolbar高度，这里为了4.4往上滑的时候由于透明状态栏Toolbar遮不住看起来难看，
                        // 根据偏移量设置透明度制造出往上滑动逐渐消失的效果
                        toolbar.setAlpha((toolbarHeight + verticalOffset) * 1.0f / toolbarHeight);
                    }
                    // AppBarLayout没有偏移量时，告诉Fragment刷新控件可用
                    RxBus.get().post("enableRefreshLayoutOrScrollRecyclerView", verticalOffset == 0);
                }
            });
        }
    }

    /**
     * 处理布局延伸到状态栏，对4.4以上系统有效
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void handleStatusView() {
        // 针对4.4和SettingsActivity(因为要做换肤，而状态栏在5.0是设置为透明的，若不这样处理换肤时状态栏颜色不会变化)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT || this instanceof SettingsActivity) {

            // 生成一个状态栏大小的矩形
            View statusBarView = ViewUtil.createStatusView(this, ThemeUtil.getColor(this, R.attr.colorPrimary));
            // 添加 statusBarView 到布局中
            ViewGroup contentLayout = (ViewGroup) mDrawerLayout.getChildAt(0);
            contentLayout.addView(statusBarView, 0);
            // 内容布局不是 LinearLayout 时,设置margin或者padding top
            final View view = contentLayout.getChildAt(1);
            final int statusBarHeight = MeasureUtil.getStatusBarHeight(this);
            if (!(contentLayout instanceof LinearLayout) && view != null) {
                view.setPadding(0, statusBarHeight, 0, 0);
            }
            // 设置属性
            ViewGroup drawer = (ViewGroup) mDrawerLayout.getChildAt(1);
            mDrawerLayout.setFitsSystemWindows(false);
            contentLayout.setFitsSystemWindows(false);
            contentLayout.setClipToPadding(true);
            drawer.setFitsSystemWindows(false);

            if (this instanceof SettingsActivity) {
                // 因为要SettingsActivity做换肤，所以statusBarView也要设置
                statusBarView.setTag("skin:primary:background");
                view.setTag("skin:primary:background");
            }

        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // 5.0以上跟4.4统一，状态栏颜色和Toolbar的一致
            mDrawerLayout.setStatusBarBackgroundColor(ThemeUtil.getColor(this, R.attr.colorPrimary));
        }
    }

    /**
     * 订阅结束自己的事件，这里使用来结束导航的Activity
     */
    private void initFinishRxBus() {
        mFinishObservable = RxBus.get().register("finish", Boolean.class);
        mFinishObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean themeChange) {
                try {
                    if (themeChange && !AppManager.getAppManager().getCurrentNavActivity().getName().equals(BaseActivity.this.getClass().getName())) {
                        //  切换皮肤的做法是设置页面通过鸿洋大大的不重启换肤，其他后台导航页面的统统干掉，跳转回去的时候，
                        //  因为用了FLAG_ACTIVITY_REORDER_TO_FRONT，发现栈中无之前的activity存在了，就重启设置了主题，
                        // 这样一来就不会所有Activity都做无重启刷新控件更该主题造成的卡顿现象
                        finish();
                    } else if (!themeChange) {
                        // 这个是入口新闻页面退出时发起的通知所有导航页面退出的事件
                        finish();
                        AppManager.getAppManager().clear();
                        KLog.e("订阅事件结束：" + BaseActivity.this.getClass().getName());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    KLog.e("找不到此类");
                }
            }
        });
    }

    protected void showSnackbar(String msg) {
        Snackbar.make(getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showSnackbar(int id) {
        Snackbar.make(getDecorView(), id, Snackbar.LENGTH_SHORT).show();
    }

    public void showActivityReorderToFront(Activity aty, Class<?> cls, boolean backPress) {

        App.getActivityHelper().addActivity(cls);

        AppManager.getAppManager().orderNavActivity(cls.getName(), backPress);

        Intent intent = new Intent();
        intent.setClass(aty, cls);
        // 此标志用于启动一个Activity的时候，若栈中存在此Activity实例，则把它调到栈顶。不创建多一个
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        aty.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void showActivity(Activity aty, Intent it) {
        aty.startActivity(it);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(mMenuId, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerLayout != null && item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == android.R.id.home && mToolbarIndicator == -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                // 返回键时未关闭侧栏时关闭侧栏
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (!(this instanceof NewsActivity) && mHasNavigationView) {
                try {
                    showActivityReorderToFront(this, AppManager.getAppManager().getLastNavActivity(), true);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    KLog.e("找不到类名啊");
                }
                return true;
            } else if (this instanceof NewsActivity) {
                // NewsActivity发送通知结束所有导航的Activity
                RxBus.get().post("finish", false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            finish();
            overridePendingTransition(0, R.anim.anim_slide_out);
        }
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 继承BaseView抽出显示信息通用行为
     *
     * @param msg 信息
     */
    @Override
    public void toast(String msg) {
        showSnackbar(msg);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    /**
     * 用户已选择tab的情况再次点击该tab，列表返回顶部，需要在setupWithViewPager后设置比如监听会把覆盖
     *
     * @param viewPager
     * @param tabLayout TabLayout
     */
    protected void setOnTabSelectEvent(final ViewPager viewPager, final TabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(new OnTabSelectedListenerAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                RxBus.get().post("enableRefreshLayoutOrScrollRecyclerView", tab.getPosition());
            }
        });
    }

}
