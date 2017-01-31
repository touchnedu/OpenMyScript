package com.touchnedu.gradea.studya.math.modules;


public class ContentManager {
	private static ContentManager cm = null;
	private String rightAnswer;
	private int targetAnsBoxWidth, targetAnsBoxHeight;
	private float coordinateX, coordinateY;

	public static ContentManager getInstance() {
		if(cm == null)
			cm = new ContentManager();
		return cm;
	}
	
	public void setAnswer(String answer) {
		rightAnswer = answer;
	}
	
	public String getAnswer() {
		return rightAnswer;
	}
	
	public void setTargetAnsBoxWidth(int width) {
		targetAnsBoxWidth = width * (int)DataManager.currentDeviceDensity;
	}
	
	public int getTargetAnsBoxWidth() {
		return targetAnsBoxWidth;
	}
	
	public void setTargetAnsBoxHeight(int height) {
		targetAnsBoxHeight = height * (int)DataManager.currentDeviceDensity;
	}
	
	public int getTargetAnsBoxHeight() {
		return targetAnsBoxHeight;
	}
	
	public void setCoordinateX(float position) {
		coordinateX = position;
	}
	
	public float getCoordinateX() {
		return coordinateX;
	}
	
	public void setCoordinateY(float position) {
		coordinateY = position;
	}
	
	public float getCoordinateY() {
		return coordinateY;
	}
}
