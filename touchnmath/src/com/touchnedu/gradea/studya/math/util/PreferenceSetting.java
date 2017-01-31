package com.touchnedu.gradea.studya.math.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceSetting {

	private Context context;
	
	public PreferenceSetting(Context context){
		this.context = context;
	}
	
	public void setPrefs(String prefName, String keyValue, String value) {
		final SharedPreferences config = context.getSharedPreferences(prefName, 0);

		SharedPreferences.Editor configEditor = config.edit();
		configEditor.putString(keyValue, value);

		configEditor.commit();
	}
	
	public void setPrefs(String prefName, String keyValue, boolean value) {
		final SharedPreferences config = context.getSharedPreferences(prefName, 0);

		SharedPreferences.Editor configEditor = config.edit();
		configEditor.putBoolean(keyValue, value);

		configEditor.commit();
	}
	
	
	public String getPrefsString(String prefName, String keyValue) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, 0);
        return prefs.getString(keyValue, "");
	}
	
	public boolean getPrefsBoolean(String prefName, String keyValue) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, 0);
        return prefs.getBoolean(keyValue, false);
	}
	
}
