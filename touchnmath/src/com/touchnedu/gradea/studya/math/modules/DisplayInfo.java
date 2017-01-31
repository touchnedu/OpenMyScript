package com.touchnedu.gradea.studya.math.modules;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class DisplayInfo {
	private static final String INFO = "INFO";
	private Context context;
	
	public DisplayInfo(Context context) {
		this.context = context;
	}
	
	public void getDisplayDimension() {
		/**
		 * Activity를 상속받지 않은 클래스에서는 DisplayMetrics를 다음과 같이 정의한다.
		 * context로부터 getResource()를 얻고 getDisplayMetrics()로 정보를 가져온다.
		 */
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		DataManager.currentDeviceDensityDpi = dm.densityDpi;
    DataManager.currentDeviceDensity = dm.density;
    Display display = ((WindowManager)context
			    														.getSystemService(Context.WINDOW_SERVICE))
			    														.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    
    DataManager.stageW = size.x;
    DataManager.stageH = size.y;
    
    if(DataManager.currentDeviceDensityDpi < 400) {
    	DataManager.penStrokeWidth = 10f;
    }
    
	}
	
	public void setStatusBarHeight() {
 	  /* 태블릿이 아닐 경우 Status Bar 높이를 구한다. */
    int screenType = (context.getApplicationContext()
			    								 .getResources().getConfiguration()
			    								 .screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
    if(screenType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
    	int resId = context.getResources().getIdentifier("status_bar_height", 
   																												"dimen", "android");
    	if(resId > 0) {
    		DataManager.statusH = context.getResources().getDimensionPixelSize(resId);
    	}
    }
  }
	
	public void writeLog() {
		Log.i(INFO, "현재 기기 densityDpi : " + DataManager.currentDeviceDensityDpi);
    Log.i(INFO, "현재 기기 density : " + DataManager.currentDeviceDensity);
    Log.i(INFO, "스테이터스바 높이 : " + DataManager.statusH);
    Log.i(INFO, "화면 가로 크기 : " + DataManager.stageW);
    Log.i(INFO, "화면 세로 크기 : " + DataManager.stageH);
	}
	
}
