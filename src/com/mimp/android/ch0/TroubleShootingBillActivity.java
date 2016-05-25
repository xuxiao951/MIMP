package com.mimp.android.ch0;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mimp.android.po.ImageDispose;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
@SuppressLint({ "SimpleDateFormat", "WorldReadableFiles", "ShowToast" })
public class TroubleShootingBillActivity extends Activity implements OnClickListener{
	 private static final String TAG = "TroubleShootingBill";
	private final static int SCANNIN_GREQUEST_CODE = 1;
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;
	// 定义组件
	private TextView username = null;
	private TextView shootingdate = null;
	private TextView shootingbillid=null;
	private TextView tvRepairDeptName = null;
	private TextView tvReceiver = null;
	private TextView tvReceiveDate = null;
	private TextView tvFinDate = null;
	private TextView tvRepairJudge = null;
	
	private RatingBar RBRepairJudge = null;//维修评价评分
	private int RepairJudgeValue = 0;//维修评价值
	
	private Spinner sptroubletype = null;
	private Spinner sptroublearea = null;
	private Spinner spFinRepairName = null;
	private EditText troubledetail = null;
	private EditText checkdetail = null;//审核意见
	//两个标题，为了适应两个按钮。1是一个按钮的。2是两个按钮的。
	private TextView troubleshootingbilltitle1=null;
	private TextView troubleshootingbilltitle2=null;
	
	private Button btncodescan = null;
	private Button btnImageUpload = null;
	//定义一个线程对象
	private Handler handle;
	//全局参数变量
	private SharedPreferences sp = null;
	// http链接
	private FinalHttp http = null;
	private AjaxParams params = null;
	private String strLogonID = "";
	private String strUserID = "";
	private String strUserName = "";
	private String strURL = "";
	private String URL = "";
	private String strReporterID = "";//报修单中报修者的id号
	private String strID = "";
	private String strRepairDeptID = "";
	private String strRepairDeptName = "";
	private String strTroubleTypeID = "";
	private String strTroubleTypeName = "";
	private String strTroubleAreaID = "";
	private String strTroubleAreaName = "";
	private String strFinRepairName = "";
	private String strFinRepairID = "";
	//派工单号
	private String strDispatchID = "";
	
	private String strPhotoA = "";
	private String strPhotoB = "";
	
	// 定义了一个simpleadapter的适配器.但是这个适配器是给spinner用的.故障类型
	private SimpleAdapter simp_adapter_TroubleType = null;
	private List<Map<String, Object>> datalist_TroubleType;
	// 定义了一个simpleadapter的适配器.但是这个适配器是给spinner用的.故障位置
	private SimpleAdapter simp_adapter_TroubleArea = null;
	private List<Map<String, Object>> datalist_TroubleArea;
	// 定义了一个simpleadapter的适配器.但是这个适配器是给spinner用的.维修人
	private SimpleAdapter simp_adapter_FinRepairName = null;
	private List<Map<String, Object>> datalist_FinRepairName;
	
	private Button btnBack=null;
	private Button btnMultiFun1=null;
	private Button btnMultiFun2=null;
	
	private Intent intent=null;
	private String RequestID="";
	//故障申报单的流程状态。
	private int troubleshootingbillflowstatus=0;
	
	private List<Map<String, Object>> datalist_listview;
	//定义了一个listview的simpleadapter的适配器
	private SimpleAdapter simp_adapter_listview;
	//定义了一个listview
	private ListView lvchangelog;
	private FmsBean Fms= new FmsBean();//报修单类实例，保存报修单的数据。
	private String Fun1Name = "";//功能按钮1 点击时要执行的功能
	private String Fun2Name = "";//功能按钮2 点击时要执行的功能
	
	private ProgressDialog pd;//正在加载进度对话框
	private ProgressBar pbImageUpload;//上传图片进度条
	
	
	private ImageView ivTroubleImage = null;//故障图片
	private FinalBitmap fb = null;
	private String picPath = null;//选择的图片的路径
	//审核意见的布局
	private LinearLayout LinearLayoutApplyRemark = null;
	private ScrollView sv = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_troubleshootingbill);
		//获取传入的bill.根据billid的值进行各种判断
		intent = getIntent();  
		RequestID = intent.getStringExtra("BillId");  
//		System.out.println(RequestID);
		// 1实例化.实例化空间
		this.initview();
		// 2监听。空间的事件监听
		this.listener();

		 if(RequestID.trim().length()==0)//新增报修单
		 {
			 troubleshootingbillflowstatus=0;
			 this.PresetValue("","","",0);
			 
		 } else{//编辑状态			 
			 getBillByBillID(RequestID);
//			 //填充流程记录
			 getWorkFlowByBillID(RequestID);
					
		 }
		 /**
		  * 故障类型的监听事件	
		  */
		sptroubletype.setOnItemSelectedListener(new OnItemSelectedListener() {			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
	//						Toast.makeText(TroubleShootingBillActivity.this, "no", 0).show();
			}
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				TextView tv_troubletypeid = (TextView) view.findViewById(R.id.spinner_item_id);
				strTroubleTypeID = tv_troubletypeid.getText().toString();
				TextView tv_troubletypename = (TextView) view.findViewById(R.id.spinner_item_name);
				strTroubleTypeName = tv_troubletypename.getText().toString();
				getRepairDept();
			}
	
			
		});
		/**
		 * 故障区域的监听函数
		 */
		sptroublearea.setOnItemSelectedListener(new OnItemSelectedListener() {			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
	//						Toast.makeText(TroubleShootingBillActivity.this, "no", 0).show();
			}
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				TextView tv_troubleareaid = (TextView) view.findViewById(R.id.spinner_item_id);
				strTroubleAreaID = tv_troubleareaid.getText().toString();
				TextView tv_troubleareaname = (TextView) view.findViewById(R.id.spinner_item_name);
				strTroubleAreaName = tv_troubleareaname.getText().toString();
				getRepairDept();
			
			}
		});
		/**
		 * 维修人的监听函数
		 */
		spFinRepairName.setOnItemSelectedListener(new OnItemSelectedListener() {			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
	//						Toast.makeText(TroubleShootingBillActivity.this, "no", 0).show();
			}
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				TextView tv_FinRepairID = (TextView) view.findViewById(R.id.spinner_item_id);
				strFinRepairID = tv_FinRepairID.getText().toString();
				TextView tv_FinRepairName = (TextView) view.findViewById(R.id.spinner_item_name);
				strFinRepairName = tv_FinRepairName.getText().toString();
			}
		});
						
	    
	}

