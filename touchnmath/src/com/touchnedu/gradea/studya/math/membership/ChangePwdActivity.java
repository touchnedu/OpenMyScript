package com.touchnedu.gradea.studya.math.membership;

import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.BaseActivityManager;
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
import android.widget.EditText;
import android.widget.Toast;

public class ChangePwdActivity extends ParentActivity implements OnClickListener {
	public Context mContext;
	private Button pwdButton;
	private MathUserNetwork mathNetwork;
	private int result = 0;
	private BaseActivityManager am = BaseActivityManager.getInstance();

	ProgressDialog progressDialog;
	StringBuilder resultMessage = new StringBuilder();
	Handler myHandler = new Handler(new HandlerCallback()); 
	
	private class HandlerCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 1) {
				if(progressDialog != null)
					progressDialog.cancel();

				if(result == MathUserNetwork.OKAY) {
					showMessage("비밀번호가 변경되었습니다.");

					SharedPreferencesManager.getInstance().setIsLogin(false);
					SharedPreferencesManager.getInstance().setIsAutoLogin(false);
					SharedPreferencesManager.getInstance().saveProperties(mContext);

					am.finishAllActivity();	
					Intent intent = new Intent(ChangePwdActivity.this, LoginActivity.class);
					startActivity(intent);
					
				} else if(result == MathUserNetwork.NO_NETWORK) {
					showMessage("로그인 실패: 네트워크 상태를 확인하세요");
					
				}	else if(result == MathUserNetwork.NOT_MATCH) {
					showMessage("비밀번호를 확인해주세요.");
					
				}

				resultMessage = new StringBuilder();
			}
			
			return true;
		}
	};

	public void showMessage(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_change_pwd);
		am.addActivity(this);

		mathNetwork = new MathUserNetwork(this);
		
		initView();
	}

	public void initView() {
		pwdButton = (Button) findViewById(R.id.pwd_button);
		pwdButton.setOnClickListener(this);

		// 뒤로 가기 버튼
		Button registerButton = (Button) findViewById(R.id.btn_prev);
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pwd_button:
			String param = "비밀번호변경";
			requestLogin(param);
			break;
		default:
			break;

		}
	}

	private void requestLogin(String param) {
		Log.i("requestLogin :: ", "param = " + param);

		EditText curPasswordText = (EditText) findViewById(R.id.txt_cpassword);
		EditText newPasswordText = (EditText) findViewById(R.id.txt_password);
		EditText rePasswordText = (EditText) findViewById(R.id.txt_rpassword);

		final String curPwd = curPasswordText.getText().toString();
		final String newPwd = newPasswordText.getText().toString();
		final String rwPwd = rePasswordText.getText().toString();

		Log.d("pwd_button cPassword   :: ", curPwd);
		Log.d("pwd_button sPassword   :: ", newPwd);

		String warningMessage = null;
		if(curPwd.length() == 0 || newPwd.length() == 0 || rwPwd.length() == 0) {
			if(curPwd.length() == 0) {
				warningMessage = "현재 비밀번호를 입력해주세요.";
			} else if(newPwd.length() == 0) {
				warningMessage = "비밀번호를 입력해주세요.";
			} else if(rwPwd.length() == 0) {
				warningMessage = "비밀번호를 확인해주세요.";
			}
		} else {
			if(!newPwd.equals(rwPwd)) 
				warningMessage = "비밀번호를 확인해주세요.";
		}
		
		if(warningMessage != null) {
			new AlertDialog.Builder(ChangePwdActivity.this).setTitle(warningMessage)
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
			return; // 해당 메소드를 종료시켜서 return 이하 명령문은 수행되지 않는다.
		}

		progressDialog = ProgressDialog.show(this, "", "잠시만 기다려주세요.", true);

		new Thread(new Runnable() {
			public void run() {
				result = mathNetwork.userPwdChange(curPwd, newPwd, resultMessage);
				Message msg = myHandler.obtainMessage();
				msg.what = 1;
				myHandler.sendMessage(msg);
			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		am.removeActivity(this);
		System.gc();
	}

	// 뒤로 버튼 종료
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

}
