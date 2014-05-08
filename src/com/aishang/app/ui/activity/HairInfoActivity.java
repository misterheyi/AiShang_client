package com.aishang.app.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.aishang.app.R;
import com.aishang.app.common.Constants;
import com.aishang.app.data.bean.HairStyle;
import com.aishang.app.ui.base.BaseActivity;
import com.lidroid.xutils.BitmapUtils;

public class HairInfoActivity extends BaseActivity implements Constants, OnItemClickListener {
	private ListView mListView;
	private ImageView mImageView;
	private List<HairStyle> hairStyles;
	private HairStyle show;
	private BitmapUtils bitmapUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hairinfo);
		mListView = (ListView) findViewById(R.id.list);
		mImageView = (ImageView) findViewById(R.id.show);
		hairStyles = getApp().getHairInfo();
		show = getApp().getShow();
		bitmapUtil = new BitmapUtils(this.getApplicationContext());
		bitmapUtil.display(mImageView, bce + show.getHairStyle_path());
		mListView.setAdapter(new MyHairListAdapter());
		mListView.setOnItemClickListener(this);
	}

	class MyHairListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return hairStyles.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_hairinfo, null);
			}
			ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
			bitmapUtil.display(imageView, bce + hairStyles.get(position).getHairStyle_path());
			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		bitmapUtil.display(mImageView, bce + hairStyles.get(position).getHairStyle_path());
	}

}
