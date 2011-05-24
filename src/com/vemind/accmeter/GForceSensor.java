package com.vemind.accmeter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class GForceSensor implements SensorEventListener {
	private final SensorManager mSensorManager;
	private final Sensor mAccelerometer;
	private TextView bindedTV;
	float gravity[];
	float linearAcc[];

	public GForceSensor(SensorManager sManager) {
	     mSensorManager = sManager;
	     mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	     gravity = new float[3];
	     linearAcc = new float[3];
	}
	
	public boolean regListener() {
         return mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void unregListener() {
		mSensorManager.unregisterListener(this);
	}
	
	public void bindTextView (TextView tv) {
		bindedTV = tv;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	    // alpha is calculated as t / (t + dT)
	    // with t, the low-pass filter's time-constant
	    // and dT, the event delivery rate

		final float alpha = 0.9f;
    
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linearAcc[0] = event.values[0] - gravity[0];
        linearAcc[1] = event.values[1] - gravity[1];
        linearAcc[2] = event.values[2] - gravity[2];
        
        float finRes = Math.max(Math.max(linearAcc[0], linearAcc[1]), linearAcc[2]) * 100; //getting max G value
        
        finRes = ((float)Math.round(finRes))/100; // round to 2 digits
        
        if (bindedTV != null) bindedTV.setText(((Float)finRes).toString());
	}
}
