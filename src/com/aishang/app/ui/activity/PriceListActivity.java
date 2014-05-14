package com.aishang.app.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.aishang.app.R;
import com.aishang.app.common.Constants;
import com.aishang.app.common.GestureListener;
import com.aishang.app.data.bean.AdPicture;
import com.aishang.app.data.bean.Users;
import com.aishang.app.data.dto.PriceListDTO;
import com.aishang.app.ui.base.BaseActivity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

public class PriceListActivity extends BaseActivity implements Constants {

	private Button b1, b2, b3, b4;
	private ViewFlipper viewFlipper;
	private ArrayList<View> pageViews;
	private PriceListDTO priceListDTO;
	private List<AdPicture> ad1;
	private List<AdPicture> ad2;
	private List<AdPicture> ad3;
	private List<Users> hairstylist;
	Animation leftInAnimation;
	Animation leftOutAnimation;
	Animation rightInAnimation;
	Animation rightOutAnimation;

	private static int CURRENT_INDEX = 0;

	private static final int TJ_INDEX = 0;
	private static final int CP_INDEX = 1;
	private static final int JM_INDEX = 2;
	private static final int FX_INDEX = 3;

	private LinearLayout slideBanner;
	
	private int index = 0;

	private int ad1_index = 0;
	private int ad2_index = 0;
	private int ad3_index = 0;
	private int hairstylist_index = 0;
	
