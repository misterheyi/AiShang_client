package com.aishang.app.ui.activity;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

	private static final int CHUXIAO = 0;
	private static final int CHANPING = 1;
	private static final int JIAMU = 2;
	private static final int FAXINGSHI = 3;

	private TextView imageView;

	private PriceListDTO priceListDTO;
	private List<AdPicture> ad1;
	private List<AdPicture> ad2;
	private List<AdPicture> ad3;
	private List<Users> hairstylist;

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
		slideBanner = (LinearLayout)findViewById(R.id.slideBanner);
		b1 = (Button) findViewById(R.id.b1);
		b2 = (Button) findViewById(R.id.b2);
		b3 = (Button) findViewById(R.id.b3);
		b4 = (Button) findViewById(R.id.b4);
		bitmapUtil = new BitmapUtils(getApp());
		// mAdFragment = new ADFragment();
		imageView = (TextView) this.findViewById(R.id.content);
		BitmapDisplayConfig bigPicDisplayConfig = new BitmapDisplayConfig();
        bigPicDisplayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用, 图片太大时容易OOM。
        bigPicDisplayConfig.setBitmapConfig(Bitmap.Config.RGB_565);
        bigPicDisplayConfig.setBitmapMaxSize(BitmapCommonUtils.getScreenSize(getApplicationContext()));
        bitmapUtil.configDefaultDisplayConfig(bigPicDisplayConfig);
        bitmapUtil.configMemoryCacheEnabled(true);
        bitmapUtil.configDiskCacheEnabled(true);
        // swipeFragment(mPLADianeiChuxiao);
		// setLongClickable是必须的
		IntentFilter filter = new IntentFilter(ACTION_PRICELIST);
		update = new Update();
		registerReceiver(update, filter);
		imageView.setLongClickable(true);
		imageView.setOnTouchListener(new MyGestureListener(this));
		priceListDTO = getApp().getPriceListDTO();
		index = CHUXIAO;
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
	
	class Update extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			priceListDTO = getApp().getPriceListDTO();
			if (priceListDTO != null) {
				ad1 = priceListDTO.getAd1();
				bitmapUtil.display(imageView, bce + ad1.get(ad1_index).getAdPicture_path());
				showSlideBanner(ad1.size(),ad1_index);
				ad2 = priceListDTO.getAd2();
				ad3 = priceListDTO.getAd3();
				hairstylist = priceListDTO.getHairstylist();
			}
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
			switch (index) {
			case CHUXIAO: {
				ad1_index = ad1_index + 1;
				if(ad1_index >= ad1.size()){
					ad1_index = 0;
				}
				bitmapUtil.display(imageView, bce + ad1.get(ad1_index).getAdPicture_path());
				showSlideBanner(ad1.size(),ad1_index);
				break;
			}
			case CHANPING: {
				ad2_index = ad2_index + 1;
				if(ad2_index >= ad2.size()){
					ad2_index = 0;
				}
				bitmapUtil.display(imageView, bce + ad2.get(ad2_index).getAdPicture_path());
				showSlideBanner(ad2.size(),ad2_index);
				break;
			}
			case JIAMU: {
				ad3_index = ad3_index + 1;
				if(ad3_index >= ad3.size()){
					ad3_index = 0;
				}
				bitmapUtil.display(imageView, bce + ad3.get(ad3_index).getAdPicture_path());
				showSlideBanner(ad3.size(),ad3_index);
				break;
			}
			case FAXINGSHI: {
				hairstylist_index = hairstylist_index + 1;
				if(hairstylist_index >= hairstylist.size()){
					hairstylist_index = 0;
				}
				bitmapUtil.display(imageView,bce + hairstylist.get(hairstylist_index).getUsers_face());
				showSlideBanner(hairstylist.size(),hairstylist_index);
				break;
			}
			}
			return super.left();
		}

		@Override
		public boolean right() {
			switch (index) {
			case CHUXIAO: {
				ad1_index = ad1_index - 1;
				if(ad1_index < 0){
					ad1_index = ad1.size() - 1;
				}
				bitmapUtil.display(imageView, bce + ad1.get(ad1_index).getAdPicture_path());
				showSlideBanner(ad1.size(),ad1_index);
				break;
			}
			case CHANPING: {
				ad2_index = ad2_index - 1;
				if(ad2_index < 0){
					ad2_index = ad2.size() - 1;
				}
				bitmapUtil.display(imageView, bce + ad2.get(ad2_index).getAdPicture_path());
				showSlideBanner(ad2.size(),ad2_index);
				break;
			}
			case JIAMU: {
				ad3_index = ad3_index - 1;
				if(ad3_index < 0){
					ad3_index = ad3.size() - 1;
				}
				bitmapUtil.display(imageView, bce + ad3.get(ad3_index).getAdPicture_path());
				showSlideBanner(ad3.size(),ad3_index);
				break;
			}
			case FAXINGSHI: {
				hairstylist_index = hairstylist_index - 1;
				if(hairstylist_index < 0){
					hairstylist_index = hairstylist.size() - 1;
				}
				bitmapUtil.display(imageView, bce + hairstylist.get(hairstylist_index).getUsers_face());
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

		switch (v.getId()) {
		case R.id.b1:
			b1.setBackgroundResource(R.drawable.btn_black);
			b1.setTextColor(Color.WHITE);
			ad1_index = 0;
			index = CHUXIAO;
			bitmapUtil.display(imageView, bce + ad1.get(ad1_index).getAdPicture_path());
			showSlideBanner(ad1.size(),ad1_index);
			break;
		case R.id.b2:
			b2.setBackgroundResource(R.drawable.btn_black);
			b2.setTextColor(Color.WHITE);
			ad2_index = 0;
			index = CHANPING;
			bitmapUtil.display(imageView, bce + ad2.get(ad2_index).getAdPicture_path());
			showSlideBanner(ad2.size(),ad2_index);
			break;
		case R.id.b3:
			b3.setBackgroundResource(R.drawable.btn_black);
			b3.setTextColor(Color.WHITE);
			ad3_index = 0;
			index = JIAMU;
			bitmapUtil.display(imageView, bce + ad3.get(ad3_index).getAdPicture_path());
			showSlideBanner(ad3.size(),ad3_index);
			break;
		case R.id.b4:
			b4.setBackgroundResource(R.drawable.btn_black);
			b4.setTextColor(Color.WHITE);
			hairstylist_index = 0;
			index = FAXINGSHI;
			bitmapUtil.display(imageView, bce + hairstylist.get(hairstylist_index).getUsers_face());
			showSlideBanner(ad2.size(),hairstylist_index);
			break;
		}
	}

}