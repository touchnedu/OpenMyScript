package com.touchnedu.gradea.studya.math;

import com.touchnedu.gradea.studya.math.modules.BookCaseActivity;
import com.touchnedu.gradea.studya.math.service.MusicService;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class TutorialMenuActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private View decorView;
	private int uiOption;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("prjt", "IsFirstConnect 1 : " + SharedPreferencesManager.getInstance().isFirstConnect);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tutorial_menu);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
													getApplicationContext(), getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
//		setSoftButtonVisibility();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		Context mContext;

		public SectionsPagerAdapter(Context mContext, FragmentManager fm) {
			super(fm);
			this.mContext = mContext;
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch (position) {
			case 0:
				return new Tabs01(mContext);
			case 1:
				return new Tabs02(mContext);
			case 2:
				return new Tabs03(mContext);
			case 3:
				return new Tabs04(mContext);
			case 4:
				return new Tabs05(mContext);
			case 5:
				return new Tabs06(mContext);
//			case 6:
//				return new Tabs7(mContext);
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 6 total pages.
			return 6;
		}

		/*@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}*/
	}
	
	
	@Override
  protected void onDestroy() {
    System.gc();
    super.onDestroy();
  }
	
	@Override
	protected void onUserLeaveHint() {
		MusicService.getInstance(getApplicationContext()).pauseMusic();
		super.onUserLeaveHint();
	}
	
	@Override
	protected void onResume() {
		if(SharedPreferencesManager.getInstance().bgSound)
			MusicService.getInstance(getApplicationContext()).startMusic();
		super.onResume();
	}

	// 뒤로 버튼 종료
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			checkStartActivityNumber();
			return super.onKeyDown(keyCode, event);
				
		}
		return true;
		
	}
	
	private void checkStartActivityNumber() {
		if(SharedPreferencesManager.getInstance().isFirstConnect) {
			new AlertDialog.Builder(this).setTitle("튜토리얼 종료")	
							.setMessage("튜토리얼을 종료하시겠습니까?")
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					startActivity(new Intent(TutorialMenuActivity.this, BookCaseActivity.class));
					finish();
				}
			}).setNegativeButton("취소", null).show();
		} 
	}
	
//	public void setSoftButtonVisibility() {
//		decorView = getWindow().getDecorView();
//		uiOption = getWindow().getDecorView().getSystemUiVisibility();
//		
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//			uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//			uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//		
//		decorView.setSystemUiVisibility(uiOption);
//		
//	}
	
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//		if(hasFocus)
//			decorView.setSystemUiVisibility(uiOption);
//		
//	}

}