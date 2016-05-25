package com.mimp.android.view.fragment;

import com.mimp.android.ch0.IndexGridAdapter;
import com.mimp.android.ch0.IndexGridView;
import com.mimp.android.ch0.R;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Index extends Fragment implements OnClickListener{
	private IndexGridView gridview;
	private IndexGridAdapter gridadapter;
	private FragmentActivity activity;
	private ImageButton ib_close;
	private TextView tv_welcome=null;
	//定义sp
	private SharedPreferences sp = null;	
	private String strUserName = "";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view= inflater.inflate(R.layout.fragment_index, container,false);
		initview(view);
		this.listener();
		return view;
	}
	private void listener() {
		// TODO Auto-generated method stub
		ib_close.setOnClickListener(this);
	}
	private void initview(View v) {
		// TODO Auto-generated method stub
		
		// 初始化SharedPreferences.用来保存应用程序的一些偏好设置
		sp = getActivity().getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);	
		
		strUserName = sp.getString("USER_NAME", "");
		
		ib_close = (ImageButton) v.findViewById(R.id.ib_close);
		tv_welcome = (TextView) v.findViewById(R.id.tv_welcome);
		
		
		gridview=(IndexGridView) v.findViewById(R.id.fragment_index_gridview);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();		
		
		gridadapter = new IndexGridAdapter(activity);
		gridview.setAdapter(gridadapter);
		
		tv_welcome.setText("欢迎您回来！"+strUserName);
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "功能待开发", 0).show();
				switch (position) {
				case 0:
					Toast.makeText(getActivity(), "功能待开发", 0).show();
//					Intent intent=new Intent(this,SimpleActivity.class);
//		            intent.putExtra...//SimpleActivity是共用的，接受参数来显示对应的item内容
//		            startActivity(intent);
					break;

				default:
					break;
				}
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_close:
//			Toast.makeText(getActivity(), "test", 0).show();
			getActivity().finish();
			break;
			
		default:
			break;
		}
	}
	

}