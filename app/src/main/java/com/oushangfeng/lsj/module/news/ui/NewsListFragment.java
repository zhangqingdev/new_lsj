package com.oushangfeng.lsj.module.news.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.base.BaseFragment;
import com.oushangfeng.lsj.base.BaseSpacesItemDecoration;
import com.oushangfeng.lsj.bean.IndexNewsWapper;
import com.oushangfeng.lsj.bean.IndexPageBannerModel;
import com.oushangfeng.lsj.bean.IndexPageModel;
import com.oushangfeng.lsj.callback.OnBannerClickListener;
import com.oushangfeng.lsj.callback.OnEmptyClickListener;
import com.oushangfeng.lsj.callback.OnItemClickAdapter;
import com.oushangfeng.lsj.callback.OnLoadMoreListener;
import com.oushangfeng.lsj.callback.RequestCallback;
import com.oushangfeng.lsj.common.DataLoadType;
import com.oushangfeng.lsj.module.news.presenter.ILSJNewsPresenter;
import com.oushangfeng.lsj.module.news.presenter.ILSJNewsPresenterImpl;
import com.oushangfeng.lsj.module.news.ui.adapter.NewsListRecyclerAdapter;
import com.oushangfeng.lsj.module.news.view.ILSJNewsView;
import com.oushangfeng.lsj.utils.ClickUtils;
import com.oushangfeng.lsj.utils.MeasureUtil;
import com.oushangfeng.lsj.utils.Utils;
import com.oushangfeng.lsj.widget.ThreePointLoadingView;
import com.oushangfeng.lsj.widget.refresh.RefreshLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: NewsListFragment<p>
 * Fuction: 新闻列表界面<p>
 * UpdateUser: <p>
 * UpdateDate: <p>
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_news_list,
        handleRefreshLayout = true)
public class NewsListFragment extends BaseFragment<ILSJNewsPresenter> implements ILSJNewsView {


    private NewsListRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;


    private ThreePointLoadingView mLoadingView;

	private List<IndexPageBannerModel> banner ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static NewsListFragment newInstance() {
        NewsListFragment fragment = new NewsListFragment();
        return fragment;
    }

    @Override
    protected void initView(View fragmentRootView) {

        mLoadingView = (ThreePointLoadingView) fragmentRootView.findViewById(R.id.tpl_view);
        mLoadingView.setOnClickListener(this);

        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.recycler_view);

        mRefreshLayout = (RefreshLayout) fragmentRootView.findViewById(R.id.refresh_layout);

