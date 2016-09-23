package com.xx.wheelview.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.zj.wheelview.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 *
 */
public class CommonUtil {

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 *
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input) || "null".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static String join(String[] strArr) {
		StringBuffer sb = new StringBuffer("");
		if (strArr.length > 0) {
			for (String str : strArr) {
				if (!CommonUtil.isEmpty(str)) {
					sb.append(str);
					sb.append(":GGGGGG:");
				}
			}
			if (sb.toString().endsWith(":GGGGGG:")) {
				sb.delete(sb.lastIndexOf(":GGGGGG:"), sb.length());
			}
		}
		return sb.toString();
	}

	public static String[] split(String str) {
		if (CommonUtil.isEmpty(str)) {
			return null;
		}else {
			String[] split = str.split(":GGGGGG:");
			List<String> arr = new ArrayList<>();
			for (String string : split) {
				if (!CommonUtil.isEmpty(string)) {
					arr.add(string);
				}
			}
			return arr.toArray(new String[]{});
		}
	}
	
	/**
	 * 打印Toast方法1
	 */
	public static void showToastMessage(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 200);
		toast.show();
	}
	
	/**
	 * 打印Toast方法2
	 */
	public static void showToastIdMessage(Context context, int msgId) {
		Toast toast = Toast.makeText(context, msgId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 200);
		toast.show();
	}
	
	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftKeybord(Activity activity) {
		if (null == activity) {
			return;
		}
		try {
			final View v = activity.getWindow().peekDecorView();
			if (v != null && v.getWindowToken() != null) {
				InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		} catch (Exception e) {

		}
	}
	
	/**
	 * 获取网络类型
	 * @param ctx
	 * @return
	 */
	public static String getNetWorkSubType(Context ctx) {
		if (null == ctx) {
			return null;
		}
		String networkState = "";
		ConnectivityManager connectMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null) {
			if("".equals(info.getSubtypeName()) || info.getSubtypeName() == null){
				networkState = "wifi";
			}else{
				networkState = info.getSubtypeName();
			}
		}
		return networkState;
	}
	
	/**
	 * 判断手机是否安装过某应用
	 * @param context
	 * @param packageName
	 * @return 安装过返回true，否则返回false
	 */
	public static boolean isAvilible(Context context, String packageName){ 
		//获取packagemanager 
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息 
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名 
        List<String> pName = new ArrayList<String>();
        if(pinfo != null){ 
            for(int i = 0; i < pinfo.size(); i++){ 
                String pn = pinfo.get(i).packageName; 
                pName.add(pn); 
            } 
        } 
        //判断pName中是否有目标程序的包名，有返回true，没有返回false
        return pName.contains(packageName);
	} 
	
	/**
	 * 判断应用是否正在运行
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAppRunning(Context context, String packageName) {
		boolean isAppRunning = false;
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses(); 
		for (RunningAppProcessInfo appProcess : list) { 
			String processName = appProcess.processName; 
	        if (processName != null && processName.equals(packageName)) { 
	        	isAppRunning = true; 
	        	break; 
	        } 
		}
		return isAppRunning;
	}
	
	/** 
     * 通过packagename启动应用 
     * @param context 
     * @param packagename 
     * */  
    public static void startAppFromPackageName(Context context, String packagename) {
    	PackageManager packageManager = context.getPackageManager();   
    	Intent intent = packageManager.getLaunchIntentForPackage(packagename);  
    	context.startActivity(intent);    
    }

	/**
	 * 安装apk
	 * @param context
	 * @param file
	 */
	public static void installAPK(Context context, File file) {
		if (file == null || !file.exists())
			return;
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}
    
    /**
     * 通过包名获取versionCode
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		String packagename = context.getPackageName();
    	PackageInfo pinfo = null;
		try {
			pinfo = packageManager.getPackageInfo(packagename, PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	return pinfo.versionCode;
    }

	static public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString();
		}
		return null;
	}
	
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email){
		boolean flag = false;
		try{
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber){
		boolean flag = false;
		try{
			Pattern regex = Pattern.compile("^(((13[0-9])|(177)|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证身份证号
	 * @param idNum
	 * @return
     */
	public static boolean checkIdCard(String idNum) {
		Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
		Matcher matcher = idNumPattern.matcher(idNum);
		return matcher.matches();
	}

	/**
	 * 验证两个日期是否为同一天
	 * @param date1
	 * @param date2
     * @return
     */
	public static boolean isSameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth&& cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}

	/**
	 * 计算sign值
	 * @param params
	 * @return
     */
	public static String getSign(TreeMap<String, String> params) {
		Iterator iterator = params.keySet().iterator();
		StringBuffer sb = new StringBuffer("");
		while (iterator.hasNext()) {
			sb.append(params.get(iterator.next()));
		}
		LogUtil.log("sign--" + sb.toString());
		sb.append(Constant.key);
		return com.zj.wheelview.utils.Md5Util.md5(sb.toString());
	}

	/**
	 * 获取屏幕宽度
	 * @param context
     */
	public static int getScreenWidth(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		//int height = metric.heightPixels;
		return  width;
	}

	/**
	 * 获取屏幕高度
	 * @param context
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metric);
		//int width = metric.widthPixels;
		int height = metric.heightPixels;
		return  height;
	}
	
}
