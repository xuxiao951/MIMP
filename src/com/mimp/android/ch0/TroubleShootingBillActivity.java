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
	 * ѡ���ļ�
	 */
	public static final int TO_SELECT_PHOTO = 3;
	// �������
	private TextView username = null;
	private TextView shootingdate = null;
	private TextView shootingbillid=null;
	private TextView tvRepairDeptName = null;
	private TextView tvReceiver = null;
	private TextView tvReceiveDate = null;
	private TextView tvFinDate = null;
	private TextView tvRepairJudge = null;
	
	private RatingBar RBRepairJudge = null;//ά����������
	private int RepairJudgeValue = 0;//ά������ֵ
	
	private Spinner sptroubletype = null;
	private Spinner sptroublearea = null;
	private Spinner spFinRepairName = null;
	private EditText troubledetail = null;
	private EditText checkdetail = null;//������
	//�������⣬Ϊ����Ӧ������ť��1��һ����ť�ġ�2��������ť�ġ�
	private TextView troubleshootingbilltitle1=null;
	private TextView troubleshootingbilltitle2=null;
	
	private Button btncodescan = null;
	private Button btnImageUpload = null;
	//����һ���̶߳���
	private Handler handle;
	//ȫ�ֲ�������
	private SharedPreferences sp = null;
	// http����
	private FinalHttp http = null;
	private AjaxParams params = null;
	private String strLogonID = "";
	private String strUserID = "";
	private String strUserName = "";
	private String strURL = "";
	private String URL = "";
	private String strReporterID = "";//���޵��б����ߵ�id��
	private String strID = "";
	private String strRepairDeptID = "";
	private String strRepairDeptName = "";
	private String strTroubleTypeID = "";
	private String strTroubleTypeName = "";
	private String strTroubleAreaID = "";
	private String strTroubleAreaName = "";
	private String strFinRepairName = "";
	private String strFinRepairID = "";
	//�ɹ�����
	private String strDispatchID = "";
	
	private String strPhotoA = "";
	private String strPhotoB = "";
	
	// ������һ��simpleadapter��������.��������������Ǹ�spinner�õ�.��������
	private SimpleAdapter simp_adapter_TroubleType = null;
	private List<Map<String, Object>> datalist_TroubleType;
	// ������һ��simpleadapter��������.��������������Ǹ�spinner�õ�.����λ��
	private SimpleAdapter simp_adapter_TroubleArea = null;
	private List<Map<String, Object>> datalist_TroubleArea;
	// ������һ��simpleadapter��������.��������������Ǹ�spinner�õ�.ά����
	private SimpleAdapter simp_adapter_FinRepairName = null;
	private List<Map<String, Object>> datalist_FinRepairName;
	
	private Button btnBack=null;
	private Button btnMultiFun1=null;
	private Button btnMultiFun2=null;
	
	private Intent intent=null;
	private String RequestID="";
	//�����걨��������״̬��
	private int troubleshootingbillflowstatus=0;
	
	private List<Map<String, Object>> datalist_listview;
	//������һ��listview��simpleadapter��������
	private SimpleAdapter simp_adapter_listview;
	//������һ��listview
	private ListView lvchangelog;
	private FmsBean Fms= new FmsBean();//���޵���ʵ�������汨�޵������ݡ�
	private String Fun1Name = "";//���ܰ�ť1 ���ʱҪִ�еĹ���
	private String Fun2Name = "";//���ܰ�ť2 ���ʱҪִ�еĹ���
	
	private ProgressDialog pd;//���ڼ��ؽ��ȶԻ���
	private ProgressBar pbImageUpload;//�ϴ�ͼƬ������
	
	
	private ImageView ivTroubleImage = null;//����ͼƬ
	private FinalBitmap fb = null;
	private String picPath = null;//ѡ���ͼƬ��·��
	//�������Ĳ���
	private LinearLayout LinearLayoutApplyRemark = null;
	private ScrollView sv = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_troubleshootingbill);
		//��ȡ�����bill.����billid��ֵ���и����ж�
		intent = getIntent();  
		RequestID = intent.getStringExtra("BillId");  
