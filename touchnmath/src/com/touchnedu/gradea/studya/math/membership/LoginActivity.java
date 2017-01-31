package com.touchnedu.gradea.studya.math.membership;

import java.util.regex.Pattern;

import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.TutorialMenuActivity;
import com.touchnedu.gradea.studya.math.modules.BookCaseActivity;
import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.service.ParentActivity;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ParentActivity implements OnClickListener {
	private Button login;
	private MathUserNetwork mathNetwork;
	private int loginResult = 0;
	private static Context mContext;
	private static final String PRJT = "prjt";

	ProgressDialog progressDialog;
	StringBuilder resultMessage = new StringBuilder();
	
	Handler mHandler = new Handler(new HandlerCallBack());
	private class HandlerCallBack implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 1) {
				if (progressDialog != null)
					progressDialog.cancel();

				if (loginResult == MathUserNetwork.OKAY) {
					writeLog("Login Result : OKAY");
					// 자동 로그인 설정
					SharedPreferencesManager.getInstance().setIsLogin(true);
					SharedPreferencesManager.getInstance().setIsAutoLogin(true);
					SharedPreferencesManager.getInstance().saveProperties(mContext);

					// 메인 화면으로 넘어가는 부분 -> 책장 화면으로 변경
					DataManager.onNaverLogin = false;
					// 첫 번째 로그인일 경우 튜토리얼 액티비티로 넘어간다.
					if(SharedPreferencesManager.getInstance().getIsFirstConnect()) {
						startActivity(new Intent(LoginActivity.this, 
																									TutorialMenuActivity.class));
						SharedPreferencesManager.getInstance().setIsFirstConnect(false);
						SharedPreferencesManager.getInstance().saveProperties(mContext);
						
					} else {
						startActivity(new Intent(LoginActivity.this, 
																											BookCaseActivity.class));
						
					}
					LoginActivity.this.finish();

				} else if(loginResult == MathUserNetwork.NO_NETWORK) {
					showMessage("로그인 실패: 네트워크 상태를 확인하세요");

				} else if(loginResult == MathUserNetwork.ERROR) {
					showMessage("아이디 또는 비밀번호를 확인하세요.");

				} else {
					showMessage("#_#"); // 어떤 경우에 뜰까?
				}

				SharedPreferences userDetails = 
											getSharedPreferences("userdetails", Context.MODE_PRIVATE);
				String username = userDetails.getString("username", "");
				String password = userDetails.getString("password", "");

				if (username.equals("") || password.equals("")) {
					EditText passwordEditText = (EditText) findViewById(R.id.txt_password);
					passwordEditText.setText("");

				}

				resultMessage = new StringBuilder();
			}
			
			return true;
		}
	}

	public void showMessage(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this; // 오류나면 getApplicationContext()로 해본다.
		setContentView(R.layout.activity_login);
		
		mathNetwork = new MathUserNetwork(this);

		initSetting(); // 네이버 로그인 세팅 초기화하기
		initView();
	}

	private void initSetting() {
		// 뒤로 가기 버튼
		Button backBtn = (Button) findViewById(R.id.login_btn_prev);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
	
	public void initView() {
		login = (Button)findViewById(R.id.login_button);
		login.setOnClickListener(this);

	}

	private boolean validEmail(String email) {
		Pattern pattern = Patterns.EMAIL_ADDRESS;
		return pattern.matcher(email).matches();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_button:
			String param = "로그인 성공";
			requestLogin(param);
			break;

		default:
			break;
		}

	}

	private void requestLogin(String param) {
		Log.i("requestLogin :: ", "param = " + param);

		EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
		EditText passwordEditText = (EditText) findViewById(R.id.txt_password);

		final String sUserName = usernameEditText.getText().toString();
		final String sPassword = passwordEditText.getText().toString();

		String warningMessage = null;
		if (sUserName.length() == 0 || sPassword.length() == 0) {
			if (sUserName.length() == 0) {
				warningMessage = "이메일 주소를 입력해주세요";
			} else if (sPassword.length() == 0) {
				warningMessage = "비밀번호를 입력해주세요";
			}
		}

		if (warningMessage == null) {
			if (!validEmail(sUserName))
				warningMessage = "이메일 주소가 올바르지 않습니다";
		}

		if (warningMessage != null) {
			new AlertDialog.Builder(LoginActivity.this).setTitle(warningMessage)
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
			return;
		}

		progressDialog = ProgressDialog.show(this, "", "로그인중입니다..", true);

		new Thread(new Runnable() {
			public void run() {
				loginResult = mathNetwork.login(sUserName, sPassword, resultMessage);
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				mHandler.sendMessage(msg);

				if (MathUserNetwork.OKAY == loginResult) {// when login success.
					// 자동 로그인 설정
					SharedPreferencesManager.getInstance().setEmail(sUserName);
					DataManager.curMemEmail = sUserName;

					SharedPreferencesManager.getInstance().setIsLogin(true);
					SharedPreferencesManager.getInstance().setIsAutoLogin(true);
					SharedPreferencesManager.getInstance().saveProperties(mContext);

				}
			}
		}).start();

	}
	
	private void writeLog(String msg) {
		Log.i(PRJT, msg);
	}

}