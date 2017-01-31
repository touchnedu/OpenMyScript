package com.touchnedu.gradea.studya.math.modules;

import com.touchnedu.gradea.studya.math.R;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class AnimationModule {
	private Animation animation, fadeInAni, fadeOutAni, blink;
	
	public void setAniOption(float startAlpha, float endAlpha, int time) {
		animation = new AlphaAnimation(startAlpha, endAlpha);
		animation.setFillAfter(true);
		animation.setDuration(time);
	}
	
	public void startViewAnimation(View view) {
		view.startAnimation(animation);
	}
	
	public void startButtonAnimation(Button button) {
		button.startAnimation(animation);
	}
	
	public void setBlinkAnimation(Context context, final View nextButton) {
		blink = AnimationUtils.loadAnimation(context, R.anim.blink);
		blink.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			@Override
			public void onAnimationStart(Animation animation) {
				nextButton.startAnimation(blink);
			}
		});
	}
	
	
}
