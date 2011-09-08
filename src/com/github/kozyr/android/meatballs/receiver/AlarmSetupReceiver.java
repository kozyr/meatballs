package com.github.kozyr.android.meatballs.receiver;

import com.github.kozyr.android.meatballs.AlarmSetup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmSetupReceiver extends BroadcastReceiver {

	private static final String TAG = "Meatyballs";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		Log.i(TAG, "Received boot broadcast event.");
		AlarmSetup as = new AlarmSetup(ctx.getApplicationContext());
		as.setupDefault(false);
	}

}
