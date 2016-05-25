package com.mimp.android.ch0;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mimp.android.ch0.R;
/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class IndexGridAdapter extends BaseAdapter {
	private Context mContext;

	public String[] img_text = { "温度监测", "环境控制", "天气预报", "手机订餐", "手机订车", "我要保修",
			"通知公告", "维修攻略", "系统设置", };
	public int[] imgs = { R.drawable.app_phonecharge, R.drawable.app_phonecharge,
			R.drawable.app_phonecharge, R.drawable.app_phonecharge,
			R.drawable.app_phonecharge, R.drawable.app_phonecharge,
			R.drawable.app_phonecharge, R.drawable.app_phonecharge, R.drawable.app_phonecharge };

	public IndexGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.index_grid_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
		iv.setBackgroundResource(imgs[position]);

		tv.setText(img_text[position]);
		return convertView;
	}

}
