package com.touchnedu.gradea.studya.math;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Toast;

public class MainLayout extends LinearLayout {
	/* 상속 계층도 : View -> ViewGroup -> LinearLayout 
	 * View를 상속받았으니 화면에 보여질 수 있겠구나~!
	 * ViewGroup을 상속받았으니 다른 View를 포함할 수 있겠구나~!
	 * */
	private static final int SLIDING_DURATION = 1500; // 슬라이딩 되는 타임 ms
	private static final int QUERY_INTERVAL = 16;
	int mainLayoutWidth;
	private View menu;
	private View content;
	private static int menuRightMargin = 15;

	private enum MenuState {
		HIDING, HIDDEN, SHOWING, SHOWN // 0, 1, 2, 3 순으로 열거형 상수가 된다.
	};

	private int contentXOffset;
	private MenuState currentMenuState = MenuState.HIDDEN;
	private Scroller menuScroller = new Scroller(this.getContext(),	
																											new EaseInInterpolator());
	private Runnable menuRunnable = new MenuRunnable();
	private Handler menuHandler = new Handler();
	int prevX = 0;
	boolean isDragging = false;
	int lastDiffX = 0;

	/**
	 * 책장 화면 
	 */
	
  /* 사용자 정의 뷰를 만들려면 View 클래스에서 새로운 클래스를 상속받고 해당 클래스에
	 * 출력 형식과 이벤트 핸들링을 구성하여 만들 수 있다. View 클래스는 세 종류의 생성자를
	 * 가지고 있으며 뷰가 어떤 방식으로 사용되는지에 따라 호출되는 생성자가 달라진다.
	 * 
	 * 1. public View(Context context);
	 * -> 자바 코드에서 직접 View 객체를 생성할 때 사용된다. 즉, XML 형식의 리소스를
	 *    활용하지 않는 경우이다.
	 *    
	 * 2. public View(Context context, AttributeSet attrs);
	 * -> 리소스에서 XML로 정의한 뷰의 속성 값들이 attrs 객체에 저장되어 전달된다. 
	 *    이 값들은 해당 뷰가 만들어질 때 사용된다.
	 *    
	 * 3. public View(Context context, AttributeSet attrs, int defStyle);
	 * -> defStyle은 뷰에 어떤 스타일 테마를 적용할 것인지를 나타낸다. 0은 아무 스타일도
	 *    적용되지 않으며 리소스의 XML에서 android:theme에 특정 값을 설정할 때 해당 값에
	 *    맞는 스타일이 적용된다.
	 *    
	 * View는 시스템 자원이기 때문에 안드로이드 시스템에 의해서 관리된다. 따라서 액티비티에서
	 * 뷰를 사용하려면 액티비티가 안드로이드 시스템에 뷰 생성을 요청해야 한다. 
	 * 안드로이드 시스템은 뷰를 생성하기 위해 해당 액티비티의 정보가 필요하기 때문에
	 * View 클래스의 생성자에 해당 액티비티의 참조값을 요구한다.(1, 2, 3)
	 * 
	 * View 클래스의 생성자는 실행의 의미를 가진 액티비티를 요구하는 것이 맞지만 
	 * Activity 클래스를 사용하면 해당 View 클래스는 Activity 클래스에 종속되기 때문에
	 * Activity의 상위 클래스인 Context 클래스를 사용하여 실행 의미를 가진 다른 클래스에도
	 * 적용 가능하도록 구현되어있다. 따라서 View 클래스의 모든 객체 생성자에는 Context
	 * 클래스 객체가 넘어간다.
	 */
	
	public MainLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MainLayout(Context context) {
		super(context);
	}

