package com.touchnedu.gradea.studya.math;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class PersonalInfoForJoinActivity extends Activity {
	private WebView myWebView;
	public Context mContext;

	@Override
  protected void onCreate(Bundle savedInstanceState) {
  	mContext = this;
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_personal_info_for_join);
        
    initView();
  }
	
	@SuppressLint("SetJavaScriptEnabled")
	public void initView() {
		// 뒤로 가기 버튼
		Button registerButton = (Button)findViewById(R.id.btn_prev);
  	registerButton.setOnClickListener(new View.OnClickListener() {
  		@Override
  		public void onClick(View view) {
  			PersonalInfoForJoinActivity.this.finish();
  		}
  	});
		
		myWebView = (WebView) findViewById(R.id.webview);
		// Configure related browser settings
		myWebView.getSettings().setLoadsImagesAutomatically(true);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		// Configure the client to use when opening URLs
		myWebView.setWebViewClient(new MyBrowser());
		
		// string 리소스중 name이 touchnedu_personal의 문자열을 getText()로 가져온다.
		// 개인정보취급방침
    CharSequence touchnedu_url03 = getText(R.string.touchnedu_personal);
		
		// Load the initial URL
		myWebView.loadUrl(touchnedu_url03.toString());

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
  protected void onDestroy() {	
    System.gc();
    super.onDestroy();
  }
}
