package com.mimp.android.common;

import net.tsz.afinal.FinalDb;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.easemob.chat.EMChatManager;
import com.juns.health.net.loopj.android.http.RequestParams;
import com.mimp.android.bean.User;
import com.mimp.android.ch0.App;
import com.mimp.android.ch0.Constants;
import com.mimp.android.ch0.GloableParams;
import com.mimp.android.net.BaseJsonRes;
import com.mimp.android.net.NetClient;

public class UserUtils {
	/**
	 * 获取用户信息
	 * 
	 * @param context
	 * @return
	 */
	public static User getUserModel(Context context) {
		User user = null;
		String jsondata = Utils.getValue(context, Constants.UserInfo);
		// Log.e("", jsondata);
		if (!TextUtils.isEmpty(jsondata))
			user = JSON.parseObject(jsondata, User.class);
		return user;
	}

	/**
	 * 获取用户ID
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserID(Context context) {
		User user = getUserModel(context);
		if (user != null)
			return user.getTelephone();
		else
			return "";
	}

	/**
	 * 获取用户名字
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserName(Context context) {
		User user = getUserModel(context);
		if (user != null)
			return user.getUserName();
		else
			return "";
	}

	/**
	 * 获取用户
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserPwd(Context context) {
		User user = getUserModel(context);
		if (user != null)
			return user.getPassword();
		else
			return "";
	}

	public static void getLogout(Context context) {
		EMChatManager.getInstance().logout();// �?��环信聊天
		Utils.RemoveValue(context, Constants.LoginState);
		Utils.RemoveValue(context, Constants.UserInfo);
		App.getInstance2().exit();
	}

	public static void initUserInfo(final Context context,
			final String telphone, final ImageView img_avar,
			final TextView txt_name) {
		NetClient netClient = new NetClient(context);
		RequestParams params = new RequestParams();
		params.put("telphone", telphone);
		netClient.post(Constants.getUserInfoURL, params, new BaseJsonRes() {

			@Override
			public void onMySuccess(String data) {
				User user = JSON.parseObject(data, User.class);
				if (user != null) {
					if (user.getUserName() != null) {
						txt_name.setText(user.getUserName());
					}
					if (user.getHeadUrl() != null) {
						NetClient.getIconBitmap(img_avar, user.getHeadUrl());
					}
					FinalDb db = FinalDb.create(context, Constants.DB_NAME,
							false);
					if (db.findById(user.getId(), User.class) != null)
						db.deleteById(User.class, user.getId());
					db.save(user);
					GloableParams.UserInfos.add(user);
					GloableParams.Users.put(user.getTelephone(), user);
				}
			}

			@Override
			public void onMyFailure() {

			}
		});
	}

}
