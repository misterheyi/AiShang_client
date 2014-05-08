package com.aishang.app.ui.activity;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.aishang.app.R;
import com.aishang.app.common.Constants;
import com.aishang.app.data.bean.AdVideo;
import com.aishang.app.data.bean.Version;
import com.aishang.app.data.dto.AdVideoDTO;
import com.aishang.app.db.Video;
import com.aishang.app.download.DownloadItem;
import com.aishang.app.service.DownloadService;
import com.aishang.app.service.PlayADService;
import com.aishang.app.ui.base.BaseActivity;

public class MainActivity extends BaseActivity implements Constants {

	private Intent service;
	private BroadcastReceiver update;
	private BroadcastReceiver autoUpdate;
	private BroadcastReceiver receiver;
	private BroadcastReceiver isUpdate;
	
	private Intent adIntent;

	// private PlayADService adService;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initDir();

		service = new Intent(getApplicationContext(), DownloadService.class);
		startService(service);

		adIntent = new Intent(getApplicationContext(), PlayADService.class);
		startService(adIntent);

		update = new Update();
		IntentFilter filter = new IntentFilter(ACTION_ADVIDEO);
		registerReceiver(update, filter);
		autoUpdate = new AutoUpdate();
		IntentFilter filter2 = new IntentFilter(ACTION_AUTO_UPDATE);
		registerReceiver(autoUpdate, filter2);
		receiver = new DownloadCompleteReceiver();
		registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		isUpdate = new AskIsUpdate();
		registerReceiver(isUpdate, new IntentFilter(ACTION_IS_UPDATE));

		getApp().getAdVideoDTOList();
		getApp().getAdPictureDTOList();

	}

	private void initDir() {
		File file = new File(DOWNLOAD_PATH_VIDEO);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	// ServiceConnection conn = new ServiceConnection() {
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// adService = ((PlayADService.LocalBinder) service).getService();
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// // adService = null;
	// }
	//
	// };

	@Override
	protected void onResume() {
		// 重启服务
		getApp().setPlayAD(true);
		super.onResume();
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(update);
		unregisterReceiver(autoUpdate);
		unregisterReceiver(receiver);
		unregisterReceiver(isUpdate);
		stopService(service);
		stopService(adIntent);
		super.onDestroy();
	}

	public void onClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_PriceList:
			Intent priceList = new Intent(this, PriceListActivity.class);
			startActivity(priceList);
			break;
		case R.id.btn_HairShow:
			Intent hairShow = new Intent(this, HairShowActivity.class);
			startActivity(hairShow);
			break;
		case R.id.btn_LiveTV:
			Intent intent3 = new Intent(this, VideoPlayerActivity.class);
			intent3.putExtra("isClick", true);
			intent3.putExtra("status", 1);
			intent3.putExtra("type", 1);
			startActivity(intent3);
			break;
		case R.id.btn_Demand:
			Intent intent4 = new Intent(this, VideoPlayerActivity.class);
			intent4.putExtra("isClick", true);
			intent4.putExtra("status", 2);
			intent4.putExtra("type", 1);
			startActivity(intent4);
			break;
		}
	}

	class Update extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			AdVideoDTO adVideoDTO = getApp().getAdVideoDTO();
			List<AdVideo> list = adVideoDTO.getVideos();
			if (list == null)
				return;
			List<Video> videos = getApp().getVideos();
			if(videos == null)
				return;
			for (Video video : videos) {
				boolean f = false;
				for (AdVideo ad : list) {
					f = video.getVid() == ad.getAdVideo_id();
					if (f)
						break;
				}
				if (!f) {
					File file = new File(video.getFilePath());
					if (file.exists()) {
						file.delete();
					}
					getApp().deleteVideo(video.getVid());
					System.out.println("删除");
				} else {
					System.out.println("保留");
				}
			}

			for (AdVideo adVideo : list) {
				boolean f = false;
				for (Video video : videos) {
					f = video.getVid() == adVideo.getAdVideo_id();
					if (f) {
						break;
					}
				}
				if (!f) {
					System.out.println("下载");
					DownloadItem downloadItem = new DownloadItem(DOWNLOAD_PATH_VIDEO);
					downloadItem.setDownloadUrl(bce + adVideo.getAdVideo_path());
					downloadItem.arg1 = adVideo.getAdVideo_id();
					downloadItem.arg2 = adVideo.getAdVideo_type();
					Intent i = new Intent(getApplicationContext(), DownloadService.class);
					i.putExtra(SERVICE_TYPE_NAME, START_DOWNLOAD);
					i.putExtra(DOWNLOAD_INTENT, downloadItem);
					startService(i);
				} else {
					System.out.println("无需下载");
				}

			}

		}

	}

	class AutoUpdate extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("更新");
			getApp().getAdVideoDTOList();
			getApp().checkUpdate();
		}

	}

	class DownloadCompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				Cursor c = manager.query(new DownloadManager.Query().setFilterById(downId));
				c.moveToFirst();
				String s = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				System.out.println("DownloadCompleteReceiver: " + s);
				getApp().installApk(new File(s));
			}
		}
	}

	class AskIsUpdate extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("AskIsUpdate");
			Version version = (Version) intent.getSerializableExtra("VERSION");
			v = version.getVersion();
			if (v2.equals(v)) {
				return;
			}
			if (builder != null) {
				return;
			}
			dialog(version);
		}

	}

	private String v = "";
	private String v2 = "";
	private AlertDialog.Builder builder;

	private void down(Version version) {
		DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(bce + version.getPath());
		System.out.println("下载：" + bce + version.getPath());
		DownloadManager.Request dwreq = new DownloadManager.Request(uri);
		dwreq.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
		dwreq.setTitle("下载更新");
		dwreq.setDescription("");
		dwreq.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "aishangwang.apk");
		dwreq.setNotificationVisibility(0);
		manager.enqueue(dwreq);
	}

	private void dialog(final Version version) {
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.logo_index);
		builder.setTitle("检测到更新");
		builder.setPositiveButton("确定更新", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				down(version);
				builder = null;
			}

		});
		builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				v2 = version.getVersion();
				builder = null;
			}
		});
		builder.create().show();
	}

}