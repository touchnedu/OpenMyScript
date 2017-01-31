package com.touchnedu.gradea.studya.math.modules;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class TestSurface extends SurfaceView implements SurfaceHolder.Callback {

	ArrayList<ResultPoint> list;
	private Bitmap bitmap;
	SurfaceHolder holder;
	DrawThread drawThread;
	static final int RED_STATE = 0;
	static final int ERASER_STATE = 1;
	static final int DELAY = 50;
	Paint[] paintList = new Paint[2];
	int colorState = RED_STATE;
	
	public TestSurface(Context context) {
		super(context);
		init(context);
	}

	public TestSurface(Context context, AttributeSet attrs, int defStyle)	{
		super(context, attrs, defStyle);
		init(context);
	}
	
	public TestSurface(Context context, AttributeSet attrs)	{
		super(context, attrs);
		init(context);
	}
	
	public void init(Context context)	{
		// createBitmap에 width와 height를 구함, 
		// display.getwidth()가 deprecated 되었기에 다른 방법을 사용.
		Display display = ((WindowManager)context.getSystemService(
																	Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		bitmap = Bitmap.createBitmap(point.x, point.y, Bitmap.Config.ARGB_8888);
		list = new ArrayList<ResultPoint>();
		holder = getHolder();
		holder.addCallback(this);
		
		Paint redPaint = new Paint();
		redPaint.setColor(Color.RED);
		redPaint.setStrokeWidth(5);
		redPaint.setAntiAlias(true);
		
		Paint eraserPaint = new Paint();
		eraserPaint.setColor(Color.WHITE);
		eraserPaint.setStrokeWidth(50);
		eraserPaint.setAntiAlias(true);
		
		paintList[0] = redPaint;
		paintList[1] = eraserPaint;
	
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)	{
			synchronized (holder)	{
				list.add(new ResultPoint(event.getX(), event.getY(), false, colorState));
			}
			return true;
		}
	
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			synchronized (holder)	{
				list.add(new ResultPoint(event.getX(), event.getY(), true, colorState));
			}
			return true;
		}
		
	return false;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawThread = new DrawThread(holder);
		drawThread.start();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (drawThread != null)	{
			drawThread.Size(width, height);
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		drawThread.bExit = true;
		for (;;) {
			try	{
				drawThread.join();
				break;
			}	catch (Exception e)	{	}
		}
	}
	
	class DrawThread extends Thread	{
		SurfaceHolder holder;
		boolean bExit;
		int mWidth, mHeight;
		
		DrawThread(SurfaceHolder holder) {
			this.holder = holder;
			bExit = false;
		}
		
		public void Size(int Width, int Height)	{
			this.mWidth = Width;
			this.mHeight = Height;
		}
		
		public void run()	{
			Canvas canvas;
			while (bExit == false) {
				synchronized (holder)	{
					canvas = holder.lockCanvas();
					if (canvas == null)
						break;
						canvas.drawColor(Color.WHITE);
						canvas.drawBitmap(bitmap, 0, 0, null);
				
					for (int i = 0; i < list.size(); i++)	{
						if (list.get(i).Draw)	{
							canvas.drawLine(list.get(i - 1).x, list.get(i - 1).y, list.get(i).x, list.get(i).y, paintList[list.get(i).colorState]);
						}
					}
					
					holder.unlockCanvasAndPost(canvas);
			
				}
			
				try	{
					Thread.sleep(TestSurface.DELAY);
				}	catch (Exception e)	{ }
			
			}
		
		}
		
	}
	
}