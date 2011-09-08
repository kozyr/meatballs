package com.github.kozyr.android.meatballs;

import com.github.kozyr.android.meatballs.map.TruckLocation;

public interface LocationUpdateListener {
	public void locationChanged(TruckLocation location);
}
