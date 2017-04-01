package com.oushangfeng.lsj.app;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Admin on 2017/4/1.
 */

public class CutsomGlideModule extends OkHttpGlideModule {

	@Override
	public void applyOptions(Context context, GlideBuilder builder) {
		super.applyOptions(context, builder);
	}

	@Override
	public void registerComponents(Context context, Glide glide) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.readTimeout(30, TimeUnit.SECONDS);
		builder.writeTimeout(30,TimeUnit.SECONDS);
		builder.connectTimeout(30,TimeUnit.SECONDS);
		glide.register(GlideUrl.class, InputStream.class,new OkHttpUrlLoader.Factory(builder.build()));
	}
}