/**
 * 根据保修单号获取报修单相关数据
 * @param BillID
 */
	private void getBillByBillID(String BillID) {
			params.put("BillID", BillID);		
			// 服务器字符串要从sharepre中获取。
			String URL = strURL + "/ws/serunit.asmx/GetBillByBillID";
			http.post(URL, params, new AjaxCallBack<String>() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					pd = new ProgressDialog(TroubleShootingBillActivity.this);  
					pd.setTitle("");
					pd.setMessage("数据加载中");
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pd.show();
				}
				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					// TODO Auto-generated method stub
					String Result = t.replaceAll("(<[^>]*>)", "").trim();
					// 返回用户名，表示登录正确。否则表示用户名密码错误
					if (Result.length() > 0) {
						//将result通过json解析
						getbilldatajson(Result);	
						pd.dismiss();
					} 
				}
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
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
	 * 根据返回结果，解析报修单数据
	 * @param strJson
	 */
	private void getbilldatajson(String strJson){
		try {
		    datalist_TroubleType.clear();
		    datalist_TroubleArea.clear();
			//用JSON字符串来初始化一个JSON对象
		    JSONObject jsonObject = new JSONObject(strJson);
		    //然后读取result后面的数组([]号里的内容)，用这个内容来初始化一个JSONArray对象
		    JSONArray  aNews =  new JSONArray( jsonObject.getString("TSBill") );
		    for(int i=0; i< aNews.length(); i++)
		    {
		    	
		    	//不显示二维码扫描的按钮
//		    	ibtncodescan.setVisibility(View.INVISIBLE);
		    	//填充单号

		    	shootingbillid.setText(aNews.getJSONObject(i).getString("BillID"));
		    	username.setText(aNews.getJSONObject(i).getString("Reporter"));
		    	troubledetail.setText(aNews.getJSONObject(i).getString("ReportRemark"));
		    	shootingdate.setText(aNews.getJSONObject(i).getString("ReportDate"));
		    	tvRepairDeptName.setText(aNews.getJSONObject(i).getString("RepairDeptName"));

		    	
//		    	tvRepairJudge.setText(aNews.getJSONObject(i).getString("RepairJudge"));
		    	
		    	troubleshootingbillflowstatus= Integer.parseInt(aNews.getJSONObject(i).getString("FlowType"));  
		    	strRepairDeptID = aNews.getJSONObject(i).getString("RepairDeptID");
		    	
		    	strID = aNews.getJSONObject(i).getString("ID");
		    	strDispatchID = aNews.getJSONObject(i).getString("DispatchID");
		    	
		    	strReporterID = aNews.getJSONObject(i).getString("ReporterID");
		    	//初始化故障类型		    	
		    	strPhotoA =  aNews.getJSONObject(i).getString("PhotoA");
		    	strPhotoB =  aNews.getJSONObject(i).getString("PhotoB");

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("spinner_item_image", R.drawable.ic_launcher);
				map.put("spinner_item_id", aNews.getJSONObject(i).getString("TroubleType"));
				map.put("spinner_item_name", aNews.getJSONObject(i).getString("TroubleTypeName"));
				datalist_TroubleType.add(map);							

				simp_adapter_TroubleType.notifyDataSetChanged();  
		    	sptroubletype.setSelection(1,true);
		    	sptroubletype.setEnabled(false);
		    	//初始化故障位置
		    	
		    	Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("spinner_item_image", R.drawable.ic_launcher);
				map1.put("spinner_item_id", aNews.getJSONObject(i).getString("TroubleArea"));
				map1.put("spinner_item_name", aNews.getJSONObject(i).getString("TroubleAreaName"));
				datalist_TroubleArea.add(map1);
			
				simp_adapter_TroubleArea.notifyDataSetChanged();  
		    	sptroublearea.setSelection(1,true);
		    	sptroublearea.setEnabled(false);

//				//初始化维修人
				if (aNews.getJSONObject(i).getString("FinRepairID").length()>0 & Integer.parseInt(aNews.getJSONObject(i).getString("FlowType"))!=1)
				{
				datalist_FinRepairName.clear();
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("spinner_item_image", R.drawable.ic_launcher);
				map2.put("spinner_item_id", aNews.getJSONObject(i).getString("FinRepairID"));
				map2.put("spinner_item_name", aNews.getJSONObject(i).getString("FinRepairName"));
				datalist_FinRepairName.add(map2);
				simp_adapter_FinRepairName.notifyDataSetChanged();  
				spFinRepairName.setSelection(1,true);
				spFinRepairName.setEnabled(false);
				}
				//设置维修等级
				RBRepairJudge.setRating(Float.parseFloat(aNews.getJSONObject(i).getString("RepairJudge")));
				
				PresetValue(aNews.getJSONObject(i).getString("ReporterID"), aNews.getJSONObject(i).getString("ReceiverID"), strUserID,Integer.parseInt(aNews.getJSONObject(i).getString("FlowType")));
		    }
		    
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("JSON Error: ", e.toString());  
		}
		

	}	

