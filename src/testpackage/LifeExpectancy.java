package testpackage;

import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.events.MapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import parsing.ParseFeed;
import processing.core.PApplet;

public class LifeExpectancy extends PApplet {
	UnfoldingMap map;
	float rectSize = 50;
	float rectSizeDiff;
	Location lastZoomLocation = null;
	
	HashMap<String, Float> lifeExpMap;
	List<Feature> countries;
	List<Marker> countryMarkers;
	
	public void setup() {
		size(800, 600, P2D);
		map = new UnfoldingMap(this, 50, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
		map.setTweening(true);
		MapUtils.createDefaultEventDispatcher(this, map);
		lifeExpMap = ParseFeed.loadLifeExpectancyFromCSV(this, "LifeExpectancyWorldBank.csv");
		countries = GeoJSONReader.loadData(this, "countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		map.addMarkers(countryMarkers);
		shadeCountries();
	}
	
	public void draw() {
		background(150);
		map.draw();
		if(lastZoomLocation != null) {
			ScreenPosition pos = map.getScreenPosition(lastZoomLocation);
			noFill();
			stroke(255,0,0,200);
			rectMode(CENTER);
			rect(pos.x, pos.y, rectSize, rectSize);
			rectSize += rectSizeDiff;
			if(rectSize < 10 || rectSize > 50) {
				lastZoomLocation = null;
			}
		}
		
	}
	
	private void shadeCountries() {
		for (Marker marker: countryMarkers) {
			String countryId = marker.getId();
			if (lifeExpMap.containsKey(countryId)) {
				float lifeExp = lifeExpMap.get(countryId);
				int colorLevel = (int) map(lifeExp, 40, 90, 10, 255);
				marker.setColor(color(255-colorLevel, 100, colorLevel));
			} else {
				marker.setColor(color(150,150,150));
			}
		}
	}
	
	public void mapChanger(MapEvent mapEvent) {
		if(mapEvent.getType().equals(ZoomMapEvent.TYPE_ZOOM)) {
			ZoomMapEvent zoomMapEvent = (ZoomMapEvent) mapEvent;
			lastZoomLocation = zoomMapEvent.getCenter();
			if (zoomMapEvent.getZoomLevelDelta() < 0 || zoomMapEvent.getZoomDelta() < 0 ) {
				rectSize = 50;
				rectSizeDiff = -3;
			} else {
				rectSize = 10;
				rectSizeDiff = 3;
			}
			
			
		}
	}
	
}
