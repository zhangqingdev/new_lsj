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
import com.oushangfeng.lsj.module.photo.presenter.ILSJPhotoListPresenter;
import com.oushangfeng.lsj.module.photo.presenter.ILSJPhotoListPresenterImpl;
import com.oushangfeng.lsj.module.photo.view.ILSJPhotoListView;
import com.oushangfeng.lsj.utils.ClickUtils;
import com.oushangfeng.lsj.utils.GlideUtils;
import com.oushangfeng.lsj.utils.MeasureUtil;
import com.oushangfeng.lsj.widget.ThreePointLoadingView;
import com.oushangfeng.lsj.widget.refresh.RefreshLayout;
import com.socks.library.KLog;

import java.util.Random;

/**
 * ClassName: PhotoListFragment<p>
 * Fuction: 图片新闻列表界面<p>
 * UpdateDate: <p>
 */
@ActivityFragmentInject(contentViewId = R.layout.fragment_photo_list,
        handleRefreshLayout = true)
public class PhotoListFragment extends BaseFragment<ILSJPhotoListPresenter> implements ILSJPhotoListView {

    protected static final String PHOTO_ID = "photo_id";
    protected static final String POSITION = "position";

    protected String mPhotoId;

    private BaseRecyclerAdapter<IndexPhotoModel.PhotoModel> mAdapter;
    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;

    private ThreePointLoadingView mLoadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhotoId = getArguments().getString(PHOTO_ID);
            mPosition = getArguments().getInt(POSITION);
        }
    }

    public static PhotoListFragment newInstance(String newsId, int position) {
        PhotoListFragment fragment = new PhotoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PHOTO_ID, newsId);
        bundle.putInt(POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void initView(View fragmentRootView) {

        mLoadingView = (ThreePointLoadingView) fragmentRootView.findViewById(R.id.tpl_view);
        mLoadingView.setOnClickListener(this);

        mRecyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.recycler_view);

        mRefreshLayout = (RefreshLayout) fragmentRootView.findViewById(R.id.refresh_layout);

		mPresenter = new ILSJPhotoListPresenterImpl(this,"7777777",mPhotoId,1);
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
		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        mAdapter = new BaseRecyclerAdapter<IndexPhotoModel.PhotoModel>(getActivity(), data.list, layoutManager) {

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_photo_summary;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final IndexPhotoModel.PhotoModel item) {

                GlideUtils.loadDefault(item.img.get(0), holder.getImageView(R.id.iv_photo_summary), false, DecodeFormat.PREFER_ARGB_8888, DiskCacheStrategy.RESULT);
                //                Glide.with(getActivity()).load(item.kpic).asBitmap().animate(R.anim.image_load).placeholder(R.drawable.ic_loading).error(R.drawable.ic_fail).format(DecodeFormat.PREFER_ARGB_8888)
                //                        .diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.getImageView(R.id.iv_photo_summary));

//                holder.getTextView(R.id.tv_photo_summary).setText(item.title);
                holder.getTextView(R.id.tv_photo_summary).setVisibility(View.GONE);
				Random random = new Random();
				int height = 300 +random.nextInt(400);
				ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
				layoutParams.height = height;
				holder.itemView.setLayoutParams(layoutParams);
            }
        };

        mAdapter.setOnItemClickListener(new OnItemClickAdapter() {
            @Override
            public void onItemClick(View view, int position) {

                if (ClickUtils.isFastDoubleClick()) {
                    return;
                }

                KLog.e(mAdapter.getData().get(position).title + ";" + mAdapter.getData().get(position).id);

                view = view.findViewById(R.id.iv_photo_summary);
                Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                intent.putExtra("photoId", mAdapter.getData().get(position).id);
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

        mAdapter.setOnLoadMoreListener(10, new OnLoadMoreListener() {
            @Override
            public void loadMore() {
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
		if (mAdapter == null) {
			initNewsList(data);
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
				if (data == null || data.list.size() == 0) {
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