/**
 * 预置
 * @param ReportID 报修人ID。id序列号
 * @param ReceriverID 接单人的ID。id序列号
 * @param UserID 当前用户ID 号。id自增序列号
 * @param Flag 0 设置菜单，1，执行按钮1的功能。2执行按钮2的功能
 */
	private void PresetValue(String ReportID,String ReceriverID,String UserID,int Flag) {
		// TODO Auto-generated method stub
		//如果图片不为空，下载图片缩略图
		if(!(strPhotoA == null || strPhotoA.length() <= 0))
		{
			LoadImage("A");
		}
		if(!(strPhotoB == null || strPhotoB.length() <= 0))
		{
			LoadImage("B");
		}
		switch (Flag) {
		case 0://新增报修单。初始填报
				btnMultiFun1.setVisibility(View.VISIBLE);
			    btnMultiFun1.setText("提交");
				sptroublearea.setEnabled(true);
				sptroubletype.setEnabled(true);
				
				username.setText(sp.getString("USER_NAME", "").toString());
				troubledetail.setEnabled(true);
				shootingdate.setText(getcurtime());
				genbillid();
				getTroubleArea();
				getTroubleType();				
				Fun1Name = "InsertBill()";
				
				LinearLayoutApplyRemark.setVisibility(View.GONE);
				
				break;
		case 1://已上报
			if (ReportID.equals(UserID)){
				btnMultiFun1.setVisibility(View.VISIBLE);
				//删除按钮
				btnMultiFun1.setText("删除");
				btnMultiFun2.setVisibility(View.GONE);
				RBRepairJudge.setIsIndicator(false);
				Fun1Name = "TSAudit(7)";
			}else
			{
				spFinRepairName.setEnabled(true);
				getFinRepairName();//获取可维修人。自己可以给自己修吗？
				//两个按钮。[派工]【退单】
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("退单");
				btnMultiFun2.setVisibility(View.VISIBLE);
				btnMultiFun2.setText("派工");
				troubleshootingbilltitle2.setText("部门接单");
				Fun1Name = "RefuseBill()";
				Fun2Name = "InsertDispatch()";
			}
			break;
		case 2://重报
			if (ReportID.equals(UserID)){
				//删除按钮
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("删除");
				btnMultiFun2.setVisibility(View.GONE);
				RBRepairJudge.setIsIndicator(false);
				Fun1Name = "TSAudit(7)";
			}else
			{
				spFinRepairName.setEnabled(true);
				getFinRepairName();//获取可维修人。自己可以给自己修吗？
				//两个按钮。[派工]【退单】
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("退单");
				btnMultiFun2.setVisibility(View.VISIBLE);
				btnMultiFun2.setText("派工");
				troubleshootingbilltitle2.setText("部门接单");
				Fun1Name = "RefuseBill()";
				Fun2Name = "InsertDispatch()";
			}
			break;
		case 3://已派工
			btnMultiFun1.setVisibility(View.VISIBLE);
			btnMultiFun1.setText("重派");
			troubleshootingbilltitle2.setText("维修记录");
			btnMultiFun2.setVisibility(View.VISIBLE);
			btnMultiFun2.setText("完成");			
			Fun1Name = "InsertRepairLog(6)";
			Fun2Name = "InsertRepairLog(5)";
			break;
		case 4://已退单
			//如果当前用户为报修人，那么可以确认，或者重报
			if (ReportID.equals(UserID)){
				//两个按钮。[确认]【重报】
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("重报");
				btnMultiFun2.setVisibility(View.VISIBLE);
				btnMultiFun2.setText("确认");
				troubleshootingbilltitle2.setText("部门退单");
				Fun1Name = "RepeatReport()";//这个函数还没有写
				Fun2Name = "TSAudit(7)";
				RBRepairJudge.setIsIndicator(false);
			}else if (ReceriverID.equals(UserID)){
				//一个按钮。【派工】
				spFinRepairName.setEnabled(true);
				getFinRepairName();//获取可维修人。自己可以给自己修吗？
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("派工");
				Fun1Name = "InsertDispatch()";
				
			}
			break;
		case 5://维修完成
			//两个按钮。[同意]【退回】
			if (ReportID.equals(UserID)){
			btnMultiFun1.setVisibility(View.VISIBLE);
			btnMultiFun1.setText("退回");
			btnMultiFun2.setVisibility(View.VISIBLE);
			btnMultiFun2.setText("同意");
			troubleshootingbilltitle2.setText("维修完成");
			RBRepairJudge.setIsIndicator(false);
			Fun1Name = "TSAudit(8)";//这个函数还没有写
			Fun2Name = "TSAudit(7)";
			}
			break;
		case 6://申请重派
			spFinRepairName.setEnabled(true);
			getFinRepairName();//获取可维修人。自己可以给自己修吗？
			btnMultiFun1.setVisibility(View.VISIBLE);
			btnMultiFun1.setText("派工");
			troubleshootingbilltitle2.setText("重新派工");
			Fun1Name = "InsertDispatch()";			
			break;
		case 7://报修完成
			btnMultiFun1.setVisibility(View.VISIBLE);
			btnMultiFun1.setVisibility(View.GONE);
			troubleshootingbilltitle2.setText("报修已完成");
			break;
		case 8://报修未确认
			//如果当前用户为报修人，那么可以确认，或者重报
			if (ReportID.equals(UserID)){
				//两个按钮。[确认]【重报】
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("确认");
				RBRepairJudge.setIsIndicator(false);
				Fun1Name = "TSAudit(7)";
			}else if (ReceriverID.equals(UserID)){
				//一个按钮。【接单】
				spFinRepairName.setEnabled(true);
				getFinRepairName();//获取可维修人。自己可以给自己修吗？
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("退单");
				btnMultiFun2.setVisibility(View.VISIBLE);
				btnMultiFun2.setText("重派");
				troubleshootingbilltitle2.setText("报修不确认");
				Fun1Name = "RefuseBill()";//这个函数还没有写
				Fun2Name = "InsertDispatch()";
			}
			break;
		default:
			break;
		}					 
	}	
private void LoadImage(String Type) {
	// TODO Auto-generated method stub
	String ImagePath = strURL+"/Data/Photon/"+RequestID+"_A.jpg";
	fb.display(ivTroubleImage,ImagePath);
}

