package com.oushangfeng.lsj.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by Admin on 2017/3/31.
 */

public class PlaceHolderDrawable extends Drawable {

	private Context context;
	private int resId;
	private Bitmap bitmap;

	private Paint paint;

	public PlaceHolderDrawable(Context context,int resId){
		this.context = context;
		this.resId = resId;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		bitmap = BitmapFactory.decodeResource(context.getResources(),resId);
	}


	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Color.GRAY);
		int left = (canvas.getWidth()-bitmap.getWidth())/2;
		int top = (canvas.getHeight()-bitmap.getHeight())/2;
		canvas.drawBitmap(bitmap,left,top,paint);
	}

	@Override
	public void setAlpha(int i) {
		paint.setAlpha(i);
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		paint.setColorFilter(colorFilter);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
}
