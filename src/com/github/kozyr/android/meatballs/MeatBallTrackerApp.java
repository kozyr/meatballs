package com.github.kozyr.android.meatballs;

import com.github.kozyr.android.meatballs.map.TruckLocation;
import com.github.kozyr.android.meatballs.service.MeatBallService;

import android.app.Application;
import android.content.Intent;

public class MeatBallTrackerApp extends Application {

	private TruckLocation animated;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		AlarmSetup alarm = new AlarmSetup(getApplicationContext());
		alarm.setupDefault(true);
		
		Intent intent = new Intent(this, MeatBallService.class);
		startService(intent);
	}

	public TruckLocation getLastAnimatedTruck() {
		return animated;
	}
	
	public void setLastAnimatedTruck(TruckLocation truck) {
		animated = truck;
	}
}
