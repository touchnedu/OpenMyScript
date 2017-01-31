package com.touchnedu.gradea.studya.math.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

public class InitAnswer {
	ImageView clearImgView;
	Bitmap answerBitmap;
	Context context = null;
	
	public InitAnswer(ImageView clearImgView, Bitmap answerBitmap) {
		this.clearImgView = clearImgView;
		this.answerBitmap = answerBitmap;
		
	}
	
	public void clearAnswer() {
		Bitmap clearBitmap;
		if(clearImgView != null && clearImgView.getWidth() > 0 
														&& clearImgView.getHeight() > 0) {
			clearBitmap = Bitmap.createBitmap(clearImgView.getWidth(), 
																				clearImgView.getHeight(), 
																				Config.ARGB_8888);
			clearImgView.setImageBitmap(clearBitmap);
			
		}
		
	}
	
}
