package com.github.kozyr.android.meatballs.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.format.DateFormat;

import com.github.kozyr.android.meatballs.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TruckOverlay extends Overlay {

	private TruckLocation truckLocation;
	private Bitmap truckBitmap;
	
	private Paint textPaint;
	
	private static final String TAG = "Meatyballs";
	
	public TruckOverlay() {
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setARGB(255, 0, 0, 0);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(16);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		if (truckLocation != null) {
			Projection projection = mapView.getProjection();
			Point screen = new Point();
			GeoPoint geo = truckLocation.getLocation();
			projection.toPixels(geo, screen);

			if (truckBitmap == null) {
				truckBitmap = BitmapFactory.decodeResource(
						mapView.getResources(), R.drawable.icon); 
			}
			
			float hw = (truckBitmap.getWidth()  / 2.0f);
			float hh = (truckBitmap.getHeight() / 2.0f);
			
			canvas.drawBitmap(truckBitmap, 
					screen.x - hw, 
					screen.y - hh, 
					null); 
			
			
			if (truckLocation.getTime() > 0) {
				StringBuilder text = new StringBuilder(truckLocation.getAddress());
				text.append(" @");
				text.append(DateFormat.format("h:mmaa", truckLocation.getTime()));
				// Log.i(TAG, "Drawing text " + text.toString());
				canvas.drawText(text.toString(), 
						screen.x,
						screen.y - hh,
						textPaint);
			}
		}

		super.draw(canvas, mapView, shadow);
	}
	
	public void setTruckLocation(TruckLocation loc) {
		truckLocation = loc;
	}
}
