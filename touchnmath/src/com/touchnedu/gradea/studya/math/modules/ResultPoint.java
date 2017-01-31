package com.touchnedu.gradea.studya.math.modules;

import java.io.Serializable;

public class ResultPoint implements Serializable {
	private static final long serialVersionUID = 1L;
	
	float x;
	float y;
	boolean Draw;
	int colorState;
	
	ResultPoint(float ax, float ay, boolean ad, int colorState)	{
		this.x = ax;
		this.y = ay;
		this.Draw = ad;
		this.colorState = colorState;
	}
	
}
