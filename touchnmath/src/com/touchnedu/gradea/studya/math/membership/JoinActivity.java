package com.touchnedu.gradea.studya.math.membership;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.touchnedu.gradea.studya.math.AgreeForJoinActivity;
import com.touchnedu.gradea.studya.math.PersonalInfoForJoinActivity;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.modules.AccountInfo;
import com.touchnedu.gradea.studya.math.modules.BookCaseActivity;
import com.touchnedu.gradea.studya.math.modules.CustomDatePicker;
import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.service.BaseActivityManager;
import com.touchnedu.gradea.studya.math.service.ParentActivity;
import com.touchnedu.gradea.studya.math.util.ErrorEventSender;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;
import com.touchnedu.gradea.studya.math.util.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class JoinActivity extends ParentActivity 
														implements OnClickListener, OnItemSelectedListener {
	public Context mContext;
	private String completedId = "", selectedItem, selectedSex = "";
	private Spinner spItems;
	private ArrayAdapter<String> arrayAdapter;
	private List<String> listData = new ArrayList<String>();
	
	private Button buttonRegister, selectBirth, googleAccount;
	private ImageView selectManOn, selectManOff, selectWomanOn, selectWomanOff;
	private TextView selectMan, selectWoman;
	private EditText emailEditText, passwordEditText, confirmPassword;

	private BaseActivityManager am = BaseActivityManager.getInstance();
	private MathUserNetwork mathNetwork;
	ProgressDialog progressDialog;
	StringBuilder resultMessage = new StringBuilder();

	Handler myHandler = new Handler(new HandlerCallback()); 
	private class HandlerCallback implements Handler.Callback {
		public boolean handleMessage(Message msg) {
			String message = null;
			if(progressDialog != null)
				progressDialog.cancel();

			if(MathUserNetwork.OKAY == msg.what) {
				message = "회원 가입에 성공하였습니다.";
				
				DataManager.curMemEmail = completedId;
				SharedPreferencesManager.getInstance().setEmail(completedId);
				SharedPreferencesManager.getInstance().setIsLogin(true);
				SharedPreferencesManager.getInstance().setIsAutoLogin(true);
				SharedPreferencesManager.getInstance().saveProperties(mContext);

				Log.d("JoinActivity login result 자동 로그인 설정 SharedPreferencesManager.getInstance().isLogin :: ",
						SharedPreferencesManager.getInstance().isLogin.toString());
				Log.d("JoinActivity login result 자동 로그인 설정 SharedPreferencesManager.getInstance().isAutoLogin :: ",
						SharedPreferencesManager.getInstance().isAutoLogin.toString());

				am.finishAllActivity();
				Intent intent = new Intent(JoinActivity.this, BookCaseActivity.class);
				startActivity(intent);
				
				resultMessage = new StringBuilder();
			} else if(MathUserNetwork.NO_NETWORK == msg.what) {
				message = "네트워크에 연결할 수 없습니다";
			} else if(MathUserNetwork.ALREADY == msg.what) {
				message = "이미 존재하는 아이디입니다.";
			}
			toast(message);
			resultMessage = new StringBuilder();
			
			return true;
		}
	};

	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		mathNetwork = new MathUserNetwork(this);
		
		initSpinnerItems();
		initView();
		
	}

	private void initSpinnerItems() {
		listData.add("학생(초, 중, 고)");
		listData.add("선생님");
		listData.add("학부모");
		listData.add("대학생 및 일반인");
		
		spItems = (Spinner)findViewById(R.id.join_job);
		arrayAdapter = new ArrayAdapter<String>(JoinActivity.this, 
																							R.layout.spinner_item, listData);
		
		arrayAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
		spItems.setAdapter(arrayAdapter);
		spItems.setOnItemSelectedListener(this);
	}
	
	public void initView() {
		buttonRegister 	= (Button)findViewById(R.id.register_button);
		googleAccount		= (Button)findViewById(R.id.google_account);
		selectBirth 		= (Button)findViewById(R.id.join_birth);
		selectManOn 		= (ImageView)findViewById(R.id.sex_man_on);
		selectManOff 		= (ImageView)findViewById(R.id.sex_man_off);
		selectWomanOn 	= (ImageView)findViewById(R.id.sex_woman_on);
		selectWomanOff 	= (ImageView)findViewById(R.id.sex_woman_off);
		selectMan				= (TextView)findViewById(R.id.sex_man);
		selectWoman			= (TextView)findViewById(R.id.sex_woman);
		
		emailEditText 		= (EditText)findViewById(R.id.txt_email);
		passwordEditText 	= (EditText)findViewById(R.id.txt_password);
		confirmPassword 	= (EditText)findViewById(R.id.confirm_password);
		
		CheckBox isAgree = (CheckBox) findViewById(R.id.checkAgreement);
		isAgree.setChecked(true);

		buttonRegister.setOnClickListener(this);
		googleAccount.setOnClickListener(this);
		selectBirth.setOnClickListener(this);
		selectMan.setOnClickListener(this);
		selectWoman.setOnClickListener(this);
		selectManOn.setOnClickListener(this);
		selectManOff.setOnClickListener(this);
		selectWomanOn.setOnClickListener(this);
		selectWomanOff.setOnClickListener(this);

		// 뒤로 가기 버튼
		Button registerButton = (Button) findViewById(R.id.btn_prev);
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

	}

	private boolean validEmail(String email) {
		Pattern pattern = Patterns.EMAIL_ADDRESS;
		return pattern.matcher(email).matches();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Spinner spinner = (Spinner)parent;
		if(spinner.getId() == R.id.join_job) {
			selectedItem = parent.getItemAtPosition(position).toString();
		}
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.call_private1:
			startActivity(new Intent(mContext, AgreeForJoinActivity.class));
			break;

		case R.id.call_private2:
			startActivity(new Intent(mContext, PersonalInfoForJoinActivity.class));
			break;

		case R.id.join_birth:
			CustomDatePicker.showDatePicker(this);
			break;
			
		case R.id.sex_man:
		case R.id.sex_man_off:
			selectManOff.setVisibility(View.GONE);
			selectManOn.setVisibility(View.VISIBLE);
			selectWomanOff.setVisibility(View.VISIBLE);
			selectWomanOn.setVisibility(View.GONE);
			selectedSex = "M";
			break;
			
		case R.id.sex_woman:
		case R.id.sex_woman_off:
			selectManOff.setVisibility(View.VISIBLE);
			selectManOn.setVisibility(View.GONE);
			selectWomanOff.setVisibility(View.GONE);
			selectWomanOn.setVisibility(View.VISIBLE);
			selectedSex = "F";
			break;
			
		case R.id.register_button:
			validJoinForm();
			break;
			
		case R.id.google_account:
			emailEditText.setText(AccountInfo.getAccountInfo(this));
			break;

		default:
			break;
		}
	}

	private void validJoinForm() {
		CheckBox isAgree = (CheckBox)findViewById(R.id.checkAgreement);

		final String sEmail = emailEditText.getText().toString();
		final String sPassword = passwordEditText.getText().toString();
		final String confirmPwd = confirmPassword.getText().toString();
		final String birth = selectBirth.getText().toString();

		String warningMessage = null;
		if(sEmail.length() == 0 || sPassword.length() == 0) {
			if(sEmail.length() == 0) {
				warningMessage = "이메일 주소를 입력해주세요";
				
			} else if(sPassword.length() == 0) {
				warningMessage = "비밀번호를 입력해주세요";
				
			} else if(sPassword.length() > 15) {
				warningMessage = "비밀번호는 15자 이내로 입력해주세요";
				
			} else if(sPassword.length() < 3) {
				warningMessage = "비밀번호는 4자 이상 입력해주세요";
				
			} 
			
		} else {
			if(!sPassword.equals(confirmPwd)) {
				warningMessage = "비밀번호가 일치하지 않습니다";
			} else if(birth.equals("생년")) {
				warningMessage = "생년을 선택해주세요.";
			} else if(selectedSex.equals("")) {
				warningMessage = "성별을 선택해주세요.";
			}
		}

		if(warningMessage == null) {
			if(!validEmail(sEmail))
				warningMessage = "이메일 주소가 올바르지 않습니다";
			else if(!isAgree.isChecked())
				warningMessage = "서비스약관 및 개인정보취급방침에 동의해주세요";
		}

		if(warningMessage != null) {
			Util.customOneDialog(mContext, "", warningMessage, 
																						R.layout.custom_dialog_one, false);
			return;
		}

		progressDialog = ProgressDialog.show(this, "", "잠시만 기다려주세요.", true);

		new Thread(new Runnable() {
			public void run() {
				try {
					String[] nameArr;
					nameArr = sEmail.split("@");
					int result = mathNetwork.register(nameArr[0], sPassword, sEmail, 
																							birth, selectedItem, selectedSex);
					
					if(result == 0) 
						completedId = sEmail;
						
					Message msg = myHandler.obtainMessage();
					msg.what = result;
					myHandler.sendMessage(msg);
					
				} catch (Exception e) {
					ErrorEventSender.sendErrorMsg("- joinActivity 293\n" + e.getMessage());
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

	@Override
	public void onNothingSelected(AdapterView<?> parent) { }

}