//		System.out.println(RequestID);
		// 1ʵ����.ʵ�����ռ�
		this.initview();
		// 2�������ռ���¼�����
		this.listener();

		 if(RequestID.trim().length()==0)//�������޵�
		 {
			 troubleshootingbillflowstatus=0;
			 this.PresetValue("","","",0);
			 
		 } else{//�༭״̬			 
			 getBillByBillID(RequestID);
//			 //������̼�¼
			 getWorkFlowByBillID(RequestID);
					
		 }
		 /**
		  * �������͵ļ����¼�	
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
		 * ��������ļ�������
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
		 * ά���˵ļ�������
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
 * ���ݱ��޵��Ż�ȡ���޵��������
 * @param BillID
 */
	private void getBillByBillID(String BillID) {
			params.put("BillID", BillID);		
			// �������ַ���Ҫ��sharepre�л�ȡ��
			String URL = strURL + "/ws/serunit.asmx/GetBillByBillID";
			http.post(URL, params, new AjaxCallBack<String>() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					pd = new ProgressDialog(TroubleShootingBillActivity.this);  
					pd.setTitle("");
					pd.setMessage("���ݼ�����");
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pd.show();
				}
				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					// TODO Auto-generated method stub
					String Result = t.replaceAll("(<[^>]*>)", "").trim();
					// �����û�������ʾ��¼��ȷ�������ʾ�û����������
					if (Result.length() > 0) {
						//��resultͨ��json����
						getbilldatajson(Result);	
						pd.dismiss();
					} 
				}
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
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
	 * ���ݷ��ؽ�����������޵�����
	 * @param strJson
	 */
	private void getbilldatajson(String strJson){
		try {
		    datalist_TroubleType.clear();
		    datalist_TroubleArea.clear();
			//��JSON�ַ�������ʼ��һ��JSON����
		    JSONObject jsonObject = new JSONObject(strJson);
		    //Ȼ���ȡresult���������([]���������)���������������ʼ��һ��JSONArray����
		    JSONArray  aNews =  new JSONArray( jsonObject.getString("TSBill") );
		    for(int i=0; i< aNews.length(); i++)
		    {
		    	
		    	//����ʾ��ά��ɨ��İ�ť
//		    	ibtncodescan.setVisibility(View.INVISIBLE);
		    	//��䵥��

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
		    	//��ʼ����������		    	
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
		    	//��ʼ������λ��
		    	
		    	Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("spinner_item_image", R.drawable.ic_launcher);
				map1.put("spinner_item_id", aNews.getJSONObject(i).getString("TroubleArea"));
				map1.put("spinner_item_name", aNews.getJSONObject(i).getString("TroubleAreaName"));
				datalist_TroubleArea.add(map1);
			
				simp_adapter_TroubleArea.notifyDataSetChanged();  
		    	sptroublearea.setSelection(1,true);
		    	sptroublearea.setEnabled(false);

//				//��ʼ��ά����
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
				//����ά�޵ȼ�
				RBRepairJudge.setRating(Float.parseFloat(aNews.getJSONObject(i).getString("RepairJudge")));
				
				PresetValue(aNews.getJSONObject(i).getString("ReporterID"), aNews.getJSONObject(i).getString("ReceiverID"), strUserID,Integer.parseInt(aNews.getJSONObject(i).getString("FlowType")));
		    }
		    
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("JSON Error: ", e.toString());  
		}
		

	}	

