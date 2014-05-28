package com.aishang.app.download;

import java.io.File;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import com.aishang.app.common.Constants;

public class DownloadManager implements Constants {

	private boolean isStop;
	private HttpHandler<File> mHttpHandler;

	public DownloadManager startDownload(String url, String toPath, AjaxCallBack<File> downCallBack) {
		if (downCallBack == null) {
			throw new RuntimeException("RequestCallBack对象不能为null");
		} else {
			FinalHttp down = new FinalHttp();
			mHttpHandler = down.download(url, toPath, false, downCallBack);
		}

		return this;
	}

	public void stopDownload() {
		if (mHttpHandler != null) {
			mHttpHandler.stop();
			mHttpHandler.cancel(true);
			if (!mHttpHandler.isStop()) {
				mHttpHandler.stop();
				mHttpHandler.cancel(true);
			}
		}
	}

	public boolean isStop() {
		isStop = mHttpHandler.isStop();
		return isStop;
	}

}
