package com.vemind.accmeter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;

public class SpeedService extends Service implements LocationListener {
	private LocationManager locMan;
	private LocationListener locLis;
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
    	locMan = (LocationManager) getSystemService (Context.LOCATION_SERVICE); 
        locLis = new SpeedService();
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locLis);
        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent i) {
    	mySpeed = (SpeedProcessor) i.getParcelableExtra(SpeedProcessor.NAME);
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
    }
    @Override
    public void onDestroy() {
    	locMan.removeUpdates(locLis);

    }
	
	
	@Override
	public void onLocationChanged(Location location) {
    	if(location!=null) { 
            if(location.hasSpeed() && mySpeed != null){ 
            	mySpeed.addSpeedValue (location.getSpeed());
            	mySpeed.setStatus(true);
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

    	if ((status == LocationProvider.OUT_OF_SERVICE) || (status == LocationProvider.TEMPORARILY_UNAVAILABLE))
    		mySpeed.setStatus(false);
    } 
}
