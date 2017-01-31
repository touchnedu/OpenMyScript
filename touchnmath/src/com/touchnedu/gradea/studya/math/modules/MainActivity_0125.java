// Copyright (c) MyScript

package com.touchnedu.gradea.studya.math.modules;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.myscript.atk.maw.MathWidgetApi;
import com.myscript.atk.maw.MathWidgetApi.RecognitionBeautification;
import com.myscript.atk.maw.sample.resources.SimpleResourceHelper;
import com.myscript.certificate.MyCertificate;
import com.touchnedu.gradea.studya.math.BuildConfig;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.BookmarkManager;
import com.touchnedu.gradea.studya.math.service.InitAnswer;
import com.touchnedu.gradea.studya.math.service.MusicService;
import com.touchnedu.gradea.studya.math.service.OnContentScrollListener;
import com.touchnedu.gradea.studya.math.service.OnWindowFocusListener;
import com.touchnedu.gradea.studya.math.service.ParentActivity;
import com.touchnedu.gradea.studya.math.service.PlaySound;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;
import com.touchnedu.gradea.studya.math.util.Util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;



public class MainActivity_0125 extends ParentActivity implements
																		      android.view.View.OnClickListener,
																		      OnContentScrollListener {
	
  private static final boolean DBG = BuildConfig.DEBUG;
  private static final String TAG = "TAG";
  private static final String INFO = "INFO";
  private static final String PRJT = "prjt";

  Context mContext;
  
  /** Math Widget */
  private MyScriptModule msModule;

  /** View 정의 */
  ImageView answerImageView; // 정답 입력 시 들어가는 이미지 뷰
  Bitmap clearBitmap, chapterTitleBitmap, nextChapterBitmap;
  ImageView clearImgView, hintImgView, hintBackView, hintCloseBtn, 
  					chapterImgView, chapterPrvImgView, nextNo;
  TextView quizText, chapterText;
  ScrollView quizScrollView;
  Button nextYes, toList;
  private WebView /*mathContent,*/ hintContent;
  private final Handler handler = new Handler();
  Animation animation, fadeInAni, fadeOutAni, blink;
  
  /** Layout 정의 */
  LinearLayout webViewAreaLayout;
  FrameLayout hintViewAreaLayout;
  
  /** 로컬 변수 정의 */
  private String chapterTitleUrl = "";
  private int targetAnsBoxWidth;
  private int targetAnsBoxHeight;
  private int interpolatedHandlerLocationX = 8;
  private boolean backEvent = false;
  
  /** 프리퍼런스 로드 */
  SharedPreferencesManager instance = SharedPreferencesManager.getInstance();
  
  /** 클래스 변수 정의 */
  private NoteFragment noteFragment;
  private CustomScroll customScrollbar;
  private BookmarkManager bookmarkManager;
  private AnswerBoxView answerBoxView;
  private PlaySound playSound;
  private ChapterTitleController chapterTitle, chapterPrvImg;
  
  private WebViewMath wvMath = WebViewMath.getInstance();
  private ViewModule vm = ViewModule.getInstance();
  
  @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = this;
    MusicService.getInstance(getApplicationContext()).stopMusic();
    
    if(instance.skinMath.toString().equals("white")) {
    	LayoutInflateController.inflateLayout(this, R.layout.activity_main_white);
    	chapterTitleUrl = getString(R.string.chapter_white); 
    } else {
    	LayoutInflateController.inflateLayout(this, R.layout.activity_main);	
    	chapterTitleUrl = getString(R.string.chapter_default); 
    }
    
    /* 마이스크립트 모듈 */
    msModule = new MyScriptModule(mContext);
    msModule.initMyScriptWidget();
    
    /* 문제 화면 웹뷰 초기화 */
    wvMath.initView(mContext);
    
    
    /* 뷰 세팅 */
    answerImageView = (ImageView)findViewById(R.id.answer_back);
    hintImgView = (ImageView)findViewById(R.id.hint_background);
    hintBackView = (ImageView)findViewById(R.id.hint_backgroundcolor);
    
    chapterImgView = (ImageView)findViewById(R.id.image_chapter);
    hintCloseBtn = (ImageView)findViewById(R.id.hint_closebutton);
    chapterText = (TextView)findViewById(R.id.text_chapter);
    hintBackView.setBackgroundColor(Color.argb(128, 0, 0, 0));
    // 추가

    if(getIntent().getExtras().getBoolean("chapterImg"))
    	setChapterTitleImg();
    
    /* 클래스 변수 세팅 */
    answerBoxView = new AnswerBoxView(this);
    answerBoxView.initActiveView((ImageView)findViewById(R.id.answer_img), 
    														 (ImageView)findViewById(R.id.answerbox_img));
    bookmarkManager = new BookmarkManager(
    															(ImageView)findViewById(R.id.bookmark_img01),
    															(ImageView)findViewById(R.id.bookmark_img02), 
    															MainActivity_0125.this);
    
    playSound = PlaySound.getInstance(getApplicationContext());
    // 문제 화면에서는 배경음을 끈다.
    if(instance.getBgSound())
    	playSound.stopSound(DataManager.BACKGROUND);
    
    /* 클릭 이벤트 등록 */
    hintCloseBtn.setOnClickListener(this);
    
  	
  	hintContent = (WebView)findViewById(R.id.hint_webview);
  	hintContent.setBackgroundColor(Color.TRANSPARENT);
  	hintContent.getSettings().setJavaScriptEnabled(true);
  	hintContent.addJavascriptInterface(new AndroidBridge(), "hintAndroid");
  	hintContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
  	hintContent.setWebViewClient(new WebViewClient() {
  		@Override
  		public void onPageFinished(WebView view, String url) {
  			super.onPageFinished(view, url);
  			loadHintContent();
  		}
  	});
    
  	/* 블링크 애니메이션 세팅 */
  	blink = AnimationUtils.loadAnimation(this, R.anim.blink);
  	blink.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				clearQuiz();
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
//				nextButton.startAnimation(fadeInAni);
				if(animation == blink) {
					answerImageView.setVisibility(View.VISIBLE);
				}
			}
		});
  	
    /* 연습장 뷰와 커스텀 스크롤바 세팅 */
    FragmentManager fManager = getFragmentManager();
    noteFragment = (NoteFragment)fManager.findFragmentById(R.id.note_fragment);
    customScrollbar = (CustomScroll)fManager.findFragmentById(R.id.leftScrollBar);
    /* 기존 스크롤 바 삭제 */
    quizScrollView = (ScrollView)findViewById(R.id.view_left_scroll);
    quizScrollView.setVerticalScrollBarEnabled(false);
    quizScrollView.setHorizontalScrollBarEnabled(false);
    
    /* 레이아웃 세팅 */
    hintViewAreaLayout = (FrameLayout)findViewById(R.id.hintViewArea);
    hintViewAreaLayout.setVisibility(View.INVISIBLE);
    fadeInAni = new AlphaAnimation(0f, 1f);
