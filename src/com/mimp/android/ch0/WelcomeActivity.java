package com.mimp.android.ch0;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import com.mimp.android.po.DownLoadManager;
import com.mimp.android.po.UpdateInfo;
import com.mimp.android.po.UpdateInfoParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class WelcomeActivity extends Activity {
	// http链接

	
	private static final int GOTO_MAIN_ACTIVITY = 0;
	private SharedPreferences sp = null;
	private String strURL = "";
	private final String TAG = this.getClass().getName();
	private final int UPDATA_NONEED = 0;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int SDCARD_NOMOUNTED = 3;
	private final int DOWN_ERROR = 4;
	private UpdateInfo info;
	private String localVersion;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		InitView();
		if (sp.getString("Server_URL", "").trim().length() > 0) {
			
			strURL = sp.getString("Server_URL", "").trim();

		}
		
		try {
			localVersion = getVersionName();
			Log.e(TAG, localVersion);
			CheckVersionTask cv = new CheckVersionTask();
			new Thread(cv).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 30);//30毫秒
	}	
		private void InitView() {
	// TODO Auto-generated method stub
		sp = this.getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);
		
}

private String getVersionName() throws Exception {
	//getPackageName()是你当前类的包名，0代表是获取版本信息  
	PackageManager packageManager = getPackageManager();
	PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
			0);
	return packInfo.versionName;
}
public class CheckVersionTask implements Runnable {
	InputStream is;
	public void run() {
		try {
			String path = strURL+getResources().getString(R.string.AppVersionURL);
			Log.e(TAG, path);
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET"); 
            int responseCode = conn.getResponseCode(); 
            if (responseCode == 200) { 
                // 从服务器获得一个输入流 
            	is = conn.getInputStream(); 
            } 
			info = UpdateInfoParser.getUpdataInfo(is);
			if (info.getVersion().equals(localVersion)) {
				Log.i(TAG, "版本号相同");
				Message msg = new Message();
				msg.what = UPDATA_NONEED;
				handler.sendMessage(msg);
				// LoginMain();
			} else {
				Log.i(TAG, "版本号不相同 ");
				Message msg = new Message();
				msg.what = UPDATA_CLIENT;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			Message msg = new Message();
			msg.what = GET_UNDATAINFO_ERROR;
			handler.sendMessage(msg);
			e.printStackTrace();
		}
	}
}
Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch (msg.what) {
		case UPDATA_NONEED:
			LoginMain();
		case UPDATA_CLIENT:
			 //对话框通知用户升级程序   
			showUpdataDialog();
			break;
		case GET_UNDATAINFO_ERROR:
			//服务器超时   
//            Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", 1).show(); 
            LoginMain();
			break;
		case DOWN_ERROR:
			//下载apk失败  
            Toast.makeText(getApplicationContext(), "下载新版本失败", 1).show(); 
            LoginMain();
			break;
		}
	}
};
/* 
 *  
 * 弹出对话框通知用户更新程序  
 *  
 * 弹出对话框的步骤： 
 *  1.创建alertDialog的builder.   
 *  2.要给builder设置属性, 对话框的内容,样式,按钮 
 *  3.通过builder 创建一个对话框 
 *  4.对话框show()出来   
 */  
protected void showUpdataDialog() {
	AlertDialog.Builder builer = new Builder(this);
	builer.setTitle("版本升级");
	builer.setMessage(info.getDescription());
	 //当点确定按钮时从服务器上下载 新的apk 然后安装   ?
	builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			Log.i(TAG, "下载apk,更新");
			downLoadApk();
		}
	});
	builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			LoginMain();	
		}
	});
	AlertDialog dialog = builer.create();
	dialog.show();
}
/* 
 * 从服务器中下载APK 
 */  
protected void downLoadApk() {  
    final ProgressDialog pd;    //进度条对话框  
    pd = new  ProgressDialog(this);  
    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
    pd.setMessage("正在下载更新");  
    pd.show();  
    new Thread(){  
        @Override  
        public void run() {  
            try {  
                File file = DownLoadManager.getFileFromServer(info.getUrl(), pd);  
                sleep(3000);  
                installApk(file);  
                pd.dismiss(); //结束掉进度条对话框  
            } catch (Exception e) {  
                Message msg = new Message();  
                msg.what = DOWN_ERROR;  
                handler.sendMessage(msg);  
                e.printStackTrace();  
            }  
        }}.start();  
}  
  
//安装apk   
protected void installApk(File file) {  
    Intent intent = new Intent();  
    //执行动作  
    intent.setAction(Intent.ACTION_VIEW);  
    //执行的数据类型  
    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");  
    startActivity(intent);  
}  
/*  
 * 进入程序的主界面  
 */    
private void LoginMain(){    
    Intent intent = new Intent();
    intent.setClass(this,LoginActivity.class);
    startActivity(intent);
    finish();			
}  	
}
