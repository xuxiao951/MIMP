package com.mimp.android.ch0;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import com.baidu.frontia.FrontiaApplication;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;

public class App extends FrontiaApplication {

	private static Context _context;

	@Override
	public void onCreate() {
		super.onCreate();
		_context = getApplicationContext();
		initEMChat();
		EMChat.getInstance().init(_context);
		EMChat.getInstance().setDebugMode(true);
		EMChat.getInstance().setAutoLogin(true);
		EMChatManager.getInstance().getChatOptions().setUseRoster(true);
		FrontiaApplication.initFrontiaApplication(this);
		// CrashHandler crashHandler = CrashHandler.getInstance();// ȫ���쳣��׽
		// crashHandler.init(_context);
	}

	private void initEMChat() {
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		if (processAppName == null
				|| !processAppName.equalsIgnoreCase("com.juns.wechat")) {
			return;
		}
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// ��ȡ��EMChatOptions����
		// �����Զ����������ʾ
		options.setNotifyText(new OnMessageNotifyListener() {

			@Override
			public String onNewMessageNotify(EMMessage message) {
				return "��ĺ��ѷ�����һ����ϢŶ";
			}

			@Override
			public String onLatestMessageNotify(EMMessage message,
					int fromUsersNum, int messageNum) {
				return fromUsersNum + "�����ѣ�������" + messageNum + "����Ϣ";
			}

			@Override
			public String onSetNotificationTitle(EMMessage arg0) {
				return null;
			}

			@Override
			public int onSetSmallIcon(EMMessage arg0) {
				return 0;
			}
		});
		options.setOnNotificationClickListener(new OnNotificationClickListener() {

			@Override
			public Intent onNotificationClick(EMMessage message) {
				Intent intent = new Intent(_context, MainActivity.class);
				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { // ������Ϣ
					intent.putExtra("userId", message.getFrom());
//					intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
				} else { // Ⱥ����Ϣ
					// message.getTo()ΪȺ��id
					intent.putExtra("groupId", message.getTo());
//					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
				}
				return intent;
			}
		});
		// IntentFilter callFilter = new
		// IntentFilter(EMChatManager.getInstance()
		// .getIncomingCallBroadcastAction());
		// registerReceiver(new CallReceiver(), callFilter);
	}

	private class CallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ����username
			String from = intent.getStringExtra("from");
			// call type
			String type = intent.getStringExtra("type");
//			startActivity(new Intent(_context, VoiceCallActivity.class)
//					.putExtra("username", from).putExtra("isComingCall", true));
		}
	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(info.processName,
									PackageManager.GET_META_DATA));
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
			}
		}
		return processName;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		try {
			deleteCacheDirFile(getHJYCacheDir(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public static Context getInstance() {
		return _context;
	}

	// ����list��������ÿһ��activity�ǹؼ�
	private List<Activity> mList = new LinkedList<Activity>();
	private static App instance;

	// ���췽��
	// ʵ����һ��
	public synchronized static App getInstance2() {
		if (null == instance) {
			instance = new App();
		}
		return instance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	// �ر�ÿһ��list�ڵ�activity
	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public static String getHJYCacheDir() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return Environment.getExternalStorageDirectory().toString()
					+ "/Health/Cache";
		else
			return "/System/com.juns.Walk/Walk/Cache";
	}

	public static String getHJYDownLoadDir() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return Environment.getExternalStorageDirectory().toString()
					+ "/Walk/Download";
		else {
			return "/System/com.Juns.Walk/Walk/Download";
		}
	}

	public static void deleteCacheDirFile(String filePath,
			boolean deleteThisPath) throws IOException {
		if (!TextUtils.isEmpty(filePath)) {
			File file = new File(filePath);
			if (file.isDirectory()) {// ����Ŀ¼
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteCacheDirFile(files[i].getAbsolutePath(), true);
				}
			}
			if (deleteThisPath) {
				if (!file.isDirectory()) {// ������ļ���ɾ��
					file.delete();
				} else {// Ŀ¼
					if (file.listFiles().length == 0) {// Ŀ¼��û���ļ�����Ŀ¼��ɾ��
						file.delete();
					}
				}
			}
		}
	}
}

