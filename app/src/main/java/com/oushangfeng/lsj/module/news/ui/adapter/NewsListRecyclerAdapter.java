package com.oushangfeng.lsj.module.news.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.base.BaseRecyclerViewHolder;
import com.oushangfeng.lsj.bean.IndexNewsWapper;
import com.oushangfeng.lsj.bean.IndexPageBannerModel;
import com.oushangfeng.lsj.bean.IndexPageModel;
import com.oushangfeng.lsj.callback.OnBannerClickListener;
import com.oushangfeng.lsj.callback.OnEmptyClickListener;
import com.oushangfeng.lsj.callback.OnItemClickListener;
import com.oushangfeng.lsj.callback.OnLoadMoreListener;
import com.oushangfeng.lsj.utils.GlideUtils;
import com.oushangfeng.lsj.widget.NoScrollViewPager;
import com.oushangfeng.lsj.widget.ViewPagerIndicator;
import com.oushangfeng.lsj.widget.ViewPagerScroller;

import java.util.ArrayList;
import java.util.List;


public  class NewsListRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_MORE = 3;
    public static final int TYPE_EMPTY = 4;
    private static final int TYPE_MORE_FAIL = 5;
	public static final int TYPE_BANNER = 100;
	public static final int TYPE_NEWS = 200;
	public static final int TYPE_MULT = 300;

    protected List<IndexNewsWapper> mData;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected OnItemClickListener mClickListener;
    protected boolean mShowLoadMoreView;
    protected boolean mShowEmptyView;

    private RecyclerView.LayoutManager mLayoutManager;

    private int mLastPosition = -1;
    private String mExtraMsg;
    private OnEmptyClickListener mEmptyClickListener;
	private OnBannerClickListener mOnBannerClickListener;
    private int mMoreItemCount;

    private OnLoadMoreListener mOnLoadMoreListener;

    private Boolean mEnableLoadMore;
	NoScrollViewPager viewPager;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					viewPager.setCurrentItem(
							viewPager.getCurrentItem() + 1, true);
					handler.sendEmptyMessageDelayed(1,4000);
					break;
			}
			super.handleMessage(msg);
		}
	};


    public NewsListRecyclerAdapter(Context context, List<IndexNewsWapper> data) {
        this(context, data, null);
    }

    public NewsListRecyclerAdapter(Context context, List<IndexNewsWapper> data, RecyclerView.LayoutManager layoutManager) {
        mContext = context;
        mLayoutManager = layoutManager;
        mData = data == null ? new ArrayList<IndexNewsWapper>() : data;
        mInflater = LayoutInflater.from(context);
    }

	public void startBannerScroll(){
		if(viewPager!=null){
			List<View> views = ((GuidePageAdapter)viewPager.getAdapter()).getViews();
			if(views != null && views.size() > 2 && !handler.hasMessages(1)){
				handler.sendEmptyMessageDelayed(1,4000);
			}
		}
	}

	public void stopBannerScroll(){
		handler.removeMessages(1);
	}


    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_MORE) {
            return new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.item_load_more, parent, false));
        } else if (viewType == TYPE_MORE_FAIL) {
            final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.item_load_more_failed, parent, false));
            if (mOnLoadMoreListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEnableLoadMore = true;
                        mShowLoadMoreView = true;
                        notifyItemChanged(getItemCount() - 1);
                        holder.itemView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mOnLoadMoreListener.loadMore();
                            }
                        }, 300);
                    }
                });
            }
            return holder;
        } else if (viewType == TYPE_EMPTY) {
            final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.item_empty_view, parent, false));
            if (mEmptyClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEmptyClickListener.onEmptyClick();
                    }
                });
            }
            return holder;
        } else if(viewType == TYPE_BANNER){
			final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.news_item_banner, parent, false));
			viewPager = (NoScrollViewPager) holder.itemView.findViewById(R.id.viewpager);
			ViewPagerIndicator indicator = (ViewPagerIndicator)holder.itemView.findViewById(R.id.indicator);
			ViewPagerScroller viewPagerScroller = new ViewPagerScroller(mContext);
			viewPagerScroller.initViewPagerScroll(viewPager);
			indicator.setViewPager(viewPager);
			GuidePageAdapter adapter = new GuidePageAdapter(new ArrayList<View>());
			viewPager.setAdapter(adapter);
			viewPager.setOffscreenPageLimit(2);
			return holder;
		} else if(viewType == TYPE_MULT){
			final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.news_item_mult, parent, false));
			if (mClickListener != null) {
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (holder.getLayoutPosition() != RecyclerView.NO_POSITION) {
							try {
								mClickListener.onItemClick(v, holder.getLayoutPosition());
							} catch (IndexOutOfBoundsException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
			return holder;
		} else {
			final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.item_news_summary, parent, false));
			if (mClickListener != null) {
				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (holder.getLayoutPosition() != RecyclerView.NO_POSITION) {
							try {
								mClickListener.onItemClick(v, holder.getLayoutPosition());
							} catch (IndexOutOfBoundsException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
			return holder;
		}

    }

	public void setOnBannerClickListener(OnBannerClickListener listener){
		this.mOnBannerClickListener = listener;
	}

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MORE) {
            fullSpan(holder, TYPE_MORE);
        } else if (getItemViewType(position) == TYPE_MORE_FAIL) {
            fullSpan(holder, TYPE_MORE_FAIL);
            holder.setText(R.id.tv_failed, mExtraMsg + "请点击重试！");
        } else if (getItemViewType(position) == TYPE_EMPTY) {
            fullSpan(holder, TYPE_EMPTY);
            holder.setText(R.id.tv_error, mExtraMsg);
        } else if(getItemViewType(position) == TYPE_BANNER){
			GuidePageAdapter adapter = (GuidePageAdapter) viewPager.getAdapter();
			List<View> views = adapter.getViews();
			views.clear();
			IndexNewsWapper wapper = mData.get(position);
			List<IndexPageBannerModel> bannerModel = (List<IndexPageBannerModel>) wapper.data;
			ViewPagerIndicator indicator = (ViewPagerIndicator)holder.itemView.findViewById(R.id.indicator);
			if(bannerModel != null && !bannerModel.isEmpty()){
				indicator.refreshIndicator(bannerModel.size());
				LayoutInflater inflater = LayoutInflater.from(mContext);
				for(int i = 0;i<bannerModel.size();i++){
					View view = inflater.inflate(R.layout.banner_item,viewPager,false);
					final IndexPageBannerModel item = bannerModel.get(i);
					ImageView imageView = (ImageView) view.findViewById(R.id.iv_banner);
					imageView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(mOnBannerClickListener != null){
								mOnBannerClickListener.onBannerClick(item);
							}
						}
					});
					GlideUtils.loadDefault(item.img.get(0).url,imageView, null, null, DiskCacheStrategy.RESULT);
					TextView tvBanner = (TextView) view.findViewById(R.id.tv_banner);
					tvBanner.setText(item.title);
					views.add(view);

				}
				adapter.notifyDataSetChanged();
				startBannerScroll();
			}
        }else if(getItemViewType(position) == TYPE_MULT){
			IndexNewsWapper wapper = mData.get(position);
			IndexPageModel.IndexArticleContent itemContent = (IndexPageModel.IndexArticleContent) wapper.data;
			holder.getTextView(R.id.tv_title).setText(itemContent.title);
			List<IndexPageModel.ImgEntity> imgs = itemContent.img;
			ImageView[] imageViews = new ImageView[]{holder.getImageView(R.id.iv1),holder.getImageView(R.id.iv2),holder.getImageView(R.id.iv3)};
			for(int i = 0;i<imageViews.length;i++){
				if(i<imgs.size()){
					GlideUtils.loadDefault(imgs.get(i).url,imageViews[i], null, null, DiskCacheStrategy.RESULT);
				}
			}
			holder.getTextView(R.id.tv_zan).setText(itemContent.laud+"");
		}else {

			IndexNewsWapper item = mData.get(position);
			IndexPageModel.IndexArticleContent itemContent = (IndexPageModel.IndexArticleContent) item.data;

			GlideUtils.loadDefault(itemContent.img.get(0).url,holder.getImageView(R.id.iv_news_summary_photo), null, null, DiskCacheStrategy.RESULT);
			//                Glide.with(getActivity()).load(item.imgsrc).asBitmap().animate(R.anim.image_load).diskCacheStrategy(DiskCacheStrategy.RESULT)
			//                        .placeholder(R.drawable.ic_loading).error(R.drawable.ic_fail).into(holder.getImageView(R.id.iv_news_summary_photo));
			holder.getTextView(R.id.tv_news_summary_title).setText(itemContent.title);
			holder.getTextView(R.id.tv_news_summary_digest).setText(itemContent.desc);
			holder.getTextView(R.id.tv_zan).setText(itemContent.laud+"");
		}

        if (!mShowEmptyView && mOnLoadMoreListener != null && (mEnableLoadMore != null && mEnableLoadMore) && !mShowLoadMoreView && position == getItemCount() - 1 && getItemCount() >= mMoreItemCount) {
            mShowLoadMoreView = true;
            holder.itemView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnLoadMoreListener.loadMore();
                    notifyItemInserted(getItemCount());
                }
            }, 300);
        }

    }

    private void fullSpan(BaseRecyclerViewHolder holder, final int type) {
        if (mLayoutManager != null) {
            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                if (((StaggeredGridLayoutManager) mLayoutManager).getSpanCount() != 1) {
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                    params.setFullSpan(true);
                }
            } else if (mLayoutManager instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
                final GridLayoutManager.SpanSizeLookup oldSizeLookup = gridLayoutManager.getSpanSizeLookup();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (getItemViewType(position) == type) {
                            return gridLayoutManager.getSpanCount();
                        }
                        if (oldSizeLookup != null) {
                            return oldSizeLookup.getSpanSize(position);
                        }
                        return 1;
                    }
                });
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(BaseRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public void add(int pos, IndexNewsWapper item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

    public void delete(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addMoreData(List<IndexNewsWapper> data) {
        int startPos = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(startPos, data.size());
    }

    public List<IndexNewsWapper> getData() {
        return mData;
    }

    public void setData(List<IndexNewsWapper> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if (mShowEmptyView) {
            return TYPE_EMPTY;
        }

        if (mOnLoadMoreListener != null && (mEnableLoadMore != null && mEnableLoadMore) && mShowLoadMoreView && getItemCount() - 1 == position) {
            return TYPE_MORE;
        }

        if (mOnLoadMoreListener != null && !mShowLoadMoreView && (mEnableLoadMore != null && !mEnableLoadMore) && getItemCount() - 1 == position) {
            return TYPE_MORE_FAIL;
        }

        return bindViewType(position);
    }

    @Override
    public int getItemCount() {
        int i = mOnLoadMoreListener == null || mEnableLoadMore == null ? 0 : (mEnableLoadMore && mShowLoadMoreView) || (!mShowLoadMoreView && !mEnableLoadMore) ? 1 : 0;
        return mShowEmptyView ? 1 : mData != null ? mData.size() + i : 0;
    }



    protected int bindViewType(int position) {
        return mData.get(position).type;
    }

    public void showEmptyView(boolean showEmptyView, @NonNull String msg) {
        mShowEmptyView = showEmptyView;
        mExtraMsg = msg;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnEmptyClickListener(OnEmptyClickListener listener) {
        mEmptyClickListener = listener;
    }

    public void setOnLoadMoreListener(int moreItemCount, @NonNull OnLoadMoreListener onLoadMoreListener) {
        mMoreItemCount = moreItemCount;
        mOnLoadMoreListener = onLoadMoreListener;
        mEnableLoadMore = true;
    }

    public void loadMoreSuccess() {
        mEnableLoadMore = true;
        mShowLoadMoreView = false;
        notifyItemRemoved(getItemCount());
    }

    public void loadMoreFailed(String errorMsg) {
        mEnableLoadMore = false;
        mShowLoadMoreView = false;
        mExtraMsg = errorMsg;
        notifyItemChanged(getItemCount() - 1);
    }

    /**
     * 设置是否开启底部加载
     *
     * @param enableLoadMore true为开启；false为关闭；null为已经全部加载完毕的关闭
     */
    public void enableLoadMore(Boolean enableLoadMore) {
        mEnableLoadMore = enableLoadMore;
    }


	private class GuidePageAdapter extends PagerAdapter {

		private List<View> views;

		public List<View> getViews(){
			return views;
		}

		public GuidePageAdapter(List<View> views){
			this.views = views;
		}



		@Override
		public int getCount() {
			if (views.size() > 2) {
				return Integer.MAX_VALUE;
			} else {
				return views.size();
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {

		}

		/**
		 * viewpager缓存3个元素
		 * */
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			int position = arg1 % views.size();
			View view = views.get(position);
			ViewParent vp = view.getParent();
			if (vp != null) {
				ViewGroup vg = (ViewGroup) vp;
				vg.removeView(view);
			}
			((ViewPager) arg0).addView(view);
			return view;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}


	}


}