	BitmapUtils bitmapUtil;
	private BroadcastReceiver update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pricelist);
		b1 = (Button) findViewById(R.id.b1);
		b2 = (Button) findViewById(R.id.b2);
		b3 = (Button) findViewById(R.id.b3);
		b4 = (Button) findViewById(R.id.b4);
		bitmapUtil = new BitmapUtils(getApp());
		viewFlipper = (ViewFlipper) this.findViewById(R.id.content);
		slideBanner = (LinearLayout)findViewById(R.id.slideBanner);
		pageViews = new ArrayList<View>();
		BitmapDisplayConfig bigPicDisplayConfig = new BitmapDisplayConfig();
		bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用,
													// 图片太大时容易OOM。
		bigPicDisplayConfig.setBitmapConfig(Bitmap.Config.RGB_565);
		bigPicDisplayConfig.setBitmapMaxSize(BitmapCommonUtils
				.getScreenSize(getApplicationContext()));
		bitmapUtil.configDefaultDisplayConfig(bigPicDisplayConfig);
		bitmapUtil.configMemoryCacheEnabled(true);
		bitmapUtil.configDiskCacheEnabled(true);
		IntentFilter filter = new IntentFilter(ACTION_PRICELIST);
		update = new Update();
		registerReceiver(update, filter);
		viewFlipper.setLongClickable(true);
		viewFlipper.setOnTouchListener(new MyGestureListener(this));
		priceListDTO = getApp().getPriceListDTO();
		leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
		leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
		rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
		rightOutAnimation = AnimationUtils
				.loadAnimation(this, R.anim.right_out);
		CURRENT_INDEX = 0;
	}

	class Update extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			priceListDTO = getApp().getPriceListDTO();
			if (priceListDTO != null) {
				ad1 = priceListDTO.getAd1();
				ad2 = priceListDTO.getAd2();
				ad3 = priceListDTO.getAd3();
				hairstylist = priceListDTO.getHairstylist();
				for (int i = 0; i < ad1.size(); i++) {
					viewFlipper.addView(getImageView(ad1.get(i).getAdPicture_path()));
				}
				showSlideBanner(ad1.size(),ad1_index);
				
				for (int i = 0; i < ad2.size(); i++) {
					getImageView(ad2.get(i).getAdPicture_path());
				}
				for (int i = 0; i < ad3.size(); i++) {
					getImageView(ad3.get(i).getAdPicture_path());
				}
				for (int i = 0; i < hairstylist.size(); i++) {
					getImageView(hairstylist.get(i).getUsers_face());
				}
			}
		}

	}

	private TextView getImageView(String path) {
		TextView imageView = new TextView(this);
		bitmapUtil.display(imageView, bce + path);
		return imageView;
	}

	private void showSlideBanner(int length,int index){
		slideBanner.removeAllViewsInLayout();
		for(int i=0 ; i<length ; i++){
			ImageView imageView = new ImageView(this);
			if(i==index){
				imageView.setBackgroundResource(R.drawable.btn_round2);
			}else{
				imageView.setBackgroundResource(R.drawable.btn_round1);
			}
			slideBanner.addView(imageView);
		}
	}
	
	/**
	 * 继承GestureListener，重写left和right方法
	 */
	private class MyGestureListener extends GestureListener implements
			Constants {
		public MyGestureListener(Context context) {
			super(context);
		}

		@Override
		public boolean left() {
			viewFlipper.setInAnimation(leftInAnimation);
			viewFlipper.setOutAnimation(leftOutAnimation);
			viewFlipper.showPrevious();// 向左滑动
			switch (index) {
			case TJ_INDEX: {
				ad1_index = ad1_index + 1;
				if(ad1_index >= ad1.size()){
					ad1_index = 0;
				}
				showSlideBanner(ad1.size(),ad1_index);
				break;
			}
			case CP_INDEX: {
				ad2_index = ad2_index + 1;
				if(ad2_index >= ad2.size()){
					ad2_index = 0;
				}
				showSlideBanner(ad2.size(),ad2_index);
				break;
			}
			case JM_INDEX: {
				ad3_index = ad3_index + 1;
				if(ad3_index >= ad3.size()){
					ad3_index = 0;
				}
				showSlideBanner(ad3.size(),ad3_index);
				break;
			}
			case FX_INDEX: {
				hairstylist_index = hairstylist_index + 1;
				if(hairstylist_index >= hairstylist.size()){
					hairstylist_index = 0;
				}
				showSlideBanner(hairstylist.size(),hairstylist_index);
				break;
			}
			}
			return super.left();
		}

		@Override
		public boolean right() {
			viewFlipper.setInAnimation(rightInAnimation);
			viewFlipper.setOutAnimation(rightOutAnimation);
			viewFlipper.showNext();// 向右滑动
			switch (index) {
			case TJ_INDEX: {
				ad1_index = ad1_index - 1;
				if(ad1_index < 0){
					ad1_index = ad1.size() - 1;
				}
				showSlideBanner(ad1.size(),ad1_index);
				break;
			}
			case CP_INDEX: {
				ad2_index = ad2_index - 1;
				if(ad2_index < 0){
					ad2_index = ad2.size() - 1;
				}
				showSlideBanner(ad2.size(),ad2_index);
				break;
			}
			case JM_INDEX: {
				ad3_index = ad3_index - 1;
				if(ad3_index < 0){
					ad3_index = ad3.size() - 1;
				}
				showSlideBanner(ad3.size(),ad3_index);
				break;
			}
			case FX_INDEX: {
				hairstylist_index = hairstylist_index - 1;
				if(hairstylist_index < 0){
					hairstylist_index = hairstylist.size() - 1;
				}
				showSlideBanner(hairstylist.size(),hairstylist_index);
				break;
			}
			}
			return super.right();
		}
	}

	@Override
	protected void onDestroy() {
		getApp().setPriceListDTO(null);
		unregisterReceiver(update);
		this.finish();
		super.onDestroy();
	}

	public void swipe(View v) {
		b1.setBackgroundResource(0);
		b2.setBackgroundResource(0);
		b3.setBackgroundResource(0);
		b1.setTextColor(Color.rgb(69, 64, 57));
		b4.setBackgroundResource(0);
		b4.setTextColor(Color.rgb(69, 64, 57));
		b2.setTextColor(Color.rgb(69, 64, 57));
		b3.setTextColor(Color.rgb(69, 64, 57));
		viewFlipper.removeAllViewsInLayout();
		switch (v.getId()) {
		case R.id.b1: {
			CURRENT_INDEX = TJ_INDEX;
			priceListDTO = getApp().getPriceListDTO();
			if(priceListDTO != null){
				for (int i = 0; i < ad1.size(); i++) {
					viewFlipper.addView(getImageView(ad1.get(i).getAdPicture_path()));
				}
			}
			ad1_index = 0;
			index = TJ_INDEX;
			showSlideBanner(ad1.size(),ad1_index);
			break;
		}
		case R.id.b2: {
			CURRENT_INDEX = CP_INDEX;
			priceListDTO = getApp().getPriceListDTO();
			if(priceListDTO != null){
				for (int i = 0; i < ad2.size(); i++) {
					viewFlipper.addView(getImageView(ad2.get(i).getAdPicture_path()));
				}
			}
			ad2_index = 0;
			index = CP_INDEX;
			showSlideBanner(ad2.size(),ad2_index);
			break;
		}
		case R.id.b3: {
			CURRENT_INDEX = JM_INDEX;
			priceListDTO = getApp().getPriceListDTO();
			if(priceListDTO != null){
				for (int i = 0; i < ad3.size(); i++) {
					viewFlipper.addView(getImageView(ad3.get(i).getAdPicture_path()));
				}
			}
			ad3_index = 0;
			index = JM_INDEX;
			showSlideBanner(ad3.size(),ad3_index);
			break;
		}
		case R.id.b4: {
			CURRENT_INDEX = FX_INDEX;
			priceListDTO = getApp().getPriceListDTO();
			if(priceListDTO != null){
				for (int i = 0; i < hairstylist.size(); i++) {
					viewFlipper.addView(getImageView(hairstylist.get(i).getUsers_face()));
				}
			}
			hairstylist_index = 0;
			index = FX_INDEX;
			showSlideBanner(hairstylist.size(),hairstylist_index);
			break;
		}
		}
	}

}
