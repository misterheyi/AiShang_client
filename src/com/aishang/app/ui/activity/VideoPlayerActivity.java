package com.aishang.app.ui.activity;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.aishang.app.R;
import com.aishang.app.common.Constants;
import com.aishang.app.data.bean.AdPicture;
import com.aishang.app.data.dto.AdPictureDTO;
import com.aishang.app.db.Video;
import com.aishang.app.download.DownloadItem;
import com.aishang.app.service.DownloadService;
import com.aishang.app.ui.base.BaseActivity;
import com.lidroid.xutils.BitmapUtils;

public class VideoPlayerActivity extends BaseActivity implements Constants, OnCompletionListener {

	private VideoView mVideoView;
	private ImageView mImageView;

	private int status;
	private boolean isClick;
	private int type;

	private List<Video> videos;
	private List<AdPicture> pictures;

	private int playPosition = 0;
	private int picturePosition = 0;

	private int now_play_stats;
	private static final int VIDEO = 0;
	private static final int PICTURE = 1;

	private BitmapUtils bitmapUtil;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		getApp().setPlaying(true);

		isClick = getIntent().getBooleanExtra("isClick", false);
		status = getIntent().getIntExtra("status", 0);
		type = getIntent().getIntExtra("type", 0);

		mVideoView = (VideoView) findViewById(R.id.videoView);
		mImageView = (ImageView) findViewById(R.id.adView);

		bitmapUtil= new BitmapUtils(getApplicationContext());
		mVideoView.setOnCompletionListener(this);
		mVideoView.requestFocus();
		videos = getApp().getVideoByType(type);

		if (videos.size() == 0) {
			if (isClick) {
				startAction();
			}
			return;
		}

		AdPictureDTO priceListDTO = getApp().getAdPictureDTOList();
		if (priceListDTO != null) {
			pictures = priceListDTO.getAd4();

		}

		play();

		handler = new Handler();

	}

	public void play() {
		File file = new File(videos.get(playPosition).getFilePath());
		if (!file.exists()) {
			if (isClick) {
				startAction();
			} else {
				finish();
				startDownload(videos.get(playPosition));
			}
		} else {
			mVideoView.setVideoPath(videos.get(playPosition).getFilePath());
			mVideoView.start();
			getApp().watchVideo(videos.get(playPosition).getVid());
		}
		now_play_stats = VIDEO;
		mImageView.setVisibility(View.GONE);
		mVideoView.setVisibility(View.VISIBLE);
	}

	public void playPicture() {
		System.out.println("playPicture" + pictures);
		if (pictures == null || pictures.isEmpty()) {
			replayVideo();
			AdPictureDTO priceListDTO = getApp().getAdPictureDTOList();
			if (priceListDTO != null) {
				pictures = getApp().getPriceListDTO().getAd4();
			}
			return;
		}
		videos = getApp().getVideoByType(type);
		picturePosition++;
		if (picturePosition >= pictures.size()) {
			picturePosition = 0;
		}

		AdPicture adPicture = pictures.get(picturePosition);
		getApp().playScrollPicture(adPicture.getAdPicture_id());
		bitmapUtil.display(mImageView, bce + adPicture.getAdPicture_path());
		now_play_stats = PICTURE;
		mImageView.setVisibility(View.VISIBLE);
		mVideoView.setVisibility(View.GONE);

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				replayVideo();
			}
		}, 15000);
	}

	public void startDownload(Video adVideo) {
		getApp().deleteVideo(adVideo.getVid());
		DownloadItem downloadItem = new DownloadItem(DOWNLOAD_PATH_VIDEO);
		downloadItem.setDownloadUrl(adVideo.getPath());
		downloadItem.arg1 = adVideo.getVid();
		downloadItem.arg2 = adVideo.getType();
		Intent i = new Intent(getApplicationContext(), DownloadService.class);
		i.putExtra(SERVICE_TYPE_NAME, START_DOWNLOAD);
		i.putExtra(DOWNLOAD_INTENT, downloadItem);
		startService(i);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		switch (now_play_stats) {
		case VIDEO:
			if (isClick) {
				getApp().setPlayAD(false);
				startAction();
			} else
				playPicture();
			break;
		case PICTURE:
			replayVideo();
			break;
		}

	}

	public void replayVideo() {
		if (isClick) {
			getApp().setPlayAD(false);
			startAction();
		} else {
			playPosition++;
			if (playPosition >= videos.size()) {
				playPosition = 0;
			}
			if (getApp().isPlayAD()) {
				playPosition = 0;
				videos = getApp().getVideoByType(1);
			}
			play();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getApp().isPlayAD()) {
				// startSoft(getApp().getSoft(), this);
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		getApp().setPlaying(false);
		getApp().setPriceListDTO(null);
		super.onDestroy();
	}

	public void close(View v) {
		if (!isClick) {
			mVideoView.stopPlayback();
			finish();
		}
	}

	public void startAction() {
		switch (status) {
		case 1:
			startSoft("com.vst_hd.live", this);
			getApp().setSoft("com.vst_hd.live");
			break;
		case 2:
			startSoft("com.togic.livevideo", this);
			getApp().setSoft("com.togic.livevideo");
			break;
		}
		finish();
	}

}