/**
 * 左右的按钮要执行的功能
 * @param Fun2Name 要执行的函数名
 */
	private void Fun2(String fun2Name) {

		if (fun2Name.equals("TSAudit(7)")){
			TSAudit("7");
		}
		if (fun2Name.equals("InsertRepairLog(5)")){
			InsertRepairLog("5");
		}
		if (fun2Name.equals("InsertDispatch()")){
			InsertDispatch();
		}
}
	/**
	 * 右边的那妞要执行的功能
	 * @param Fun1Name 要执行的函数名
	 */
	private void Fun1(String fun1Name) {
	// TODO Auto-generated method stub
		if (fun1Name.equals("InsertBill()")){
			if (this.ValidateInput()) {
				insertBill();
			}
		}
		if (fun1Name.equals("TSAudit(7)")){
			TSAudit("7");
		}
		//重报
		if (fun1Name.equals("RepeatReport()")){
			RepeatReport();
		}
		
		if (fun1Name.equals("TSAudit(8)")){
			TSAudit("8");
		}
		if (fun1Name.equals("InsertDispatch()")){
			InsertDispatch();
		}
		if (fun1Name.equals("RefuseBill()")){
			RefuseBill();
		}
		if (fun1Name.equals("InsertRepairLog(6)")){
			InsertRepairLog("6");
		}
}
	/**
	 * 重报
	 */
	private void RepeatReport() {
		// TODO Auto-generated method stub
		URL = strURL + "/ws/serunit.asmx/RepeatReportTSB";
		params.put("ID", strID);
		params.put("ReporterID", strReporterID);	
		params.put("BillID", RequestID);	
		params.put("Reporter", strUserName);
		params.put("Remark", troubledetail.getText().toString());	
		params.put("RepairDeptID", strRepairDeptID);	
		params.put("RepairDeptName", strRepairDeptName);	
		params.put("TroubleArea", strTroubleAreaID);	
		params.put("TroubleAreaName", strTroubleAreaName);	
		params.put("TroubleType", strTroubleTypeID);			
		params.put("TroubleTypeName", strTroubleTypeName);	
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
				// 返回用户名，表示登录正确。否则表示用户名密码错误
				if (Result=="OK") {
					//将result通过json解析									
				} 
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
			}
		});			
		this.finish();	
	}
	// 实例化
	private void initview() {
		// TODO Auto-generated method stub
		pbImageUpload = (ProgressBar) findViewById(R.id.pb_ImageUpload);//上传进度对话框
		ivTroubleImage = (ImageView) this.findViewById(R.id.iv_trouble_image);
		fb = FinalBitmap.create(this);//初始化FinalBitmap模块
		sv = (ScrollView) this.findViewById(R.id.sv_Tsb);
		sv.smoothScrollTo(0, 0);
		btnBack=(Button) this.findViewById(R.id.button_back);			
		username = (TextView) this.findViewById(R.id.tvUserName);
		shootingdate = (TextView) this.findViewById(R.id.tvShootingDate);
		tvRepairDeptName = (TextView) this.findViewById(R.id.tvRepairDeptName);

	
		RBRepairJudge = (RatingBar) this.findViewById(R.id.rB_TSB);//维修评价进度条
		RBRepairJudge.setStepSize(1);//设置步长为1
		RBRepairJudge.setRating(RepairJudgeValue);
//		RBRepairJudge.setIsIndicator(false);

		sptroubletype = (Spinner) this.findViewById(R.id.spTroubleType);
		sptroublearea = (Spinner) this.findViewById(R.id.spTroubleArea);
		spFinRepairName = (Spinner) this.findViewById(R.id.spFinRepairName);
		
		sptroublearea.setEnabled(false);
		sptroubletype.setEnabled(false);
		spFinRepairName.setEnabled(false);
		
		
		troubledetail = (EditText) this.findViewById(R.id.etTroubleDetail);
		troubledetail.clearFocus();
		
		troubledetail.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		troubledetail.setSingleLine(false);
		troubledetail.setHorizontallyScrolling(false);
		troubledetail.setEnabled(false);
		
		checkdetail = (EditText) this.findViewById(R.id.et_apply_remark);
		checkdetail.clearFocus();
		
		checkdetail.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		checkdetail.setSingleLine(false);
		checkdetail.setHorizontallyScrolling(false);
		
		shootingbillid=(TextView) this.findViewById(R.id.tvBillID);
		
		btnMultiFun1=(Button) this.findViewById(R.id.button_multi_function_1);
		//btnMultiFun1.setText("测试");
		btnMultiFun2=(Button) this.findViewById(R.id.buttoin_multi_function_2);
		btnMultiFun2.setVisibility(View.INVISIBLE);
		btnMultiFun1.setVisibility(View.INVISIBLE);

		btncodescan = (Button) this.findViewById(R.id.btn_CodeScan);
		btnImageUpload = (Button) this.findViewById(R.id.btn_ImpageUpload);
		//定义一个标题组建
		troubleshootingbilltitle1=(TextView) this.findViewById(R.id.TroubleShootingbill_title1);
		troubleshootingbilltitle2=(TextView) this.findViewById(R.id.TroubleShootingbill_title2);
		
		LinearLayoutApplyRemark = (LinearLayout) this.findViewById(R.id.ll_tsb_apply_remark);
		
		//绑定流程listview
		lvchangelog = (ListView) this.findViewById(R.id.lvChangeLog);
		//获取全局参数
		sp = this.getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);
		
		// 初始化ajax对象
		http = new FinalHttp();
		params = new AjaxParams();	
		strURL = sp.getString("Server_URL", "");		
		//用户登录名
		strLogonID = sp.getString("USER_LoginID", "");
		//用户登录名
		strUserID = sp.getString("USER_ID", "");
		//用户名。中文名
		strUserName = sp.getString("USER_NAME", "");
		//服务器的地址只有虚拟目录的。
		strURL = sp.getString("Server_URL", "");	
		
		
		//初始化故障类型spinner
		datalist_TroubleType = new ArrayList<Map<String, Object>>();
		simp_adapter_TroubleType = new SimpleAdapter(this, datalist_TroubleType, R.layout.activity_troubleshootingbill_spinner,
				new String[] { "spinner_item_image", "spinner_item_id", "spinner_item_name" },
				new int[] { R.id.spinner_item_image, R.id.spinner_item_id, R.id.spinner_item_name });
		sptroubletype.setAdapter(simp_adapter_TroubleType);		
		sptroubletype.setSelection(0,false);
		
		//初始化故障类型spinner
		datalist_TroubleArea = new ArrayList<Map<String, Object>>();
		simp_adapter_TroubleArea = new SimpleAdapter(this, datalist_TroubleArea, R.layout.activity_troubleshootingbill_spinner,
				new String[] { "spinner_item_image", "spinner_item_id", "spinner_item_name" },
				new int[] { R.id.spinner_item_image, R.id.spinner_item_id, R.id.spinner_item_name });
		sptroublearea.setAdapter(simp_adapter_TroubleArea);		
		sptroublearea.setSelection(0,false);
		
		//初始化维修人spinner
		datalist_FinRepairName = new ArrayList<Map<String, Object>>();
		simp_adapter_FinRepairName = new SimpleAdapter(this, datalist_FinRepairName, R.layout.activity_troubleshootingbill_spinner,
				new String[] { "spinner_item_image", "spinner_item_id", "spinner_item_name" },
				new int[] { R.id.spinner_item_image, R.id.spinner_item_id, R.id.spinner_item_name });
		spFinRepairName.setAdapter(simp_adapter_FinRepairName);		
		spFinRepairName.setSelection(0,false);
		
		//初始化listview
		datalist_listview = new ArrayList<Map<String,Object>>();
		simp_adapter_listview = new SimpleAdapter(getApplication(), datalist_listview, R.layout.activity_troubleshootingbill_workflow_item, new String[]{"workflow_item_pic","workflow_item_OptName","workflow_item_ChangeDate","workflow_item_ChangeType","workflow_item_ChangeRemark"}, new int[]{R.id.id_workflow_item_pic,R.id.id_workflow_item_OptName,R.id.id_workflow_item_ChangeDate,R.id.id_workflow_item_ChangeType,R.id.id_workflow_item_ChangeRemark});
		lvchangelog.setAdapter(simp_adapter_listview);	
		
		

	}