//    fadeInAni.setFillAfter(true);
    fadeInAni.setDuration(350);
    fadeOutAni = new AlphaAnimation(1f, 0f);
//    fadeOutAni.setFillAfter(true);
    fadeOutAni.setDuration(350);
    
    /* 웹뷰 레이아웃 스크롤바 표시 여부 세팅 */
    webViewAreaLayout = (LinearLayout)findViewById(R.id.webview_layout);
    /* 문제의 세로 크기에 따라 스크롤 바 표시 여부 변경
     * 문제의 크기에 따라 레이아웃의 크기가 바뀔 때 이벤트를 처리한다. */
    webViewAreaLayout.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, 
																				 int bottom, int oldLeft, int oldTop, 
																				 int oldRight, int oldBottom) {
				
				// 레이아웃 변경이 없다면 그냥 종료
				if(left == 0 && top == 0 && right == 0 & bottom == 0) {
					return;
				}
				setScrollbarVisibility();
				
			}
		});
    
    SlideMenuController.DEFAULT_STATE = 99;
    
    /* 앱 타이틀 세팅 */
    setTitle(getResources().getString(R.string.activity_name));
    /** 커스텀 세팅 종료 */
    
    /** 문제 데이터 초기화 */
    loadQuiz();
    
    /* 디바이스별 연습장 핸들러 보간을 위해 DP 단위의 PX 값 정의 */
    DataManager.interpolationWidth = (int)TypedValue.applyDimension(
																					TypedValue.COMPLEX_UNIT_DIP, 
																					interpolatedHandlerLocationX, 
																					getResources().getDisplayMetrics());
    /* --------------------------------------------------------------------- */
    
