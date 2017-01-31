package com.touchnedu.gradea.studya.math.service;

import com.igaworks.IgawCommon;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.modules.SlideMenuController;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ParentActivity extends Activity {
	private BaseActivityManager am = BaseActivityManager.getInstance();
//	private View decorView;
//	private int uiOption;
	protected SlideMenuController smc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_drawer);
		smc = new SlideMenuController(this);
		smc.initMenu();
		am.addActivity(this);
//		setSoftButtonVisibility();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		am.removeActivity(this);
	}
	
	public void finishAll() {
		am.finishAllActivity();
	}
	
	public boolean checkDrawerState() {
		boolean state = SlideMenuController.DRAWER_STATE; 
		smc.controlDrawer();
		return state;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IgawCommon.startSession(ParentActivity.this);
		if(SharedPreferencesManager.getInstance().bgSound) {
			MusicService.getInstance(getApplicationContext()).resumeMusic();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		IgawCommon.endSession();
	}

	@Override
	protected void onUserLeaveHint() {
		if(SharedPreferencesManager.getInstance().bgSound)
			MusicService.getInstance(getApplicationContext()).pauseMusic();
		super.onUserLeaveHint();
	}
	
	/* 소프트 버튼 감추기 */
//	protected void setSoftButtonVisibility() {
//		decorView = getWindow().getDecorView();
//		uiOption = getWindow().getDecorView().getSystemUiVisibility();
//		
//		/* A |= B -> A = A | B
//		 * 좌변과 우변의 값을 비트단위 OR 연산 후에 좌변에 대입
//		 * 각 비트를 비교하여 어느 한쪽이 1이면 1, 그렇지 않으면 0을 반환
//		 * a = 10101010
//		 * b = 11110101
//		 * a|b = 11111111
//		 * 10진수로 바꾸면 255
//		 */
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//			uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		
//		decorView.setSystemUiVisibility(uiOption);
//	}
	
//	protected void setSwipeOption(boolean b) {
//		smc.setGestureOpen(b);
//	}

//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//		if(hasFocus)
//			decorView.setSystemUiVisibility(uiOption);
//	}
	
}
