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
import com.touchnedu.gradea.studya.math.OptionActivity;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.TutorialMenuActivity;
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
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.Window;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;



public class MainActivity_bk extends ParentActivity implements
																		      MathWidgetApi.OnConfigureListener,
																		      MathWidgetApi.OnRecognitionListener,
																		      MathWidgetApi.OnWritingListener,
																		      android.view.View.OnClickListener,
																		      OnContentScrollListener {
	
  private static final boolean DBG = BuildConfig.DEBUG;
  private static final String TAG = "TAG";
  private static final String INFO = "INFO";
  private static final String PRJT = "prjt";

  Context mContext;
  
  /** Math Widget */
  private MathWidgetApi mWidget;

  /**
   * List of error dialog types.
   */
  /** Notify the user that a MSB resource is not found or invalid. */
  public static final int DIALOG_ERROR_RESSOURCE = 0;
  /** Notify the user that a MSB certificate is missing or invalid. */
  public static final int DIALOG_ERROR_CERTIFICATE = 1;
  /** Notify the user that maximum number of items has been reached. */
  public static final int DIALOG_ERROR_RECOTIMEOUT = 2;
  /** One error dialog at a time. */
//  private boolean mErrorDlgDisplayed = false;

  /** View 정의 */
  ImageView answerImageView; // 정답 입력 시 들어가는 이미지 뷰
  Bitmap clearBitmap, chapterTitleBitmap, nextChapterBitmap;
  ImageView clearImgView, hintImgView, hintBackView, hintCloseBtn, 
  					chapterImgView, chapterPrvImgView, nextNo;
  View prevButton, resetButton, hintButton, solButton, nextButton;
  TextView quizText, chapterText;
  ScrollView quizScrollView;
  Button nextYes, toList;
  private WebView mathContent, hintContent;
  private final Handler handler = new Handler();
  Animation animation, fadeInAni, fadeOutAni, blink;
  
  /** Layout 정의 */
  LinearLayout webViewAreaLayout;
  FrameLayout hintViewAreaLayout;
  
  /** 로컬 변수 정의 */
  private String resultTxt = "";
  private String rightAnswer = "";
  private float coordinateX = 0;
  private float coordinateY = 0;
  private int targetAnsBoxWidth;
  private int targetAnsBoxHeight;
  private int interpolatedHandlerLocationX = 8;
  private boolean backEvent = false;
  
  /* 필기 인식 후 얻은 비트맵 좌표 */
  private Timer aniTimer = null;
  private TimerTask aniTimerTask = null;
  private int aniFrameCount = 0;
  
  /** 프리퍼런스 로드 */
  SharedPreferencesManager instance = SharedPreferencesManager.getInstance();
  
  /** 클래스 변수 정의 */
  private NoteFragment noteFragment;
  private CustomScroll customScrollbar;
  private BookmarkManager bookmarkManager;
  private AnswerBoxView answerBoxView;
  private PlaySound playSound;
  private ChapterTitleController chapterTitle, chapterPrvImg;
  private PopupWindow mPopupWindow;
  
  @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    
    mContext = this;
    MusicService.getInstance(getApplicationContext()).stopMusic();
    
    if(instance.skinMath.toString().equals("white")) {
    	setContentView(R.layout.activity_main_white);
//    	DataManager.chapterTitleUrl = 
//    										"http://touchnbox.cafe24.com/chapter_images/white_bar_";
    } else {
    	setContentView(R.layout.activity_main);	
//    	DataManager.chapterTitleUrl = 
//    										"http://touchnbox.cafe24.com/chapter_images/bar_";
    }
    
    /** ATK 기본 필수 세팅 */
    mWidget = (MathWidgetApi) findViewById(R.id.myscript_maw);
    mWidget.setOnConfigureListener(this);
    mWidget.setOnRecognitionListener(this);
    mWidget.setOnWritingListener(this);
    
    /** 커스텀 세팅 시작 */
    mWidget.setBeautificationOption(RecognitionBeautification.BeautifyFontify);
    mWidget.setBaselineColor(View.INVISIBLE);	
    mWidget.setBackgroundColor(View.INVISIBLE);
    mWidget.setItalicTypeface(Typeface.createFromAsset(getAssets(), 
    																								"STIXGeneral-Italic.ttf")); 
    
    /* 뷰 세팅 */
    answerImageView = (ImageView)findViewById(R.id.answer_back);
    hintImgView = (ImageView)findViewById(R.id.hint_background);
    hintBackView = (ImageView)findViewById(R.id.hint_backgroundcolor);
    
    chapterImgView = (ImageView)findViewById(R.id.image_chapter);
    prevButton = findViewById(R.id.myscript_prevButton);
    resetButton = findViewById(R.id.myscript_resetButton);
    hintButton = findViewById(R.id.myscript_hintButton);
    hintCloseBtn = (ImageView)findViewById(R.id.hint_closebutton);
    solButton = findViewById(R.id.myscript_solButton);
    nextButton = findViewById(R.id.myscript_nextButton);
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
    															MainActivity_bk.this);
    
    playSound = PlaySound.getInstance(getApplicationContext());
    // 문제 화면에서는 배경음을 끈다.
    if(instance.getBgSound())
    	playSound.stopSound(DataManager.BACKGROUND);
    
    /* 클릭 이벤트 등록 */
    prevButton.setOnClickListener(this);
    resetButton.setOnClickListener(this);
    hintButton.setOnClickListener(this);
    solButton.setOnClickListener(this);
    nextButton.setOnClickListener(this);
    hintCloseBtn.setOnClickListener(this);
    
    /* 웹뷰 세팅 */
    mathContent = (WebView)findViewById(R.id.math_content);
    mathContent.getSettings().setJavaScriptEnabled(true);
    mathContent.getSettings().setBuiltInZoomControls(false);
    mathContent.setHorizontalScrollBarEnabled(false);
    // 웹뷰 성능 개선
    mathContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//    mathContent.setBackgroundColor(Color.argb(64, 0, 0, 0));
    mathContent.addJavascriptInterface(new AndroidBridge(), "android");
    mathContent.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				loadMathContent();
			}
    });
    
    animation = new AlphaAnimation(0.0f, 1.0f);
    animation.setFillAfter(true);
    animation.setDuration(2000);
  	mathContent.startAnimation(animation);
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
				nextButton.startAnimation(fadeInAni);
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
    
    /* 앱 타이틀 세팅 */
    setTitle(getResources().getString(R.string.activity_name));
    /** 커스텀 세팅 종료 */
    
    /** Configure equation recognition engine */
    configure();
    
    /** 문제 데이터 초기화 */
    loadQuiz();
    
    mWidget.setInkThickness((int)DataManager.penStrokeWidth);
    
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
  
  /** html 로딩이 완료되면 자바스크립트 실행 */
  protected void loadMathContent() {
  	String loadNumber = DataManager.qChapterNum + DataManager.qQuizNum + 
  																													DataManager.qAddNum;
  	mathContent.loadUrl("javascript:setVersion('" 
  																	+ android.os.Build.VERSION.SDK_INT + "')");
  	mathContent.loadUrl("javascript:loadQuiz('" + loadNumber + "')");
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
			mWidget.clear(true);
			clearNoteView();
			blink.cancel();
			resetWebView();
			mathContent.loadUrl("javascript:prevQuiz()");
			break;
			
		case R.id.myscript_resetButton:
			resetMathContent();
			break;
			
		case R.id.myscript_hintButton:
			initHintContent();
			break;
			
		case R.id.myscript_solButton:
			mathContent.loadUrl("javascript:viewSolution()");
			clearQuiz();
			initAnswerBox();
			nextButton.startAnimation(blink);
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
		mWidget.clear(true);
		clearNoteView();
		blink.cancel();
		noteFragment.setSliderBarInit();
		resetWebView();
		setButtonEnabled(true);
		mathContent.loadUrl("javascript:nextQuiz()");
	}
	
	private void resetMathContent() {
		initQuiz();
		mWidget.clear(true);
		blink.cancel();
		resetWebView();
		loadMathContent();
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
//		hintContent.loadUrl(DataManager.hintUrl);
		
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
  		handler.post(new Runnable() {
  			@Override
  			public void run() {
  				rightAnswer = answer;
  				writeLog(PRJT, "답 : " + answer);
  			}
  		});
  	}
  	@Override
  	@JavascriptInterface
  	public void setScale(final int width, final int height) {
  		writeLog(PRJT, "크기 : " 	+ width + " X " + height);
  		/* 필기 인식 후 얻는 이미지의 크기 지정 */
  		targetAnsBoxHeight = height * (int)DataManager.currentDeviceDensity;
  		targetAnsBoxWidth = width * (int)DataManager.currentDeviceDensity;
  		writeLog(PRJT, "setScale 후 targetAnsBoxHeight = " + targetAnsBoxHeight + ", targetAndBoxWidth = " + targetAnsBoxWidth);
  		
  	}
  	@Override
  	@JavascriptInterface
  	public void setPosition(final int top, final int left) {
  		writeLog(PRJT, "포지션X : " + left + ", 포지션Y : " + top);
  		/* positionX,Y는 두고, 어긋나는 부분을 dp->px(float)로 변환해야 함. */
  		coordinateX = left * DataManager.currentDeviceDensity + convertDp(18);
  		coordinateY = top * DataManager.currentDeviceDensity - convertDp(20);
  		
  	}
  	@Override
  	@JavascriptInterface
  	public void rightSoundPlay() {
  		playSound.playSound(DataManager.RIGHT_SOUND);
  		DataManager.qBoxNum += 1;
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
						Util.oneDialog(mContext, "첫 번째 문제입니다");
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
						backToList(mContext, getString(R.string.last_quiz_warning), 
																						getString(R.string.select_chapter));
					}
				}
  		});
  	}
  	
  }
  /* 웹뷰로부터 호출되는 메소드 종료 */
  
  private void checkNextChapter() {
  	mathContent.loadUrl("javascript:isNextChapter(" + DataManager.qChapterNum + ")");
  }
  
  /** 다음 챕터 없음, 목차로 이동 */
  private void backToList(Context context, String title, String msg) {
  	AlertDialog.Builder backAlert = new AlertDialog.Builder(context);
  	backAlert.setTitle(title).setMessage(msg)
  		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
  	backAlert.show();
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
//				DataManager.chapterTitleText = DataManager.nextChapterTitleText;
				loadMathContent();
//				setChapterTitle();
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
				mathContentReset();
			}
		});
		
	}
  
	/** 웹뷰 쓰레드 kill */
	private void mathContentReset() {
		mathContent.loadUrl("");
		mathContent.stopLoading();
//		android.os.Process.killProcess(android.os.Process.myPid());
		finish();
	}
  
  /** 문제 화면 클리어 시작 */
  private void clearQuiz() {
		answerImageView.setVisibility(View.INVISIBLE);
		((View)mWidget).setVisibility(View.INVISIBLE);
  }
  /* 문제 화면 클리어 종료 */
  
  private void callBlinkAnimation() {
  	nextButton.startAnimation(blink);
  }
  
  /** 연습장 리셋 시작 */
  private void clearNoteView() {
  	if(noteFragment == null) {
  		noteFragment = new NoteFragment();
  	}
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
		boolean popupStatus = false;
		if(mPopupWindow != null) {
			popupStatus = mPopupWindow.isShowing();
		}
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(popupStatus) {
				mPopupWindow.dismiss();
				popupStatus = false;
			} else if(hintViewAreaLayout.getVisibility() == View.VISIBLE) {
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
		closeHandWriting();
    mWidget.release(this);
    bookmarkManager.closeDBManager();
    clearNoteView();
    
    setCurrentQuizState(true, DataManager.qChapterNum + DataManager.qQuizNum);
    
		System.gc();
    super.onDestroy();
    
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
    Log.i("prjt", "저장함 : " + instance.isNotCompletedQuiz + ", " + instance.notCompletedQuiz);
  }
  
  /*@Override
  public void onBackPressed() { moveTaskToBack(true); }*/
  // ----------------------------------------------------------------------
  // Math widget styleable library - equation recognition engine configuration

  private void configure() {
    // Equation resource    
    final String[] resources = new String[]{"math-ak.res", "math-grm-maw.res"};

    // Prepare resources
    final String subfolder = "math";
    final String resourcePath = new String(getFilesDir().getPath() + java.io.File.separator + subfolder);
    SimpleResourceHelper
        .copyResourcesFromAssets(getAssets(), subfolder /* from */, resourcePath /* to */, resources /* resource names */);

    // Configure math widget
    mWidget.setResourcesPath(resourcePath);
    mWidget.configure(this, resources, MyCertificate.getBytes(), MathWidgetApi.AdditionalGestures.DefaultGestures);
  }

  // ----------------------------------------------------------------------
  // Math widget styleable library - equation recognition engine configuration

  @Override
  public void onConfigurationBegin() {
    if (DBG)
      Log.d(TAG, "Equation configuration begins"); }

  @Override
  public void onConfigurationEnd(final boolean success) {
    if (DBG) {
      if (success)
        Log.d(TAG, "Equation configuration succeeded");
      else
        Log.d(TAG, "Equation configuration failed (" + mWidget.getErrorString() + ")");
    }
    if (DBG) {
      if (success)
        Log.d(TAG, "Equation configuration loaded successfully");
      else
        Log.d(
            TAG,
            "Equation configuration error - did you copy the equation resources to your SD card? ("
                + mWidget.getErrorString() + ")");
    }
    // Notify user using dialog box
//    if (!success)
//      showErrorDlg(DIALOG_ERROR_RESSOURCE);
  }

  // ----------------------------------------------------------------------
  // Math Widget styleable library - equation recognition process

  @Override
  public void onRecognitionBegin() {}

  /** 인식 종료 후 답 체크 : 메인 기능 시작 */
  @SuppressLint("NewApi")
  @Override
  public void onRecognitionEnd() {
  	aniFrameCount = 0;
//    answerImageView.setBackgroundColor(Color.argb(32, 0, 0, 0));
  	
    if(!mWidget.getResultAsText().equals("")) {
    	((View)mWidget).setVisibility(View.INVISIBLE);
    	
    	if(aniTimerTask == null) {
    		aniTimerTask = new TimerTask() {
    			@Override
    			public void run() {
    				runOnUiThread(generateAnswer);
    				
    			}
    		};
    	}
    	
    	if(aniTimer == null) {
    		aniTimer = new Timer();
    		aniTimer.scheduleAtFixedRate(aniTimerTask, 0, 50);
    		
    	}
    }
  }
  /* 인식 종료 후 답 체크 : 메인 기능 종료 */
  
  /** 인식 종료 후 정규화되는 과정(필기 -> 디지털 기호) 시작 */
  private Runnable generateAnswer = new Runnable() {
  	@Override
  	public void run() {
  		
  		Canvas canvas;
  		Bitmap backBitmap;
  		Bitmap answerBitmap;
  		int aniFrame = 12;
  		aniFrameCount++;
  		
  		if(aniFrameCount == 1) {
  			resultTxt = mWidget.getResultAsText();
  			if(resultTxt.equals("⟩")) {
  				resultTxt = ">";
  			} else if(resultTxt.equals("⟨")) {
  				resultTxt = "<";
  			}
  			if(resultTxt.contains(","))
  				resultTxt = resultTxt.replace(",", ".");
  		}
  		
  		if(aniFrameCount > aniFrame) {
  			closeHandWriting();
  			if(resultTxt.equals(rightAnswer)) { /* -- 정답일 때 -- */
    			mathContent.loadUrl("javascript:correctAnswer('" 
    																						+ DataManager.qBoxNum + "')");
    			
    			initAnswerBox();
    			
    		} else { /* -- 오답일 때 -- */
    			playSound.playSound(DataManager.WRONG_SOUND);
    			
    			if(!resultTxt.equals("")) {
    				mathContent.loadUrl("javascript:incorrectAnswer('" 
    																						+ DataManager.qBoxNum + "')");
    			}
    		}
  			
  		} else {
  			answerBitmap = mWidget.getResultAsImage(MainActivity_bk.this, R.id.answer_back);
  			
    		if(answerBitmap != null) {
    			if(aniFrameCount == 12)
    			writeLog(INFO, "TARGETBOXHEIGHT : " + targetAnsBoxHeight);
    			writeLog(INFO, "answerBitmap.getWidth() : " + answerBitmap.getWidth());
    			writeLog(INFO, "answerBitmap.getHeight() : " + answerBitmap.getHeight());
    			/* 일정한 크기가 되면 Height값이 368이하로 내려가지 않는다.*/
    			if(answerBitmap.getHeight() <= DataManager.minHeight()) {
    				/* 비트맵을 잘라낼 x, y좌표, 잘라낼 비트맵 크기w, h  */
    				answerBitmap = Bitmap.createBitmap(answerBitmap, 
															    						 answerBitmap.getWidth() / 3, 
															    						 answerBitmap.getHeight() / 3, 		
															    						 answerBitmap.getWidth() / 3, 
															    						 answerBitmap.getHeight() / 3);
    				
    			} else {
    				int interpolatedPivotX = 
    										DataManager.interpolatedValue(answerBitmap.getWidth());
    				int interpolatedPivotY = 
    										DataManager.interpolatedValue(answerBitmap.getHeight());
    				writeLog(PRJT, "getWidth() : " + answerBitmap.getWidth() + ", getHeight() : " + answerBitmap.getHeight());
    				writeLog(PRJT, "interpX : " + interpolatedPivotX + ", interpY : " + interpolatedPivotY);
    				
    				int incorrectAnsHeight = answerBitmap.getHeight() - (interpolatedPivotY * 2);
    				if(incorrectAnsHeight < 1) {
    					incorrectAnsHeight = 1;
    				}
    				
    				answerBitmap = Bitmap.createBitmap(answerBitmap, 
													 							interpolatedPivotX, interpolatedPivotY, 		
													 							answerBitmap.getWidth() 
													 													- (interpolatedPivotX * 2), 
													 							incorrectAnsHeight);
    			}
    			writeLog(PRJT, "width : " + targetAnsBoxWidth + ", height : " + targetAnsBoxHeight);
    			answerBitmap = Bitmap.createScaledBitmap(answerBitmap, 
  																		  					 targetAnsBoxWidth, 
  																		  				   targetAnsBoxHeight, 
  																		  				   true);
    			answerImageView.setX(coordinateX);
    		  answerImageView.setY(coordinateY);
    			
    			/** 
    			 * 필기 인식 종료 후 해당 크기 만큼의 비트맵 객체 생성 - answerBitmap
    			 * ans_Bmp를 담을 캔버스 생성을 위해 같은 크기의 비트맵 객체 생성 - backBitmap
    			 * 생성한 캔버스에 빈 비트맵 backBitmap를 올려서 answerBitmap를 담을 준비 완료
    			 * backBitmap가 올려진 캔버스에 answerBitmap를 출력한다.
    			 * 출력한 이미지를 최종적으로 볼 수 있게 하기 위해 View 에 set 한다.
    			 * */
    			backBitmap = Bitmap.createBitmap(answerBitmap.getWidth(), 
    																			 answerBitmap.getHeight(), 
    																			 Bitmap.Config.ALPHA_8);
    			
    			canvas = new Canvas(backBitmap);
//    			canvas.drawBitmap(answerBitmap, null, new Rect(0, 0, 100, 100), null);
    			canvas.drawBitmap(answerBitmap, 0, 0, null);
    			
    			clearBitmap = answerBitmap;
    			answerImageView.setImageBitmap(answerBitmap);
//    			answerImageView.setImageBitmap(backBitmap);
    			// backBitmap로 세팅하는 게 맞음. 수정해야함.
//    			clearImgView = answerImageView;
    			
//    			answerBoxView.setAnswerBitmap(answerBitmap, coordinateX, coordinateY);
    		}
  		}
  		
  	};
  };
  /* 인식 종료 후 정규화되는 과정(필기 -> 디지털 기호) 종료 */

  /** 타이머, 태스크 취소 시작 */
  private void closeHandWriting() {
  	if(aniTimerTask != null) {
  		aniTimerTask.cancel();
  		aniTimerTask = null;
  	}
  	if(aniTimer != null) {
  		aniTimer.cancel();
  		aniTimer.purge();
  		aniTimer = null;
  	}
  	mWidget.clear(false);
  	((View)mWidget).setVisibility(View.VISIBLE);
  	
  }
  /* 타이머, 태스크 취소 종료 */
  
  /** 퀴즈 화면 로딩 시작 */
  public void loadQuiz() {
  	initQuiz();
  	// 웹 뷰에 문제 로드
  	try {
//    	mathContent.loadUrl(DataManager.mathContentUrl);
    } catch (Exception e) {
    	e.printStackTrace();
    }
  	
	}
  /* 퀴즈 화면 로딩 종료 */
  
  /** */
  private void initQuiz() {
  	initAnswerBox();
  	((View)mWidget).setVisibility(View.VISIBLE);
  	DataManager.qBoxNum = 1;
  	
  }
  
  private void setChapterTitleImg() {
  	chapterTitle = new ChapterTitleController().getInstance();
		chapterImgView.setVisibility(View.VISIBLE);
		String chapterImgName = /*DataManager.chapterTitleUrl +*/  
														DataManager.qChapterNum.substring(0, 3) + "_" + 
														DataManager.qChapterNum.substring(3) + ".png";
		writeLog(PRJT, "소챕터 이미지 : " + chapterImgName);
		chapterTitle.execute(chapterImgName, "chapterTitle");
  }
  
  /** mWidget.getResultAsImage() 초기화 시작 */
  private void initAnswerBox() {
  	InitAnswer ia = new InitAnswer(
											(ImageView)findViewById(R.id.answer_back), clearBitmap);
  	ia.clearAnswer();
  }
  /* mWidget.getResultAsImage() 초기화 종료 */

  /** dp를 px로 변환한 값을 반환  */
  private float convertDp(float value) {
  	return DataManager.dp2px(this, value);
  }
  
  /** px를 dp로 변환한 값을 반환 */
//  private float convertPx(float value) {
//  	return DataManager.px2dp(this, value);
//  }
  
  
  @Override
  public void onWritingBegin() {
    playSound.stopSound(DataManager.WRITE_SOUND);
    playSound.playSound(DataManager.WRITE_SOUND);
  }

  @Override
  public void onWritingEnd() {
    playSound.stopSound(DataManager.WRITE_SOUND);
  }

  // ----------------------------------------------------------------------
  // Math Widget styleable library - Errors

  @Override
  public Dialog onCreateDialog(final int id) {
    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    alertBuilder.setCancelable(false);
    switch (id) {
    // Language pack update missing resource
      case DIALOG_ERROR_RESSOURCE :
        alertBuilder.setTitle(R.string.langpack_parsing_error_title);
        alertBuilder.setMessage(R.string.langpack_parsing_error_msg);
        alertBuilder.setPositiveButton(android.R.string.ok, abortListener);
        break;
      // Certificate error
      case DIALOG_ERROR_CERTIFICATE :
        alertBuilder.setTitle(R.string.certificate_error_title);
        alertBuilder.setMessage(R.string.certificate_error_msg);
        alertBuilder.setPositiveButton(android.R.string.ok, abortListener);
        break;
      // Maximum item count error
      case DIALOG_ERROR_RECOTIMEOUT :
        alertBuilder.setTitle(R.string.recotimeout_error_title);
        alertBuilder.setMessage(R.string.recotimeout_error_msg);
        alertBuilder.setPositiveButton(android.R.string.ok, closeListener);
        break;
    }
    final AlertDialog alert = alertBuilder.create();
    return alert;
  }

  private final DialogInterface.OnClickListener closeListener = 
  																			new DialogInterface.OnClickListener() {
    @Override
    public void onClick(final DialogInterface di, final int position) {
//      mErrorDlgDisplayed = false;
    }
  };

  private final DialogInterface.OnClickListener abortListener = 
  																			new DialogInterface.OnClickListener() {
    @Override
    public void onClick(final DialogInterface di, final int position) {
//      mErrorDlgDisplayed = false;
      finish();
    }
  };
  
	/* 팝엄 메뉴 */
	public void popupMenu(View v) {
		View popupView = getLayoutInflater().inflate(R.layout.popup_window, 
																	(ViewGroup)findViewById(R.id.popup_element));

		mPopupWindow = new PopupWindow(popupView, 
																	 RelativeLayout.LayoutParams.WRAP_CONTENT, 
																	 LayoutParams.WRAP_CONTENT);

		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog); 
		mPopupWindow.showAsDropDown(v, 0, DataManager.pwLocationY);
		
		mPopupWindow.setTouchable(true); 
		mPopupWindow.setFocusable(false); 
		mPopupWindow.setOutsideTouchable(true); 
		mPopupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, 0, 0);
		
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
		nextButton.setEnabled(b);
		prevButton.setEnabled(b); 
		resetButton.setEnabled(b); 
		hintButton.setEnabled(b); 
		solButton.setEnabled(b);
	}
	
	private void writeLog(String tag, String msg) {
//		Log.i(tag, msg);
	}
}