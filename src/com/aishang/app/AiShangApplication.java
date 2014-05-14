package com.aishang.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.aishang.app.common.Constants;
import com.aishang.app.common.PreferenceUtil;
import com.aishang.app.data.bean.Cache;
import com.aishang.app.data.bean.HairStyle;
import com.aishang.app.data.bean.Version;
import com.aishang.app.data.dto.AdPictureDTO;
import com.aishang.app.data.dto.AdVideoDTO;
import com.aishang.app.data.dto.HairStyleDTO;
import com.aishang.app.data.dto.PriceListDTO;
import com.aishang.app.db.Video;
import com.aishang.app.download.DownloadItem;
import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class AiShangApplication extends Application implements Constants {

	private PriceListDTO mPriceListDTO;
	private HairStyleDTO mHairStyleDTO;
	private AdVideoDTO mAdVideoDTO;
	private AdPictureDTO mAdPictureDTO;
	private HttpUtils http;
	private DbUtils db;
	private PreferenceUtil mPreferenceUtil;

	private List<DownloadItem> downLoadList;
	private List<HairStyle> hairInfo;
	private HairStyle show;

	private int ad_time;
	private boolean isPlaying;
	private boolean isPlayAD;
	private String soft;

	public static final int CACHE_PRICELIST_TYPE = 1;
	public static final int CACHE_ADPICTURE_TYPE = 2;
	public static final int CACHE_HAIRSTYLE_TYPE = 3;

	@Override
	public void onCreate() {
		super.onCreate();
		http = new HttpUtils();
		db = DbUtils.create(this, "aishang");
		mPreferenceUtil = new PreferenceUtil(getApplicationContext());
	}

	//获取店内推荐数据
	public PriceListDTO getPriceListDTO() {
		if (mPriceListDTO == null || mPriceListDTO.getStatus_code() == 500) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("hairstylistId",mPreferenceUtil.getString("hairstylist"));
			params.addBodyParameter("storeId",mPreferenceUtil.getString("store"));
			params.addBodyParameter("agentId",mPreferenceUtil.getString("agent"));
			http.send(HttpMethod.POST, priceList, params,
					new RequestCallBack<String>() {
						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String responseStr = responseInfo.result;
							if (TextUtils.isEmpty(responseStr)) {
								return;
							}
							mPriceListDTO = JSON.parseObject(responseStr, PriceListDTO.class);
							if (mPriceListDTO.getStatus_code() != 500) {
								saveCache(responseInfo.result,CACHE_PRICELIST_TYPE);
								Intent action = new Intent(ACTION_PRICELIST);
								AiShangApplication.this.sendBroadcast(action);
							}
						}

						@Override
						public void onStart() {
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Cache c = getCache(CACHE_PRICELIST_TYPE);
							if (c == null) {
								return;
							}
							mPriceListDTO = JSON.parseObject(c.getCache(),
									PriceListDTO.class);
							if (mPriceListDTO.getStatus_code() != 500) {
								Intent action = new Intent(ACTION_PRICELIST);
								AiShangApplication.this.sendBroadcast(action);
							}
						}
					});
		}
		return mPriceListDTO;
	}
	
	//获取最新版本
	public void checkUpdate() {
		http.send(HttpMethod.GET, chechUpdate + "?version=1",
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String t = responseInfo.result;
						if (TextUtils.isEmpty(t)|| t.toLowerCase(Locale.CHINA).equals("null")) {
							return;
						}
						Version version = null;
						try {
							version = JSON.parseObject(t, Version.class);
							if (TextUtils.isEmpty(version.getPath())) {
								return;
							}
						} catch (Exception e) {
							return;
						}
						Intent intent = new Intent(ACTION_IS_UPDATE);
						intent.putExtra("VERSION", version);
						sendBroadcast(intent);
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						System.out.println("checkUpdate: " + msg);
					}
				});
	}

	//获取发行库数据
	public void getHairStyleDTO(String[] tag, final String page) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sex", tag[0]);
		params.addBodyParameter("area", tag[1]);
		params.addBodyParameter("desc", tag[2]);
		params.addBodyParameter("height", tag[3]);
		params.addBodyParameter("page", page);
		params.addBodyParameter("hairstylistId",mPreferenceUtil.getString("hairstylist"));
		params.addBodyParameter("storeId", mPreferenceUtil.getString("store"));
		http.send(HttpMethod.POST, hairStyle, params,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String t = responseInfo.result;
						if (TextUtils.isEmpty(t)) {
							return;
						}
						mHairStyleDTO = JSON.parseObject(t, HairStyleDTO.class);
						if (page.equals("1")) {
							saveCache(t, CACHE_HAIRSTYLE_TYPE);
						}
						Intent action = new Intent(ACTION_HAIRSTYLE);
						AiShangApplication.this.sendBroadcast(action);
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Cache c = getCache(CACHE_HAIRSTYLE_TYPE);
						if (c != null) {
							mHairStyleDTO = JSON.parseObject(c.getCache(),
									HairStyleDTO.class);
							Intent action = new Intent(ACTION_HAIRSTYLE);
							AiShangApplication.this.sendBroadcast(action);
						}
					}
				});
	}

	//获取视频数据
	public void getAdVideoDTOList() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("hairstylistId",mPreferenceUtil.getString("hairstylist"));
		params.addBodyParameter("storeId", mPreferenceUtil.getString("store"));
		params.addBodyParameter("agentId", mPreferenceUtil.getString("agent"));
		http.send(HttpMethod.POST, adVideo, params,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String t = responseInfo.result;
						if (TextUtils.isEmpty(t)) {
							return;
						}
						mAdVideoDTO = JSON.parseObject(t, AdVideoDTO.class);
						Intent action = new Intent(ACTION_ADVIDEO);
						AiShangApplication.this.sendBroadcast(action);
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
	}

	//获取待机广告图片
	public AdPictureDTO getAdPictureDTOList() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("hairstylistId",mPreferenceUtil.getString("hairstylist"));
		params.addBodyParameter("storeId", mPreferenceUtil.getString("store"));
		params.addBodyParameter("agentId", mPreferenceUtil.getString("agent"));
		http.send(HttpMethod.POST, scrollPicture, params,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String t = responseInfo.result;
						if (TextUtils.isEmpty(t)) {
							return;
						}
						saveCache(responseInfo.result, CACHE_ADPICTURE_TYPE);
						mAdPictureDTO = JSON.parseObject(t, AdPictureDTO.class);
						Intent action = new Intent(ACTION_ADPICTURE);
						AiShangApplication.this.sendBroadcast(action);
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Cache c = getCache(CACHE_ADPICTURE_TYPE);
						if (c == null) {
							return;
						}
						mAdPictureDTO = JSON.parseObject(c.getCache(),
								AdPictureDTO.class);
						if (mAdPictureDTO.getStatus_code() != 500) {
							Intent action = new Intent(ACTION_ADPICTURE);
							AiShangApplication.this.sendBroadcast(action);
						}
					}
				});
		return mAdPictureDTO;
	}

	//添加待机视频播放次数
	public void watchVideo(int vid) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("id", vid + "");
		http.send(HttpMethod.GET, playAd, params,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println(responseInfo.result);
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
	}

	//添加待机图片播放次数
	public void playScrollPicture(int id) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", id + "");
		http.send(HttpMethod.POST, playScrollPicture, params,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println(responseInfo.result);
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});

	}
	
	public String getSoft() {
		return soft;
	}

	public void setSoft(String soft) {
		this.soft = soft;
	}

	public void saveCache(String cache, int type) {
		try {
			Cache c = new Cache();
			c.setType(type);
			c.setCache(cache);
			db.save(c);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public Cache getCache(int type) {
		List<Cache> list;
		try {
			list = db.findAll(Selector.from(Cache.class)
					.where("type", "=", type).orderBy("id", true));
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void checkADUpdate() {

	}

	public void installApk(File file) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("application/vnd.android.package-archive");
		intent.setData(Uri.fromFile(file));
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);

	}

	public void setPriceListDTO(PriceListDTO dto) {
		this.mPriceListDTO = dto;
	}

	public HairStyleDTO getHairStyleDTO() {
		return mHairStyleDTO;
	}

	public void setHairStyleDTO(HairStyleDTO dto) {
		this.mHairStyleDTO = dto;
	}

	public AdPictureDTO getAdPictureDTO() {
		return mAdPictureDTO;
	}

	public void setAdPictureDTO(AdPictureDTO mAdPictureDTO) {
		this.mAdPictureDTO = mAdPictureDTO;
	}

	public AdVideoDTO getAdVideoDTO() {
		return mAdVideoDTO;
	}

	public void setAdVideoDTO(AdVideoDTO mAdVideoDTO) {
		this.mAdVideoDTO = mAdVideoDTO;
	}

	public List<DownloadItem> getDownLoadList() {
		return downLoadList;
	}

	public void setDownLoadList(List<DownloadItem> downLoadList) {
		this.downLoadList = downLoadList;
	}

	public void saveVideo(Video video) {
		try {
			db.save(video);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public List<Video> getVideos() {
		List<Video> list = null;
		try {
			list = db.findAll(Selector.from(Video.class).orderBy("id", true));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list == null ? new ArrayList<Video>() : list;
	}

	public List<Video> getVideoByType(int type) {
		List<Video> list = null;
		try {
			list = db.findAll(Selector.from(Video.class).where("type", "=", type).orderBy("id", true));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void deleteVideo(int id) {
		try {
			db.delete(Video.class, WhereBuilder.b("vid", "=", id));
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public List<HairStyle> getHairInfo() {
		if (hairInfo == null) {
			return new ArrayList<HairStyle>();
		}
		return hairInfo;
	}

	public void setHairInfo(List<HairStyle> hairInfo) {
		this.hairInfo = hairInfo;
	}

	public HairStyle getShow() {
		return show;
	}

	public void setShow(HairStyle show) {
		this.show = show;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		Log.d("VideoPlayerActivity", "设置播放状态：" + isPlaying);
		this.isPlaying = isPlaying;
	}

	public int getAd_time() {
		return ad_time;
	}

	public void setAd_time(int ad_time) {
		this.ad_time = ad_time;
	}

	public boolean isPlayAD() {
		return isPlayAD;
	}

	public void setPlayAD(boolean isPlayAD) {
		Log.d("VideoPlayerActivity", "设置是否播放：" + isPlayAD);
		this.isPlayAD = isPlayAD;
	}
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public int getVersion() {
		int versionCode = 1;
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        versionCode = info.versionCode;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return versionCode;
	}

}
