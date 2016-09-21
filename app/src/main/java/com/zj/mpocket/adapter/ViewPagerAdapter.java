package com.zj.mpocket.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zj.mpocket.Constant;
import com.zj.mpocket.activity.MainActivity;
import com.zj.mpocket.utils.PreferenceUtil;

import java.util.List;

/**
 * 引导页面适配器
 */
public class ViewPagerAdapter extends PagerAdapter {

	// 界面列表
	private List<View> views;

	Activity activity;

	public ViewPagerAdapter(Activity activity, List<View> views) {
		this.views = views;
		this.activity = activity;
	}

	// 销毁arg1位置的界面
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	// 获得当前界面size
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
		if(arg1 == 2) {
			views.get(arg1).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//设置已经引导过了，下次启动不用再次引导
					PreferenceUtil.setPrefBoolean(activity, Constant.APP_CONFIG, Context.MODE_PRIVATE, Constant.NEW_INSTALL, false);
					Intent intent = new Intent(activity, MainActivity.class);
					activity.startActivity(intent);
					activity.finish();
				}
			});
		}
		return views.get(arg1);
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

}