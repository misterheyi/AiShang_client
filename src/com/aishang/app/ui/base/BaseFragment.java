package com.aishang.app.ui.base;

import android.app.Fragment;
import android.os.Bundle;

import com.aishang.app.AiShangApplication;
import com.aishang.app.common.Constants;

public class BaseFragment extends Fragment implements Constants {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public AiShangApplication getApp() {
		return (AiShangApplication) getActivity().getApplication();
	}

	public void adTimeZero() {
		System.out.println("AD Time Zero");
		getApp().setAd_time(0);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

}
