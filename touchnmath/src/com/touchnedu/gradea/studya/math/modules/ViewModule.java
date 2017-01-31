package com.touchnedu.gradea.studya.math.modules;

import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.service.InitAnswer;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class ViewModule implements View.OnClickListener {
	private static ViewModule vm = null;
	private ContentManager cm = ContentManager.getInstance();
	private SharedPreferencesManager spManager 
																			= SharedPreferencesManager.getInstance();
	private Activity mActivity;
	
	private View prevButton, resetButton, hintButton, solButton, nextButton;
	private ImageView answerImgVw;
	private Bitmap clearBitmap;
	
	public static ViewModule getInstance() {
		if(vm == null)
			vm = new ViewModule();
		return vm;
	}
	
	public void initView(Context context) {
		mActivity = (Activity)context;
		
		answerImgVw = (ImageView)mActivity.findViewById(R.id.answer_back);
		prevButton = mActivity.findViewById(R.id.myscript_prevButton);
		resetButton = mActivity.findViewById(R.id.myscript_resetButton);
		hintButton = mActivity.findViewById(R.id.myscript_hintButton);
		solButton = mActivity.findViewById(R.id.myscript_solButton);
		nextButton = mActivity.findViewById(R.id.myscript_nextButton);
		
		/** 리스너를 구현한 객체를 참조할지 안 할지 테스트 */
		prevButton.setOnClickListener((View.OnClickListener)mActivity);
		resetButton.setOnClickListener((View.OnClickListener)mActivity);
		hintButton.setOnClickListener((View.OnClickListener)mActivity);
		solButton.setOnClickListener((View.OnClickListener)mActivity);
		nextButton.setOnClickListener((View.OnClickListener)mActivity);
		
		
		
	}
	
	public void setAnsImgVwPosition() {
		answerImgVw.setX(cm.getCoordinateX());
		answerImgVw.setY(cm.getCoordinateY());
	}
	
	public void setAnsImgVwBitmap(Bitmap bitmap) {
		answerImgVw.setImageBitmap(bitmap);
	}
	
	public void setClearBitmap(Bitmap bitmap) {
		clearBitmap = bitmap;
	}
	
	/* mWidget.getResultAsImage() 초기화 */
	public void initAnswerBox() {
		new InitAnswer(answerImgVw, clearBitmap).clearAnswer();;
	}
	
	public void setButtonEnabled(boolean b) {
		nextButton.setEnabled(b);
		prevButton.setEnabled(b); 
		resetButton.setEnabled(b); 
		hintButton.setEnabled(b); 
		solButton.setEnabled(b);
	}

	@Override
	public void onClick(View v) {
		
	}
	
	
	
}
