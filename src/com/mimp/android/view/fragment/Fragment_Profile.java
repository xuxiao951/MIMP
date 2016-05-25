package com.mimp.android.view.fragment;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mimp.android.ch0.Constants;
import com.mimp.android.ch0.GloableParams;
import com.mimp.android.ch0.R;
import com.mimp.android.common.UserUtils;
import com.mimp.android.common.Utils;

//��
public class Fragment_Profile extends Fragment implements OnClickListener {
	private Activity ctx;
	private View layout;
	private TextView tvname, tv_accout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (layout == null) {
			ctx = this.getActivity();
			layout = ctx.getLayoutInflater().inflate(R.layout.fragment_profile,
					null);
			initViews();
			initData();
			setOnListener();
		} else {
			ViewGroup parent = (ViewGroup) layout.getParent();
			if (parent != null) {
				parent.removeView(layout);
			}
		}
		return layout;
	}

	private void initViews() {
		tvname = (TextView) layout.findViewById(R.id.tvname);
		tv_accout = (TextView) layout.findViewById(R.id.tvmsg);
		String id = Utils.getValue(getActivity(), Constants.User_ID);
		tv_accout.setText(getString(R.string.wechat_id) + "��" + id);
		if (GloableParams.UserInfos != null) {
			String name = UserUtils.getUserName(ctx);
			if (name != null && !TextUtils.isEmpty(name))
				tvname.setText(name);
		}
	}

	private void setOnListener() {
		layout.findViewById(R.id.view_user).setOnClickListener(this);
		layout.findViewById(R.id.txt_album).setOnClickListener(this);
		layout.findViewById(R.id.txt_collect).setOnClickListener(this);
		layout.findViewById(R.id.txt_money).setOnClickListener(this);
		layout.findViewById(R.id.txt_card).setOnClickListener(this);
		layout.findViewById(R.id.txt_smail).setOnClickListener(this);
		layout.findViewById(R.id.txt_setting).setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.view_user:
//			Utils.start_Activity(getActivity(), MyCodeActivity.class);
			break;
		case R.id.txt_album:// ���
//			Utils.start_Activity(getActivity(), PublicActivity.class,
//					new BasicNameValuePair(Constants.NAME,
//							getString(R.string.my_posts)));
			break;
		case R.id.txt_collect:// �ղ�
//			Utils.start_Activity(getActivity(), PublicActivity.class,
//					new BasicNameValuePair(Constants.NAME,
//							getString(R.string.collection)));
			break;
		case R.id.txt_money:// Ǯ��
//			Utils.start_Activity(getActivity(), PublicActivity.class,
//					new BasicNameValuePair(Constants.NAME,
//							getString(R.string.wallet)));
			break;
		case R.id.txt_card:// ���
//			Utils.start_Activity(getActivity(), PublicActivity.class,
//					new BasicNameValuePair(Constants.NAME,
//							getString(R.string.card_bag)));
			break;
		case R.id.txt_smail:// ����
//			Utils.start_Activity(getActivity(), PublicActivity.class,
//					new BasicNameValuePair(Constants.NAME,
//							getString(R.string.expression)));
			break;
		case R.id.txt_setting:// ����
//			Utils.start_Activity(getActivity(), SettingActivity.class);
			break;
		default:
			break;
		}
	}

}