package com.aishang.app.receiver;

import java.io.File;
import java.util.List;

import com.aishang.app.AiShangApplication;
import com.aishang.app.common.Constants;
import com.aishang.app.data.bean.AdVideoVO;
import com.aishang.app.data.dto.AdVideoDTO;
import com.aishang.app.db.Video;
import com.aishang.app.download.DownloadItem;
import com.aishang.app.service.DownloadService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownloadVideoReceiver extends BroadcastReceiver implements Constants{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_ADVIDEO)){
		AiShangApplication application = (AiShangApplication)context.getApplicationContext();
		AdVideoDTO adVideoDTO = application.getAdVideoDTO();
		List<AdVideoVO> list = adVideoDTO.getVideos();
		if (list == null)
			return;
		List<Video> videos = application.getVideos();
		if(videos == null)
			return;
		for (Video video : videos) {
			boolean f = false;
			for (AdVideoVO ad : list) {
				f = video.getVid() == ad.getAdVideo().getAdVideo_id();
				if (f)
					break;
			}
			if (!f) {
				File file = new File(video.getFilePath());
				if (file.exists()) {
					file.delete();
				}
				application.deleteVideo(video.getVid());
			} else {
				System.out.println("保留");
			}
		}

		for (AdVideoVO adVideo : list) {
			boolean f = false;
			for (Video video : videos) {
				f = video.getVid() == adVideo.getAdVideo().getAdVideo_id();
				if (f) {
					break;
				}
			}
			if (!f) {
				DownloadItem downloadItem = new DownloadItem(DOWNLOAD_PATH_VIDEO);
				downloadItem.setDownloadUrl(bce + adVideo.getAdVideo().getAdVideo_path());
				downloadItem.setFileName(adVideo.getAdVideo().getAdVideo_desc());
				downloadItem.arg1 = adVideo.getAdVideo().getAdVideo_id();
				downloadItem.arg2 = adVideo.getAdVideo().getAdVideo_type();
				downloadItem.arg3 = adVideo.getUser().getUserGroup_id();
				Intent i = new Intent(application, DownloadService.class);
				i.putExtra(SERVICE_TYPE_NAME, START_DOWNLOAD);
				i.putExtra(DOWNLOAD_INTENT, downloadItem);
				application.startService(i);
			} else {
				Log.d("AiShang", "该视频无需下载:"+adVideo.getAdVideo().getAdVideo_desc());
			}

		}
		}
	}
}