	/* widthMeasureSpec과 heightMeasureSpec은 부모 뷰로부터 결정된 치수가 넘어온다. 
	 * MeasureSpec.getSize(widthMeasureSpec)을 통해 해당 치수를 얻어올 수 있고
	 * MeasureSpec.getMode(widthMeasureSpec)을 통해 해창 치수의 mode를 얻어올 수 있다.
	 * mode는 AT_MOST, EXACTLY, UNSPECIFIED 의 세 종류가 있다.
	 * 
	 * 1. AT_MOST : wrap_content (뷰 내부의 크기에 따라 크기가 달라짐)
	 * 2. EXACTLY : fill_parent, match_parent (외부에서 이미 크기가 지정되었음)
	 * 3. UNSPECIFIED : mode가 세팅되지 않은 크기가 넘어올 때. (이 경우는 없다고 봐도 됨)
	 * 
	 *  onMeasure() 메소드는 View의 크기를 결정한다. 윈도우가 laid out 되는 처음 순간
	 *  또는 layout이 변경될때마다 호출된다. 여기서 View의 크기를 결정해야 하는 이유는
	 *  density-independent pixel units(dp), wrap_content, or fill_parent 와 같은
	 *  relative value가 사용되기 때문이다. 
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// onMeasure() 파라미터의 Default 값은 100 * 100이다.
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if(widthMode == MeasureSpec.AT_MOST) {
			Log.i("prjt", "widthMode : AT_MOST");
		} else if(widthMode == MeasureSpec.EXACTLY) {
			Log.i("prjt", "widthMode : EXACTLY");
		} else if(widthMode == MeasureSpec.UNSPECIFIED) {
			Log.i("prjt", "widthMode : UNSPECIFIED");
		}
//		Log.i("prjt", "heightMode : " + heightMode);

		mainLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
		// mainlayoutWidth = 1080
		menuRightMargin = mainLayoutWidth * 18 / 100;
		// menuRightMargin = 1080 * 18 / 100 = 194 그런데 안 쓰이므로 아이고 의미 없다.
	}

	/* 화면에 뷰가 추가될 때(포함될 때) 호출된다. */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.i("prjt", "onAttachedToWindow()");
		// 책장 화면, 책장 화면이 뜰 때 한 번만 호출됨.
		Log.i("prjt", "getChildCount()" + this.getChildCount());
		menu = this.getChildAt(0); // 현재 보이는 child 중에서 0번째 아이템의 위치를 받는다.
		content = this.getChildAt(1); // Tab 과 관련된 메소드. 검색해보라-
		/*content.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return MainLayout.this.onContentTouch(v, event);
			}
		});*/
//		menu.setVisibility(View.GONE);
	}

	/* onMeasure() 메소드에서 결정된 width와 height를 가지고 앱 전체 화면에서 현재 뷰가
	 * 그려지는 bound를 돌려준다. onLayout()에서는 일반적으로 이 뷰에 딸린 children을
	 * 위치시키고 크기를 조정하는 작업을 한다.
	 * 유의할 점은 넘어오는 파라미터가 앱 전체를 기준으로 한 위치를 돌려준다.
	 * super 메소드에서는 아무것도 하지 않기 때문에 작성하지 않는다.
	 * 쉽게 말해 뷰가 그 자식들에게 크기와 위치를 할당할 때 호출된다.
	 * */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		Log.i("prjt", "onLayout()");
		if (changed) {
			Log.i("prjt", "onLayout() changed true"); // 진입할 때 true
			LayoutParams contentLayoutParams = (LayoutParams)content.getLayoutParams();
			contentLayoutParams.width = this.getWidth();
			contentLayoutParams.height = this.getHeight();
			/* getLayoutParams() : 현재 레이아웃 요소의 속성 객체를 얻어온다.
			 * setLayoutParams() : 해당 속성 객체의 값을 변경한다.
			 * 두 메소드는 함께 사용하는 것이 바람직하다.
			 * set 메소드만 사용할 경우 해당 객체에 대한 모든 속성을 정의해주지 않으면
			 * 미지정 속성에 대해 초기화가 진행되기 때문이다. 
			 */
			
//			LayoutParams menuLayoutParams = (LayoutParams)menu.getLayoutParams();
//			menuLayoutParams.height = this.getHeight();
//			menuLayoutParams.width = this.getWidth() - menuRightMargin;
		}
