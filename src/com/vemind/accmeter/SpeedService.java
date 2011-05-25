package com.vemind.accmeter;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;

public class SpeedService extends Service implements LocationListener {
	private SpeedProcessor mySpeed;
	private IBinder mBinder;
	private boolean mAllowRebind;
	private int mStartMode;

	@Override
    public void onCreate() {
        // The service is being created
    }
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
    	mySpeed = (SpeedProcessor) i.getParcelableExtra(SpeedProcessor.NAME);
        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent i) {
    	mySpeed = (SpeedProcessor) i.getParcelableExtra(SpeedProcessor.NAME);
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }
	
	
	@Override
	public void onLocationChanged(Location location) {
    	if(location!=null) { 
            if(location.hasSpeed() && mySpeed != null){ 
            	mySpeed.addSpeedValue (location.getSpeed());
            }
        }	
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
