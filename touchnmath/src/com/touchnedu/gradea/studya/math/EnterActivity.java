package com.touchnedu.gradea.studya.math;

import com.touchnedu.gradea.studya.math.membership.JoinActivity;
import com.touchnedu.gradea.studya.math.membership.LoginActivity;
import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.service.BaseActivityManager;
import com.touchnedu.gradea.studya.math.service.NaverLogin;
import com.touchnedu.gradea.studya.math.service.ParentActivity;
import com.touchnedu.gradea.studya.math.util.Util;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class EnterActivity extends ParentActivity implements OnClickListener {
	private BaseActivityManager am = BaseActivityManager.getInstance();
	private Button mLoginBtn, mJoinBtn, mNaver, njOk, njCancel;
	private ImageView njStudentOn, njStudentOff, njTeacherOn, njTeacherOff,
										njParentOn, njParentOff, njEtcOn, njEtcOff;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enterpage);
		
		am.addActivity(this);
		
		mJoinBtn  = (Button)findViewById(R.id.enterpage_join);
		mLoginBtn = (Button)findViewById(R.id.enterpage_login);
		mNaver	  = (Button)findViewById(R.id.enterpage_naver);
		mJoinBtn.setOnClickListener(this);
		mLoginBtn.setOnClickListener(this);
		mNaver.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.enterpage_login:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.enterpage_join:
			startActivity(new Intent(this, JoinActivity.class));
			break;
		case R.id.enterpage_naver:
			customDialogForNaverJoin();
			break;
		case R.id.naverjoin_student_off:
			studentOn();
			break;
		case R.id.naverjoin_teacher_off:
			teacherOn();
			break;
		case R.id.naverjoin_parent_off:
			parentOn();
			break;
		case R.id.naverjoin_etc_off:
			etcOn();
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Util.finishApp(this);
		}
				
		return super.onKeyDown(keyCode, event);
		
	}
	
	private void customDialogForNaverJoin() {
		final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
		dialog.setContentView(R.layout.custom_dialog_job);
		dialog.show();
		
		njStudentOn 	= (ImageView)dialog.findViewById(R.id.naverjoin_student_on);
		njStudentOff 	= (ImageView)dialog.findViewById(R.id.naverjoin_student_off);
		njTeacherOn 	= (ImageView)dialog.findViewById(R.id.naverjoin_teacher_on);
		njTeacherOff	= (ImageView)dialog.findViewById(R.id.naverjoin_teacher_off);
		njParentOn 		= (ImageView)dialog.findViewById(R.id.naverjoin_parent_on);
		njParentOff 	= (ImageView)dialog.findViewById(R.id.naverjoin_parent_off);
		njEtcOn 			= (ImageView)dialog.findViewById(R.id.naverjoin_etc_on);
		njEtcOff 			= (ImageView)dialog.findViewById(R.id.naverjoin_etc_off);
		
		njOk					= (Button)dialog.findViewById(R.id.naver_join_ok);
		njCancel			= (Button)dialog.findViewById(R.id.naver_join_cancel);
		
		njStudentOff.setOnClickListener(this);
		njTeacherOff.setOnClickListener(this);
		njParentOff.setOnClickListener(this);
		njEtcOff.setOnClickListener(this);
		
		njOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startOnNaver();
				dialog.dismiss();
			}
		});
		njCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
	}
	
	private void studentOn() {
		njStudentOn.setVisibility(View.VISIBLE);
		njStudentOff.setVisibility(View.GONE);
		njTeacherOn.setVisibility(View.GONE);
		njTeacherOff.setVisibility(View.VISIBLE);
		njParentOn.setVisibility(View.GONE);
		njParentOff.setVisibility(View.VISIBLE);
		njEtcOn.setVisibility(View.GONE);
		njEtcOff.setVisibility(View.VISIBLE);
		DataManager.curJob = "학생";
	}
	
	private void teacherOn() {
		njStudentOn.setVisibility(View.GONE);
		njStudentOff.setVisibility(View.VISIBLE);
		njTeacherOn.setVisibility(View.VISIBLE);
		njTeacherOff.setVisibility(View.GONE);
		njParentOn.setVisibility(View.GONE);
		njParentOff.setVisibility(View.VISIBLE);
		njEtcOn.setVisibility(View.GONE);
		njEtcOff.setVisibility(View.VISIBLE);
		DataManager.curJob = "선생님";
	}

	private void parentOn() {
		njStudentOn.setVisibility(View.GONE);
		njStudentOff.setVisibility(View.VISIBLE);
		njTeacherOn.setVisibility(View.GONE);
		njTeacherOff.setVisibility(View.VISIBLE);
		njParentOn.setVisibility(View.VISIBLE);
		njParentOff.setVisibility(View.GONE);
		njEtcOn.setVisibility(View.GONE);
		njEtcOff.setVisibility(View.VISIBLE);
		DataManager.curJob = "학부모";
	}
	
	private void etcOn() {
		njStudentOn.setVisibility(View.GONE);
		njStudentOff.setVisibility(View.VISIBLE);
		njTeacherOn.setVisibility(View.GONE);
		njTeacherOff.setVisibility(View.VISIBLE);
		njParentOn.setVisibility(View.GONE);
		njParentOff.setVisibility(View.VISIBLE);
		njEtcOn.setVisibility(View.VISIBLE);
		njEtcOff.setVisibility(View.GONE);
		DataManager.curJob = "대학생 및 일반인";
	}
	
	private void startOnNaver() {
		if(njStudentOn.getVisibility() == View.GONE &&
																		njTeacherOn.getVisibility() == View.GONE &&
																		njEtcOn.getVisibility() == View.GONE &&
																		njParentOn.getVisibility() == View.GONE) {
			Util.customOneDialog(this, "", "직업을 선택하세요.", 
																						R.layout.custom_dialog_one, false);
			return;
		} 
		
		NaverLogin naverLogin = new NaverLogin(EnterActivity.this);
		naverLogin.forceLogin();
		DataManager.onNaverLogin = true;
			
	}
	
}
