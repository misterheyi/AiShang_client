package com.aishang.app.common;

import java.io.File;

import android.os.Environment;

public interface Constants {

	public static final String host = "http://suona110.vicp.cc:8088/";
//	static final String host = "http://192.168.1.102/";
	public static final String bce = "http://suona110.vicp.cc:8088/";
//	static final String bce = "http://192.168.1.102:80/";
	static final String api = "api/1.0/";
//	static final String api = "/api/1.0/";
	static final String login = host + api + "login";
	static final String priceList = host + api + "adPictrue";
	static final String hairStyle = host + api + "hairStyle";
	static final String adVideo = host + api + "adVideo";
	static final String playAd = host + api + "playAd";
	static final String scrollPicture = host + api + "scrollPicture";
	static final String playScrollPicture = host + api + "playScrollPicture";
	static final String chechUpdate = host + api + "checkUpdate";

	static final String ACTION_PRICELIST = "com.aishang.action.pricelist";
	static final String ACTION_HAIRSTYLE = "com.aishang.action.hairstyle";
	static final String ACTION_ADVIDEO = "com.aishang.action.advideo";
	static final String ACTION_ADPICTURE = "com.aishang.action.adpicture";
	static final String ACTION_AUTO_UPDATE = "com.aishang.action.autoUpdate";
	static final String ACTION_IS_UPDATE = "com.aishang.action.isUpdate";

	String SERVICE_TYPE_NAME = "servicetype"; // 通过Intent获取启动服务类型的名字
	String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
	String DOWNLOAD_PATH = SDCARD + File.separator + "AiShang" + File.separator + "Download" + File.separator;
	String DOWNLOAD_PATH_VIDEO = DOWNLOAD_PATH + "Video" + File.separator;
	String DOWNLOAD_PATH_APK = DOWNLOAD_PATH + "APK" + File.separator;
	String DOWNLOAD_INTENT = "DownloadItem";

	/**
	 * 出错
	 */
	int ERROR_CODE = -1;
	/**
	 * 启动下载模块
	 */
	int START_DOWNLOAD = 99;
	/**
	 * 从数据库中装载下载任务
	 */
	int START_DOWNLOAD_LOADITEM = 10;
	/**
	 * 将数据库中所有的下载状态设置为 暂停
	 */
	int START_DOWNLOAD_ALLPAUSE = 11;

	/**
	 * 正在下载
	 */
	int DOWNLOAD_STATE_DOWNLOADING = 2;
	/**
	 * 暂停
	 */
	int DOWNLOAD_STATE_PAUSE = 3;
	/**
	 * 等待
	 */
	int DOWNLOAD_STATE_WATTING = 4;
	/**
	 * 下载失败
	 */
	int DOWNLOAD_STATE_FAIL = 5;
	/**
	 * 下载成功
	 */
	int DOWNLOAD_STATE_SUCCESS = 6;
	/**
	 * 继续下载
	 */
	int DOWNLOAD_STATE_RESUME = 7;
	/**
	 * 任务被删除
	 */
	int DOWNLOAD_STATE_DELETE = 8;
	/**
	 * 清除所有任务
	 */
	int DOWNLOAD_STATE_CLEAR = 9;
	/**
	 * 未下载的状态
	 */
	int DOWNLOAD_STATE_NONE = 0;

}
