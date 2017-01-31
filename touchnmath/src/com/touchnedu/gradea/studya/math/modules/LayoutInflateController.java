package com.touchnedu.gradea.studya.math.modules;

import com.touchnedu.gradea.studya.math.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class LayoutInflateController {

	public static void inflateLayout(Context context, int layoutParam) {
		Activity activity = (Activity)context;
		
		ImageView drawerOpenBtn = 
												(ImageView)activity.findViewById(R.id.drawer_open_btn);
		drawerOpenBtn.setVisibility(View.VISIBLE);
		
		FrameLayout inflateLayout = 
												(FrameLayout)activity.findViewById(R.id.inflate_layout);
		LayoutInflater inflater = 
										(LayoutInflater)activity.getSystemService(
																							Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(layoutParam, inflateLayout);
		
	}
	
	public static void inflateLayoutWithoutBtn(Context context, int layoutParam) {
		Activity activity = (Activity)context;
		FrameLayout inflateLayout = 
												(FrameLayout)activity.findViewById(R.id.inflate_layout);
		LayoutInflater inflater = 
										(LayoutInflater)activity.getSystemService(
																							Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(layoutParam, inflateLayout);
	}
	
}
