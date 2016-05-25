package com.mimp.android.ch0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

@SuppressLint("HandlerLeak")
public class MainTab04Fragment extends Fragment {
	
	private FragmentActivity activity;
	private ReflashListView listview;
	private TsBillListAdapter tsBillListAdapter ;
	//listview中的数据
	private ArrayList<Map<String,Object>> listData=new ArrayList<Map<String, Object>>();


	// http链接
	private FinalHttp http = null;
	private AjaxParams params = null;
	//定义sp
	private SharedPreferences sp = null;
	
	private String loginID = "";//获得登录ID
	private String strURL = "";//获取服务器地址
	
	private ProgressDialog pd;//正在加载进度对话框
    private static final int LOAD_DATA_FINISH = 10;//涓婃媺鍒锋柊
    private static final int REFRESH_DATA_FINISH = 11;//涓嬫媺鍒锋柊
    private static final String TAG = "FindGroupFragment";
    private int mCount = 10;

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	View view = inflater.inflate(R.layout.activity_main_tab04, container,false);
	//初始化view。这里只放耗费时间少的语句
	initView();
	return view;
}
private void initView() {
	// TODO Auto-generated method stub
	//afinal进行ajax的清秀
	// 初始化ajax对象
	http = new FinalHttp();
	params = new AjaxParams();	
	// 初始化SharedPreferences.用来保存应用程序的一些偏好设置
	sp = getActivity().getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);	
	//获取登录ID。默认情况下。能进到这个位置的ID是存在的。
	loginID = sp.getString("USER_ID", "");
	strURL = sp.getString("Server_URL", "");
}
@Override
public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
	activity = getActivity();
	
	pd = new ProgressDialog(activity);  
	pd.setTitle("");
	pd.setMessage("数据加载中");
	pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	
	//初始化listview
	listview = (ReflashListView) this.getView().findViewById(R.id.listview);	
	
//	loadData(0);
//	getCheckFmsByLoginID("xuxiao");
	//监听下拉刷新事件
	listview.setOnRefreshListener(new ReflashListView.OnRefreshListener() {
         @Override
         public void onRefresh() {
             // TODO 刷新
             Log.e(TAG, "onRefresh");
//             loadData(0);
         }

     });
	//监听加载更多事件
	listview.setOnLoadListener(new ReflashListView.OnLoadMoreListener() {

         @Override
         public void onLoadMore() {
             // TODO 加载更多
             Log.e(TAG, "onLoad");
//             loadData(1);
         }
     });
 }
/**
 * 加载数据
 * @param type
 */
public void loadData(final int type){
	switch (type) {
	case 0://刷新数据
		getCheckFmsByLoginID("xuxiao");
		break;
	case 1://加载数据
		getCheckFmsByLoginID("xuxiao");
		break;
	default:
		break;
	}
	switch (type) {
	case 0://刷新数据
		 if(new TsBillListAdapter(activity, listData)!=null){
        	 new TsBillListAdapter(activity, listData).notifyDataSetChanged();
         }
         listview.onRefreshComplete();	//涓嬫媺鍒锋柊瀹屾垚
		break;
	case 1://加载数据
		 if(new TsBillListAdapter(activity, listData)!=null){
        	 new TsBillListAdapter(activity, listData).notifyDataSetChanged();
         }
         listview.onLoadMoreComplete();	//鍔犺浇鏇村瀹屾垚
		break;
	default:
		break;
	}
}

//
// public void loadData(final int type){
//     new Thread(){
//         @Override
//         public void run() {
//             switch (type) {
//                 case 0://刷新
//                	 getCheckFmsByLoginID("xuxiao");
//                     break;
//
//                 case 1://加载更多
//                	 int _Index = mCount + 10;
//                     mCount = _Index;
//                     getCheckFmsByLoginID("xuxiao");
//                     break;
//             }
//
//             try {
//                 Thread.sleep(2000);
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//
//             if(type==0){//刷新数据结束
//
//                 myHandler.sendEmptyMessage(REFRESH_DATA_FINISH);
//             }else if(type==1){
//            	 //加载数据结束
//                 myHandler.sendEmptyMessage(LOAD_DATA_FINISH);
//             }
//         }
//     }.start();
// }
 /**
  * 通过登录id获得待审的单据
  * @param string
  * @return
  */
 public void getCheckFmsByLoginID(String loginID) {
	// TODO Auto-generated method stub
	// 传输时，对密码进行加密处理，服务器端也进行加密处理。
	AjaxParams params = new AjaxParams();
	params.put("LogonID", loginID);		
	// 服务器字符串要从sharepre中获取。
	String URL = strURL + "/ws/serunit.asmx/GetTaskBill";
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			pd.show();
			//开始显示正在加载对话框
		}		
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// 返回用户名，表示登录正确。否则表示用户名密码错误
			if (Result.length() > 0) {
				getdatajson(Result);	
				pd.dismiss();
			}
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			pd.dismiss();
		}
	});	
}

/**
 * 解析json数据
 * @param strJson
 * @return
 */
private void getdatajson(String strJson){
	listData.clear();
	try {
		//用JSON字符串来初始化一个JSON对象
	    JSONObject jsonObject = new JSONObject(strJson);
	    //然后读取result后面的数组([]号里的内容)，用这个内容来初始化一个JSONArray对象
	    JSONArray  aNews =  new JSONArray( jsonObject.getString("TSBill") );
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
		Log.e("dataseize3", Integer.toString(listData.size()));
		tsBillListAdapter=new TsBillListAdapter(activity, listData);
		listview.setAdapter(tsBillListAdapter);
	    
	} catch (Exception e) {
		// TODO: handle exception
		Log.e("JSON Error: ", e.toString());  
	}

}

//获取最新数据
 //通知界面刷新
 //通知listview刷新数据完毕

}
