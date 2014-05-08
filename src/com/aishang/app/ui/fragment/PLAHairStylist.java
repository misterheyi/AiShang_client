package com.aishang.app.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aishang.app.R;
import com.aishang.app.data.bean.Users;
import com.aishang.app.data.dto.PriceListDTO;
import com.aishang.app.ui.base.BaseFragment;
import com.lidroid.xutils.BitmapUtils;

public class PLAHairStylist extends BaseFragment {
	private View loading;
	private PriceListDTO priceListDTO;
	private BroadcastReceiver update;
	private LinearLayout layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list_h, null);
		loading = v.findViewById(R.id.loading);
		layout = (LinearLayout) v.findViewById(R.id.layout);
		display();
		IntentFilter filter = new IntentFilter(ACTION_PRICELIST);
		update = new Update();
		getActivity().registerReceiver(update, filter);
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(update);
	}

	public void display() {
		priceListDTO = getApp().getPriceListDTO();
		if (priceListDTO != null) {
			loading.setVisibility(View.GONE);
			for (Users p : priceListDTO.getHairstylist()) {
				View vv = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_list, null);
				View img = vv.findViewById(R.id.img);
				BitmapUtils finalBitmap = new BitmapUtils(getApp());
				finalBitmap.display(img, bce + p.getUsers_face());
				layout.addView(vv);
			}
		}
	}

	class Update extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			display();
/*			priceListDTO = getApp().getPriceListDTO();
			if (priceListDTO == null) {
				return;
			}
			loading.setVisibility(View.GONE);
			for (Users p : priceListDTO.getHairstylist()) {
				View vv = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_list, null);
				View img = vv.findViewById(R.id.img);
				FinalBitmap finalBitmap = FinalBitmap.create(getApp());
				finalBitmap.display(img, bce + p.getUsers_face());
				layout.addView(vv);
			}*/
		}

	}

}