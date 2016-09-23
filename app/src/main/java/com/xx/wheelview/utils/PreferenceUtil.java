package com.zj.wheelview.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtil {
	
	public static String getPrefString(Context context, String name, int mode, String key, final String defaultValue) {
		final SharedPreferences settings = context.getSharedPreferences(name, mode);
		return settings.getString(key, defaultValue);
	}

	public static void setPrefString(Context context, String name, int mode, final String key, final String value) {
		final SharedPreferences settings = context.getSharedPreferences(name, mode);
		settings.edit().putString(key, value).commit();
	}

	public static boolean getPrefBoolean(Context context, String name, int mode, final String key, final boolean defaultValue) {
		final SharedPreferences settings = context.getSharedPreferences(name, mode);
		return settings.getBoolean(key, defaultValue);
	}

	public static void setPrefBoolean(Context context, String name, int mode, final String key, final boolean value) {
		final SharedPreferences settings = context.getSharedPreferences(name, mode);
		settings.edit().putBoolean(key, value).commit();
	}

	public static void setPrefInt(Context context, String name, int mode, final String key, final int value) {
		final SharedPreferences settings = context.getSharedPreferences(name, mode);
		settings.edit().putInt(key, value).commit();
	}

	public static int getPrefInt(Context context, String name, int mode, final String key, final int defaultValue) {
		final SharedPreferences settings = context.getSharedPreferences(name, mode);
		return settings.getInt(key, defaultValue);
	}
	
	public static void clearPreference(Context context, String name, int mode) {
		final SharedPreferences sp = context.getSharedPreferences(name, mode);
		final Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
}
