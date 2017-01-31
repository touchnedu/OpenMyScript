package com.touchnedu.gradea.studya.math.membership;

import java.util.regex.Pattern;

import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.ParentActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPwdActivity extends ParentActivity implements OnClickListener {

	public Context mContext;

	private MathUserNetwork mathNetwork;
	ProgressDialog progressDialog;
	StringBuilder resultMessage = new StringBuilder();

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			String message = null;
			if (progressDialog != null)
				progressDialog.cancel();

			if (MathUserNetwork.OKAY == msg.what) {
				toast("비밀번호 멜로 발송 했습니다.");
				Intent intent = new Intent(FindPwdActivity.this, LoginActivity.class);
				startActivity(intent);
				resultMessage = new StringBuilder();
				return;
			} else if (MathUserNetwork.NO_NETWORK == msg.what) {
				message = "네트워크에 연결할 수 없습니다";
			} else {
				message = resultMessage.toString();
			}
			resultMessage = new StringBuilder();

			new AlertDialog.Builder(FindPwdActivity.this).setTitle(message)
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		}
	};

	private void toast(String msg) {
		Toast.makeText(this, msg, 2).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_find_pwd);

		mathNetwork = new MathUserNetwork(this);

		initView();
	}

	public void initView() {
		Button buttonRegister = (Button) findViewById(R.id.find_button);
		buttonRegister.setOnClickListener(this);

		// 뒤로 가기 버튼
		Button registerButton = (Button) findViewById(R.id.btn_prev);
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intentLogin = new Intent(mContext, LoginActivity.class);
				startActivity(intentLogin);
				FindPwdActivity.this.finish();
			}
		});

	}

	private boolean validEmail(String email) {
		Pattern pattern = Patterns.EMAIL_ADDRESS;
		return pattern.matcher(email).matches();
	}

	@Override
	public void onClick(View v) {

		EditText emailEditText = (EditText) findViewById(R.id.txt_email);

		final String sEmail = emailEditText.getText().toString();
		String warningMessage = null;

		if (sEmail.length() == 0) {
			if (sEmail.length() == 0) {
				warningMessage = "이메일 주소를 입력해주세요";
			}
		}

		if (warningMessage == null) {
			if (!validEmail(sEmail))
				warningMessage = "이메일 주소가 올바르지 않습니다";
		}

		if (warningMessage != null) {
			new AlertDialog.Builder(FindPwdActivity.this).setTitle(warningMessage)
					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			return;
		}

		progressDialog = ProgressDialog.show(this, "", "잠시만 기다려주세요..", true);

		new Thread(new Runnable() {
			public void run() {
				try {
					int result = mathNetwork.findPassword(sEmail, resultMessage);
					Log.d("register result", "" + result);
					Message msg = myHandler.obtainMessage();
					msg.what = result;
					myHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		System.gc();
		super.onDestroy();
	}

}