//		menu.layout(left, top, right - menuRightMargin, bottom);
		// 기존에 사용하려고 했던 menu 인듯. 지금은 숨겨져서 사용안돼서 주석 처리함.
		content.layout(left + contentXOffset, top, right + contentXOffset, bottom);
		
	}

	/* 용도가? 메뉴 관련인 듯. 사용 안 되고 있으니 이것도 사용 안 되는 중일 듯. */
	public void toggleMenu() {
		Log.i("prjt", "toggleMenu()");
		if (currentMenuState == MenuState.HIDING || 
																				currentMenuState == MenuState.SHOWING) {
			return;
		}

		switch (currentMenuState) {
		case HIDDEN:
			currentMenuState = MenuState.SHOWING;
			menu.setVisibility(View.VISIBLE);
			menuScroller.startScroll(0, 0, menu.getLayoutParams().width, 0,	SLIDING_DURATION);
			Log.i("prjt", "HIDDEN");
			break;
		case SHOWN:
			currentMenuState = MenuState.HIDING;
			menuScroller.startScroll(contentXOffset, 0, -contentXOffset, 0, SLIDING_DURATION);
			Log.i("prjt", "SHOWN");
			break;
		default:
			break;
		}
		menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
		this.invalidate();
	}

	protected class MenuRunnable implements Runnable {
		@Override
		public void run() {
			Log.i("prjt", "MenuRunnable run()");
			boolean isScrolling = menuScroller.computeScrollOffset();
			adjustContentPosition(isScrolling);
		}
	}

	private void adjustContentPosition(boolean isScrolling) {
		Log.i("prjt", "adjustContentPosition()");
		int scrollerXOffset = menuScroller.getCurrX();

		content.offsetLeftAndRight(scrollerXOffset - contentXOffset);

		contentXOffset = scrollerXOffset;
		this.invalidate();
		if (isScrolling)
			menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
		else
			this.onMenuSlidingComplete();
	}

	private void onMenuSlidingComplete() {
		switch (currentMenuState) {
		case SHOWING:
			currentMenuState = MenuState.SHOWN;
			break;
		case HIDING:
			currentMenuState = MenuState.HIDDEN;
			menu.setVisibility(View.GONE);
			break;
		default:
			return;
		}
	}

	protected class EaseInInterpolator implements Interpolator {
		@Override
		public float getInterpolation(float t) {
			return (float) Math.pow(t - 1, 5) + 1;
		}

	}

	public boolean isMenuShown() {
		return currentMenuState == MenuState.SHOWN;
	}

	public boolean onContentTouch(View v, MotionEvent event) {
		if (currentMenuState == MenuState.HIDING
																		|| currentMenuState == MenuState.SHOWING) {
			return false;
		}
		int curX = (int) event.getRawX();
		int diffX = 0;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			Log.d(" MainLayout ", " MotionEvent.ACTION_DOWN");
			Log.d(" MainLayout ", " MotionEvent.ACTION_DOWN");
			Log.d(" MainLayout ", " MotionEvent.ACTION_DOWN");
			
			prevX = curX;
			return true;

		case MotionEvent.ACTION_MOVE:
			if (!isDragging) {
				isDragging = true;
				menu.setVisibility(View.VISIBLE);
			}
			diffX = curX - prevX;
			if (contentXOffset + diffX <= 0) {
				diffX = -contentXOffset;
			} else if (contentXOffset + diffX > mainLayoutWidth
					- menuRightMargin) {
				diffX = mainLayoutWidth - menuRightMargin - contentXOffset;
			}
			content.offsetLeftAndRight(diffX);
			contentXOffset += diffX;
			this.invalidate();

			prevX = curX;
			lastDiffX = diffX;
			return true;

		case MotionEvent.ACTION_UP:
			Log.d("MainLayout.java onContentTouch()", "Up lastDiffX "
					+ lastDiffX);

			if (lastDiffX > 0) {
				currentMenuState = MenuState.SHOWING;
				menuScroller.startScroll(contentXOffset, 0,
						menu.getLayoutParams().width - contentXOffset, 0,
						SLIDING_DURATION);
			} else if (lastDiffX < 0) {
				currentMenuState = MenuState.HIDING;
				menuScroller.startScroll(contentXOffset, 0, -contentXOffset, 0,
						SLIDING_DURATION);
			}
			menuHandler.postDelayed(menuRunnable, QUERY_INTERVAL);
			this.invalidate();
			isDragging = false;
			prevX = 0;
			lastDiffX = 0;
			return true;

		default:
			break;
		}

		return false;
	}
}
