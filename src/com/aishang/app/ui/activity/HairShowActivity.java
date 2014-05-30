package com.aishang.app.ui.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.aishang.app.NetworkDetector;
import com.aishang.app.R;
import com.aishang.app.common.Constants;
import com.aishang.app.common.DisplayUtils;
import com.aishang.app.data.bean.HairStyle;
import com.aishang.app.ui.base.BaseActivity;

public class HairShowActivity extends BaseActivity implements Constants, OnScrollListener, OnItemClickListener {

	private PopupWindow popupWindow;
	private View popupLayout;
	private LinearLayout layout_sex, layout_desc, layout_area, layout_height;
	private Button button1, button2, button3, button4;
	private GridView gridView;
	private GridAdapter adapter;
	private BroadcastReceiver update;
	private View loading;
	// PopupWindow
	private String[] str_sex = { "", "女性", "男性" };
	private String[] str_area = { "", "亚洲", "欧洲" };
	private String[] str_desc = { "", "活泼可爱", "端庄典雅", "优雅时尚", "新锐个性", "盘发扎发" };
	private String[] str_height = { "", "长", "中", "短" };
	private String[] str_tag = { "", "", "", "" };
	private String[] str_tag2 = { "", "", "", "" };
	private String[] str_tag_show = { "不限", "不限", "不限", "不限" };
	private List<Button> btns_sex, btns_desc, btns_area, btns_height;
	private OnClickListener mOnSexBtnClick, mOnDescBtnClick, mOnAreaBtnClick, mOnHightBtnClick;

