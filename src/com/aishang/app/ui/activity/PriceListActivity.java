package com.aishang.app.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;

import com.aishang.app.R;
import com.aishang.app.common.Constants;
import com.aishang.app.ui.base.BaseActivity;
import com.aishang.app.ui.fragment.PLADianeiChuxiao;
import com.aishang.app.ui.fragment.PLAHairStylist;
import com.aishang.app.ui.fragment.PLAJiamuBiao;
import com.aishang.app.ui.fragment.PLATeseShangpin;

public class PriceListActivity extends BaseActivity implements Constants {

	private Button b1, b2, b3, b4;
	private Fragment mPLADianeiChuxiao;
	private Fragment mPLATeseShangpin;
	private Fragment mPLAJiamuBiao;
	private Fragment mPLAHairStylist;
	// private Fragment mAdFragment;
	private Fragment mContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pricelist);
		b1 = (Button) findViewById(R.id.b1);
		b2 = (Button) findViewById(R.id.b2);
		b3 = (Button) findViewById(R.id.b3);
		b4 = (Button) findViewById(R.id.b4);
		mContent = new Fragment();
		mPLADianeiChuxiao = new PLADianeiChuxiao();
		mPLATeseShangpin = new PLATeseShangpin();
		mPLAJiamuBiao = new PLAJiamuBiao();
		mPLAHairStylist = new PLAHairStylist();
		swipeFragment(mPLADianeiChuxiao);
	}

	private void swipeFragment(Fragment to) {
		if (!to.equals(mContent)) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			if (!to.isAdded()) {
				transaction.hide(mContent).add(R.id.content, to).commit();
			} else {
				transaction.hide(mContent).show(to).commit();
			}
			mContent = to;
		}
	}

	@Override
	protected void onDestroy() {
		getApp().setPriceListDTO(null);
		this.finish();
		super.onDestroy();
	}

	public void swipe(View v) {
		b1.setBackgroundResource(0);
		b2.setBackgroundResource(0);
		b3.setBackgroundResource(0);
		b4.setBackgroundResource(0);
		b1.setTextColor(Color.rgb(69, 64, 57));
		b4.setTextColor(Color.rgb(69, 64, 57));
		b2.setTextColor(Color.rgb(69, 64, 57));
		b3.setTextColor(Color.rgb(69, 64, 57));
		TextPaint tp1 = b1.getPaint(); 
		tp1.setFakeBoldText(false);
		TextPaint tp2 = b2.getPaint(); 
		tp2.setFakeBoldText(false); 
		TextPaint tp3 = b3.getPaint(); 
		tp3.setFakeBoldText(false); 
		TextPaint tp4 = b4.getPaint(); 
		tp4.setFakeBoldText(false); 
		switch (v.getId()) {
		case R.id.b1: {
			TextPaint tp = b1.getPaint(); 
			tp.setFakeBoldText(true); 
			b1.setTextColor(Color.WHITE);
			swipeFragment(mPLADianeiChuxiao);
			break;
		}
		case R.id.b2: {
			TextPaint tp = b2.getPaint(); 
			tp.setFakeBoldText(true); 
			b2.setTextColor(Color.WHITE);
			swipeFragment(mPLATeseShangpin);
			break;
		}
		case R.id.b3: {
			TextPaint tp = b3.getPaint(); 
			tp.setFakeBoldText(true); 
			b3.setTextColor(Color.WHITE);
			swipeFragment(mPLAJiamuBiao);
			break;
		}
		case R.id.b4: {
			TextPaint tp = b4.getPaint(); 
			tp.setFakeBoldText(true); 
			b4.setTextColor(Color.WHITE);
			swipeFragment(mPLAHairStylist);
			break;
		}
		}
	}

}