//获得维修部门
private void getFinRepairName() {
	// TODO Auto-generated method stub	
//	if (troubleshootingbillflowstatus==1)
//	{
//		return;
//	}
//	
	// 服务器字符串要从sharepre中获取。
	String URL = strURL + "/ws/serunit.asmx/GetFinRepairName";
//			System.out.println("1234"+strRepairDeptID);
	params.put("RepairDeptID", strRepairDeptID);
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
//					System.out.println(Result);
			// 返回用户名，表示登录正确。否则表示用户名密码错误
			datalist_FinRepairName.clear();
			try {
				//用JSON字符串来初始化一个JSON对象
			    JSONObject jsonObject = new JSONObject(Result);
			    //然后读取result后面的数组([]号里的内容)，用这个内容来初始化一个JSONArray对象
			    JSONArray  aNews =  new JSONArray( jsonObject.getString("FinRepairName") );
			    for(int i=0; i< aNews.length(); i++)
			    {//				    	
			    	//初始化故障位置 自己不能给自己修
			    	if(!strReporterID.equals(aNews.getJSONObject(i).getString("ID"))){
			    		Map<String, Object> map = new HashMap<String, Object>();
						map.put("spinner_item_image", R.drawable.ic_launcher);
						map.put("spinner_item_id", aNews.getJSONObject(i).getString("ID"));
						map.put("spinner_item_name", aNews.getJSONObject(i).getString("Name"));
						datalist_FinRepairName.add(map);							
						simp_adapter_FinRepairName.notifyDataSetChanged();  
			    	}
			    	
			    }
			    
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("JSON Error: ", e.toString());  
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("警示").   
		                setMessage("当前维修部门没有可用的维修人员，请联系管理员").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
//				Toast.makeText(getApplication(), "当前维修部门没有可用的维修人员，请联系管理员", 0).show();
			}			
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("警示").   
	                setMessage("网络或服务器故障").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
		}
	});
	
}

	//获得维修部门
	private void getRepairDept() {
		// TODO Auto-generated method stub	
			if (strTroubleAreaID.length()>0 & strTroubleTypeID.length()>0)
			{
		// 传输时，对密码进行加密处理，服务器端也进行加密处理。
			AjaxParams params = new AjaxParams();
			String strURL = sp.getString("Server_URL", "");			
			// 服务器字符串要从sharepre中获取。
			String URL = strURL + "/ws/serunit.asmx/GeRepairDept";
			params.put("TroubleAreaID", strTroubleAreaID);
			params.put("TroubleTypeID", strTroubleTypeID);
			params.put("ReportDate", shootingdate.getText().toString().substring(11));
			http.post(URL, params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					// TODO Auto-generated method stub
					String Result = t.replaceAll("(<[^>]*>)", "").trim();
					// 返回用户名，表示登录正确。否则表示用户名密码错误
					if (Result.length() > 0) {
						String a[] = Result.split(",");   
						strRepairDeptID = a[0];
						strRepairDeptName = a[1];	
						tvRepairDeptName.setText(a[1]);					
					} else
					{
						strRepairDeptID = "";
						strRepairDeptName = "";	
						tvRepairDeptName.setText("");
//						Toast.makeText(getApplication(), "当前选择的区域与类型没有相应的维修部门，请联系管理员", 0).show();
						Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
				                setTitle("警示").   
				                setMessage("当前选择的区域与类型没有相应的维修部门，请联系管理员").   
				                setIcon(R.drawable.ic_launcher).   
				                create();   
				        alertDialog.show(); 
					}
				}
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
//					Toast.makeText(getApplication(), "网络错误，没有查到相应的维修部门，请联系管理员", 0).show();
					Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
			                setTitle("警示").   
			                setMessage("网络或服务器故障").   
			                setIcon(R.drawable.ic_launcher).   
			                create();   
			        alertDialog.show(); 
				}
			});
			}
	}

	//ajax获取故障区域，并填充
	private void getTroubleArea() {
	
		// 传输时，对密码进行加密处理，服务器端也进行加密处理。				
		// 服务器字符串要从sharepre中获取。
		String URL = strURL + "/ws/serunit.asmx/GeTroubleArea";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
				// 返回用户名，表示登录正确。否则表示用户名密码错误
				if (Result.length() > 0) {
					//将result通过json解析
					datalist_TroubleArea.clear();
					try {
						//用JSON字符串来初始化一个JSON对象
					    JSONObject jsonObject = new JSONObject(Result);
					    //然后读取result后面的数组([]号里的内容)，用这个内容来初始化一个JSONArray对象
					    JSONArray  aNews =  new JSONArray( jsonObject.getString("TroubleArea") );
					    for(int i=0; i< aNews.length(); i++)
					    {
	//						    	
					    	//初始化故障位置
					    	Map<String, Object> map = new HashMap<String, Object>();
							map.put("spinner_item_image", R.drawable.ic_launcher);
							map.put("spinner_item_id", aNews.getJSONObject(i).getString("code"));
							map.put("spinner_item_name", aNews.getJSONObject(i).getString("name"));
							datalist_TroubleArea.add(map);							
							simp_adapter_TroubleArea.notifyDataSetChanged(); 
					    }
					    
					} catch (Exception e) {
						// TODO: handle exception
						Log.e("JSON Error: ", e.toString());  
					}							
				} 
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("警示").   
		                setMessage("网络或服务器故障").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
			}
		});

	}		
	//ajax获取故障区域，并填充
	private void getTroubleType() {				
		// 服务器字符串要从sharepre中获取。
		String URL = strURL + "/ws/serunit.asmx/GeTroubleType";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
				// 返回用户名，表示登录正确。否则表示用户名密码错误
				if (Result.length() > 0) {
					//将result通过json解析
			    	datalist_TroubleType.clear();
					try {
						//用JSON字符串来初始化一个JSON对象
					    JSONObject jsonObject = new JSONObject(Result);
					    //然后读取result后面的数组([]号里的内容)，用这个内容来初始化一个JSONArray对象
					    JSONArray  aNews =  new JSONArray( jsonObject.getString("TroubleType") );
					    for(int i=0; i< aNews.length(); i++)
					    {
					    	//初始化故障类型

							Map<String, Object> map = new HashMap<String, Object>();
							map.put("spinner_item_image", R.drawable.ic_launcher);
							map.put("spinner_item_id", aNews.getJSONObject(i).getString("code"));
							map.put("spinner_item_name", aNews.getJSONObject(i).getString("name"));
							datalist_TroubleType.add(map);
							simp_adapter_TroubleType.notifyDataSetChanged();  
					    }
					    
					} catch (Exception e) {
						// TODO: handle exception
						Log.e("JSON Error: ", e.toString());  
					}							
				} 
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("警示").   
		                setMessage("网络或服务器故障").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
			}
		});

	}	
