package com.aishang.app.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.aishang.app.AiShangApplication;
import com.aishang.app.common.Constants;
import com.aishang.app.ui.activity.VideoPlayerActivity;

@SuppressLint("HandlerLeak")
public class PlayADService extends Service implements Constants {
	private IBinder binder;
	private AiShangApplication application;
	private Runnable logic;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Intent intent4 = new Intent(PlayADService.this, VideoPlayerActivity.class);
			intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent4.putExtra("type", 0);
			startActivity(intent4);
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		binder = new LocalBinder();
		application = (AiShangApplication) getApplication();
		application.setAd_time(0);
		logic = new ServerRun();
		handler.post(logic);
	}

	public class LocalBinder extends Binder {

		public PlayADService getService() {
			return PlayADService.this;
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// executorService.submit(new ServerRun());
		return super.onStartCommand(intent, flags, startId);
	}

	class ServerRun implements Runnable {

		int update = 0;

		@Override
		public void run() {
			application.setAd_time(application.getAd_time() + 1);
			System.out.println("无操作时间: " + application.getAd_time());
			if (application.getAd_time() == 30) {
				System.out.println("无操作时间到 播放视频");
				application.setAd_time(0);
				if (!application.isPlaying() && application.isPlayAD()) {
					if (application.getVideos().size() > 0)
						handler.sendEmptyMessage(1);
				}
			}

			update++;

			if (update == 20) {
				update = 0;
				Intent intent = new Intent(ACTION_AUTO_UPDATE);
				sendBroadcast(intent);
			}
			
			handler.postDelayed(this, 1000);
		}
	}

	@Override
	public void onDestroy() {
		System.out.println("Service Destory");
		handler.removeCallbacks(logic);
		super.onDestroy();
	}

}