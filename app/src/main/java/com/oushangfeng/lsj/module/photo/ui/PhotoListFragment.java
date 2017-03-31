package com.oushangfeng.lsj.module.photo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.base.BaseFragment;
import com.oushangfeng.lsj.base.BaseRecyclerAdapter;
import com.oushangfeng.lsj.base.BaseRecyclerViewHolder;
import com.oushangfeng.lsj.base.BaseSpacesItemDecoration;
import com.oushangfeng.lsj.bean.IndexPhotoModel;
import com.oushangfeng.lsj.callback.OnEmptyClickListener;
import com.oushangfeng.lsj.callback.OnItemClickAdapter;
import com.oushangfeng.lsj.callback.OnLoadMoreListener;
import com.oushangfeng.lsj.common.DataLoadType;
import com.oushangfeng.lsj.module.news.ui.NewsDetailActivity;
import com.oushangfeng.lsj.module.photo.presenter.ILSJPhotoListPresenter;
import com.oushangfeng.lsj.module.photo.presenter.ILSJPhotoListPresenterImpl;
import com.oushangfeng.lsj.module.photo.view.ILSJPhotoListView;
import com.oushangfeng.lsj.utils.ClickUtils;
import com.oushangfeng.lsj.utils.GlideUtils;
import com.oushangfeng.lsj.utils.MeasureUtil;
import com.oushangfeng.lsj.utils.Utils;
import com.oushangfeng.lsj.widget.ThreePointLoadingView;
import com.oushangfeng.lsj.widget.refresh.RefreshLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * ClassName: PhotoListFragment<p>
 * Fuction: 图片新闻列表界面<p>
 * UpdateDate: <p>
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_photo_list,
		handleRefreshLayout = true)
public class PhotoListFragment extends BaseFragment<ILSJPhotoListPresenter> implements ILSJPhotoListView {


	private BaseRecyclerAdapter<IndexPhotoModel.PhotoModel> mAdapter;
	private RecyclerView mRecyclerView;
	private RefreshLayout mRefreshLayout;

	private ThreePointLoadingView mLoadingView;