/**
 * 生成保修单号
 */
	private void genbillid() {
		// TODO Auto-generated method stub					
	// 服务器字符串要从sharepre中获取。
	String URL = strURL + "/ws/serunit.asmx/GeBillID";
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// 返回用户名，表示登录正确。否则表示用户名密码错误
			if (Result.length() > 0) {
				//将result通过json解析
				shootingbillid.setText(Result);						
			} 
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("警示").   
	                setMessage("网络或服务器故障").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
		}
	});	
			
	}
	/**
	 * 获取当前时间
	 */
	private String getcurtime() {
		// TODO Auto-generated method stub
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}

	// 监听
	private void listener() {
		// TODO Auto-generated method stub

		btncodescan.setOnClickListener(this);
		btnImageUpload.setOnClickListener(this);
		ivTroubleImage.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		
		btnMultiFun1.setOnClickListener(this);
		btnMultiFun2.setOnClickListener(this);
	}



@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	// 保存前要先对所必填的项目进行验证
	this.ValidateInput();
	switch (v.getId()) {
	case R.id.button_back:
		//Toast.makeText(this.getApplication(), "button_back", 0).show();
		this.finish();
		break;
	case R.id.btn_CodeScan:
		Intent intent = new Intent();
		intent.setClass(TroubleShootingBillActivity.this, CaptureActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
		break;
	case R.id.btn_ImpageUpload://上传图片功能
		Intent intent1 = new Intent(this,SelectPicActivity.class);
		startActivityForResult(intent1, TO_SELECT_PHOTO);
		break;	
	case R.id.iv_trouble_image:
		Toast.makeText(getApplication(), "to", 0).show();
		break;
	case R.id.button_multi_function_1:
		this.Fun1(Fun1Name);
		break;
	case R.id.buttoin_multi_function_2:
		this.Fun2(Fun2Name);
		break;
	default:
		break;
	}
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
	case SCANNIN_GREQUEST_CODE://扫描二维码结束 1
		if(resultCode == RESULT_OK){
			Bundle bundle = data.getExtras();
			//显示扫描到的内容			
			strURL = bundle.getString("result");
		}
	case TO_SELECT_PHOTO://选择图像结束 3
		Log.i(TAG, "最终选择的图片1=");
		Log.e(TAG, Integer.toString(resultCode));
		if(resultCode==Activity.RESULT_OK)
		{
			picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			Log.i(TAG, "最终选择的图片="+picPath);
			ivTroubleImage.setImageBitmap(new ImageDispose().getImageThumbnail(picPath,50,50));
			ImageUpload();
		}
		break;
	}
}

/**
 * 图片上传
 * @throws IOException 
 */
