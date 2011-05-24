package com.vemind.accmeter;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class GForceView extends View {
	
    public GForceView(Context context) {
        super(context);
        initGForceView();
    }
    
    public GForceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public void setPadding (int left, int top, int right, int bottom) {
    	super.setPadding(left, top, right, bottom);
    }


    private void initGForceView() {
		// TODO Auto-generated method stub
		
	}
    
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
