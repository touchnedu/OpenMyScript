package com.touchnedu.gradea.studya.math;

import com.touchnedu.gradea.studya.math.modules.LayoutInflateController;
import com.touchnedu.gradea.studya.math.modules.SlideMenuController;
import com.touchnedu.gradea.studya.math.service.ParentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PersonalInfoActivity extends ParentActivity {
	private WebView myWebView;
	public Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SlideMenuController.TEMP_STATE = SlideMenuController.DEFAULT_STATE;
		SlideMenuController.DEFAULT_STATE = 0;
		super.onCreate(savedInstanceState);
		mContext = this;

		LayoutInflateController.inflateLayout(this, R.layout.activity_personal_info);
		
		initView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void initView() {
		myWebView = (WebView) findViewById(R.id.webview);
		// Configure related browser settings
		myWebView.getSettings().setLoadsImagesAutomatically(true);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		// Configure the client to use when opening URLs
		myWebView.setWebViewClient(new MyBrowser());

		// string 리소스중 name이 touchnedu_personal의 문자열을 getText()로 가져온다.
		// 개인정보취급방침
		CharSequence url = getText(R.string.touchnedu_personal);

		// Load the initial URL
		myWebView.loadUrl(url.toString());

	}

	// Manages the behavior when URLs are loaded
	private class MyBrowser extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SlideMenuController.DEFAULT_STATE = 0;
	}
	
	// 뒤로 버튼 종료
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !checkDrawerState()) {
			finish();
		}
		return true;

	}
}
