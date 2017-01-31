package com.touchnedu.gradea.studya.math.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;

public class DataManager {
	public static String appVersion = "";
	public static final String updatedDate = " (2016.08.18)";
	public static String storeVersion = "";
	
	// Note의 가로/세로 쓰기 영역
	public static int noteViewAreaW = 100; // 연습장 전체 가로 크기
	public static int noteViewAreaH = 100; // 연습장 전체 세로 크기
	public static int drawableAreaW = 10; // 연습장 그리기 가능 영역 가로 크기
	public static int drawableAreaH = 10; // 연습장 그리기 가능 영역 세로 크기
	public static int slideBarW = 10; // 슬라이더 폭
	public static int handlerW = 10; // 핸들러 폭
	public static int handlerCoordY = 0; // 핸들로 Y 좌표
	public static int interpolationWidth = 10;
	
	// Popup Window 상단 바 보정값
	public static int pwLocationY = 0;
	
	// 사운드 정보
	public static final int BACKGROUND = 0;
	public static final int WRITE_SOUND = 1;
	public static final int RIGHT_SOUND = 2;
	public static final int WRONG_SOUND = 3;
	public static final int FINAL_SOUND = 4;
	
	// 문제 정보
	public static String savedQuizNum = "";
	public static boolean showSavedQuiz = true;
	
	// 회원 정보
	public static String curMemNo = "";
	public static String curMemEmail = "";
	public static String curMemName = "";
	public static String curMemNickname = "";
	public static String curMemPhoto = "";
	public static String curMemSex = "";
	public static String curMemAge = "";
	public static String curJob = "";
	public static boolean onNaverLogin = false;

	// mdpi 세팅
	public static int stageW = 100; // 디바이스의 가로 크기
	public static int stageH = 100; // 디바이스의 세로 크기
	public static int statusH = 0;
	
	// 공지사항 띄우기
	public static boolean isFirstNotice = true;
	
	public static String qChapterNum = "";
	public static String qQuizNum = "";
	public static String qAddNum = "00";
	public static int qBoxNum = 1;
	
	public static int currentPageState = 0;
	
	// Default 펜 굵기
	public static float penStrokeWidth = 20.0f;
	
	public static float baseDensityDpi = 160.0f; // 기준 밀도
	public static float currentDeviceDensityDpi = 160.0f; // 현재 기기 밀도
	public static float currentDeviceDensity = 0;
	
	public static Bitmap noteBitmap = null;
	public static String curAndVersion = "";
	
	/** 현재 디스플레이 화면에 비례한 DP 단위를 PX 단위로 변환 */
	public static float dp2px(Context context, float dp) {
		float convertedPx = TypedValue.applyDimension(
																TypedValue.COMPLEX_UNIT_DIP, 
																dp,	
																context.getResources().getDisplayMetrics());
		return convertedPx;
	}
	
	/** 현재 디스플레이 화면에 비례한 PX 단위를 DP 단위로 변환 */
	public static float px2dp(Context context, float px) {
		float convertedDp = px / (currentDeviceDensityDpi / baseDensityDpi);
		return convertedDp;
	}
	
	/** 기본 dpi 비율을 1920으로 기준. */
	public static float densityDpiRatio() {
		return (currentDeviceDensityDpi / baseDensityDpi) / 3;
	}
	
	/** 필기 인식의 최소 높이 지정. 이 크기 이하로는 작아지지 않음. */
	public static int minHeight() {
		int returnHeight = 368;
		if(currentDeviceDensityDpi < 480f) {
			returnHeight = 280;
		}
		return returnHeight;
	}
	
	/** HD 폰일 경우 이미지 여백 자르는 픽셀 크기를 줄인다. */
	public static int interpolatedValue(int value) {
		int interpolatedValue = (int)((4500 * densityDpiRatio() 
																				* densityDpiRatio()) / value) * 10; 
		return interpolatedValue;
	}
	
}
