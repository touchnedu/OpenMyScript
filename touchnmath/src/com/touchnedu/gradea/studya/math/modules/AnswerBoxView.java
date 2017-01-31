package com.touchnedu.gradea.studya.math.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class AnswerBoxView {
	Context context = null;
	ImageView originAnswerBox;
	ImageView tryAnswerBox;
	
	public AnswerBoxView(Context context) {
		this.context = context;
	}

	public void initActiveView(ImageView answerImg, ImageView answerBoxImg) {
		originAnswerBox = answerImg;
		tryAnswerBox = answerBoxImg;
	}
	
	public void clearAnswerView() {
		clearAnswerBox();
		clearTryAnswerBox();
	}
	
	
	public void clearAnswerBox() {
		Bitmap clearBitmap;
		if(originAnswerBox != null && originAnswerBox.getWidth() > 0 && originAnswerBox.getHeight() > 0) {
			clearBitmap = Bitmap.createBitmap(originAnswerBox.getWidth(), originAnswerBox.getHeight(), Bitmap.Config.ARGB_8888);
			originAnswerBox.setImageBitmap(clearBitmap);
		}
	}
	
	public void clearTryAnswerBox() {
		Bitmap clearBitmap;
		if(tryAnswerBox != null && tryAnswerBox.getWidth() > 0 && tryAnswerBox.getHeight() > 0) {
			clearBitmap = Bitmap.createBitmap(tryAnswerBox.getWidth(), tryAnswerBox.getHeight(), Bitmap.Config.ARGB_8888);
			tryAnswerBox.setImageBitmap(clearBitmap);
		}
		
	}

	public void setAnswerBitmap(Bitmap bitmap, float coordinateX, float coordinateY) {
		if(bitmap != null) {
			tryAnswerBox.setImageBitmap(bitmap);
			tryAnswerBox.setX(coordinateX);
			tryAnswerBox.setY(coordinateY);
			
		} else {
			clearTryAnswerBox();
		}
		
	}
	
	
	
}
