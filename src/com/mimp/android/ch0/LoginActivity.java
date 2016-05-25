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
	private LinearLayout mLoginLinearLayout; // 登录内容的容器
	private Animation mTranslate; // 位移动画
	private Dialog mLoginingDlg; // 显示正在登录的Dialog
	
	private TextView tv_Server;
	private Button btnLogin;
	
	private SharedPreferences sp = null;
	
	 
	private EditText etUserID = null;
	private EditText etPassword = null;
	
	private CheckBox cbRemPassword = null;
	private CheckBox cbAutoLogin = null;
	// http链接
	private FinalHttp http = null;
	private AjaxParams params = null;
	private String strURL = "";
	
	private Handler handler;

	private int ValidateType = 0;//验证类型。0为至验证单位，1为同时验证用户
	
	private String strUserLoginID = "";
	private String strPassword = "";

	private static final int MSG_SUCCESS = 0;//获取图片成功的标识  
    private static final int MSG_FAILURE = 1;//获取图片失败的标识  
    

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//初始化
		this.init();
		//监听
		this.listener();

		mLoginLinearLayout.startAnimation(mTranslate); // Y轴水平移动
//		// 检查手机是否链接网络
//		boolean networkState = NetworkDetector.detect(LoginActivity.this);
//		if (!networkState) {
//			Toast.makeText(getApplication(), "网络连接故障，请检查手机网络", 0).show();
//		}
		
		// 监听记住密码多选框按钮事件
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

		// 监听自动登录多选框事件
		cbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (cbAutoLogin.isChecked()) {
					sp.edit().putBoolean("AUTO_ISCHECK", true).commit();

				} else {
					// System.out.println("自动登录没有选中");
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}
			}
		});
		
		if (sp.getString("Server_URL", "").trim().length() > 0) {
		
			strURL = sp.getString("Server_URL", "").trim();

		}else
		{
			tv_Server.setText("请点击此处扫描服务器的二维码");
		}
			
		
		if (sp.getString("Unit_Name", "").trim().length()>0){
			tv_Server.setText(sp.getString("Unit_Name", "").trim());
		}
		
		if (sp.getBoolean("ISCHECK", false)) {
			// 设置默认是记录密码状态
			cbRemPassword.setChecked(true);
			etUserID.setText(sp.getString("USER_LoginID", ""));
			etPassword.setText(sp.getString("PASSWORD", ""));
			// 判断自动登陆多选框状态
			if (sp.getBoolean("AUTO_ISCHECK", false)) {
				// 设置默认是自动登录状态
				cbAutoLogin.setChecked(true);
				// 验证用户名与密码。设置为自动登录的时候，不验证服务器，直接进行验证用户名，密码。
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
		//初始化个推推送
		PushManager.getInstance().initialize(this.getApplicationContext());
		//获取电话信息
		PhoneInfo siminfo = new PhoneInfo(LoginActivity.this);  
		
		
		
		ValidateType = 0;
		tv_Server = (TextView) this.findViewById(R.id.login_tv_server);
		btnLogin = (Button) this.findViewById(R.id.login_btnLogin);
		
		// 初始化ajax对象
		http = new FinalHttp();
		params = new AjaxParams();
		
		etUserID = (EditText) this.findViewById(R.id.et_UserID);
		etPassword = (EditText) this.findViewById(R.id.et_Password);
		
		cbRemPassword = (CheckBox) findViewById(com.mimp.android.ch0.R.id.cbRemPassword);
		cbAutoLogin = (CheckBox) findViewById(R.id.cbAutoLogin);
		
		// 初始化SharedPreferences.用来保存应用程序的一些偏好设置
		sp = this.getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);
		
		mLoginLinearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
		mTranslate = AnimationUtils.loadAnimation(this, R.anim.my_translate); // 初始化动画对象
		initLoginingDlg();

	}


	/* 初始化正在登录对话框 */
	private void initLoginingDlg() {
		mLoginingDlg = new Dialog(this, R.style.loginingDlg );
		mLoginingDlg.setContentView(R.layout.logining_dlg);

		Window window = mLoginingDlg.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		// 获取和mLoginingDlg关联的当前窗口的属性，从而设置它在屏幕中显示的位置

		// 获取屏幕的高宽
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int cxScreen = dm.widthPixels;
		int cyScreen = dm.heightPixels;

		int height = (int) getResources().getDimension(
				R.dimen.loginingdlg_height);// 高42dp
		int lrMargin = (int) getResources().getDimension(
				R.dimen.loginingdlg_lr_margin); // 左右边沿10dp
		int topMargin = (int) getResources().getDimension(
				R.dimen.loginingdlg_top_margin); // 上沿20dp

		params.y = (-(cyScreen - height) / 2) + topMargin; // -199
		/* 对话框默认位置在屏幕中心,所以x,y表示此控件到"屏幕中心"的偏移量 */

		params.width = cxScreen;
		params.height = height;
		// width,height表示mLoginingDlg的实际大小

		mLoginingDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
	}

	/* 显示正在登录对话框 */
	private void showLoginingDlg() {
		if (mLoginingDlg != null)
			mLoginingDlg.show();
	}

	/* 关闭正在登录对话框 */
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
			//扫描服务器的二维码
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
				//显示扫描到的内容
				
				strURL = bundle.getString("result");
				Toast.makeText(getApplication(), strURL, 0).show();
				ValidateType = 0;
				 GetUnitName();

			}
			break;
		}
    }

	// 根据获取类型获取单位名称
	private void GetUnitName() {
//		progressDialog1 = ProgressDialog.show(LoginActivity.this, "","正在验证服务器...", true, false);
		
		
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
				closeLoginingDlg();// 关闭对话框
			}
			// 
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this).   
		                setTitle("警示").   
		                setMessage("服务器错误，请检查网络或重新选择服务器").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
