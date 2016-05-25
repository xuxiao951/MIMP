package com.mimp.android.ch0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

@SuppressLint("WorldReadableFiles")
public class MainTab02Fragment extends Fragment implements OnClickListener{
private final static int TSB_GREQUEST_CODE = 1;
//定义一个线程对象
private Handler handle;
// http链接
private FinalHttp http = null;
private AjaxParams params = null;
//定义sp
private SharedPreferences sp = null;

private String loginID = "";//获得登录ID
private String strURL = "";//获取服务器地址

private Intent intent=null;

//定一个一个fragmentactivity
private FragmentActivity activity;
private ReflashListView lvMyTSB;
private TsBillListAdapter tsBillListAdapter ;
//listview中的数据
private ArrayList<Map<String,Object>> listData=new ArrayList<Map<String, Object>>();
	
   
private ProgressDialog pdTab02;//正在加载进度对话框
   
private static final int LOAD_DATA_FINISH = 10;//涓婃媺鍒锋柊
private static final int REFRESH_DATA_FINISH = 11;//涓嬫媺鍒锋柊
private static final String TAG = "FindGroupFragment";
private int mCount = 10;

//定一个一个radiogroup
private RadioGroup rgroupb_mission=null;
private RadioButton rb_todo_mission=null;
private RadioButton rb_done_mission=null;
private RadioButton rb_all_mission=null;
//存储选中的radio。默认选中第一个
private String RadioSelected = "1";

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	View view =  inflater.inflate(R.layout.activity_main_tab02, container,false);
	//初始化
	this.initview(view);
	//2监听
	this.listenter();

	return view;
}
@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshdata("");
	}
@Override
public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
super.onActivityCreated(savedInstanceState);

activity = getActivity();

//初始化listview
lvMyTSB = (ReflashListView) this.getView().findViewById(R.id.lvMyTSB);		

tsBillListAdapter=new TsBillListAdapter(activity, listData);
lvMyTSB.setAdapter(tsBillListAdapter);

lvMyTSB.setCanLoadMore(false);
lvMyTSB.setAutoLoadMore(false);

pdTab02 = new ProgressDialog(activity);  
pdTab02.setTitle("");
pdTab02.setMessage("数据加载中");
pdTab02.setProgressStyle(ProgressDialog.STYLE_SPINNER);


//getMyBillbylogonid(loginID);
//监听下拉刷新事件
lvMyTSB.setOnRefreshListener(new ReflashListView.OnRefreshListener() {
     @Override
     public void onRefresh() {
         // TODO 刷新
         Log.e(TAG, "onRefresh");
         loadData(0);
     }

 });
//监听加载更多事件
lvMyTSB.setOnLoadListener(new ReflashListView.OnLoadMoreListener() {

     @Override
     public void onLoadMore() {
         // TODO 加载更多
         Log.e(TAG, "onLoad");
         loadData(1);
     }
 });
lvMyTSB.setOnItemClickListener(new OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position,long id) {
	View v=adapterView.getChildAt(position);
	
	//v.setBackgroundColor(Color.RED);
	//Toast.makeText(getActivity(),"当前行ID="+Integer.toString(position),Toast.LENGTH_LONG).show();
	//Toast.makeText(getActivity(),"当前行ID="+datalist.get(position).get("Id"),Toast.LENGTH_LONG).show();
	intent = new Intent();
	intent.putExtra("BillId",listData.get(position-1).get("BillID").toString());
    intent.setClass(getActivity(),TroubleShootingBillActivity.class );
	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	startActivityForResult(intent, TSB_GREQUEST_CODE);
	
	//Toast.makeText(getActivity(),"您选择了当前行"+datalist.get(position).get("Id"),Toast.LENGTH_LONG).show();
    }
});
rgroupb_mission.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//Toast.makeText(getActivity(),"100",Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		if (checkedId==R.id.rb_todo_mission){
//					Toast.makeText(getActivity(),"1",Toast.LENGTH_LONG).show();
			RadioSelected = "1";
			refreshdata("待办");
		}
		if (checkedId==R.id.rb_done_mission){
//					Toast.makeText(getActivity(),"2",Toast.LENGTH_LONG).show();
			RadioSelected = "2";
			refreshdata("已办");
		}
		if (checkedId==R.id.rb_all_mission){
//					Toast.makeText(getActivity(),"3",Toast.LENGTH_LONG).show();
			RadioSelected = "3";
			refreshdata("全部");
		}
	}
});	
		
	}
private void listenter() {
	// TODO Auto-generated method stub
	
}
private void initview(View view) {
	// TODO Auto-generated method stub


	//afinal进行ajax的清秀
	// 初始化ajax对象
	http = new FinalHttp();
	params = new AjaxParams();	
	// 初始化SharedPreferences.用来保存应用程序的一些偏好设置
	sp = getActivity().getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);	
	
