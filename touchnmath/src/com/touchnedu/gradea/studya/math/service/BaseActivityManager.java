package com.touchnedu.gradea.studya.math.service;

import java.util.ArrayList;

import android.app.Activity;

public class BaseActivityManager {
	private static BaseActivityManager activityManager;
	private ArrayList<Activity> activityList;
	
	private BaseActivityManager() {
		activityList = new ArrayList<Activity>();
	}
	
	public static BaseActivityManager getInstance() {
		if(activityManager == null) {
			activityManager = new BaseActivityManager();
		}
		return activityManager;
	}
	
	/**
	 * 액티비티 리스트 getter
	 * @return activityList
	 */
	public ArrayList<Activity> getActivityList() {
		return activityList;
	}
	
	/**
	 * 액티비티 리스트에 추가
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	/**
	 * 액티비티 리스트에서 삭제
	 * @param activity
	 * @return boolean
	 */
	public boolean removeActivity(Activity activity) {
		return activityList.remove(activity);
	}
	
	/**
	 * 모든 액티비티 종료
	 */
	public void finishAllActivity() {
		for(Activity activity : activityList) {
			activity.finish();
		}
	}
	
}
