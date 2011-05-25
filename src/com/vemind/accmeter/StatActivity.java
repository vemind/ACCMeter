package com.vemind.accmeter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class StatActivity extends Activity {
	private static final int CLEAR_ALL_ID = Menu.FIRST;
	private static final int SETTINGS_ID = CLEAR_ALL_ID + 1;

	private SpeedProcessor mySpeed;
	
	//Value TextViews
	private TextView maxSpeedTV;
	private TextView dMaxSpeedTV;
	private TextView maxAcc0TV;
	private TextView maxAcc40TV;
	private TextView systemInfoTV;
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView (R.layout.statistics);
		
		maxSpeedTV = (TextView) findViewById (R.id.value_max_speed);
		dMaxSpeedTV = (TextView) findViewById (R.id.value_dmax_speed);
		maxAcc0TV = (TextView) findViewById (R.id.value_max_acc0);
		maxAcc40TV = (TextView) findViewById (R.id.value_max_acc40);
		systemInfoTV = (TextView) findViewById (R.id.text_system_info);
		
		Intent i = getIntent();
		mySpeed = (SpeedProcessor) i.getParcelableExtra(SpeedProcessor.NAME);
		
		fillData();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, CLEAR_ALL_ID, 0, R.string.clear_log);
        menu.add(0, SETTINGS_ID, 0, R.string.settings);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case CLEAR_ALL_ID:
                clearStatistics();                
                return true;
            case SETTINGS_ID:
            	showSettings();
            	return true;
                
        }

        return super.onMenuItemSelected(featureId, item);
    }
	
	private void clearStatistics() {
		mySpeed.clearAllLogs();
		fillData();
	}

	private void showSettings() {
		// TODO Auto-generated method stub
		
	}

	private void fillData() {
		mySpeed.open(this);
		SpeedStatistic speedStat = mySpeed.getStats();
		maxSpeedTV.setText (speedStat.getMaxKmh().toString() + " km/h");
		dMaxSpeedTV.setText (speedStat.getDailyMaxKmh().toString() + " km/h");
		maxAcc0TV.setText (speedStat.getFastest0Sec().toString() + " s");
		maxAcc40TV.setText (speedStat.getFastest40Sec().toString() + " s");
		systemInfoTV.setText ("System info \nOverall entries: " + speedStat.totalCounts.toString());
	}

}
