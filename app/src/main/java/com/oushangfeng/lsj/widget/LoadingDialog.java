package com.oushangfeng.lsj.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oushangfeng.lsj.R;

public class LoadingDialog extends Dialog {

	private Activity mContext;
	private TextView tv;
	private ProgressBar progress;
	
	public LoadingDialog(Activity context) {
		super(context, R.style.loading_dialog);
		this.mContext = context;
		this.setCanceledOnTouchOutside(false);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.loading_dialog, null);
		tv = (TextView)view.findViewById(R.id.tipTextView);
		this.addContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void show(String msg) {
		if(mContext !=null &&!mContext.isFinishing()){
			tv.setText(msg);
			this.show();
		}
	}

}
