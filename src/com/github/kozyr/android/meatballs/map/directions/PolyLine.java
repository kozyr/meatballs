package com.github.kozyr.android.meatballs.map.directions;

import java.io.Serializable;

import com.google.android.maps.GeoPoint;

@SuppressWarnings("serial")
public class PolyLine implements Serializable {
	private String points;
	private String levels;
	
	private GeoPoint start;
	private GeoPoint end;
    private String encodedKey;
	
	public PolyLine(String points, String levels) {
		this.points = points;
		this.levels = levels;
	}
	
	public PolyLine(PolyLine pl) {
		if (pl != null) {
			points = pl.points;
			levels = pl.levels;
			this.start = pl.start;
			this.end = pl.end;
		}
	}
	
	public String getPoints() {
		return points;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getLevels() {
		return levels;
	}
	public void setLevels(String levels) {
		this.levels = levels;
	}
	public GeoPoint getStart() {
		return start;
	}
	public void setStart(GeoPoint start) {
		this.start = start;
	}
	public GeoPoint getEnd() {
		return end;
	}
	public void setEnd(GeoPoint end) {
		this.end = end;
	}
	@Override
	public String toString() {
		return "PolyLine [encodedKey=" + encodedKey + ", end=" + end
				+ ", levels=" + levels + ", points=" + points + ", start="
				+ start + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((encodedKey == null) ? 0 : encodedKey.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((levels == null) ? 0 : levels.hashCode());
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		PolyLine other = (PolyLine) obj;
		if (encodedKey == null) {
			if (other.encodedKey != null)
				return false;
		} else if (!encodedKey.equals(other.encodedKey))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (levels == null) {
			if (other.levels != null)
				return false;
		} else if (!levels.equals(other.levels))
			return false;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
}
