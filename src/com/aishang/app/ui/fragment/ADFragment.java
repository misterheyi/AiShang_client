package com.aishang.app.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aishang.app.R;
import com.aishang.app.data.bean.AdPicture;
import com.aishang.app.data.dto.PriceListDTO;
import com.aishang.app.ui.base.BaseFragment;
import com.lidroid.xutils.BitmapUtils;

public class ADFragment extends BaseFragment {

	private ViewPager viewPager;
	private MyPagerAdapter adapter;
	private List<View> views;

	private Handler handler;

	private BroadcastReceiver update;
	private PriceListDTO priceListDTO;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_pager, null);
		priceListDTO = getApp().getPriceListDTO();
		viewPager = (ViewPager) v.findViewById(R.id.pager);

		if (priceListDTO != null) {
			initPager();
		}

		IntentFilter filter = new IntentFilter(ACTION_PRICELIST);
		update = new Update();
		getActivity().registerReceiver(update, filter);

		return v;
	}

	public void initPager() {
		List<AdPicture> ad = priceListDTO.getAd4();
		views = new ArrayList<View>();
		for (AdPicture bitmap : ad) {
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_ad, null);
			view.setTag(bitmap);
			views.add(view);
		}
		adapter = new MyPagerAdapter(views);
		viewPager.setAdapter(adapter);
		handler = new Handler();
		handler.post(autoChangeAd);
	}

	Runnable autoChangeAd = new Runnable() {

		@Override
		public void run() {
			int index = viewPager.getCurrentItem();
			index++;
			if (index >= views.size()) {
				index = 0;
			}
			viewPager.setCurrentItem(index);
			handler.postDelayed(autoChangeAd, 3000);
		}
	};

	@Override
	public void onDestroy() {
		handler.removeCallbacks(autoChangeAd);
		super.onDestroy();
	};

	class MyPagerAdapter extends PagerAdapter {

		private List<View> mViews;

		public MyPagerAdapter(List<View> mViews) {
			this.mViews = mViews;
		}

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = mViews.get(position);
			View view = v.findViewById(R.id.view);
			AdPicture id = (AdPicture) v.getTag();
			BitmapUtils finalBitmap = new BitmapUtils(getApp());
			finalBitmap.display(view, bce + id.getAdPicture_path());
			container.addView(v);
			return v;
		}

	}

	class Update extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			priceListDTO = getApp().getPriceListDTO();
			if (priceListDTO == null) {
				return;
			}
			initPager();
		}

	}
}
