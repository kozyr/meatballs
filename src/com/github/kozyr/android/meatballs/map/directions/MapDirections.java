package com.github.kozyr.android.meatballs.map.directions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MapDirections implements Serializable {
	private List<MapPath> path;
	private PolyLine overview;
	
	public MapDirections() {
		path = new ArrayList<MapPath>();
	}
	
	public MapDirections(MapDirections md) {
		this();
		if (md != null) {
			for (MapPath mp : md.getPath()) {
				path.add(new MapPath(mp));
			}
			overview = new PolyLine(md.overview);
		}
	}
	
	public List<MapPath> getPath() {
		return path;
	}
	
	public void setPath(List<MapPath> path) {
		this.path = path;
	}
	
	public void addPathElement(MapPath pe) {
		path.add(pe);
	}
	
	public PolyLine getOverview() {
		return overview;
	}
	
	public void setOverview(PolyLine overview) {
		this.overview = overview;
	}
	
	public boolean isEmpty() {
		return path == null || path.size() == 0;
	}

	@Override
	public String toString() {
		return "MapDirections [path=" + path + ", overview=" + overview + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((overview == null) ? 0 : overview.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		MapDirections other = (MapDirections) obj;
		if (overview == null) {
			if (other.overview != null)
				return false;
		} else if (!overview.equals(other.overview))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}