/**
 * Ԥ��
 * @param ReportID ������ID��id���к�
 * @param ReceriverID �ӵ��˵�ID��id���к�
 * @param UserID ��ǰ�û�ID �š�id�������к�
 * @param Flag 0 ���ò˵���1��ִ�а�ť1�Ĺ��ܡ�2ִ�а�ť2�Ĺ���
 */
	private void PresetValue(String ReportID,String ReceriverID,String UserID,int Flag) {
		// TODO Auto-generated method stub
		//���ͼƬ��Ϊ�գ�����ͼƬ����ͼ
		if(!(strPhotoA == null || strPhotoA.length() <= 0))
		{
			LoadImage("A");
		}
		if(!(strPhotoB == null || strPhotoB.length() <= 0))
		{
			LoadImage("B");
		}
		switch (Flag) {
		case 0://�������޵�����ʼ�
				btnMultiFun1.setVisibility(View.VISIBLE);
			    btnMultiFun1.setText("�ύ");
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
		case 1://���ϱ�
			if (ReportID.equals(UserID)){
				btnMultiFun1.setVisibility(View.VISIBLE);
				//ɾ����ť
				btnMultiFun1.setText("ɾ��");
				btnMultiFun2.setVisibility(View.GONE);
				RBRepairJudge.setIsIndicator(false);
				Fun1Name = "TSAudit(7)";
			}else
			{
				spFinRepairName.setEnabled(true);
				getFinRepairName();//��ȡ��ά���ˡ��Լ����Ը��Լ�����
				//������ť��[�ɹ�]���˵���
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("�˵�");
				btnMultiFun2.setVisibility(View.VISIBLE);
				btnMultiFun2.setText("�ɹ�");
				troubleshootingbilltitle2.setText("���Žӵ�");
				Fun1Name = "RefuseBill()";
				Fun2Name = "InsertDispatch()";
			}
			break;
		case 2://�ر�
			if (ReportID.equals(UserID)){
				//ɾ����ť
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("ɾ��");
				btnMultiFun2.setVisibility(View.GONE);
				RBRepairJudge.setIsIndicator(false);
				Fun1Name = "TSAudit(7)";
			}else
			{
				spFinRepairName.setEnabled(true);
				getFinRepairName();//��ȡ��ά���ˡ��Լ����Ը��Լ�����
				//������ť��[�ɹ�]���˵���
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("�˵�");
				btnMultiFun2.setVisibility(View.VISIBLE);
				btnMultiFun2.setText("�ɹ�");
				troubleshootingbilltitle2.setText("���Žӵ�");
				Fun1Name = "RefuseBill()";
				Fun2Name = "InsertDispatch()";
			}
			break;
		case 3://���ɹ�
			btnMultiFun1.setVisibility(View.VISIBLE);
			btnMultiFun1.setText("����");
			troubleshootingbilltitle2.setText("ά�޼�¼");
			btnMultiFun2.setVisibility(View.VISIBLE);
			btnMultiFun2.setText("���");			
			Fun1Name = "InsertRepairLog(6)";
			Fun2Name = "InsertRepairLog(5)";
			break;
		case 4://���˵�
			//�����ǰ�û�Ϊ�����ˣ���ô����ȷ�ϣ������ر�
			if (ReportID.equals(UserID)){
				//������ť��[ȷ��]���ر���
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("�ر�");
				btnMultiFun2.setVisibility(View.VISIBLE);
				btnMultiFun2.setText("ȷ��");
				troubleshootingbilltitle2.setText("�����˵�");
				Fun1Name = "RepeatReport()";//���������û��д
				Fun2Name = "TSAudit(7)";
				RBRepairJudge.setIsIndicator(false);
			}else if (ReceriverID.equals(UserID)){
				//һ����ť�����ɹ���
				spFinRepairName.setEnabled(true);
				getFinRepairName();//��ȡ��ά���ˡ��Լ����Ը��Լ�����
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("�ɹ�");
				Fun1Name = "InsertDispatch()";
				
			}
			break;
		case 5://ά�����
			//������ť��[ͬ��]���˻ء�
			if (ReportID.equals(UserID)){
			btnMultiFun1.setVisibility(View.VISIBLE);
			btnMultiFun1.setText("�˻�");
			btnMultiFun2.setVisibility(View.VISIBLE);
			btnMultiFun2.setText("ͬ��");
			troubleshootingbilltitle2.setText("ά�����");
			RBRepairJudge.setIsIndicator(false);
			Fun1Name = "TSAudit(8)";//���������û��д
			Fun2Name = "TSAudit(7)";
			}
			break;
		case 6://��������
			spFinRepairName.setEnabled(true);
			getFinRepairName();//��ȡ��ά���ˡ��Լ����Ը��Լ�����
			btnMultiFun1.setVisibility(View.VISIBLE);
			btnMultiFun1.setText("�ɹ�");
			troubleshootingbilltitle2.setText("�����ɹ�");
			Fun1Name = "InsertDispatch()";			
			break;
		case 7://�������
			btnMultiFun1.setVisibility(View.VISIBLE);
			btnMultiFun1.setVisibility(View.GONE);
			troubleshootingbilltitle2.setText("���������");
			break;
		case 8://����δȷ��
			//�����ǰ�û�Ϊ�����ˣ���ô����ȷ�ϣ������ر�
			if (ReportID.equals(UserID)){
				//������ť��[ȷ��]���ر���
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("ȷ��");
				RBRepairJudge.setIsIndicator(false);
				Fun1Name = "TSAudit(7)";
			}else if (ReceriverID.equals(UserID)){
				//һ����ť�����ӵ���
				spFinRepairName.setEnabled(true);
				getFinRepairName();//��ȡ��ά���ˡ��Լ����Ը��Լ�����
				btnMultiFun1.setVisibility(View.VISIBLE);
				btnMultiFun1.setText("�˵�");
				btnMultiFun2.setVisibility(View.VISIBLE);
				btnMultiFun2.setText("����");
				troubleshootingbilltitle2.setText("���޲�ȷ��");
				Fun1Name = "RefuseBill()";//���������û��д
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
 * ���ҵİ�ťҪִ�еĹ���
 * @param Fun2Name Ҫִ�еĺ�����
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
	 * �ұߵ����Ҫִ�еĹ���
	 * @param Fun1Name Ҫִ�еĺ�����
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
		//�ر�
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
	 * �ر�
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
				// �����û�������ʾ��¼��ȷ�������ʾ�û����������
				if (Result=="OK") {
					//��resultͨ��json����									
				} 
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
			}
		});			
		this.finish();	
	}
	// ʵ����
	private void initview() {
		// TODO Auto-generated method stub
		pbImageUpload = (ProgressBar) findViewById(R.id.pb_ImageUpload);//�ϴ����ȶԻ���
		ivTroubleImage = (ImageView) this.findViewById(R.id.iv_trouble_image);
		fb = FinalBitmap.create(this);//��ʼ��FinalBitmapģ��
		sv = (ScrollView) this.findViewById(R.id.sv_Tsb);
		sv.smoothScrollTo(0, 0);
		btnBack=(Button) this.findViewById(R.id.button_back);			
		username = (TextView) this.findViewById(R.id.tvUserName);
		shootingdate = (TextView) this.findViewById(R.id.tvShootingDate);
		tvRepairDeptName = (TextView) this.findViewById(R.id.tvRepairDeptName);

	
		RBRepairJudge = (RatingBar) this.findViewById(R.id.rB_TSB);//ά�����۽�����
		RBRepairJudge.setStepSize(1);//���ò���Ϊ1
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
		//btnMultiFun1.setText("����");
		btnMultiFun2=(Button) this.findViewById(R.id.buttoin_multi_function_2);
		btnMultiFun2.setVisibility(View.INVISIBLE);
		btnMultiFun1.setVisibility(View.INVISIBLE);

		btncodescan = (Button) this.findViewById(R.id.btn_CodeScan);
		btnImageUpload = (Button) this.findViewById(R.id.btn_ImpageUpload);
		//����һ�������齨
		troubleshootingbilltitle1=(TextView) this.findViewById(R.id.TroubleShootingbill_title1);
		troubleshootingbilltitle2=(TextView) this.findViewById(R.id.TroubleShootingbill_title2);
		
		LinearLayoutApplyRemark = (LinearLayout) this.findViewById(R.id.ll_tsb_apply_remark);
		
		//������listview
		lvchangelog = (ListView) this.findViewById(R.id.lvChangeLog);
		//��ȡȫ�ֲ���
		sp = this.getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);
		
		// ��ʼ��ajax����
		http = new FinalHttp();
		params = new AjaxParams();	
		strURL = sp.getString("Server_URL", "");		
		//�û���¼��
		strLogonID = sp.getString("USER_LoginID", "");
		//�û���¼��
		strUserID = sp.getString("USER_ID", "");
		//�û�����������
		strUserName = sp.getString("USER_NAME", "");
		//�������ĵ�ַֻ������Ŀ¼�ġ�
		strURL = sp.getString("Server_URL", "");	
		
		
		//��ʼ����������spinner
		datalist_TroubleType = new ArrayList<Map<String, Object>>();
		simp_adapter_TroubleType = new SimpleAdapter(this, datalist_TroubleType, R.layout.activity_troubleshootingbill_spinner,
				new String[] { "spinner_item_image", "spinner_item_id", "spinner_item_name" },
				new int[] { R.id.spinner_item_image, R.id.spinner_item_id, R.id.spinner_item_name });
		sptroubletype.setAdapter(simp_adapter_TroubleType);		
		sptroubletype.setSelection(0,false);
		
		//��ʼ����������spinner
		datalist_TroubleArea = new ArrayList<Map<String, Object>>();
		simp_adapter_TroubleArea = new SimpleAdapter(this, datalist_TroubleArea, R.layout.activity_troubleshootingbill_spinner,
				new String[] { "spinner_item_image", "spinner_item_id", "spinner_item_name" },
				new int[] { R.id.spinner_item_image, R.id.spinner_item_id, R.id.spinner_item_name });
		sptroublearea.setAdapter(simp_adapter_TroubleArea);		
		sptroublearea.setSelection(0,false);
		
		//��ʼ��ά����spinner
		datalist_FinRepairName = new ArrayList<Map<String, Object>>();
		simp_adapter_FinRepairName = new SimpleAdapter(this, datalist_FinRepairName, R.layout.activity_troubleshootingbill_spinner,
				new String[] { "spinner_item_image", "spinner_item_id", "spinner_item_name" },
				new int[] { R.id.spinner_item_image, R.id.spinner_item_id, R.id.spinner_item_name });
		spFinRepairName.setAdapter(simp_adapter_FinRepairName);		
		spFinRepairName.setSelection(0,false);
		
		//��ʼ��listview
		datalist_listview = new ArrayList<Map<String,Object>>();
		simp_adapter_listview = new SimpleAdapter(getApplication(), datalist_listview, R.layout.activity_troubleshootingbill_workflow_item, new String[]{"workflow_item_pic","workflow_item_OptName","workflow_item_ChangeDate","workflow_item_ChangeType","workflow_item_ChangeRemark"}, new int[]{R.id.id_workflow_item_pic,R.id.id_workflow_item_OptName,R.id.id_workflow_item_ChangeDate,R.id.id_workflow_item_ChangeType,R.id.id_workflow_item_ChangeRemark});
		lvchangelog.setAdapter(simp_adapter_listview);	
		
		

	}

