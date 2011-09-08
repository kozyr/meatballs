package com.github.kozyr.android.meatballs.map;

import java.util.Date;

import com.github.kozyr.android.meatballs.map.directions.MapDirections;
import com.google.android.maps.GeoPoint;

public class TruckLocation {
	private double lat;
	private double lon;
	private long when;
	private MapDirections directions;
	
	private String address;
	
	public TruckLocation() {
		lat = 41.947279;
		lon = -87.654160;
		
		address = "Chicago";
		when = (new Date()).getTime();
	}
	
	
	
	public TruckLocation(double lat, double lon, String address) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.address = address;
		when = -1;
	}

	public TruckLocation(TruckLocation tl) {
		lat = tl.lat;
		lon = tl.lon;
		address = tl.address;
	}

	public TruckLocation(TruckLocation tl, boolean all) {
		this(tl);
		if (all) {
			this.when = tl.getTime();
			this.directions = new MapDirections(tl.getDirections());
		}
	}


	public void timestamp() {
		when = (new Date()).getTime();
	}

	public long getTime() {
		return when;
	}



	public void setTime(long when) {
		this.when = when;
	}

	public double getLat() {
		return lat;
	}



	public void setLat(double lat) {
		this.lat = lat;
	}



	public double getLon() {
		return lon;
	}



	public void setLon(double lon) {
		this.lon = lon;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}

	public GeoPoint getLocation() {
		return new GeoPoint((int) (getLat() * 1000000),
				(int) (getLon() * 1000000));
	}
	
	public String getTextGeoPoint() {
		return lat + "," + lon;
	}
	
	public void setDirections(MapDirections md) {
		directions = md;
	}
	
	public MapDirections getDirections() {
		return directions;
	}

	@Override
	public String toString() {
		return "TruckLocation [lat=" + lat + ", lon=" + lon + ", when=" + when
				+ ", address=" + address + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TruckLocation other = (TruckLocation) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		return true;
	}
	
	
}
