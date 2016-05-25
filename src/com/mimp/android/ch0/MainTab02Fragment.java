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
//����һ���̶߳���
private Handler handle;
// http����
private FinalHttp http = null;
private AjaxParams params = null;
//����sp
private SharedPreferences sp = null;

private String loginID = "";//��õ�¼ID
private String strURL = "";//��ȡ��������ַ

private Intent intent=null;

//��һ��һ��fragmentactivity
private FragmentActivity activity;
private ReflashListView lvMyTSB;
private TsBillListAdapter tsBillListAdapter ;
//listview�е�����
private ArrayList<Map<String,Object>> listData=new ArrayList<Map<String, Object>>();
	
   
private ProgressDialog pdTab02;//���ڼ��ؽ��ȶԻ���
   
private static final int LOAD_DATA_FINISH = 10;//上拉刷新
private static final int REFRESH_DATA_FINISH = 11;//下拉刷新
private static final String TAG = "FindGroupFragment";
private int mCount = 10;

//��һ��һ��radiogroup
private RadioGroup rgroupb_mission=null;
private RadioButton rb_todo_mission=null;
private RadioButton rb_done_mission=null;
private RadioButton rb_all_mission=null;
//�洢ѡ�е�radio��Ĭ��ѡ�е�һ��
private String RadioSelected = "1";

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	View view =  inflater.inflate(R.layout.activity_main_tab02, container,false);
	//��ʼ��
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
lvMyTSB = (ReflashListView) this.getView().findViewById(R.id.lvMyTSB);		

tsBillListAdapter=new TsBillListAdapter(activity, listData);
lvMyTSB.setAdapter(tsBillListAdapter);

lvMyTSB.setCanLoadMore(false);
lvMyTSB.setAutoLoadMore(false);

pdTab02 = new ProgressDialog(activity);  
pdTab02.setTitle("");
pdTab02.setMessage("���ݼ�����");
pdTab02.setProgressStyle(ProgressDialog.STYLE_SPINNER);


//getMyBillbylogonid(loginID);
//��������ˢ���¼�
lvMyTSB.setOnRefreshListener(new ReflashListView.OnRefreshListener() {
     @Override
     public void onRefresh() {
         // TODO ˢ��
         Log.e(TAG, "onRefresh");
         loadData(0);
     }

 });
//�������ظ����¼�
lvMyTSB.setOnLoadListener(new ReflashListView.OnLoadMoreListener() {

     @Override
     public void onLoadMore() {
         // TODO ���ظ��च
         Log.e(TAG, "onLoad");
         loadData(1);
     }
 });
lvMyTSB.setOnItemClickListener(new OnItemClickListener() {
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
	
	//Toast.makeText(getActivity(),"��ѡ���˵�ǰ��"+datalist.get(position).get("Id"),Toast.LENGTH_LONG).show();
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
			refreshdata("����");
		}
		if (checkedId==R.id.rb_done_mission){
//					Toast.makeText(getActivity(),"2",Toast.LENGTH_LONG).show();
			RadioSelected = "2";
			refreshdata("�Ѱ�");
		}
		if (checkedId==R.id.rb_all_mission){
//					Toast.makeText(getActivity(),"3",Toast.LENGTH_LONG).show();
			RadioSelected = "3";
			refreshdata("ȫ��");
		}
	}
});	
		
	}
private void listenter() {
	// TODO Auto-generated method stub
	
}
private void initview(View view) {
	// TODO Auto-generated method stub


	//afinal����ajax������
	// ��ʼ��ajax����
	http = new FinalHttp();
	params = new AjaxParams();	
	// ��ʼ��SharedPreferences.��������Ӧ�ó����һЩƫ������
	sp = getActivity().getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);	
	
//    GetMyBill(view);
	

	
	rgroupb_mission = (RadioGroup) view.findViewById(R.id.rgroupb_mission);
	rb_todo_mission=(RadioButton) view.findViewById(R.id.rb_todo_mission);
	rb_done_mission=(RadioButton) view.findViewById(R.id.rb_done_mission);
	rb_all_mission=(RadioButton) view.findViewById(R.id.rb_all_mission);
	
	loginID = sp.getString("USER_LoginID", "");
	strURL = sp.getString("Server_URL", "");
	
}
//��ˢ���б�ͼ������
private void refreshdata(String flowstatus) {
	// TODO Auto-generated method stub
	loadData(0);
	//���¸�д��ť����ʾ����
	//ButtonBadgeView();
	MainActivity parentactivity = (MainActivity) getActivity();
	parentactivity.ButtonBadgeView();
}


@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	
}


/**
 * ��������
 * @param type
 */
public void loadData(final int type){

	switch (type) {
	case 0://ˢ������
		getMyBillbylogonid(loginID);
		break;
	case 1://��������
		getMyBillbylogonid(loginID);
		break;
	default:
		break;
	}

	switch (type) {
	case 0://ˢ������
		 if(new TsBillListAdapter(activity, listData)!=null){
        	 new TsBillListAdapter(activity, listData).notifyDataSetChanged();
         }
		 lvMyTSB.onRefreshComplete();	//下拉刷新完成
		break;
	case 1://��������
		 if(new TsBillListAdapter(activity, listData)!=null){
        	 new TsBillListAdapter(activity, listData).notifyDataSetChanged();
         }
		 lvMyTSB.onLoadMoreComplete();	//加载更多完成
		break;
	default:
		break;
	}
}

//ajax��ȡ���󵥾ݡ�ʹ��ajax����ȡ
private void getMyBillbylogonid(String logonid) {
	

		// ����ʱ����������м��ܴ�����������Ҳ���м��ܴ���
		AjaxParams params = new AjaxParams();
		String strLogoID = sp.getString("USER_ID", "");
		String strURL = sp.getString("Server_URL", "");		
		
		params.put("LogonID", logonid);	
		params.put("GetType", RadioSelected);
		// �������ַ���Ҫ��sharepre�л�ȡ��
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
				// �����û�������ʾ��¼��ȷ�������ʾ�û����������
				if (Result.length() > 0) {
					//��resultͨ��json����
					listData.clear();
					getdatajson(Result);	
					
				} 
				pdTab02.dismiss();
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
//				Toast.makeText(getActivity(), "�û����������������������", Toast.LENGTH_LONG).show();
//				btnLogin.setVisibility(1);
				Dialog alertDialog = new AlertDialog.Builder(getActivity()).   
		                setTitle("��ʾ").   
		                setMessage("��������������").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
				pdTab02.dismiss();
			}
		});

}
/**
* ����json����
* @param strJson
* @return
*/
private void getdatajson(String strJson){
	try {
		//��JSON�ַ�������ʼ��һ��JSON����
	    JSONObject jsonObject = new JSONObject(strJson);
	    //Ȼ���ȡresult���������([]���������)���������������ʼ��һ��JSONArray����
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
