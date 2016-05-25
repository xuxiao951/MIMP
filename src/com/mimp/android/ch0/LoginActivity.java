package com.mimp.android.ch0;

import com.mimp.android.po.EncryptMD5;
import com.mimp.android.po.PhoneInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.igexin.sdk.PushManager;
public class LoginActivity extends Activity  implements OnClickListener{
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private LinearLayout mLoginLinearLayout; // ��¼���ݵ�����
	private Animation mTranslate; // λ�ƶ���
	private Dialog mLoginingDlg; // ��ʾ���ڵ�¼��Dialog
	
	private TextView tv_Server;
	private Button btnLogin;
	
	private SharedPreferences sp = null;
	
	 
	private EditText etUserID = null;
	private EditText etPassword = null;
	
	private CheckBox cbRemPassword = null;
	private CheckBox cbAutoLogin = null;
	// http����
	private FinalHttp http = null;
	private AjaxParams params = null;
	private String strURL = "";
	
	private Handler handler;

	private int ValidateType = 0;//��֤���͡�0Ϊ����֤��λ��1Ϊͬʱ��֤�û�
	
	private String strUserLoginID = "";
	private String strPassword = "";

	private static final int MSG_SUCCESS = 0;//��ȡͼƬ�ɹ��ı�ʶ  
    private static final int MSG_FAILURE = 1;//��ȡͼƬʧ�ܵı�ʶ  
    

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//��ʼ��
		this.init();
		//����
		this.listener();

		mLoginLinearLayout.startAnimation(mTranslate); // Y��ˮƽ�ƶ�
//		// ����ֻ��Ƿ���������
//		boolean networkState = NetworkDetector.detect(LoginActivity.this);
//		if (!networkState) {
//			Toast.makeText(getApplication(), "�������ӹ��ϣ������ֻ�����", 0).show();
//		}
		
		// ������ס�����ѡ��ť�¼�
		cbRemPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (cbRemPassword.isChecked()) {

					sp.edit().putBoolean("ISCHECK", true).commit();

				} else {

					sp.edit().putBoolean("ISCHECK", false).commit();

				}

			}
		});

		// �����Զ���¼��ѡ���¼�
		cbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (cbAutoLogin.isChecked()) {
					sp.edit().putBoolean("AUTO_ISCHECK", true).commit();

				} else {
					// System.out.println("�Զ���¼û��ѡ��");
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}
			}
		});
		
		if (sp.getString("Server_URL", "").trim().length() > 0) {
		
			strURL = sp.getString("Server_URL", "").trim();

		}else
		{
			tv_Server.setText("�����˴�ɨ��������Ķ�ά��");
		}
			
		
		if (sp.getString("Unit_Name", "").trim().length()>0){
			tv_Server.setText(sp.getString("Unit_Name", "").trim());
		}
		
		if (sp.getBoolean("ISCHECK", false)) {
			// ����Ĭ���Ǽ�¼����״̬
			cbRemPassword.setChecked(true);
			etUserID.setText(sp.getString("USER_LoginID", ""));
			etPassword.setText(sp.getString("PASSWORD", ""));
			// �ж��Զ���½��ѡ��״̬
			if (sp.getBoolean("AUTO_ISCHECK", false)) {
				// ����Ĭ�����Զ���¼״̬
				cbAutoLogin.setChecked(true);
				// ��֤�û��������롣����Ϊ�Զ���¼��ʱ�򣬲���֤��������ֱ�ӽ�����֤�û��������롣
				ValidateType = 1;	
			}			
		}
		GetUnitName();
		
	
		
	}

	private void listener() {
		// TODO Auto-generated method stub
		btnLogin.setOnClickListener(this);
		tv_Server.setOnClickListener(this);
	}

	private void init() {
		// TODO Auto-generated method stub
		//��ʼ����������
		PushManager.getInstance().initialize(this.getApplicationContext());
		//��ȡ�绰��Ϣ
		PhoneInfo siminfo = new PhoneInfo(LoginActivity.this);  
		
		
		
		ValidateType = 0;
		tv_Server = (TextView) this.findViewById(R.id.login_tv_server);
		btnLogin = (Button) this.findViewById(R.id.login_btnLogin);
		
		// ��ʼ��ajax����
		http = new FinalHttp();
		params = new AjaxParams();
		
		etUserID = (EditText) this.findViewById(R.id.et_UserID);
		etPassword = (EditText) this.findViewById(R.id.et_Password);
		
		cbRemPassword = (CheckBox) findViewById(com.mimp.android.ch0.R.id.cbRemPassword);
		cbAutoLogin = (CheckBox) findViewById(R.id.cbAutoLogin);
		
		// ��ʼ��SharedPreferences.��������Ӧ�ó����һЩƫ������
		sp = this.getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);
		
		mLoginLinearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
		mTranslate = AnimationUtils.loadAnimation(this, R.anim.my_translate); // ��ʼ����������
		initLoginingDlg();

	}


	/* ��ʼ�����ڵ�¼�Ի��� */
	private void initLoginingDlg() {
		mLoginingDlg = new Dialog(this, R.style.loginingDlg );
		mLoginingDlg.setContentView(R.layout.logining_dlg);

		Window window = mLoginingDlg.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		// ��ȡ��mLoginingDlg�����ĵ�ǰ���ڵ����ԣ��Ӷ�����������Ļ����ʾ��λ��

		// ��ȡ��Ļ�ĸ߿�
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int cxScreen = dm.widthPixels;
		int cyScreen = dm.heightPixels;

		int height = (int) getResources().getDimension(
				R.dimen.loginingdlg_height);// ��42dp
		int lrMargin = (int) getResources().getDimension(
				R.dimen.loginingdlg_lr_margin); // ���ұ���10dp
		int topMargin = (int) getResources().getDimension(
				R.dimen.loginingdlg_top_margin); // ����20dp

		params.y = (-(cyScreen - height) / 2) + topMargin; // -199
		/* �Ի���Ĭ��λ������Ļ����,����x,y��ʾ�˿ؼ���"��Ļ����"��ƫ���� */

		params.width = cxScreen;
		params.height = height;
		// width,height��ʾmLoginingDlg��ʵ�ʴ�С

		mLoginingDlg.setCanceledOnTouchOutside(true); // ���õ��Dialog�ⲿ��������ر�Dialog
	}

	/* ��ʾ���ڵ�¼�Ի��� */
	private void showLoginingDlg() {
		if (mLoginingDlg != null)
			mLoginingDlg.show();
	}

	/* �ر����ڵ�¼�Ի��� */
	private void closeLoginingDlg() {
		if (mLoginingDlg != null && mLoginingDlg.isShowing())
			mLoginingDlg.dismiss();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_btnLogin:
			ValidateType = 1;
			GetUnitName();;
			break;
		case R.id.login_tv_server:
			//ɨ��������Ķ�ά��
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this,CaptureActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;
		default:
			break;
		}
	}



	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				//��ʾɨ�赽������
				
				strURL = bundle.getString("result");
				Toast.makeText(getApplication(), strURL, 0).show();
				ValidateType = 0;
				 GetUnitName();

			}
			break;
		}
    }

	// ���ݻ�ȡ���ͻ�ȡ��λ����
	private void GetUnitName() {
//		progressDialog1 = ProgressDialog.show(LoginActivity.this, "","������֤������...", true, false);
		
		
		// TODO Auto-generated method stub
		AjaxParams params = new AjaxParams();		
		String URL = strURL+"/ws/serunit.asmx/UnitName";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				showLoginingDlg(); 
			}
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String unitname = t.replaceAll("(<[^>]*>)", "").trim();
				if (unitname.length()>0)
				{					
					if (ValidateType == 0){
						tv_Server.setText(unitname);
						sp.edit().putString("Server_URL", strURL).commit();
						sp.edit().putString("Unit_Name", unitname).commit();
					}

					if (ValidateType ==1)
					{
						VerifyUser();
					}
					
				}