//    if(DataManager.onNaverLogin) {
//    	Toast.makeText(this, "현재 네이버 " + DataManager.curMemEmail + 
//    											"(으)로 로그인 중입니다.", Toast.LENGTH_SHORT).show();
//    }
    
  }
  
  protected void loadHintContent() {
  	String hintNumber = DataManager.qChapterNum;
  	hintContent.loadUrl("javascript:loadHint('" + hintNumber + "')");
  }

	@Override
  public void onWindowFocusChanged(boolean hasFocus) {
  	super.onWindowFocusChanged(hasFocus);
  	/** 화면(Activity)이 모두 표시되고 난 후에 작업을 해야 할 경우에 수행
  	 * onCreate는 화면을 불러오기 전이라 View의 크기를 불러온다던가 하는 작업
  	 * 수행이 불가능하다. 따라서 Flick 기능을 수행하는 아래 두 가지는 화면이
  	 * 모두 표시 완료된 후에 작업해야 한다. */
  	if(noteFragment instanceof OnWindowFocusListener) {
  		/* noteFragment가 OnWindowFocusListener 타입으로 형변환이 가능한지 여부를
  		 * 판단. true면 가능. */
  		((OnWindowFocusListener)noteFragment).onWindowFocusChanged(hasFocus);
  		writeLog(INFO, "notFragment hasFocus : " + hasFocus);
  	}
  	if(customScrollbar instanceof OnWindowFocusListener) {
  		((OnWindowFocusListener)customScrollbar).onWindowFocusChanged(hasFocus);
  		writeLog(INFO, "customScrollbar hasFocus : " + hasFocus);
  	}
  	
  }
  
  /** 클릭 이벤트 리스너 시작 */
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.myscript_prevButton:
			initQuiz();
			msModule.widgetClear(true);
			clearNoteView();
			blink.cancel();
			resetWebView();
			wvMath.loadUrl("javascript:prevQuiz()");
			break;
			
		case R.id.myscript_resetButton:
			resetMathContent();
			break;
			
		case R.id.myscript_hintButton:
			initHintContent();
			break;
			
		case R.id.myscript_solButton:
			wvMath.loadUrl("javascript:viewSolution()");
			clearQuiz();
			vm.initAnswerBox();