//    GetMyBill(view);
	

	
	rgroupb_mission = (RadioGroup) view.findViewById(R.id.rgroupb_mission);
	rb_todo_mission=(RadioButton) view.findViewById(R.id.rb_todo_mission);
	rb_done_mission=(RadioButton) view.findViewById(R.id.rb_done_mission);
	rb_all_mission=(RadioButton) view.findViewById(R.id.rb_all_mission);
	
	loginID = sp.getString("USER_LoginID", "");
	strURL = sp.getString("Server_URL", "");
	
}
//是刷新列表及图标提醒
private void refreshdata(String flowstatus) {
	// TODO Auto-generated method stub
	loadData(0);
	//重新改写按钮的提示数字
	//ButtonBadgeView();
	MainActivity parentactivity = (MainActivity) getActivity();
	parentactivity.ButtonBadgeView();
}


@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	
}


/**
 * 加载数据
 * @param type
 */
public void loadData(final int type){

	switch (type) {
	case 0://刷新数据
		getMyBillbylogonid(loginID);
		break;
	case 1://加载数据
		getMyBillbylogonid(loginID);
		break;
	default:
		break;
	}

	switch (type) {
	case 0://刷新数据
		 if(new TsBillListAdapter(activity, listData)!=null){
        	 new TsBillListAdapter(activity, listData).notifyDataSetChanged();
         }
		 lvMyTSB.onRefreshComplete();	//涓嬫媺鍒锋柊瀹屾垚
		break;
	case 1://加载数据
		 if(new TsBillListAdapter(activity, listData)!=null){
        	 new TsBillListAdapter(activity, listData).notifyDataSetChanged();
         }
		 lvMyTSB.onLoadMoreComplete();	//鍔犺浇鏇村瀹屾垚
		break;
	default:
		break;
	}
}

//ajax获取待审单据。使用ajax来获取
private void getMyBillbylogonid(String logonid) {
	

		// 传输时，对密码进行加密处理，服务器端也进行加密处理。
		AjaxParams params = new AjaxParams();
		String strLogoID = sp.getString("USER_ID", "");
		String strURL = sp.getString("Server_URL", "");		
		
		params.put("LogonID", logonid);	
		params.put("GetType", RadioSelected);
		// 服务器字符串要从sharepre中获取。
		String URL = strURL + "/ws/serunit.asmx/GetMyBill";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();

				pdTab02.show();
			}
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
				// 返回用户名，表示登录正确。否则表示用户名密码错误
				if (Result.length() > 0) {
					//将result通过json解析
					listData.clear();
					getdatajson(Result);	
					
				} 
				pdTab02.dismiss();
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
//				Toast.makeText(getActivity(), "用户名、密码错误，请重新输入", Toast.LENGTH_LONG).show();
//				btnLogin.setVisibility(1);
				Dialog alertDialog = new AlertDialog.Builder(getActivity()).   
		                setTitle("警示").   
		                setMessage("网络或服务器故障").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
				pdTab02.dismiss();
			}
		});

}
/**
* 解析json数据
* @param strJson
* @return
*/
private void getdatajson(String strJson){
	try {
		//用JSON字符串来初始化一个JSON对象
	    JSONObject jsonObject = new JSONObject(strJson);
	    //然后读取result后面的数组([]号里的内容)，用这个内容来初始化一个JSONArray对象
	    JSONArray  aNews =  new JSONArray(jsonObject.getString("MyTSBill") );
	    for(int i=0; i< aNews.length(); i++)

	    {
	    	Map<String,Object>map=new HashMap<String, Object>();
	    	map.put("pic",R.drawable.ic_tsb);
	    	map.put("BillID",aNews.getJSONObject(i).getString("BillID"));
	    	map.put("Reporter",aNews.getJSONObject(i).getString("Reporter"));
	    	map.put("ReportDate",aNews.getJSONObject(i).getString("ReportDate"));
	    	map.put("TroubleAreaName",aNews.getJSONObject(i).getString("TroubleAreaName"));
	    	map.put("TroubleTypeName",aNews.getJSONObject(i).getString("TroubleTypeName"));
	    	map.put("ReportRemark",aNews.getJSONObject(i).getString("ReportRemark"));
	    	map.put("FlowTypeName",aNews.getJSONObject(i).getString("FlowTypeName"));
	    	listData.add(map);
	    }
//		tsBillListAdapter=new TsBillListAdapter(activity, listData);
		lvMyTSB.setAdapter(tsBillListAdapter);
	    
	} catch (Exception e) {
		// TODO: handle exception
		Log.e("JSON Error: ", e.toString());  
	}

}



}
