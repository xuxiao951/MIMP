package com.mimp.android.ch0;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.jauker.widget.BadgeView;
import com.mimp.android.common.Utils;
import com.mimp.android.dialog.titlemenu.ActionItem;
import com.mimp.android.dialog.titlemenu.TitlePopup;
import com.mimp.android.dialog.titlemenu.TitlePopup.OnItemOnClickListener;
import com.mimp.android.view.fragment.Fragment_Index;
import com.mimp.android.view.fragment.Fragment_Profile;
import com.mimp.android.view.fragment.Fragment_Task;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class MainActivity extends FragmentActivity implements OnClickListener{
	//定义组件
	private ImageView img_right;
	//0定义组件
	private final static int TSB_GREQUEST_CODE = 1;
	
	private ViewPager mViewPager;
	//适配器
	private FragmentPagerAdapter mAdapter;
    //数据源
	private List<Fragment> mFragments;
	private SharedPreferences sp = null;
	private LinearLayout mTab01;
	private LinearLayout mTab02;
	private LinearLayout mTab03;
	private LinearLayout mTab04;
	private TitlePopup titlePopup;
	
	private ImageButton mImgTab01;
	private ImageButton mImgTab02;
	private ImageButton mImgTab03;
	private ImageButton mImgTab04;

	//主界面上边的标题
	private TextView tvMaintitle=null;
	
	private Intent intent=null;

	private BadgeView badgeviewTab01=null;
	private BadgeView badgeviewTab02=null;
	// http链接
	private FinalHttp http = null;
	private AjaxParams params = null;
	//待处理的单据的数量
	private String taskNum = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	     setContentView(R.layout.activity_main);
		//实例化
		initView();
		//监听
		initEvent();
		setSelect(0);
//		setOverflowShowingAlways();
		initPopWindow();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		mTab01.setOnClickListener(this);
		mTab02.setOnClickListener(this);
		mTab03.setOnClickListener(this);
		mTab04.setOnClickListener(this);
		
		
		img_right.setOnClickListener(this);
		
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// TODO Auto-generated method stub

		// 初始化SharedPreferences.用来保存应用程序的一些偏好设置
		sp = this.getSharedPreferences("AppPreferences", Context.MODE_WORLD_READABLE);
		
		tvMaintitle = (TextView) this.findViewById(R.id.txt_title);
//		ivmultifun=(ImageView) this.findViewById(R.id.ivMultiFun);
		
		img_right = (ImageView) findViewById(R.id.img_right);
		
//		img_right.setVisibility(View.VISIBLE);
//		img_right.setImageResource(R.drawable.icon_add);
		
		
		
//		ivmultifun.setImageResource(R.drawable.actionbar_add_icon);
		mViewPager=(ViewPager) this.findViewById(R.id.id_viewpager);		
		
		mTab01=(LinearLayout) this.findViewById(R.id.id_tab_01);
		mTab02=(LinearLayout) this.findViewById(R.id.id_tab_02);
		mTab03=(LinearLayout) this.findViewById(R.id.id_tab_03);
		mTab04=(LinearLayout) this.findViewById(R.id.id_tab_04);

		
		mImgTab01=(ImageButton) this.findViewById(R.id.id_tab_01_img);
		mImgTab02=(ImageButton) this.findViewById(R.id.id_tab_02_img);
		mImgTab03=(ImageButton) this.findViewById(R.id.id_tab_03_img);
		mImgTab04=(ImageButton) this.findViewById(R.id.id_tab_04_img);

		//fragment类。具体tab01 tab02 tab03 tab04的具体含义要根据最终设置。主要是需求分析没有完全做好。
		mFragments=new ArrayList<Fragment>();
		
		Fragment mTabIndex = new Fragment_Index();
		Fragment mTabTask = new Fragment_Task();
		Fragment mTabMs = new MainTab03Fragment();
//		Fragment mTab04 = new MainTab04Fragment();
//		Fragment mTab04 = new Fragment_Profile();
		Fragment mTabProfile = new Fragment_Profile();
		
		mFragments.add( mTabIndex);
		mFragments.add(mTabTask);
		mFragments.add(mTabMs);
		mFragments.add(mTabProfile);
		
		badgeviewTab01 = new com.jauker.widget.BadgeView(this);
		badgeviewTab02 = new com.jauker.widget.BadgeView(this);
		//初始化所有按钮的提醒数字
		//厨师化ajax对象
		http = new FinalHttp();
		params = new AjaxParams();
		
		//绑定需要审批的列表数量
		ButtonBadgeView();		
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mFragments.size();
			}
			
			@Override
			public Fragment getItem(int arg0) {
				// TODO Auto-generated method stub
				return mFragments.get(arg0);
			}
		};
		Log.e("madapter", Integer.toString(mAdapter.getCount()));
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			
			@Override
			public void onPageSelected(int arg0)
			{
				int currentItem = mViewPager.getCurrentItem();
				//Toast.makeText(getApplication(), "to", 0).show();
				setTab(currentItem);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void initPopWindow() {
		// 实例化标题栏弹窗
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		titlePopup.setItemOnClickListener(onitemClick);
		// 给标题栏弹窗添加子类
		titlePopup.addAction(new ActionItem(this, R.string.menu_WantReport,
				R.drawable.icon_menu_group));
		titlePopup.addAction(new ActionItem(this, R.string.menu_AddMsg,
				R.drawable.icon_menu_addfriend));
		titlePopup.addAction(new ActionItem(this, R.string.menu_qrcode,
				R.drawable.icon_menu_sao));
		titlePopup.addAction(new ActionItem(this, R.string.menu_Finish,
				R.drawable.abv));
	}
	private OnItemOnClickListener onitemClick = new OnItemOnClickListener() {

		@Override
		public void onItemClick(ActionItem item, int position) {
			// mLoadingDialog.show();
			switch (position) {
			case 0:// 发起群聊
//				Utils.start_Activity(MainActivity.this,TroubleShootingBillActivity.class);
				intent = new Intent();
				intent.putExtra("BillId","");
			    intent.setClass(getApplication(),TroubleShootingBillActivity.class );
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, TSB_GREQUEST_CODE);
				break;
			case 1:// 添加朋友
//				Utils.start_Activity(MainActivity.this, TroubleShootingBillActivity.class,
//						new BasicNameValuePair(Constants.NAME, "添加朋友"));
				break;
			case 2:// 扫一扫
				Utils.start_Activity(MainActivity.this, CaptureActivity.class);
				break;
			case 3:// 收钱
//				Utils.start_Activity(MainActivity.this, TroubleShootingBillActivity.class);
				break;
			default:
				break;
			}
		}
	};
	public void ButtonBadgeView() {
		// TODO Auto-generated method stub
		getTaskNum();
	}

	private void  getTaskNum() {
		// TODO Auto-generated method stub
		
		
		AjaxParams params = new AjaxParams();
		params.put("LogonID", sp.getString("USER_LoginID", ""));
		params.put("UserID", sp.getString("USER_ID", ""));
		// 服务器字符串要从sharepre中获取。
		String URL = sp.getString("Server_URL", "").trim() + "/ws/serunit.asmx/GetTaskNum";
		http.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				String Result = t.replaceAll("(<[^>]*>)", "").trim();
				String a[] = Result.split(",");   			
				// TODO Auto-generated method stub
//				badgeviewTab01.setTargetView(mImgTab01);
//				badgeviewTab01.setBadgeCount(Integer.parseInt(a[0]));
				badgeviewTab02.setTargetView(mImgTab02);
				badgeviewTab02.setBadgeCount(Integer.parseInt(a[0]));	
				}			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.id_tab_01:
			setSelect(0);
			break;
		case R.id.id_tab_02:
			setSelect(1);
			break;
		case R.id.id_tab_03:
			setSelect(2);
			break;
		case R.id.id_tab_04:
			setSelect(3);
			break;
		case R.id.img_right:
			
				titlePopup.show(findViewById(R.id.layout_bar));
			
			break;
		default:
			break; 
		}
	}
	private void setSelect(int i)
	{
		setTab(i);
		mViewPager.setCurrentItem(i);
	}
	private void setTab(int i)
	{
		resetImgs();
		// 设置图片为亮色
		// 切换内容区域
		switch (i)
		{
		case 0:
			mImgTab01.setImageResource(R.drawable.tab_01_pressed);
			img_right.setVisibility(View.VISIBLE);
			img_right.setImageResource(R.drawable.icon_add);
//			ivmultifun.setVisibility(View.VISIBLE);
			tvMaintitle.setText("首页");
			//刷新tab中的数据
			
			break;
		case 1:
			mImgTab02.setImageResource(R.drawable.tab_02_pressed);
			img_right.setVisibility(View.VISIBLE);
			img_right.setImageResource(R.drawable.icon_add);
//			ivmultifun.setVisibility(View.VISIBLE);
			tvMaintitle.setText("任务");
			break;
		case 2:
			tvMaintitle.setText("通知");
			img_right.setVisibility(View.GONE);
			mImgTab03.setImageResource(R.drawable.tab_03_pressed);
//			ivmultifun.setVisibility(View.GONE);
			break;
		case 3:
			tvMaintitle.setText("设置");
			img_right.setVisibility(View.GONE);
			mImgTab04.setImageResource(R.drawable.tab_04_pressed);
//			ivmultifun.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * 切换图片至暗色
	 */
	private void resetImgs()
	{
		mImgTab01.setImageResource(R.drawable.tab_01_normal);
		mImgTab02.setImageResource(R.drawable.tab_02_normal);
		mImgTab03.setImageResource(R.drawable.tab_03_normal);
		mImgTab04.setImageResource(R.drawable.tab_04_normal);
	}
}
