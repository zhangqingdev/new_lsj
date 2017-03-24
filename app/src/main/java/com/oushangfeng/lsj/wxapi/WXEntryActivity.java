package com.oushangfeng.lsj.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.oushangfeng.lsj.utils.MainConstants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, MainConstants.WX_APPID, true);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	@Override
	public void onResp(BaseResp resp) {
		int type = resp.getType();
		if (type == ConstantsAPI.COMMAND_SENDAUTH) {
			SendAuth.Resp r = (Resp) resp;
			JSONObject data = new JSONObject();
			try {
				data.put("errcode", r.errCode);
				data.put("state", r.state);
				data.put("code", r.code);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent(MainConstants.ACTION_WX_AUTH);
			intent.putExtra("data", data.toString());
			sendBroadcast(intent);
		}else if(type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
			Intent intent = new Intent(MainConstants.ACTION_WX_SHARE);
			intent.putExtra("errCode", resp.errCode);
			intent.putExtra("errStr",resp.errStr);
			sendBroadcast(intent);
		}
		this.finish();
	}
}