//			nextButton.startAnimation(blink);
			break;
			
		case R.id.myscript_nextButton:
			goToNextQuiz();
			break;
		
		case R.id.hint_closebutton:
			closeHintView();
			break;
			
		default:
			break;

		}
	}
	/* 클릭 이벤트 리스너 종료 */
	
	private void goToNextQuiz() {
		initQuiz();
		msModule.widgetClear(true);
		clearNoteView();
		blink.cancel();
		noteFragment.setSliderBarInit();
		resetWebView();
		setButtonEnabled(true);
		wvMath.loadUrl("javascript:nextQuiz()");
	}
	
	private void resetMathContent() {
		initQuiz();
		msModule.widgetClear(true);
		blink.cancel();
		resetWebView();
		wvMath.loadMathContent();
		backEvent = false;
	}

	private void closeHintView() {
		hintViewAreaLayout.startAnimation(fadeOutAni);
		hintViewAreaLayout.setVisibility(View.INVISIBLE);
	}

	/** 웹뷰 화면 리셋 - 스크롤 바 위치와 뷰의 위치 */
	private void resetWebView() {
		customScrollbar.setScrollHandlerTo(0);
		onContentScrolled(0);
	}
	
	/** 힌트 화면 초기화 시작 */
	private void initHintContent() {
		hintContent.loadUrl(getString(R.string.hint_url));
		
		if(hintViewAreaLayout.getVisibility() == View.INVISIBLE) {
			hintViewAreaLayout.startAnimation(fadeInAni);
			hintViewAreaLayout.setVisibility(View.VISIBLE);
		}
		
	}
	/* 힌트 화면 초기화 종료 */

	/** 웹뷰로부터 호출되는 메소드 시작 */
	public class AndroidBridge implements JavaScriptMainCallback {
  	@Override
  	@JavascriptInterface
  	public void setAnswer(final String answer) {
  	}
  	@Override
  	@JavascriptInterface
  	public void setScale(final int width, final int height) {
  	}
  	@Override
  	@JavascriptInterface
  	public void setPosition(final int top, final int left) {
  	}
  	@Override
  	@JavascriptInterface
  	public void rightSoundPlay() {
  	}
  	@Override
  	@JavascriptInterface
  	public void finishQuiz() {
  		handler.post(new Runnable() {
				@Override
				public void run() {
					playSound.playSound(DataManager.FINAL_SOUND);
		  		if(instance.autoNextQuiz)
		  			setButtonEnabled(false);
				}
  		});
  		
  		if(instance.autoNextQuiz) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						goToNextQuiz();
					}
	  		}, 2500);				
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						callBlinkAnimation();
					}
	  		});
			}
  		
  	}
  	@Override
  	@JavascriptInterface
  	public void quizMessage(final String msg) {
  		handler.post(new Runnable() {
				@Override
				public void run() {
					if(msg.equals("first")) {
						Util.customOneDialog(mContext, "", 
																 getString(R.string.first_quiz_alert), 
																 R.layout.custom_dialog_one,
																 false);
					} else if(msg.equals("last")) {
						checkNextChapter();
					}
				}
  		});
  	}
  	@Override
  	@JavascriptInterface
  	public void setCurrentQuizNumber(final String number) {
  		handler.post(new Runnable() {
				@Override
				public void run() {
					DataManager.qQuizNum = number;
					bookmarkManager.getBookmarkInfo();
					if(DataManager.qQuizNum.equals("001")) {
						setChapterTitleImg();
					}
					
				}
  		});
  	}
  	@Override
  	@JavascriptInterface
  	public void setBackEvent() {
  		/* 모범답안 보기를 누르면 뒤로가기 버튼을 눌렀을 때 새로고침 기능을 실행. */
  		backEvent = true;
  	}
  	@Override
  	@JavascriptInterface
  	public void getIsNextChapter(final boolean isNextChapter, final String src) {
  		writeLog(PRJT, "isNextChapter : " + isNextChapter + ", src : " + src);
  		handler.post(new Runnable() {
				@Override
				public void run() {
					if(isNextChapter) {
						goToNextChapter(mContext, src);
					} else {
						Util.customOneDialog(mContext, 
																 getString(R.string.last_quiz_warning), 
																 getString(R.string.select_chapter), 
																 R.layout.custom_dialog_one,
																 false);
					}
				}
  		});
  	}
  	
  }
  /* 웹뷰로부터 호출되는 메소드 종료 */
  
  private void checkNextChapter() {
  	wvMath.loadUrl("javascript:isNextChapter(" + DataManager.qChapterNum + ")");
  }
  
  /** 다음 챕터로 이동 */
	private void goToNextChapter(Context context, String src) {
		final Dialog dialog = new Dialog(context, R.style.FullHeightDialog);
		dialog.setContentView(R.layout.custom_dialog_01);
		dialog.show();
		
		nextYes = (Button)dialog.findViewById(R.id.to_chapter_ok);
		nextNo = (ImageView)dialog.findViewById(R.id.chapter_preview_cancel);
		toList = (Button)dialog.findViewById(R.id.chapter_preview_list);
		chapterPrvImgView = (ImageView)dialog.findViewById(R.id.chapter_preview_img);
		chapterPrvImgView.setVisibility(View.VISIBLE);
		
		chapterPrvImg = new ChapterTitleController();
		chapterPrvImg.execute(src, "nextChapter");
		
		nextYes.setOnClickListener(this);
		nextYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataManager.qQuizNum = "001";
				// 다음 챕터로 챕터번호를 다시 세팅한다.
				DataManager.qChapterNum = Util.chapterConvertPlus(DataManager.qChapterNum);
				wvMath.loadMathContent();
				dialog.dismiss();
				setChapterTitleImg();
			}
		});
		nextNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		toList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				wvMath.mathContentReset();
				finish();
			}
		});
		
	}
  
  /** 문제 화면 클리어 시작 */
  private void clearQuiz() {
		answerImageView.setVisibility(View.INVISIBLE);
		msModule.setWidgetVisibility(View.INVISIBLE);
  }
  /* 문제 화면 클리어 종료 */
  
  private void callBlinkAnimation() {
//  	nextButton.startAnimation(blink);
  }
  
  /** 연습장 리셋 시작 */
  private void clearNoteView() {
  	if(noteFragment == null)
  		noteFragment = new NoteFragment();
  	noteFragment.clearNoteView();
  }
  /* 연습장 리셋 종료 */
  
  /** 스크롤바 표시 여부 세팅 시작 */
  private void setScrollbarVisibility() {
  	// 현재 액티비티와 연관된 Fragment를 관리할 객체를 가져온다.
  	FragmentManager fm = getFragmentManager();
  	if(fm != null && customScrollbar != null) {
  		if(getContentScrollSize() <= 0) {
  			// 커스텀 스크롤 바가 보이는 상태면 감추고 
  			if(!customScrollbar.isHidden()) {
  				fm.beginTransaction().hide(customScrollbar).commit();
  				
  			}
  		} else {
  			// 그렇지 않으면 보인다.
  			if(customScrollbar.isHidden()) {
  				fm.beginTransaction().show(customScrollbar).commit();
  				
  			}
  		}
  	}
  	
  }
  /* 스크롤바 표시 여부 세팅 종료 */
  
  /** 문제 영역 크기 가져오기 시작 */
  private int getContentScrollSize() {
  	// 문제 영역 레이아웃의 높이 - 문제 영역 웹뷰의 높이 
  	int rawSize = Math.max(0, webViewAreaLayout.getHeight() 
  																							- quizScrollView.getHeight());
  	return rawSize;
  }
  /* 문제 영역 크기 가져오기 종료 */
  
  /** OnContentScrolled 구현 시작 */
  @Override
  public void onContentScrolled(float rateF) {
  	quizScrollView.scrollTo(0, (int)(rateF * getContentScrollSize()));
  	
  }
  /* OnContentScrolled 구현 종료 */
  
  
  /** 손대지 않는 기능들 */
  
  //뒤로가기 버튼 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/* 액티비티를 종료시킨다면,
		 * 현재 풀고 있는 문제 번호를 프리퍼런스에 저장한 후에 종료한다. */
		if(keyCode == KeyEvent.KEYCODE_BACK && !checkDrawerState()) {
			if(hintViewAreaLayout.getVisibility() == View.VISIBLE) {
				closeHintView();
			} else if(backEvent == true) {
				resetMathContent();
			} else {
				finish();
				return super.onKeyDown(keyCode, event);
			}
		}
		return true;
		
	}
	
	@Override
  protected void onDestroy() {
		if(instance.bgSound) 
			MusicService.getInstance(getApplicationContext()).restartMusic();
		msModule.closeHandWriting();
    msModule.widgetRelease();
    bookmarkManager.closeDBManager();
    clearNoteView();
    
    setCurrentQuizState(true, DataManager.qChapterNum + DataManager.qQuizNum);
    
		System.gc();
    super.onDestroy();
    
  }
	
	@Override
	protected void onRestart() {
		super.onRestart();
		SlideMenuController.DEFAULT_STATE = 99;
	}
	
  @Override
  protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);
    setCurrentQuizState(true, DataManager.qChapterNum + DataManager.qQuizNum);
    
  }
  
  private void setCurrentQuizState(boolean b, String quizNumber) {
  	instance.setIsNotCompletedQuiz(b);
    instance.setNotCompletedQuiz(quizNumber);
    instance.saveProperties(mContext);
  }
  
  /** 퀴즈 화면 로딩 시작 */
  public void loadQuiz() {
  	initQuiz();
  	// 웹 뷰에 문제 로드
  	try {
  		wvMath.loadUrl(getString(R.string.math_content));
    } catch (Exception e) {
    	e.printStackTrace();
    }
  	
	}
  /* 퀴즈 화면 로딩 종료 */
  
  /** */
  private void initQuiz() {
  	vm.initAnswerBox();
  	msModule.setWidgetVisibility(View.VISIBLE);
  	DataManager.qBoxNum = 1;
  	
  }
  
  private void setChapterTitleImg() {
  	chapterTitle = new ChapterTitleController().getInstance();
		chapterImgView.setVisibility(View.VISIBLE);
		String chapterImgName = chapterTitleUrl +  
														DataManager.qChapterNum.substring(0, 3) + "_" + 
														DataManager.qChapterNum.substring(3) + ".png";
		writeLog(PRJT, "소챕터 이미지 : " + chapterImgName);
		chapterTitle.execute(chapterImgName, "chapterTitle");
  }
	
	// 웹으로부터 이미지 받아와서 표시하는 클래스
	private class ChapterTitleController extends AsyncTask<String, Integer, Bitmap> {
		ChapterTitleController instance = null;
		int viewType = 1;
		
		@Override
		protected Bitmap doInBackground(String... url) {
			try {
				URL valueSrc = new URL(url[0]);
				// 단일 http 접속을 위한 캐스트. HttpURLConnection 은 웹페이지 요청을 위한 클래스
				HttpURLConnection conn = (HttpURLConnection)valueSrc.openConnection();
				conn.setDoInput(true); // InputStream으로 응답 헤더와 메세지를 읽어들이겠다는 옵션
				conn.connect();
				InputStream is = conn.getInputStream();
				
				if(url[1].equals("chapterTitle")) {
					chapterTitleBitmap = BitmapFactory.decodeStream(is);
					viewType = 1;
				} else if(url[1].equals("nextChapter")) {
					nextChapterBitmap = BitmapFactory.decodeStream(is);
					viewType = 2;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(url[1].equals("chapterTitle")) {
				return chapterTitleBitmap;
			} else {
				return nextChapterBitmap;
			}
			
		}
		
		protected void onPostExecute(Bitmap bitmap) {
			if(viewType == 1) {
				chapterImgView.setImageBitmap(bitmap);
			} else if(viewType == 2) {
				chapterPrvImgView.setImageBitmap(bitmap);
			}
		}
		
		public ChapterTitleController getInstance() {
			if(instance == null) {
				instance = new ChapterTitleController();
			}
			return instance;
		}

	}
	
	private void setButtonEnabled(boolean b) {
//		nextButton.setEnabled(b);
//		prevButton.setEnabled(b); 
//		resetButton.setEnabled(b); 
//		hintButton.setEnabled(b); 
//		solButton.setEnabled(b);
	}
	
	private void writeLog(String tag, String msg) {
//		Log.i(tag, msg);
	}
}