//���ά�޲���
private void getFinRepairName() {
	// TODO Auto-generated method stub	
//	if (troubleshootingbillflowstatus==1)
//	{
//		return;
//	}
//	
	// �������ַ���Ҫ��sharepre�л�ȡ��
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
			// �����û�������ʾ��¼��ȷ�������ʾ�û����������
			datalist_FinRepairName.clear();
			try {
				//��JSON�ַ�������ʼ��һ��JSON����
			    JSONObject jsonObject = new JSONObject(Result);
			    //Ȼ���ȡresult���������([]���������)���������������ʼ��һ��JSONArray����
			    JSONArray  aNews =  new JSONArray( jsonObject.getString("FinRepairName") );
			    for(int i=0; i< aNews.length(); i++)
			    {//				    	
			    	//��ʼ������λ�� �Լ����ܸ��Լ���
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
		                setTitle("��ʾ").   
		                setMessage("��ǰά�޲���û�п��õ�ά����Ա������ϵ����Ա").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
//				Toast.makeText(getApplication(), "��ǰά�޲���û�п��õ�ά����Ա������ϵ����Ա", 0).show();
			}			
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("��ʾ").   
	                setMessage("��������������").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
		}
	});
	
}

	//���ά�޲���
	private void getRepairDept() {
		// TODO Auto-generated method stub	
			if (strTroubleAreaID.length()>0 & strTroubleTypeID.length()>0)
			{
		// ����ʱ����������м��ܴ�����������Ҳ���м��ܴ���
			AjaxParams params = new AjaxParams();
			String strURL = sp.getString("Server_URL", "");			
			// �������ַ���Ҫ��sharepre�л�ȡ��
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
					// �����û�������ʾ��¼��ȷ�������ʾ�û����������
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
//						Toast.makeText(getApplication(), "��ǰѡ�������������û����Ӧ��ά�޲��ţ�����ϵ����Ա", 0).show();
						Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
				                setTitle("��ʾ").   
				                setMessage("��ǰѡ�������������û����Ӧ��ά�޲��ţ�����ϵ����Ա").   
				                setIcon(R.drawable.ic_launcher).   
				                create();   
				        alertDialog.show(); 
					}
				}
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
//					Toast.makeText(getApplication(), "�������û�в鵽��Ӧ��ά�޲��ţ�����ϵ����Ա", 0).show();
					Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
			                setTitle("��ʾ").   
			                setMessage("��������������").   
			                setIcon(R.drawable.ic_launcher).   
			                create();   
			        alertDialog.show(); 
				}
			});
			}
	}

	//ajax��ȡ�������򣬲����
	private void getTroubleArea() {
	
		// ����ʱ����������м��ܴ�����������Ҳ���м��ܴ���				
		// �������ַ���Ҫ��sharepre�л�ȡ��
		String URL = strURL + "/ws/serunit.asmx/GeTroubleArea";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
				// �����û�������ʾ��¼��ȷ�������ʾ�û����������
				if (Result.length() > 0) {
					//��resultͨ��json����
					datalist_TroubleArea.clear();
					try {
						//��JSON�ַ�������ʼ��һ��JSON����
					    JSONObject jsonObject = new JSONObject(Result);
					    //Ȼ���ȡresult���������([]���������)���������������ʼ��һ��JSONArray����
					    JSONArray  aNews =  new JSONArray( jsonObject.getString("TroubleArea") );
					    for(int i=0; i< aNews.length(); i++)
					    {
	//						    	
					    	//��ʼ������λ��
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
		                setTitle("��ʾ").   
		                setMessage("��������������").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
			}
		});

	}		
	//ajax��ȡ�������򣬲����
	private void getTroubleType() {				
		// �������ַ���Ҫ��sharepre�л�ȡ��
		String URL = strURL + "/ws/serunit.asmx/GeTroubleType";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
				// �����û�������ʾ��¼��ȷ�������ʾ�û����������
				if (Result.length() > 0) {
					//��resultͨ��json����
			    	datalist_TroubleType.clear();
					try {
						//��JSON�ַ�������ʼ��һ��JSON����
					    JSONObject jsonObject = new JSONObject(Result);
					    //Ȼ���ȡresult���������([]���������)���������������ʼ��һ��JSONArray����
					    JSONArray  aNews =  new JSONArray( jsonObject.getString("TroubleType") );
					    for(int i=0; i< aNews.length(); i++)
					    {
					    	//��ʼ����������

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
		                setTitle("��ʾ").   
		                setMessage("��������������").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
			}
		});

	}	
