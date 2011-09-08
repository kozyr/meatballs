package com.github.kozyr.android.meatballs;

import java.util.List;

import com.github.kozyr.android.meatballs.map.TruckLocation;
import com.github.kozyr.android.meatballs.map.TruckOverlay;
import com.github.kozyr.android.meatballs.map.directions.MapDirections;
import com.github.kozyr.android.meatballs.map.directions.MapPath;
import com.github.kozyr.android.meatballs.map.directions.PolyLine;
import com.github.kozyr.android.meatballs.service.MeatBallService;
import com.github.kozyr.android.meatballs.service.MeatBallService.LocalBinder;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MeatBallTrackerActivity extends MapActivity 
implements LocationUpdateListener{ 
	private MeatBallService meatBallService;
	private boolean isBound = false;

	private static final String TAG = "Meatyballs";

	private MapView mapView;
	private TruckOverlay truckOverlay;
	private View progressBar;

	private boolean inProgress;

	private final Handler handler = new Handler();

	private static final int DEFAULT_LAT = 41884410;
	private static final int DEFAULT_LON = -87636710;

	/** Defines callbacks for service binding, passed to bindService() */
	private final ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {

			LocalBinder binder = (LocalBinder) service;
			meatBallService = binder.getService();
			isBound = true;
			meatBallService.setLocationListener(MeatBallTrackerActivity.this);
			showLastLocation();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
	}

	private void initUI() {
		setContentView(R.layout.main);

		progressBar = findViewById(R.id.my_progress);

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mapView.setLongClickable(true);
		mapView.getController().setZoom(17);
		mapView.getController().animateTo(
				new GeoPoint(DEFAULT_LAT, DEFAULT_LON));

		truckOverlay = new TruckOverlay();
		mapView.getOverlays().add(truckOverlay);

		Button refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshLocation();	
			}
		});
	}

	private void refreshLocation() {
		if (isBound) {
			showProgressBar();
			meatBallService.refreshLocation();
		}
	}

	private void showLastLocation() {
		if (isBound) {
			TruckLocation location = meatBallService.getLastLocation();
			if (location != null) {
				showTruck(location);
			}
		}
	}

	private void showTruck(TruckLocation truck) {
		Log.i(TAG, "Truck at " + truck.getAddress());
		
		MapDirections directions = truck.getDirections();
		if (directions != null && !directions.isEmpty() && shouldAnimate(truck)) {
			Log.i(TAG, "Moving through directions...");
			animatePath(directions.getPath(), 0, truck);
			setAnimated(truck);
		} else {
			truckOverlay.setTruckLocation(truck);
			mapView.getController().animateTo(truck.getLocation());
		}
	}

	private void setAnimated(TruckLocation truck) {
		MeatBallTrackerApp app = (MeatBallTrackerApp) getApplication();
		app.setLastAnimatedTruck(truck);
	}

	private boolean shouldAnimate(TruckLocation truck) {
		MeatBallTrackerApp app = (MeatBallTrackerApp) getApplication();
		
		return !truck.equals(app.getLastAnimatedTruck());
	}

	private void animatePath(final List<MapPath> queue, 
			final int index, final TruckLocation end) {
		if (index >= queue.size()) {
			truckOverlay.setTruckLocation(end);
			// mapView.getController().animateTo(end.getLocation());
			return;
		}
		
		PolyLine single = queue.get(index).getPolyLine();
		TruckLocation temp = new TruckLocation(
				single.getEnd().getLatitudeE6() / 1000000.0,
				single.getEnd().getLongitudeE6() / 1000000.0,
				""
				);
		truckOverlay.setTruckLocation(temp);
		mapView.getController().animateTo(single.getEnd(), new Runnable() {

			@Override
			public void run() {
				animatePath(queue, index + 1, end);
			}
		});
	}

	private void showProgressBar() {
		progressBar.setVisibility(View.VISIBLE);
		
		inProgress = true;
	}

	private void hideProgressBar() {
		progressBar.setVisibility(View.GONE);
		
		inProgress = false;
	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = new Intent(this, MeatBallService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}


	@Override
	protected void onStop() {
		super.onStop();

		// Unbind from the service
		if (isBound) {
			meatBallService.removeLocationListener();
			unbindService(serviceConnection);
			isBound = false;
		}
	}

	@Override
	protected void onDestroy() {

		if (isFinishing()) {
			Intent intent = new Intent(this, MeatBallService.class);
			stopService(intent);
		}

		super.onDestroy();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void locationChanged(final TruckLocation location) {
		Log.i(TAG, "Received updated " + location);

		handler.post(new Runnable() {
			@Override
			public void run() {
				mapView.getController().stopAnimation(true);
				showTruck(location);
				hideProgressBar();
			}
		});
	}
}