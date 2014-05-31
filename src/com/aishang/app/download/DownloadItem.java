package com.aishang.app.download;

import java.io.Serializable;
import java.util.UUID;

public class DownloadItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private int downloadState;
	private String downloadUrl;
	private String filePath;
	private String percentage = "0%";
	private String uuid;
	private String fileName;
	public int arg1, arg2 , arg3;

	private DownloadManager downLoadManager;

	public DownloadItem(String relativePATH) {
		UUID uuid = UUID.randomUUID();
		setUUID(uuid.toString().replace("-", ""));
		setFilePath(relativePATH + uuid);
	}

	public DownloadManager getDownLoadManager() {
		return downLoadManager;
	}

	public void setDownLoadManager(DownloadManager downLoadManager) {
		this.downLoadManager = downLoadManager;
	}

	public int getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public String getFileName() {
		if(fileName == null){
			return "没有文件描述";
		}
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
