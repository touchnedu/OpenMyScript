package com.touchnedu.gradea.studya.math.service;

import com.touchnedu.gradea.studya.math.R;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SavedQuizBox {
	private View backgroundLine;
	private LinearLayout savedQuizLayout;
	private ImageView cancelSavedQuiz;
	private Activity activity;
	
	public SavedQuizBox(Context context) {
		activity = (Activity)context;
		backgroundLine = (View)activity.findViewById(R.id.background_line);
		savedQuizLayout = (LinearLayout)activity.findViewById(R.id.save_quiz_layout);
		cancelSavedQuiz = (ImageView)activity.findViewById(R.id.cancel_saved_quiz);
	}
	
	public void ShowSavedQuizBox() {
		cancelSavedQuiz.setVisibility(View.VISIBLE);
		savedQuizLayout.setVisibility(View.VISIBLE);
		backgroundLine.setVisibility(View.VISIBLE);
	}
	
	public void closeSavedQuizBox() {
		backgroundLine.setVisibility(View.GONE);
		savedQuizLayout.setVisibility(View.GONE);
		cancelSavedQuiz.setVisibility(View.GONE);
	}
	
}