/**
 * ���ɱ��޵���
 */
	private void genbillid() {
		// TODO Auto-generated method stub					
	// �������ַ���Ҫ��sharepre�л�ȡ��
	String URL = strURL + "/ws/serunit.asmx/GeBillID";
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// �����û�������ʾ��¼��ȷ�������ʾ�û����������
			if (Result.length() > 0) {
				//��resultͨ��json����
				shootingbillid.setText(Result);						
			} 
		}
		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("��ʾ").   
	                setMessage("��������������").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
		}
	});	
			
	}
	/**
	 * ��ȡ��ǰʱ��
	 */
	private String getcurtime() {
		// TODO Auto-generated method stub
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}

	// ����
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
	// ����ǰҪ�ȶ����������Ŀ������֤
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
	case R.id.btn_ImpageUpload://�ϴ�ͼƬ����
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
	case SCANNIN_GREQUEST_CODE://ɨ���ά����� 1
		if(resultCode == RESULT_OK){
			Bundle bundle = data.getExtras();
			//��ʾɨ�赽������			
			strURL = bundle.getString("result");
		}
	case TO_SELECT_PHOTO://ѡ��ͼ����� 3
		Log.i(TAG, "����ѡ���ͼƬ1=");
		Log.e(TAG, Integer.toString(resultCode));
		if(resultCode==Activity.RESULT_OK)
		{
			picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			Log.i(TAG, "����ѡ���ͼƬ="+picPath);
			ivTroubleImage.setImageBitmap(new ImageDispose().getImageThumbnail(picPath,50,50));
			ImageUpload();
		}
		break;
	}
}

