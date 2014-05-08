package com.aishang.app.download;

import java.io.File;

import com.aishang.app.common.Constants;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class DownloadManager implements Constants {

	private boolean isStop;
	private HttpHandler<File> mHttpHandler;

	public DownloadManager startDownload(String url, String toPath, RequestCallBack<File> downCallBack) {
		if (downCallBack == null) {
			throw new RuntimeException("RequestCallBack对象不能为null");
		} else {
			HttpUtils down = new HttpUtils();
			mHttpHandler = down.download(url, toPath, false, downCallBack);
		}

		return this;
	}

	public void stopDownload() {
		if (mHttpHandler != null) {
			mHttpHandler.stop();
			mHttpHandler.cancel(true);
			if (!mHttpHandler.isStopped()) {
				mHttpHandler.stop();
				mHttpHandler.cancel(true);
			}
		}
	}

	public boolean isStop() {
		isStop = mHttpHandler.isStopped();
		return isStop;
	}

}
