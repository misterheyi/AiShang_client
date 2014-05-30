package com.aishang.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.exception.DbException;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
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
import com.aishang.app.data.dto.AdVideoDTO;
import com.aishang.app.data.dto.HairStyleDTO;
import com.aishang.app.data.dto.PriceListDTO;
import com.aishang.app.data.dto.ScrollPictureDTO;
import com.aishang.app.db.Video;
import com.aishang.app.download.DownloadItem;
import com.alibaba.fastjson.JSON;

public class AiShangApplication extends Application implements Constants {

	private PriceListDTO mPriceListDTO;
	private HairStyleDTO mHairStyleDTO;
	private AdVideoDTO mAdVideoDTO;
	private ScrollPictureDTO mAdPictureDTO;
	private FinalDb db = null;
	private PreferenceUtil mPreferenceUtil;

	private List<DownloadItem> downLoadList;
	private List<HairStyle> hairInfo;
	private HairStyle show;
	private FinalHttp http = new FinalHttp();
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
		db = FinalDb.create(this);
		downLoadList = new ArrayList<DownloadItem>();
		mPreferenceUtil = new PreferenceUtil(getApplicationContext());
	}

	//获取店内推荐数据
	public PriceListDTO getPriceListDTO() {
		if (mPriceListDTO == null || mPriceListDTO.getStatus_code() == 500) {
			AjaxParams params = new AjaxParams();
			params.put("hairstylistId",mPreferenceUtil.getString("hairstylist"));
			params.put("storeId",mPreferenceUtil.getString("store"));
			params.put("agentId",mPreferenceUtil.getString("agent"));
			http.post(priceList, params,
					new AjaxCallBack<String>() {
						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
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

						@Override
						public void onSuccess(String responseStr) {
							if (TextUtils.isEmpty(responseStr)) {
								return;
							}
							mPriceListDTO = JSON.parseObject(responseStr, PriceListDTO.class);
							if (mPriceListDTO.getStatus_code() != 500) {
								saveCache(responseStr,CACHE_PRICELIST_TYPE);
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
		AjaxParams params = new AjaxParams();
		params.put("version","1");
		http.get(chechUpdate,params,
				new AjaxCallBack<String>() {

					@Override
					public void onSuccess(String responseStr) {
						if (TextUtils.isEmpty(responseStr)|| responseStr.toLowerCase(Locale.CHINA).equals("null")) {
							return;
						}
						Version version = null;
						try {
							version = JSON.parseObject(responseStr, Version.class);
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
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						System.out.println("checkUpdate: " + strMsg);
					}
				});
	}

	//获取发行库数据
	public void getHairStyleDTO(String[] tag, final String page) {
		AjaxParams params = new AjaxParams();
		params.put("sex", tag[0]);
		params.put("area", tag[1]);
		params.put("desc", tag[2]);
		params.put("height", tag[3]);
		params.put("page", page);
		params.put("hairstylistId",mPreferenceUtil.getString("hairstylist"));
		params.put("storeId", mPreferenceUtil.getString("store"));
		http.post(hairStyle, params,
				new AjaxCallBack<String>() {

					@Override
					public void onSuccess(String responseStr) {
						if (TextUtils.isEmpty(responseStr)) {
							return;
						}
						mHairStyleDTO = JSON.parseObject(responseStr, HairStyleDTO.class);
						if (page.equals("1")) {
							saveCache(responseStr, CACHE_HAIRSTYLE_TYPE);
						}
						Intent action = new Intent(ACTION_HAIRSTYLE);
						AiShangApplication.this.sendBroadcast(action);
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
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
		AjaxParams params = new AjaxParams();
		params.put("hairstylistId",mPreferenceUtil.getString("hairstylist"));
		params.put("storeId", mPreferenceUtil.getString("store"));
		params.put("agentId", mPreferenceUtil.getString("agent"));
		http.post(adVideo, params,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String responseStr) {
						if (TextUtils.isEmpty(responseStr)) {
							return;
						}
						mAdVideoDTO = JSON.parseObject(responseStr, AdVideoDTO.class);
						Intent action = new Intent(ACTION_ADVIDEO);
						AiShangApplication.this.sendBroadcast(action);
					}

					@Override
					public void onStart() {
					
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
					}
				});
	}

	//获取待机广告图片
	public ScrollPictureDTO getAdPictureDTOList() {
		AjaxParams params = new AjaxParams();
		params.put("hairstylistId",mPreferenceUtil.getString("hairstylist"));
		params.put("storeId", mPreferenceUtil.getString("store"));
		params.put("agentId", mPreferenceUtil.getString("agent"));
		http.post(scrollPicture, params,
				new AjaxCallBack<String>() {

					@Override
					public void onSuccess(String responseStr) {
						if (TextUtils.isEmpty(responseStr)) {
							return;
						}
						saveCache(responseStr, CACHE_ADPICTURE_TYPE);
						mAdPictureDTO = JSON.parseObject(responseStr, ScrollPictureDTO.class);
						Intent action = new Intent(ACTION_ADPICTURE);
						AiShangApplication.this.sendBroadcast(action);
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						Cache c = getCache(CACHE_ADPICTURE_TYPE);
						if (c == null) {
							return;
						}
						mAdPictureDTO = JSON.parseObject(c.getCache(),
								ScrollPictureDTO.class);
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
		AjaxParams params = new AjaxParams();
		params.put("id", vid+"");
		http.get(playAd, params,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String responseStr) {
						System.out.println("添加播放次数成功");
					}
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						System.out.println("添加播放次数失败");
					}
				});
	}

	//添加待机图片播放次数
	public void playScrollPicture(int id) {
		AjaxParams params = new AjaxParams();
		params.put("id", id + "");
		http.get(playScrollPicture, params,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String responseStr) {
						System.out.println("添加图片播放次数成功");
					}
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						System.out.println("添加图片播放次数失败");
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
			list = db.findAllByWhere(Cache.class, "type="+type, "id desc");
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

	public ScrollPictureDTO getAdPictureDTO() {
		return mAdPictureDTO;
	}

	public void setAdPictureDTO(ScrollPictureDTO mAdPictureDTO) {
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
			Log.d("AiShang", "保存视频：" + video.getVid()+" 视频类型："+video.getGroupId());
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public List<Video> getVideos() {
		List<Video> list = null;
		try {
			list = db.findAll(Video.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Video> getVideoByType(int type) {
		List<Video> list = new ArrayList<Video>();
		try {
			list = db.findAllByWhere(Video.class,"type="+type,"groupId desc");
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list==null?new ArrayList<Video>():list;
	}

	public void deleteVideo(int id) {
		try {
			db.deleteByWhere(Video.class, "vid="+id);
			Log.d("AiShang", "删除视频：" + id);
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
		Log.d("AiShang", "设置播放状态：" + isPlaying);
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
		Log.d("AiShang", "设置是否播放：" + isPlayAD);
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
