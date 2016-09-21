package com.zj.mpocket.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.zj.mpocket.Constant;
import com.zj.mpocket.R;
import com.zj.mpocket.activity.MerchantManageActivity;
import com.zj.mpocket.utils.CommonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * download service 下载服务
 * 
 * @author wxx
 * 
 */
public class DownloadService extends Service {

	public static final String BUNDLE_KEY_DOWNLOAD_URL = "download_url";
	
	private static final int NOTIFY_ID = 0;
	private int progress;
	private NotificationManager mNotificationManager;
	private boolean canceled;
	
	private String downloadUrl;
	
	private String mTitle = "正在下载最新版本";

	private String saveFileName = Constant.DEFAULT_SAVE_FILE_PATH;

	private DownloadBinder binder;

	private boolean serviceIsDestroy = false;

	private Context mContext = this;

	private Thread downLoadThread;

	private Notification.Builder mNotification;

	RemoteViews contentview;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// 下载完毕
				mNotificationManager.cancel(NOTIFY_ID);
				installApk();
				break;
			case 2:
				// 取消通知
				mNotificationManager.cancel(NOTIFY_ID);
				break;
			case 1:
				int rate = msg.arg1;
				if (rate < 100) {
					contentview.setTextViewText(R.id.tv_download_state, mTitle + "(" + rate + "%" + ")");
					contentview.setProgressBar(R.id.pb_download, 100, rate, false);
				} else {
					// 下载完毕后变换通知形式
					//mNotification.flags = Notification.FLAG_AUTO_CANCEL;
					mNotification.setAutoCancel(true);
					//mNotification.contentView = null;
					mNotification.setContent(null);
					Intent intent = new Intent(mContext, MerchantManageActivity.class);
					// 告知已完成
					intent.putExtra("completed", "yes");
					// 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
					PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					//mNotification.setLatestEventInfo(mContext, "下载完成", "文件已下载完毕", contentIntent);
					mNotification.setContentTitle("下载完成");
					mNotification.setContentText("文件已下载完毕");
					mNotification.setContentIntent(contentIntent);
					serviceIsDestroy = true;
					stopSelf();// 停掉服务自身
				}
				mNotificationManager.notify(NOTIFY_ID, mNotification.build());
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		downloadUrl = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL);
		saveFileName = saveFileName + getSaveFileName(downloadUrl); 
		return binder;
	}
	
	private String getSaveFileName(String downloadUrl) {
		if (downloadUrl == null || "".equals(downloadUrl)) {
			return "";
		}
		return downloadUrl.substring(downloadUrl.lastIndexOf("/"));
	}

	@Override
	public void onCreate() {
		super.onCreate();
		binder = new DownloadBinder();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		stopForeground(true);// 这个不确定是否有作用
	}

	private void startDownload() {
		canceled = false;
		downloadApk();
	}

	/**
	 * 创建通知
	 */
	private void setUpNotification() {
		//int icon = R.drawable.ic_notification;
		CharSequence tickerText = "准备下载";
		long when = System.currentTimeMillis();
		//mNotification = new Notification(0, tickerText, when);
		mNotification = new Notification.Builder(mContext)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(tickerText)
				.setWhen(when);
		// 放置在"正在运行"栏目中
		//mNotification.flags = Notification.FLAG_ONGOING_EVENT;
		mNotification.setOngoing(true);

		contentview = new RemoteViews(getPackageName(), R.layout.download_notification_show);
		contentview.setTextViewText(R.id.tv_download_state, mTitle);
		// 指定个性化视图
		//mNotification.contentView = contentView;
		mNotification.setContent(contentview);

		Intent intent = new Intent(this, MerchantManageActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 指定内容意图
		//mNotification.contentIntent = contentIntent;
		mNotification.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFY_ID, mNotification.build());
	}

	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		CommonUtil.installAPK(mContext, apkfile);
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			File file = new File(Constant.DEFAULT_SAVE_FILE_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			String apkFile = saveFileName;
			File saveFile = new File(apkFile);
			try {
				downloadUpdateFile(downloadUrl, saveFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[1024];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 10 >= downloadCount) {
					downloadCount += 10;
					// 更新进度
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					msg.arg1 = downloadCount;
					mHandler.sendMessage(msg);
				}
			}
			
			// 下载完成通知安装
			mHandler.sendEmptyMessage(0);
			// 下载完了，cancelled也要设置
			canceled = true;
			
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

	public class DownloadBinder extends Binder {
		public void start() {
			if (downLoadThread == null || !downLoadThread.isAlive()) {
				progress = 0;
				setUpNotification();
				new Thread() {
					public void run() {
						// 下载
						startDownload();
					};
				}.start();
			}
		}

		public void cancel() {
			canceled = true;
		}

		public int getProgress() {
			return progress;
		}

		public boolean isCanceled() {
			return canceled;
		}

		public boolean serviceIsDestroy() {
			return serviceIsDestroy;
		}

		public void cancelNotification() {
			mHandler.sendEmptyMessage(2);
		}

	}
}