/**
 * ͼƬ�ϴ�
 * @throws IOException 
 */
private void ImageUpload()  {
	pd = new ProgressDialog(TroubleShootingBillActivity.this);  
	pd.setTitle("");
	pd.setMessage("ͼƬ�ϴ���");
	pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	pd.show();
	// TODO Auto-generated method stub
	// �������ַ���Ҫ��sharepre�л�ȡ��
	AjaxParams params = new AjaxParams();
	Log.e("image", picPath);
	URL = strURL + "/ws/serunit.asmx/UploadImage";
	params.put("BillID", RequestID);		

	//String uploadBuffer1 = new String(Base64.encode(new ImageDispose().getBytesFromFile(new File(picPath)),Base64.DEFAULT));  //����Base64����  
	
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
	                setTitle("��ʾ").   
	                setMessage("ͼƬ�ϴ��ɹ�").   
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

//ȷ��
private void TSAudit(String typeid) {	
	// �������ַ���Ҫ��sharepre�л�ȡ��
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
		pd.setMessage("���ݱ�����");
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	@Override
	public void onSuccess(String t) {
		super.onSuccess(t);
		// TODO Auto-generated method stub
		String Result = t.replaceAll("(<[^>]*>)", "").trim();
		// �����û�������ʾ��¼��ȷ�������ʾ�û����������
		if (Result.equals("OK")) {
			//��resultͨ��json����	
			TroubleShootingBillActivity.this.finish();	
		}else
		{
			Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
	                setTitle("��ʾ").   
	                setMessage("�ύʧ�ܣ�����ϵ����Ա").   
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
                setTitle("��ʾ").   
                setMessage("��������������").   
                setIcon(R.drawable.ic_launcher).   
                create();   
        alertDialog.show(); 
        pd.dismiss();
	}
});			


}
//�ӵ�
private void ReceiveBill() {	
	// �������ַ���Ҫ��sharepre�л�ȡ��				
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
			pd.setMessage("���ݱ�����");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// �����û�������ʾ��¼��ȷ�������ʾ�û����������
			if (Result.equals("OK")) {
				//��resultͨ��json����
				TroubleShootingBillActivity.this.finish();					
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("��ʾ").   
		                setMessage("�ύʧ�ܣ�����ϵ����Ա").   
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
 * �˵�
 */
	private void RefuseBill() {	
		// �������ַ���Ҫ��sharepre�л�ȡ��
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
			pd.setMessage("���ݱ�����");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// �����û�������ʾ��¼��ȷ�������ʾ�û����������
			if (Result.equals("OK")) {
				//��resultͨ��json����		
				TroubleShootingBillActivity.this.finish();		
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("��ʾ").   
		                setMessage("�ύʧ�ܣ�����ϵ����Ա").   
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
	                setTitle("��ʾ").   
	                setMessage("��������������").   
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
			pd.setMessage("���ݱ�����");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// �����û�������ʾ��¼��ȷ�������ʾ�û����������
			if (Result.equals("OK")) {
				//��resultͨ��json����		
				TroubleShootingBillActivity.this.finish();		
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("��ʾ").   
		                setMessage("�ύʧ�ܣ�����ϵ����Ա").   
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
	                setTitle("��ʾ").   
	                setMessage("��������������").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 	
			pd.dismiss();
		}
	});			
	}
//����ά����־��
	private void InsertRepairLog(String TypeID) {
	// TODO Auto-generated method stub
		// TODO Auto-generated method stub
	// ����ʱ����������м��ܴ�����������Ҳ���м��ܴ���							
	params.put("BillID", shootingbillid.getText().toString());	
	params.put("ReporterID", strReporterID);
	params.put("RepairDeptID", strRepairDeptID);
	params.put("TypeID", TypeID);	
	params.put("DispatchID", strDispatchID);	
	params.put("Opter", strUserName);	
	params.put("CheckRemark", checkdetail.getText().toString().trim());	
	// �������ַ���Ҫ��sharepre�л�ȡ��
	String URL = strURL + "/ws/serunit.asmx/InsertRepairLog";
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			pd = new ProgressDialog(TroubleShootingBillActivity.this);  
			pd.setTitle("");
			pd.setMessage("���ݱ�����");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// �����û�������ʾ��¼��ȷ�������ʾ�û����������
			if (Result.equals("OK")) {
				//��resultͨ��json����
				TroubleShootingBillActivity.this.finish();		
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("��ʾ").   
		                setMessage("�ύʧ�ܣ�����ϵ����Ա").   
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
	                setTitle("��ʾ").   
	                setMessage("��������������").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 	
	    	pd.dismiss();
		}
	});
		
		
}


