package com.touchnedu.gradea.studya.math.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.TutorialMenuActivity;
import com.touchnedu.gradea.studya.math.modules.BookCaseActivity;
import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;
import com.touchnedu.gradea.studya.math.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class NaverLogin {
	private String OAUTH_CLIENT_ID = "DqOgZd10Rlx8YZjXIo5c";
	private String OAUTH_CLIENT_SECRET = "B8kmAHrbyJ";
	private String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";
	private OAuthLogin mOAuthLoginInstance;
	private Context mContext;
	private static final String TAG = "Naver";
	
	String nEmail = "";
	String accessToken = "";
	String tokenType;
	
	public NaverLogin(Context context) {
		mContext = context;
		mOAuthLoginInstance = OAuthLogin.getInstance();
		mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, 
																				OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
	}
	
	public void forceLogin() {
		mOAuthLoginInstance.startOauthLoginActivity((Activity)mContext, mOAuthLoginHandler);
	}
	
	public boolean forceLogout() {
		mOAuthLoginInstance.logout(mContext);
		// 로그아웃 성공 시 accessToken == "" 가 됨.
		if(mOAuthLoginInstance.getState(mContext).toString().equals("NEED_LOGIN")) {
			DataManager.onNaverLogin = false;
			SharedPreferencesManager.getInstance().setEmail("");
			SharedPreferencesManager.getInstance().setIsLogin(false);
			SharedPreferencesManager.getInstance().setIsAutoLogin(false);
			SharedPreferencesManager.getInstance().saveProperties(mContext);
			return true;
		} else {
			return false;
		}
//		return mOAuthLoginInstance.logoutAndDeleteToken(mContext);
		
	}
	
	private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
		@Override
		public void run(boolean success) {
			if(success) {
				accessToken = mOAuthLoginInstance.getAccessToken(mContext);
				String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
				long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
				tokenType = mOAuthLoginInstance.getTokenType(mContext);
				
				writeLog("accessToken " + accessToken);
				writeLog("refreshToken " + refreshToken);
				writeLog("String.valueOf(expiresAt) " + String.valueOf(expiresAt));
				writeLog("tokenType " + tokenType);
				writeLog("mOAuthLoginInstance.getState(mContext).toString() " 
													+ mOAuthLoginInstance.getState(mContext).toString());
				
				new RequestApiTask().execute(); // 로그인이 성공하면 네이버 계정값들을 가져온다.
				
			} else {
//				Toast.makeText(mContext, "로그인이 취소/실패", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private class RequestApiTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			final String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
			String at = mOAuthLoginInstance.getAccessToken(mContext);
			parsingVersionData(mOAuthLoginInstance.requestApi(mContext, at, url));
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void content) {
			if(nEmail == null) {
				Toast.makeText(mContext, 
											 "로그인이 실패하였습니다. 잠시 후 다시 시도해주세요!", 
											 Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	private void parsingVersionData(String data) { // xml 파싱
		String[] fArray = new String[9];
		try {
			/* XML 파싱을 위한 XML파서인 XmlPullParserFactory 객체를 생성한다. 
			 * XmlPullParserFactory의 인스턴스를 얻고 같은 인스턴스의 newPullParser()를
			 * 호출하여 인스턴스를 얻는다.
			 * */
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			
			// Byte 배열을 차례대로 읽어들이기 위한 클래스 ByteArrayInputStream
			InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
			parser.setInput(input, "UTF-8");
			
			int parserEvent = parser.getEventType();
			
			String tag;
			boolean inText = false;
			int colIdx = 0;
			
			/**
			 * XmlPullParser는 문서를 순차적으로 읽으며 이벤트를 발생시킨다.
			 * START_DOCUMENT : 문서의 시작
			 * END_DOCUMENT : 문서의 끝
			 * START_TAG : 태그의 시작(예 : <data>)
			 * END_TAG : 태그의 끝(예: </data>)
			 * TEXT : START_TAG와 END_TAG에서 발생하는 이벤트
			 * 
			 * 순차적으로 읽어가며 이벤트를 발생시키기 때문에 뒤로 돌아갈 수 없다.
			 * 따라서 START_TAG 이벤트가 발생할 때 임시 변수(tag)에 Tag값을 저장하고 
			 * TEXT 이벤트에서 저장된 Tag 값을 확인하여 적절한 변수에 값을 넣는다.
			 * 
			 * ※ START_TAG와 END_TAG에서는 getName()을, TEXT에서는 getText()를 사용한다.
			 * 그렇지 않으면 null 값 반환
			 */
			while(parserEvent != XmlPullParser.END_DOCUMENT) {
				switch(parserEvent) {
				case XmlPullParser.START_TAG: // <tag>
					// getName() 메소드는 태그를 가져온다.
					tag = parser.getName();
					writeLog("<" + tag + ">");
					if(tag.compareTo("xml") == 0) {
						inText = false;
					} else if(tag.compareTo("data") == 0) {
						inText = false;
					} else if(tag.compareTo("result") == 0) {
						inText = false;
					} else if(tag.compareTo("resultcode") == 0) {
						inText = false;
					} else if(tag.compareTo("message") == 0) {
						inText = false;
					} else if(tag.compareTo("response") == 0) {
						inText = false;
					} else {
						inText = true;
					}
					break;
					
				case XmlPullParser.TEXT: // 데이터
					tag = parser.getName();
					writeLog("" + parser.getText());
					if(inText) {
						if(parser.getText() == null) { // getText() 메소드는 데이터를 가져온다.
							fArray[colIdx] = "";
						} else {
							fArray[colIdx] = parser.getText().trim();
						}
						colIdx++;
					}
					inText = false;
					break;
					
				case XmlPullParser.END_TAG: // </tag>
					tag = parser.getName();
					writeLog("</" + tag + ">");
					inText = false;
					break;
				}
				// 다음 사건을 조사하면서 문서를 처음부터 순회한다.
				parserEvent = parser.next();
			}
			
		} catch(Exception e) {
			Log.e("dd", "Error in network call", e);
		}
		
		/**
		 * 네이버 응답으로 받아오는 값(fArray)
		 * 0 : 이메일
		 * 1 : 닉네임(별명)
		 * 2 : 사용자 확인 값(네이버 설명)
		 * 3 : 프로필 사진 URL
		 * 4 : 연령대
		 * 5 : 성별
		 * 6 : 네이버 아이디 정보(54110324)
		 * 7 : 이름
		 * 8 : 생일
		 */
		
		DataManager.curMemEmail = fArray[0];
		DataManager.curMemNickname = fArray[1];
		DataManager.curMemPhoto = fArray[3];
		DataManager.curMemAge = fArray[4]; // 연령대 ex) 30-39
		DataManager.curMemSex = fArray[5];
		DataManager.curMemName = fArray[7];
		
		checkIdOnDB(DataManager.curMemEmail);
		
	}
	
	/**
	 * 네이버 아이디로 로그인 시 해당 아이디가 DB 에 있는지 확인 후
	 * 존재하면 책장 화면으로, 존재하지 않으면 DB에 입력하고 책장 화면으로 이동.
	 */
	private void checkIdOnDB(String id) {
		NaverJoin nj = new NaverJoin(mContext);
		
		/* 네이버 가입 여부 확인 */
		if(nj.isExist(id)) {
			goToActivity();
		} else {
			if(nj.joinNaverId().equals("success")) {
				toast(mContext.getString(R.string.naver_start_hello));
				goToActivity();
			} else {
				failDialog();
			}
		}
		
	}
	
	/* Toast는 UI 쓰레드를 사용하는데 쓰레드 안에서 쓰레드를 사용하게 되면 에러가 발생
	 * 따라서 핸들러를 별도로 생성해서 돌려야 한다. */
	private void toast(final String msg) {
		Handler tempHandler = new Handler(Looper.getMainLooper());
		tempHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			}
		}, 0);
	}
	
	/* Toast와 마찬가지로 처리 */
	private void failDialog() {
		Handler tempHandler = new Handler(Looper.getMainLooper());
		tempHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Util.customOneDialog(mContext, 
												mContext.getString(R.string.naver_start_error_title), 
												mContext.getString(R.string.naver_start_error_content), 
												R.layout.custom_dialog_one, 
												false);
			}
		}, 0);
	}
	
	
	/**
	 * 프리퍼런스에 네이버 아이디 로그인/자동로그인 등록 후 
	 * 책장 화면으로 넘어간다.
	 */
	private void goToActivity() {
		SharedPreferencesManager.getInstance().setEmail("nAutoLogin");
		SharedPreferencesManager.getInstance().setIsLogin(true);
		SharedPreferencesManager.getInstance().setIsAutoLogin(true);
		SharedPreferencesManager.getInstance().saveProperties(mContext);
		
		// 첫 번째 로그인일 경우 튜토리얼 페이지로 넘어간다.
		if(SharedPreferencesManager.getInstance().isFirstConnect) {
			Intent intent = new Intent(mContext, TutorialMenuActivity.class);
			mContext.startActivity(intent);
		} else {
			Intent intent = new Intent(mContext, BookCaseActivity.class);
			mContext.startActivity(intent);
		}
		((Activity)mContext).finish();
		
	}
	
	private void writeLog(String msg) {
		Log.i(TAG, msg);
	}
	
}
