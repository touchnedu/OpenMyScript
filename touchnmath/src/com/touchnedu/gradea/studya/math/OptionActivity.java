package com.touchnedu.gradea.studya.math;

import com.touchnedu.gradea.studya.math.membership.ChangePwdActivity;
import com.touchnedu.gradea.studya.math.membership.UserQuitActivity;
import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.modules.LayoutInflateController;
import com.touchnedu.gradea.studya.math.modules.MarketVersionChecker;
import com.touchnedu.gradea.studya.math.modules.SendMailToContact;
import com.touchnedu.gradea.studya.math.modules.SlideMenuController;
import com.touchnedu.gradea.studya.math.service.BaseActivityManager;
import com.touchnedu.gradea.studya.math.service.MusicService;
import com.touchnedu.gradea.studya.math.service.NaverJoin;
import com.touchnedu.gradea.studya.math.service.NaverLogin;
import com.touchnedu.gradea.studya.math.service.ParentActivity;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OptionActivity extends ParentActivity implements OnClickListener {
	private Context mContext;
	private ImageView naverIcon, bgmOn, bgmOff, efxOn, efxOff,
										whiteSkinOn, whiteSkinOff, greenSkinOn, greenSkinOff,
										autoNextOn, autoNextOff, updateOn, updateOff;
	private TextView myAccount, changePwd, logout, viewNotice, agreement, 
									 personalInfo, appVersion, appUpdatedDate, memberQuit, contact;
	private BaseActivityManager am = BaseActivityManager.getInstance();

	@Override
  protected void onCreate(Bundle savedInstanceState) {
		SlideMenuController.TEMP_STATE = SlideMenuController.DEFAULT_STATE; 
		SlideMenuController.DEFAULT_STATE = 5;
    super.onCreate(savedInstanceState);
    mContext = this;
    
    LayoutInflateController.inflateLayout(this, R.layout.activity_option);
    
    myAccount 			= (TextView)findViewById(R.id.myAccount);
    changePwd 			= (TextView)findViewById(R.id.change_pwd);
    logout 					= (TextView)findViewById(R.id.logout);
    viewNotice 			= (TextView)findViewById(R.id.view_notice);
    agreement 			= (TextView)findViewById(R.id.view_agreement);
    personalInfo 		= (TextView)findViewById(R.id.view_personal_info);
    appVersion 			= (TextView)findViewById(R.id.app_version);
    appUpdatedDate 	= (TextView)findViewById(R.id.app_updated_date);
    memberQuit 			= (TextView)findViewById(R.id.member_quit);
    contact 				= (TextView)findViewById(R.id.contact);
    
    naverIcon 			= (ImageView)findViewById(R.id.naver_icon);
    bgmOn 					= (ImageView)findViewById(R.id.bgm_on);
    bgmOff 					= (ImageView)findViewById(R.id.bgm_off);
    efxOn 					= (ImageView)findViewById(R.id.efx_on);
    efxOff 					= (ImageView)findViewById(R.id.efx_off);
    whiteSkinOn 		= (ImageView)findViewById(R.id.skin_white_on);
    whiteSkinOff 		= (ImageView)findViewById(R.id.skin_white_off);
    greenSkinOn 		= (ImageView)findViewById(R.id.skin_green_on);
    greenSkinOff 		= (ImageView)findViewById(R.id.skin_green_off);
    autoNextOn			= (ImageView)findViewById(R.id.auto_next_on);
    autoNextOff			= (ImageView)findViewById(R.id.auto_next_off);
    updateOn				= (ImageView)findViewById(R.id.update_on);
    updateOff				= (ImageView)findViewById(R.id.update_off);
    
    am.addActivity(this);
    
    checkVerInfo();
    initView();
  }
	
	public void initView() {
		logout.setOnClickListener(this);
		viewNotice.setOnClickListener(this);
		agreement.setOnClickListener(this);
		personalInfo.setOnClickListener(this);
		memberQuit.setOnClickListener(this);
		contact.setOnClickListener(this);
		
		appVersion.setText(DataManager.appVersion);
		
		if(DataManager.onNaverLogin) {
			changePwd.setTextColor(Color.rgb(204, 204, 204));
			naverIcon.setVisibility(View.VISIBLE);
		} else {
			changePwd.setOnClickListener(this);
			naverIcon.setVisibility(View.INVISIBLE);
		}
		
		if(SharedPreferencesManager.getInstance().getBgSound()) { 
			bgmOn.setVisibility(View.VISIBLE);
			bgmOff.setVisibility(View.GONE);
		} else {
			bgmOn.setVisibility(View.GONE);
			bgmOff.setVisibility(View.VISIBLE);
		}
		
		if(SharedPreferencesManager.getInstance().getEfxSound()) { 
			efxOn.setVisibility(View.VISIBLE);
			efxOff.setVisibility(View.GONE);
		} else {
			efxOn.setVisibility(View.GONE);
			efxOff.setVisibility(View.VISIBLE);
		}
		
		if(SharedPreferencesManager.getInstance().skinMath.equals("white")) {
			setWhiteSkinRadioBtn();
		} else {
			setGreenSkinRadioBtn();
		}
		
		if(SharedPreferencesManager.getInstance().autoNextQuiz) {
			autoNextOn.setVisibility(View.VISIBLE);
			autoNextOff.setVisibility(View.GONE);
		} else {
			autoNextOn.setVisibility(View.GONE);
			autoNextOff.setVisibility(View.VISIBLE);
		}
		
		myAccount.setText(DataManager.curMemEmail);
		
  }
	
	private void setWhiteSkinRadioBtn() {
		whiteSkinOn.setVisibility(View.VISIBLE);
		whiteSkinOff.setVisibility(View.GONE);
		greenSkinOn.setVisibility(View.GONE);
		greenSkinOff.setVisibility(View.VISIBLE);
	}
	
	private void setGreenSkinRadioBtn() {
		whiteSkinOn.setVisibility(View.GONE);
		whiteSkinOff.setVisibility(View.VISIBLE);
		greenSkinOn.setVisibility(View.VISIBLE);
		greenSkinOff.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.logout:
			if(DataManager.onNaverLogin)
				naverLogout();
			else
				Logout();
			break;
			
		case R.id.change_pwd:
			startActivity(new Intent(this, ChangePwdActivity.class));
			break;
			
		case R.id.view_notice:
			Intent notice = new Intent(this, NoticeActivity.class);
			startActivity(notice);
			break;
			
		case R.id.view_agreement:
			startActivity(new Intent(this, AgreeInfoActivity.class));
			break;
			
		case R.id.view_personal_info:
			startActivity(new Intent(this, PersonalInfoActivity.class));
			break;
			
		case R.id.member_quit:
			checkIsId();
			startActivity(new Intent(this, UserQuitActivity.class));
			break;
			
		case R.id.contact:
			new SendMailToContact(this).showBrowser();
			break;
			
		case R.id.bgm_on:
			bgmOn.setVisibility(View.GONE);
			bgmOff.setVisibility(View.VISIBLE);
			MusicService.getInstance(getApplicationContext()).stopMusic();
			setSoundPreferences(10);
			break;
			
		case R.id.bgm_off:
			bgmOn.setVisibility(View.VISIBLE);
			bgmOff.setVisibility(View.GONE);
			MusicService.getInstance(getApplicationContext()).initMusic();
			MusicService.getInstance(getApplicationContext()).startMusic();
			setSoundPreferences(11);
			break;
			
		case R.id.efx_on:
			efxOn.setVisibility(View.GONE);
			efxOff.setVisibility(View.VISIBLE);
			setSoundPreferences(20);
			break;
		
		case R.id.efx_off:
			efxOn.setVisibility(View.VISIBLE);
			efxOff.setVisibility(View.GONE);
			setSoundPreferences(21);
			break;
			
		case R.id.auto_next_on:
			autoNextOn.setVisibility(View.GONE);
			autoNextOff.setVisibility(View.VISIBLE);
			setAutoNextQuiz(false);
			break;
			
		case R.id.auto_next_off:
			autoNextOn.setVisibility(View.VISIBLE);
			autoNextOff.setVisibility(View.GONE);
			setAutoNextQuiz(true);
			break;
		
		case R.id.skin_white_off:
		case R.id.skin_white_text:
			setWhiteSkinRadioBtn();
			setSkinProperty("white");
			break;
			
		case R.id.skin_green_off:
		case R.id.skin_green_text:
			setGreenSkinRadioBtn();
			setSkinProperty("green");
			break;
			
		case R.id.update_on:
			updateApplication();
			break;
			
		default:
			break;
		}

	}

	/* 앱 업데이트 */
	private void updateApplication() {
		String addr = MarketVersionChecker.marketAddress(getPackageName());
		Intent marketLauncher = new Intent(Intent.ACTION_VIEW);
		marketLauncher.setData(Uri.parse(addr));
		startActivity(marketLauncher);
	}
	
	/* 자동 문제 넘기기 */
	private void setAutoNextQuiz(boolean b) {
		SharedPreferencesManager.getInstance().setAutoNextQuiz(b);
		SharedPreferencesManager.getInstance().saveProperties(this);
	}
	
	/* SharedPreference 스킨 세팅 */
	private void setSkinProperty(String value) {
		SharedPreferencesManager.getInstance().setSkinMath(value);
		SharedPreferencesManager.getInstance().saveProperties(this);
	}
	
	/* SharedPreference 사운드 세팅 */
	private void setSoundPreferences(int no) {
		switch(no) {
		case 10:
			SharedPreferencesManager.getInstance().setBgSound(false);
			break;
			
		case 11:
			SharedPreferencesManager.getInstance().setBgSound(true);
			break;
			
		case 20:
			SharedPreferencesManager.getInstance().setEfxSound(false);
			break;
			
		case 21:
			SharedPreferencesManager.getInstance().setEfxSound(true);
			break;
			
		}
		SharedPreferencesManager.getInstance().saveProperties(this);
	}
	
	// 뒤로 가기 버튼 종료
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && !checkDrawerState()) {
			finish();
		}
		return true;
		
	}
	
	private void naverLogout() {
		new AlertDialog.Builder(this).setTitle("로그아웃")
							.setMessage("로그아웃 하시겠습니까?")
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(new NaverLogin(OptionActivity.this).forceLogout()) {
					Toast.makeText(OptionActivity.this, 
															"로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
					finishAll();
					Intent intent = new Intent(OptionActivity.this, TouchnEduActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					startActivity(intent);
					OptionActivity.this.finish();
					DataManager.showSavedQuiz = false;
				}
			}
		}).setNegativeButton("취소", null).show();
		
	}
	
	private void Logout() {
		new AlertDialog.Builder(this).setTitle("로그아웃")
							.setMessage("로그아웃 하시겠습니까?")
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				SharedPreferencesManager.getInstance().setIsLogin(false);
				SharedPreferencesManager.getInstance().setIsAutoLogin(false);
				SharedPreferencesManager.getInstance().saveProperties(mContext);
				
				Intent intent = new Intent(OptionActivity.this, TouchnEduActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);       
				OptionActivity.this.finish();
				DataManager.showSavedQuiz = false;
			}
		}).setNegativeButton("취소", null).show();

	}
	
	private void checkVerInfo() {
		// 현재 앱 버전 정보 가져오기
		String deviceVer = null;
		try {
			deviceVer = getPackageManager()
															.getPackageInfo(getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if(!DataManager.storeVersion.equals(deviceVer)) {
			setUpdateBtn(true);
		} else {
			setUpdateBtn(false);
		}
	}
	
	/**
	 * 네트워크를 사용하므로 쓰레드를 사용해야 한다.
	 */
	private void checkIsId() {
		new Thread() {
			@Override
			public void run() {
				NaverJoin nj = new NaverJoin(mContext);
				nj.isExist(DataManager.curMemEmail);
			}
		}.start();
	}
	
	private void setUpdateBtn(boolean b) {
		if(b) {
			appUpdatedDate.setText(R.string.not_lastest_ver);
			updateOff.setVisibility(View.GONE);
			updateOn.setVisibility(View.VISIBLE);
		} else {
			appUpdatedDate.setText(R.string.lastest_ver);
			updateOff.setVisibility(View.VISIBLE);
			updateOn.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		am.removeActivity(this);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		SlideMenuController.DEFAULT_STATE = 5;
	}
	
	
	
}
