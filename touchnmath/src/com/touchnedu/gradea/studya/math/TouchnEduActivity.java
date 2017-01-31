package com.touchnedu.gradea.studya.math;

import com.touchnedu.gradea.studya.math.modules.BookCaseActivity;
import com.touchnedu.gradea.studya.math.modules.DataManager;
import com.touchnedu.gradea.studya.math.modules.MarketVersionChecker;
import com.touchnedu.gradea.studya.math.service.NaverLogin;
import com.touchnedu.gradea.studya.math.service.ParentActivity;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;
import com.touchnedu.gradea.studya.math.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

public class TouchnEduActivity extends ParentActivity implements IOnHandlerMessage {
	WeakRefHandler wrHandler = new WeakRefHandler(this);
	public Context mContext;
	
	@Override
	public void dispatchMessage(Message msg) {
		switch (msg.what) {
		// 네트워크 체크
		case 0:
			boolean isConnect = Util.networkCheck(mContext);
			if(isConnect) {
				wrHandler.sendEmptyMessageDelayed(2, 1500);
			} else {
				wrHandler.sendEmptyMessage(1);
			}
			break;
		// 네트워크 에러 창 띄우고 종료
		case 1:
			Util.customOneDialog(mContext, "", getString(R.string.end_msg), 
																							R.layout.custom_dialog_one, true);
			break;
		// 서비스 시작
		case 2:
			goMainActivity();
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
												 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 상단 타이틀 밑 안테나 창 제거 -> Splash 페이지이므로 없애야 한다.
		
		mContext = this;

		SharedPreferencesManager.getInstance().loadProperties(mContext);
		/* 인스턴스를 생성하고 this를 인자로 Preferenece를 얻는다. */
		
		View v = new View(this);
		setContentView(v);
		v.setBackgroundResource(R.drawable.splash);
		
		wrHandler.sendEmptyMessage(0);
		/* Handler.sendEmptyMessage(int what) 
		 * -> Message what(ID)을 사용할 경우 (아무 메세지도 보내지 않는다.)
		 *    여기서 숫자는 핸들의 구분자. 0이면 0번 쓰레드, 1이면 1번 쓰레드 이런 식의 구분 용도.
		 * Handler.sendMessage(Message msg)
		 * -> Message what, arg1, obj 등 ID와 정보 등을 같이 사용할 경우.
		 *    what 값 외에 arg1, arg2, obj 등을 보낼 수 있다.
		 *   멍
		 *    
		 * 이것은 Handler에서 msg.what으로 참조가 가능.
		 */

		SharedPreferencesManager.getInstance().showDataManager();
		/* 로그 출력 하는 기능 외에 별 거 없음. */
	
		// GooglePlay 버전 정보 가져오기
		MarketVersionChecker.getMarketVersion(getPackageName());
		
	}
	
	private void goMainActivity(){
		if (SharedPreferencesManager.getInstance().isLogin) {
			if(SharedPreferencesManager.getInstance().getEmail().equals("nAutoLogin")) {
				
				NaverLogin naverLogin = new NaverLogin(TouchnEduActivity.this);
				naverLogin.forceLogin();
				DataManager.onNaverLogin = true;
				
			} else {
				checkIsFirstLogin();
				
			}
		} else {
			Intent intent = new Intent(getApplicationContext(), EnterActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			TouchnEduActivity.this.finish();
		}
	}
	
	private void checkIsFirstLogin() {
		DataManager.curMemEmail = SharedPreferencesManager.getInstance().getEmail();
		DataManager.onNaverLogin = false;
		if(SharedPreferencesManager.getInstance().isFirstConnect) {
			startActivity(new Intent(getApplicationContext(), TutorialMenuActivity.class));
			
		} else {
			Intent intent = new Intent(getApplicationContext(), BookCaseActivity.class);
			/* 로그인 처리가 되면 로그인 화면으로 돌아갈 필요가 없으므로 스택을 전부 제거한다. */
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			/* 스플래시 액티비티는 더이상 불필요하므로 종료 finish()는 해당 액티비티를 
			 * 종료하는 것이다. 종료 버튼 하나로 어플을 종료할 때는 
			 * moveTaskToBack(true)를 위에 작성한다.
			 */
		}
		TouchnEduActivity.this.finish();
	}
	

}