package com.touchnedu.gradea.studya.math.modules;
//package com.myscript.atk.maw.sample;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import com.myscript.atk.maw.MathWidgetApi;
//import com.myscript.atk.maw.MathWidgetApi.RecognitionBeautification;
//import com.myscript.atk.maw.sample.resources.SimpleResourceHelper;
//import com.myscript.atk.maw.service.OnWindowFocusListener;
//import com.myscript.certificate.MyCertificate;
//
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//
//public class NoteFragment extends Fragment 
//													implements
//													MathWidgetApi.OnConfigureListener,
//										      MathWidgetApi.OnRecognitionListener,
//										      MathWidgetApi.OnGestureListener,
//										      MathWidgetApi.OnWritingListener,
//										      MathWidgetApi.OnTimeoutListener,
//										      MathWidgetApi.OnSolvingListener,
//										      MathWidgetApi.OnUndoRedoListener,
//													OnWindowFocusListener {
//	
//	private static final boolean DBG = BuildConfig.DEBUG;
//	private static final String TAG = "MainActivity";
//	/** Notify the user that a MSB resource is not found or invalid. */
//  public static final int DIALOG_ERROR_RESSOURCE = 0;
//  /** Notify the user that a MSB certificate is missing or invalid. */
//  public static final int DIALOG_ERROR_CERTIFICATE = 1;
//  /** Notify the user that maximum number of items has been reached. */
//  public static final int DIALOG_ERROR_RECOTIMEOUT = 2;
//  /** One error dialog at a time. */
//  private boolean mErrorDlgDisplayed = false;
//	
//	private View myscriptNoteView;
//	private MathWidgetApi noteWidget;
//	private FrameLayout fLayout;
//	private ImageView noteImgView, sliderImgView;
//	private Paint penPaint;
//	private Canvas canvas;
//	private Button clearButton;
//	
//	private float touchX, touchY, touchRawX, touchRawY, prvTouchRawX, 
//								prvTouchRawY, dstMemoX, dstMemoY;
//	private final float flickPower = 50f;
//	private final float easingRate = 0.5f;
//	private final int checkFlickDistance = 10;
//	private boolean onMotionCheck = false;
//	
//	private Timer aniTimer = null;
//	private TimerTask aniTimerTask = null;
//	
//	/* 프래그먼트의 UI를 구성하는 뷰(View)를 반환.
//	 * 실제 사용할 뷰를 만드는 작업을 수행.
//	 -> LayoutInflater를 매개변수로 받아서 layout으로 설정한 xml을 연결하거나
//	   bundle에 의한 작업을 하는 메소드.
//	 * UI를 가지지 않는 프래그먼트일 경우 null을 반환할 수 있음 */
//	@Override
//	public View onCreateView(LayoutInflater inflater, 
//															ViewGroup container, Bundle savedInstanceState) {
//		// ViewGroup container 는 Activity가 소유한 부모 ViewGroup.
//		// 즉, 이 그룹에 fragment가 속하게 된다.
//		
//		/** 두 번째 인자는 갖다 넣을 ViewGroup, 세 번째 인자는 attach 여부. 
//		 * container를 두 번째 인자로 넣을 경우 부모 레이아웃에 View를 생성하여
//		 * 자식 View로 넣는다. 
//		 * 아래와 같이 할 경우 시스템이 inflate된 레이아웃을 container에 집어넣기
//		 * 때문에 false를 해야 함. true로 하면 마지막 레이아웃에서 불필요한 View를
//		 * 생성하게 된다.*/
//		
//		Log.d(TAG, "fragment 진입");
//		
//		View mainView = inflater.inflate(R.layout.fragment_note, container, false);
//		
//		fLayout 			= (FrameLayout)mainView.findViewById(R.id.note_layout);
//		sliderImgView = (ImageView)mainView.findViewById(R.id.note_slider);
////		noteImgView 	= (ImageView)mainView.findViewById(R.id.note_back);
//		noteWidget = (MathWidgetApi)mainView.findViewById(R.id.myscript_note);
//		clearButton = (Button)mainView.findViewById(R.id.note_clearButton);
//		fLayout.setDrawingCacheEnabled(false);
//		
//		if(clearButton != null) {
//			clearButton.setVisibility(View.INVISIBLE);
//			clearButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					clearNoteView();
//					
//				}
//			});
//		}
//		
//		mainView.setDrawingCacheEnabled(false);
//		
//		noteWidget.setOnConfigureListener(this);
//		noteWidget.setInkThickness(20);
//		noteWidget.setBeautificationOption(RecognitionBeautification.BeautifyDisabled);
//		noteWidget.setBaselineColor(View.INVISIBLE);	
//		noteWidget.setBackgroundColor(Color.argb(64, 0, 0, 0));
//		noteWidget.setInkColor(Color.rgb(255, 255, 255));
//		((View)noteWidget).setDrawingCacheEnabled(false);
//		configure();
//    
//		return mainView;
//	}
//
//
//	public void clearNoteView() {
//		noteWidget.clear(false);
//		
//	}
//	
//	/* Activity의 onCreate()가 완료된 다음 생성되는 메소드.
//	 * 이 메소드가 호출될 때는 Activity의 모든 View가 만들어지고 난 다음이다.  
//	 * */
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		Log.d("prjt", "onActivityCreated 0");
//		
//		sliderImgView.setOnTouchListener(new View.OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				
//				switch(event.getAction()) {
//				
//				case MotionEvent.ACTION_DOWN:
//					cancelFlickMemo();
//					// 최초 터치 지점의 원시 X, Y 값을 담아둔다.
//					prvTouchRawX = touchRawX = event.getRawX();
//					prvTouchRawY = touchRawY = event.getRawY();
//					break;
//					
//				case MotionEvent.ACTION_MOVE:
//					// 움직임이 발생했을 때 그 움직인 거리를 movedX, movedY에 담는다.
//					float movedX = event.getRawX() - touchRawX;
//					float movedY = event.getRawY() - touchRawY;
//					// 움직인 거리를 moveNoteView에 전달.
//					moveNoteView(movedX, movedY, false);
//					
//					prvTouchRawX = touchRawX;
//					prvTouchRawY = touchRawY;
//					
//				  // 움직임이 끝나면 그 원시 좌표를 touchRaw에 담는다.
//					touchRawX = event.getRawX();
//					touchRawY = event.getRawY();
//					break;
//					
//				case MotionEvent.ACTION_UP:
//					/** < 연습장을 드래그 했을 때의 이벤트 >
//					 * 1. 드래그가 종료되면 그 시점의 X, Y 좌표를 coordinate에 담아둔다.
//					 * 2. 이동한 거리 만큼의 원시 좌표를 movedRawX, Y에 담는다.
//					 * 3. 이동한 수평 거리가 10보다 크고 이동거리 X가 이동거리 Y보다
//					 *   크면 coordinateX와 (50.0*이동거리 X)의 합을 0과 비교하여 큰 값을
//					 *   coordinateX에 담는다.
//					 * 4. 작으면 coordinateY와 (50.0*이동거리 Y)의 합을 0과 비교하여 작은
//					 *   값을 coordinateY에 담는다. 
//					 * 5. coordinateX, coordinateY의 값을 moveNoteView에 전달한다.
//					 *  
//					 * <정리> X는 왼쪽으로만 움직이고 Y는 아래쪽으로만 움직인다. */
//					
//					// 1
//					float coordinateX = sliderImgView.getX();
//					float coordinateY = sliderImgView.getY();
//					
//					// 2
//					float movedRawX = event.getRawX() - prvTouchRawX;
//					float movedRawY = event.getRawY() - prvTouchRawY;
//					
//					// 3
//					if(Math.abs(movedRawX) > checkFlickDistance 
//														&& Math.abs(movedRawX) > Math.abs(movedRawY)) {
//						
//						coordinateX = Math.max(0, coordinateX + flickPower * movedRawX);
//						
//					// 4	
//					} else if(Math.abs(movedRawX) > checkFlickDistance
//														&& Math.abs(movedRawX) < Math.abs(movedRawY)) {
//						
//						coordinateY = Math.min(0, coordinateY + flickPower * movedRawY);
//					}
//					
//					// 5
//					moveNoteView(coordinateX, coordinateY, true);
//					
//					v.performClick();
//					break;
//				
//				}
//				
//				/* false면 해당 뷰에서 터치 이벤트를 사용하지 않고 전달함.
//				 * true면 반대로 해당 뷰에서 이벤트를 사용하고 전달하지 않고 끝냄. 
//				 * onTouch > onLongClick > onClick 순인데 onTouch에서 끝남. */
//				return true;
//			}
//		});
//		
//	}
//	
//	/* Fragment가 화면에 표시된다. 화면의 모든 UI가 만들어지고 난 후 호출 */
//	@Override
//	public void onStart() {
//		super.onStart();
//		Log.d(TAG, "메모 프래그먼트 시작!");
//		
//	}
//
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		/** 연습장의 전체 영역, 그리기 가능 영역, 스크롤 영역 세팅*/
//		DataManager.noteViewAreaW = fLayout.getWidth();
//		DataManager.noteViewAreaH = fLayout.getHeight();
//		DataManager.drawableAreaW = ((View)noteWidget).getWidth();
//		DataManager.drawableAreaH = ((View)noteWidget).getHeight();
//		DataManager.slideBarW = sliderImgView.getWidth();
//		
//		setNoteView(DataManager.stageW, 0);
//		clearButton.setVisibility(View.INVISIBLE);
//		
//	}
//
//
//	private void moveNoteView(float movedX, float movedY, boolean motion) {
//
//		// 연습장 이동 구현
//		if(motion && !onMotionCheck) {
//			onMotionCheck = true;
//			
//			final int tempX = (int)movedX;
//			final int tempY = (int)movedY;
//			
//			aniTimer = new Timer();
//			aniTimerTask = new TimerTask() {
//				@Override
//				public void run() {
//					dstMemoX = Math.max(tempX, 0);
//					dstMemoX = Math.max(dstMemoX, DataManager.noteViewAreaW - DataManager.drawableAreaW - DataManager.slideBarW);
//					dstMemoX = Math.min(dstMemoX, DataManager.noteViewAreaW - DataManager.slideBarW / 2);
//					
//					dstMemoY = Math.min(tempY, 0);
//					dstMemoY = Math.max(dstMemoY, DataManager.noteViewAreaH - DataManager.drawableAreaH);
//					
//					timerFlickMemo();
//					
//				}
//			};
//			
//			aniTimer.scheduleAtFixedRate(aniTimerTask, 0, 33);
//			
//		} else {
//			onMoveNoteView(movedX, movedY);
//		}
//		
//	}
//	
//	protected void timerFlickMemo() {
//		if(getActivity() != null) {
//			getActivity().runOnUiThread(generate);
//		} else {
//			Log.i("prjt", "timerFlickMemo 0 getActivity() is null");
//		}
//		
//	}
//	
//	private Runnable generate = new Runnable() {
//		@Override
//		public void run() {
//			float currX = dstMemoX - (dstMemoX - sliderImgView.getX()) * easingRate;
//			float currY = dstMemoY - (dstMemoY - sliderImgView.getY()) * easingRate;
//			
//			if(Math.abs(currX - dstMemoX) < 5 && Math.abs(currY - dstMemoY) < 5) {
//				currX = dstMemoX;
//				currY = dstMemoY;
//				cancelFlickMemo();
//			}
//			
//			onMoveNoteView(currX - sliderImgView.getX(), currY - sliderImgView.getY());
//			
//		}
//	};
//
//	private void cancelFlickMemo() {
//		onMotionCheck = false;
//		
//		if(aniTimer != null) {
//			aniTimer.cancel();
//		}
//		if(aniTimerTask != null) {
//			aniTimerTask.cancel();
//		}
//		
//	}
//	
//	private void onMoveNoteView(float movedX, float movedY) {
//		int barW = DataManager.slideBarW;
//		int memoContentW = DataManager.drawableAreaW;
//		int memoContentH = DataManager.drawableAreaH;
//		
//		// 상단 마감
//		float finishT = Math.min(0, sliderImgView.getY() + movedY);
//		movedY = finishT - sliderImgView.getY();
//		finishT = Math.max(DataManager.noteViewAreaH - memoContentH, sliderImgView.getY() + movedY);
//		movedY = finishT - sliderImgView.getY();
//		
//		// 오른쪽 마감
//		float finishR = Math.max(sliderImgView.getX() + movedX, 0);
//		finishR = Math.max(finishR, DataManager.noteViewAreaW - (DataManager.drawableAreaW + DataManager.slideBarW));
//		finishR = Math.min(finishR, DataManager.noteViewAreaW - DataManager.slideBarW / 2);
//		movedX = finishR - sliderImgView.getX();
//		
//		/** 연습장 위치 세팅 */
//		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(barW, memoContentH);
//		layoutParams.setMargins((int)(sliderImgView.getX() + movedX), 
//														(int)(sliderImgView.getY() + movedY), 
//														0, 0);
//		sliderImgView.setLayoutParams(layoutParams);
//		/* 스크롤 뷰 세팅 */
//		
//		layoutParams = new FrameLayout.LayoutParams(memoContentW, memoContentH);
//		layoutParams.setMargins((int)(sliderImgView.getX() + barW + movedX), (int)(sliderImgView.getY() + movedY), 0, 0);
//		((View)noteWidget).setLayoutParams(layoutParams);
//		/* 연습장 뷰 세팅 */
//		
//		if(sliderImgView.getX() + DataManager.slideBarW < DataManager.noteViewAreaW) {
//			clearButton.setVisibility(View.VISIBLE);
//		} else {
//			clearButton.setVisibility(View.INVISIBLE);
//		}
//		
//	}
//
//
//	/* 사용자와 상호작용이 가능한 상태. 이벤트 적용 가능. */
////	@Override
////	public void onResume() {
////		super.onResume();
////	}
//
//	private void setNoteView(int width, int height) {
//		int sendToMoveNoteViewX = (int)(width - sliderImgView.getX());
//		int sendToMoveNoteViewY = (int)(height - sliderImgView.getY());
//		
//		moveNoteView(sendToMoveNoteViewX, sendToMoveNoteViewY, false);
//		
//	}
//
//	
//	/** MathWidget 추상 메소드 구현 */
//	@Override
//	public void onUndoRedoStateChanged() {
//		if (DBG)
//      Log.d(TAG, "End writing");
//		
//	}
//
//	@Override
//	public void onUsingAngleUnitChanged(final boolean used) {
//		if (DBG)
//      Log.d(TAG, "onUsingAngleUnitChanged");
//		
//	}
//
//	@Override
//	public void onRecognitionTimeout() {
//		Log.d(TAG, "onRecognitionTimeout");
//	}
//
//	@Override
//	public void onWritingBegin() {
//		if (DBG)
//      Log.d(TAG, "Start writing 고고");
//	}
//
//	@Override
//	public void onWritingEnd() {
//		if (DBG)
//      Log.d(TAG, "End writing 고고");
//	}
//
//	@Override
//	public void onEraseGesture(final boolean partial) {
//		Log.d(TAG, "지우기");
//		
//	}
//
//	@Override
//	public void onRecognitionBegin() {
//		if (DBG)
//	    Log.d(TAG, "Equation recognition begins");
//	}
//
//	@Override
//	public void onRecognitionEnd() {
//		if (DBG)
//	    Log.d(TAG, "onRecognitionEnd noteWidget : " + noteWidget);
//		Log.d(TAG, "값은 : " + noteWidget.getResultAsText());
//	}
//
//	private void configure() {
//		// Equation resource    
//    final String[] resources = new String[]{"math-ak.res", "math-grm-maw.res"};
//
//    // Prepare resources
//    final String subfolder = "math";
//    final String resourcePath = new String(getActivity().getFilesDir().getPath() + java.io.File.separator + subfolder);
//    Log.d(TAG, "noteWidget resourcePath : " + resourcePath);
//    Log.d(TAG, "resources : " + resources);
//    SimpleResourceHelper
//        .copyResourcesFromAssets(getActivity().getAssets(), subfolder /* from */, resourcePath /* to */, resources /* resource names */);
//
//    // Configure math widget
//    noteWidget.setResourcesPath(resourcePath);
//    noteWidget.configure(getActivity().getBaseContext(), resources, MyCertificate.getBytes(), MathWidgetApi.AdditionalGestures.DefaultGestures);
//    
//  }
//	
//	@Override
//	public void onConfigurationBegin() {
//		if (DBG)
//      Log.d(TAG, "Equation configuration begins");
//		
//	}
//
//	@Override
//	public void onConfigurationEnd(boolean success) {
//		if (DBG) {
//      if (success)
//        Log.d(TAG, "Equation configuration succeeded");
//      else
//        Log.d(TAG, "Equation configuration failed (" + noteWidget.getErrorString() + ")");
//    }
//    if (DBG) {
//      if (success)
//        Log.d(TAG, "Equation configuration loaded successfully");
//      else
//        Log.d(
//            TAG,
//            "Equation configuration error - did you copy the equation resources to your SD card? ("
//                + noteWidget.getErrorString() + ")");
//    }
//		
//	}
//	
//	
//	
//}