	private int itemWidth;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static PhotoListFragment newInstance() {
		PhotoListFragment fragment = new PhotoListFragment();
		return fragment;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if(isVisibleToUser){
			MobclickAgent.onEvent(getActivity().getApplicationContext(),"photo_list_show");
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	protected void initView(View fragmentRootView) {

		mLoadingView = (ThreePointLoadingView) fragmentRootView.findViewById(R.id.tpl_view);
		mLoadingView.setOnClickListener(this);

		mRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.recycler_view);

		mRefreshLayout = (RefreshLayout) fragmentRootView.findViewById(R.id.refresh_layout);

		mPresenter = new ILSJPhotoListPresenterImpl(this, Utils.getDevId(getActivity()), 0, 10);

		itemWidth = (MeasureUtil.getScreenSize(getActivity()).x - MeasureUtil.dip2px(getActivity(),12))/2;
	}

	@Override
	public void showProgress() {
		mLoadingView.play();
	}

	@Override
	public void hideProgress() {
		mLoadingView.stop();
	}


	private void initNewsList(IndexPhotoModel data) {

//        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

		mAdapter = new BaseRecyclerAdapter<IndexPhotoModel.PhotoModel>(getActivity(), data.list, layoutManager) {

			@Override
			public int getItemLayoutId(int viewType) {
				return R.layout.item_photo;
			}

			@Override
			public void bindData(BaseRecyclerViewHolder holder, final int position, final IndexPhotoModel.PhotoModel item) {
				ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
				List<IndexPhotoModel.ImgEntity> imgs = item.img;
				if (imgs != null && !imgs.isEmpty()) {
					//宽度340dp
					IndexPhotoModel.ImgEntity entity = imgs.get(0);
					layoutParams.height = entity.height * itemWidth / entity.width;
					holder.itemView.setLayoutParams(layoutParams);
//				Glide.with(getActivity()).load(item.img.get(0)).dontAnimate().thumbnail(0.2f).into(holder.getImageView(R.id.iv_photo_summary));
					ImageView imageView = holder.getImageView(R.id.iv_photo_summary);

					String tag = (String) imageView.getTag(R.string.app_name);
					if(!Utils.isEmpty(tag)&&tag.equals(item.img.get(0).url)){

					}else {
						GlideUtils.loadDefault(item.img.get(0).url, imageView, false, DecodeFormat.PREFER_ARGB_8888, DiskCacheStrategy.ALL);
						imageView.setTag(R.string.app_name,item.img.get(0).url);
					}
//				Glide.with(getActivity()).load(item.img.get(0)).into(holder.getImageView(R.id.iv_photo_summary));
					//                Glide.with(getActivity()).load(item.kpic).asBitmap().animate(R.anim.image_load).placeholder(R.drawable.ic_loading).error(R.drawable.ic_fail).format(DecodeFormat.PREFER_ARGB_8888)
					//                        .diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.getImageView(R.id.iv_photo_summary));
				} else {
					holder.getImageView(R.id.iv_photo_summary).setImageResource(R.drawable.ic_error_outline_black);

				}
//                holder.getTextView(R.id.tv_photo_summary).setText(item.title);
			}
		};

		mAdapter.setOnItemClickListener(new OnItemClickAdapter() {
			@Override
			public void onItemClick(View view, int position) {

				if (ClickUtils.isFastDoubleClick()) {
					return;
				}

				MobclickAgent.onEvent(getActivity(),"photo_item_click");


				view = view.findViewById(R.id.iv_photo_summary);
				IndexPhotoModel.PhotoModel item = mAdapter.getData().get(position);
//				Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);

//
//
//				ArrayList<PhotoModel> data = new ArrayList<>();
//				PhotoModel model = new PhotoModel();
//				model.img = item.img.get(0).url;
//				data.add(model);
//
//				intent.putExtra("data", data);
//				intent.putExtra("title", item.title);
				Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
				intent.putExtra("url",item.url);
				intent.putExtra("title","查看大图");
				//让新的Activity从一个小的范围扩大到全屏
				ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
				ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

			}
		});

		mAdapter.setOnEmptyClickListener(new OnEmptyClickListener() {
			@Override
			public void onEmptyClick() {
				showProgress();
				mPresenter.refreshData();
			}
		});

		mAdapter.setOnLoadMoreListener(mAdapter.getData().size(), new OnLoadMoreListener() {
			@Override
			public void loadMore() {
				MobclickAgent.onEvent(getActivity().getApplicationContext(),"photo_next_page");
				mPresenter.loadPhotoData();
				// mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
			}
		});

		mRecyclerView.setLayoutManager(layoutManager);
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
	public void getPhotoList(IndexPhotoModel data, @NonNull String errorMsg, @DataLoadType.DataLoadTypeChecker int type) {
		if (data == null) {
			data = new IndexPhotoModel();
		}
//		data = new IndexPhotoModel();
//		IndexPhotoModel.PhotoModel item = new IndexPhotoModel.PhotoModel();
//		IndexPhotoModel.ImgEntity img = new IndexPhotoModel.ImgEntity();
//		img.url = "http://img3.duitang.com/uploads/item/201608/16/20160816125631_eSW3L.jpeg";
//		img.height = 1130;
//		img.width = 750;
//		item.img = new ArrayList<>();
//		item.img.add(img);
//		data.list = new ArrayList<>();
//		data.list.add(item);
		if (mAdapter == null) {
			initNewsList(data);
		}
		if((data.list == null || data.list.isEmpty())&&type == DataLoadType.TYPE_REFRESH_SUCCESS){
			type = DataLoadType.TYPE_REFRESH_FAIL;
			errorMsg = "获取数据失败，请稍后再试";
		}

		switch (type) {
			case DataLoadType.TYPE_REFRESH_SUCCESS:
				mRefreshLayout.refreshFinish();
				mAdapter.enableLoadMore(true);
				mAdapter.setData(data.list);
				break;
			case DataLoadType.TYPE_REFRESH_FAIL:
				mRefreshLayout.refreshFinish();
				mAdapter.enableLoadMore(false);
				mAdapter.showEmptyView(true, errorMsg);
				mAdapter.notifyDataSetChanged();
				break;
			case DataLoadType.TYPE_LOAD_MORE_SUCCESS:
				mAdapter.loadMoreSuccess();
				if (data == null || data.list == null || data.list.size() == 0) {
					mAdapter.enableLoadMore(null);
					toast("全部加载完毕");
					return;
				}
				mAdapter.addMoreData(data.list);
				break;
			case DataLoadType.TYPE_LOAD_MORE_FAIL:
				mAdapter.loadMoreFailed(errorMsg);
				break;
		}
	}
}