private void ImageUpload()  {
	pd = new ProgressDialog(TroubleShootingBillActivity.this);  
	pd.setTitle("");
	pd.setMessage("图片上传中");
	pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	pd.show();
	// TODO Auto-generated method stub
	// 服务器字符串要从sharepre中获取。
	AjaxParams params = new AjaxParams();
	Log.e("image", picPath);
	URL = strURL + "/ws/serunit.asmx/UploadImage";
	params.put("BillID", RequestID);		

	//String uploadBuffer1 = new String(Base64.encode(new ImageDispose().getBytesFromFile(new File(picPath)),Base64.DEFAULT));  //进行Base64编码  
	
	String uploadBuffer = new ImageDispose().bitmapToString(picPath);	
	
	
	params.put("fs", uploadBuffer);	
	Log.e(TAG, "url="+URL);
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();			
		}
		@Override
		public void onLoading(long count, long current) {
			// TODO Auto-generated method stub
			 //textView.setText(current+"/"+count);
			super.onLoading(count, current);
			Log.e("test","count = "+String.valueOf(count)+"current="+String.valueOf(current));
		}
		
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			pd.dismiss();
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("提示").   
	                setMessage("图片上传成功").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			// TODO Auto-generated method stub
			super.onFailure(t, errorNo, strMsg);
			pd.dismiss();
			
		}
		
	});		
}

//确认
private void TSAudit(String typeid) {	
	// 服务器字符串要从sharepre中获取。
URL = strURL + "/ws/serunit.asmx/TSAudit";
params.put("ID", strID);	
params.put("BillID", RequestID);	
params.put("RepairDeptID", strRepairDeptID);	
params.put("ReporterID", strReporterID);	
params.put("OptName", strUserName);	
params.put("TypeID", typeid);	
params.put("CheckRemark", checkdetail.getText().toString().trim());	
params.put("RepairJudge", Integer.toString((int) RBRepairJudge.getRating()));	
http.post(URL, params, new AjaxCallBack<String>() {
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		pd = new ProgressDialog(TroubleShootingBillActivity.this);  
		pd.setTitle("");
		pd.setMessage("数据保存中");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	@Override
	public void onSuccess(String t) {
		super.onSuccess(t);
		// TODO Auto-generated method stub
		String Result = t.replaceAll("(<[^>]*>)", "").trim();
		// 返回用户名，表示登录正确。否则表示用户名密码错误
		if (Result.equals("OK")) {
			//将result通过json解析	
			TroubleShootingBillActivity.this.finish();	
		}else
		{
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("警示").   
	                setMessage("提交失败，请联系管理员").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
		}
		pd.dismiss();
		
	}
	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
		super.onFailure(t, errorNo, strMsg);
		Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
                setTitle("警示").   
                setMessage("网络或服务器故障").   
                setIcon(R.drawable.ic_launcher).   
                create();   
        alertDialog.show(); 
        pd.dismiss();
	}
});			


}
//接单
private void ReceiveBill() {	
	// 服务器字符串要从sharepre中获取。				
	String URL = strURL + "/ws/serunit.asmx/ReceiveBill";
	String strLogoID = sp.getString("USER_NAME", "");
	params.put("LogonID", strLogoID);	
	params.put("BillID", RequestID);	
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			pd = new ProgressDialog(TroubleShootingBillActivity.this);  
			pd.setTitle("");
			pd.setMessage("数据保存中");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// 返回用户名，表示登录正确。否则表示用户名密码错误
			if (Result.equals("OK")) {
				//将result通过json解析
				TroubleShootingBillActivity.this.finish();					
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("警示").   
		                setMessage("提交失败，请联系管理员").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 		
			}
			pd.dismiss();
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
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
 * 退单
 */
	private void RefuseBill() {	
		// 服务器字符串要从sharepre中获取。
	URL = strURL + "/ws/serunit.asmx/RefuseBill";
	params.put("ReporterID", strReporterID);	
	params.put("ReceiverID", strUserID);	
	params.put("ReceiverName", strUserName);
	params.put("ID", strID);
	params.put("BillID", RequestID);	
	params.put("CheckRemark", checkdetail.getText().toString().trim());	
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			pd = new ProgressDialog(TroubleShootingBillActivity.this);  
			pd.setTitle("");
			pd.setMessage("数据保存中");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// 返回用户名，表示登录正确。否则表示用户名密码错误
			if (Result.equals("OK")) {
				//将result通过json解析		
				TroubleShootingBillActivity.this.finish();		
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("警示").   
		                setMessage("提交失败，请联系管理员").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 		
			} 
			pd.dismiss();
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("警示").   
	                setMessage("网络或服务器故障").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 	
			pd.dismiss();
		}
	});			


	}
	private void InsertDispatch() {	
		
	URL = strURL + "/ws/serunit.asmx/InsertDispatch";
	params.put("UserID", strUserID);	
	params.put("UserName", strUserName);	
	params.put("BillID", RequestID);	
	params.put("Dispatcher", strUserName);	
	params.put("RepairerID", strFinRepairID);
	params.put("RepairerName", strFinRepairName);	
	params.put("Remark", checkdetail.getText().toString().trim());
	
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			pd = new ProgressDialog(TroubleShootingBillActivity.this);  
			pd.setTitle("");
			pd.setMessage("数据保存中");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// 返回用户名，表示登录正确。否则表示用户名密码错误
			if (Result.equals("OK")) {
				//将result通过json解析		
				TroubleShootingBillActivity.this.finish();		
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("警示").   
		                setMessage("提交失败，请联系管理员").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 		
			} 
			pd.dismiss();
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("警示").   
	                setMessage("网络或服务器故障").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 	
			pd.dismiss();
		}
	});			
	}
//插入维修日志。
	private void InsertRepairLog(String TypeID) {
	// TODO Auto-generated method stub
		// TODO Auto-generated method stub
	// 传输时，对密码进行加密处理，服务器端也进行加密处理。							
	params.put("BillID", shootingbillid.getText().toString());	
	params.put("ReporterID", strReporterID);
	params.put("RepairDeptID", strRepairDeptID);
	params.put("TypeID", TypeID);	
	params.put("DispatchID", strDispatchID);	
	params.put("Opter", strUserName);	
	params.put("CheckRemark", checkdetail.getText().toString().trim());	
	// 服务器字符串要从sharepre中获取。
	String URL = strURL + "/ws/serunit.asmx/InsertRepairLog";
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			pd = new ProgressDialog(TroubleShootingBillActivity.this);  
			pd.setTitle("");
			pd.setMessage("数据保存中");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// 返回用户名，表示登录正确。否则表示用户名密码错误
			if (Result.equals("OK")) {
				//将result通过json解析
				TroubleShootingBillActivity.this.finish();		
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("警示").   
		                setMessage("提交失败，请联系管理员").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 		
			} 
			pd.dismiss();
		}
		
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("警示").   
	                setMessage("网络或服务器故障").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 	
	    	pd.dismiss();
		}
	});
		
		
}


