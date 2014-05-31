package com.aishang.app.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aishang.app.AiShangApplication;
import com.aishang.app.R;
import com.aishang.app.common.Constants;
import com.aishang.app.db.Video;
import com.aishang.app.download.DownloadItem;
import com.aishang.app.download.DownloadManager;

public class DownloadService extends Service implements Constants {

	private Handler handler;
	private List<DownloadItem> downloadItems;
	private Map<String, DownloadItem> currentDownloadItems;
	private AiShangApplication application;

	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
		currentDownloadItems = new HashMap<String, DownloadItem>();
		application = (AiShangApplication) getApplication();
		downloadItems = new ArrayList<DownloadItem>();
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		int code = intent.getIntExtra(SERVICE_TYPE_NAME, ERROR_CODE);
		//System.out.println("Service:" + code);
		switch (code) {
		case START_DOWNLOAD:
			// 开启下载服务
			DownloadItem item = (DownloadItem) intent.getSerializableExtra(DOWNLOAD_INTENT);
			boolean f = false;
			for(DownloadItem downloadItem : downloadItems){
				f = downloadItem.arg1 == item.arg1;
				if(f){
					break;
				}
			}
			if(!f){
				item.setDownloadState(DOWNLOAD_STATE_WATTING);
				Log.d("VideoPlayerActivity", "添加到下载服务：" + item.arg1);
				downloadItems.add(item);
				startDownload(item);
				startTimerUpdateProgress();
			}
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(update);
		super.onDestroy();
	}

	/**
	 * 开始下载
	 */
	public void startDownload(DownloadItem di) {
		for (int i = 0; i < downloadItems.size(); i++) {
			if (downloadItems.get(i).getDownloadState() == DOWNLOAD_STATE_WATTING) {
				if (currentDownloadItems.size() < 1) {
					DownloadItem temp = downloadItems.get(i);
					temp.setDownloadState(DOWNLOAD_STATE_DOWNLOADING);
					DownloadManager manager = download(temp);
					temp.setDownLoadManager(manager);
					currentDownloadItems.put(temp.getUUID(), temp);
				} else {
					if (di != null)
						di.setDownloadState(DOWNLOAD_STATE_WATTING);
				}
			}
		}
	}

	/**
	 * 重启下载
	 */
	public void resumeDownload(DownloadItem item) {
		item.setDownloadState(DOWNLOAD_STATE_WATTING);
		startDownload(item);
	}

	/**
	 * 暂停下载
	 */
	public void pauseDownload(DownloadItem item) {
		item.setDownloadState(DOWNLOAD_STATE_PAUSE);
		DownloadManager manager = item.getDownLoadManager();
		if (manager != null) {
			manager.stopDownload();
		}
	}

	/**
	 * 暂停全部下载
	 */
	public void allPauseDownload() {
		for (int i = 0; i < downloadItems.size(); i++) {
			DownloadItem item = downloadItems.get(i);
			if (item.getDownloadState() != DOWNLOAD_STATE_DOWNLOADING) {
				item.setDownloadState(DOWNLOAD_STATE_PAUSE);
				DownloadManager manager = item.getDownLoadManager();
				if (manager != null) {
					manager.stopDownload();
				}
			}

		}
	}

	/**
	 * 删除下载
	 */
	public void deleteDownload() {

	}

	/**
	 * 清空下载
	 */
	public void clearDownload() {

	}

	public DownloadManager download(final DownloadItem item) {
		DownloadManager manager = new DownloadManager();
		manager.startDownload(item.getDownloadUrl(), item.getFilePath(), new AjaxCallBack<File>() {
			@Override
			public void onStart() {
				Log.d("AiShang", "开始下载文件："+item.getFileName());
				mBuilder.setContentTitle("下载文件中...").setContentText(item.getFileName()+"").setSmallIcon(R.drawable.logo_index);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				Log.d("AiShang", "下载文件失败:"+item.getFileName());
				t.printStackTrace();
//				item.setDownloadState(DOWNLOAD_STATE_FAIL);
				startDownload(null);
			}

			@Override
			public void onLoading(long count, long current) {
				Log.d("AiShang", "下载中->" + current + "/" + count);
				mBuilder.setProgress(100, (int) ((float) current / (float) count * 100), false);
				mNotifyManager.notify(0, mBuilder.build());
			}

			@Override
			public void onSuccess(File responseInfo) {
				item.setDownloadState(DOWNLOAD_STATE_SUCCESS);
				currentDownloadItems.remove(item.getUUID());
				Video video = new Video();
				video.setFilePath(item.getFilePath());
				video.setPath(item.getDownloadUrl());
				video.setVid(item.arg1);
				video.setType(item.arg2);
				video.setGroupId(item.arg3);

				application.saveVideo(video);
				mBuilder.setContentText("下载完成").setProgress(0, 0, false);
				mNotifyManager.cancel(0);
				downloadItems.remove(item);
				startDownload(null);
			}

		});
		return manager;
	}

	public void startTimerUpdateProgress() {
		handler.postDelayed(update, 1000);
	}

	Runnable update = new Runnable() {

		@Override
		public void run() {

		}
	};

	public void notifyProgress(int incr) {

	}

}