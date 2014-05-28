package com.aishang.app.ui.activity;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aishang.app.R;
import com.aishang.app.common.Constants;
import com.aishang.app.common.PreferenceUtil;
import com.aishang.app.data.dto.LoginDTO;
import com.aishang.app.ui.base.BaseActivity;
import com.alibaba.fastjson.JSON;

public class LoginActivity extends BaseActivity implements Constants {

	private EditText emailEditText;
	private EditText pwdEditText;
	private Dialog dialog;
	private TextView textView;
	private String imei;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceUtil preferenceUtil = new PreferenceUtil(getApplication());
		boolean isLogin = preferenceUtil.getBoolean("isLogin");
		if (isLogin) {
			intoMainActivity();
			this.finish();
			return;
		}

		setContentView(R.layout.activity_login);
		emailEditText = (EditText) findViewById(R.id.email);
		pwdEditText = (EditText) findViewById(R.id.pwd);
		textView = (TextView) findViewById(R.id.text);
		// imei = getLocalMacAddress().hashCode() + "";
		// textView.setText("IMEI: " + imei);
		textView.setVisibility(View.GONE);
		initDialog();
	}

	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public void checkLogin() {

	}

	public void intoMainActivity() {
		Intent intent = new Intent(getApplication(), MainActivity.class);
		startActivity(intent);
		LoginActivity.this.finish();
	}

	private void initDialog() {
		dialog = new Dialog(this, R.style.Theme_AiShang_Dailog);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading,
				null);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
	}

	public void login(View view) {
		if (TextUtils.isEmpty(emailEditText.getEditableText())
				&& TextUtils.isEmpty(pwdEditText.getEditableText()))
			return;
		String email = emailEditText.getEditableText().toString();
		String pwd = pwdEditText.getEditableText().toString();
		dialog.show();
		FinalHttp http = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put("email", email);
		params.put("pwd", pwd);
		params.put("imei", imei);
		http.post(login, params,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String responseStr) {
						System.out.println(responseStr);
						if (TextUtils.isEmpty(responseStr))
							return;
						LoginDTO dto = JSON.parseObject(responseStr, LoginDTO.class);
						if (dto.getStatus_code() == 500) {
							dialog.cancel();
							return;
						}
						intoMainActivity();
						PreferenceUtil preferenceUtil = new PreferenceUtil(getApplication());
						preferenceUtil.putBoolean("isLogin", true);
						preferenceUtil.putString("store", dto.getStore().getUsers_id() + "");
						preferenceUtil.putString("hairstylist", dto.getHairstylist().getUsers_id() + "");
						preferenceUtil.putString("agent", dto.getAgent().getUsers_id() + "");
						dialog.cancel();
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						System.out.println(strMsg);
						dialog.cancel();
					}
				});

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
}