//				progressDialog1.dismiss();
				closeLoginingDlg();// �رնԻ���
			}
			// 
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this).   
		                setTitle("��ʾ").   
		                setMessage("�����������������������ѡ�������").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
//				Toast.makeText(getApplication(), "��������ַ����ȷ��������ɨ���ά��", 1).show();
//				tv_Server.setText("����������ȥ�˰���");
//				progressDialog1.dismiss();	
				closeLoginingDlg();// �رնԻ���
			}
			
		});

		}
	// ���ݻ�ȡ���ͻ�ȡ��λ����
	private void VerifyUser() {
		strUserLoginID = etUserID.getText().toString();
		strPassword = etPassword.getText().toString();
		if (strUserLoginID.length()>0 & strPassword.length()>0)
		{
//			progressDialog2 = ProgressDialog.show(LoginActivity.this, "","��¼��.......", true, false);
			// ������¼
		showLoginingDlg(); 
		// TODO Auto-generated method stub
		AjaxParams params = new AjaxParams();
		params.put("UserID", strUserLoginID);
		params.put("Password", EncryptMD5.main(strPassword));
		params.put("ClientID", PushManager.getInstance().getClientid(getApplicationContext()));
		// �������ַ���Ҫ��sharepre�л�ȡ��
		String URL = strURL + "/ws/serunit.asmx/Validate";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
//					System.out.println(Result);
				// �����û�������ʾ��¼��ȷ�������ʾ�û����������
				if (Result.length() > 0) {
//						tvUnitName.setText(Result);
					// ����û��������룬�����д�Ĳ�һ�£���ô����¼�ɹ���Ҫ�����û��������뵽�����С�������������ݿ�
					String a[] = Result.split(",");   	
					Editor editor = sp.edit();
					//��¼����ûʱ����ˣ�����ô�Űɣ��Լ�֪������
					editor.putString("USER_LoginID", strUserLoginID);
					editor.putString("USER_NAME", a[0]);
					editor.putString("USER_ID", a[1]);
					editor.putString("PASSWORD", strPassword);
					editor.commit();						
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					LoginActivity.this.startActivity(intent);
				} else {
//					Toast.makeText(LoginActivity.this, "�û����������������������", Toast.LENGTH_LONG).show();
					Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this).   
			                setTitle("��ʾ").   
			                setMessage("�û������������").   
			                setIcon(R.drawable.ic_launcher).   
			                create();   
			        alertDialog.show(); 
					btnLogin.setVisibility(1);
				}
				// Toast.makeText(getApplication(), "result="+t, 0).show();
//					progressDialog2.dismiss();
				closeLoginingDlg();// �رնԻ���
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
//				Toast.makeText(LoginActivity.this, "�û����������������������", Toast.LENGTH_LONG).show();
//					progressDialog2.dismiss();
				Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this).   
		                setTitle("��ʾ").   
		                setMessage("�û������������").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
				closeLoginingDlg();// �رնԻ���
			}
		});
		}
		}

	/* �˳���Activityʱ����users */
	@Override
	public void onPause() {
		super.onPause();
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
