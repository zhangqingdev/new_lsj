package com.oushangfeng.lsj.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wudi on 17/3/26.
 */

public class NoScrollViewPager extends ViewPager {




	public NoScrollViewPager(Context context) {
		super(context);
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_MOVE){
			requestDisallowInterceptTouchEvent(true);
		}else {
			requestDisallowInterceptTouchEvent(false);
		}
		return super.onTouchEvent(ev);
	}
}
