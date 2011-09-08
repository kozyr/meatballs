package com.github.kozyr.android.meatballs.map.directions;

import java.io.Serializable;

public class MapPath implements Serializable {
	private PolyLine polyLine;
	private String instructions;
	private String distance;
	
	public MapPath(PolyLine polyLine, String instructions) {
		this.polyLine = polyLine;
		this.instructions = instructions;
	}
	
	public MapPath(PolyLine line) {
		polyLine = line;
	}
	
	public MapPath(MapPath mp) {
		if (mp != null) {
			polyLine = new PolyLine(mp.polyLine);
			instructions = mp.instructions;
			distance = mp.distance;
		}
	}
	
	public PolyLine getPolyLine() {
		return polyLine;
	}
	public void setPolyLine(PolyLine polyLine) {
		this.polyLine = polyLine;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	@Override
	public String toString() {
		return "MapPath [instructions="
				+ instructions + ", polyLine=" + polyLine + ", distance=" + distance + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((distance == null) ? 0 : distance.hashCode());
		result = prime * result
				+ ((instructions == null) ? 0 : instructions.hashCode());
		result = prime * result
				+ ((polyLine == null) ? 0 : polyLine.hashCode());
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
		MapPath other = (MapPath) obj;
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (!distance.equals(other.distance))
			return false;
		if (instructions == null) {
			if (other.instructions != null)
				return false;
		} else if (!instructions.equals(other.instructions))
			return false;
		if (polyLine == null) {
			if (other.polyLine != null)
				return false;
		} else if (!polyLine.equals(other.polyLine))
			return false;
		return true;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getDistance() {
		return distance;
	}
	
	
}