	private int page = 1;
	private boolean isRefresh;
	private boolean isRefreshable = true;
	private boolean isNew;
	private List<HairStyle> hairStyles;
	private FinalBitmap fb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hairshow);
		loading = findViewById(R.id.loading);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		preparePopupWindow();
		preparePopupButton();
		fb = FinalBitmap.create(getApp());//初始化FinalBitmap模块
		hairStyles = new ArrayList<HairStyle>();
		gridView = (GridView) findViewById(R.id.grid);
		gridView.setOnScrollListener(this);
		gridView.setOnItemClickListener(this);
		adapter = new GridAdapter();
		gridView.setAdapter(adapter);

		IntentFilter filter = new IntentFilter(ACTION_HAIRSTYLE);
		update = new Update();
		registerReceiver(update, filter);

		getHairStyles();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(update);
	}

	public void onClicked(View v) {
		switch (v.getId()) {
		case R.id.filter:
			popupWindow.showAsDropDown(findViewById(R.id.title));
			break;
		}
	}

	@SuppressWarnings("deprecation")
	public void preparePopupWindow() {
		popupLayout = LayoutInflater.from(this).inflate(R.layout.view_popup, null);
		popupWindow = new PopupWindow(popupLayout, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
	}

	/**
	 * 加载PopupButton
	 */
	public void preparePopupButton() {
		layout_sex = (LinearLayout) popupLayout.findViewById(R.id.layout_sex);
		layout_desc = (LinearLayout) popupLayout.findViewById(R.id.layout_desc);
		layout_area = (LinearLayout) popupLayout.findViewById(R.id.layout_area);
		layout_height = (LinearLayout) popupLayout.findViewById(R.id.layout_height);

		btns_sex = new ArrayList<Button>();
		btns_desc = new ArrayList<Button>();
		btns_area = new ArrayList<Button>();
		btns_height = new ArrayList<Button>();
		mOnSexBtnClick = new OnPopupBtnClick(0);
		mOnAreaBtnClick = new OnPopupBtnClick(1);
		mOnDescBtnClick = new OnPopupBtnClick(2);
		mOnHightBtnClick = new OnPopupBtnClick(3);

		addButton(str_area, btns_area, layout_area, mOnAreaBtnClick);
		addButton(str_desc, btns_desc, layout_desc, mOnDescBtnClick);
		addButton(str_height, btns_height, layout_height, mOnHightBtnClick);
		addButton(str_sex, btns_sex, layout_sex, mOnSexBtnClick);

		Button search = (Button) popupLayout.findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				button1.setText(str_tag_show[0]);
				button2.setText(str_tag_show[1]);
				button3.setText(str_tag_show[2]);
				button4.setText(str_tag_show[3]);
				popupWindow.dismiss();
				page = 1;
				isRefreshable = true;
				for (int i = 0; i < str_tag2.length; i++) {
					str_tag[i] = str_tag2[i];
				}
				isNew = true;
				getHairStyles();

				showLoadingDialog();
			}
		});
	}

	private void addButton(String[] strs, List<Button> btns, LinearLayout layout, OnClickListener listener) {
		for (String str : strs) {
			Button button = new Button(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(30, 0, 0, 0);
			button.setLayoutParams(params);
			if ("".equals(str)) {
				button.setText("不限");
				button.setTextColor(Color.WHITE);
				button.setBackgroundResource(R.drawable.btn_tags_selected);
			} else {
				button.setText(str);
				button.setBackgroundResource(R.drawable.btn_tags_normal);
			}
			button.setTag(str);
			button.setOnClickListener(listener);
			layout.addView(button);
			btns.add(button);
		}
	}

	class OnPopupBtnClick implements OnClickListener {
		int list_tag = 0;

		public OnPopupBtnClick(int tag) {
			list_tag = tag;
		}

		@Override
		public void onClick(View v) {
			String tag = (String) v.getTag();
			Button button = (Button) v;
			switch (list_tag) {
			case 0:
				initButtonStatus(btns_sex);
				break;
			case 1:
				initButtonStatus(btns_area);
				break;
			case 2:
				initButtonStatus(btns_desc);
				break;
			case 3:
				initButtonStatus(btns_height);
				break;

			}
			button.setBackgroundResource(R.drawable.btn_tags_selected);
			button.setTextColor(Color.WHITE);

			str_tag2[list_tag] = tag;
			str_tag_show[list_tag] = button.getText().toString();
		}
	}

	public void initButtonStatus(List<Button> list) {
		for (Button button : list) {
			button.setBackgroundResource(R.drawable.btn_tags_normal);
			button.setTextColor(Color.BLACK);
		}
	}

	class GridAdapter extends BaseAdapter {

		public GridAdapter() {

		}

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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_hair, null);
				image = (ImageView) convertView.findViewById(R.id.imgView);
				convertView.setTag(image);
			} else {
				image = (ImageView) convertView.getTag();
			}
			fb.display(image, bce + hairStyles.get(position).getHairStyle_path(),DisplayUtils.px2dip(getApplicationContext(), (float)218),DisplayUtils.px2dip(getApp(), (float)298));
			return convertView;
		}
	}

	class Update extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			isRefresh = false;
			loading.setVisibility(View.GONE);
			List<HairStyle> list = getApp().getHairStyleDTO().getHairStyles();
			if (list != null && list.size() > 0) {
				if (isNew) {
					dialog.cancel();
					hairStyles.clear();
					adapter.notifyDataSetChanged();
					isNew = false;
				}
				hairStyles.addAll(list);
				adapter.notifyDataSetChanged();
				page++;
				if (list.size() < 20) {
					isRefreshable = false;
					Toast.makeText(getApplicationContext(), "加载完成", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "加载" + list.size() + "条新数据", Toast.LENGTH_SHORT).show();
				}
			} else {
				if (isNew) {
					dialog.cancel();
					hairStyles.clear();
					adapter.notifyDataSetChanged();
					isNew = false;
				}
				Toast.makeText(getApplicationContext(), "没有数据", Toast.LENGTH_SHORT).show();
			}

		}

	}

	public void getHairStyles() {
		isRefresh = true;
		getApp().getHairStyleDTO(str_tag, page + "");
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	private boolean isToast = false;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (totalItemCount <= 0) {
			return;
		}
		if (firstVisibleItem + visibleItemCount >= totalItemCount) {
			if (!isRefresh && isRefreshable) {
				if (!NetworkDetector.detect(HairShowActivity.this)) {
					if (!isToast) {
						Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
						isToast = true;
					}
				} else {
					getHairStyles();
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		getApp().setShow(hairStyles.get(position));
		int start = position - 10;
		int end = position + 10;
		if (start < 0) {
			start = 0;
		}
		if (end >= hairStyles.size()) {
			end = hairStyles.size();
		}
		ArrayList<HairStyle> list = new ArrayList<HairStyle>();
		for (int i = start; i < end; i++) {
			list.add(hairStyles.get(i));
		}
		getApp().setHairInfo(list);

		Intent intent = new Intent(getApplicationContext(), HairInfoActivity.class);
		startActivity(intent);

	}

	private Dialog dialog;

	public void showLoadingDialog() {
		dialog = new Dialog(this, R.style.Theme_AiShang_Dailog);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		dialog.show();
	}

}