//�������޵�
private void insertBill() {
	// TODO Auto-generated method stub
	
	// ����ʱ����������м��ܴ�����������Ҳ���м��ܴ���
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
	// �������ַ���Ҫ��sharepre�л�ȡ��
	String URL = strURL + "/ws/serunit.asmx/InsertTSB";
	
	http.post(URL, params, new AjaxCallBack<String>() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			pd = new ProgressDialog(TroubleShootingBillActivity.this);  
			pd.setTitle("");
			pd.setMessage("���ݱ�����");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.show();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			// TODO Auto-generated method stub
			String Result = t.replaceAll("(<[^>]*>)", "").trim();
			// �����û�������ʾ��¼��ȷ�������ʾ�û����������
			if (Result.equals("OK")) {
				//��resultͨ��json����
				TroubleShootingBillActivity.this.finish();		
			} else
			{
				Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
		                setTitle("��ʾ").   
		                setMessage("�ύʧ�ܣ�����ϵ����Ա").   
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
	                setTitle("��ʾ").   
	                setMessage("��������������").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show(); 
			pd.dismiss();
		}
	});
}
// �ڱ���ǰ��������֤���������Ƿ�Ϊ��
private boolean ValidateInput() {
	// TODO Auto-generated method stub
	
	if (tvRepairDeptName.getText().toString().trim().length() == 0) {
		Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
                setTitle("��ʾ").   
                setMessage("ά�޲��Ų���Ϊ�գ�������ѡ���������ͣ�������ϵ����Ա").   
                setIcon(R.drawable.ic_launcher).   
                create();   
        alertDialog.show(); 
		return false;
	}
	return true;

}

