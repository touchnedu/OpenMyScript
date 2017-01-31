package com.touchnedu.gradea.studya.math.modules;

import com.igaworks.IgawCommon;
import com.touchnedu.gradea.studya.math.AllHintActivity;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.BaseActivityManager;
import com.touchnedu.gradea.studya.math.service.MusicService;
import com.touchnedu.gradea.studya.math.service.ParentActivity;
import com.touchnedu.gradea.studya.math.service.SavedQuizBox;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;
import com.touchnedu.gradea.studya.math.util.Util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BookCaseActivity extends ParentActivity implements OnClickListener {
	private ImageView hiddenBtn;
	private WebView bookCase;
	private TextView closeTxt;
	private final Handler handler = new Handler();
	private Context context;
	private BaseActivityManager am = BaseActivityManager.getInstance();
	private MusicService ms;
	private WebViewBookCase wvbc;
	private SharedPreferencesManager sInstance = 
													 							SharedPreferencesManager.getInstance();
	private static final String TAG = "prjt";
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
//		writeLog(sInstance.notCompletedQuiz);
		
		LayoutInflateController.inflateLayout(this, R.layout.activity_bookcase);
    SlideMenuController.DEFAULT_STATE = 1;
		
    IgawCommon.startApplication(BookCaseActivity.this);
    
		ms = MusicService.getInstance(getApplicationContext());
		ms.initMusic();
		ms.isMpOn();
		
		if(sInstance.bgSound) 
			ms.startMusic();
		
		if(sInstance.isFirstConnect) {
			sInstance.setIsFirstConnect(false);
			sInstance.saveProperties(context);
		}
		
		hiddenBtn = (ImageView)findViewById(R.id.hidden_btn);
		hiddenBtn.setOnClickListener(this);
		
		wvbc = new WebViewBookCase(this);
		wvbc.setWebViewBookCase(wvbc);
		wvbc.initWebView();
		bookCase = wvbc.getWebView();
		bookCase.getSettings().setJavaScriptEnabled(true);
		bookCase.addJavascriptInterface(new AndroidBridgeBookcase(), "bookcase");
		
//		backgroundLine = (View)findViewById(R.id.background_line);
//		savedQuizLayout = (LinearLayout)findViewById(R.id.save_quiz_layout);
//		cancelSavedQuiz = (ImageView)findViewById(R.id.cancel_saved_quiz);
		
		if(sInstance.getIsNotCompletedQuiz() && DataManager.showSavedQuiz) 
			showSavedQuiz();
		
		DisplayInfo di = new DisplayInfo(this);
    di.getDisplayDimension();
    di.setStatusBarHeight();
    di.writeLog();
    
    if(DeviceInfo.getApiVersionNumber() < 20 && DataManager.isFirstNotice) {
    	runNoticePopup();
    	DataManager.isFirstNotice = false;
    }
    am.addActivity(this);
    
    /* get the application version information */
    getAppVersionInfo();
    
	}
	
	private void getAppVersionInfo() {
		try {
			PackageInfo i = context.getPackageManager()
														 .getPackageInfo(context.getPackageName(), 0);
			DataManager.appVersion = "V" + i.versionName;
		} catch (Exception e) {
			Log.e(TAG, "error", e);
			DataManager.appVersion = "버전 정보를 찾을 수 없습니다.";
		}
		
	}

	/** dialog 객체를 생성하고 xml 레이아웃을 가져오기 때문에 다이얼로그 내부의
	 * 콘텐츠는 dialog로부터 받아와야 한다. 안 그러면 Null Pointer 에러 남..
	 * 따라서 dialog.findViewById( ... )
	 * */
	private void runNoticePopup() {
		final Dialog dialog = new Dialog(context, R.style.FullHeightDialog);
		dialog.setContentView(R.layout.custom_dialog_02);
		dialog.show();
		closeTxt = (TextView)dialog.findViewById(R.id.notice_close_01);
		closeTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	/** 안드로이드 브릿지 */
	public class AndroidBridgeBookcase implements JavaScriptCallback {
		@Override
		@JavascriptInterface
		public void openQuizActivity(final String code) {
			handler.post(new Runnable() {
				public void run() {
					openMainActivity(code, false);
				}
			});
		}
		@Override
		@JavascriptInterface
		public void setChapterTitle(final String title) {
			handler.post(new Runnable() {
				public void run() {
					if(!title.equals(""))
						wvbc.setTitle(title);	
				}
			});
		}
		@Override
		@JavascriptInterface
		public void notReadyContent() {
			handler.post(new Runnable() {
				public void run() {
					Util.customOneDialog(context, "", getString(R.string.not_ready), 
							 								 							R.layout.custom_dialog_one, false);
				}
			});
		}
		@Override
		@JavascriptInterface
		public void getHintContentInfo(final String title, final String gCode, 
																													final String cCode) {
			handler.post(new Runnable() {
				public void run() {
					openHintActivity(title, gCode, cCode);
				}
			});
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && !checkDrawerState()) {
			// 뒤로가기 버튼을 눌렀을 때 웹뷰의 웹페이지가 뒤로 갈 수 있다면
			if(wvbc.canGoBack()) {
				wvbc.goBack();
				wvbc.setTitle(getResources().getString(R.string.navi_section1));
				
			} else {
				Util.finishApp(context);
				return super.onKeyDown(keyCode, event);
				
			}
		}
		return true;
		
	}
	
	/* open MainActivity */
	private void openMainActivity(String code, boolean b) {
		DataManager.qChapterNum = code.substring(0, 7);
		DataManager.qQuizNum = code.substring(7, 10);
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("chapterImg", b);
		startActivity(intent);
	}
	
	/*
	 * open HintActivity
	 */
	private void openHintActivity(String title, String gCode, String cCode) {
		Intent intent = new Intent(this, AllHintActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("schoolNumber", gCode);
		intent.putExtra("chapterNumber", cCode);
		startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hidden_btn:
			runHiddenFunc();
			break;
			
		case R.id.goto_saved_quiz:
			openMainActivity(sInstance.notCompletedQuiz, true);
			closeSavedQuizWindow();
			break;
			
		case R.id.cancel_saved_quiz:
			closeSavedQuizWindow();
			break;
			
		default:
			break;
		}

	}
	
	public void runHiddenFunc() {
		if(checkAdministrator()) {
			if(DataManager.currentPageState == 1) {
				wvbc.initPage();
				Toast.makeText(context, "공개용 페이지가 로드되었습니다.", Toast.LENGTH_SHORT).show();
			} else if(DataManager.currentPageState == 0) {
				wvbc.callTestPage();
			}
		}
	}
	
	private boolean checkAdministrator() {
		if(DataManager.curMemEmail.equals("sehony@naver.com") ||
				DataManager.curMemEmail.equals("limeof@naver.com")) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i("prjt", "BookCase onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState == null)
			return;
		if(!savedInstanceState.getString("savedQuiz").equals("") &&
														savedInstanceState.getString("savedQuiz") != null) {
			DataManager.savedQuizNum = savedInstanceState.getString("savedQuiz");
			showSavedQuiz();
		}
	}

	@Override
  protected void onDestroy() {
		am.removeActivity(this);
		ms.stopMusic();
    super.onDestroy();
  }
	
	@Override
	protected void onRestart() {
		SlideMenuController.DEFAULT_STATE = 1;
		super.onRestart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		writeLog("bookcase onPause()");
	}

	private void showSavedQuiz() {
		new SavedQuizBox(this).ShowSavedQuizBox();
	}
	
	private void closeSavedQuizWindow() {
		new SavedQuizBox(this).closeSavedQuizBox();
		sInstance.setIsNotCompletedQuiz(false);
		sInstance.setNotCompletedQuiz("");
		sInstance.saveProperties(context);
	}
	
	private void writeLog(String msg) {
		Log.i(TAG, msg);
	}
	
}
