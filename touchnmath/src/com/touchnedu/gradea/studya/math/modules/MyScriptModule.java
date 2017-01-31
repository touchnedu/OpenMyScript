package com.touchnedu.gradea.studya.math.modules;

import java.util.Timer;
import java.util.TimerTask;

import com.myscript.atk.maw.MathWidgetApi;
import com.myscript.atk.maw.MathWidgetApi.RecognitionBeautification;
import com.myscript.atk.maw.sample.resources.SimpleResourceHelper;
import com.myscript.certificate.MyCertificate;
import com.touchnedu.gradea.studya.math.BuildConfig;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.PlaySound;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

public class MyScriptModule implements MathWidgetApi.OnConfigureListener,
																			 MathWidgetApi.OnRecognitionListener,
																			 MathWidgetApi.OnWritingListener {

	private static final String TAG = "MyScriptModule";
	private static final boolean DBG = BuildConfig.DEBUG;
	
	private ContentManager cm = ContentManager.getInstance();
	private WebViewMath wvMath = WebViewMath.getInstance();
	private ViewModule vm = ViewModule.getInstance();
	private MathWidgetApi mWidget;
	private Context mContext;
	private Activity mActivity;
	
	private PlaySound playSound;
	
	private Timer aniTimer = null;
	private TimerTask aniTimerTask = null;
	private int aniFrameCount = 0;
	private String resultTxt = "";
	
	public MyScriptModule(Context mContext) {
		this.mContext = mContext;
	}
	
	public void initMyScriptWidget() {
		mActivity = (Activity)mContext;
		playSound = PlaySound.getInstance(mContext);
		/** ATK 기본 필수 세팅 */
    mWidget = (MathWidgetApi)mActivity.findViewById(R.id.myscript_maw);
    mWidget.setOnConfigureListener(this);
    mWidget.setOnRecognitionListener(this);
    mWidget.setOnWritingListener(this);
    
    /** 커스텀 세팅 시작 */
    mWidget.setBeautificationOption(RecognitionBeautification.BeautifyFontify);
    mWidget.setBaselineColor(View.INVISIBLE);	
    mWidget.setBackgroundColor(View.INVISIBLE);
    mWidget.setItalicTypeface(Typeface.createFromAsset(mContext.getAssets(), 
    																								"STIXGeneral-Italic.ttf")); 
    mWidget.setInkThickness((int)DataManager.penStrokeWidth);
    configure();
	}
	
	public void widgetRelease() {
		mWidget.release(mContext);
	}
	
	public void widgetClear(boolean b) {
		mWidget.clear(b);
	}
	
	public void setWidgetVisibility(int visibility) {
		((View)mWidget).setVisibility(visibility);
	}
	
	public void closeHandWriting() {
		if(aniTimerTask != null) {
			aniTimerTask.cancel();
			aniTimerTask = null;
		}
		if(aniTimer != null) {
			aniTimer.cancel();
			aniTimer.purge();
			aniTimer = null;
		}
		widgetClear(false);
		setWidgetVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onWritingBegin() {
		playSound.stopSound(DataManager.RIGHT_SOUND);
		playSound.playSound(DataManager.RIGHT_SOUND);
	}

	@Override
	public void onWritingEnd() {
		playSound.stopSound(DataManager.RIGHT_SOUND);
	}

	@Override
	public void onRecognitionBegin() { }

	@Override
	public void onRecognitionEnd() {
		aniFrameCount = 0;
		if(mWidget.getResultAsText().equals(""))
			return;
		
		setWidgetVisibility(View.INVISIBLE);
		
		if(aniTimerTask == null) {
			aniTimerTask = new TimerTask() {
				@Override
				public void run() {
					mActivity.runOnUiThread(generateAnswer);
				}
			};
		}
		
		if(aniTimer == null) {
			aniTimer = new Timer();
			aniTimer.scheduleAtFixedRate(aniTimerTask, 0, 50);
		}
		
	}
	
	private Runnable generateAnswer = new Runnable() {
		@Override
		public void run() {
			int aniFrame = 12;
			aniFrameCount++;
		
			if(aniFrameCount == 1) {
				resultTxt = mWidget.getResultAsText();
				if(resultTxt.equals("⟩"))
					resultTxt = ">";
				else if(resultTxt.equals("⟨"))
					resultTxt = "<";
				
				if(resultTxt.contains(","))
					resultTxt = resultTxt.replace(",", ".");
				
			}
			
			if(aniFrameCount > aniFrame) {
				closeHandWriting();
				if(resultTxt.equals(cm.getAnswer())) {
					wvMath.loadUrl("javascript:correctAnswer('" 
																								+ DataManager.qBoxNum + "')");
					// initAnswerBox(); 구현
					vm.initAnswerBox();
					
				} else {
					playSound.playSound(DataManager.WRONG_SOUND);
					if(!resultTxt.equals(""))
						wvMath.loadUrl("javascript:incorrectAnswer('" 
																								+ DataManager.qBoxNum + "')");
				}
				
			} else {
				setAnswerBitmap();
					
			}
			
		}
		
	};
	
	private void setAnswerBitmap() {
		Canvas canvas;
		Bitmap backBitmap, answerBitmap;
		
		answerBitmap = mWidget.getResultAsImage(mContext, R.id.answer_back);
		if(answerBitmap == null) 
			return;
		if(answerBitmap.getHeight() <= DataManager.minHeight()) {
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
			int wrongAnsHeight = answerBitmap.getHeight() - (interpolatedPivotY * 2);
			if(wrongAnsHeight < 1)
				wrongAnsHeight = 1;
			
			answerBitmap = Bitmap.createBitmap(
														answerBitmap, interpolatedPivotX,	
														interpolatedPivotY,
														answerBitmap.getWidth() - (interpolatedPivotX * 2),
														wrongAnsHeight);
			
		}
		answerBitmap = Bitmap.createScaledBitmap(answerBitmap, 
																						 cm.getTargetAnsBoxWidth(), 
																						 cm.getTargetAnsBoxHeight(), 
																						 true);
		vm.setAnsImgVwPosition();
		
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
//	canvas.drawBitmap(answerBitmap, null, new Rect(0, 0, 100, 100), null);
		canvas.drawBitmap(answerBitmap, 0, 0, null);
		vm.setClearBitmap(answerBitmap);
		vm.setAnsImgVwBitmap(answerBitmap);
		
	}

	/** ----------------------------------------------------------------------- */
	@Override
	public void onConfigurationBegin() { 
		if (DBG)
      Log.d(TAG, "Equation configuration begins");
	}

	@Override
	public void onConfigurationEnd(final boolean success) {	
		if (DBG) {
      if (success)
        Log.d(TAG, "Equation configuration succeeded");
      else
        Log.d(TAG, "Equation configuration failed (" 
        																			+ mWidget.getErrorString() + ")");
    }
    if (DBG) {
      if (success)
        Log.d(TAG, "Equation configuration loaded successfully");
      else
        Log.d(TAG, "Equation configuration error "
        					 + "- did you copy the equation resources to your SD card? ("
                	 + mWidget.getErrorString() + ")");
    }
	}
	
  private void configure() {
    // Equation resource    
    final String[] resources = new String[]{"math-ak.res", "math-grm-maw.res"};

    // Prepare resources
    final String subfolder = "math";
    final String resourcePath = new String(mContext.getFilesDir().getPath() 
    																			+ java.io.File.separator + subfolder);
    SimpleResourceHelper.copyResourcesFromAssets(mContext.getAssets(), 
    																						 subfolder /* from */, 
    																						 resourcePath /* to */, 
    																						 resources /* resource names */);

    // Configure math widget
    mWidget.setResourcesPath(resourcePath);
    mWidget.configure(mContext, resources, MyCertificate.getBytes(), 
    													MathWidgetApi.AdditionalGestures.DefaultGestures);
  }

}
