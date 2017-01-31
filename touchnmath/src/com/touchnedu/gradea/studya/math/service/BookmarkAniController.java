package com.touchnedu.gradea.studya.math.service;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public abstract class BookmarkAniController extends AnimationDrawable {
	Handler animationHandler;
	public BookmarkAniController(AnimationDrawable animationDrawable) {
		for(int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
			this.addFrame(animationDrawable.getFrame(i), animationDrawable.getDuration(i));
		}
	}
	
	@Override
	public void start() {
		super.start();
		
		animationHandler = new Handler();
		animationHandler.postDelayed(new Runnable() {
			public void run() {
				onAnimationFinish();
			}
		}, getTotalDuration());
	}

	/** 모든 프레임 재생 시간 리턴 */
	private long getTotalDuration() {
		int duration = 0;
		for(int i = 0; i < this.getNumberOfFrames(); i++) {
			duration += this.getDuration(i);
		}
		return duration;
	}
	
	/** 애니메이션 종료 시 호출 */
	abstract void onAnimationFinish(); 
	
}
