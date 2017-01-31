package com.touchnedu.gradea.studya.math;

import com.igaworks.IgawCommon;
import com.touchnedu.gradea.studya.math.modules.JavaScriptHintCallback;
import com.touchnedu.gradea.studya.math.modules.SlideMenuController;
import com.touchnedu.gradea.studya.math.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AllHintActivity extends Activity implements OnClickListener {
	private Context context;
	private TextView hTitle;
	private Button hBackButton;
	private ImageView prevButton, nextButton;
	private WebView hWebView;
	private String schoolCode, largeChapterCode;
	private int hintNumber = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SlideMenuController.TEMP_STATE = SlideMenuController.DEFAULT_STATE;
		SlideMenuController.DEFAULT_STATE = 0;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_viewhint);
		IgawCommon.startSession(AllHintActivity.this);
		context = this;
		
		initView();
	}
	
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private void initView() {
		schoolCode = getIntent().getExtras().getString("schoolNumber");
		largeChapterCode = getIntent().getExtras().getString("chapterNumber");
		Log.i("prjt", "largeChapterCode : " + largeChapterCode);
		
		hTitle = (TextView)findViewById(R.id.view_hint_title);
		hTitle.setText(getIntent().getExtras().getString("title"));
		hBackButton = (Button)findViewById(R.id.view_hint_back);
		prevButton = (ImageView)findViewById(R.id.view_hint_prev);
		nextButton = (ImageView)findViewById(R.id.view_hint_next);
		hWebView = (WebView)findViewById(R.id.view_hint_wv);
		
		hBackButton.setOnClickListener(this);
		prevButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		
		hWebView.getSettings().setJavaScriptEnabled(true);
		hWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		hWebView.addJavascriptInterface(new HintBridge(), "hintroid");
		hWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// 힌트 로드하기
				callHintPage();
			}
		});
		
		CharSequence hintUrl = getString(R.string.all_hint_view);
		hWebView.loadUrl(hintUrl.toString());
		
	}

	private void callHintPage() {
		String hintCode = largeChapterCode + Util.setNumberStyle(hintNumber);
		Log.i("prjt", "hintCode : " + schoolCode + " + " + hintCode);
		hWebView.loadUrl("javascript:loadHintAll('" + schoolCode + "', '" 
																														+ hintCode + "')");
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.view_hint_back:
			finish();
			break;
			
		case R.id.view_hint_prev:
			if(hintNumber == 1) {
				Util.customOneDialog(context, "", getString(R.string.first_hint), 
																						R.layout.custom_dialog_one,	false);
				return;
			}
			--hintNumber;
			callHintPage();
			break;
			
		case R.id.view_hint_next:
			++hintNumber;
			callHintPage();
			break;
			
		}
	}

	private class HintBridge implements JavaScriptHintCallback {
		@JavascriptInterface
		@Override
		public void isLastHint() {
			new Handler().post(new Runnable() {
				public void run() {
					lastHintDialog();
				}
			});
		}
	}
	
	private void lastHintDialog() {
		Handler tempHandler = new Handler(Looper.getMainLooper());
		tempHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Util.customOneDialog(context, "", getString(R.string.last_hint), 
																						R.layout.custom_dialog_one, false);
			}
		}, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		IgawCommon.endSession();
	}
	
	
	
}
