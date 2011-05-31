package com.vemind.accmeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class SpeedCanvas extends SurfaceView implements Callback {
    private CanvasThread canvasThread;
    private double speedVal;
    private boolean isReady;
    private final float baseX = 160;
    private final float baseY = 240;
    private final float baseLenght = 140;
    private float stopX;
    private float stopY;
 
    public SpeedCanvas(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
 
    public SpeedCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
 
        this.getHolder().addCallback(this);
        this.canvasThread = new CanvasThread(getHolder());
        this.setFocusable(true);
    }
 
    public SpeedCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
 
    }
 
    public void drawSpeed(double speed) {
    	speedVal = speed;
    	calculatePoints();
        canvasThread.setRunning(true);
        canvasThread.start();
    }
 
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
 
    }
 
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
    	isReady = true; 
    }
 
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        boolean retry = true;
        canvasThread.setRunning(false);
        while(retry) {
            try {
                canvasThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
    	if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            Paint paint = new Paint();
            paint.setARGB(100, 0, 0, 255);
            canvas.drawCircle(baseX, baseY, 150, paint);
            paint.setARGB(255, 255, 0, 0);
            canvas.drawLine(baseX, baseY, stopX, stopY, paint);
    	}
    }
    
    private void calculatePoints() {
    	stopX = (float) (baseX + baseLenght * Math.cos(Math.toRadians(180 - speedVal)));
    	stopY = (float) (baseY - baseLenght * Math.sin(Math.toRadians(180 - speedVal)));
    }
 
    private class CanvasThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean isRun = false;
 
        public CanvasThread(SurfaceHolder holder) {
            this.surfaceHolder = holder;
        }
 
        public void setRunning(boolean run) {
            this.isRun = run;
        }
 
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Canvas c;
 
            while(isRun) {
                c = null;
                if (isReady) {
	                try {
	                    c = this.surfaceHolder.lockCanvas();
	                    if (c!=null) {
		                    synchronized(this.surfaceHolder) {
		                    	SpeedCanvas.this.onDraw(c);
		                    }
	                    }
	                } finally {
	                    surfaceHolder.unlockCanvasAndPost(c);
	                }
                }
            }
        }
    }
}
