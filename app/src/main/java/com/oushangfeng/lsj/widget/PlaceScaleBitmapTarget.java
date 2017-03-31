package com.oushangfeng.lsj.widget;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * Created by Admin on 2017/3/31.
 */

public class PlaceScaleBitmapTarget extends BitmapImageViewTarget {

	ImageView.ScaleType placeScale;
	ImageView.ScaleType originScale;

	public PlaceScaleBitmapTarget(ImageView view, ImageView.ScaleType placeScale) {
		super(view);
		this.placeScale = placeScale;
		originScale = view.getScaleType();
	}

	@Override
	public void onLoadStarted(Drawable placeholder) {
		getView().setScaleType(placeScale);
		super.onLoadStarted(placeholder);
	}


	@Override
	public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
		getView().setScaleType(originScale);
		super.onResourceReady(resource, glideAnimation);
	}

}
