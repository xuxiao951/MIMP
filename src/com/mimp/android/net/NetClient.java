package com.mimp.android.net;

import java.io.File;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.juns.health.net.loopj.android.http.AsyncHttpClient;
import com.juns.health.net.loopj.android.http.JsonHttpResponseHandler;
import com.juns.health.net.loopj.android.http.RequestParams;
import com.mimp.android.ch0.App;
import com.mimp.android.ch0.Constants;
import com.mimp.android.ch0.R;
import com.mimp.android.common.NetUtil;
import com.mimp.android.common.Utils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class NetClient {

	private static Context context;
	// http ����
	private AsyncHttpClient client;
	// ��ʱʱ��
	private int TIMEOUT = 20000;

	// ͼƬ������
	private static ImageLoader mImageLoader;
	private static DisplayImageOptions binner_options;
	private static DisplayImageOptions icon_options;
	private static DisplayImageOptions user_icon_options;
	private static DisplayImageOptions girl_options;
	// Imageload����Ŀ¼
	private static File cacheDir = new File(App.getHJYCacheDir() + "/Imageload");

	public NetClient(Context context) {
		NetClient.context = context;
		client = new AsyncHttpClient();
		client.setTimeout(TIMEOUT);
	}

	static {
		ActivityManager am = (ActivityManager) App.getInstance()
				.getSystemService(Context.ACTIVITY_SERVICE);
		int memClass = am.getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 4; // Ӳ���û���������Ϊϵͳ�����ڴ��1/4

		binner_options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.large_logo_default_4) // resource
				// or
				.showImageForEmptyUri(R.drawable.large_logo_default_4) // resource
				.showImageOnFail(R.drawable.large_logo_default_4) // resource or
				.cacheInMemory(false) // default
				.cacheOnDisc(true) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.build();
		icon_options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.head) // resource
														// or
				.showImageForEmptyUri(R.drawable.head) // resource
				.showImageOnFail(R.drawable.head) // resource or
				.cacheInMemory(true) // default
				.cacheOnDisc(true) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.displayer(new RoundedBitmapDisplayer(50)) // default
				.build();
		user_icon_options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.head) // resource
				.showImageForEmptyUri(R.drawable.head) // resource
				.showImageOnFail(R.drawable.head) // resource
				.cacheInMemory(true) // default
				.cacheOnDisc(true) // default
				.imageScaleType(ImageScaleType.NONE) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				App.getInstance()).threadPoolSize(10)
				.threadPriority(Thread.NORM_PRIORITY + 1)
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(cacheSize))
				.memoryCacheSize(cacheSize)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheSize(30 * 1024 * 1024).discCacheFileCount(500)
				.writeDebugLogs().build();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(config);
	}

	/**
	 * ����url��ȡСͼƬ ���Զ����õ�imageview�� ��ȡ��ͼƬ���浽�ڴ�(������ش�ͼ)
	 * 
	 * @param img
	 * @param url
	 */
	public static void getIconBitmap(ImageView view, String url) {
		mImageLoader.displayImage(url, view, icon_options);
	}

	/**
	 * ����url��ȡ��ͼƬ ���Զ����õ�imageview�� ��ȡ��ͼƬ�����浽�ڴ�
	 * 
	 * @param img
	 * @param url
	 */
	public static void getBinnerBitmap(ImageView imageView, String url) {
		mImageLoader.displayImage(url, imageView, binner_options);
	}

	public static void getGirlBitmap(ImageView imageView, String url) {
		mImageLoader.displayImage(url, imageView, girl_options);
	}

	public static void getBinnerBitmap(String url, ImageLoadingListener listener) {
		mImageLoader.loadImage(url, listener);
	}

	/**
	 * ����url��ȡ��ͼƬ ���Զ����õ�imageview�� ��ȡ��ͼƬ�����浽�ڴ�
	 * 
	 * @param img
	 * @param url
	 */
	public static void getHalfHeightBitmap(ImageView imageView, String uri,
			ImageLoadingListener lister) {
		mImageLoader.displayImage(uri, imageView, binner_options, lister);
	}

	/**
	 * get��ʽ������÷��� ���ظ�ʽ��Ϊjson���� ����Ϊjson
	 * 
	 * @param url
	 *            ����URL
	 * @param params
	 *            ������� ����Ϊ��
	 * @param res
	 *            ����ʵ�ִ��� ����ɹ�ʧ�ܵ� �ص�
	 */
	public void get(String url, RequestParams params,
			final JsonHttpResponseHandler res) {
		if (!NetUtil.checkNetWork(context)) {
			Utils.showLongToast(context, Constants.NET_ERROR);
			return;
		}
		try {
			if (params != null)
				// ��������� ��ȡjson����
				client.get(url, params, res);
			else
				// ��������� ��ȡjson����
				client.get(url, res);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
	}

	/**
	 * json post��ʽ������÷��� ����Ϊjson
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            ������� ����Ϊ��
	 * @param res
	 *            ����ʵ�ִ��� ����ɹ�ʧ�ܵ� �ص�
	 */
	public void post(String url, RequestParams params,
			final JsonHttpResponseHandler res) {
		System.out.println("����URL��" + url);
		if (!NetUtil.checkNetWork(context)) {
			Utils.showLongToast(context, Constants.NET_ERROR);
			return;
		}
		try {
			if (params != null) {
				client.post(url, params, res);
			} else {
				client.post(url, res);
			}
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
	}
}