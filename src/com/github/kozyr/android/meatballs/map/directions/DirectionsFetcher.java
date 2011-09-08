package com.github.kozyr.android.meatballs.map.directions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.kozyr.android.meatballs.Util;
import com.github.kozyr.android.meatballs.map.TruckLocation;
import com.google.android.maps.GeoPoint;


public class DirectionsFetcher {
	
	private static Logger log = Logger.getLogger(DirectionsFetcher.class.getName());
	private static final String DIRECTIONS_URL = 
			"http://maps.google.com/maps/api/directions/json?sensor=false&origin=";
	
	public static MapDirections getDirections(TruckLocation prev, TruckLocation next) throws DirectionFetchException {
		MapDirections directions = new MapDirections();
		String url = buildUrl(prev, next);
		log.info("Fetching directions for: " + url);
		if (url != null) {
			try {
				String jsonDirections = Util.fetchData(url);
				log.info("JSON Directions: " + jsonDirections);
				directions = parseDirections(jsonDirections);
				log.info(directions.toString());
			} catch (IOException e) {
				log.log(Level.SEVERE, "Failed to obtain directions", e);
				throw new DirectionFetchException("Failed to obtain directions", e);
			}
		}
		
		return directions;
	}
	
	private static MapDirections parseDirections(String jsonStr) throws DirectionFetchException {
		MapDirections directions = new MapDirections();
		
		try {
			JSONObject json = new JSONObject(jsonStr);
			String status = json.getString("status");
			if ("OK".equals(status)) {
				JSONArray allRoutes = json.getJSONArray("routes");
				JSONObject route = allRoutes.getJSONObject(0);
				JSONObject overview_polyline = route.getJSONObject("overview_polyline");
				directions.setOverview(new PolyLine(
						overview_polyline.getString("points"),
						overview_polyline.getString("levels")));
				parseRoutes(route, directions);
			}
		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(),  e);
			throw new DirectionFetchException("Failed to generate directions map.", e);
		}
		
		return directions;
	}

	private static void parseRoutes(JSONObject route, MapDirections directions) throws JSONException {
		JSONArray legs = route.getJSONArray("legs");
		for (int i = 0; i < legs.length(); i++) {
			JSONArray steps = legs.getJSONObject(i).getJSONArray("steps");
			for (int j = 0; j < steps.length(); j++) {
				JSONObject step = steps.getJSONObject(j);
				String instr = Util.removeHtml(step.getString("html_instructions"));
				if (j == steps.length() - 1) {
					int end = instr.indexOf("Destination");
					if (end != -1) {
						instr = instr.substring(0, end);
					}
				}
				String distance = step.getJSONObject("distance").getString("text");
				JSONObject polyLine = step.optJSONObject("polyline");
				PolyLine poly = null;
				if (polyLine != null) {
					poly = new PolyLine(
						polyLine.getString("points"),
						polyLine.getString("levels"));
					JSONObject startLocation = step.optJSONObject("start_location");
					if (startLocation != null) {
						GeoPoint start = new GeoPoint(
								(int)(startLocation.getDouble("lat") * 1000000), 
								(int) (startLocation.getDouble("lng") * 1000000));
						poly.setStart(start);
					}
					JSONObject endLocation = step.optJSONObject("end_location");
					if (endLocation != null) {
						GeoPoint end = new GeoPoint(
								(int)(endLocation.getDouble("lat") * 1000000), 
								(int) (endLocation.getDouble("lng") * 1000000));
						poly.setEnd(end);
					}
				}
				MapPath path = new MapPath(poly, instr);
				path.setDistance(distance);
				directions.addPathElement(path);
			}
		}
	}

	

	/**
	 * 
	 * @param instr
	 * @return null if there is just one or no points at all
	 */
	private static String buildUrl(TruckLocation start, TruckLocation finish) {
		
		StringBuilder url = new StringBuilder(DIRECTIONS_URL);
		url.append(start.getTextGeoPoint());
		url.append("&destination=");
		url.append(finish.getTextGeoPoint());
		
		return url.toString();
	}
}
