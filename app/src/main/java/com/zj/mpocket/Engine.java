package com.zj.mpocket;

import android.content.Context;
import android.text.TextUtils;

import com.zj.mpocket.utils.PreferenceUtil;

public class Engine {

	private static Engine instance;

	public static Engine getInstance() {
		if (instance == null) {
			instance = new Engine();
		}
		return instance;
	}

	String merchantId;
	String loginToken;

	/**
	 * 版本号
	 * @param context
	 * @return
	 */
	private int getCurrentVersion(Context context) {
		if (null == context) {
			return -1;
		}
		return PreferenceUtil.getPrefInt(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.CURRENT_VERSION, -1);
	}

	private void setCurrentVersion(Context context, int currentVersion) {
		if (null == context) {
			return;
		}
		PreferenceUtil.setPrefInt(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.CURRENT_VERSION, currentVersion);
	}

	/**
	 * 判断用户是否已经登录
	 * @param context
	 * @return
	 */
	public boolean isLogined(Context context) {
		if (null == context) {
			return false;
		}
		//accesToken有值且loginId有值就代表已登录
		if (!TextUtils.isEmpty(getToken(context)) && !TextUtils.isEmpty(getUserId(context))) {
			return true;
		}
		return false;
	}
	
	/**
	 * 从配置文件获取merchantId
	 * @param context
	 * @return
	 */
	public String getUserId(Context context) {
		if (null == context) {
			return null;
		}
		return PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, null);
	}
	
	/**
	 * 保存merchantId到配置文件
	 * @param context
	 * @param merchantId
	 */
	public void setMerchantId(Context context, String merchantId) {
		if (null == context) {
			return;
		}
		PreferenceUtil.setPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_Id, merchantId);
	}
	
	/**
	 * 获取token
	 * @param context
	 * @return
	 */
	private String getToken(Context context) {
		if (null == context) {
			return null;
		}
		return PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, null);
	}

	/**
	 * 保存token
	 * @param context
	 * @param token
	 */
	private void setToken(Context context, String token) {
		if (null == context) {
			return;
		}
		PreferenceUtil.setPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.TOKEN, token);
	}
	
	/**
	 * 退出登录时调用
	 */
	private void exitLogin(Context context) {
		//保留账号密码，显示在登录页面的输入框,其他全部清除
		String account = PreferenceUtil.getPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_ACCOUNT, null);
		PreferenceUtil.clearPreference(context, Constant.USER_INFO, Context.MODE_PRIVATE);
		PreferenceUtil.setPrefString(context, Constant.USER_INFO, Context.MODE_PRIVATE, Constant.MERCHANT_ACCOUNT, account);
		loginToken = null;
		merchantId = null;
	}

}
