package com.touchnedu.gradea.studya.math.modules;

import com.touchnedu.gradea.studya.math.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class CustomNavigationDrawer extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_drawer);
		
		SlideMenuController smc = new SlideMenuController(this);
		smc.initMenu();
		
	}
	
	
}
