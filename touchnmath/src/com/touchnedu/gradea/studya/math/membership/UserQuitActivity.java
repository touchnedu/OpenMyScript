package com.touchnedu.gradea.studya.math.membership;

import com.touchnedu.gradea.studya.math.EnterActivity;
import com.touchnedu.gradea.studya.math.OptionActivity;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.service.BaseActivityManager;
import com.touchnedu.gradea.studya.math.service.NaverLogin;
import com.touchnedu.gradea.studya.math.service.ParentActivity;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class UserQuitActivity extends ParentActivity implements OnClickListener {
	public Context mContext;
	private Button quit_button;
	private MathUserNetwork mathNetwork;
	private BaseActivityManager am = BaseActivityManager.getInstance();
	private NaverLogin nl;

	private int loginResult = 0;
	private static final String PRJT = "prjt";
	ProgressDialog progressDialog;
	StringBuilder resultMessage = new StringBuilder();

	Handler myHandler = new Handler(new HandlerCallBack());
	private class HandlerCallBack implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 1) {
				if (progressDialog != null)
					progressDialog.cancel();

				if (loginResult == MathUserNetwork.OKAY) {
					writeLog("loginResult OKAY");
					
					nl.forceLogout();

					SharedPreferencesManager.getInstance().setIsLogin(false);
					SharedPreferencesManager.getInstance().setIsAutoLogin(false);
					SharedPreferencesManager.getInstance().saveProperties(mContext);

					showMessage("탈퇴처리가 완료되었습니다.");
					am.finishAllActivity();
					Intent intent = new Intent(UserQuitActivity.this, EnterActivity.class);
					startActivity(intent);
					
				}	else if(loginResult == MathUserNetwork.NO_NETWORK) {
					showMessage("로그인 실패: 네트워크 상태를 확인하세요");
					writeLog("loginResult NO_NETWORK");
					
				}	else {
					showMessage(resultMessage.toString());
					writeLog("loginResult ERROR");
					
				}

				resultMessage = new StringBuilder(); // 이거 왜 있는 거지?
			}
			return true;
		}
	};

	private void showMessage(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		nl = new NaverLogin(mContext);
		setContentView(R.layout.activity_user_quit);

		mathNetwork = new MathUserNetwork(this);

		initView();
	}

	public void initView() {
		quit_button = (Button) findViewById(R.id.quit_button);
		quit_button.setOnClickListener(this);

		// 뒤로 가기 버튼
		Button registerButton = (Button)findViewById(R.id.btn_prev);
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(UserQuitActivity.this, OptionActivity.class);
				startActivity(intent);
				UserQuitActivity.this.finish();
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.quit_button:
			writeLog("탈퇴할 회원 번호 : " + DataManager.curMemNo);
			new AlertDialog.Builder(this).setTitle("회원 탈퇴")	
												.setMessage(getString(R.string.user_quit))
												.setPositiveButton("확인", 
																				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					requestLogin();
				}
			}).setNegativeButton("취소", null).show();
		break;

		}
	}

	private void requestLogin() {
		progressDialog = ProgressDialog.show(this, "", "잠시만 기다려주세요.", true);

		new Thread(new Runnable() {
			public void run() {
				// 탈퇴 메소드 수행 후 결과를 리턴받는다.
				loginResult = mathNetwork.userQuit();
				// 메세지 가져오기(메세지 객체)
				Message msg = myHandler.obtainMessage();
				// 메세지 id 설정
				msg.what = 1;
				// 메세지 큐에 넣는다.
				myHandler.sendMessage(msg);
				// 메세지 큐에 들어간 메세지는 순서대로 핸들러가 처리한다.
				// 이때, handleMessage()메소드에 정의된 기능이 수행된다.
			}
		}).start();
		/**
		 * (http://humble.tistory.com/14 참고.)
		 * 핸들러는 메세지 처리 방식과 Runnable 객체 실행 방식이 있다.
		 * 여기서는 메세지 처리 방식으로 구현되었음.
		 * 1. 핸들러 정의 
		 * 2. 스레드 생성 -> 핸들러로 메세지 보내기.
		 */

	}

	@Override
	protected void onDestroy() {
		System.gc();
		super.onDestroy();
	}

	// 뒤로 가기 버튼 앱 종료
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			return false;
		default:
			return false;
		}
	}
	
	private void writeLog(String msg) {
		Log.i(PRJT, msg);
	}

}