/**
 * ���ݱ��޵��Ż�ȡ����������Ϣ
 * @param BillID
 */
private void getWorkFlowByBillID(String BillID) {
	
			
params.put("BillID", BillID);		
// �������ַ���Ҫ��sharepre�л�ȡ��
String URL = strURL + "/ws/serunit.asmx/GeWorkFlowByBillID";
http.post(URL, params, new AjaxCallBack<String>() {
	@Override
	public void onSuccess(String t) {
		super.onSuccess(t);
		// TODO Auto-generated method stub
		String Result = t.replaceAll("(<[^>]*>)", "").trim();
		// �����û�������ʾ��¼��ȷ�������ʾ�û����������
		if (Result.length() > 0) {
			//��resultͨ��json����
			getworkflowdatajson(Result);	
			
		} 
	}
	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
		super.onFailure(t, errorNo, strMsg);
		Dialog alertDialog = new AlertDialog.Builder(TroubleShootingBillActivity.this).   
                setTitle("��ʾ").   
                setMessage("��������������").   
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
		//��JSON�ַ�������ʼ��һ��JSON����
	    JSONObject jsonObject = new JSONObject(strJson);
	    //Ȼ���ȡresult���������([]���������)���������������ʼ��һ��JSONArray����
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
  //�����ı�����
           return ((String) object1.get("workflow_item_ChangeDate")).compareTo((String) object2.get("workflow_item_ChangeDate"));

      }    

     });    

}

return list;

}
		
}