//新增报修单
private void insertBill() {
	// TODO Auto-generated method stub
	
	// 传输时，对密码进行加密处理，服务器端也进行加密处理。
	AjaxParams params = new AjaxParams();
	String strURL = sp.getString("Server_URL", "");		
	
	params.put("BillID", shootingbillid.getText().toString());	
	params.put("UserID", strUserID);
	params.put("LogonID", sp.getString("USER_LoginID", ""));	
//			params.put("ReportDate", shootingdate.getText().toString());	
	params.put("Remark", troubledetail.getText().toString());	
	params.put("RepairDeptID", strRepairDeptID);	
	params.put("RepairDeptName", strRepairDeptName);	
	params.put("TroubleArea", strTroubleAreaID);	
	params.put("TroubleAreaName", strTroubleAreaName);	
	params.put("TroubleType", strTroubleTypeID);			
	params.put("TroubleTypeName", strTroubleTypeName);	
	// 服务器字符串要从sharepre中获取。
	String URL = strURL + "/ws/serunit.asmx/InsertTSB";
	
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			pd = new ProgressDialog(TroubleShootingBillActivity.this);  
			pd.setTitle("");
			pd.setMessage("数据保存中");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// 返回用户名，表示登录正确。否则表示用户名密码错误
			if (Result.equals("OK")) {
				//将result通过json解析
				TroubleShootingBillActivity.this.finish();		
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("警示").   
		                setMessage("提交失败，请联系管理员").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 		
			} 
			pd.dismiss();
		}
		
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("警示").   
	                setMessage("网络或服务器故障").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
			pd.dismiss();
		}
	});
}
// 在保存前，首先验证所必填项是否为空
private boolean ValidateInput() {
	// TODO Auto-generated method stub
	
	if (tvRepairDeptName.getText().toString().trim().length() == 0) {
		Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
                setTitle("警示").   
                setMessage("维修部门不能为空，请重新选择区域及类型，或者联系管理员").   
                setIcon(R.drawable.ic_launcher).   
                create();   
        alertDialog.show(); 
		return false;
	}
	return true;

}

/**
 * 根据保修单号获取工作流程信息
 * @param BillID
 */
private void getWorkFlowByBillID(String BillID) {
	
			
params.put("BillID", BillID);		
// 服务器字符串要从sharepre中获取。
String URL = strURL + "/ws/serunit.asmx/GeWorkFlowByBillID";
http.post(URL, params, new AjaxCallBack<String>() {
	@Override
	public void onSuccess(String t) {
		super.onSuccess(t);
		// TODO Auto-generated method stub
		String Result = t.replaceAll("(<[^>]*>)", "").trim();
		// 返回用户名，表示登录正确。否则表示用户名密码错误
		if (Result.length() > 0) {
			//将result通过json解析
			getworkflowdatajson(Result);	
			
		} 
	}
	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
		super.onFailure(t, errorNo, strMsg);
		Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
                setTitle("警示").   
                setMessage("网络或服务器故障").   
                setIcon(R.drawable.ic_launcher).   
                create();   
        alertDialog.show(); 
	}
});

}
private void getworkflowdatajson(String strJson){
	try {
//				 datalist_listview = new ArrayList<Map<String,Object>>();
		datalist_listview.clear();
		//用JSON字符串来初始化一个JSON对象
	    JSONObject jsonObject = new JSONObject(strJson);
	    //然后读取result后面的数组([]号里的内容)，用这个内容来初始化一个JSONArray对象
	    JSONArray  aNews =  new JSONArray( jsonObject.getString("WorkFlow") );
	    for(int i=0; i< aNews.length(); i++)

	    {
	    	Map<String, Object> map = new HashMap<String, Object>();
	    	map.put("workflow_item_pic",R.drawable.ic_launcher);
	    	map.put("workflow_item_OptName",aNews.getJSONObject(i).getString("OptName"));
	    	map.put("workflow_item_ChangeDate",aNews.getJSONObject(i).getString("ChangeDate"));
	    	map.put("workflow_item_ChangeType",aNews.getJSONObject(i).getString("FlowTypeName"));
	    	map.put("workflow_item_ChangeRemark",aNews.getJSONObject(i).getString("ChangeRemark"));
	    	datalist_listview.add(map);
	    }

	    
		simp_adapter_listview = new SimpleAdapter(getApplication(), getSortData(datalist_listview), R.layout.activity_troubleshootingbill_workflow_item, new String[]{"workflow_item_pic","workflow_item_OptName","workflow_item_ChangeDate","workflow_item_ChangeType","workflow_item_ChangeRemark"}, new int[]{R.id.id_workflow_item_pic,R.id.id_workflow_item_OptName,R.id.id_workflow_item_ChangeDate,R.id.id_workflow_item_ChangeType,R.id.id_workflow_item_ChangeRemark});
		lvchangelog.setAdapter(simp_adapter_listview);	
	    simp_adapter_listview.notifyDataSetChanged();


	    
	} catch (Exception e) {
		// TODO: handle exception
		Log.e("JSON Error: ", e.toString());  
	}
	

}
private List<Map<String, Object>> getSortData(List<Map<String,Object>> list) {


if (!list.isEmpty()) {    

     Collections.sort(list, new Comparator<Map<String, Object>>() {

      @Override

      public int compare(Map<String, Object> object1,Map<String, Object> object2) {
  //根据文本排序
           return ((String) object1.get("workflow_item_ChangeDate")).compareTo((String) object2.get("workflow_item_ChangeDate"));

      }    

     });    

}

return list;

}
		
}
