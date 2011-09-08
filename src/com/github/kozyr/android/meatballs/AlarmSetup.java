package com.github.kozyr.android.meatballs;

import java.util.Calendar;

import com.github.kozyr.android.meatballs.service.MeatBallService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmSetup {
	
	private Context ctx;
	private AlarmManager manager;
	private Calendar lunch;
	
	private static final String TAG = "Meatyballs"; 
	
	private static final String ALARM_SET = "alarmSet";
	
	public AlarmSetup(Context ctx) {
		this.ctx = ctx;
		this.manager = (AlarmManager)  
				ctx.getSystemService(Context.ALARM_SERVICE);
		lunch = Calendar.getInstance();
        lunch.set(Calendar.HOUR_OF_DAY,11); 
        lunch.set(Calendar.MINUTE,30); 
        lunch.set(Calendar.SECOND,0); 
        lunch.set(Calendar.MILLISECOND,0);
	}
	
	public void setupDefault(boolean check) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx); 
		if ( (check && !settings.getBoolean(ALARM_SET, false)) || !check) {
	        doSetup(settings);
		}
	}

	private void doSetup(SharedPreferences settings) {
		Intent intent = new Intent(ctx, MeatBallService.class);
		PendingIntent pending = PendingIntent.getService(ctx, 0, intent, 0); 
		manager.setRepeating(AlarmManager.RTC_WAKEUP, 
				lunch.getTimeInMillis(), 
				AlarmManager.INTERVAL_DAY, 
				pending);
				
		Log.i(TAG, "Alarm set.");
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(ALARM_SET, true);
		editor.commit();
	}
}
