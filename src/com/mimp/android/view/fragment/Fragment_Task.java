package com.mimp.android.view.fragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mimp.android.ch0.MainActivity;
import com.mimp.android.ch0.R;
import com.mimp.android.ch0.ReflashListView;
import com.mimp.android.ch0.TroubleShootingBillActivity;
import com.mimp.android.ch0.TsBillListAdapter;

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
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class Fragment_Task extends Fragment implements OnClickListener{
	//0定义组件
	private final static int TSB_GREQUEST_CODE = 1;
	//private static final int RESULT_OK = 0;
	//定义一个线程对象
	private Handler handle;
	// http链接
	private FinalHttp http = null;
	private AjaxParams params = null;
	//定义sp
	private SharedPreferences sp = null;	

	private String strWhere="";
	private int dbCount=0;
	
	private ImageView ivmultifun=null;
	
	private String loginID = "";//获得登录ID
	private String strURL = "";//获取服务器地址
   private Intent intent=null;
   
   //定一个一个fragmentactivity
   private FragmentActivity activity;
	private ReflashListView listview;
	private TsBillListAdapter tsBillListAdapter ;
	//listview中的数据
	private ArrayList<Map<String,Object>> listData=new ArrayList<Map<String, Object>>();
   //定一个一个radiogroup
   private RadioGroup rgroupb_mission=null;
   private RadioButton rb_todo_mission=null;
   private RadioButton rb_done_mission=null;
   private RadioButton rb_all_mission=null;

   private ProgressDialog pd;//正在加载进度对话框
   
   private static final int LOAD_DATA_FINISH = 10;//涓婃媺鍒锋柊
   private static final int REFRESH_DATA_FINISH = 11;//涓嬫媺鍒锋柊
   private static final String TAG = "FindGroupFragment";
   private int mCount = 10;
   private String [] items = {"我要报修", "扫一扫", "系统设置"};
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	View view= inflater.inflate(R.layout.activity_main_tab01, container,false);
	//1实例化
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
	listview = (ReflashListView) this.getView().findViewById(R.id.lv_TaskList);	
	tsBillListAdapter=new TsBillListAdapter(activity, listData);
	listview.setAdapter(tsBillListAdapter);
	
	pd = new ProgressDialog(activity);  
	pd.setTitle("");
	pd.setMessage("数据加载中");
	pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	listview.setCanLoadMore(false);
	listview.setAutoLoadMore(false);
	
	
	
	
//	getCheckFmsByLoginID(loginID);
	//监听下拉刷新事件
	listview.setOnRefreshListener(new ReflashListView.OnRefreshListener() {
         @Override
         public void onRefresh() {
             // TODO 刷新
             Log.e(TAG, "onRefresh");
             loadData(0);
         }

     });
	//监听加载更多事件
	listview.setOnLoadListener(new ReflashListView.OnLoadMoreListener() {

         @Override
         public void onLoadMore() {
             // TODO 加载更多
             Log.e(TAG, "onLoad");
             loadData(1);
         }
     });
	listview.setOnItemClickListener(new OnItemClickListener() {
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
	    }
	});
	
	rgroupb_mission.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			//Toast.makeText(getActivity(),"100",Toast.LENGTH_LONG).show();
			// TODO Auto-generated method stub
			if (checkedId==R.id.rb_todo_mission){
//				Toast.makeText(getActivity(),"1",Toast.LENGTH_LONG).show();
				refreshdata("待办");
			}
			if (checkedId==R.id.rb_done_mission){
//				Toast.makeText(getActivity(),"2",Toast.LENGTH_LONG).show();
				refreshdata("已办");
			}
			if (checkedId==R.id.rb_all_mission){
//				Toast.makeText(getActivity(),"3",Toast.LENGTH_LONG).show();
				refreshdata("全部");
			}
		}
	});
	
	
}
private void listenter() {
	// TODO Auto-generated method stub

//	ivmultifun.setOnClickListener(this);
}
private void initview(View view) {
	// TODO Auto-generated method stub

//	ivmultifun=(ImageView) getActivity().findViewById(R.id.ivMultiFun);
	

	
	rgroupb_mission = (RadioGroup) view.findViewById(R.id.rgroupb_mission);
	rb_todo_mission=(RadioButton) view.findViewById(R.id.rb_todo_mission);
	rb_done_mission=(RadioButton) view.findViewById(R.id.rb_done_mission);
	rb_all_mission=(RadioButton) view.findViewById(R.id.rb_all_mission);
	
	
	//afinal进行ajax的清秀
	// 初始化ajax对象
	http = new FinalHttp();
	params = new AjaxParams();	
	// 初始化SharedPreferences.用来保存应用程序的一些偏好设置
	sp = getActivity().getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);	
	
	loginID = sp.getString("USER_LoginID", "");
	strURL = sp.getString("Server_URL", "");
	


}
@Override
public void onClick(View v) {
	switch (v.getId()) {
//	case R.id.ivMultiFun:
//		intent = new Intent();
//		intent.putExtra("BillId","");
//	    intent.setClass(getActivity(),TroubleShootingBillActivity.class );
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		//Toast.makeText(getActivity(), "result", 0).show();
//		startActivityForResult(intent, TSB_GREQUEST_CODE);
		

		
		
		
//		break;
	default:
		break;
	}
}



@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
	case TSB_GREQUEST_CODE:		
		//刷新listview的数据
		this.refreshdata("1");		
/*		if(resultCode == RESULT_OK){
			Bundle bundle = data.getExtras();
Toast.makeText(getActivity(), "result", 0).show();
		}*/
		
		break;
	}
}	
//是刷新列表及图标提醒
private void refreshdata(String flowstatus) {
	// TODO Auto-generated method stub

	loadData(0);
	MainActivity parentactivity = (MainActivity) getActivity();
	parentactivity.ButtonBadgeView();
}


/**
 * 加载数据
 * @param type
 */
public void loadData(final int type){
	switch (type) {
	case 0://刷新数据
		getCheckFmsByLoginID(loginID);
		break;
	case 1://加载数据
		getCheckFmsByLoginID(loginID);
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
				listData.clear();
				getdatajson(Result);	
				
			}
			pd.dismiss();
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(getActivity()).   
	                setTitle("警示").   
	                setMessage("网络或服务器故障").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
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
//	listData.clear();
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


		tsBillListAdapter.notifyDataSetChanged();
	    
	} catch (Exception e) {
		// TODO: handle exception
		Log.e("JSON Error: ", e.toString());  
	}

}
}
