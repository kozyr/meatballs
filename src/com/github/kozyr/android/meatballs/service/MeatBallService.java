package com.github.kozyr.android.meatballs.service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import com.github.kozyr.android.meatballs.LocationUpdateListener;
import com.github.kozyr.android.meatballs.MeatBallTrackerActivity;
import com.github.kozyr.android.meatballs.R;
import com.github.kozyr.android.meatballs.map.TruckLocation;
import com.github.kozyr.android.meatballs.map.directions.DirectionFetchException;
import com.github.kozyr.android.meatballs.map.directions.DirectionsFetcher;
import com.github.kozyr.android.meatballs.map.directions.MapDirections;
import com.github.kozyr.android.meatballs.map.directions.MapPath;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class MeatBallService extends Service {

	private static final int NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;

	private static final int SHOW_NOTIFICATION = 1;

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	private LocationUpdateListener locationListener;

	private static final String TAG = "Meatyballs";

	private final Random random = new Random();

	private final AtomicReference<TruckLocation> locationHolder = 
			new AtomicReference<TruckLocation>();

	private final List<TruckLocation> tracker = Arrays.asList(
			new TruckLocation(41.88074, -87.62939, "Dearborn and Monroe"),
			new TruckLocation(41.88065, -87.63691, "Wacker and Monroe"),
			new TruckLocation(41.87336, -87.66908, "Rush Hospital"),
			new TruckLocation(41.8947433, -87.6370975, "Huron and Orleans"),
			new TruckLocation(41.8891915, -87.6326085, "Kinzie and LaSalle"));
	
	private TruckLocation prev;

	// Binder given to clients
	private final IBinder binder = new LocalBinder();

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public MeatBallService getService() {
			// Return this instance of MeatBallService so clients can call public methods
			return MeatBallService.this;
		}
	}

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "Received location update request...");
				synchronized (this) {
					try {
						updateLocation();
						TruckLocation last = getLastLocation();
						if (msg.arg1 == SHOW_NOTIFICATION && locationListener == null) {
							showNotification(last);
						} 
						if (locationListener != null) {
							locationListener.locationChanged(last);
						}
					} catch (Exception e) {
						Log.e(TAG, "Failed...", e);
					}
				}
		}
	}

	class LocationUpdater extends TimerTask {

		@Override
		public void run() {
			Message msg = mServiceHandler.obtainMessage();
			msg.arg1 = SHOW_NOTIFICATION;
			mServiceHandler.sendMessage(msg);
		}
	}

	private Timer updateTimer;
	private TimerTask updateTask;


	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Start up the thread running the service.  Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.  We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("MeatballThread", 
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler 
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		notificationManager = (NotificationManager) 
				getSystemService(NOTIFICATION_SERVICE);
		updateTimer = new Timer();
		updateTask = new LocationUpdater();

		// update location every 10 minutes when running
		updateTimer.schedule(updateTask, 1000, 10 * 60 * 1000);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "Service destroyed.");

		updateTask.cancel();
		super.onDestroy();
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification(TruckLocation truck) {

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.icon, 
				getText(R.string.notification_text),
				System.currentTimeMillis());

		Intent start = new Intent(this, MeatBallTrackerActivity.class);
		start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, start,
				PendingIntent.FLAG_UPDATE_CURRENT);

		String locText = getString(R.string.location_text) + truck.getAddress();
		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, 
				getText(R.string.notification_title),
				locText, contentIntent);

		// Send the notification.
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	public TruckLocation getLastLocation() {
		TruckLocation last = locationHolder.get();
		if (last != null) {
			last = new TruckLocation(last, true);
		}
		return last;
	}

	public void refreshLocation() {
		Message msg = mServiceHandler.obtainMessage();
		mServiceHandler.sendMessage(msg);
	}

	private void updateLocation() throws DirectionFetchException {
		TruckLocation next = tracker.get(random.nextInt(tracker.size()));
		if (!next.equals(prev)) {
			Log.i(TAG, "Service updated location...");
			TruckLocation location = new TruckLocation(next);
			location.timestamp();
			if (prev != null) {
				MapDirections directions = DirectionsFetcher.
						getDirections(prev, next);
				logDirections(directions);
				location.setDirections(directions);
			}
			prev = next;
			locationHolder.set(location);
		}
	}
	
	private void logDirections(MapDirections directions) {
		if (directions != null) {
			Log.i(TAG, "MAP DIRECTIONS:");
			Log.i(TAG, "Overview: " + directions.getOverview());
			Log.i(TAG, "Number of paths:" + directions.getPath().size());
			for (MapPath path : directions.getPath()) {
				Log.i(TAG, path.toString());
			}
		} else {
			Log.w(TAG, "MapDirections are null!");
		}
	}

	public void setLocationListener(LocationUpdateListener listener) {
		this.locationListener = listener;
	}

	public void removeLocationListener() {
		this.locationListener = null;
	}
}
