package com.oushangfeng.lsj.widget;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Created by Admin on 2017/3/31.
 */

public class PlaceScaleImageViewTarget extends GlideDrawableImageViewTarget {

	ImageView.ScaleType placeScale;
	ImageView.ScaleType originScale;

	public PlaceScaleImageViewTarget(ImageView view, ImageView.ScaleType placeScale) {
		super(view);
		this.placeScale = placeScale;
		originScale = view.getScaleType();
	}

	@Override
	public void onLoadStarted(Drawable placeholder) {
		getView().setScaleType(placeScale);
		super.onLoadStarted(placeholder);
	}


//	@Override
//	public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//		getView().setScaleType(originScale);
//		super.onResourceReady(resource, glideAnimation);
//	}

	@Override
	public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
		getView().setScaleType(originScale);
		super.onResourceReady(resource, animation);
	}
}
