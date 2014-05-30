package com.aishang.app.ui.base;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.aishang.app.AiShangApplication;

public class BaseActivity extends Activity {
	protected PowerManager powerManager = null;
	protected WakeLock wakeLock = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		this.wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.wakeLock.acquire();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.wakeLock.release();
	}

	public AiShangApplication getApp() {
		return (AiShangApplication) getApplication();
	}

	public void adTimeZero() {
		//System.out.println("AD Time Zero");
		getApp().setAd_time(0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		adTimeZero();
		return super.dispatchTouchEvent(ev);
	}

	public void back(View v) {
		finish();
	}

	public void setting(View v) {
		Intent intent = new Intent(Settings.ACTION_SETTINGS);
		startActivity(intent);
	}

	public void startSoft(String packageName, Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = packageManager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pi == null) {
			Toast.makeText(this, "没有此软件", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);
		List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			String className = ri.activityInfo.name;
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName cn = new ComponentName(packageName, className);
			intent.setComponent(cn);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

}
