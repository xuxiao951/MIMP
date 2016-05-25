package com.mimp.android.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.juns.health.net.loopj.android.http.JsonHttpResponseHandler;
import com.mimp.android.ch0.App;
import com.mimp.android.ch0.Constants;
import com.mimp.android.common.Utils;

public abstract class BaseJsonRes extends JsonHttpResponseHandler {

	@Override
	public void onSuccess(JSONObject response) {
		try {
			String result = response.getString(Constants.Result);
			// System.out.println("返回的�?" + response);
			if (result == null) {
				Utils.showLongToast(App.getInstance(), Constants.NET_ERROR);
			} else if (result.equals("1")) {
				String str_data = response.getString(Constants.Value);
				onMySuccess(str_data);
			} else {
				String str = response.getString(Constants.Info);
				Utils.showLongToast(App.getInstance(), str);
				onMyFailure();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			onMyFailure();
		}
	}

	public abstract void onMySuccess(String data);

	public abstract void onMyFailure();
}
