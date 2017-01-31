package com.touchnedu.gradea.studya.math.modules;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.OnContentScrollListener;
import com.touchnedu.gradea.studya.math.service.OnWindowFocusListener;

public class CustomScroll extends Fragment implements OnWindowFocusListener {
	private FrameLayout frameLayout;
	private ImageView scrollHandler;
	
	private float pressedY;
	private float prevPressedY;
	private float destScrollY;
	
	private int flickCheckDistance = 10;
	private float flickPower = 50f;
	private float easingRate = 0.5f;
	private boolean isMotion = false;
	
	private Timer aniTimer = null;
	private TimerTask aniTimerTask = null;
	
//	private int scrollRngW = 1;
	private int scrollRngH = 1;
//	private int scrollDotW = 1;
	private int scrollDotH = 1;
	
	private OnContentScrollListener scrollListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			scrollListener = (OnContentScrollListener)activity;
		} catch(Exception e) {
			throw new ClassCastException(activity.toString() + " OnContentScrollListener 구현 필요");
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
													 												Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.custom_scrollbar, container, false);
		frameLayout = (FrameLayout)rootView.findViewById(R.id.custom_scrollview_layout);
		scrollHandler = (ImageView)rootView.findViewById(R.id.custom_scroll_handler);
		
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		frameLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					cancelFlickScroll();
					prevPressedY = pressedY = event.getY();
					setScrollHandlerTo(event.getY());
					break;
				
				case MotionEvent.ACTION_MOVE:
					setScrollHandlerTo(event.getY());
					prevPressedY = pressedY;
					pressedY = event.getY();
					break;

				case MotionEvent.ACTION_UP:
					float finalY = event.getY();
					if(Math.abs(event.getY() - prevPressedY) > flickCheckDistance) {
						finalY = flickPower * (event.getY() - prevPressedY);
					}
					moveScrollHandlerTo(finalY, true);
					
					v.performClick();
					break;
			
				}
				
				return true;
			}
		});
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
//		scrollRngW = frameLayout.getWidth();
		scrollRngH = frameLayout.getHeight();
//		scrollDotW = scrollHandler.getWidth();
		scrollDotH = scrollHandler.getHeight();
		
	}

	protected void setScrollHandlerTo(float getY) {
		float setY = getY - scrollHandler.getY();
		moveScrollHandlerTo(setY, false);
	}

	protected void moveScrollHandlerTo(float getDestY, boolean motion) {
		if(motion && !isMotion) {
			isMotion = true;
			
			final float tempY = getDestY;
			
			aniTimer = new Timer();
			aniTimerTask = new TimerTask() {
				@Override
				public void run() {
					destScrollY = Math.max(tempY, 0);
					destScrollY = Math.min(destScrollY, scrollRngH - scrollDotH);
					timerStart();
					
				}
			};
			aniTimer.scheduleAtFixedRate(aniTimerTask, 0, 33);
			
		} else {
			onMoveScrollHandlerTo(getDestY);
		}
	}
	
	private void onMoveScrollHandlerTo(float getDestY) {
		float onMoveY = Math.max(scrollHandler.getY() + getDestY, 0);
		onMoveY = Math.min(onMoveY, scrollRngH - scrollDotH);
		getDestY = onMoveY - scrollHandler.getY();
		
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
													scrollHandler.getWidth(), scrollHandler.getHeight());
		layoutParams.setMargins((int)scrollHandler.getX(), 
																(int)(scrollHandler.getY() + getDestY), 0, 0);
		
		scrollHandler.setLayoutParams(layoutParams);

		float rateF = scrollHandler.getY() / (scrollRngH - scrollDotH);
		scrollListener.onContentScrolled(rateF);
		
	}
	
	private void timerStart() {
		if(getActivity() != null) {
			getActivity().runOnUiThread(generate);
		} else{
  		Log.i("MemoFragment", "timerStart's getActivity() is null" );
  	}
	}
	
	private Runnable generate = new Runnable() {
		@Override
		public void run() {
			float currentY = (float)(destScrollY - (destScrollY - scrollHandler.getY()) * easingRate);
			if(Math.abs(currentY - destScrollY) < 5) {
				currentY = destScrollY;
				cancelFlickScroll();
			} else {
			}
			onMoveScrollHandlerTo((int)(currentY - scrollHandler.getY()));
		}
	}; 

	private void cancelFlickScroll() {
		isMotion = false;
		if(aniTimer != null) {
			aniTimer.cancel();
		}
		if(aniTimerTask != null) {
			aniTimerTask.cancel();
		}
		
	}
	
}