//				Toast.makeText(getApplication(), "服务器地址不正确，请重新扫描二维码", 1).show();
//				tv_Server.setText("服务器哪里去了啊？");
//				progressDialog1.dismiss();	
				closeLoginingDlg();// 关闭对话框
			}
			
		});

		}
	// 根据获取类型获取单位名称
	private void VerifyUser() {
		strUserLoginID = etUserID.getText().toString();
		strPassword = etPassword.getText().toString();
		if (strUserLoginID.length()>0 & strPassword.length()>0)
		{
//			progressDialog2 = ProgressDialog.show(LoginActivity.this, "","登录中.......", true, false);
			// 启动登录
		showLoginingDlg(); 
		// TODO Auto-generated method stub
		AjaxParams params = new AjaxParams();
		params.put("UserID", strUserLoginID);
		params.put("Password", EncryptMD5.main(strPassword));
		params.put("ClientID", PushManager.getInstance().getClientid(getApplicationContext()));
		// 服务器字符串要从sharepre中获取。
		String URL = strURL + "/ws/serunit.asmx/Validate";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				// TODO Auto-generated method stub
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
//					System.out.println(Result);
				// 返回用户名，表示登录正确。否则表示用户名密码错误
				if (Result.length() > 0) {
//						tvUnitName.setText(Result);
					// 如果用户名与密码，本地中存的不一致，那么，登录成功后。要保存用户名，密码到本地中。且清楚所有数据库
					String a[] = Result.split(",");   	
					Editor editor = sp.edit();
					//登录名。没时间改了，就这么着吧，自己知道就行
					editor.putString("USER_LoginID", strUserLoginID);
					editor.putString("USER_NAME", a[0]);
					editor.putString("USER_ID", a[1]);
					editor.putString("PASSWORD", strPassword);
					editor.commit();						
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					LoginActivity.this.startActivity(intent);
				} else {
//					Toast.makeText(LoginActivity.this, "用户名、密码错误，请重新输入", Toast.LENGTH_LONG).show();
					Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this).   
			                setTitle("警示").   
			                setMessage("用户名，密码错误").   
			                setIcon(R.drawable.ic_launcher).   
			                create();   
			        alertDialog.show(); 
					btnLogin.setVisibility(1);
				}
				// Toast.makeText(getApplication(), "result="+t, 0).show();
//					progressDialog2.dismiss();
				closeLoginingDlg();// 关闭对话框
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
//				Toast.makeText(LoginActivity.this, "用户名、密码错误，请重新输入", Toast.LENGTH_LONG).show();
//					progressDialog2.dismiss();
				Dialog alertDialog = new AlertDialog.Builder(LoginActivity.this).   
		                setTitle("警示").   
		                setMessage("用户名，密码错误").   
		                setIcon(R.drawable.ic_launcher).   
		                create();   
		        alertDialog.show(); 
				closeLoginingDlg();// 关闭对话框
			}
		});
		}
		}

	/* 退出此Activity时保存users */
	@Override
	public void onPause() {
		super.onPause();
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
