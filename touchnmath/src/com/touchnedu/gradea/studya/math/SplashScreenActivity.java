package com.touchnedu.gradea.studya.math;

import com.touchnedu.gradea.studya.math.membership.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class SplashScreenActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		/** set time to splash out */
		final int welcomeScreenDisplay = 3000;
		/** create a thread to show splash up to splash time */
		Thread welcomeThread = new Thread() {
			int wait = 0;
			@Override
			public void run() {
				try {
					super.run();
					/**
					 * use while to get the splash time. Use sleep() to increase
					 * the wait variable for every 100L.
					 */
					while (wait < welcomeScreenDisplay) {
						sleep(100);
						wait += 100;
					}
				} catch (Exception e) {
					Log.i("Exception :: ", e.toString());
				} finally {
					/**
					 * Called after splash times up. Do some action after splash
					 * times up. Here we moved to another main activity class
					 */
//					startActivity(new Intent(SplashScreenActivity.this, TutorialActivity.class));
					startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
//					startActivity(new Intent(SplashScreenActivity.this, JoinActivity.class));
//					startActivity(new Intent(SplashScreenActivity.this, FindPwdActivity.class));
//					startActivity(new Intent(SplashScreenActivity.this, StartActivity.class));
										
					finish();
				}
			}
		};
		welcomeThread.start();
		
	}
}