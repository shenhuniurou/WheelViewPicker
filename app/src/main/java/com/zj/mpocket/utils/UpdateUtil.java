package com.zj.mpocket.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.mpocket.NetApi;
import com.zj.mpocket.R;
import com.zj.mpocket.model.UpdateModel;
import com.zj.mpocket.service.DownloadService;
import com.zj.mpocket.view.CommonDialog;
import com.zj.mpocket.view.WaitDialog;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * 更新工具类
 * 
 * @author wxx
 * 
 */

public class UpdateUtil
{

	private UpdateModel mUpdate;

	private Context mContext;
	
	private boolean isShow = false;
	
	private WaitDialog _waitDialog;

	static ServiceConnection conn;

	private AsyncHttpResponseHandler mCheckUpdateHandle = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			hideCheckDialog();
			if (isShow) {
				//showFaileDialog();
			}
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] responseBody) {
			hideCheckDialog();
			try {
				if (responseBody != null) {
					String result = new String(responseBody);
					LogUtil.log("result----" + result);
					JSONObject obj = new JSONObject(result);
					String resultCode = obj.getString("resultCode");
					String msg = obj.getString("msg");
					if ("00".equals(resultCode)) {
						String versionInfo = obj.getString("versionInfo");
						mUpdate = JSON.parseObject(versionInfo, UpdateModel.class);
					}else {
						CommonUtil.showToastMessage(mContext, msg);
					}
				}
			}catch (JSONException e) {
				e.printStackTrace();
			}
			onFinshCheck();
		}
	};

	public UpdateUtil(Context context, boolean isShow) {
		this.mContext = context;
		this.isShow = isShow;
	}
	
	public boolean haveNew() {
		if (this.mUpdate == null) {
			return false;
		}
		boolean haveNew = false;
		int curVersionCode = CommonUtil.getVersionCode(mContext);
		if (curVersionCode < Integer.parseInt(mUpdate.getVersion_code())) {
			haveNew = true;
		}
		return haveNew;
	}

	public void checkUpdate() {
		if (isShow) {
			showCheckDialog();
		}
		NetApi.getVersionInfo(mCheckUpdateHandle);
	}
	
	private void onFinshCheck() {
		if (haveNew()) {
			showUpdateInfo();
		} else {
			if (isShow) {
				//showLatestDialog();
			}
		}
	}

	private void showCheckDialog() {
		if (_waitDialog == null) {
			_waitDialog = DialogHelper.getWaitDialog((Activity) mContext, mContext.getString(R.string.loading_new_version_info));
		}
		_waitDialog.show();
	}

	private void hideCheckDialog() {
		if (_waitDialog != null) {
			_waitDialog.dismiss();
		}
	}
	
	private void showUpdateInfo() {
		if (mUpdate == null) {
			return;
		}
		CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(mContext);
		dialog.setTitle(mContext.getString(R.string.find_new_version) + mUpdate.getVersion_name());
		dialog.setMessage(mUpdate.getVersion_desc() + "<br/><br/>是否立即下载？");
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				openDownLoadService(mContext, mUpdate.getDownload_url());
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 下载服务
	 * @param context
	 * @param downurl
	 */
	public static void openDownLoadService(Context context, String downurl) {
		conn = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
				binder.start();
			}
		};
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, downurl);
		context.startService(intent);
		context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	public static void unbindService(Context context) {
		if (conn != null) {
			context.unbindService(conn);
		}
	}

}
