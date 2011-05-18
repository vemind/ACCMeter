package com.vemind.accmeter;

import android.database.Cursor;

public class SpeedStatistic {
	public Integer maxSpeed; //maximum speed all the time in meters per second * 10
	public Integer dMaxSpeed; //this day max
	public Long fastestAcc0; //all the time fastest 0-100kmh in milisecs
	public Long dFastestAcc0; //this day
	public Long fastestAcc40; // 40-100kmh
	public Long dFastestAcc40; // daily 40-100kmh
	public Integer totalCounts; // Overall counts in DB
	
	public SpeedStatistic () {
		maxSpeed = 0;
		dMaxSpeed = 0;
		fastestAcc0 = 0l;
		dFastestAcc0 = 0l;
		fastestAcc40 = 0l;
		dFastestAcc40 = 0l;
	}
	
	public void recalculate (Cursor data){
		
		totalCounts = data.getCount();
		if (totalCounts > 0) {
			maxSpeed = getMaxSpeed (data);
			fastestAcc0 = getFastestAcc0 (data);
			fastestAcc40 = getFastestAcc40 (data);
		}
	}
	
	private Long getFastestAcc40(Cursor data) { // TODO implement generic method
		int valueColumn = data.getColumnIndex(SpeedProcessor.KEY_VALUE);
		int dateColumn = data.getColumnIndex(SpeedProcessor.KEY_DATE);
		
		int speedVal;
		long startTime = 0;
		long endTime = 0;
		Long fastestTime = 0l;
		
		for (data.moveToFirst(); !data.isLast(); data.moveToNext()) {
			speedVal = data.getInt(valueColumn);
			if (speedVal < 112) { // less than 11 m/s 11.2*3.6 ~ 40
				startTime = data.getLong(dateColumn);
			}
			if ((speedVal * 36 / 100) > 100 && startTime != 0) {
				endTime = data.getLong(dateColumn);
				if (fastestTime > (endTime - startTime) || fastestTime == 0) fastestTime = endTime - startTime;
			}
		}
		return fastestTime;
	}

	private Long getFastestAcc0(Cursor data) {
		int valueColumn = data.getColumnIndex(SpeedProcessor.KEY_VALUE);
		int dateColumn = data.getColumnIndex(SpeedProcessor.KEY_DATE);
		
		int speedVal;
		long startTime = 0;
		long endTime = 0;
		Long fastestTime = 0l;
		
		for (data.moveToFirst(); !data.isLast(); data.moveToNext()) {
			speedVal = data.getInt(valueColumn);
			if (speedVal < 10) { // less than 1 m/s
				startTime = data.getLong(dateColumn);
			}
			if ((speedVal * 36 / 100) > 100 && startTime != 0) {
				endTime = data.getLong(dateColumn);
				if (fastestTime > (endTime - startTime) || fastestTime == 0) fastestTime = endTime - startTime;
			}
		}
		return fastestTime;
	}

	private Integer getMaxSpeed(Cursor data) {
		int valueColumn = data.getColumnIndex(SpeedProcessor.KEY_VALUE);
		int tempMax = 0;
		int currSpeed = 0;

		for (data.moveToFirst(); !data.isLast(); data.moveToNext()) { //max speed
			currSpeed = data.getInt(valueColumn);
			tempMax = (tempMax > currSpeed) ? tempMax : currSpeed;
		}
		return tempMax;
	}

	public Float getMaxKmh() {
		float retSpeed = (float) (maxSpeed * 36); // one digit precision TODO remove all hardcode
		return retSpeed/100;
	}
	
	public Float getFastest0Sec() {
		return ((float)(fastestAcc0/10))/100; //two digits precision
	}
	
	public Float getFastest40Sec() {
		return ((float)(fastestAcc40/10))/100; //two digits again
	}
}
