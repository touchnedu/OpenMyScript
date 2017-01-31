package com.touchnedu.gradea.studya.math.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesManager {

	private final static SharedPreferencesManager instance = new SharedPreferencesManager();

	/* 싱글톤 객체 생성 */
	public static SharedPreferencesManager getInstance() {
		return instance;
	}

	private SharedPreferencesManager() {

	}

	/**
	 * 싱글톤 쉐어드에 저장
	 * @param context
	 */
	public void saveProperties(Context context) {
		/*
		 * 회원 관련
		 */
		SharedPreferences myPref = context.getSharedPreferences("DataManager", 
																													Context.MODE_PRIVATE);
		SharedPreferences.Editor myEditor = myPref.edit();

		myEditor.putString("registration_id", registration_id);
		myEditor.putBoolean("isLogin", isLogin);
		myEditor.putBoolean("isAutoLogin", isAutoLogin);
		myEditor.putBoolean("pushAgreement", pushAgreement);

		myEditor.putString("user_key", user_key);
		myEditor.putString("serial", serial);

		myEditor.putString("email", email);
		myEditor.putString("nickname", nickname);
		myEditor.putString("phone", phone);
		myEditor.putString("password", password);
		myEditor.putString("notCompletedQuiz", notCompletedQuiz);
		
		myEditor.putBoolean("isFirstConnect", isFirstConnect);
		myEditor.putBoolean("isNotCompletedQuiz", isNotCompletedQuiz);
		myEditor.putBoolean("bgSound", bgSound);
		myEditor.putBoolean("efxSound", efxSound);
		myEditor.putBoolean("autoNextQuiz", autoNextQuiz);

		myEditor.putString("sex", sex);
		myEditor.putString("device_type", device_type);
		myEditor.putString("married", married);
		myEditor.putString("car", car);
		myEditor.putInt("job_id", job_id);
		myEditor.putString("age", age);
		
		myEditor.putString("grade", grade);
		myEditor.putString("gradeTerm", gradeTerm); // 목차 선택
		myEditor.putString("skinMath", skinMath); // 스킨

		myEditor.putString("agreement_flag", agreement_flag);
		myEditor.putString("agreement_time", agreement_time);
		myEditor.putString("privacy_flag", privacy_flag);
		myEditor.putString("privacy_time", privacy_time);
		myEditor.putString("auth_id", auth_id);
		myEditor.putString("recommend_id", recommend_id);
		myEditor.commit();
	}

	public void loadProperties(Context context) {
		SharedPreferences myPref = context.getSharedPreferences("DataManager",
																										Context.MODE_PRIVATE);

		registration_id = myPref.getString("registration_id", "");
		isLogin = myPref.getBoolean("isLogin", false);
		isAutoLogin = myPref.getBoolean("isAutoLogin", false);
		pushAgreement = myPref.getBoolean("pushAgreement", false);

		user_key = myPref.getString("user_key", "");
		serial = myPref.getString("serial", "");

		email = myPref.getString("email", "");
		nickname = myPref.getString("nickname", "");
		phone = myPref.getString("phone", "");
		password = myPref.getString("password", "");
		notCompletedQuiz = myPref.getString("notCompletedQuiz", "");
		
		isFirstConnect = myPref.getBoolean("isFirstConnect", true);
		isNotCompletedQuiz = myPref.getBoolean("isNotCompletedQuiz", false);
		bgSound = myPref.getBoolean("bgSound", false);
		efxSound = myPref.getBoolean("efxSound", true);
		autoNextQuiz = myPref.getBoolean("autoNextQuiz", false);
		
		sex = myPref.getString("sex", "");
		device_type = myPref.getString("device_type", "");
		married = myPref.getString("married", "");
		car = myPref.getString("car", "");
		job_id = myPref.getInt("job_id", 0);
		age = myPref.getString("age", "");
		grade = myPref.getString("grade", "");
		gradeTerm = myPref.getString("gradeTerm", "");
		skinMath = myPref.getString("skinMath", "green");

		agreement_flag = myPref.getString("agreement_flag", "");
		agreement_time = myPref.getString("agreement_time", "");
		privacy_flag = myPref.getString("privacy_flag", "");
		privacy_time = myPref.getString("privacy_time", "");
		auth_id = myPref.getString("auth_id", "");
		recommend_id = myPref.getString("recommend_id", "");
	}

	public void showDataManager() {
		Log.i("test",
				"----------------------------[DataManager]---------------------------");
		Log.i("test", "registration_id = " + registration_id);
		Log.i("test", "isLogin = " + isLogin);
		Log.i("test", "isAutoLogin = " + isAutoLogin);
		Log.i("test", "pushAgreement = " + pushAgreement);
		Log.i("test", "user_key = " + user_key);
		Log.i("test", "serial = " + serial);

		Log.i("test", "email = " + email);
		Log.i("test", "nickname = " + nickname);
		Log.i("test", "phone = " + phone);
		Log.i("test", "password = " + password);

		Log.i("test", "sex = " + sex);
		Log.i("test", "device_type = " + device_type);
		Log.i("test", "married = " + married);
		Log.i("test", "car = " + car);
		Log.i("test", "job_id = " + job_id);
		Log.i("test", "age = " + age);
		
		Log.i("text", "isFirstConnect = " + isFirstConnect);
		Log.i("text", "isNotCompletedQuiz = " + isNotCompletedQuiz);
		Log.i("text", "notCompletedQuiz = " + notCompletedQuiz);
		Log.i("test", "Background Music = " + bgSound);
		Log.i("test", "Effect Sound = " + efxSound);
		Log.i("test", "Auto Next Quiz = " + autoNextQuiz);
		
		Log.i("test", "grade = " + grade);
		Log.i("test", "gradeTerm = " + gradeTerm);
		Log.i("test", "skinMath = " + skinMath);

		Log.i("test", "agreement_flag = " + agreement_flag);
		Log.i("test", "agreement_time = " + agreement_time);
		Log.i("test", "privacy_flag = " + privacy_flag);
		Log.i("test", "privacy_time = " + privacy_time);
		Log.i("test", "auth_id = " + auth_id);
		Log.i("test", "recommend_id = " + recommend_id);
		Log.i("test",
				"----------------------------[DataManager]---------------------------");
	}

	public String registration_id;
	public String push_coupon_id;
	public Boolean isLogin;
	public Boolean isAutoLogin;
	public Boolean isPush = false;
	public Boolean pushAgreement;

	public String push_regid;
	public String push_auth;

	public String email;
	public String nickname;
	public String phone;
	public String password;

	public String user_key;
	public String serial;
	/**
	 * 성별 M , F
	 */
	public String sex;
	/**
	 * 디바이스 타입 1 = 아이폰,  2 = 안드
	 */
	public String device_type="2";
	public String married;
	public String car;
	public int job_id;
	public String age;
	public String grade; // 학년
	public String gradeTerm; // 목차선택
	public String skinMath; // 스킨

	/* 
	 * 스킨
	 */
	public String getSkinMath() {
		return skinMath;
	}

	public void setSkinMath(String skinMath) {
		this.skinMath = skinMath;
	}
	
	/*
	 * 효과음 및 배경음악, 문제 자동 넘김
	 */
	public boolean bgSound;
	public boolean efxSound;
	public boolean autoNextQuiz;
	
	
	/*
	 * 단원목차
	 */
	public String getGradeTerm() {
		return gradeTerm;
	}

	public void setGradeTerm(String gradeTerm) {
		this.gradeTerm = gradeTerm;
	}

	public boolean isAutoNextQuiz() {
		return autoNextQuiz;
	}

	public void setAutoNextQuiz(boolean autoNextQuiz) {
		this.autoNextQuiz = autoNextQuiz;
	}
	
	/**
	 * 이용 약관 동의 여부 Y, N
	 */
	public String agreement_flag;
	/**
	 * 이용 약관 동의 시간 14자리(20121217215307)
	 */
	public String agreement_time;

	/**
	 * 개인정보 동의 여부 Y, N
	 */
	public String privacy_flag;
	/**
	 * 개인정보 동의 시간 14자리(20121217215307)
	 */
	public String privacy_time;
	/**
	 * SMS 인증 키값
	 */
	public String auth_id;
	/**
	 * 추천인
	 */
	public String recommend_id;
	/**
	 * 첫 접속 여부
	 */
	public boolean isFirstConnect;
	/**
	 * 풀던 문제
	 */
	public boolean isNotCompletedQuiz;
	public String notCompletedQuiz;
	
	public String getNotCompletedQuiz() {
		return notCompletedQuiz;
	}

	public void setNotCompletedQuiz(String notCompletedQuiz) {
		this.notCompletedQuiz = notCompletedQuiz;
	}

	public boolean getIsNotCompletedQuiz() {
		return isNotCompletedQuiz;
	}

	public void setIsNotCompletedQuiz(boolean isNotCompletedQuiz) {
		this.isNotCompletedQuiz = isNotCompletedQuiz;
	}

	public boolean getBgSound() {
		return bgSound;
	}

	public void setBgSound(boolean bgSound) {
		this.bgSound = bgSound;
	}

	public boolean getEfxSound() {
		return efxSound;
	}

	public void setEfxSound(boolean efxSound) {
		this.efxSound = efxSound;
	}

	public boolean getIsFirstConnect() {
		return isFirstConnect;
	}
	
	public void setIsFirstConnect(boolean isFirstConnect) {
		this.isFirstConnect = isFirstConnect;
	}
	
	public String getPush_regid() {
		return push_regid;
	}

	public void setPush_regid(String push_regid) {
		this.push_regid = push_regid;
	}

	public String getPush_auth() {
		return push_auth;
	}

	public void setPush_auth(String push_auth) {
		this.push_auth = push_auth;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getPushAgreement() {
		return pushAgreement;
	}

	public void setPushAgreement(Boolean pushAgreement) {
		this.pushAgreement = pushAgreement;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}

	public String getMarried() {
		return married;
	}

	public void setMarried(String married) {
		this.married = married;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public int getJob_id() {
		return job_id;
	}

	public void setJob_id(int job_id) {
		this.job_id = job_id;
	}

	public String getAgreement_flag() {
		return agreement_flag;
	}

	public void setAgreement_flag(String agreement_flag) {
		this.agreement_flag = agreement_flag;
	}

	public String getAgreement_time() {
		return agreement_time;
	}

	public void setAgreement_time(String agreement_time) {
		this.agreement_time = agreement_time;
	}

	public String getPrivacy_flag() {
		return privacy_flag;
	}

	public void setPrivacy_flag(String privacy_flag) {
		this.privacy_flag = privacy_flag;
	}

	public String getPrivacy_time() {
		return privacy_time;
	}

	public void setPrivacy_time(String privacy_time) {
		this.privacy_time = privacy_time;
	}

	public String getAuth_id() {
		return auth_id;
	}

	public void setAuth_id(String auth_id) {
		this.auth_id = auth_id;
	}

	public String getRecommend_id() {
		return recommend_id;
	}

	public void setRecommend_id(String recommend_id) {
		this.recommend_id = recommend_id;
	}

	public Boolean getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(Boolean isLogin) {
		this.isLogin = isLogin;
	}


	public Boolean getIsAutoLogin() {
		return isAutoLogin;
	}

	public void setIsAutoLogin(Boolean isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}

	public String getUser_key() {
		return user_key;
	}

	public void setUser_key(String user_key) {
		this.user_key = user_key;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getRegistration_id() {
		return registration_id;
	}

	public void setRegistration_id(String registration_id) {
		this.registration_id = registration_id;
	}

	public Boolean getIsPush() {
		return isPush;
	}

	public void setIsPush(Boolean isPush) {
		this.isPush = isPush;
	}

	public String getPush_coupon_id() {
		return push_coupon_id;
	}

	public void setPush_coupon_id(String push_coupon_id) {
		this.push_coupon_id = push_coupon_id;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}
	
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

}
