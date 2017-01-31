package com.touchnedu.gradea.studya.math.modules;


import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.PlaySound;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewMath {
	private static WebViewMath wvMath = null;
	private Context mContext;
	private Activity mActivity;
	private WebView mathContent;
	private ContentManager cm = ContentManager.getInstance();
	private ViewModule vm = ViewModule.getInstance();
	private SharedPreferencesManager spm = SharedPreferencesManager.getInstance();
	private PlaySound playSound;
	private final Handler mHandler = new Handler();
	private AnimationModule am;
	private float positionVal;
	
	public static WebViewMath getInstance() {
		if(wvMath == null) {
			wvMath = new WebViewMath();
		}
		return wvMath;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public void initView(Context context) {
		mContext = context;
		mActivity = (Activity)context;
		playSound = PlaySound.getInstance(mContext);
		
		mathContent = (WebView)mActivity.findViewById(R.id.math_content);
		mathContent.getSettings().setJavaScriptEnabled(true);
		mathContent.getSettings().setBuiltInZoomControls(false);
		mathContent.setHorizontalScrollBarEnabled(false);
		mathContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mathContent.addJavascriptInterface(new AndroidBridge(), "android");
		mathContent.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				 loadMathContent();
			}
		});
		
		am = new AnimationModule();
		am.setAniOption(0f, 1f, 2000);
		am.startViewAnimation(mathContent);
		
	}
	
	public WebView getMathContent() {
		return mathContent;
	}
	
	public void loadMathContent() {
		String loadNumber = DataManager.qChapterNum + DataManager.qQuizNum 
																													+ DataManager.qAddNum;
		loadUrl("javascript:setVersion('" 
																		+ android.os.Build.VERSION.SDK_INT + "')");
		loadUrl("javascript:loadQuiz('" + loadNumber + "')");
	}
	
	public void mathContentReset() {
		mathContent.loadUrl("");
		mathContent.stopLoading();
	}
	
	public void loadUrl(String url) {
		mathContent.loadUrl(url);
	}
	
	public class AndroidBridge implements JavaScriptMainCallback {
		@Override	@JavascriptInterface
		public void setAnswer(final String answer) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					cm.setAnswer(answer);
				}
			});
		}
		@Override	@JavascriptInterface
		public void setScale(final int width, final int height) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					/* 필기 인식 후 얻는 이미지의 크기 세팅 */
					int tempValue = width * (int)DataManager.currentDeviceDensity;
					cm.setTargetAnsBoxWidth(tempValue);
					tempValue = height *  (int)DataManager.currentDeviceDensity;
					cm.setTargetAnsBoxHeight(tempValue);
				}
			});
		}
		@Override	@JavascriptInterface
		public void setPosition(final int top, final int left) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					/** getResultAsImage()의 위치 보정값 */
					positionVal = left * DataManager.currentDeviceDensity 
																							+ DataManager.dp2px(mContext, 18);
					cm.setCoordinateX(positionVal);
					positionVal = top * DataManager.currentDeviceDensity 
																							+ DataManager.dp2px(mContext, 20);
					cm.setCoordinateY(positionVal);
				}
			});
		}
		@Override	@JavascriptInterface
		public void rightSoundPlay() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					playSound.playSound(DataManager.RIGHT_SOUND);
					DataManager.qBoxNum += 1;
				}
			});
		}
		@Override	@JavascriptInterface
		public void finishQuiz() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					playSound.playSound(DataManager.FINAL_SOUND);
					if(spm.autoNextQuiz)
						vm.setButtonEnabled(false);
				}
			});
		}
		@Override	@JavascriptInterface
		public void quizMessage(String msg) {
			
		}
		@Override	@JavascriptInterface
		public void setCurrentQuizNumber(String number) {
			
		}
		@Override	@JavascriptInterface
		public void setBackEvent() {
			
		}
		@Override @JavascriptInterface
		public void getIsNextChapter(boolean isNextChapter, String src) {
			
		}
	}
}
