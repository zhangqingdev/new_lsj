package com.oushangfeng.lsj.module.photo.ui;

import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.oushangfeng.lsj.R;
import com.oushangfeng.lsj.annotation.ActivityFragmentInject;
import com.oushangfeng.lsj.base.BaseActivity;
import com.oushangfeng.lsj.bean.PhotoModel;
import com.oushangfeng.lsj.module.photo.presenter.IPhotoDetailPresenter;
import com.oushangfeng.lsj.module.photo.ui.adapter.OnPageChangeListenerAdapter;
import com.oushangfeng.lsj.module.photo.ui.adapter.PhotoAdapter;
import com.oushangfeng.lsj.module.photo.view.IPhotoDetailView;
import com.oushangfeng.lsj.utils.MeasureUtil;
import com.oushangfeng.lsj.utils.SpUtil;
import com.oushangfeng.lsj.utils.StringUtils;
import com.oushangfeng.lsj.utils.Utils;
import com.oushangfeng.lsj.utils.ViewUtil;
import com.oushangfeng.lsj.widget.HackyViewPager;
import com.oushangfeng.lsj.widget.ThreePointLoadingView;
import com.socks.library.KLog;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;
import zhou.widget.RichText;

/**
 * ClassName: PhotoDetailActivity<p>
 * Fuction: 图片新闻详情界面<p>
 * UpdateDate: <p>
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_photo_detail,
        enableSlidr = true,
        menuId = R.menu.menu_settings,
        toolbarTitle = R.string.photo_detail)
public class PhotoDetailActivity extends BaseActivity<IPhotoDetailPresenter> implements IPhotoDetailView {

    private ThreePointLoadingView mLoadingView;
    // 捕获安卓系统报的一个bug
    private HackyViewPager mViewPager;
    private TextView mTitleTv;
    private TextView mPageTv;
    private RichText mContentTv;
    private int mTitleTvPaddingTop;
    private int mContentTvPaddingBottom;
    private int mOffset;
    private ValueAnimator mAnimator;
    private int mContentTvWidth;
    private int mPageTvWidth;
	private ImageView ivSave;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.removeAllUpdateListeners();
            mAnimator.cancel();
        }
    }

    @Override
    protected void initView() {

        getWindow().setBackgroundDrawable(null);

        mLoadingView = (ThreePointLoadingView) findViewById(R.id.tpl_view);
        mLoadingView.setOnClickListener(this);

        mViewPager = (HackyViewPager) findViewById(R.id.viewpager);

        mTitleTv = (TextView) findViewById(R.id.tv_photo_detail_title);

        mPageTv = (TextView) findViewById(R.id.tv_photo_detail_page);

        mContentTv = (RichText) findViewById(R.id.tv_photo_detail_content);
		ivSave = (ImageView) findViewById(R.id.iv_save);


		int index = getIntent().getIntExtra("index",0);
		ArrayList<PhotoModel> data = (ArrayList<PhotoModel>) getIntent().getSerializableExtra("data");
		String title = getIntent().getStringExtra("title");
		initViewPager(data,index,title);

    }

    @Override
    public void initViewPager(final ArrayList<PhotoModel> images, final int index, String title) {

        mOffset = MeasureUtil.getScreenSize(this).y / 4;

		if(!TextUtils.isEmpty(title)){
			mTitleTv.setText(title);
		}
		mTitleTv.setTag(true);

        final PhotoAdapter photoAdapter = new PhotoAdapter(this, images);
		photoAdapter.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				updateToolBar();
			}
		});
        mViewPager.setAdapter(photoAdapter);
		ivSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				loadingDialog.show();
				int current = mViewPager.getCurrentItem();
				PhotoModel model = images.get(current);
				Glide.with(PhotoDetailActivity.this).load(model.img).asBitmap().into(new SimpleTarget<Bitmap>() {
					@Override
					public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
						loadingDialog.dismiss();
						boolean result = Utils.saveImageToGallery(PhotoDetailActivity.this,resource);
						toast(result?"保存成功":"保存失败");
					}
				});

			}
		});


        final OnPageChangeListenerAdapter mPageChangeListenerAdapter = new OnPageChangeListenerAdapter() {

            @Override
            public void onPageSelected(int position) {

                if (mSlideBackLayout!= null && !SpUtil.readBoolean("enableSlideEdge")) {
                    // 设置了侧滑返回，并且是整页侧滑的时候，第一页是整页侧滑，其他页边缘侧滑
                    if (position == 0) {
                        mSlideBackLayout.edgeOnly(false);
                    } else {
                        mSlideBackLayout.edgeOnly(true);
                    }
                }

                if (images.size() > 0) {

                    final String s = getString(R.string.photo_page, position + 1, images.size());

                    mPageTv.setText(s);

//                    final String alt = photoList.data.pics.get(position).alt;
//                    if (!TextUtils.isEmpty(alt) && !mContentTv.getText().toString().contains(alt)) {
//                        ObjectAnimator.ofFloat(mContentTv, "alpha", 0.5f, 1).setDuration(500).start();
//                        mContentTv.setRichText(getString(R.string.photo_detail_content, alt));
//                        dynamicSetTextViewGravity();
//                    }
//                    // 每次切换回来都要处理一下，因为切换回来当前的图片不会调用OnPhotoExpandListener的onExpand方法
                    controlView(photoAdapter.getPics().get(position).showTitle);
                } else {
                    mPageTv.setText(getString(R.string.photo_page, 0, 0));
                }

            }

        };



        mViewPager.addOnPageChangeListener(mPageChangeListenerAdapter);

		mViewPager.setCurrentItem(index);

        mContentTv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPageTvWidth = mPageTv.getMeasuredWidth();
                mContentTvWidth = mContentTv.getMeasuredWidth();
                KLog.e("长度：" + mPageTvWidth + ";" + mContentTvWidth);
                mPageChangeListenerAdapter.onPageSelected(index);
                mContentTv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        photoAdapter.setOnPhotoExpandListener(new PhotoAdapter.OnPhotoExpandListener() {
            @Override
            public void onExpand(boolean show, int position) {
                // KLog.e("回调的时候: " + show);
                if (mViewPager.getCurrentItem() == position) {
                    // 当前页码才处理
                    controlView(show);
                }
            }
        });

    }

	private void updateToolBar(){
		try{
			View statsView = findViewById(R.id.status_view);
			if(getSupportActionBar().isShowing()){
				getSupportActionBar().hide();
				ViewUtil.setFullScreen(PhotoDetailActivity.this);
				statsView.setBackgroundColor(Color.BLACK);
			}else {
				getSupportActionBar().show();
				ViewUtil.quitFullScreen(PhotoDetailActivity.this);
				TypedArray array = getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
				statsView.setBackgroundColor(array.getColor(0,0x000000));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

    /**
     * 根据文本的长度动态设置对齐方式
     */
    private void dynamicSetTextViewGravity() {
        if ((mContentTv.getPaint().measureText(mContentTv.getText().toString()) < mContentTvWidth)) {
            mContentTv.setGravity(Gravity.CENTER);
            // 设为中心对齐，去掉前面两个空格
            mContentTv.setRichText(StringUtils.replaceBlank(mContentTv.getText().toString()));
            KLog.e("设为中心对齐，去掉前面两个空格");
        } else {
            mContentTv.setGravity(Gravity.TOP | Gravity.START);
            KLog.e("设为原始对齐");
        }
    }

    /**
     * 图片被拉大时控制页面其他元素隐藏
     *
     * @param show
     */
    private void controlView(boolean show) {
        if (mAnimator != null && mAnimator.isRunning()) {
            return;
        }
        mAnimator = new ValueAnimator();
        if (!show && (boolean) mTitleTv.getTag()) {
            // 隐藏
            mAnimator.setIntValues(0, mOffset);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    mTitleTv.setTag(false);
                    mTitleTv.setPadding(mTitleTv.getPaddingLeft(), -(int) animation.getAnimatedValue(), mTitleTv.getPaddingRight(), mTitleTv.getPaddingBottom());

                    mContentTv.setPadding(mContentTv.getPaddingLeft(), mContentTv.getPaddingTop(), mContentTv.getPaddingRight(), -(int) animation.getAnimatedValue());

                    ViewCompat.setScaleX(mPageTv, 1 - animation.getAnimatedFraction());
                    ViewCompat.setScaleY(mPageTv, 1 - animation.getAnimatedFraction());

                }
            });
        } else if (show && !(boolean) mTitleTv.getTag()) {
            // 显示
            mAnimator = new ValueAnimator();
            mAnimator.setIntValues(mOffset, 0);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    mTitleTv.setTag(true);
                    mTitleTv.setPadding(mTitleTv.getPaddingLeft(), -(int) animation.getAnimatedValue() + mTitleTvPaddingTop, mTitleTv.getPaddingRight(),
                            mTitleTv.getPaddingBottom());

                    mContentTv.setPadding(mContentTv.getPaddingLeft(), mContentTv.getPaddingTop(), mContentTv.getPaddingRight(),
                            -(int) animation.getAnimatedValue() + mContentTvPaddingBottom);

                    ViewCompat.setScaleX(mPageTv, animation.getAnimatedFraction());
                    ViewCompat.setScaleY(mPageTv, animation.getAnimatedFraction());
                }
            });
        } else {
            return;
        }
        mAnimator.setDuration(300);
        mAnimator.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mTitleTvPaddingTop == 0) {
            mTitleTvPaddingTop = mTitleTv.getPaddingTop();
            mContentTvPaddingBottom = mContentTv.getPaddingBottom();
        }
    }

    @Override
    public void showProgress() {
        mLoadingView.play();
    }

    @Override
    public void hideProgress() {
        mLoadingView.stop();
    }

}
