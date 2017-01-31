package com.touchnedu.gradea.studya.math.modules;

import com.touchnedu.gradea.studya.math.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewBookCase {
	private Activity activity;
	private WebView mWebView;
	private static WebViewBookCase instance;
	public TextView mTitle;
	
	public WebViewBookCase() { }
	
	public WebViewBookCase(Context context) {
		activity = (Activity)context;
	}
	
	public void initWebView() {
		mWebView = (WebView)activity.findViewById(R.id.bookcase_wv);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.setHorizontalScrollBarEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i("prjt", "url : " + url);
				if (Uri.parse(url).getHost().startsWith("touchnbox")) {
          return false;
				}
	      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	      activity.startActivity(intent);
	      return true;
			}
    });

		mTitle = (TextView)activity.findViewById(R.id.bookcase_title_wv);
		initPage();
		
	}
	
	public void initPage() {
		try {
			mWebView.loadUrl(activity.getString(R.string.math_1_2_3));
			DataManager.currentPageState = 0;
			clearView();
	  } catch (Exception e) {
	  	e.printStackTrace();
	  }
	}
	
	public void callTestPage() {
		try {
			mWebView.loadUrl(activity.getString(R.string.test_content));
			DataManager.currentPageState = 1;
			clearView();
			Toast.makeText(activity, "검수용 페이지가 로드되었습니다.", Toast.LENGTH_SHORT).show();
	  } catch (Exception e) {
	  	e.printStackTrace();
	  }
	}
	
	public void setTitle(String title) {
		mTitle.setText(title);
	}
	
	public WebView getWebView() {
		return mWebView;
	}
	
	public boolean canGoBack() {
		return mWebView.canGoBack();
	}
	
	public void goBack() {
		mWebView.goBack();
	}
	
	public void clearView() {
		mWebView.clearHistory();
		mWebView.clearCache(true);
	}
	
	public void setWebViewBookCase(WebViewBookCase wvbc) {
		instance = wvbc;
	}
	
	public static WebViewBookCase getInstance() {
		return instance;
	}
	
}
