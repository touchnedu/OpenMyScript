package com.touchnedu.gradea.studya.math.modules;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.OnWindowFocusListener;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class NoteFragment extends Fragment implements OnWindowFocusListener, 
																		 									View.OnTouchListener {
	
	private static final String MEMOFRG = "MemoFragment";
	
	private FrameLayout fLayout;
	private ImageView noteImgView, handlerImgView01, handlerImgView02, 
										handlerImgView03, sliderImgView, invisibleSlider;
	private Paint penPaint;
	private Canvas canvas;
	private Button clearButton, undoButton;
	
	private float touchX, touchY, touchRawX, touchRawY, prvTouchRawX, 
								prvTouchRawY, dstMemoX, dstMemoY;
	private final float flickPower = 75f;
	private final float easingRate = 0.5f;
	private final int checkFlickDistance = 10;
	private int handlerImgChange = 0;
	private int minusMargin = 0;
	private boolean onMotionCheck = false;
	private boolean startLocation = false;
	private boolean isMoving = false;
//	private boolean defaultLocation = false;
	
	private Timer aniTimer = null;
	private TimerTask aniTimerTask = null;
	
	private ArrayList<CustomDraw> drawArray = new ArrayList<CustomDraw>();
	
	/* 프래그먼트의 UI를 구성하는 뷰(View)를 반환.
	 * 실제 사용할 뷰를 만드는 작업을 수행.
	 -> LayoutInflater를 매개변수로 받아서 layout으로 설정한 xml을 연결하거나
	   bundle에 의한 작업을 하는 메소드.
	 * UI를 가지지 않는 프래그먼트일 경우 null을 반환할 수 있음 */
	@Override
	public View onCreateView(LayoutInflater inflater, 
															ViewGroup container, Bundle savedInstanceState) {
		// ViewGroup container 는 Activity가 소유한 부모 ViewGroup.
		// 즉, 이 그룹에 fragment가 속하게 된다.
		
		/** 두 번째 인자는 갖다 넣을 ViewGroup, 세 번째 인자는 attach 여부. 
		 * container를 두 번째 인자로 넣을 경우 부모 레이아웃에 View를 생성하여
		 * 자식 View로 넣는다. 
		 * 아래와 같이 할 경우 시스템이 inflate된 레이아웃을 container에 집어넣기
		 * 때문에 false를 해야 함. true로 하면 마지막 레이아웃에서 불필요한 View를
		 * 생성하게 된다.*/
		View mainView = inflater.inflate(R.layout.fragment_note, container, false);
		
		fLayout = (FrameLayout)mainView.findViewById(R.id.note_layout);
		handlerImgView01 = (ImageView)mainView.findViewById(R.id.note_slider01_01);
		handlerImgView02 = (ImageView)mainView.findViewById(R.id.note_slider01_02);
		handlerImgView03 = (ImageView)mainView.findViewById(R.id.note_slider01_03);
		invisibleSlider = (ImageView)mainView.findViewById(R.id.note_invisible_slider);
		sliderImgView	= (ImageView)mainView.findViewById(R.id.note_slider02);
		noteImgView = (ImageView)mainView.findViewById(R.id.note_back);
		clearButton = (Button)mainView.findViewById(R.id.note_clearButton);
		undoButton = (Button)mainView.findViewById(R.id.note_undoButton);
		
		sliderImgView.setOnTouchListener(this);
		
		if(clearButton != null) {
			clearButton.setVisibility(View.INVISIBLE);
			clearButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clearNoteView();
					
				}
			});
		}
		if(undoButton != null) {
			undoButton.setVisibility(View.INVISIBLE);
			undoButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					/** Undo 구현 */
					undoDraw();
					for(int i = 0; i <= drawArray.size() - 1; i++) {
						if(drawArray.get(i).onMotion) {
							canvas.drawLine(drawArray.get(i - 1).getX, 
															drawArray.get(i - 1).getY, 
															drawArray.get(i).getX, 
															drawArray.get(i).getY, 
															penPaint);
							
						}
					}
				}
			});
		}
		
		return mainView;
	}

	/** 연습장 초기화 */
	public void clearNoteView() {
		drawArray.clear();
		if(DataManager.noteBitmap != null) {
			clearNoteBitmap();
		}
	}
	
	/** 연습장 비트맵 초기화 */
	private void clearNoteBitmap() {
		DataManager.noteBitmap.recycle();
		DataManager.noteBitmap = Bitmap.createBitmap(DataManager.drawableAreaW,
																								 DataManager.drawableAreaH, 
																								 Config.ALPHA_8);
		canvas = new Canvas(DataManager.noteBitmap);
		noteImgView.setImageBitmap(DataManager.noteBitmap);
	}
	
	/** UndoDraw 구현 */
	private void undoDraw() {
		int idx = 0;
		for(idx = drawArray.size() - 1; idx >= 0; idx--) {
			if(drawArray.get(idx).onMotion) {
				drawArray.remove(idx);
			} else {
				drawArray.remove(idx);
				break;
			}
		}
		clearNoteBitmap();
		
	}
	
	/** 슬라이더 위치 초기화 */
	public void setSliderBarInit() {
		setNoteView(DataManager.stageW, 0);
	}
	
	/* Activity의 onCreate()가 완료된 다음 생성되는 메소드.
	 * 이 메소드가 호출될 때는 Activity의 모든 View가 만들어지고 난 다음이다. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		penPaint = new Paint();
		penPaint.setStrokeWidth(DataManager.penStrokeWidth / 2);
		penPaint.setStrokeCap(Cap.ROUND);
		
		/** DRAW 구현 */
		noteImgView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					drawArray.add(new CustomDraw(event.getX(), event.getY(), false));
					touchX = event.getX();
					touchY = event.getY();
					touchRawX = event.getRawX();
					touchRawY = event.getRawY();
					break;
					
				case MotionEvent.ACTION_MOVE:
					drawArray.add(new CustomDraw(event.getX(), event.getY(), true));
					
					canvas.drawLine(touchX, touchY, event.getX(), event.getY(), penPaint);
					noteImgView.setImageBitmap(DataManager.noteBitmap);
					
					touchX = event.getX();
					touchY = event.getY();
					touchRawX = event.getRawX();
					touchRawY = event.getRawY();
					break;
					
				case MotionEvent.ACTION_UP:
					v.performClick();
					break;
					
				}
				
				return true;
			}
		});
	}
	
	/* Fragment가 화면에 표시된다. 화면의 모든 UI가 만들어지고 난 후 호출 */
	@Override
	public void onStart() {
		Log.d("prjt", "메모 프래그먼트 시작");
		super.onStart();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		/** 연습장의 전체 영역, 그리기 가능 영역, 스크롤 영역 세팅*/
		DataManager.noteViewAreaW = fLayout.getWidth();
		DataManager.noteViewAreaH = fLayout.getHeight();
		DataManager.drawableAreaW = noteImgView.getWidth();
		DataManager.drawableAreaH = noteImgView.getHeight();
		DataManager.slideBarW = sliderImgView.getWidth();
		DataManager.handlerW = handlerImgView01.getWidth();
//		defaultLocation = true;
		
		if(DataManager.noteBitmap == null) {
			DataManager.noteBitmap = Bitmap.createBitmap(DataManager.drawableAreaW, 
																									 DataManager.drawableAreaH, 
																									 Config.ALPHA_8);
		}
		
		canvas = new Canvas(DataManager.noteBitmap);
		noteImgView.setImageBitmap(DataManager.noteBitmap);
		
		/** stageW는 디바이스의 가로 해상도 */
		setNoteView(DataManager.stageW, 0);
		clearButton.setVisibility(View.INVISIBLE);
		undoButton.setVisibility(View.INVISIBLE);
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()) {
	
		case MotionEvent.ACTION_DOWN:
			cancelFlickMemo();
			// 최초 터치 지점의 원시 X, Y 값을 담아둔다.
			prvTouchRawX = touchRawX = event.getRawX();
			prvTouchRawY = touchRawY = event.getRawY();
//			Log.d(MEMOFRG, "* --- Start 슬라이더 좌표 : " + sliderImgView.getX() + " --- *");
			break;
			
		case MotionEvent.ACTION_MOVE:
			// 움직임이 발생했을 때 그 움직인 거리를 movedX, movedY에 담는다.
			float movedX = event.getRawX() - touchRawX;
			float movedY = event.getRawY() - touchRawY;
			// 움직인 거리를 moveNoteView에 전달.
			moveNoteView(movedX, movedY, false);
			
			prvTouchRawX = touchRawX;
			prvTouchRawY = touchRawY;
			
		  // 움직임이 끝나면 그 원시 좌표를 touchRaw에 담는다.
			touchRawX = event.getRawX();
			touchRawY = event.getRawY();
			
			startLocation = false;
			isMoving = true;
			
			break;
			
		case MotionEvent.ACTION_UP:
			/* < 연습장을 드래그 했을 때의 이벤트 >
			 * 1. 드래그가 종료되면 그 시점의 X, Y 좌표를 coordinate에 담아둔다.
			 * 2. 이동한 거리 만큼의 원시 좌표를 movedRawX, Y에 담는다.
			 * 3. 이동한 수평 거리가 10보다 크고 이동거리 X가 이동거리 Y보다
			 *   크면 coordinateX와 (50.0*이동거리 X)의 합을 0과 비교하여 큰 값을
			 *   coordinateX에 담는다.
			 * 4. 작으면 coordinateY와 (50.0*이동거리 Y)의 합을 0과 비교하여 작은
			 *   값을 coordinateY에 담는다. 
			 * 5. coordinateX, coordinateY의 값을 moveNoteView에 전달한다.
			 *  
			 * <정리> X는 왼쪽으로만 움직이고 Y는 아래쪽으로만 움직인다. */
			
			// 1
			float coordinateX = sliderImgView.getX();
			float coordinateY = sliderImgView.getY();
			
			// 2
			float movedRawX = event.getRawX() - prvTouchRawX;
			float movedRawY = event.getRawY() - prvTouchRawY;
			
			// 3
			if(Math.abs(movedRawX) > checkFlickDistance 
												&& Math.abs(movedRawX) > Math.abs(movedRawY)) {
				
				coordinateX = Math.max(0, coordinateX + flickPower * movedRawX);
				
			// 4	
			} else if(Math.abs(movedRawX) > checkFlickDistance
												&& Math.abs(movedRawX) < Math.abs(movedRawY)) {
				
				coordinateY = Math.min(0, coordinateY + flickPower * movedRawY);
				// 이동 거리가 flickCheck 거리보다 크더라도 
				// y축 이동 거리가 더 크면 x축으로만 움직인다.
			}
			
			isMoving = false;
			// 5
			moveNoteView(coordinateX, coordinateY, true);
			v.performClick();
			break;
		
		}
		return true;
	}
	
	private void moveNoteView(float movedX, float movedY, boolean motion) {

		// 연습장 이동 구현
		if(motion && !onMotionCheck) { /** 손을 떼었을 때 */
			onMotionCheck = true;
			final int tempX = (int)movedX;
			final int tempY = (int)movedY;
			
			aniTimer = new Timer();
			aniTimerTask = new TimerTask() {
				@Override
				public void run() {
					dstMemoX = Math.max(tempX, -(DataManager.handlerW / 2));
					dstMemoX = Math.max(dstMemoX, DataManager.noteViewAreaW 
																				- DataManager.drawableAreaW 
																				- DataManager.slideBarW
																				- DataManager.handlerW);
					
					dstMemoX = Math.min(dstMemoX, 
												DataManager.noteViewAreaW	- DataManager.handlerW / 2);
					if(dstMemoX == 0) {
						minusMargin = handlerImgView01.getWidth() + 1;
						startLocation = true;
					} else {
						minusMargin = 0;
					}
					if(dstMemoX == 1028) {
						handlerImgChange = 1;
					}
					
					dstMemoY = Math.min(tempY, 0);
					dstMemoY = Math.max(dstMemoY, DataManager.noteViewAreaH 
																									- DataManager.drawableAreaH);
					
					timerFlickMemo();
					
				}
			};
			aniTimer.scheduleAtFixedRate(aniTimerTask, 0, 33);
			
		} else { /** 연습장이 MOVE 일 때 */
			onMoveNoteView(movedX, movedY);
		}
		
	}
	
	protected void timerFlickMemo() {
		if(getActivity() != null) {
			getActivity().runOnUiThread(generate);
		} else {
			Log.i("prjt", "timerFlickMemo > getActivity() is null");
		}
		
	}
	
	private Runnable generate = new Runnable() {
		
		@Override
		public void run() {
			float currX = dstMemoX - (dstMemoX - sliderImgView.getX()) * easingRate;
			float currY = dstMemoY - (dstMemoY - sliderImgView.getY()) * easingRate;
			
			if(Math.abs(currX - dstMemoX) < 64 && Math.abs(currY - dstMemoY) < 5) {
				currX = dstMemoX;
				currY = dstMemoY;
				cancelFlickMemo();
			}
			onMoveNoteView(currX - sliderImgView.getX() - minusMargin, 
																								currY - sliderImgView.getY());
			
		}
	};

	private void cancelFlickMemo() {
		if(handlerImgChange == 1) {
			handlerImgView01.setVisibility(View.VISIBLE);
			handlerImgView03.setVisibility(View.INVISIBLE);
			handlerImgChange = 0;
		} else {
			handlerImgView01.setVisibility(View.INVISIBLE);
			handlerImgView03.setVisibility(View.VISIBLE);
		}
		onMotionCheck = false;
		
		if(aniTimer != null) {
			aniTimer.cancel();
		}
		if(aniTimerTask != null) {
			aniTimerTask.cancel();
		}
		
	}
	
	private void onMoveNoteView(float movedX, float movedY) {
		int barW = DataManager.slideBarW;
		int memoContentW = DataManager.drawableAreaW;
		int memoContentH = DataManager.drawableAreaH;
		
		/* movedX의 초기값은 디바이스의 가로 해상도가 된다. */
		/* 상단 마감 */
		float finishT = Math.min(0, sliderImgView.getY() + movedY);
		movedY = finishT - sliderImgView.getY();
		finishT = Math.max(DataManager.noteViewAreaH - memoContentH, 
											 sliderImgView.getY() + movedY);
		movedY = finishT - sliderImgView.getY();
		
		/* 오른쪽 마감 */
		float finishR = Math.max(sliderImgView.getX() + movedX, 
																									-(DataManager.handlerW / 2));
		
		finishR = Math.max(finishR, DataManager.noteViewAreaW - 
													(DataManager.drawableAreaW + DataManager.slideBarW));
		
		finishR = Math.min(finishR, 
											 DataManager.noteViewAreaW - DataManager.handlerW / 2);
		
		movedX = finishR - sliderImgView.getX();
		
		/** 연습장 위치 세팅 */
		/* 스크롤 뷰 세팅 */
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(barW, memoContentH);
		layoutParams.setMargins((int)(sliderImgView.getX() + movedX), 
														(int)(sliderImgView.getY() + movedY),	0, 0);
		sliderImgView.setLayoutParams(layoutParams);
		
		layoutParams = new FrameLayout.LayoutParams(invisibleSlider.getWidth(), invisibleSlider.getHeight());
		
		if(!isMoving) {
			layoutParams.setMargins((int)(invisibleSlider.getX() + movedX), 0, 0, 0);
			invisibleSlider.setLayoutParams(layoutParams);
		} else {
			layoutParams.setMargins((int)(invisibleSlider.getX() + movedX), 
															(int)(invisibleSlider.getY() + movedY), 0, 0);
			invisibleSlider.setLayoutParams(layoutParams);
		}
		
		/* 핸들러 뷰1 세팅 */
		float handlerSetY = invisibleSlider.getY()  
											+ (invisibleSlider.getHeight() / 2)
											- (handlerImgView01.getHeight() / 2);
		float handlerFixedY = (DataManager.stageH - DataManager.statusH) / 2  
																						- handlerImgView01.getHeight() / 2;
		
		layoutParams = new FrameLayout.LayoutParams(handlerImgView01.getWidth(), 
																								handlerImgView01.getHeight());
		
		if(isMoving) {
			layoutParams.setMargins((int)(handlerImgView01.getX() + movedX), 
															(int)(handlerSetY + movedY), 0, 0);
		} else {
			layoutParams.setMargins((int)(handlerImgView01.getX() + movedX), 
															(int)handlerFixedY, 0, 0);
		}
		
		if(startLocation || sliderImgView.getX() < -50) {
			handlerImgView02.setVisibility(View.VISIBLE);
		} else {
			handlerImgView02.setVisibility(View.INVISIBLE);
		}
		
		handlerImgView01.setLayoutParams(layoutParams);
		handlerImgView03.setLayoutParams(layoutParams);
		
		/* 핸들러 뷰2 세팅 */
		layoutParams = new FrameLayout.LayoutParams(handlerImgView02.getWidth(), 
																								handlerImgView02.getHeight());
		layoutParams.setMargins(0, (int)(getHandlerCoordY()), 0, 0);
		handlerImgView02.setLayoutParams(layoutParams);
		
		/* 연습장 뷰 세팅 */
		layoutParams = new FrameLayout.LayoutParams(memoContentW, memoContentH);
		layoutParams.setMargins((int)(sliderImgView.getX() + barW + movedX), 
															(int)(sliderImgView.getY() + movedY),	0, 0);
		noteImgView.setLayoutParams(layoutParams);
		
		/* 연습장 버튼 세팅 */
		if(sliderImgView.getX() + DataManager.slideBarW + clearButton.getWidth() 
																								< DataManager.noteViewAreaW) {
			clearButton.setVisibility(View.VISIBLE);
		} else {
			clearButton.setVisibility(View.INVISIBLE);
		}
		
		if(sliderImgView.getX() + DataManager.slideBarW + clearButton.getWidth() 
												+ undoButton.getWidth() < DataManager.noteViewAreaW) {
			undoButton.setVisibility(View.VISIBLE);
		} else {
			undoButton.setVisibility(View.INVISIBLE);
		}
		
	}


	/* 사용자와 상호작용이 가능한 상태. 이벤트 적용 가능. */
	@Override
	public void onResume() {
		super.onResume();
	}
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(MEMOFRG, "Fragment onSaveInstanceState 호출 .. ");
		/** 연습장 위치 정보를 저장해 두었다가 onResume()에서 복구시킨다. */
		super.onSaveInstanceState(outState);
	}

	private void setNoteView(int width, int height) {
		/* sendToMoveNoteViewX = width(실제 단말 해상도 X) - sliderImgView의 x 좌표
		 * sendToMoveNoteViewY = height(실제 단말 해상도 Y) - sliderImgView의 y 좌표
		 * 두 값을 moveNoteView로 전달한다. */
		int sendToMoveNoteViewX = (int)(width - sliderImgView.getX());
		int sendToMoveNoteViewY = (int)(height - sliderImgView.getY());
		moveNoteView(sendToMoveNoteViewX, sendToMoveNoteViewY, false);
		
	}
	
	/** 핸들러 가운데 고정시키기 */
	private int getHandlerCoordY() {
		int handlerCoordY = ((DataManager.stageH - DataManager.statusH) / 2) 
																		- (handlerImgView01.getHeight() / 2);
		return handlerCoordY;
	}
	
}
