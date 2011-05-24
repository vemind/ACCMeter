package com.vemind.accmeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Acc extends Activity {
    private static final int STATISTIC_ID = Menu.FIRST;
	private static final int SETTINGS_ID = STATISTIC_ID + 1;
	private static final int LOGGING_ID = SETTINGS_ID + 1;
	private static final int EXIT_ID = LOGGING_ID + 1;
	private LocationManager locMan;
	private LocationListener locLis;
	private SpeedProcessor mySpeed;
	private TextView speedText;
	private TextView speedUnits;
	private TextView gForceText;
	private PowerManager.WakeLock wLock;
	private GForceSensor gSensor;
	


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        speedText = (TextView) findViewById (R.id.text_speed);
        speedUnits = (TextView) findViewById (R.id.speed_units);
        gForceText = (TextView) findViewById (R.id.gforce_text);
        
        locMan = (LocationManager) getSystemService (Context.LOCATION_SERVICE); 
        locLis = new SpeedoActionListener();
        mySpeed = SpeedProcessor.getInstance();
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Acc");
        gSensor = new GForceSensor((SensorManager)getSystemService(SENSOR_SERVICE));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	gSensor.unregListener();
    	locMan.removeUpdates(locLis);
    	mySpeed.close();
    	wLock.release();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	gSensor.regListener();
    	gSensor.bindTextView(gForceText);
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locLis);
        mySpeed.open(this);
        wLock.acquire();
        readPreferences();
    }
    
    private void readPreferences() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    	mySpeed.saveLogs(prefs.getBoolean("logspeed", true));
		
	}

	@Override
    public void onStop(){
    	super.onStop();
    }
    
	private class SpeedoActionListener implements LocationListener { 
        @Override 
        public void onLocationChanged(Location location) { 
        	if(location!=null) { 
	            if(location.hasSpeed()){ 
	            	mySpeed.addSpeedValue (location.getSpeed());
	            	displaySpeed (true);
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
	    		displaySpeed (false);
        } 
    }
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
    	menu.clear();
        menu.add(0, STATISTIC_ID, 0, R.string.statistics);
        menu.add(0, SETTINGS_ID, 0, R.string.settings);
        menu.add(0, LOGGING_ID, 0, (mySpeed.isLogging() ? R.string.dont_save_logs : R.string.save_logs));
        menu.add(0, EXIT_ID, 0, R.string.exit);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case STATISTIC_ID:
                showStatistics();                
                return true;
            case SETTINGS_ID:
            	showSettings();
            	return true;
            case LOGGING_ID:
            	changeLogging();
            	return true;
            case EXIT_ID:
            	this.finish();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

	private void changeLogging() {
//		mySpeed.saveLogs(!mySpeed.isLogging());
	}

	private void showSettings() {
		Intent i = new Intent(this, AccPreference.class);
        startActivity(i);		
	}

	private void showStatistics() {
		Intent i = new Intent(this, StatActivity.class);
		i.putExtra(SpeedProcessor.NAME, mySpeed);
        startActivity(i);
	}

	public void displaySpeed(boolean display) {
		if (display) {
			speedText.setText(mySpeed.getSpeed().toString());
			speedUnits.setVisibility(TextView.VISIBLE);
		} else {
			speedText.setText(R.string.no_speed);
			speedUnits.setVisibility(TextView.GONE);
		}
	}
}