        mPresenter = new ILSJNewsPresenterImpl(this, Utils.getDevId(getActivity()),0);
		((ILSJNewsPresenterImpl)mPresenter).getIndexBannerList(new RequestCallback<List<IndexPageBannerModel>>() {
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
			public void requestSuccess(List<IndexPageBannerModel> data) {
				banner = data;
				List<IndexNewsWapper> lists = mAdapter.getData();
				if(lists != null && !lists.isEmpty()){
					IndexNewsWapper wapper = lists.get(0);
					if(wapper.type == 100){
						wapper.data = banner;
						mAdapter.notifyItemChanged(0);
					}
				}
			}
		},Utils.getDevId(getActivity()),"5");

    }

    @Override
    public void showProgress() {
        mLoadingView.play();
    }

    @Override
    public void hideProgress() {
        mLoadingView.stop();
    }




    private void initNewsList(final List<IndexNewsWapper> data) {
		mAdapter = new NewsListRecyclerAdapter(getActivity(),data);
        // mAdapter为空肯定为第一次进入状态
//        mAdapter = new BaseRecyclerAdapter<NeteastNewsSummary>(getActivity(), data) {
//            @Override
//            public int getItemLayoutId(int viewType) {
//                return R.layout.item_news_summary;
//            }
//
//            @Override
//            public void bindData(BaseRecyclerViewHolder holder, int position, NeteastNewsSummary item) {
//                GlideUtils.loadDefault(item.imgsrc, holder.getImageView(R.id.iv_news_summary_photo), null, null, DiskCacheStrategy.RESULT);
//                //                Glide.with(getActivity()).load(item.imgsrc).asBitmap().animate(R.anim.image_load).diskCacheStrategy(DiskCacheStrategy.RESULT)
//                //                        .placeholder(R.drawable.ic_loading).error(R.drawable.ic_fail).into(holder.getImageView(R.id.iv_news_summary_photo));
//                holder.getTextView(R.id.tv_news_summary_title).setText(item.title);
//                holder.getTextView(R.id.tv_news_summary_digest).setText(item.digest);
//                holder.getTextView(R.id.tv_news_summary_ptime).setText(item.ptime);
//            }
//        };
		mAdapter.setOnItemClickListener(new OnItemClickAdapter() {
			@Override
			public void onItemClick(View view, int position) {
				if(ClickUtils.isFastDoubleClick()){
					return ;
				}
				List<IndexNewsWapper> data = mAdapter.getData();
				IndexNewsWapper wapper = data.get(position);
				if(wapper.type == 100){
					return ;
				}else {
					MobclickAgent.onEvent(getActivity().getApplicationContext(),"news_click");
					IndexPageModel.IndexArticleContent content = (IndexPageModel.IndexArticleContent) wapper.data;
					startUrl(content.url);
				}

			}
		});

		mAdapter.setOnBannerClickListener(new OnBannerClickListener() {
			@Override
			public void onBannerClick(IndexPageBannerModel item) {
				if(item != null){
					MobclickAgent.onEvent(getActivity().getApplicationContext(),"banner_click");
					startUrl(item.url);
				}
			}
		});

//        mAdapter.setOnItemClickListener(new OnItemClickAdapter() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                if (ClickUtils.isFastDoubleClick()) {
//                    return;
//                }
//
//                // imgextra不为空的话，无新闻内容，直接打开图片浏览
//                KLog.e(mAdapter.getData().get(position).title + ";" + mAdapter.getData().get(position).postid);
//
//                view = view.findViewById(R.id.iv_news_summary_photo);
//
//                if (mAdapter.getData().get(position).postid == null) {
//                    toast("此新闻浏览不了哎╮(╯Д╰)╭");
//                    return;
//                }
//
//                // 跳转到新闻详情
//                if (!TextUtils.isEmpty(mAdapter.getData().get(position).digest)) {
//                    Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
//                    intent.putExtra("postid", mAdapter.getData().get(position).postid);
//                    intent.putExtra("imgsrc", mAdapter.getData().get(position).imgsrc);
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.iv_news_summary_photo), "photos");
//                        getActivity().startActivity(intent, options.toBundle());
//                    } else {
//                        //让新的Activity从一个小的范围扩大到全屏
//                        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, view.getWidth()/* / 2*/, view.getHeight()/* / 2*/, 0, 0);
//                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//                    }
//                } else {
//                    // 以下将数据封装成新浪需要的格式，用于点击跳转到图片浏览
//                    mSinaPhotoDetail = new SinaPhotoDetail();
//                    mSinaPhotoDetail.data = new SinaPhotoDetail.SinaPhotoDetailDataEntity();
//                    mSinaPhotoDetail.data.title = mAdapter.getData().get(position).title;
//                    mSinaPhotoDetail.data.content = "";
//                    mSinaPhotoDetail.data.pics = new ArrayList<>();
//                    // 天啊，什么格式都有 --__--
//                    if (mAdapter.getData().get(position).ads != null) {
//                        for (NeteastNewsSummary.AdsEntity entiity : mAdapter.getData().get(position).ads) {
//                            SinaPhotoDetail.SinaPhotoDetailPicsEntity sinaPicsEntity = new SinaPhotoDetail.SinaPhotoDetailPicsEntity();
//                            sinaPicsEntity.pic = entiity.imgsrc;
//                            sinaPicsEntity.alt = entiity.title;
//                            sinaPicsEntity.kpic = entiity.imgsrc;
//                            mSinaPhotoDetail.data.pics.add(sinaPicsEntity);
//                        }
//                    } else if (mAdapter.getData().get(position).imgextra != null) {
//                        for (NeteastNewsSummary.ImgextraEntity entiity : mAdapter.getData().get(position).imgextra) {
//                            SinaPhotoDetail.SinaPhotoDetailPicsEntity sinaPicsEntity = new SinaPhotoDetail.SinaPhotoDetailPicsEntity();
//                            sinaPicsEntity.pic = entiity.imgsrc;
//                            sinaPicsEntity.kpic = entiity.imgsrc;
//                            mSinaPhotoDetail.data.pics.add(sinaPicsEntity);
//                        }
//                    }
//
//                    Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
//                    intent.putExtra("neteast", mSinaPhotoDetail);
//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
//                    ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//
//                }
//            }
//        });

        mAdapter.setOnEmptyClickListener(new OnEmptyClickListener() {
            @Override
            public void onEmptyClick() {
                showProgress();
                mPresenter.refreshData();
            }
        });

        mAdapter.setOnLoadMoreListener(10, new OnLoadMoreListener() {
            @Override
            public void loadMore() {
				MobclickAgent.onEvent(getActivity().getApplicationContext(),"news_next_page");
                mPresenter.loadMoreData();
                // mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(getActivity(), 4)));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(250);
        mRecyclerView.getItemAnimator().setMoveDuration(250);
        mRecyclerView.getItemAnimator().setChangeDuration(250);
        mRecyclerView.getItemAnimator().setRemoveDuration(250);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mPresenter.refreshData();
            }
        });
    }

	@Override
	public void onDestroyView() {
		if(mAdapter != null){
			mAdapter.stopBannerScroll();
		}
		super.onDestroyView();
	}



	@Override
	public void getIndexNewsList(IndexPageModel data, @NonNull String errorMsg, @DataLoadType.DataLoadTypeChecker int type) {

		List<IndexNewsWapper> list = new ArrayList<>();

		if (mAdapter == null) {
			initNewsList(list);
		}

		if(data !=null && data.list != null){
			for(int i = 0;i<data.list.size();i++){
				IndexNewsWapper item = new IndexNewsWapper();
				IndexPageModel.IndexArticleContent articleContent = data.list.get(i);
				item.type = articleContent.showType == 1 ? 300 : 200;
				item.data = articleContent;
				list.add(item);
			}
		}

		mAdapter.showEmptyView(false, "");

		switch (type) {
			case DataLoadType.TYPE_REFRESH_SUCCESS:
				mRefreshLayout.refreshFinish();
				mAdapter.enableLoadMore(true);
				IndexNewsWapper bannerWapper = new IndexNewsWapper();
				bannerWapper.type = 100;
				bannerWapper.data = banner;
				list.add(0,bannerWapper);
				mAdapter.setData(list);
				break;
			case DataLoadType.TYPE_REFRESH_FAIL:
				mRefreshLayout.refreshFinish();
				mAdapter.enableLoadMore(false);
				mAdapter.showEmptyView(true, errorMsg);
				mAdapter.notifyDataSetChanged();
				break;
			case DataLoadType.TYPE_LOAD_MORE_SUCCESS:
				mAdapter.loadMoreSuccess();
				if (data == null || list == null || list.size() == 0) {
					mAdapter.enableLoadMore(null);
					toast("全部加载完毕");
					return;
				}
				mAdapter.addMoreData(list);
				break;
			case DataLoadType.TYPE_LOAD_MORE_FAIL:
				mAdapter.loadMoreFailed(errorMsg);
				break;
		}
	}
}
