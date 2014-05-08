package com.aishang.app.common;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtils {

	public static int getWindowWidth(Activity context) {
		return getDM(context).widthPixels;
	}

	public static int getWindowHeigth(Activity context) {
		return getDM(context).heightPixels;
	}

	public static DisplayMetrics getDM(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();// 获取当前显示的界面大小
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
