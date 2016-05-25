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
	//0�������
	private final static int TSB_GREQUEST_CODE = 1;
	//private static final int RESULT_OK = 0;
	//����һ���̶߳���
	private Handler handle;
	// http����
	private FinalHttp http = null;
	private AjaxParams params = null;
	//����sp
	private SharedPreferences sp = null;	

	private String strWhere="";
	private int dbCount=0;
	
	private ImageView ivmultifun=null;
	
	private String loginID = "";//��õ�¼ID
	private String strURL = "";//��ȡ��������ַ
   private Intent intent=null;
   
   //��һ��һ��fragmentactivity
   private FragmentActivity activity;
	private ReflashListView listview;
	private TsBillListAdapter tsBillListAdapter ;
	//listview�е�����
	private ArrayList<Map<String,Object>> listData=new ArrayList<Map<String, Object>>();
   //��һ��һ��radiogroup
   private RadioGroup rgroupb_mission=null;
   private RadioButton rb_todo_mission=null;
   private RadioButton rb_done_mission=null;
   private RadioButton rb_all_mission=null;

   private ProgressDialog pd;//���ڼ��ؽ��ȶԻ���
   
   private static final int LOAD_DATA_FINISH = 10;//上拉刷新
   private static final int REFRESH_DATA_FINISH = 11;//下拉刷新
   private static final String TAG = "FindGroupFragment";
   private int mCount = 10;
   private String [] items = {"��Ҫ����", "ɨһɨ", "ϵͳ����"};
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	View view= inflater.inflate(R.layout.activity_main_tab01, container,false);
	//1ʵ����
	this.initview(view);
	//2����
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
	
	//��ʼ��listview
	listview = (ReflashListView) this.getView().findViewById(R.id.lv_TaskList);	
	tsBillListAdapter=new TsBillListAdapter(activity, listData);
	listview.setAdapter(tsBillListAdapter);
	
	pd = new ProgressDialog(activity);  
	pd.setTitle("");
	pd.setMessage("���ݼ�����");
	pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	listview.setCanLoadMore(false);
	listview.setAutoLoadMore(false);
	
	
	
	
//	getCheckFmsByLoginID(loginID);
	//��������ˢ���¼�
	listview.setOnRefreshListener(new ReflashListView.OnRefreshListener() {
         @Override
         public void onRefresh() {
             // TODO ˢ��
             Log.e(TAG, "onRefresh");
             loadData(0);
         }

     });
	//�������ظ����¼�
	listview.setOnLoadListener(new ReflashListView.OnLoadMoreListener() {

         @Override
         public void onLoadMore() {
             // TODO ���ظ��च
             Log.e(TAG, "onLoad");
             loadData(1);
         }
     });
	listview.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> adapterView, View view, int position,long id) {
		View v=adapterView.getChildAt(position);
		
		//v.setBackgroundColor(Color.RED);
		//Toast.makeText(getActivity(),"��ǰ��ID="+Integer.toString(position),Toast.LENGTH_LONG).show();
		//Toast.makeText(getActivity(),"��ǰ��ID="+datalist.get(position).get("Id"),Toast.LENGTH_LONG).show();
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
				refreshdata("����");
			}
			if (checkedId==R.id.rb_done_mission){
//				Toast.makeText(getActivity(),"2",Toast.LENGTH_LONG).show();
				refreshdata("�Ѱ�");
			}
			if (checkedId==R.id.rb_all_mission){
//				Toast.makeText(getActivity(),"3",Toast.LENGTH_LONG).show();
				refreshdata("ȫ��");
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
	
	
	//afinal����ajax������
	// ��ʼ��ajax����
	http = new FinalHttp();
	params = new AjaxParams();	
	// ��ʼ��SharedPreferences.��������Ӧ�ó����һЩƫ������
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
		//ˢ��listview������
		this.refreshdata("1");		
/*		if(resultCode == RESULT_OK){
			Bundle bundle = data.getExtras();
Toast.makeText(getActivity(), "result", 0).show();
		}*/
		
		break;
	}
}	
//��ˢ���б�ͼ������
private void refreshdata(String flowstatus) {
	// TODO Auto-generated method stub

	loadData(0);
	MainActivity parentactivity = (MainActivity) getActivity();
	parentactivity.ButtonBadgeView();
}


/**
 * ��������
 * @param type
 */
public void loadData(final int type){
	switch (type) {
	case 0://ˢ������
		getCheckFmsByLoginID(loginID);
		break;
	case 1://��������
		getCheckFmsByLoginID(loginID);
		break;
	default:
		break;
	}

	switch (type) {
	case 0://ˢ������
		 if(new TsBillListAdapter(activity, listData)!=null){
        	 new TsBillListAdapter(activity, listData).notifyDataSetChanged();
         }
         listview.onRefreshComplete();	//下拉刷新完成
		break;
	case 1://��������
		 if(new TsBillListAdapter(activity, listData)!=null){
        	 new TsBillListAdapter(activity, listData).notifyDataSetChanged();
         }
         listview.onLoadMoreComplete();	//加载更多完成
		break;
	default:
		break;
	}
}
/**
 * ͨ����¼id��ô���ĵ���
 * @param string
 * @return
 */
public void getCheckFmsByLoginID(String loginID) {
	// TODO Auto-generated method stub
	// ����ʱ����������м��ܴ�����������Ҳ���м��ܴ���
	AjaxParams params = new AjaxParams();
	params.put("LogonID", loginID);		
	// �������ַ���Ҫ��sharepre�л�ȡ��
	String URL = strURL + "/ws/serunit.asmx/GetTaskBill";
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			
			pd.show();
			//��ʼ��ʾ���ڼ��ضԻ���
		}		
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();			
			// �����û�������ʾ��¼��ȷ�������ʾ�û����������
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
	                setTitle("��ʾ").   
	                setMessage("��������������").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
			pd.dismiss();
		}
	});	
}

/**
* ����json����
* @param strJson
* @return
*/
private void getdatajson(String strJson){
//	listData.clear();
	try {
		//��JSON�ַ�������ʼ��һ��JSON����
	    JSONObject jsonObject = new JSONObject(strJson);
	    //Ȼ���ȡresult���������([]���������)���������������ʼ��һ��JSONArray����
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
